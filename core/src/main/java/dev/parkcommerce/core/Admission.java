package dev.parkcommerce.core;

import java.time.*;
import java.util.*;
import java.util.function.Supplier;

/** Dated admission has no event inventory dependency. All payments are simulated. */
public final class Admission {
    private Admission() {}

    /** VOID is reserved for a future audited cancellation operation; this slice only issues ACTIVE. */
    public enum State { ACTIVE, VOID }
    public record Entitlement(UUID id, UUID orderId, Catalog.Variant variant, int quantity,
                              State state, Instant issuedAt) {
        public Entitlement {
            Failure.require(id != null && orderId != null && variant != null && issuedAt != null,
                "A complete admission entitlement is required");
            Failure.require(quantity >= 1 && quantity <= 20 && state != null, "Invalid entitlement quantity/state");
        }
    }

    /** Immutable issuance receipt, including the exact quote and complete entitlement set.
     *  Each child resolves owner, date, product and versions through this same snapshot.
     *  It is not a live park-access credential or a future cancellation status endpoint.
     */
    public record Order(UUID id, Pricing.Quote quote, String idempotencyKey, Instant confirmedAt,
                        List<Entitlement> entitlements) {
        public Order {
            Failure.require(id != null && quote != null && quote.kind() == Catalog.Kind.ADMISSION
                && confirmedAt != null && entitlements != null, "A complete admission order is required");
            validateKey(idempotencyKey);
            Failure.require(validSnapshot(quote), "Invalid stored admission quote");
            Failure.require(!confirmedAt.isBefore(quote.createdAt()) && quote.validAt(confirmedAt), "Invalid issuance time");
            entitlements = List.copyOf(entitlements);
            Failure.require(entitlements.size() == quote.lines().size(), "Incomplete entitlement set");
            var variants = EnumSet.noneOf(Catalog.Variant.class);
            var ids = new HashSet<UUID>();
            for (var e : entitlements) {
                Failure.require(e != null && ids.add(e.id()) && variants.add(e.variant())
                    && id.equals(e.orderId()) && confirmedAt.equals(e.issuedAt()) && e.state() == State.ACTIVE,
                    "Invalid issuance identity or entitlement state");
                Failure.require(quote.lines().stream().anyMatch(l -> l.variant() == e.variant() && l.quantity() == e.quantity()),
                    "Entitlement does not match its quote line");
            }
        }
    }
    public enum RecoveryState { CONFIRMED, NOT_CONFIRMED }
    /** NOT_CONFIRMED is an observation, not permission to change the key of an uncertain request. */
    public record Recovery(RecoveryState state, Order order) {}

    public interface Store {
        Optional<Order> order(UUID id);
        Optional<Order> byKey(String owner, String key);
        Optional<Order> byQuote(UUID quoteId);
        /** Atomically retain one complete issuance snapshot with unique quote and owner/key.
         *  Recheck existing identity before calling the factory; a concurrent identical winner
         *  returns its ORIGINAL snapshot, not the discarded factory result. A different
         *  request conflicts. Failed writes cannot publish a partial entitlement set.
         *  Factory must perform no I/O; it captures the new attempt's evaluation clock.
         */
        Order create(UUID quoteId, String owner, String key, Supplier<Order> factory);
    }

    public static Order sameRequest(Order order, UUID quoteId, String owner, String key) {
        if (!order.quote().id().equals(quoteId) || !order.quote().owner().equals(owner)
                || !order.idempotencyKey().equals(key))
            throw new Failure(Failure.Code.CONFLICT, "Admission retry identity was used for a different request");
        return order;
    }
    private static void validateKey(String key) {
        Failure.require(key != null && key.matches("[A-Za-z0-9._-]{1,100}"),
            "Idempotency-Key must contain 1-100 safe ASCII characters");
    }
    private static void validateRequest(UUID quoteId, String owner, String key) {
        Failure.require(quoteId != null, "Quote ID is required"); Failure.owner(owner);
        Failure.require(java.nio.charset.StandardCharsets.UTF_8.newEncoder().canEncode(owner), "Authenticated subject must be valid Unicode");
        validateKey(key);
    }

