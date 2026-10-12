package dev.parkcommerce.core;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/** Append-only editorial workflow. Draft versions are not published revisions. */
public final class CatalogEditorial {
    private CatalogEditorial() {}
    public enum Operation { SAVE_DRAFT, PUBLISH_DRAFT, DIRECT_PUBLISH }
    public enum AuditStatus { RECORDED, LEGACY_UNAVAILABLE }

    public record Content(String name, Catalog.Kind kind, Set<Catalog.Variant> variants) {
        public Content {
            // Reuse the published-content validation; this never inserts a product.
            var validated = new Catalog.Product("validation", 1, name, kind, variants);
            variants = validated.variants();
        }
        public Catalog.Product product(String id, long revision) {
            return new Catalog.Product(id, revision, name, kind, variants);
        }
    }
    /** Version numbers refer to the stream named by operation; no credential/payload is logged. */
    public record Audit(String actor, Operation operation, String productId,
                        long previousVersion, long version, Instant occurredAt) {
        public Audit {
            actor = CatalogEditorial.actor(actor);
            Failure.require(operation != null, "Audit operation is required");
            Failure.id(productId, "Product ID");
            Failure.require(previousVersion >= 0 && previousVersion < Long.MAX_VALUE
                && version == previousVersion + 1, "Audit versions must be consecutive");
            Failure.require(occurredAt != null, "Server timestamp is required");
        }
    }
    public record Draft(String productId, long version, long basePublishedRevision,
                        Content content, Audit audit) {
        public Draft {
            Failure.id(productId, "Product ID");
            Failure.require(version > 0 && basePublishedRevision >= 0, "Invalid draft versions");
            Failure.require(content != null, "Draft content is required");
            Failure.require(audit != null && audit.operation() == Operation.SAVE_DRAFT
                && productId.equals(audit.productId()) && version == audit.version(), "Invalid draft audit");
        }
    }
    /** Only old/unattributed snapshots may have null audit. Never manufacture a legacy actor/time. */
    public record Publication(Catalog.Product product, Long sourceDraftVersion, Audit audit,
                              AuditStatus auditStatus) {
        public Publication {
            Failure.require(product != null && auditStatus != null, "Publication is required");
            if (auditStatus == AuditStatus.LEGACY_UNAVAILABLE) {
                Failure.require(audit == null && sourceDraftVersion == null, "Legacy metadata must remain unknown");
            } else {
                Failure.require(audit != null && product.id().equals(audit.productId())
                    && product.revision() == audit.version(), "Invalid publication audit");
                if (audit.operation() == Operation.PUBLISH_DRAFT) {
                    Failure.require(sourceDraftVersion != null && sourceDraftVersion > 0, "Published draft version is required");
                } else {
                    Failure.require(audit.operation() == Operation.DIRECT_PUBLISH && sourceDraftVersion == null,
                        "Invalid direct publication metadata");
                }
            }
        }
        public static Publication legacy(Catalog.Product product) {
            return new Publication(product, null, null, AuditStatus.LEGACY_UNAVAILABLE);
        }
    }
    public record SaveDraft(long expectedDraftVersion, long basePublishedRevision, Content content) {
        public SaveDraft {
            Failure.require(expectedDraftVersion >= 0 && basePublishedRevision >= 0, "Expected versions must not be negative");
            Failure.require(content != null, "Draft content is required");
        }
    }
    public record PublishDraft(long draftVersion, long expectedPublishedRevision) {
        public PublishDraft {
            Failure.require(draftVersion > 0 && expectedPublishedRevision >= 0, "Invalid publication versions");
        }
    }
    public record HistoryPage(long snapshotRevision, List<Publication> items, String nextCursor) {
        public HistoryPage { items = List.copyOf(items); }
    }
    /** Each insert MUST atomically reject duplicate (product, version) keys.
     * Audit is retained inside that same record, not in a second write.
     * Normal application writes append consecutive versions; no delete/update path exists.
     */
    public interface Store extends Catalog.Store {
        Optional<Draft> latestDraft(String id);
        Optional<Draft> draft(String id, long version);
        void insertDraft(Draft draft);
        void insertPublication(Publication publication);
        Optional<Publication> publication(String id, long revision);
        /** Descending revision, <= snapshot; beforeExclusive == 0 means no lower-page boundary. */
        List<Publication> publications(String id, long snapshot, long beforeExclusive, int limit);
    }

