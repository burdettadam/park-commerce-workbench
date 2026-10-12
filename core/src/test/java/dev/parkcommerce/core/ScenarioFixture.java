package dev.parkcommerce.core;

import java.time.*;
import java.util.*;
import static dev.parkcommerce.core.Catalog.*;

public final class ScenarioFixture {
    public final MemoryStores.MutableClock clock = new MemoryStores.MutableClock(Instant.parse("2026-10-11T18:00:00Z"));
    public final LocalDate visit = LocalDate.of(2026, 10, 31);
    public final MemoryStores.CatalogStore catalogs = new MemoryStores.CatalogStore();
    public final MemoryStores.PricingStore prices = new MemoryStores.PricingStore();
    public final MemoryStores.BookingStore bookings = new MemoryStores.BookingStore();
    public final Catalog.Service catalog = new Catalog.Service(catalogs);
    public final Pricing.Service pricing = new Pricing.Service(prices, catalog::revision, clock, ZoneId.of("America/New_York"));
    public final Booking.Service booking = new Booking.Service(bookings, pricing::internal, clock);
    public ScenarioFixture() {
        catalog.publish(new Product("lantern-evening", 1, "Lantern Evening", Kind.EVENT, EnumSet.allOf(Variant.class)));
        catalog.publish(new Product("park-day", 1, "Adventure Park Admission", Kind.ADMISSION, EnumSet.allOf(Variant.class)));
        pricing.publish(book("lantern-evening", 1, "100.00", "70.00", new Pricing.Offer("family-four",4,1000)));
        pricing.publish(book("park-day", 1, "80.00", "50.00", null));
        booking.create(new Booking.Event("october-lanterns", "lantern-evening", visit, 3));
    }
    public Pricing.PriceBook book(String id,long version,String adult,String child,Pricing.Offer offer) {
        return new Pricing.PriceBook(id, version, 1, List.of(
            new Pricing.Rate(Variant.ADULT,visit,visit,Money.usd(adult)),
            new Pricing.Rate(Variant.CHILD,visit,visit,Money.usd(child))),offer);
    }
    public Pricing.Quote quote(String owner,int adults,int children) {
        return pricing.create(new Pricing.Request("lantern-evening",visit,adults,children),owner);
    }
    public Booking.Hold hold(String owner) { return booking.reserve("october-lanterns",quote(owner,1,0).id(),owner); }
}
