package dev.parkcommerce.core;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;
import static dev.parkcommerce.core.CoreScenarios.*;
import static dev.parkcommerce.core.Failure.Code.*;

/** Actual domain code with test-only storage. Does not establish SQL isolation. */
public final class AdmissionScenarios {
    private AdmissionScenarios() {}
    static final class Fixture {
        final ScenarioFixture f = new ScenarioFixture();
        final MemoryAdmissionStore store = new MemoryAdmissionStore();
        final Admission.Service service = new Admission.Service(store, f.pricing::internal, f.clock, ZoneId.of("America/New_York"));
        Pricing.Quote quote(String owner, int adults, int children) {
            return f.pricing.create(new Pricing.Request("park-day", f.visit, adults, children), owner);
        }
        Admission.Order buy(Pricing.Quote q, String key) { return service.confirm(q.id(), q.owner(), key); }
    }
    static Pricing.Quote change(Pricing.Quote q, String field, Object value) {
        return new Pricing.Quote(field.equals("id") ? (UUID)value:q.id(),
            field.equals("owner")?(String)value:q.owner(),field.equals("productId")?(String)value:q.productId(),
            field.equals("productName")?(String)value:q.productName(),field.equals("kind")?(Catalog.Kind)value:q.kind(),
            field.equals("catalogRevision")?(Long)value:q.catalogRevision(),field.equals("visitDate")?(LocalDate)value:q.visitDate(),
            field.equals("pricingVersion")?(Long)value:q.pricingVersion(),field.equals("quantity")?(Integer)value:q.quantity(),
            q.lines(),q.appliedOffer(),field.equals("subtotal")?(Money)value:q.subtotal(),
            field.equals("discount")?(Money)value:q.discount(),field.equals("total")?(Money)value:q.total(),
            field.equals("createdAt")?(Instant)value:q.createdAt(),field.equals("expiresAt")?(Instant)value:q.expiresAt());
    }
    static Pricing.Quote lines(Pricing.Quote q, List<Pricing.Line> lines) {
        return new Pricing.Quote(q.id(),q.owner(),q.productId(),q.productName(),q.kind(),q.catalogRevision(),q.visitDate(),q.pricingVersion(),
            q.quantity(),lines,q.appliedOffer(),q.subtotal(),q.discount(),q.total(),q.createdAt(),q.expiresAt());
    }
    static Admission.Service withQuote(Fixture x, Pricing.Quote q) {
        return new Admission.Service(x.store,id->q,x.f.clock,ZoneId.of("America/New_York"));
    }
    static void rejected(String field,Object value) throws Exception {
        var x=new Fixture();var q=x.quote("alice",1,0);
        failure(DEPENDENCY_UNAVAILABLE,()->withQuote(x,change(q,field,value)).confirm(q.id(),"alice","test"));
        equal(0,x.store.size());
    }
    public static List<Case> cases() {
        var result=new ArrayList<Case>();
        result.add(new Case("Admission: exact quote and a grouped entitlement set are retained",()->{
            var x=new Fixture();var q=x.quote("alice",2,1);var o=x.buy(q,"admit");
            equal(q,o.quote());equal(21000L,o.quote().total().cents());equal(2,o.entitlements().size());
            equal(3,o.entitlements().stream().mapToInt(Admission.Entitlement::quantity).sum());
            equal(2L,o.entitlements().stream().map(Admission.Entitlement::id).distinct().count());
            for(var e:o.entitlements()){equal(o.id(),e.orderId());equal(o.confirmedAt(),e.issuedAt());equal(Admission.State.ACTIVE,e.state());}
            equal(3,x.f.booking.availability("october-lanterns").available());
        }));
        result.add(new Case("Admission: child-only order has one child entitlement",()->{
            var x=new Fixture();var o=x.buy(x.quote("alice",0,2),"child");equal(1,o.entitlements().size());
            equal(Catalog.Variant.CHILD,o.entitlements().getFirst().variant());equal(2,o.entitlements().getFirst().quantity());
        }));
        result.add(new Case("Admission: zero-dollar quotes still issue exactly once",()->{
            var x=new Fixture();x.f.pricing.publish(x.f.book("park-day",2,"0.00","0.00",null));
            var q=x.quote("alice",20,0);var o=x.buy(q,"zero");equal(0L,o.quote().total().cents());equal(o,x.buy(q,"zero"));equal(20,o.entitlements().getFirst().quantity());
        }));
        result.add(new Case("Admission: repeated identical key returns the original aggregate",()->{
            var x=new Fixture();var q=x.quote("alice",1,0);var o=x.buy(q,"retry");
            for(int i=0;i<20;i++)equal(o,x.buy(q,"retry"));equal(1,x.store.size());
        }));
        result.add(new Case("Admission: committed replay needs neither fresh quote nor pricing service",()->{
            var x=new Fixture();var q=x.quote("alice",1,0);var o=x.buy(q,"retry");x.f.clock.advance(Duration.ofDays(40));
            var replay=new Admission.Service(x.store,id->{throw new AssertionError("Pricing called");},x.f.clock,ZoneId.of("America/New_York"));
            equal(o,replay.confirm(q.id(),"alice","retry"));equal(o,replay.recover(q.id(),"alice","retry").order());equal(o,replay.get(o.id(),"alice"));
        }));
        result.add(new Case("Admission: a key cannot change its quote",()->{
            var x=new Fixture();x.buy(x.quote("alice",1,0),"same");var q=x.quote("alice",1,0);
            failure(CONFLICT,()->x.buy(q,"same"));equal(1,x.store.size());
        }));
        result.add(new Case("Admission: re-keying a consumed quote cannot issue again",()->{
            var x=new Fixture();var q=x.quote("alice",1,0);x.buy(q,"one");failure(CONFLICT,()->x.buy(q,"two"));equal(1,x.store.size());
        }));
        result.add(new Case("Admission: keys are scoped to authenticated owner",()->{
            var x=new Fixture();var a=x.buy(x.quote("alice",1,0),"same");var b=x.buy(x.quote("bob",1,0),"same");
            check(!a.id().equals(b.id()));equal(2,x.store.size());
        }));
        result.add(new Case("Admission: key case distinguishes independent purchases",()->{
            var x=new Fixture();x.buy(x.quote("alice",1,0),"Key");x.buy(x.quote("alice",1,0),"key");equal(2,x.store.size());
        }));
        result.add(new Case("Admission: foreign quote is indistinguishable from not found",()->{
            var x=new Fixture();var q=x.quote("alice",1,0);failure(NOT_FOUND,()->x.service.confirm(q.id(),"bob","same"));equal(0,x.store.size());
        }));
        result.add(new Case("Admission: event quotes are rejected before admission persistence",()->{
            var x=new Fixture();var q=x.f.quote("alice",1,0);failure(INVALID_INPUT,()->x.buy(q,"event"));equal(0,x.store.size());
        }));
        result.add(new Case("Admission: invalid arguments fail before pricing",()->{
            var x=new Fixture();var s=new Admission.Service(x.store,id->{throw new AssertionError("Network called");},x.f.clock,ZoneId.of("America/New_York"));
            failure(INVALID_INPUT,()->s.confirm(null,"alice","key"));failure(INVALID_INPUT,()->s.confirm(UUID.randomUUID()," ","key"));
            for(String k:Arrays.asList(null,"","bad key","x".repeat(101)))failure(INVALID_INPUT,()->s.confirm(UUID.randomUUID(),"alice",k));
        }));
        result.add(new Case("Admission: expired-at-equality rejects a new purchase",()->{
            var x=new Fixture();var q=x.quote("alice",1,0);x.f.clock.advance(Duration.ofMinutes(15));failure(EXPIRED,()->x.buy(q,"expired"));equal(0,x.store.size());
        }));
        result.add(new Case("Admission: one millisecond before expiry is accepted",()->{
            var x=new Fixture();var q=x.quote("alice",1,0);x.f.clock.advance(Duration.ofMillis(899999));equal(q,x.buy(q,"last-ms").quote());
        }));
        result.add(new Case("Admission: slow dependency cannot extend the quote deadline",()->{
            var x=new Fixture();var q=x.quote("alice",1,0);var s=new Admission.Service(x.store,id->{x.f.clock.advance(Duration.ofMinutes(15));return q;},x.f.clock,ZoneId.of("America/New_York"));
            failure(EXPIRED,()->s.confirm(q.id(),"alice","slow"));equal(0,x.store.size());
        }));
        result.add(new Case("Admission: delayed store entry rechecks time before issuance",()->{
            var x=new Fixture();var q=x.quote("alice",1,0);x.store.beforeCreate=()->x.f.clock.advance(Duration.ofMinutes(15));
            failure(EXPIRED,()->x.buy(q,"delay"));equal(0,x.store.size());
        }));
        result.add(new Case("Admission: expired visit date cannot issue after park midnight",()->{
            var x=new Fixture();var start=Instant.parse("2026-10-12T03:59:00Z");var q=x.quote("alice",1,0);
            q=change(change(change(q,"visitDate",LocalDate.of(2026,10,11)),"createdAt",start),"expiresAt",start.plusSeconds(900));
            var snapshot=q;var s=new Admission.Service(x.store,id->snapshot,Clock.fixed(start.plusSeconds(60),ZoneOffset.UTC),ZoneId.of("America/New_York"));
            failure(EXPIRED,()->s.confirm(snapshot.id(),"alice","midnight"));equal(0,x.store.size());
        }));
        result.add(new Case("Admission: future-issued quote fails closed",()->{
            var x=new Fixture();var q=x.quote("alice",1,0);q=change(q,"createdAt",x.f.clock.instant().plusSeconds(1));
            var snapshot=q;failure(DEPENDENCY_UNAVAILABLE,()->withQuote(x,snapshot).confirm(snapshot.id(),"alice","future"));equal(0,x.store.size());
        }));
        result.add(new Case("Admission: old pricing version and discount snapshot survive publication",()->{
            var x=new Fixture();x.f.pricing.publish(x.f.book("park-day",2,"80.00","50.00",new Pricing.Offer("family",3,1000)));
            var q=x.quote("alice",2,1);x.f.pricing.publish(x.f.book("park-day",3,"99.00","70.00",null));
            var o=x.buy(q,"old");equal(2L,o.quote().pricingVersion());equal(18900L,o.quote().total().cents());equal(q,o.quote());
        }));
        result.add(new Case("Admission: recovery of absent result performs no write or pricing call",()->{
            var x=new Fixture();var s=new Admission.Service(x.store,id->{throw new AssertionError("Pricing called");},x.f.clock,ZoneId.of("America/New_York"));
            var r=s.recover(UUID.randomUUID(),"alice","unknown");equal(Admission.RecoveryState.NOT_CONFIRMED,r.state());equal(null,r.order());equal(0,x.store.size());
        }));
        result.add(new Case("Admission: recovery returns exact committed snapshot",()->{
            var x=new Fixture();var q=x.quote("alice",1,1);var o=x.buy(q,"key");var r=x.service.recover(q.id(),"alice","key");
            equal(Admission.RecoveryState.CONFIRMED,r.state());equal(o,r.order());
        }));
        result.add(new Case("Admission: recovery never discloses another account receipt",()->{
            var x=new Fixture();var q=x.quote("alice",1,0);x.buy(q,"key");equal(Admission.RecoveryState.NOT_CONFIRMED,x.service.recover(q.id(),"bob","key").state());
        }));
        result.add(new Case("Admission: changed recovery key or quote conflicts",()->{
            var x=new Fixture();var q=x.quote("alice",1,0);x.buy(q,"key");failure(CONFLICT,()->x.service.recover(q.id(),"alice","other"));
            failure(CONFLICT,()->x.service.recover(UUID.randomUUID(),"alice","key"));
        }));
        result.add(new Case("Admission: order reads require owner and cannot enumerate foreign IDs",()->{
            var x=new Fixture();var o=x.buy(x.quote("alice",1,0),"key");equal(o,x.service.get(o.id(),"alice"));
            failure(NOT_FOUND,()->x.service.get(o.id(),"bob"));failure(NOT_FOUND,()->x.service.get(UUID.randomUUID(),"alice"));
            failure(INVALID_INPUT,()->x.service.get(null,"alice"));failure(INVALID_INPUT,()->x.service.get(o.id(),""));
        }));
        result.add(new Case("Admission: all issued lists remain immutable",()->{
            var x=new Fixture();var o=x.buy(x.quote("alice",1,0),"key");
            try{o.entitlements().clear();throw new AssertionError("Mutable entitlements");}catch(UnsupportedOperationException expected){}
            try{o.quote().lines().clear();throw new AssertionError("Mutable quote");}catch(UnsupportedOperationException expected){}
        }));
        result.add(new Case("Admission: reconstructed storage retains exact identities",()->{
            var x=new Fixture();var q=x.quote("alice",1,1);var o=x.buy(q,"key");
            var s=new Admission.Service(x.store.restartCopy(),id->{throw new AssertionError("Pricing called");},x.f.clock,ZoneId.of("America/New_York"));equal(o,s.confirm(q.id(),"alice","key"));
        }));
        result.add(new Case("Admission: failure before atomic publication leaves no entitlement set",()->{
            var x=new Fixture();var q=x.quote("alice",1,1);x.store.beforePublish=()->{throw new IllegalStateException("Injected write failure");};
            try{x.buy(q,"key");throw new AssertionError("No failure");}catch(IllegalStateException expected){}
            equal(0,x.store.size());x.store.beforePublish=()->{};equal(2,x.buy(q,"key").entitlements().size());equal(1,x.store.size());
        }));
        result.add(new Case("Admission: event flow and route-specific key scope stay unchanged",()->{
            var x=new Fixture();var h=x.f.hold("alice");var event=x.f.booking.confirm(h.id(),"alice","same-key");
            var order=x.buy(x.quote("alice",2,1),"same-key");equal(3,order.quote().quantity());
            equal(event,x.f.booking.get(event.id(),"alice"));equal(2,x.f.booking.availability("october-lanterns").available());
            equal(1,x.f.bookings.transact("october-lanterns",l->l.orders().size()));
        }));
        result.add(new Case("Admission: 24 same-key contenders receive one entitlement set",()->race(true,true)));
        result.add(new Case("Admission: 24 keys competing for one quote produce one winner",()->race(false,true)));
        result.add(new Case("Admission: 24 quotes competing for one owner-key produce one winner",()->race(true,false)));
        result.add(new Case("Admission: missing upstream snapshot fails closed",()->{
            var x=new Fixture();failure(DEPENDENCY_UNAVAILABLE,()->withQuote(x,null).confirm(UUID.randomUUID(),"alice","key"));equal(0,x.store.size());
        }));
        result.add(new Case("Admission: mismatched upstream ID fails closed",()->rejected("id",UUID.randomUUID())));
        result.add(new Case("Admission: null upstream owner fails closed",()->rejected("owner",null)));
        result.add(new Case("Admission: invalid product identifier fails closed",()->rejected("productId","Bad Product")));
        result.add(new Case("Admission: blank product name fails closed",()->rejected("productName"," ")));
        result.add(new Case("Admission: missing kind fails closed",()->rejected("kind",null)));
        result.add(new Case("Admission: invalid catalog revision fails closed",()->rejected("catalogRevision",0L)));
        result.add(new Case("Admission: invalid pricing version fails closed",()->rejected("pricingVersion",0L)));
        result.add(new Case("Admission: missing visit date fails closed",()->rejected("visitDate",null)));
        result.add(new Case("Admission: missing quote issue time fails closed",()->rejected("createdAt",null)));
        result.add(new Case("Admission: missing quote expiry fails closed",()->rejected("expiresAt",null)));
        result.add(new Case("Admission: quantity mismatch fails closed",()->rejected("quantity",2)));
        result.add(new Case("Admission: incorrect total fails closed",()->rejected("total",new Money(1))));
        result.add(new Case("Admission: incorrect subtotal fails closed",()->rejected("subtotal",new Money(1))));
        result.add(new Case("Admission: incorrect discount fails closed",()->rejected("discount",new Money(1))));
        result.add(new Case("Admission: duplicate variants fail closed",()->{
            var x=new Fixture();var q=x.quote("alice",1,0);var malformed=lines(q,List.of(q.lines().getFirst(),q.lines().getFirst()));
            failure(DEPENDENCY_UNAVAILABLE,()->withQuote(x,malformed).confirm(q.id(),"alice","key"));equal(0,x.store.size());
        }));
        result.add(new Case("Admission: line arithmetic fails closed",()->{
            var x=new Fixture();var q=x.quote("alice",1,0);var malformed=lines(q,List.of(new Pricing.Line(Catalog.Variant.ADULT,1,new Money(1),new Money(8000),new Money(0),new Money(8000))));
            failure(DEPENDENCY_UNAVAILABLE,()->withQuote(x,malformed).confirm(q.id(),"alice","key"));equal(0,x.store.size());
        }));
        result.add(new Case("Admission: empty line set fails closed",()->{
            var x=new Fixture();var q=x.quote("alice",1,0);failure(DEPENDENCY_UNAVAILABLE,()->withQuote(x,lines(q,List.of())).confirm(q.id(),"alice","key"));equal(0,x.store.size());
        }));
        return List.copyOf(result);
    }
    private static void race(boolean sameKey,boolean sameQuote)throws Exception {
        var x=new Fixture();var shared=x.quote("alice",1,1);var barrier=new CyclicBarrier(24);
        var s=new Admission.Service(x.store,id->{try{barrier.await(10,TimeUnit.SECONDS);}catch(Exception e){throw new AssertionError(e);}return x.f.pricing.internal(id);},x.f.clock,ZoneId.of("America/New_York"));
        var jobs=new ArrayList<Callable<Admission.Order>>();
        for(int i=0;i<24;i++){var q=sameQuote?shared:x.quote("alice",1,1);var key=sameKey?"race":"race-"+i;
            jobs.add(()->{try{return s.confirm(q.id(),"alice",key);}catch(Failure e){equal(CONFLICT,e.code());return null;}});}
        try(var pool=Executors.newFixedThreadPool(24)) {
            var ids=new HashSet<UUID>();int successes=0;
            for(var f:pool.invokeAll(jobs,20,TimeUnit.SECONDS)){var o=f.get();if(o!=null){ids.add(o.id());successes++;}}
            equal(1,ids.size());equal(sameKey&&sameQuote?24:1,successes);
        }
        equal(1,x.store.size());equal(3,x.f.booking.availability("october-lanterns").available());
    }
}
