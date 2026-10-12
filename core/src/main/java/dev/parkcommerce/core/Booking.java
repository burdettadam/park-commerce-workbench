package dev.parkcommerce.core;

import java.time.*;
import java.util.*;
import java.util.function.Function;

public final class Booking {
    private Booking() {}
    public enum HoldStatus { ACTIVE, CONFIRMED, EXPIRED }
    public record Event(String id, String productId, LocalDate visitDate, int capacity) {
        public Event {
            Failure.id(id, "Event ID"); Failure.id(productId, "Product ID");
            Failure.require(visitDate != null, "Event date is required");
            Failure.require(capacity >= 1 && capacity <= 1_000_000, "Capacity must be 1-1,000,000");
        }
    }
    public record Hold(UUID id, String eventId, String owner, UUID quoteId, int quantity, Money total,
                       Instant createdAt, Instant expiresAt, HoldStatus status) {
        public Hold withStatus(HoldStatus value) { return new Hold(id, eventId, owner, quoteId, quantity, total, createdAt, expiresAt, value); }
    }
    public record Order(UUID id, UUID holdId, String eventId, String owner, String idempotencyKey,
                        UUID quoteId, int quantity, Money total, Instant confirmedAt) {}
    public record Availability(String eventId, int capacity, int confirmed, int held, int available) {}
    /** Server-observed state. Recovery never confirms a purchase or creates a new hold. */
    public record Recovery(Hold hold, Order order) {}

