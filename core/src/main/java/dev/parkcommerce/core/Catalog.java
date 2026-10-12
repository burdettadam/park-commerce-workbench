package dev.parkcommerce.core;

import java.util.*;

public final class Catalog {
    private Catalog() {}
    public enum Kind { ADMISSION, EVENT }
    public enum Variant { ADULT, CHILD }
    public record Product(String id, long revision, String name, Kind kind, Set<Variant> variants) {
        public Product {
            Failure.id(id, "Product ID");
            Failure.require(revision > 0, "Product revision must be positive");
            Failure.require(name != null && !name.isBlank() && name.length() <= 120, "Product name must contain 1-120 characters");
            Failure.require(kind != null, "Product kind is required");
            Failure.require(variants != null && !variants.isEmpty() && variants.stream().noneMatch(Objects::isNull), "At least one valid variant is required");
            variants = Collections.unmodifiableSet(EnumSet.copyOf(variants));
        }
    }
    public interface Store {
        Optional<Product> latest(String id);
        Optional<Product> revision(String id, long revision);
        List<Product> latestProducts();
        /** Must reject duplicate revisions atomically. All published revisions are retained. */
        void insert(Product product);
    }
    public static final class Service {
        private final Store store;
        public Service(Store store) { this.store = Objects.requireNonNull(store); }
        public Product publish(Product product) {
            long next = store.latest(product.id()).map(p -> p.revision() + 1).orElse(1L);
            if (product.revision() != next) throw new Failure(Failure.Code.CONFLICT, "Publish the next product revision: " + next);
            store.insert(product);
            return product;
        }
        public Product latest(String id) { Failure.id(id, "Product ID"); return store.latest(id).orElseThrow(() -> missing(id)); }
        public Product revision(String id, long revision) {
            Failure.id(id, "Product ID");
            Failure.require(revision > 0, "Revision must be positive");
            return store.revision(id, revision).orElseThrow(() -> missing(id));
        }
        public List<Product> products() { return List.copyOf(store.latestProducts()); }
        private Failure missing(String id) { return new Failure(Failure.Code.NOT_FOUND, "Product not found: " + id); }
    }
    /** Only immutable catalog snapshots cross a service boundary. */
    public interface Client { Product revision(String id, long revision); }
}
