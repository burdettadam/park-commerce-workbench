package dev.parkcommerce.core;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import static dev.parkcommerce.core.CoreScenarios.*;
import static dev.parkcommerce.core.Failure.Code.*;

/** Domain recovery checks. Memory adapters are not evidence of MySQL isolation. */
public final class CheckoutRecoveryScenarios {
    private CheckoutRecoveryScenarios() {}
    public static List<Case> cases() {
        return List.of(
            new Case("Recovery: an active hold does not confirm or allocate again", () -> {
                var f=new ScenarioFixture(); var h=f.hold("alice");
                var before=f.booking.availability(h.eventId());
                var state=f.booking.recover(h.id(),"alice","lost-response");
                equal(h,state.hold()); equal(null,state.order());
                equal(before,f.booking.availability(h.eventId()));
                equal(Optional.empty(),f.bookings.orderByKey("alice","lost-response"));
            }),
            new Case("Recovery: returns exactly the original confirmed order", () -> {
                var f=new ScenarioFixture(); var h=f.hold("alice");
                var o=f.booking.confirm(h.id(),"alice","same");
                var state=f.booking.recover(h.id(),"alice","same");
                equal(Booking.HoldStatus.CONFIRMED,state.hold().status()); equal(o,state.order());
                equal(o,f.booking.confirm(h.id(),"alice","same"));
                equal(2,f.booking.availability(h.eventId()).available());
            }),
            new Case("Recovery: confirmed order survives quote and hold expiration", () -> {
                var f=new ScenarioFixture(); var h=f.hold("alice"); var o=f.booking.confirm(h.id(),"alice","same");
                f.clock.advance(Duration.ofDays(1));
                equal(o,f.booking.recover(h.id(),"alice","same").order());
                equal(2,f.booking.availability(h.eventId()).available());
            }),
            new Case("Recovery: expiry releases an active allocation exactly once", () -> {
                var f=new ScenarioFixture(); var h=f.hold("alice"); f.clock.advance(Duration.ofMinutes(5));
                var state=f.booking.recover(h.id(),"alice","same");
                equal(Booking.HoldStatus.EXPIRED,state.hold().status()); equal(null,state.order());
                equal(Booking.HoldStatus.EXPIRED,f.bookings.hold(h.id()).orElseThrow().status());
                equal(state,f.booking.recover(h.id(),"alice","same"));
                equal(3,f.booking.availability(h.eventId()).available());
                failure(EXPIRED,()->f.booking.confirm(h.id(),"alice","same"));
            }),
            new Case("Recovery: one millisecond before expiry remains active", () -> {
                var f=new ScenarioFixture(); var h=f.hold("alice"); f.clock.advance(Duration.ofMillis(299999));
                equal(Booking.HoldStatus.ACTIVE,f.booking.recover(h.id(),"alice","same").hold().status());
                equal(2,f.booking.availability(h.eventId()).available());
            }),
            new Case("Recovery: another guest cannot read an active hold", () -> {
                var f=new ScenarioFixture(); var h=f.hold("alice");
                failure(NOT_FOUND,()->f.booking.recover(h.id(),"bob","same"));
                equal(h,f.bookings.hold(h.id()).orElseThrow());
            }),
            new Case("Recovery: another guest cannot read a confirmed receipt", () -> {
                var f=new ScenarioFixture(); var h=f.hold("alice"); f.booking.confirm(h.id(),"alice","same");
                failure(NOT_FOUND,()->f.booking.recover(h.id(),"bob","same"));
            }),
            new Case("Recovery: unknown hold has the same not-found outcome", () -> {
                var f=new ScenarioFixture(); failure(NOT_FOUND,()->f.booking.recover(UUID.randomUUID(),"alice","same"));
            }),
            new Case("Recovery: a key cannot be rebound to another hold", () -> {
                var f=new ScenarioFixture(); var a=f.hold("alice"); var b=f.hold("alice");
                f.booking.confirm(a.id(),"alice","same");
                failure(CONFLICT,()->f.booking.recover(b.id(),"alice","same"));
                equal(Booking.HoldStatus.ACTIVE,f.bookings.hold(b.id()).orElseThrow().status());
            }),
            new Case("Recovery: a different key cannot read a confirmed result", () -> {
                var f=new ScenarioFixture(); var h=f.hold("alice"); f.booking.confirm(h.id(),"alice","first");
                failure(CONFLICT,()->f.booking.recover(h.id(),"alice","second"));
            }),
            new Case("Recovery: keys remain case sensitive", () -> {
                var f=new ScenarioFixture(); var h=f.hold("alice"); f.booking.confirm(h.id(),"alice","Key");
                failure(CONFLICT,()->f.booking.recover(h.id(),"alice","key"));
            }),
            new Case("Recovery: invalid identity is rejected before any mutation", () -> {
                var f=new ScenarioFixture(); var h=f.hold("alice");
                failure(INVALID_INPUT,()->f.booking.recover(null,"alice","same"));
                failure(INVALID_INPUT,()->f.booking.recover(h.id()," ","same"));
                for(String key: Arrays.asList(null,"","bad key","x".repeat(101)))
                    failure(INVALID_INPUT,()->f.booking.recover(h.id(),"alice",key));
                equal(h,f.bookings.hold(h.id()).orElseThrow());
            }),
            new Case("Recovery: never consults the pricing dependency", () -> {
                var f=new ScenarioFixture(); var h=f.hold("alice");
                var withoutPricing=new Booking.Service(f.bookings,id->{throw new AssertionError("Network call during recovery");},f.clock);
                equal(h,withoutPricing.recover(h.id(),"alice","same").hold());
                var o=f.booking.confirm(h.id(),"alice","same");
                equal(o,withoutPricing.recover(h.id(),"alice","same").order());
            }),
            new Case("Recovery: reconstructed service returns persisted identity", () -> {
                var f=new ScenarioFixture(); var h=f.hold("alice"); var o=f.booking.confirm(h.id(),"alice","same");
                var restored=new Booking.Service(f.bookings.restartCopy(),id->{throw new AssertionError("Pricing unavailable");},f.clock);
                equal(o,restored.recover(h.id(),"alice","same").order());
            }),
            new Case("Recovery: concurrent observations and confirmations still create one order", CheckoutRecoveryScenarios::concurrent),
            new Case("Recovery: repeated active observations do not refresh the deadline", () -> {
                var f=new ScenarioFixture(); var h=f.hold("alice");
                for(int i=0;i<10;i++){f.clock.advance(Duration.ofSeconds(20));equal(h.expiresAt(),f.booking.recover(h.id(),"alice","same").hold().expiresAt());}
                equal(2,f.booking.availability(h.eventId()).available());
            })
        );
    }
    private static void concurrent() throws Exception {
        var f=new ScenarioFixture(); var h=f.hold("alice");
        try(var pool=Executors.newFixedThreadPool(8)) {
            var jobs=new ArrayList<Callable<Boolean>>();
            for(int i=0;i<24;i++) {
                final boolean confirm=i%3==0;
                jobs.add(()->{
                    if(confirm) f.booking.confirm(h.id(),"alice","same");
                    else {var state=f.booking.recover(h.id(),"alice","same");
                        check(state.hold().status()==Booking.HoldStatus.ACTIVE || state.hold().status()==Booking.HoldStatus.CONFIRMED);
                        equal(state.hold().status()==Booking.HoldStatus.CONFIRMED,state.order()!=null);}
                    return true;
                });
            }
            for(var result:pool.invokeAll(jobs,5,TimeUnit.SECONDS)) check(result.get());
        }
        var o=f.bookings.orderByKey("alice","same").orElseThrow();
        equal(o,f.booking.recover(h.id(),"alice","same").order());
        equal(1,f.bookings.transact(h.eventId(),ledger->ledger.orders().size()));
        equal(2,f.booking.availability(h.eventId()).available());
    }
}