    /** Mutable aggregate exists ONLY within Store.transact's exclusive transaction. Never cache it. */
    public static final class Ledger {
        private final Event event;
        private final Map<UUID, Hold> holds;
        private final Map<UUID, Order> orders;
        public Ledger(Event event, Collection<Hold> holds, Collection<Order> orders) {
            this.event = event; this.holds = new LinkedHashMap<>(); this.orders = new LinkedHashMap<>();
            for (Hold hold : holds) this.holds.put(hold.id(), hold);
            for (Order order : orders) this.orders.put(order.id(), order);
        }
        public Event event() { return event; }
        public List<Hold> holds() { return List.copyOf(holds.values()); }
        public List<Order> orders() { return List.copyOf(orders.values()); }
        public Ledger copy() { return new Ledger(event, holds.values(), orders.values()); }
        public void expire(Instant now) {
            holds.replaceAll((id, hold) -> hold.status() == HoldStatus.ACTIVE && !now.isBefore(hold.expiresAt())
                ? hold.withStatus(HoldStatus.EXPIRED) : hold);
        }
        public Availability availability(Instant now) {
            expire(now);
            int confirmed = holds.values().stream().filter(h -> h.status() == HoldStatus.CONFIRMED).mapToInt(Hold::quantity).sum();
            int held = holds.values().stream().filter(h -> h.status() == HoldStatus.ACTIVE).mapToInt(Hold::quantity).sum();
            return new Availability(event.id(), event.capacity(), confirmed, held, event.capacity() - confirmed - held);
        }
        public Hold reserve(Pricing.Quote quote, String owner, Instant now) {
            if (!quote.owner().equals(owner)) throw new Failure(Failure.Code.NOT_FOUND, "Quote not found");
            Failure.require(quote.kind() == Catalog.Kind.EVENT, "Capacity holds require an EVENT product");
            Failure.require(event.productId().equals(quote.productId()) && event.visitDate().equals(quote.visitDate()), "Quote does not match event product/date");
            if (!quote.validAt(now)) throw new Failure(Failure.Code.EXPIRED, "Quote has expired");
            expire(now);
            Optional<Hold> previous = holds.values().stream().filter(h -> h.quoteId().equals(quote.id())).findFirst();
            if (previous.isPresent()) {
                Hold hold = previous.get();
                if (hold.status() != HoldStatus.ACTIVE) throw new Failure(Failure.Code.CONFLICT, "This quote has already been consumed; request a new quote");
                return hold; // Reserve retries for a quote do not consume extra capacity.
            }
            if (availability(now).available() < quote.quantity()) throw new Failure(Failure.Code.SOLD_OUT, "Insufficient event capacity");
            Instant limit = now.plus(Duration.ofMinutes(5));
            Instant expires = limit.isBefore(quote.expiresAt()) ? limit : quote.expiresAt();
            Hold hold = new Hold(UUID.randomUUID(), event.id(), owner, quote.id(), quote.quantity(), quote.total(), now, expires, HoldStatus.ACTIVE);
            holds.put(hold.id(), hold);
            return hold;
        }
        public Recovery recover(UUID holdId, String owner, String key, Instant now) {
            Hold hold = holds.get(holdId);
            if (hold == null || !hold.owner().equals(owner)) throw new Failure(Failure.Code.NOT_FOUND, "Hold not found");
            Optional<Order> prior = orders.values().stream()
                .filter(o -> o.owner().equals(owner) && o.idempotencyKey().equals(key)).findFirst();
            if (prior.isPresent()) {
                Order order = sameRequest(prior.get(), holdId);
                if (hold.status() != HoldStatus.CONFIRMED)
                    throw new Failure(Failure.Code.CONFLICT, "Checkout state is inconsistent; do not start another purchase");
                return new Recovery(hold, order);
            }
            if (hold.status() == HoldStatus.CONFIRMED)
                throw new Failure(Failure.Code.CONFLICT, "Hold already confirmed with another key");
            expire(now); // Reconcile expiry within the same exclusive transaction as confirmation.
            return new Recovery(holds.get(holdId), null);
        }
        public Order confirm(UUID holdId, String owner, String key, Instant now) {
            Optional<Order> prior = orders.values().stream().filter(o -> o.owner().equals(owner) && o.idempotencyKey().equals(key)).findFirst();
            if (prior.isPresent()) return sameRequest(prior.get(), holdId);
            Hold hold = holds.get(holdId);
            if (hold == null || !hold.owner().equals(owner)) throw new Failure(Failure.Code.NOT_FOUND, "Hold not found");
            if (hold.status() == HoldStatus.CONFIRMED) throw new Failure(Failure.Code.CONFLICT, "Hold already confirmed with another key");
            if (hold.status() == HoldStatus.EXPIRED || !now.isBefore(hold.expiresAt())) throw new Failure(Failure.Code.EXPIRED, "Hold has expired");
            Order order = new Order(UUID.randomUUID(), hold.id(), event.id(), owner, key, hold.quoteId(), hold.quantity(), hold.total(), now);
            holds.put(hold.id(), hold.withStatus(HoldStatus.CONFIRMED)); orders.put(order.id(), order);
            return order;
        }
    }
    public interface Store {
        void create(Event event);
        Optional<Hold> hold(UUID id);
        Optional<Order> order(UUID id);
        Optional<Order> orderByKey(String owner, String key);
        /** Exclusive, rollback-on-error, durable transaction. Must enforce global owner/key and hold/order uniqueness. */
        <T> T transact(String eventId, Function<Ledger, T> operation);
    }
    public static Order sameRequest(Order order, UUID holdId) {
        if (!order.holdId().equals(holdId)) throw new Failure(Failure.Code.CONFLICT, "Idempotency key was used for a different hold");
        return order;
    }
    public static final class Service {
        private final Store store;
        private final Pricing.Client pricing;
        private final Clock clock;
        public Service(Store store, Pricing.Client pricing, Clock clock) {
            this.store = Objects.requireNonNull(store); this.pricing = Objects.requireNonNull(pricing); this.clock = Objects.requireNonNull(clock);
        }
        public Event create(Event event) { store.create(event); return event; }
        public Availability availability(String eventId) {
            Failure.id(eventId, "Event ID");
            return store.transact(eventId, ledger -> ledger.availability(clock.instant()));
        }
        public Hold reserve(String eventId, UUID quoteId, String owner) {
            Failure.id(eventId, "Event ID"); Failure.owner(owner); Failure.require(quoteId != null, "Quote ID is required");
            Pricing.Quote quote = pricing.quote(quoteId); // No network call while holding the inventory row lock.
            // Fail closed on a broken upstream contract before touching inventory.
            if (quote == null || !quoteId.equals(quote.id()) || quote.owner() == null || quote.kind() == null
                    || quote.productId() == null || quote.visitDate() == null || quote.total() == null
                    || quote.quantity() < 1 || quote.quantity() > 20 || quote.createdAt() == null
                    || quote.expiresAt() == null || !quote.createdAt().isBefore(quote.expiresAt()))
                throw new Failure(Failure.Code.DEPENDENCY_UNAVAILABLE, "Pricing returned an invalid quote");
            return store.transact(eventId, ledger -> ledger.reserve(quote, owner, clock.instant()));
        }
        public Order confirm(UUID holdId, String owner, String key) {
            validateCheckout(holdId, owner, key);
            Optional<Order> previous = store.orderByKey(owner, key);
            if (previous.isPresent()) return sameRequest(previous.get(), holdId);
            Hold hold = store.hold(holdId).orElseThrow(() -> new Failure(Failure.Code.NOT_FOUND, "Hold not found"));
            if (!hold.owner().equals(owner)) throw new Failure(Failure.Code.NOT_FOUND, "Hold not found");
            return store.transact(hold.eventId(), ledger -> ledger.confirm(holdId, owner, key, clock.instant()));
        }
        /** Recover using an authenticated owner and the ORIGINAL key, even after quote expiry.
         *  No pricing/network request is made; inventory locking serializes this observation
         *  with confirmation. A stale ACTIVE response is not permission to discard the key.
         */
        public Recovery recover(UUID holdId, String owner, String key) {
            validateCheckout(holdId, owner, key);
            Hold hold = store.hold(holdId).orElseThrow(() -> new Failure(Failure.Code.NOT_FOUND, "Hold not found"));
            if (!hold.owner().equals(owner)) throw new Failure(Failure.Code.NOT_FOUND, "Hold not found");
            // Check globally scoped key identity before taking this event's transaction lock.
            store.orderByKey(owner, key).ifPresent(order -> sameRequest(order, holdId));
            return store.transact(hold.eventId(), ledger -> ledger.recover(holdId, owner, key, clock.instant()));
        }
        private static void validateCheckout(UUID holdId, String owner, String key) {
            Failure.owner(owner); Failure.require(holdId != null, "Hold ID is required");
            Failure.require(key != null && key.matches("[A-Za-z0-9._-]{1,100}"), "Idempotency-Key must contain 1-100 safe ASCII characters");
        }
        public Order get(UUID id, String owner) {
            Failure.owner(owner); Failure.require(id != null, "Order ID is required");
            Order order = store.order(id).orElseThrow(() -> new Failure(Failure.Code.NOT_FOUND, "Order not found"));
            if (!order.owner().equals(owner)) throw new Failure(Failure.Code.NOT_FOUND, "Order not found");
            return order;
        }
    }
}
