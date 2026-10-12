package dev.parkcommerce.core;
public final class CoreDemo {
    public static void main(String[] args) {
        var f = new ScenarioFixture();
        var draft = f.book("lantern-evening", 2, "125.00", "85.00", new Pricing.Offer("family-four", 4, 1000));
        var preview = f.pricing.preview(new Pricing.PreviewRequest(draft, new Pricing.Request("lantern-evening", f.visit, 2, 2)));
        System.out.println("UNPUBLISHED WHAT-IF - not a purchasable quote or reservation.");
        System.out.printf("Family preview: $%s - $%s = $%s%n", preview.subtotal().amount(), preview.discount().amount(), preview.total().amount());
        System.out.println("Candidate: " + preview.candidateId());
        System.out.println("Published version after preview: " + f.prices.latest("lantern-evening").orElseThrow().version());
        var original = f.quote("guest", 1, 0);
        f.pricing.publish(f.book("lantern-evening", 2, "125.00", "85.00", null));
        var current = f.quote("guest",1,0);
        var hold = f.booking.reserve("october-lanterns", original.id(), "guest");
        var order = f.booking.confirm(hold.id(),"guest","demo-checkout");
        var replay = f.booking.confirm(hold.id(),"guest","demo-checkout");
        System.out.println("CORE-ONLY WALKTHROUGH - in-memory test fixtures, not a Spring/database deployment.");
        System.out.printf("Original quote: $%s, pricing version %d%n",original.total().amount(),original.pricingVersion());
        System.out.printf("New quote after publication: $%s, version %d%n",current.total().amount(),current.pricingVersion());
        System.out.printf("Confirmed original price: $%s%n",order.total().amount());
        System.out.println("Checkout replay returns same order: "+order.id().equals(replay.id()));
        System.out.println("Remaining event capacity: "+f.booking.availability("october-lanterns").available());
        var admissionStore = new MemoryAdmissionStore();
        var admission = new Admission.Service(admissionStore, f.pricing::internal, f.clock, java.time.ZoneId.of("America/New_York"));
        var admissionQuote = f.pricing.create(new Pricing.Request("park-day", f.visit, 2, 1), "guest");
        var admissionOrder = admission.confirm(admissionQuote.id(), "guest", "demo-admission");
        System.out.println("ADMISSION WALKTHROUGH - in-memory fixtures; no real payment or entry credential.");
        System.out.println("Admission amount: $" + admissionOrder.quote().total().amount());
        System.out.println("Admission entitlement groups: " + admissionOrder.entitlements().size());
        System.out.println("Admission ticket quantity: " + admissionOrder.entitlements().stream().mapToInt(Admission.Entitlement::quantity).sum());
        System.out.println("Event capacity after admission: " + f.booking.availability("october-lanterns").available());
        f.clock.advance(java.time.Duration.ofHours(1));
        var admissionReplay = new Admission.Service(admissionStore.restartCopy(), id -> {
            throw new AssertionError("Admission replay must not call pricing");
        }, f.clock, java.time.ZoneId.of("America/New_York"));
        System.out.println("Original admission recovered after expiry: " + admissionOrder.equals(admissionReplay.recover(admissionQuote.id(), "guest", "demo-admission").order()));
        System.out.println("Admission orders after replay: " + admissionStore.size());
        var reconstructed = new Booking.Service(f.bookings.restartCopy(), id -> {
            throw new AssertionError("Recovery must not depend on pricing");
        }, f.clock);
        var recovered = reconstructed.recover(hold.id(), "guest", "demo-checkout");
        System.out.println("RECOVERY WALKTHROUGH - reconstructed in-memory store, not a browser or database test.");
        System.out.println("Recovered state after quote expiry: " + recovered.hold().status());
        System.out.println("Recovered original order: " + order.equals(recovered.order()));
        System.out.println("Orders after recovery: " + f.bookings.transact("october-lanterns", ledger -> ledger.orders().size()));
        System.out.println("Pricing calls during recovery: 0");
        System.out.println("QUOTE CONTRACT WALKTHROUGH - core fixtures, not HTTP/GraphQL execution.");
        var status = f.pricing.status(original.id(), "guest");
        System.out.println("Original quote observed state: " + status.state());
        System.out.println("Observed park time zone: " + status.parkTimeZone());
        System.out.println("Expired quote retains original amount: $" + status.quote().total().amount());
        System.out.println("Confirmed order still recovers: " + order.equals(recovered.order()));
        var catalogs = new MemoryStores.CatalogStore();
        var editorial = new CatalogEditorial.Service(catalogs, f.clock);
        var publicCatalog = new Catalog.Service(catalogs);
        var content = new CatalogEditorial.Content("Reviewed evening", Catalog.Kind.EVENT, java.util.EnumSet.allOf(Catalog.Variant.class));
        editorial.saveDraft("reviewed-evening", new CatalogEditorial.SaveDraft(0, 0, content), "employee-a");
        editorial.saveDraft("reviewed-evening", new CatalogEditorial.SaveDraft(1, 0,
            new CatalogEditorial.Content("Later unpublished edit", Catalog.Kind.EVENT, content.variants())), "employee-a");
        System.out.println("CATALOG EDITORIAL WALKTHROUGH - in-memory fixtures, not Mongo/HTTP/UI evidence.");
        System.out.println("Guest-visible products before publication: " + publicCatalog.products().size());
        var published = editorial.publishDraft("reviewed-evening", new CatalogEditorial.PublishDraft(1, 0), "employee-b");
        System.out.println("Published exact draft: " + published.sourceDraftVersion());
        System.out.println("Guest sees: " + publicCatalog.latest("reviewed-evening").name());
        System.out.println("Later draft retained: " + editorial.latestDraft("reviewed-evening").content().name());
        System.out.println("Publication actor: " + published.audit().actor());
        // Controlled ASCII fixture values only; not an application JSON serializer.
        var audit = published.audit();
        System.out.printf(java.util.Locale.ROOT,
            "AUDIT_FIXTURE_JSON {\"actor\":\"%s\",\"operation\":\"%s\",\"productId\":\"%s\",\"previousVersion\":%d,\"version\":%d,\"occurredAt\":\"%s\"}%n",
            audit.actor(),audit.operation(),audit.productId(),audit.previousVersion(),audit.version(),audit.occurredAt());
        System.out.println("Retained published revisions: " + editorial.history("reviewed-evening", 10, null).items().size());
    }
}
