package dev.parkcommerce.core;

import java.util.*;
import java.util.function.Supplier;

/** TEST ONLY: aggregate snapshots, not SQL locking or durability evidence. */
public final class MemoryAdmissionStore implements Admission.Store {
    private final Map<UUID, Admission.Order> orders = new LinkedHashMap<>();
    public Runnable beforeCreate = () -> {};
    public Runnable beforePublish = () -> {};
    @Override public synchronized Optional<Admission.Order> order(UUID id) { return Optional.ofNullable(orders.get(id)); }
    @Override public synchronized Optional<Admission.Order> byKey(String owner, String key) {
        return orders.values().stream().filter(o->o.quote().owner().equals(owner)&&o.idempotencyKey().equals(key)).findFirst();
    }
    @Override public synchronized Optional<Admission.Order> byQuote(UUID quoteId) {
        return orders.values().stream().filter(o->o.quote().id().equals(quoteId)).findFirst();
    }
    @Override public synchronized Admission.Order create(UUID quoteId,String owner,String key,Supplier<Admission.Order> factory) {
        var prior=byKey(owner,key);if(prior.isPresent())return Admission.sameRequest(prior.get(),quoteId,owner,key);
        if(byQuote(quoteId).isPresent())throw new Failure(Failure.Code.CONFLICT,"Admission quote already consumed");
        beforeCreate.run();var candidate=Admission.sameRequest(factory.get(),quoteId,owner,key);
        beforePublish.run();orders.put(candidate.id(),candidate);return candidate;
    }
    public synchronized int size() { return orders.size(); }
    public synchronized MemoryAdmissionStore restartCopy() {var copy=new MemoryAdmissionStore();copy.orders.putAll(orders);return copy;}
}
