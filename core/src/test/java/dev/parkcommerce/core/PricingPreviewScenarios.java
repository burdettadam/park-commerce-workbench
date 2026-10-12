package dev.parkcommerce.core;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import static dev.parkcommerce.core.Catalog.*;
import static dev.parkcommerce.core.CoreScenarios.*;
import static dev.parkcommerce.core.Failure.Code.*;

/** In-memory domain contract tests, also selected by the existing JUnit bridge. */
public final class PricingPreviewScenarios {
    private PricingPreviewScenarios() {}

    private static Pricing.Request request(ScenarioFixture f, int adults, int children) {
        return new Pricing.Request("lantern-evening", f.visit, adults, children);
    }
    private static Pricing.PriceBook candidate(ScenarioFixture f) {
        return f.book("lantern-evening", 2, "100.00", "70.00", new Pricing.Offer("family-four", 4, 1000));
    }
    private static Pricing.Preview preview(ScenarioFixture f, Pricing.PriceBook book, int adults, int children) {
        return f.pricing.preview(new Pricing.PreviewRequest(book, request(f, adults, children)));
    }
    private static void totals(Pricing.Preview p, long subtotal, long discount, long total) {
        equal(subtotal, p.subtotal().cents()); equal(discount, p.discount().cents()); equal(total, p.total().cents());
    }
    public static List<Case> cases() {
        var cases = new ArrayList<Case>();
        cases.add(new Case("Preview: family fixture is independently $340 - $34 = $306", () -> {
            var f = new ScenarioFixture(); var book = candidate(f); var p = preview(f, book, 2, 2);
            totals(p, 34000, 3400, 30600);
            equal(Pricing.PreviewMode.UNPUBLISHED_INPUT, p.mode()); equal(book, p.candidate());
            equal(request(f, 2, 2), p.request()); equal("family-four", p.appliedOffer());
            equal("Lantern Evening", p.productName()); equal(Kind.EVENT, p.kind());
            equal(18000L, p.lines().get(0).total().cents()); equal(12600L, p.lines().get(1).total().cents());
        }));
        cases.add(new Case("Preview: below offer threshold applies no discount", () -> {
            var f = new ScenarioFixture(); var p = preview(f, candidate(f), 2, 1);
            totals(p, 27000, 0, 27000); equal(null, p.appliedOffer());
        }));
        cases.add(new Case("Preview: zero-percent offer is not reported as applied", () -> {
            var f = new ScenarioFixture(); var p = preview(f, f.book("lantern-evening", 2, "100.00", "70.00", new Pricing.Offer("zero", 1, 0)), 1, 0);
            totals(p, 10000, 0, 10000); equal(null, p.appliedOffer());
        }));
        cases.add(new Case("Preview: 100 percent discount never produces negative money", () -> {
            var f = new ScenarioFixture(); var p = preview(f, f.book("lantern-evening", 2, "100.00", "70.00", new Pricing.Offer("free", 1, 10000)), 2, 2);
            totals(p, 34000, 34000, 0);
        }));
        cases.add(new Case("Preview: rounds each variant subtotal half-up", () -> {
            var f = new ScenarioFixture(); var b = f.book("lantern-evening", 2, "0.05", "0.05", new Pricing.Offer("tiny", 1, 1000));
            totals(preview(f, b, 1, 1), 10, 2, 8); // 0.5 cent per line rounds to 1 cent per line.
            totals(preview(f, b, 2, 0), 10, 1, 9); // One 10-cent subtotal discounts by 1 cent, not 2.
        }));
        cases.add(new Case("Preview: maximum 20-ticket party computes exactly", () -> {
            var f = new ScenarioFixture(); totals(preview(f, candidate(f), 10, 10), 170000, 17000, 153000);
        }));
        cases.add(new Case("Preview: publication and quote reuse the same monetary lines", () -> {
            var f = new ScenarioFixture(); var b = candidate(f); var p = preview(f, b, 2, 2);
            f.pricing.publish(b); var q = f.pricing.create(p.request(), "guest");
            equal(p.lines(), q.lines()); equal(p.total(), q.total()); equal(p.discount(), q.discount());
            equal(p.subtotal(), q.subtotal()); equal(p.appliedOffer(), q.appliedOffer());
            equal(p.productName(), q.productName()); equal(p.kind(), q.kind());
            equal(p.candidate().version(), q.pricingVersion()); equal(p.candidate().catalogRevision(), q.catalogRevision());
            check(q.id() != null); equal("guest", q.owner()); equal(f.clock.instant().plus(Pricing.Service.QUOTE_TTL), q.expiresAt());
        }));
        cases.add(new Case("Preview: never reads or writes the pricing store", () -> {
            var f = new ScenarioFixture(); var service = new Pricing.Service(new ForbiddenStore(), f.catalog::revision, f.clock, ZoneId.of("America/New_York"));
            totals(service.preview(new Pricing.PreviewRequest(candidate(f), request(f, 2, 2))), 34000, 3400, 30600);
        }));
        cases.add(new Case("Preview: works before any price has been published", () -> {
            var f = new ScenarioFixture(); var empty = new MemoryStores.PricingStore();
            var service = new Pricing.Service(empty, f.catalog::revision, f.clock, ZoneId.of("America/New_York"));
            totals(service.preview(new Pricing.PreviewRequest(candidate(f), request(f, 2, 2))), 34000, 3400, 30600);
            check(empty.latest("lantern-evening").isEmpty());
        }));
        cases.add(new Case("Preview: keeps published prices, prior quotes, orders and capacity unchanged", () -> {
            var f = new ScenarioFixture(); var original = f.quote("guest", 1, 0); var h = f.booking.reserve("october-lanterns", original.id(), "guest");
            var order = f.booking.confirm(h.id(), "guest", "original"); var inventory = f.booking.availability("october-lanterns");
            var published = f.prices.latest("lantern-evening");
            for (int i = 0; i < 5; i++) preview(f, f.book("lantern-evening", 2, "125.00", "85.00", null), 2, 2);
            equal(published, f.prices.latest("lantern-evening")); equal(original, f.pricing.get(original.id(), "guest"));
            equal(order, f.booking.get(order.id(), "guest")); equal(inventory, f.booking.availability("october-lanterns"));
        }));
        cases.add(new Case("Preview: response has no purchasable identity, owner or expiration", () -> {
            var names = Arrays.stream(Pricing.Preview.class.getRecordComponents()).map(c -> c.getName()).toList();
            for (String name : List.of("id", "quoteId", "owner", "expiresAt", "createdAt")) check(!names.contains(name));
            check(!Pricing.Quote.class.isAssignableFrom(Pricing.Preview.class));
        }));
        cases.add(new Case("Preview: candidate content identity is deterministic", () -> {
            var f = new ScenarioFixture(); var first = preview(f, candidate(f), 2, 2);
            f.clock.advance(Duration.ofMinutes(1)); var second = preview(f, candidate(f), 2, 2);
            equal(first, second); check(first.candidateId().matches("sha256:[0-9a-f]{64}"));
        }));
        cases.add(new Case("Preview: identity includes candidate amount, version, offer, dates and catalog reference", () -> {
            var f = new ScenarioFixture(); var original = candidate(f); String hash = preview(f, original, 2, 2).candidateId();
            var changed = new ArrayList<Pricing.PriceBook>();
            changed.add(f.book("lantern-evening", 2, "100.01", "70.00", original.offer()));
            changed.add(f.book("lantern-evening", 3, "100.00", "70.00", original.offer()));
            changed.add(f.book("lantern-evening", 2, "100.00", "70.00", new Pricing.Offer("other", 4, 1000)));
            changed.add(f.book("lantern-evening", 2, "100.00", "70.00", new Pricing.Offer("family-four", 3, 1000)));
            changed.add(f.book("lantern-evening", 2, "100.00", "70.00", new Pricing.Offer("family-four", 4, 2000)));
            changed.add(f.book("lantern-evening", 2, "100.00", "70.00", null));
            changed.add(new Pricing.PriceBook("lantern-evening", 2, 1, List.of(
                new Pricing.Rate(Variant.ADULT, f.visit.minusDays(1), f.visit, Money.usd("100.00")),
                new Pricing.Rate(Variant.CHILD, f.visit, f.visit.plusDays(1), Money.usd("70.00"))), original.offer()));
            f.catalog.publish(new Product("lantern-evening", 2, "Renamed Event", Kind.EVENT, EnumSet.allOf(Variant.class)));
            changed.add(new Pricing.PriceBook("lantern-evening", 2, 2, original.rates(), original.offer()));
            for (var book : changed) check(!hash.equals(preview(f, book, 2, 2).candidateId()));
        }));
        cases.add(new Case("Preview: candidate identity is separate from party inputs", () -> {
            var f = new ScenarioFixture(); var a = preview(f, candidate(f), 2, 2); var b = preview(f, candidate(f), 1, 0);
            equal(a.candidateId(), b.candidateId()); check(!a.request().equals(b.request())); check(!a.total().equals(b.total()));
        }));
        cases.add(new Case("Preview: stale pricing version can be inspected but not republished", () -> {
            var f = new ScenarioFixture(); var stale = f.book("lantern-evening", 1, "125.00", "85.00", null);
            totals(preview(f, stale, 1, 0), 12500, 0, 12500); failure(CONFLICT, () -> f.pricing.publish(stale));
            equal(10000L, f.quote("guest", 1, 0).total().cents());
        }));
        cases.add(new Case("Preview: rejects mismatched product request", () -> {
            var f = new ScenarioFixture(); failure(INVALID_INPUT, () -> f.pricing.preview(new Pricing.PreviewRequest(candidate(f), new Pricing.Request("park-day", f.visit, 1, 0))));
        }));
        cases.add(new Case("Preview: rejects null candidate, request or envelope", () -> {
            var f = new ScenarioFixture(); failure(INVALID_INPUT, () -> new Pricing.PreviewRequest(null, request(f, 1, 0)));
            failure(INVALID_INPUT, () -> new Pricing.PreviewRequest(candidate(f), null)); failure(INVALID_INPUT, () -> f.pricing.preview(null));
        }));
        cases.add(new Case("Preview: rejects overlapping candidate dates", () -> {
            var f = new ScenarioFixture(); failure(INVALID_INPUT, () -> new Pricing.PriceBook("lantern-evening", 2, 1, List.of(
                new Pricing.Rate(Variant.ADULT, f.visit, f.visit.plusDays(1), Money.usd("10")),
                new Pricing.Rate(Variant.ADULT, f.visit.plusDays(1), f.visit.plusDays(2), Money.usd("20"))), null));
        }));
        cases.add(new Case("Preview: inclusive start and end date rates apply", () -> {
            var f = new ScenarioFixture(); var b = new Pricing.PriceBook("lantern-evening", 2, 1, List.of(new Pricing.Rate(Variant.ADULT, f.visit, f.visit.plusDays(1), Money.usd("10"))), null);
            totals(preview(f, b, 1, 0), 1000, 0, 1000);
            totals(f.pricing.preview(new Pricing.PreviewRequest(b, new Pricing.Request("lantern-evening", f.visit.plusDays(1), 1, 0))), 1000, 0, 1000);
        }));
        cases.add(new Case("Preview: no uncovered-date or missing-variant best guess", () -> {
            var f = new ScenarioFixture(); failure(NOT_FOUND, () -> f.pricing.preview(new Pricing.PreviewRequest(candidate(f), new Pricing.Request("lantern-evening", f.visit.plusDays(1), 1, 0))));
            var adultOnly = new Pricing.PriceBook("lantern-evening", 2, 1, List.of(new Pricing.Rate(Variant.ADULT, f.visit, f.visit, Money.usd("10"))), null);
            failure(NOT_FOUND, () -> preview(f, adultOnly, 1, 1));
        }));
        cases.add(new Case("Preview: rejects out-of-range and empty parties", () -> {
            var f = new ScenarioFixture(); failure(INVALID_INPUT, () -> request(f, -1, 1)); failure(INVALID_INPUT, () -> request(f, 0, 0));
            failure(INVALID_INPUT, () -> request(f, 20, 1)); failure(INVALID_INPUT, () -> request(f, Integer.MAX_VALUE, 1));
        }));
        cases.add(new Case("Preview: validates unused candidate variants against pinned catalog", () -> {
            var f = new ScenarioFixture(); f.catalog.publish(new Product("lantern-evening", 2, "Adults Only", Kind.EVENT, Set.of(Variant.ADULT)));
            var bad = new Pricing.PriceBook("lantern-evening", 2, 2, candidate(f).rates(), null);
            failure(INVALID_INPUT, () -> preview(f, bad, 1, 0));
        }));
        cases.add(new Case("Preview: park timezone determines the past-date boundary", () -> {
            var f = new ScenarioFixture(); var before = Clock.fixed(Instant.parse("2026-11-01T03:59:59Z"), ZoneOffset.UTC);
            var first = new Pricing.Service(new ForbiddenStore(), f.catalog::revision, before, ZoneId.of("America/New_York"));
            totals(first.preview(new Pricing.PreviewRequest(candidate(f), request(f, 1, 0))), 10000, 0, 10000);
            var after = new Pricing.Service(new ForbiddenStore(), f.catalog::revision, Clock.offset(before, Duration.ofSeconds(1)), ZoneId.of("America/New_York"));
            failure(INVALID_INPUT, () -> after.preview(new Pricing.PreviewRequest(candidate(f), request(f, 1, 0))));
        }));
        cases.add(new Case("Preview: rejects missing or mismatched upstream catalog snapshots", () -> {
            var f = new ScenarioFixture(); var missing = new Pricing.Service(new ForbiddenStore(), (id, rev) -> null, f.clock, ZoneOffset.UTC);
            failure(DEPENDENCY_UNAVAILABLE, () -> missing.preview(new Pricing.PreviewRequest(candidate(f), request(f, 1, 0))));
            var wrong = new Pricing.Service(new ForbiddenStore(), (id, rev) -> f.catalog.latest("park-day"), f.clock, ZoneOffset.UTC);
            failure(DEPENDENCY_UNAVAILABLE, () -> wrong.preview(new Pricing.PreviewRequest(candidate(f), request(f, 1, 0))));
        }));
        cases.add(new Case("Preview: pinned catalog revision is not replaced by latest", () -> {
            var f = new ScenarioFixture(); f.catalog.publish(new Product("lantern-evening", 2, "New name", Kind.EVENT, Set.of(Variant.ADULT)));
            var p = preview(f, candidate(f), 2, 2); equal(1L, p.candidate().catalogRevision()); equal("Lantern Evening", p.productName());
        }));
        cases.add(new Case("Preview: published quote also fails closed on a mismatched catalog response", () -> {
            var f = new ScenarioFixture(); var service = new Pricing.Service(f.prices, (id, rev) -> f.catalog.latest("park-day"), f.clock, ZoneOffset.UTC);
            failure(DEPENDENCY_UNAVAILABLE, () -> service.create(request(f, 1, 0), "guest"));
        }));
        cases.add(new Case("Preview: candidate and line collections are immutable", () -> {
            var f = new ScenarioFixture(); var p = preview(f, candidate(f), 1, 0);
            try { p.lines().clear(); throw new AssertionError("Mutable preview lines"); } catch (UnsupportedOperationException expected) { /* expected */ }
            try { p.candidate().rates().clear(); throw new AssertionError("Mutable rates"); } catch (UnsupportedOperationException expected) { /* expected */ }
        }));
        cases.add(new Case("Preview: concurrent publication cannot silently replace submitted candidate", PricingPreviewScenarios::concurrentPublication));
        cases.add(new Case("Preview: fingerprint matches independent binary-encoding fixture", () -> {
            var f = new ScenarioFixture(); equal("sha256:0cea663b21d35d72cc9ea7bf166890e2195a5fff54fe5649cb7a08ff49b69e3f", preview(f, candidate(f), 2, 2).candidateId());
        }));
        cases.add(new Case("Preview: rate list order is explicitly part of exact input identity", () -> {
            var f = new ScenarioFixture(); var b = candidate(f); var reversed = new ArrayList<>(b.rates()); Collections.reverse(reversed);
            var a = preview(f, b, 2, 2); var c = preview(f, new Pricing.PriceBook(b.productId(), b.version(), b.catalogRevision(), reversed, b.offer()), 2, 2);
            equal(a.lines(), c.lines()); check(!a.candidateId().equals(c.candidateId()));
        }));
        return List.copyOf(cases);
    }
    private static void concurrentPublication() throws Exception {
        var f = new ScenarioFixture(); var oldQuote = f.quote("guest", 1, 0); var book = candidate(f);
        var entered = new CountDownLatch(1); var release = new CountDownLatch(1);
        var service = new Pricing.Service(new ForbiddenStore(), (id, rev) -> {
            entered.countDown();
            try { if (!release.await(5, TimeUnit.SECONDS)) throw new AssertionError("Publication barrier timed out"); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new AssertionError(e); }
            return f.catalog.revision(id, rev);
        }, f.clock, ZoneId.of("America/New_York"));
        try (var pool = Executors.newSingleThreadExecutor()) {
            var future = pool.submit(() -> service.preview(new Pricing.PreviewRequest(book, request(f, 2, 2))));
            try {
                check(entered.await(5, TimeUnit.SECONDS));
                f.pricing.publish(f.book("lantern-evening", 2, "125.00", "85.00", null));
            } finally { release.countDown(); }
            var result = future.get(5, TimeUnit.SECONDS); equal(book, result.candidate()); totals(result, 34000, 3400, 30600);
            equal(oldQuote, f.pricing.get(oldQuote.id(), "guest")); equal(12500L, f.quote("guest", 1, 0).total().cents());
            failure(CONFLICT, () -> f.pricing.publish(book));
        }
    }
    private static final class ForbiddenStore implements Pricing.Store {
        @Override public Optional<Pricing.PriceBook> latest(String id) { throw new AssertionError("Preview read published price state"); }
        @Override public void insert(Pricing.PriceBook book) { throw new AssertionError("Preview published a price"); }
        @Override public void saveQuote(Pricing.Quote quote) { throw new AssertionError("Preview issued a quote"); }
        @Override public Optional<Pricing.Quote> quote(UUID id) { throw new AssertionError("Preview read a quote"); }
    }
}
