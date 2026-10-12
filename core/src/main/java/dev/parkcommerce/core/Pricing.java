package dev.parkcommerce.core;

import java.time.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import static dev.parkcommerce.core.Catalog.*;

public final class Pricing {
    private Pricing() {}
    public record Rate(Variant variant, LocalDate from, LocalDate through, Money unitPrice) {
        public Rate {
            Failure.require(variant != null && from != null && through != null && unitPrice != null, "A complete dated rate is required");
            Failure.require(!through.isBefore(from), "Rate end cannot precede rate start");
            Failure.require(unitPrice.cents() <= 1_000_000, "Unit price cannot exceed USD 10,000");
        }
        public boolean covers(LocalDate day) { return !day.isBefore(from) && !day.isAfter(through); }
        boolean overlaps(Rate r) { return variant == r.variant && !through.isBefore(r.from) && !r.through.isBefore(from); }
    }
    public record Offer(String code, int minimumTickets, int discountBasisPoints) {
        public Offer {
            Failure.id(code, "Offer code");
            Failure.require(minimumTickets >= 1 && minimumTickets <= 20, "Offer threshold must be 1-20 tickets");
            Failure.require(discountBasisPoints >= 0 && discountBasisPoints <= 10_000, "Offer discount must be 0-100 percent");
        }
    }
    public record PriceBook(String productId, long version, long catalogRevision, List<Rate> rates, Offer offer) {
        public PriceBook {
            Failure.id(productId, "Product ID");
            Failure.require(version > 0 && catalogRevision > 0, "Price and catalog versions must be positive");
            Failure.require(rates != null && !rates.isEmpty() && rates.size() <= 100 && rates.stream().noneMatch(Objects::isNull), "Provide 1-100 rates");
            rates = List.copyOf(rates);
            for (int i=0; i<rates.size(); i++) for (int j=i+1; j<rates.size(); j++)
                Failure.require(!rates.get(i).overlaps(rates.get(j)), "Overlapping date ranges for " + rates.get(i).variant());
        }
        Rate rate(Variant variant, LocalDate date) {
            return rates.stream().filter(r -> r.variant() == variant && r.covers(date)).findFirst()
                .orElseThrow(() -> new Failure(Failure.Code.NOT_FOUND, "No " + variant + " price for this visit date"));
        }
    }
    public record Request(String productId, LocalDate visitDate, int adults, int children) {
        public Request {
            Failure.id(productId, "Product ID");
            Failure.require(visitDate != null, "Visit date is required");
            Failure.require(visitDate.getYear() >= 1 && visitDate.getYear() <= 9999, "Visit date year must be 0001-9999");
            Failure.require(adults >= 0 && children >= 0 && adults <= 20 && children <= 20 && adults + children >= 1 && adults + children <= 20,
                "A party must have between 1 and 20 tickets");
        }
        public int quantity() { return adults + children; }
    }
    public record Line(Variant variant, int quantity, Money unitPrice, Money subtotal, Money discount, Money total) {}
    /** A caller-supplied what-if input; it is not a command to publish a price. */
    public record PreviewRequest(PriceBook candidate, Request request) {
        public PreviewRequest {
            Failure.require(candidate != null && request != null, "Preview candidate and party request are required");
            Failure.require(candidate.productId().equals(request.productId()), "Preview product must match the candidate price book");
        }
    }
    /** Describes the source of the input, not a claim about current database state. */
    public enum PreviewMode { UNPUBLISHED_INPUT }
    /** No quote ID, owner or expiry: this value cannot be submitted to booking. */
    public record Preview(PreviewMode mode, String candidateId, PriceBook candidate, Request request,
            String productName, Kind kind, List<Line> lines, String appliedOffer,
            Money subtotal, Money discount, Money total) {
        public Preview { lines = List.copyOf(lines); }
    }
    private record Calculation(Product product, List<Line> lines, String appliedOffer,
            Money subtotal, Money discount, Money total) {}