    /** Check internally consistent pricing evidence, not independently recalculate its offer.
     *  The internal pricing service remains the authority for prices, eligibility and versions.
     */
    private static boolean validSnapshot(Pricing.Quote q) {
        try {
            if (q == null || q.id() == null || q.owner() == null || q.owner().isBlank() || q.owner().length() > 100
                    || q.productId() == null || !q.productId().matches("[a-z0-9][a-z0-9-]{0,63}")
                    || q.productName() == null || q.productName().isBlank() || q.productName().length() > 120
                    || q.kind() == null || q.catalogRevision() < 1 || q.pricingVersion() < 1
                    || q.visitDate() == null || q.visitDate().getYear() < 1 || q.visitDate().getYear() > 9999
                    || q.createdAt() == null || q.expiresAt() == null
                    || !q.expiresAt().equals(q.createdAt().plus(Pricing.Service.QUOTE_TTL))
                    || q.quantity() < 1 || q.quantity() > 20 || q.lines().isEmpty() || q.lines().size() > 2
                    || q.subtotal() == null || q.discount() == null || q.total() == null) return false;
            long subtotal = 0, discount = 0, total = 0;
            int quantity = 0;
            var variants = EnumSet.noneOf(Catalog.Variant.class);
            for (var line : q.lines()) {
                if (line == null || line.variant() == null || !variants.add(line.variant()) || line.quantity() < 1
                        || line.quantity() > 20 || line.unitPrice() == null || line.unitPrice().cents() > 1_000_000
                        || line.subtotal() == null || line.discount() == null || line.total() == null
                        || line.subtotal().cents() != line.unitPrice().cents() * line.quantity()
                        || line.discount().cents() > line.subtotal().cents()
                        || line.total().cents() != line.subtotal().cents() - line.discount().cents()) return false;
                quantity += line.quantity(); subtotal += line.subtotal().cents();
                discount += line.discount().cents(); total += line.total().cents();
            }
            if (q.appliedOffer() != null && !q.appliedOffer().matches("[a-z0-9][a-z0-9-]{0,63}")) return false;
            if (discount > 0 && q.appliedOffer() == null) return false;
            return quantity == q.quantity() && subtotal == q.subtotal().cents() && discount == q.discount().cents()
                && total == q.total().cents() && total == subtotal - discount;
        } catch (DateTimeException | ArithmeticException invalid) { return false; }
    }

    public static final class Service {
        private final Store store;
        private final Pricing.Client pricing;
        private final Clock clock;
        private final ZoneId parkZone;
        public Service(Store store, Pricing.Client pricing, Clock clock, ZoneId parkZone) {
            this.store = Objects.requireNonNull(store); this.pricing = Objects.requireNonNull(pricing);
            this.clock = Objects.requireNonNull(clock); this.parkZone = Objects.requireNonNull(parkZone);
        }
        public Order confirm(UUID quoteId, String owner, String key) {
            validateRequest(quoteId, owner, key);
            var prior = known(quoteId, owner, key);
            if (prior.isPresent()) return prior.get(); // Replay is independent of current pricing/expiry.
            Pricing.Quote quote = pricing.quote(quoteId); // Never hold database locks over the network.
            if (quote == null || !quoteId.equals(quote.id()) || quote.owner() == null)
                throw new Failure(Failure.Code.DEPENDENCY_UNAVAILABLE, "Pricing returned an invalid quote");
            if (!owner.equals(quote.owner())) throw new Failure(Failure.Code.NOT_FOUND, "Quote not found");
            if (!validSnapshot(quote)) throw new Failure(Failure.Code.DEPENDENCY_UNAVAILABLE, "Pricing returned an invalid quote");
            Failure.require(quote.kind() == Catalog.Kind.ADMISSION, "Admission checkout requires an ADMISSION quote");
            return store.create(quoteId, owner, key, () -> issue(quote, key));
        }
        private Order issue(Pricing.Quote quote, String key) {
            Instant now = clock.instant(); // Captured after dependency lookup and store entry.
            if (quote.createdAt().isAfter(now))
                throw new Failure(Failure.Code.DEPENDENCY_UNAVAILABLE, "Pricing returned a future-issued quote");
            if (!quote.validAt(now) || quote.visitDate().isBefore(now.atZone(parkZone).toLocalDate()))
                throw new Failure(Failure.Code.EXPIRED, "Quote or admission visit date has expired");
            UUID orderId = UUID.randomUUID();
            var entitlements = quote.lines().stream().map(line -> new Entitlement(UUID.randomUUID(), orderId,
                line.variant(), line.quantity(), State.ACTIVE, now)).toList();
            return new Order(orderId, quote, key, now, entitlements);
        }
        /** Read-only; never calls pricing, renews a quote, or creates an order. */
        public Recovery recover(UUID quoteId, String owner, String key) {
            validateRequest(quoteId, owner, key);
            return known(quoteId, owner, key).map(o -> new Recovery(RecoveryState.CONFIRMED, o))
                .orElseGet(() -> new Recovery(RecoveryState.NOT_CONFIRMED, null));
        }
        private Optional<Order> known(UUID quoteId, String owner, String key) {
            var byKey = store.byKey(owner, key);
            if (byKey.isPresent()) return Optional.of(sameRequest(byKey.get(), quoteId, owner, key));
            // A commit may become visible between the two reads. Honor that exact result too.
            var byQuote = store.byQuote(quoteId).filter(o -> o.quote().owner().equals(owner));
            return byQuote.map(o -> sameRequest(o, quoteId, owner, key));
        }
        public Order get(UUID orderId, String owner) {
            Failure.require(orderId != null, "Order ID is required"); Failure.owner(owner);
            return store.order(orderId).filter(o -> o.quote().owner().equals(owner))
                .orElseThrow(() -> new Failure(Failure.Code.NOT_FOUND, "Admission order not found"));
        }
    }
}
