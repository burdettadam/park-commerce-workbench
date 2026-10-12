package dev.parkcommerce.core;

import java.time.*;
import java.util.*;
import java.util.function.Function;

/** TEST FIXTURES ONLY: not wired into any Spring service and not evidence of database isolation. */
public final class MemoryStores {
    private MemoryStores() {}
    public static final class MutableClock extends Clock {
        private Instant now;
        public MutableClock(Instant now) { this.now = now; }
        public void advance(Duration duration) { now = now.plus(duration); }
        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return Clock.fixed(now, zone); }
        @Override public Instant instant() { return now; }
    }
    public static final class CatalogStore implements CatalogEditorial.Store {
        private final Map<String, NavigableMap<Long, CatalogEditorial.Publication>> data = new TreeMap<>();
        private final Map<String, NavigableMap<Long, CatalogEditorial.Draft>> drafts = new TreeMap<>();
        @Override public synchronized Optional<Catalog.Product> latest(String id) {
            var versions = data.get(id); return versions == null ? Optional.empty() : Optional.of(versions.lastEntry().getValue().product());
        }
        @Override public synchronized Optional<Catalog.Product> revision(String id, long revision) {
            return publication(id, revision).map(CatalogEditorial.Publication::product);
        }
        @Override public synchronized List<Catalog.Product> latestProducts() {
            return data.values().stream().map(v -> v.lastEntry().getValue().product()).toList();
        }
        @Override public synchronized void insert(Catalog.Product product) {
            // Legacy/bootstrap fixture route, not the production employee HTTP path.
            insertPublication(CatalogEditorial.Publication.legacy(product));
        }
        @Override public synchronized void insertPublication(CatalogEditorial.Publication publication) {
            var product = publication.product(); var versions = data.computeIfAbsent(product.id(), k -> new TreeMap<>());
            if (versions.putIfAbsent(product.revision(), publication) != null)
                throw new Failure(Failure.Code.CONFLICT, "Duplicate revision");
        }
        @Override public synchronized Optional<CatalogEditorial.Publication> publication(String id, long revision) {
            return Optional.ofNullable(data.getOrDefault(id, new TreeMap<>()).get(revision));
        }
        @Override public synchronized List<CatalogEditorial.Publication> publications(String id, long snapshot, long before, int limit) {
            return data.getOrDefault(id, new TreeMap<>()).descendingMap().values().stream()
                .filter(p -> p.product().revision() <= snapshot && (before == 0 || p.product().revision() < before))
                .limit(limit).toList();
        }
        @Override public synchronized Optional<CatalogEditorial.Draft> latestDraft(String id) {
            var versions = drafts.get(id); return versions == null ? Optional.empty() : Optional.of(versions.lastEntry().getValue());
        }
        @Override public synchronized Optional<CatalogEditorial.Draft> draft(String id, long version) {
            return Optional.ofNullable(drafts.getOrDefault(id, new TreeMap<>()).get(version));
        }
        @Override public synchronized void insertDraft(CatalogEditorial.Draft draft) {
            var versions = drafts.computeIfAbsent(draft.productId(), k -> new TreeMap<>());
            if (versions.putIfAbsent(draft.version(), draft) != null)
                throw new Failure(Failure.Code.CONFLICT, "Duplicate draft version");
        }
        public synchronized CatalogStore restartCopy() {
            var restored = new CatalogStore();
            data.values().forEach(v -> v.values().forEach(restored::insertPublication));
            drafts.values().forEach(v -> v.values().forEach(restored::insertDraft));
            return restored;
        }
    }
    public static final class PricingStore implements Pricing.Store {
        private final Map<String, NavigableMap<Long, Pricing.PriceBook>> books = new HashMap<>();
        private final Map<UUID, Pricing.Quote> quotes = new HashMap<>();
        @Override public synchronized Optional<Pricing.PriceBook> latest(String id) {
            var versions = books.get(id); return versions == null ? Optional.empty() : Optional.of(versions.lastEntry().getValue());
        }
        @Override public synchronized void insert(Pricing.PriceBook book) {
            var versions = books.computeIfAbsent(book.productId(), k -> new TreeMap<>());
            if (versions.putIfAbsent(book.version(), book) != null) throw new Failure(Failure.Code.CONFLICT, "Duplicate version");
        }
        @Override public synchronized void saveQuote(Pricing.Quote quote) { quotes.put(quote.id(), quote); }
        @Override public synchronized Optional<Pricing.Quote> quote(UUID id) { return Optional.ofNullable(quotes.get(id)); }
    }
    public static final class BookingStore implements Booking.Store {
        private final Map<String, Booking.Ledger> ledgers = new HashMap<>();
        @Override public synchronized void create(Booking.Event event) {
            if (ledgers.putIfAbsent(event.id(), new Booking.Ledger(event, List.of(), List.of())) != null)
                throw new Failure(Failure.Code.CONFLICT, "Duplicate event");
        }
        @Override public synchronized Optional<Booking.Hold> hold(UUID id) {
            return ledgers.values().stream().flatMap(l -> l.holds().stream()).filter(h -> h.id().equals(id)).findFirst();
        }
        @Override public synchronized Optional<Booking.Order> order(UUID id) {
            return ledgers.values().stream().flatMap(l -> l.orders().stream()).filter(o -> o.id().equals(id)).findFirst();
        }
        @Override public synchronized Optional<Booking.Order> orderByKey(String owner, String key) {
            return ledgers.values().stream().flatMap(l -> l.orders().stream()).filter(o -> o.owner().equals(owner) && o.idempotencyKey().equals(key)).findFirst();
        }
        @Override public synchronized <T> T transact(String eventId, Function<Booking.Ledger,T> operation) {
            var original = ledgers.get(eventId);
            if (original == null) throw new Failure(Failure.Code.NOT_FOUND, "Event not found");
            var copy = original.copy();
            T result = operation.apply(copy);
            for (var hold : copy.holds()) {
                var collision = ledgers.values().stream().flatMap(l -> l.holds().stream())
                    .filter(h -> h.quoteId().equals(hold.quoteId()) && !h.id().equals(hold.id())).findFirst();
                if (collision.isPresent()) throw new Failure(Failure.Code.CONFLICT, "Quote uniqueness");
            }
            for (var order : copy.orders()) {
                var collision = orderByKey(order.owner(), order.idempotencyKey());
                if (collision.isPresent() && !collision.get().id().equals(order.id())) throw new Failure(Failure.Code.CONFLICT, "Owner/key uniqueness");
            }
            ledgers.put(eventId, copy); // Failed operations never publish partial state.
            return result;
        }
        public synchronized BookingStore restartCopy() {
            BookingStore restored = new BookingStore();
            ledgers.forEach((key, ledger) -> restored.ledgers.put(key, ledger.copy()));
            return restored;
        }
    }
}