    public record Quote(UUID id, String owner, String productId, String productName, Kind kind, long catalogRevision,
            LocalDate visitDate, long pricingVersion, int quantity, List<Line> lines, String appliedOffer,
            Money subtotal, Money discount, Money total, Instant createdAt, Instant expiresAt) {
        public Quote { lines = List.copyOf(lines); }
        public boolean validAt(Instant instant) { return instant.isBefore(expiresAt); }
    }
    /** Only describes expiry as observed now; not inventory, payment, or unused-quote state. */
    public enum QuoteState { UNEXPIRED, EXPIRED }
    public record QuoteStatus(Quote quote, QuoteState state, Instant evaluatedAt, String parkTimeZone) {}

    public interface Store {
        Optional<PriceBook> latest(String productId);
        void insert(PriceBook book);
        void saveQuote(Quote quote);
        Optional<Quote> quote(UUID id);
    }
    public interface Client { Quote quote(UUID id); }
    public static final class Service {
        public static final Duration QUOTE_TTL = Duration.ofMinutes(15);
        private final Store store;
        private final Catalog.Client catalog;
        private final Clock clock;
        private final ZoneId parkZone;
        public Service(Store store, Catalog.Client catalog, Clock clock, ZoneId parkZone) {
            this.store = Objects.requireNonNull(store); this.catalog = Objects.requireNonNull(catalog);
            this.clock = Objects.requireNonNull(clock); this.parkZone = Objects.requireNonNull(parkZone);
        }
        public PriceBook publish(PriceBook book) {
            Failure.require(book != null, "A price book is required");
            productFor(book);
            long next = store.latest(book.productId()).map(b -> b.version() + 1).orElse(1L);
            if (book.version() != next) throw new Failure(Failure.Code.CONFLICT, "Publish the next pricing version: " + next);
            store.insert(book); // Unique (productId, version) handles competing publishers.
            return book;
        }

        /** No price-store reads/writes, quote identity, booking access or publication. */
        public Preview preview(PreviewRequest input) {
            Failure.require(input != null, "A preview request is required");
            checkDate(input.request(), clock.instant());
            Calculation c = calculate(input.candidate(), input.request());
            return new Preview(PreviewMode.UNPUBLISHED_INPUT, candidateId(input.candidate()), input.candidate(), input.request(),
                c.product().name(), c.product().kind(), c.lines(), c.appliedOffer(), c.subtotal(), c.discount(), c.total());
        }

        public Quote create(Request request, String owner) {
            Failure.require(request != null, "A quote request is required");
            Failure.owner(owner);
            // Freeze this request's evaluation time before reading either dependency.
            // A slow lookup cannot silently extend the agreed 15-minute lifetime.
            Instant now = clock.instant();
            checkDate(request, now);
            PriceBook book = store.latest(request.productId()).orElseThrow(() -> new Failure(Failure.Code.NOT_FOUND, "No published price book"));
            Calculation c = calculate(book, request);
            Product product = c.product();
            Quote quote = new Quote(UUID.randomUUID(), owner, product.id(), product.name(), product.kind(), product.revision(),
                request.visitDate(), book.version(), request.quantity(), c.lines(), c.appliedOffer(),
                c.subtotal(), c.discount(), c.total(), now, now.plus(QUOTE_TTL));
            store.saveQuote(quote);
            return quote;
        }

        /** Preview and quote issuance share all validation and monetary operations. */
        private Calculation calculate(PriceBook book, Request request) {
            Failure.require(book.productId().equals(request.productId()), "Price book and request products must match");
            Product product = productFor(book);
            int bps = book.offer() != null && request.quantity() >= book.offer().minimumTickets() ? book.offer().discountBasisPoints() : 0;
            List<Line> lines = new ArrayList<>();
            line(lines, product, book, Variant.ADULT, request.adults(), request.visitDate(), bps);
            line(lines, product, book, Variant.CHILD, request.children(), request.visitDate(), bps);
            Money subtotal = new Money(0), discount = new Money(0);
            for (Line line : lines) { subtotal = subtotal.plus(line.subtotal()); discount = discount.plus(line.discount()); }
            return new Calculation(product, List.copyOf(lines), bps > 0 ? book.offer().code() : null,
                subtotal, discount, subtotal.minus(discount));
        }

