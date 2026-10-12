package dev.parkcommerce.core;

import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import static dev.parkcommerce.core.Catalog.*;
import static dev.parkcommerce.core.CoreScenarios.*;
import static dev.parkcommerce.core.Failure.Code.*;

/** Domain-only tests; clocks and storage are explicit test doubles. */
public final class PricingContractScenarios {
    private PricingContractScenarios() {}
    static final ZoneId PARK = ZoneId.of("America/New_York");
    static final Product PRODUCT = new Product("contract-night", 1, "Contract Night", Kind.EVENT, EnumSet.allOf(Variant.class));
    static Pricing.PriceBook book(LocalDate day) {
        return new Pricing.PriceBook(PRODUCT.id(), 1, 1, List.of(new Pricing.Rate(Variant.ADULT, day, day, new Money(10000))), null);
    }
    static Pricing.Request request(LocalDate day) { return new Pricing.Request(PRODUCT.id(), day, 1, 0); }
    static final class TickingClock extends Clock {
        final Instant initial; final Duration step; final AtomicInteger reads;
        private final ZoneId zone;
        TickingClock(Instant initial, Duration step) { this(initial, step, new AtomicInteger(), ZoneOffset.UTC); }
        private TickingClock(Instant initial, Duration step, AtomicInteger reads, ZoneId zone) {
            this.initial=initial; this.step=step; this.reads=reads; this.zone=zone;
        }
        @Override public ZoneId getZone() { return zone; }
        @Override public Clock withZone(ZoneId value) { return new TickingClock(initial,step,reads,value); }
        @Override public Instant instant() { return initial.plus(step.multipliedBy(reads.getAndIncrement())); }
    }
    public static List<Case> cases() {
        var cases = new ArrayList<Case>();
        cases.add(new Case("Quote contract: one clock instant binds date, creation and deadline", () -> {
            var clock = new TickingClock(Instant.parse("2030-10-01T12:00:00Z"), Duration.ofSeconds(1));
            var store = new MemoryStores.PricingStore(); var day = LocalDate.of(2030,10,1); store.insert(book(day));
            var service = new Pricing.Service(store,(id,revision)->PRODUCT,clock,PARK);
            var quote = service.create(request(day), "alice");
            equal(clock.initial, quote.createdAt()); equal(clock.initial.plusSeconds(900), quote.expiresAt());
            equal(1,clock.reads.get());
        }));
        cases.add(new Case("Quote contract: a request cannot change park date midway through issuance", () -> {
            var clock = new TickingClock(Instant.parse("2030-10-01T03:59:59.999Z"), Duration.ofMillis(1));
            var day = LocalDate.of(2030,9,30); var store = new MemoryStores.PricingStore(); store.insert(book(day));
            var quote = new Pricing.Service(store,(id,revision)->PRODUCT,clock,PARK).create(request(day),"alice");
            equal(clock.initial, quote.createdAt()); equal(day,quote.visitDate()); equal(1,clock.reads.get());
        }));
        cases.add(new Case("Quote contract: slow catalog response does not extend the quote lifetime", () -> {
            Instant start=Instant.parse("2030-10-01T12:00:00Z");
            var clock=new MemoryStores.MutableClock(start); var day=LocalDate.of(2030,10,1);
            var store=new MemoryStores.PricingStore(); store.insert(book(day));
            Catalog.Client catalog=(id,revision)->{clock.advance(Duration.ofMinutes(2));return PRODUCT;};
            var quote=new Pricing.Service(store,catalog,clock,PARK).create(request(day),"alice");
            equal(start,quote.createdAt()); equal(start.plusSeconds(900),quote.expiresAt());
        }));
        cases.add(new Case("Quote contract: preview date validation uses one instant", () -> {
            var clock=new TickingClock(Instant.parse("2030-10-01T12:00:00Z"),Duration.ofSeconds(1));
            var day=LocalDate.of(2030,10,1);
            var service=new Pricing.Service(new MemoryStores.PricingStore(),(id,revision)->PRODUCT,clock,PARK);
            equal(10000L,service.preview(new Pricing.PreviewRequest(book(day),request(day))).total().cents());
            equal(1,clock.reads.get());
        }));
        cases.add(new Case("Quote status: one nanosecond before expiry is unexpired", () -> {
            var f=new ScenarioFixture();var q=f.quote("alice",1,0);
            f.clock.advance(Duration.ofMinutes(15).minusNanos(1));var status=f.pricing.status(q.id(),"alice");
            equal(Pricing.QuoteState.UNEXPIRED,status.state());equal(q,status.quote());
            equal(q.expiresAt().minusNanos(1),status.evaluatedAt());equal("America/New_York",status.parkTimeZone());
        }));
        cases.add(new Case("Quote status: exact expiry is expired but historical reads remain available", () -> {
            var f=new ScenarioFixture();var q=f.quote("alice",1,0);f.clock.advance(Duration.ofMinutes(15));
            var status=f.pricing.status(q.id(),"alice");equal(Pricing.QuoteState.EXPIRED,status.state());
            equal(q.expiresAt(),status.evaluatedAt());equal(q,f.pricing.get(q.id(),"alice"));equal(q,f.pricing.internal(q.id()));
            failure(EXPIRED,()->f.booking.reserve("october-lanterns",q.id(),"alice"));
            equal(3,f.booking.availability("october-lanterns").available());
        }));
        cases.add(new Case("Quote status: repeated reads never renew the original deadline", () -> {
            var f=new ScenarioFixture();var q=f.quote("alice",1,0);
            for(int i=0;i<20;i++){f.clock.advance(Duration.ofMinutes(1));equal(q,f.pricing.status(q.id(),"alice").quote());}
            equal(Pricing.QuoteState.EXPIRED,f.pricing.status(q.id(),"alice").state());
        }));
        cases.add(new Case("Quote status: authenticated owner is checked before status is exposed", () -> {
            var f=new ScenarioFixture();var q=f.quote("alice",1,0);f.clock.advance(Duration.ofDays(1));
            var noClock=new TickingClock(f.clock.instant(),Duration.ZERO);
            var service=new Pricing.Service(f.prices,(id,rev)->{throw new AssertionError("No catalog reads");},noClock,PARK);
            failure(NOT_FOUND,()->service.status(q.id(),"bob"));equal(0,noClock.reads.get());
            failure(NOT_FOUND,()->service.status(UUID.randomUUID(),"alice"));equal(0,noClock.reads.get());
        }));
        cases.add(new Case("Quote status: observation needs only one quote read and one clock sample", () -> {
            var f=new ScenarioFixture();var q=f.quote("alice",1,0);var reads=new AtomicInteger();
            Pricing.Store readOnly=new Pricing.Store(){
                public Optional<Pricing.PriceBook> latest(String id){throw new AssertionError("No pricing read");}
                public void insert(Pricing.PriceBook b){throw new AssertionError("No publishing");}
                public void saveQuote(Pricing.Quote value){throw new AssertionError("No quote writes");}
                public Optional<Pricing.Quote> quote(UUID id){reads.incrementAndGet();return f.prices.quote(id);}
            };
            var clock=new TickingClock(q.createdAt(),Duration.ofSeconds(1));
            var service=new Pricing.Service(readOnly,(id,rev)->{throw new AssertionError("No catalog reads");},clock,PARK);
            equal(q,service.status(q.id(),"alice").quote());equal(1,reads.get());equal(1,clock.reads.get());
        }));
        cases.add(new Case("Quote status: expiry is evaluated after a slow snapshot read", () -> {
            var f=new ScenarioFixture();var q=f.quote("alice",1,0);
            Pricing.Store slow=new Pricing.Store(){
                public Optional<Pricing.PriceBook> latest(String id){throw new AssertionError();}
                public void insert(Pricing.PriceBook b){throw new AssertionError();}
                public void saveQuote(Pricing.Quote value){throw new AssertionError();}
                public Optional<Pricing.Quote> quote(UUID id){f.clock.advance(Duration.ofMinutes(15));return f.prices.quote(id);}
            };
            var service=new Pricing.Service(slow,(id,rev)->{throw new AssertionError();},f.clock,PARK);
            equal(Pricing.QuoteState.EXPIRED,service.status(q.id(),"alice").state());
        }));
        cases.add(new Case("Quote status: existing confirmed order still recovers after expiry", () -> {
            var f=new ScenarioFixture();var h=f.hold("alice");var order=f.booking.confirm(h.id(),"alice","stable-key");
            f.clock.advance(Duration.ofDays(1));equal(Pricing.QuoteState.EXPIRED,f.pricing.status(h.quoteId(),"alice").state());
            equal(order,f.booking.recover(h.id(),"alice","stable-key").order());
            equal(order,f.booking.confirm(h.id(),"alice","stable-key"));equal(1,f.booking.availability(h.eventId()).confirmed());
        }));
        cases.add(new Case("Quote status: new catalog and price publications do not rewrite its snapshot", () -> {
            var f=new ScenarioFixture();var q=f.quote("alice",2,1);
            f.catalog.publish(new Product("lantern-evening",2,"New editorial name",Kind.EVENT,EnumSet.allOf(Variant.class)));
            var next=new Pricing.PriceBook("lantern-evening",2,2,List.of(new Pricing.Rate(Variant.ADULT,f.visit,f.visit,new Money(20000)),
                new Pricing.Rate(Variant.CHILD,f.visit,f.visit,new Money(12000))),null);f.pricing.publish(next);
            equal(q,f.pricing.status(q.id(),"alice").quote());var changed=f.quote("alice",2,1);
            equal(2L,changed.catalogRevision());equal(2L,changed.pricingVersion());equal("New editorial name",changed.productName());
            equal(52000L,changed.total().cents());equal(27000L,q.total().cents());
        }));
        cases.add(new Case("Quote status: missing owner and ID fail explicitly", () -> {
            var f=new ScenarioFixture();var q=f.quote("alice",1,0);
            failure(INVALID_INPUT,()->f.pricing.status(null,"alice"));
            failure(INVALID_INPUT,()->f.pricing.status(q.id(),""));failure(INVALID_INPUT,()->f.pricing.status(q.id(),null));
        }));
        cases.add(new Case("Quote contract: slow response can return an expired quote but cannot renew it", () -> {
            var day=LocalDate.of(2030,10,1);Instant start=Instant.parse("2030-10-01T12:00:00Z");
            var clock=new MemoryStores.MutableClock(start);var store=new MemoryStores.PricingStore();store.insert(book(day));
            var service=new Pricing.Service(store,(id,rev)->{clock.advance(Duration.ofMinutes(16));return PRODUCT;},clock,PARK);
            var q=service.create(request(day),"alice");equal(start.plusSeconds(900),q.expiresAt());
            equal(Pricing.QuoteState.EXPIRED,service.status(q.id(),"alice").state());
        }));
        cases.add(new Case("Quote contract: rejects yesterday before any dependency access", () -> {
            var day=LocalDate.of(2030,9,30);var store=new Pricing.Store(){
                public Optional<Pricing.PriceBook> latest(String id){throw new AssertionError("No store access");}
                public void insert(Pricing.PriceBook b){throw new AssertionError();}
                public void saveQuote(Pricing.Quote q){throw new AssertionError();}
                public Optional<Pricing.Quote> quote(UUID id){throw new AssertionError();}
            };
            var service=new Pricing.Service(store,(id,rev)->{throw new AssertionError("No catalog read");},
                Clock.fixed(Instant.parse("2030-10-01T04:00:00Z"),ZoneOffset.UTC),PARK);
            failure(INVALID_INPUT,()->service.create(request(day),"alice"));
        }));
        // Literal UTC/park-date pairs independently specify the intended New York boundary.
        String[][] dates={
            {"2030-03-10T04:59:59.999Z","2030-03-09","yes"},
            {"2030-03-10T05:00:00Z","2030-03-09","no"},
            {"2030-03-10T06:59:59Z","2030-03-10","yes"},
            {"2030-03-10T07:00:00Z","2030-03-10","yes"},
            {"2030-07-01T03:59:59.999Z","2030-06-30","yes"},
            {"2030-07-01T04:00:00Z","2030-06-30","no"},
            {"2030-11-03T05:59:59Z","2030-11-03","yes"},
            {"2030-11-03T06:00:00Z","2030-11-03","yes"},
            {"2030-11-04T04:59:59.999Z","2030-11-03","yes"},
            {"2030-11-04T05:00:00Z","2030-11-03","no"}
        };
        for(var row:dates)cases.add(new Case("Quote date: "+row[0]+" for "+row[1]+" allowed="+row[2],()->{
            var day=LocalDate.parse(row[1]);var store=new MemoryStores.PricingStore();store.insert(book(day));
            var service=new Pricing.Service(store,(id,rev)->PRODUCT,Clock.fixed(Instant.parse(row[0]),ZoneId.of("Pacific/Honolulu")),PARK);
            if(row[2].equals("yes"))equal(day,service.create(request(day),"alice").visitDate());
            else failure(INVALID_INPUT,()->service.create(request(day),"alice"));
        }));
        for(String start:List.of("2030-03-10T06:55:00Z","2030-11-03T05:55:00Z"))
            cases.add(new Case("Quote TTL: DST transition retains 900 elapsed seconds at "+start,()->{
                var clock=new MemoryStores.MutableClock(Instant.parse(start));var day=clock.instant().atZone(PARK).toLocalDate();
                var store=new MemoryStores.PricingStore();store.insert(book(day));var service=new Pricing.Service(store,(id,rev)->PRODUCT,clock,PARK);
                var q=service.create(request(day),"alice");equal(Duration.ofSeconds(900),Duration.between(q.createdAt(),q.expiresAt()));
                clock.advance(Duration.ofSeconds(900));equal(Pricing.QuoteState.EXPIRED,service.status(q.id(),"alice").state());
            }));
        cases.add(new Case("Quote calendar: both range endpoints are included and a gap is not priced", () -> {
            var from=LocalDate.of(2030,10,1);var through=from.plusDays(2);var store=new MemoryStores.PricingStore();
            store.insert(new Pricing.PriceBook(PRODUCT.id(),1,1,List.of(new Pricing.Rate(Variant.ADULT,from,through,new Money(11111))),null));
            var service=new Pricing.Service(store,(id,rev)->PRODUCT,Clock.fixed(Instant.parse("2030-09-01T12:00:00Z"),ZoneOffset.UTC),PARK);
            for(var day:List.of(from,from.plusDays(1),through))equal(11111L,service.create(request(day),"alice").total().cents());
            failure(NOT_FOUND,()->service.create(request(from.minusDays(1)),"alice"));failure(NOT_FOUND,()->service.create(request(through.plusDays(1)),"alice"));
        }));
        cases.add(new Case("Quote calendar: next-day rates may be adjacent but not share an endpoint", () -> {
            var day=LocalDate.of(2030,10,1);var a=new Pricing.Rate(Variant.ADULT,day,day.plusDays(2),new Money(100));
            failure(INVALID_INPUT,()->new Pricing.PriceBook(PRODUCT.id(),1,1,List.of(a,new Pricing.Rate(Variant.ADULT,day.plusDays(2),day.plusDays(4),new Money(200))),null));
            var store=new MemoryStores.PricingStore();store.insert(new Pricing.PriceBook(PRODUCT.id(),1,1,List.of(a,new Pricing.Rate(Variant.ADULT,day.plusDays(3),day.plusDays(4),new Money(200))),null));
            var service=new Pricing.Service(store,(id,rev)->PRODUCT,Clock.fixed(Instant.parse("2030-09-01T12:00:00Z"),ZoneOffset.UTC),PARK);
            equal(100L,service.create(request(day.plusDays(2)),"alice").total().cents());equal(200L,service.create(request(day.plusDays(3)),"alice").total().cents());
        }));
        cases.add(new Case("Quote calendar: unrequested variant does not require a rate", () -> {
            var day=LocalDate.of(2030,10,1);var store=new MemoryStores.PricingStore();store.insert(book(day));
            var service=new Pricing.Service(store,(id,rev)->PRODUCT,Clock.fixed(Instant.parse("2030-09-01T12:00:00Z"),ZoneOffset.UTC),PARK);
            equal(1,service.create(request(day),"alice").lines().size());
            failure(NOT_FOUND,()->service.create(new Pricing.Request(PRODUCT.id(),day,0,1),"alice"));
        }));
        for(String value:List.of("2030-02-29","2030-2-03","2030-02-3","2030-10-01T00:00:00Z"," 2030-10-01","2030-10-01 ","+10000-01-01","0000-01-01","2030-13-01","2030-01-32","20301001"))
            cases.add(new Case("Quote input rejects malformed date: "+value,()->failure(INVALID_INPUT,()->QuoteContract.parseDate(value))));
        cases.add(new Case("Quote input: valid leap day and four-digit year boundaries parse strictly", () -> {
            equal(LocalDate.of(2032,2,29),QuoteContract.parseDate("2032-02-29"));
            equal(LocalDate.of(1,1,1),QuoteContract.parseDate("0001-01-01"));equal(LocalDate.of(9999,12,31),QuoteContract.parseDate("9999-12-31"));
            failure(INVALID_INPUT,()->QuoteContract.parseDate(null));
            failure(INVALID_INPUT,()->new Pricing.Request(PRODUCT.id(),LocalDate.of(10000,1,1),1,0));
        }));
        for(String value:List.of("1-1-1-1-1","not-a-uuid","00000000000000000000000000000000","{00000000-0000-0000-0000-000000000000}","00000000-0000-0000-0000-00000000000g","00000000-0000-0000-0000-000000000000 "))
            cases.add(new Case("Quote input rejects malformed ID: "+value,()->failure(INVALID_INPUT,()->QuoteContract.parseId(value))));
        cases.add(new Case("Quote input: full UUID accepts either hexadecimal case without changing identity", () -> {
            String id="ABCDEF01-2345-6789-ABCD-EF0123456789";
            equal(UUID.fromString(id),QuoteContract.parseId(id));equal(QuoteContract.parseId(id),QuoteContract.parseId(id.toLowerCase(Locale.ROOT)));
            failure(INVALID_INPUT,()->QuoteContract.parseId(null));
        }));
        cases.add(new Case("Quote input: absent quantities differ from explicit zero", () -> {
            var input=new QuoteContract.Input(PRODUCT.id(),"2030-10-01",1,0);equal(request(LocalDate.of(2030,10,1)),input.toRequest());
            failure(INVALID_INPUT,()->new QuoteContract.Input(PRODUCT.id(),"2030-10-01",null,1).toRequest());
            failure(INVALID_INPUT,()->new QuoteContract.Input(PRODUCT.id(),"2030-10-01",1,null).toRequest());
            failure(INVALID_INPUT,()->new QuoteContract.Input(PRODUCT.id(),"2030-10-01",0,0).toRequest());
            failure(INVALID_INPUT,()->new QuoteContract.Input(PRODUCT.id(),"2030-10-01",21,0).toRequest());
        }));
        cases.add(new Case("Quote contract: legacy snapshot record shape and immutable lines remain unchanged", () -> {
            equal(List.of("id","owner","productId","productName","kind","catalogRevision","visitDate","pricingVersion","quantity","lines","appliedOffer","subtotal","discount","total","createdAt","expiresAt"),
                Arrays.stream(Pricing.Quote.class.getRecordComponents()).map(java.lang.reflect.RecordComponent::getName).toList());
            var f=new ScenarioFixture();var q=f.quote("alice",1,0);
            try{q.lines().clear();throw new AssertionError("Mutable quote lines");}catch(UnsupportedOperationException expected){equal(q,f.pricing.status(q.id(),"alice").quote());}
        }));
        return List.copyOf(cases);
    }
}
