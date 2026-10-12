package dev.parkcommerce.catalog;

import dev.parkcommerce.core.*;
import java.util.*;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Repository;
import jakarta.annotation.PostConstruct;

/** Immutable bounded documents, not a growing aggregate or a two-write audit protocol. */
@Repository
public class MongoCatalogStore implements CatalogEditorial.Store {
    @Document("product_revisions")
    public record Snapshot(@Id String id, String productId, long revision, StoredProduct product,
                           Long sourceDraftVersion, CatalogEditorial.Audit audit) {
        // Reads pre-editorial documents without inventing actor/timestamp metadata.
        CatalogEditorial.Publication publication() {
            return new CatalogEditorial.Publication(product.toDomain(), sourceDraftVersion, audit,
                audit == null ? CatalogEditorial.AuditStatus.LEGACY_UNAVAILABLE : CatalogEditorial.AuditStatus.RECORDED);
        }
    }
    // Keep Mongo's nested _id convention out of the pure domain type. Older
    // snapshots may contain _id; the archive's literal legacy fixture uses id.
    public record StoredProduct(@Id String mongoId, @Field("id") String productId,
                                long revision, String name, Catalog.Kind kind,
                                Set<Catalog.Variant> variants) {
        static StoredProduct fromDomain(Catalog.Product product) {
            return new StoredProduct(null, product.id(), product.revision(), product.name(), product.kind(), product.variants());
        }
        Catalog.Product toDomain() {
            return new Catalog.Product(productId != null ? productId : mongoId, revision, name, kind, variants);
        }
    }
    @Document("product_drafts")
    public record DraftSnapshot(@Id String id, String productId, long version, CatalogEditorial.Draft draft) {}
    private final MongoTemplate mongo;
    public MongoCatalogStore(MongoTemplate mongo) { this.mongo = mongo; }
    @PostConstruct void indexes() {
        mongo.indexOps(Snapshot.class).ensureIndex(new Index().on("productId", Sort.Direction.ASC).on("revision", Sort.Direction.DESC).unique());
        mongo.indexOps(DraftSnapshot.class).ensureIndex(new Index().on("productId", Sort.Direction.ASC).on("version", Sort.Direction.DESC).unique());
    }
    @Override public Optional<Catalog.Product> latest(String id) {
        var query = Query.query(Criteria.where("productId").is(id)).with(Sort.by(Sort.Direction.DESC, "revision")).limit(1);
        return Optional.ofNullable(mongo.findOne(query, Snapshot.class)).map(snapshot -> snapshot.product().toDomain());
    }
    @Override public Optional<Catalog.Product> revision(String id, long revision) {
        return Optional.ofNullable(mongo.findById(id + ":" + revision, Snapshot.class)).map(snapshot -> snapshot.product().toDomain());
    }
    @Override public List<Catalog.Product> latestProducts() {
        var pipeline = Aggregation.newAggregation(
            Aggregation.sort(Sort.by(Sort.Order.asc("productId"), Sort.Order.desc("revision"))),
            Aggregation.group("productId").first(Aggregation.ROOT).as("snapshot"),
            Aggregation.replaceRoot("snapshot"), Aggregation.sort(Sort.Direction.ASC, "productId"), Aggregation.limit(100));
        return mongo.aggregate(pipeline, "product_revisions", Snapshot.class).getMappedResults().stream()
            .map(snapshot -> snapshot.product().toDomain()).toList();
    }
    @Override public void insert(Catalog.Product product) {
        // Compatibility/bootstrap-only Store contract. HTTP uses audited insertPublication.
        insertPublication(CatalogEditorial.Publication.legacy(product));
    }
    @Override public void insertPublication(CatalogEditorial.Publication publication) {
        var product = publication.product();
        try {
            mongo.insert(new Snapshot(product.id() + ":" + product.revision(), product.id(), product.revision(), StoredProduct.fromDomain(product),
                publication.sourceDraftVersion(), publication.audit()));
        } catch (DuplicateKeyException error) {
            throw new Failure(Failure.Code.CONFLICT, "This catalog revision was already published; reload before publishing");
        }
    }
    @Override public Optional<CatalogEditorial.Publication> publication(String id, long revision) {
        return Optional.ofNullable(mongo.findById(id + ":" + revision, Snapshot.class)).map(Snapshot::publication);
    }
    @Override public List<CatalogEditorial.Publication> publications(String id, long snapshot, long before, int limit) {
        Criteria revision = Criteria.where("revision").lte(snapshot);
        if (before > 0) revision = revision.lt(before);
        var query = Query.query(Criteria.where("productId").is(id)).addCriteria(revision)
            .with(Sort.by(Sort.Direction.DESC, "revision")).limit(limit);
        return mongo.find(query, Snapshot.class).stream().map(Snapshot::publication).toList();
    }
    @Override public Optional<CatalogEditorial.Draft> latestDraft(String id) {
        var query = Query.query(Criteria.where("productId").is(id)).with(Sort.by(Sort.Direction.DESC, "version")).limit(1);
        return Optional.ofNullable(mongo.findOne(query, DraftSnapshot.class)).map(DraftSnapshot::draft);
    }
    @Override public Optional<CatalogEditorial.Draft> draft(String id, long version) {
        return Optional.ofNullable(mongo.findById(id + ":" + version, DraftSnapshot.class)).map(DraftSnapshot::draft);
    }
    @Override public void insertDraft(CatalogEditorial.Draft draft) {
        try {
            mongo.insert(new DraftSnapshot(draft.productId() + ":" + draft.version(), draft.productId(), draft.version(), draft));
        } catch (DuplicateKeyException error) {
            throw new Failure(Failure.Code.CONFLICT, "Draft changed; reload before saving");
        }
    }
}