    public static final class Service {
        private final Store store;
        private final Clock clock;
        public Service(Store store, Clock clock) {
            this.store = Objects.requireNonNull(store);
            this.clock = Objects.requireNonNull(clock);
        }
        public Draft saveDraft(String id, SaveDraft request, String subject) {
            Failure.id(id, "Product ID"); actor(subject);
            Failure.require(request != null, "Draft request is required");
            long current = store.latestDraft(id).map(Draft::version).orElse(0L);
            expect(current, request.expectedDraftVersion(), "Draft changed; reload before saving");
            expect(publishedVersion(id), request.basePublishedRevision(), "Published base changed; review and rebase the draft");
            long version = next(current);
            var result = new Draft(id, version, request.basePublishedRevision(), request.content(),
                audit(subject, Operation.SAVE_DRAFT, id, current, version));
            // A racing save uses the same next key and is rejected at the store.
            // A racing PUBLICATION may make the pinned base stale; no auto-rebase is implied.
            store.insertDraft(result);
            return result;
        }
        public Draft latestDraft(String id) {
            Failure.id(id, "Product ID");
            return store.latestDraft(id).orElseThrow(() -> missing("Draft"));
        }
        public Draft draft(String id, long version) {
            Failure.id(id, "Product ID"); Failure.require(version > 0, "Draft version must be positive");
            return store.draft(id, version).orElseThrow(() -> missing("Draft"));
        }
        public Publication publishDraft(String id, PublishDraft request, String subject) {
            Failure.id(id, "Product ID"); actor(subject);
            Failure.require(request != null, "Publication request is required");
            Draft selected = draft(id, request.draftVersion());
            expect(selected.basePublishedRevision(), request.expectedPublishedRevision(), "Draft base does not match publication request");
            expect(publishedVersion(id), request.expectedPublishedRevision(), "Published base changed; save a rebased draft first");
            long revision = next(request.expectedPublishedRevision());
            var result = new Publication(selected.content().product(id, revision), selected.version(),
                audit(subject, Operation.PUBLISH_DRAFT, id, request.expectedPublishedRevision(), revision), AuditStatus.RECORDED);
            store.insertPublication(result);
            return result;
        }
        /** Compatibility path for the original authenticated immediate-publication HTTP endpoint. */
        public Catalog.Product publishDirect(Catalog.Product product, String subject) {
            actor(subject); Failure.require(product != null, "Product is required");
            long before = publishedVersion(product.id());
            expect(product.revision(), next(before), "Publish the next catalog revision");
            store.insertPublication(new Publication(product, null,
                audit(subject, Operation.DIRECT_PUBLISH, product.id(), before, product.revision()), AuditStatus.RECORDED));
            return product;
        }
        public HistoryPage history(String id, int limit, String cursor) {
            Failure.id(id, "Product ID"); Failure.require(limit >= 1 && limit <= 50, "History limit must be 1-50");
            long snapshot, before;
            if (cursor == null) {
                snapshot = publishedVersion(id); before = 0;
            } else {
                Cursor parsed = Cursor.decode(cursor, id); snapshot = parsed.snapshot(); before = parsed.before();
                // Reject invented bounds and a cursor from deleted/corrupted history instead of silently skipping.
                Failure.require(store.publication(id, snapshot).isPresent() && store.publication(id, before).isPresent(),
                    "History cursor no longer identifies retained revisions");
            }
            if (snapshot == 0) return new HistoryPage(0, List.of(), null);
            List<Publication> fetched = store.publications(id, snapshot, before, limit + 1);
            List<Publication> items = List.copyOf(fetched.subList(0, Math.min(limit, fetched.size())));
            String nextCursor = fetched.size() > limit
                ? new Cursor(id, snapshot, items.getLast().product().revision()).encode() : null;
            return new HistoryPage(snapshot, items, nextCursor);
        }
        private long publishedVersion(String id) { return store.latest(id).map(Catalog.Product::revision).orElse(0L); }
        private Audit audit(String actor, Operation operation, String id, long previous, long version) {
            return new Audit(actor, operation, id, previous, version, clock.instant().truncatedTo(ChronoUnit.MILLIS));
        }
    }
    private record Cursor(String productId, long snapshot, long before) {
        private String encode() {
            return Base64.getUrlEncoder().withoutPadding().encodeToString(
                ("v1:" + productId + ":" + snapshot + ":" + before).getBytes(StandardCharsets.US_ASCII));
        }
        private static Cursor decode(String value, String id) {
            Failure.require(value != null && value.length() <= 256 && value.matches("[A-Za-z0-9_-]+"), "Invalid history cursor");
            try {
                String[] parts = new String(Base64.getUrlDecoder().decode(value), StandardCharsets.US_ASCII).split(":", -1);
                Failure.require(parts.length == 4 && parts[0].equals("v1") && parts[1].equals(id), "Invalid history cursor");
                Cursor parsed = new Cursor(parts[1], Long.parseLong(parts[2]), Long.parseLong(parts[3]));
                Failure.require(parsed.snapshot >= parsed.before && parsed.before > 0
                    && parsed.encode().equals(value), "Invalid history cursor");
                return parsed;
            } catch (IllegalArgumentException error) { throw Failure.invalid("Invalid history cursor"); }
        }
    }
    private static String actor(String subject) {
        Failure.owner(subject);
        Failure.require(subject.chars().noneMatch(Character::isISOControl), "Actor must not contain control characters");
        return subject;
    }
    private static long next(long current) {
        if (current == Long.MAX_VALUE) throw new Failure(Failure.Code.CONFLICT, "Version space exhausted");
        return current + 1;
    }
    private static void expect(long actual, long expected, String message) {
        if (actual != expected) throw new Failure(Failure.Code.CONFLICT, message);
    }
    private static Failure missing(String what) { return new Failure(Failure.Code.NOT_FOUND, what + " not found"); }
}