        private void checkDate(Request request, Instant evaluatedAt) {
            Failure.require(!request.visitDate().isBefore(evaluatedAt.atZone(parkZone).toLocalDate()),
                "Visit date is in the past in the park time zone");
        }

        private Product productFor(PriceBook book) {
            Product product = catalog.revision(book.productId(), book.catalogRevision());
            if (product == null || !product.id().equals(book.productId()) || product.revision() != book.catalogRevision())
                throw new Failure(Failure.Code.DEPENDENCY_UNAVAILABLE, "Catalog did not return the requested product revision");
            for (Rate rate : book.rates())
                Failure.require(product.variants().contains(rate.variant()), "Price variant is not offered by this product revision");
            return product;
        }

        private void line(List<Line> lines, Product product, PriceBook book, Variant variant, int quantity, LocalDate day, int bps) {
            if (quantity == 0) return;
            Failure.require(product.variants().contains(variant), "Variant not available: " + variant);
            Rate rate = book.rate(variant, day);
            Money subtotal = rate.unitPrice().times(quantity), discount = subtotal.discount(bps);
            lines.add(new Line(variant, quantity, rate.unitPrice(), subtotal, discount, subtotal.minus(discount)));
        }
        public Quote get(UUID id, String owner) {
            Quote quote = internal(id);
            if (!quote.owner().equals(Failure.owner(owner))) throw new Failure(Failure.Code.NOT_FOUND, "Quote not found");
            return quote;
        }
        /** Historical reads stay compatible; observing expiry never rewrites a quote. */
        public QuoteStatus status(UUID id, String owner) {
            Quote quote = get(id, owner); // Authorization before revealing status or sampling time.
            Instant evaluatedAt = clock.instant();
            return new QuoteStatus(quote, quote.validAt(evaluatedAt) ? QuoteState.UNEXPIRED : QuoteState.EXPIRED,
                evaluatedAt, parkZone.getId());
        }
        public Quote internal(UUID id) {
            Failure.require(id != null, "Quote ID is required");
            return store.quote(id).orElseThrow(() -> new Failure(Failure.Code.NOT_FOUND, "Quote not found"));
        }
    }
    /**
     * Versioned, length-prefixed UTF-8/big-endian encoding of the full candidate.
     * Rate list order is significant. This identifies inputs, not approval or a quote.
     */
    private static String candidateId(PriceBook book) {
        try {
            var bytes = new ByteArrayOutputStream();
            try (var out = new DataOutputStream(bytes)) {
                text(out, "park-commerce-price-book-v1");
                text(out, book.productId()); out.writeLong(book.version()); out.writeLong(book.catalogRevision());
                out.writeInt(book.rates().size());
                for (Rate rate : book.rates()) {
                    text(out, rate.variant().name()); out.writeLong(rate.from().toEpochDay());
                    out.writeLong(rate.through().toEpochDay()); out.writeLong(rate.unitPrice().cents());
                }
                out.writeBoolean(book.offer() != null);
                if (book.offer() != null) {
                    text(out, book.offer().code()); out.writeInt(book.offer().minimumTickets()); out.writeInt(book.offer().discountBasisPoints());
                }
            }
            return "sha256:" + HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes.toByteArray()));
        } catch (IOException | NoSuchAlgorithmException error) {
            throw new IllegalStateException("Cannot identify the price-book candidate", error);
        }
    }
    private static void text(DataOutputStream out, String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        out.writeInt(bytes.length); out.write(bytes);
    }

}
