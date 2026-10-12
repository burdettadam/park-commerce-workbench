package dev.parkcommerce.core;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static dev.parkcommerce.core.Catalog.*;
import static dev.parkcommerce.core.Failure.Code.*;

/** Runnable with only JDK 21; the same named cases are exposed as JUnit dynamic tests. */
public final class CoreScenarios {
    @FunctionalInterface public interface Checked { void run() throws Exception; }
    public record Case(String name, Checked body) {}
    public static void equal(Object expected,Object actual) {
        if (!Objects.equals(expected,actual)) throw new AssertionError("Expected " + expected + ", got " + actual);
    }
    public static void check(boolean value) { if (!value) throw new AssertionError("Condition was false"); }
    public static void failure(Failure.Code code,Checked action) throws Exception {
        try { action.run(); } catch(Failure e) { equal(code,e.code());return; }
        throw new AssertionError("Expected " + code);
    }
    public static List<Case> cases() {
        var cases = new ArrayList<Case>();
        cases.add(new Case("Money parses exact cents", () -> equal(12050L,Money.usd("120.50").cents())));
        cases.add(new Case("Money renders two decimals", () -> equal("1.20",Money.usd("1.2").amount())));
        cases.add(new Case("Money rejects fractional cents", () -> failure(INVALID_INPUT,()->Money.usd("1.005"))));
        cases.add(new Case("Money rejects negative amounts", () -> failure(INVALID_INPUT,()->Money.usd("-1"))));
        cases.add(new Case("Money rejects NaN", () -> failure(INVALID_INPUT,()->Money.usd("NaN"))));
        cases.add(new Case("Money rejects overflow", () -> failure(INVALID_INPUT,()->Money.usd("999999999999999999999"))));
        cases.add(new Case("Money permits zero", () -> equal("0.00",new Money(0).amount())));
        cases.add(new Case("Discount rounds half-up per line", () -> equal(1L,new Money(5).discount(1000).cents())));
        cases.add(new Case("Money addition and subtraction conserve cents", () -> equal(Money.usd("1.11"),Money.usd("1.11").plus(Money.usd("2.22")).minus(Money.usd("2.22")))));
        cases.add(new Case("Discount rejects more than 100 percent", () -> failure(INVALID_INPUT,()->new Money(1).discount(10001))));
        cases.add(new Case("Multiplication rejects negative quantity", () -> failure(INVALID_INPUT,()->new Money(1).times(-1))));
        cases.add(new Case("Catalog rejects unsafe identifier", () -> failure(INVALID_INPUT,()->new Product("../x",1,"X",Kind.EVENT,Set.of(Variant.ADULT)))));
        cases.add(new Case("Catalog rejects empty variants", () -> failure(INVALID_INPUT,()->new Product("x",1,"X",Kind.EVENT,Set.of()))));
        cases.add(new Case("Catalog accepts immutable variant input", () -> equal(Set.of(Variant.ADULT),new Product("x",1,"X",Kind.EVENT,Set.of(Variant.ADULT)).variants())));
        cases.add(new Case("Catalog defensively copies variants", () -> { var variants=EnumSet.of(Variant.ADULT);var p=new Product("x",1,"X",Kind.EVENT,variants);variants.add(Variant.CHILD);equal(1,p.variants().size()); }));
        cases.add(new Case("Catalog preserves earlier revision", () -> {var f=new ScenarioFixture();f.catalog.publish(new Product("lantern-evening",2,"New Name",Kind.EVENT,Set.of(Variant.ADULT)));equal("Lantern Evening",f.catalog.revision("lantern-evening",1).name());equal(2L,f.catalog.latest("lantern-evening").revision());}));
        cases.add(new Case("Catalog rejects skipped revision", () -> {var f=new ScenarioFixture();failure(CONFLICT,()->f.catalog.publish(new Product("lantern-evening",3,"X",Kind.EVENT,Set.of(Variant.ADULT))));}));
        cases.add(new Case("Catalog rejects duplicate publication", () -> {var f=new ScenarioFixture();failure(CONFLICT,()->f.catalog.publish(f.catalog.latest("lantern-evening")));}));
        cases.add(new Case("Catalog latest list has one entry per product", () -> {var f=new ScenarioFixture();equal(2,f.catalog.products().size());}));
        cases.add(new Case("Catalog missing product returns not found", () -> {var f=new ScenarioFixture();failure(NOT_FOUND,()->f.catalog.latest("missing"));}));
        cases.add(new Case("Rates reject reversed dates", () -> failure(INVALID_INPUT,()->new Pricing.Rate(Variant.ADULT,LocalDate.of(2026,2,2),LocalDate.of(2026,2,1),new Money(100)))));
        cases.add(new Case("Rates reject excessive unit price", () -> failure(INVALID_INPUT,()->new Pricing.Rate(Variant.ADULT,LocalDate.now(),LocalDate.now(),new Money(1_000_001)))));
        cases.add(new Case("Price book rejects overlapping ranges", () -> {var f=new ScenarioFixture();var r=new Pricing.Rate(Variant.ADULT,f.visit,f.visit,new Money(100));failure(INVALID_INPUT,()->new Pricing.PriceBook("park-day",1,1,List.of(r,r),null));}));
        cases.add(new Case("Adjacent nonoverlapping ranges allowed", () -> {var f=new ScenarioFixture();new Pricing.PriceBook("park-day",1,1,List.of(new Pricing.Rate(Variant.ADULT,f.visit,f.visit,new Money(100)),new Pricing.Rate(Variant.ADULT,f.visit.plusDays(1),f.visit.plusDays(1),new Money(200))),null);}));
        cases.add(new Case("Shared inclusive date boundary is an overlap", () -> {var f=new ScenarioFixture();failure(INVALID_INPUT,()->new Pricing.PriceBook("park-day",1,1,List.of(new Pricing.Rate(Variant.ADULT,f.visit,f.visit.plusDays(1),new Money(100)),new Pricing.Rate(Variant.ADULT,f.visit.plusDays(1),f.visit.plusDays(2),new Money(200))),null));}));
        cases.add(new Case("Pricing rejects skipped version", () -> {var f=new ScenarioFixture();failure(CONFLICT,()->f.pricing.publish(f.book("park-day",3,"1","1",null)));}));
        cases.add(new Case("Pricing rejects missing catalog revision", () -> {var f=new ScenarioFixture();failure(NOT_FOUND,()->f.pricing.publish(new Pricing.PriceBook("park-day",2,999,f.book("park-day",2,"1","1",null).rates(),null)));}));
        cases.add(new Case("Pricing rejects variant outside pinned catalog", () -> {var f=new ScenarioFixture();f.catalog.publish(new Product("single",1,"Single",Kind.EVENT,Set.of(Variant.ADULT)));failure(INVALID_INPUT,()->f.pricing.publish(f.book("single",1,"1","1",null)));}));
        cases.add(new Case("Quote calculates adult and child totals", () -> {var f=new ScenarioFixture();equal(17000L,f.quote("alice",1,1).total().cents());}));
        cases.add(new Case("Family offer applies at four tickets", () -> {var f=new ScenarioFixture();var q=f.quote("alice",2,2);equal(30600L,q.total().cents());equal(3400L,q.discount().cents());equal("family-four",q.appliedOffer());}));
        cases.add(new Case("Family offer absent below threshold", () -> {var f=new ScenarioFixture();equal(null,f.quote("alice",1,0).appliedOffer());}));
        cases.add(new Case("Quote duration is fifteen minutes", () -> {var f=new ScenarioFixture();var q=f.quote("alice",1,0);equal(Duration.ofMinutes(15),Duration.between(q.createdAt(),q.expiresAt()));}));
        cases.add(new Case("Quote expires at exact boundary", () -> {var f=new ScenarioFixture();var q=f.quote("alice",1,0);check(!q.validAt(q.expiresAt()));check(q.validAt(q.expiresAt().minusNanos(1)));}));
        cases.add(new Case("Quote remains immutable after repricing", () -> {var f=new ScenarioFixture();var q=f.quote("alice",1,0);f.pricing.publish(f.book("lantern-evening",2,"125","80",null));equal(10000L,f.pricing.get(q.id(),"alice").total().cents());equal(12500L,f.quote("alice",1,0).total().cents());equal(1L,q.pricingVersion());}));
        cases.add(new Case("Quote retains old product revision and name", () -> {var f=new ScenarioFixture();f.catalog.publish(new Product("lantern-evening",2,"Changed",Kind.EVENT,Set.of(Variant.ADULT)));equal("Lantern Evening",f.quote("alice",1,0).productName());}));
        cases.add(new Case("Quote cannot be read by another owner", () -> {var f=new ScenarioFixture();var q=f.quote("alice",1,0);failure(NOT_FOUND,()->f.pricing.get(q.id(),"bob"));}));
        cases.add(new Case("Quote requires authenticated owner", () -> {var f=new ScenarioFixture();failure(INVALID_INPUT,()->f.quote("",1,0));}));
        cases.add(new Case("Quote rejects zero tickets", () -> failure(INVALID_INPUT,()->new Pricing.Request("park-day",LocalDate.now(),0,0))));
        cases.add(new Case("Quote rejects negative tickets", () -> failure(INVALID_INPUT,()->new Pricing.Request("park-day",LocalDate.now(),-1,2))));
        cases.add(new Case("Quote rejects more than twenty tickets", () -> failure(INVALID_INPUT,()->new Pricing.Request("park-day",LocalDate.now(),20,1))));
        cases.add(new Case("Quote rejects past visit dates", () -> {var f=new ScenarioFixture();failure(INVALID_INPUT,()->f.pricing.create(new Pricing.Request("park-day",LocalDate.of(2026,10,10),1,0),"alice"));}));
        cases.add(new Case("Quote past-date check uses park timezone", () -> {var f=new ScenarioFixture();var clock=Clock.fixed(Instant.parse("2026-11-01T01:00:00Z"),ZoneOffset.UTC);var p=new Pricing.Service(f.prices,f.catalog::revision,clock,ZoneId.of("America/New_York"));equal(f.visit,p.create(new Pricing.Request("park-day",f.visit,1,0),"alice").visitDate());}));
        cases.add(new Case("Quote cannot price an uncovered day", () -> {var f=new ScenarioFixture();failure(NOT_FOUND,()->f.pricing.create(new Pricing.Request("park-day",f.visit.plusDays(1),1,0),"alice"));}));
        cases.add(new Case("All line totals reconcile across 230 party shapes", () -> {var f=new ScenarioFixture();for(int a=0;a<=20;a++)for(int c=0;c<=20-a;c++){if(a+c==0)continue;var q=f.quote("alice",a,c);equal(q.total().cents(),q.lines().stream().mapToLong(l->l.total().cents()).sum());equal(q.subtotal().cents()-q.discount().cents(),q.total().cents());}}));
        cases.add(new Case("Event rejects zero capacity", () -> failure(INVALID_INPUT,()->new Booking.Event("event","park-day",LocalDate.now(),0))));
        cases.add(new Case("Duplicate event cannot reset capacity", () -> {var f=new ScenarioFixture();failure(CONFLICT,()->f.booking.create(new Booking.Event("october-lanterns","lantern-evening",f.visit,100)));equal(3,f.booking.availability("october-lanterns").capacity());}));
        cases.add(new Case("Hold reduces availability", () -> {var f=new ScenarioFixture();f.hold("alice");equal(2,f.booking.availability("october-lanterns").available());}));
        cases.add(new Case("Hold retries reuse same quote allocation", () -> {var f=new ScenarioFixture();var q=f.quote("alice",1,0);var h=f.booking.reserve("october-lanterns",q.id(),"alice");equal(h.id(),f.booking.reserve("october-lanterns",q.id(),"alice").id());equal(2,f.booking.availability("october-lanterns").available());}));
        cases.add(new Case("Hold caps expiration at quote expiration", () -> {var f=new ScenarioFixture();var q=f.quote("alice",1,0);f.clock.advance(Duration.ofMinutes(14));var h=f.booking.reserve("october-lanterns",q.id(),"alice");equal(q.expiresAt(),h.expiresAt());}));
        cases.add(new Case("Hold cannot use expired quote", () -> {var f=new ScenarioFixture();var q=f.quote("alice",1,0);f.clock.advance(Duration.ofMinutes(15));failure(EXPIRED,()->f.booking.reserve("october-lanterns",q.id(),"alice"));}));
        cases.add(new Case("Hold cannot use another owner's quote", () -> {var f=new ScenarioFixture();var q=f.quote("alice",1,0);failure(NOT_FOUND,()->f.booking.reserve("october-lanterns",q.id(),"bob"));}));
        cases.add(new Case("Hold requires matching event date", () -> {var f=new ScenarioFixture();f.booking.create(new Booking.Event("next-day","lantern-evening",f.visit.plusDays(1),5));var q=f.quote("alice",1,0);failure(INVALID_INPUT,()->f.booking.reserve("next-day",q.id(),"alice"));}));
        cases.add(new Case("Admission quote cannot reserve an event", () -> {var f=new ScenarioFixture();var q=f.pricing.create(new Pricing.Request("park-day",f.visit,1,0),"alice");failure(INVALID_INPUT,()->f.booking.reserve("october-lanterns",q.id(),"alice"));}));
        cases.add(new Case("Mixed adult and child variants share capacity", () -> {var f=new ScenarioFixture();var q=f.quote("alice",1,2);f.booking.reserve("october-lanterns",q.id(),"alice");failure(SOLD_OUT,()->f.hold("bob"));equal(0,f.booking.availability("october-lanterns").available());}));
        cases.add(new Case("Expired hold frees capacity exactly once", () -> {var f=new ScenarioFixture();f.hold("alice");f.clock.advance(Duration.ofMinutes(5));equal(3,f.booking.availability("october-lanterns").available());equal(3,f.booking.availability("october-lanterns").available());}));
        cases.add(new Case("Expired allocation cannot be recycled with same quote", () -> {var f=new ScenarioFixture();var q=f.quote("alice",1,0);f.booking.reserve("october-lanterns",q.id(),"alice");f.clock.advance(Duration.ofMinutes(5));failure(CONFLICT,()->f.booking.reserve("october-lanterns",q.id(),"alice"));}));
        cases.add(new Case("Confirmation consumes held capacity once", () -> {var f=new ScenarioFixture();var h=f.hold("alice");var o=f.booking.confirm(h.id(),"alice","k1");equal(h.total(),o.total());var a=f.booking.availability("october-lanterns");equal(1,a.confirmed());equal(0,a.held());equal(2,a.available());}));
        cases.add(new Case("Repeated checkout returns identical order", () -> {var f=new ScenarioFixture();var h=f.hold("alice");var o=f.booking.confirm(h.id(),"alice","k1");for(int i=0;i<20;i++)equal(o,f.booking.confirm(h.id(),"alice","k1"));}));
        cases.add(new Case("Successful checkout replay survives later expiry", () -> {var f=new ScenarioFixture();var h=f.hold("alice");var o=f.booking.confirm(h.id(),"alice","k1");f.clock.advance(Duration.ofDays(1));equal(o,f.booking.confirm(h.id(),"alice","k1"));}));
        cases.add(new Case("Checkout key cannot change hold", () -> {var f=new ScenarioFixture();var h1=f.hold("alice");var h2=f.hold("alice");f.booking.confirm(h1.id(),"alice","k1");failure(CONFLICT,()->f.booking.confirm(h2.id(),"alice","k1"));}));
        cases.add(new Case("Confirmed hold rejects a different key", () -> {var f=new ScenarioFixture();var h=f.hold("alice");f.booking.confirm(h.id(),"alice","k1");failure(CONFLICT,()->f.booking.confirm(h.id(),"alice","k2"));}));
        cases.add(new Case("Checkout keys are scoped to owner", () -> {var f=new ScenarioFixture();var a=f.hold("alice");var b=f.hold("bob");var o1=f.booking.confirm(a.id(),"alice","k1");var o2=f.booking.confirm(b.id(),"bob","k1");check(!o1.id().equals(o2.id()));}));
        cases.add(new Case("Checkout key reuse across events conflicts", () -> {var f=new ScenarioFixture();var a=f.hold("alice");f.booking.confirm(a.id(),"alice","k1");f.booking.create(new Booking.Event("other","lantern-evening",f.visit,5));var b=f.booking.reserve("other",f.quote("alice",1,0).id(),"alice");failure(CONFLICT,()->f.booking.confirm(b.id(),"alice","k1"));}));
        cases.add(new Case("Checkout rejects expired hold at exact boundary", () -> {var f=new ScenarioFixture();var h=f.hold("alice");f.clock.advance(Duration.ofMinutes(5));failure(EXPIRED,()->f.booking.confirm(h.id(),"alice","k1"));}));
        cases.add(new Case("Checkout cannot use another owner's hold", () -> {var f=new ScenarioFixture();var h=f.hold("alice");failure(NOT_FOUND,()->f.booking.confirm(h.id(),"bob","k1"));}));
        cases.add(new Case("Checkout rejects unsafe idempotency key", () -> {var f=new ScenarioFixture();var h=f.hold("alice");failure(INVALID_INPUT,()->f.booking.confirm(h.id(),"alice","bad\nkey"));}));
        cases.add(new Case("Order retrieval enforces ownership", () -> {var f=new ScenarioFixture();var h=f.hold("alice");var o=f.booking.confirm(h.id(),"alice","k1");failure(NOT_FOUND,()->f.booking.get(o.id(),"bob"));equal(o,f.booking.get(o.id(),"alice"));}));
        cases.add(new Case("Missing inventory never creates capacity", () -> {var f=new ScenarioFixture();failure(NOT_FOUND,()->f.booking.availability("missing"));}));
        cases.add(new Case("Rollback preserves ledger after failed operation", () -> {var f=new ScenarioFixture();var q=f.quote("alice",1,0);failure(CONFLICT,()->f.bookings.transact("october-lanterns",l->{l.reserve(q,"alice",f.clock.instant());throw new Failure(CONFLICT,"Abort");}));equal(3,f.booking.availability("october-lanterns").available());}));
        cases.add(new Case("State reconstruction preserves checkout identity", () -> {var f=new ScenarioFixture();var h=f.hold("alice");var o=f.booking.confirm(h.id(),"alice","k1");var restarted=new Booking.Service(f.bookings.restartCopy(),f.pricing::internal,f.clock);equal(o,restarted.confirm(h.id(),"alice","k1"));equal(2,restarted.availability("october-lanterns").available());}));
        cases.add(new Case("32 concurrent reservations allocate exactly 3 tickets", CoreScenarios::concurrentHolds));
        cases.add(new Case("24 concurrent checkout retries produce one order", CoreScenarios::concurrentCheckout));
        cases.add(new Case("One quote cannot reserve two event instances", () -> {var f=new ScenarioFixture();var q=f.quote("alice",1,0);f.booking.reserve("october-lanterns",q.id(),"alice");f.booking.create(new Booking.Event("other","lantern-evening",f.visit,3));failure(CONFLICT,()->f.booking.reserve("other",q.id(),"alice"));equal(3,f.booking.availability("other").available());}));
        cases.add(new Case("Missing upstream quote fails closed before allocating", () -> {var f=new ScenarioFixture();var service=new Booking.Service(f.bookings,id->null,f.clock);failure(DEPENDENCY_UNAVAILABLE,()->service.reserve("october-lanterns",UUID.randomUUID(),"alice"));equal(3,f.booking.availability("october-lanterns").available());}));
        cases.add(new Case("Mismatched upstream quote ID fails closed", () -> {var f=new ScenarioFixture();var q=f.quote("alice",1,0);var service=new Booking.Service(f.bookings,id->q,f.clock);failure(DEPENDENCY_UNAVAILABLE,()->service.reserve("october-lanterns",UUID.randomUUID(),"alice"));equal(3,f.booking.availability("october-lanterns").available());}));
        cases.add(new Case("Invalid upstream quantity cannot increase capacity", () -> {var f=new ScenarioFixture();var q=f.quote("alice",1,0);var bad=new Pricing.Quote(q.id(),q.owner(),q.productId(),q.productName(),q.kind(),q.catalogRevision(),q.visitDate(),q.pricingVersion(),-1,q.lines(),q.appliedOffer(),q.subtotal(),q.discount(),q.total(),q.createdAt(),q.expiresAt());var service=new Booking.Service(f.bookings,id->bad,f.clock);failure(DEPENDENCY_UNAVAILABLE,()->service.reserve("october-lanterns",q.id(),"alice"));equal(3,f.booking.availability("october-lanterns").available());}));
        cases.add(new Case("Order lookup requires an ID", () -> {var f=new ScenarioFixture();failure(INVALID_INPUT,()->f.booking.get(null,"alice"));}));
        cases.addAll(PricingPreviewScenarios.cases());
        cases.addAll(CheckoutRecoveryScenarios.cases());
        cases.addAll(CatalogEditorialScenarios.cases());
        cases.addAll(PricingContractScenarios.cases());
        cases.addAll(AdmissionScenarios.cases());
        return List.copyOf(cases);
    }
    private static void concurrentHolds() throws Exception {
        var f=new ScenarioFixture();
        var quotes=new ArrayList<Pricing.Quote>();for(int i=0;i<32;i++)quotes.add(f.quote("buyer-"+i,1,0));
        AtomicInteger won=new AtomicInteger(),sold=new AtomicInteger();
        try(var pool=Executors.newFixedThreadPool(16)) {
            var jobs=new ArrayList<Callable<Void>>();
            for(var q:quotes)jobs.add(()->{try{f.booking.reserve("october-lanterns",q.id(),q.owner());won.incrementAndGet();}catch(Failure e){if(e.code()!=SOLD_OUT)throw e;sold.incrementAndGet();}return null;});
            for(var result:pool.invokeAll(jobs))result.get();
        }
        equal(3,won.get());equal(29,sold.get());equal(0,f.booking.availability("october-lanterns").available());
    }
    private static void concurrentCheckout() throws Exception {
        var f=new ScenarioFixture();var h=f.hold("alice");var ids=new HashSet<UUID>();
        try(var pool=Executors.newFixedThreadPool(12)) {
            var jobs=new ArrayList<Callable<UUID>>();for(int i=0;i<24;i++)jobs.add(()->f.booking.confirm(h.id(),"alice","replay").id());
            for(var result:pool.invokeAll(jobs))ids.add(result.get());
        }
        equal(1,ids.size());equal(1,f.booking.availability("october-lanterns").confirmed());
    }
    public static void main(String[] args) throws Exception {
        int passed=0,failed=0;
        for(var test:cases())try{test.body().run();System.out.println("PASS "+test.name());passed++;}catch(Throwable e){System.out.println("FAIL "+test.name());e.printStackTrace(System.out);failed++;}
        System.out.printf("RESULT: %d passed, %d failed; JDK-only core tests with in-memory test stores.%n",passed,failed);
        if(failed>0)throw new AssertionError("Core test failures: "+failed);
    }
}
