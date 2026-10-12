package dev.parkcommerce.core;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import static dev.parkcommerce.core.Catalog.*;
import static dev.parkcommerce.core.CoreScenarios.*;
import static dev.parkcommerce.core.Failure.Code.*;

/** JDK-only contract tests. Test-memory stores do not prove MongoDB isolation. */
public final class CatalogEditorialScenarios {
    private CatalogEditorialScenarios() {}
    static final Instant NOW = Instant.parse("2030-10-01T12:00:00Z");
    static CatalogEditorial.Content content(String name) {
        return new CatalogEditorial.Content(name, Kind.EVENT, EnumSet.allOf(Variant.class));
    }
    static final class Fixture {
        final MemoryStores.CatalogStore store = new MemoryStores.CatalogStore();
        final MemoryStores.MutableClock clock = new MemoryStores.MutableClock(NOW);
        final Catalog.Service guest = new Catalog.Service(store);
        final CatalogEditorial.Service editor = new CatalogEditorial.Service(store, clock);
        CatalogEditorial.Draft save(long expected, long base, String name) {
            return editor.saveDraft("night", new CatalogEditorial.SaveDraft(expected, base, content(name)), "employee-a");
        }
        CatalogEditorial.Publication publish(long draft, long base) {
            return editor.publishDraft("night", new CatalogEditorial.PublishDraft(draft, base), "employee-b");
        }
    }
    public static List<Case> cases() {
        var cases = new ArrayList<Case>();
        cases.add(new Case("Catalog draft is invisible to guest reads", () -> {
            var f = new Fixture(); var draft = f.save(0, 0, "Unpublished night");
            equal(1L, draft.version()); equal(List.of(), f.guest.products());
            failure(NOT_FOUND, () -> f.guest.latest("night"));
            failure(NOT_FOUND, () -> f.guest.revision("night", 1));
        }));
        cases.add(new Case("Catalog publication selects the exact retained draft", () -> {
            var f = new Fixture(); f.save(0, 0, "Reviewed"); f.save(1, 0, "Later edit");
            var published = f.publish(1, 0);
            equal("Reviewed", published.product().name()); equal(1L, published.sourceDraftVersion());
            equal("Later edit", f.editor.latestDraft("night").content().name());
            equal("Reviewed", f.guest.latest("night").name());
        }));
        cases.add(new Case("Catalog draft save rejects a stale predecessor", () -> {
            var f = new Fixture(); f.save(0, 0, "One");
            failure(CONFLICT, () -> f.save(0, 0, "Overwritten"));
            equal("One", f.editor.latestDraft("night").content().name());
        }));
        cases.add(new Case("Catalog publication rejects an already-used published base", () -> {
            var f = new Fixture(); f.save(0, 0, "One"); f.publish(1, 0);
            failure(CONFLICT, () -> f.publish(1, 0)); equal(1L, f.guest.latest("night").revision());
        }));
        cases.add(new Case("Catalog history traversal excludes later publications", () -> {
            var f = new Fixture();
            for (int i=1; i<=3; i++) { f.save(i-1, i-1, "Name " + i); f.publish(i, i-1); }
            var page = f.editor.history("night", 2, null);
            equal(List.of(3L,2L), page.items().stream().map(p -> p.product().revision()).toList());
            f.save(3, 3, "Newer"); f.publish(4, 3);
            var tail = f.editor.history("night", 2, page.nextCursor());
            equal(3L, tail.snapshotRevision()); equal(1L, tail.items().getFirst().product().revision());
            equal(null, tail.nextCursor());
        }));
        cases.add(new Case("Catalog audit comes from explicit actor and server clock", () -> {
            var f = new Fixture(); var draft = f.save(0, 0, "One"); f.clock.advance(Duration.ofSeconds(2));
            var published = f.publish(1, 0);
            equal("employee-a", draft.audit().actor()); equal(NOW, draft.audit().occurredAt());
            equal("employee-b", published.audit().actor()); equal(NOW.plusSeconds(2), published.audit().occurredAt());
        }));
        cases.add(new Case("Catalog draft keeps prior published product guest-visible", () -> {
            var f = new Fixture(); f.save(0,0,"Published"); f.publish(1,0); f.save(1,1,"Private edit");
            equal("Published", f.guest.latest("night").name()); equal(1, f.guest.products().size());
            failure(NOT_FOUND, () -> f.guest.revision("night",2));
        }));
        cases.add(new Case("Catalog historical drafts cannot be rewritten", () -> {
            var f = new Fixture(); var first=f.save(0,0,"One"); f.save(1,0,"Two");
            equal(first,f.editor.draft("night",1)); equal(2L,f.editor.latestDraft("night").version());
            failure(CONFLICT, () -> f.store.insertDraft(first));
        }));
        cases.add(new Case("Catalog publication preserves prior name and variants", () -> {
            var f=new Fixture(); f.save(0,0,"Original"); var first=f.publish(1,0);
            f.editor.saveDraft("night",new CatalogEditorial.SaveDraft(1,1,new CatalogEditorial.Content("Adults only",Kind.EVENT,Set.of(Variant.ADULT))),"employee-a");
            f.publish(2,1); equal(first.product(),f.guest.revision("night",1));
            equal(Set.of(Variant.ADULT),f.guest.latest("night").variants());
        }));
        cases.add(new Case("Catalog publication pins its stored base not a caller rebase", () -> {
            var f=new Fixture(); f.save(0,0,"Old draft"); f.editor.publishDirect(new Product("night",1,"Direct",Kind.EVENT,Set.of(Variant.ADULT)),"rival");
            failure(CONFLICT,()->f.publish(1,1)); failure(CONFLICT,()->f.publish(1,0));
            equal("Direct",f.guest.latest("night").name());
        }));
        cases.add(new Case("Catalog explicit rebase requires a new saved draft version", () -> {
            var f=new Fixture(); f.save(0,0,"Draft"); f.publish(1,0);
            var rebased=f.save(1,1,"Reviewed again"); var p=f.publish(rebased.version(),1);
            equal(2L,p.product().revision()); equal(2L,p.sourceDraftVersion());
        }));
        cases.add(new Case("Catalog saving against stale published base is rejected", () -> {
            var f=new Fixture(); f.save(0,0,"One"); f.publish(1,0);
            failure(CONFLICT,()->f.save(1,0,"Stale")); equal(1L,f.editor.latestDraft("night").version());
        }));
        cases.add(new Case("Catalog save and publication streams can safely interleave", () -> {
            var f=new Fixture(); f.save(0,0,"One"); f.publish(1,0);
            var store=new Forwarding(f.store); store.beforeDraft=()->f.editor.publishDirect(new Product("night",2,"Racing publication",Kind.EVENT,Set.of(Variant.ADULT)),"rival");
            var editor=new CatalogEditorial.Service(store,f.clock);
            var saved=editor.saveDraft("night",new CatalogEditorial.SaveDraft(1,1,content("Pending")),"employee-a");
            equal(1L,saved.basePublishedRevision()); equal(2L,f.guest.latest("night").revision());
            failure(CONFLICT,()->f.publish(2,1)); equal("Pending",f.editor.latestDraft("night").content().name());
        }));
        cases.add(new Case("Catalog concurrent edit does not change reviewed publication content", () -> {
            var f=new Fixture(); f.save(0,0,"Reviewed"); var store=new Forwarding(f.store);
            store.beforePublication=()->f.save(1,0,"Saved concurrently");
            var p=new CatalogEditorial.Service(store,f.clock).publishDraft("night",new CatalogEditorial.PublishDraft(1,0),"employee-b");
            equal("Reviewed",p.product().name()); equal(1L,p.sourceDraftVersion());
            equal("Saved concurrently",f.editor.latestDraft("night").content().name());
        }));
        cases.add(new Case("Catalog losing publication cannot write an orphan audit event", () -> {
            var f=new Fixture(); f.save(0,0,"Reviewed"); var store=new Forwarding(f.store);
            store.beforePublication=()->f.editor.publishDirect(new Product("night",1,"Winner",Kind.EVENT,Set.of(Variant.ADULT)),"rival");
            var editor=new CatalogEditorial.Service(store,f.clock);
            failure(CONFLICT,()->editor.publishDraft("night",new CatalogEditorial.PublishDraft(1,0),"loser"));
            var page=f.editor.history("night",10,null); equal(1,page.items().size()); equal("rival",page.items().getFirst().audit().actor());
        }));
        cases.add(new Case("Catalog 24 simultaneous saves retain exactly one new draft", () -> {
            var f=new Fixture(); var store=new Forwarding(f.store); var gate=new CyclicBarrier(24); store.beforeDraft=()->await(gate);
            var editor=new CatalogEditorial.Service(store,f.clock);
            equal(1,race(24,()->editor.saveDraft("night",new CatalogEditorial.SaveDraft(0,0,content("Concurrent")),"employee-a")));
            equal(1L,f.editor.latestDraft("night").version()); equal(List.of(),f.guest.products());
        }));
        cases.add(new Case("Catalog 24 simultaneous publications retain one revision and audit", () -> {
            var f=new Fixture(); f.save(0,0,"Reviewed"); var store=new Forwarding(f.store); var gate=new CyclicBarrier(24); store.beforePublication=()->await(gate);
            var editor=new CatalogEditorial.Service(store,f.clock);
            equal(1,race(24,()->editor.publishDraft("night",new CatalogEditorial.PublishDraft(1,0),"employee-b")));
            equal(1,f.editor.history("night",50,null).items().size()); equal(1L,f.guest.latest("night").revision());
        }));
        cases.add(new Case("Catalog unknown product history is empty without creating records", () -> {
            var f=new Fixture(); var page=f.editor.history("unknown",10,null);
            equal(0L,page.snapshotRevision()); equal(List.of(),page.items()); equal(null,page.nextCursor()); equal(List.of(),f.guest.products());
        }));
        cases.add(new Case("Catalog draft-only product has empty published history", () -> {
            var f=new Fixture(); f.save(0,0,"Hidden"); equal(List.of(),f.editor.history("night",1,null).items());
        }));
        cases.add(new Case("Catalog one-row history has no next cursor", () -> {
            var f=new Fixture(); f.save(0,0,"One"); f.publish(1,0);
            equal(null,f.editor.history("night",1,null).nextCursor()); equal(1,f.editor.history("night",50,null).items().size());
        }));
        cases.add(new Case("Catalog history supports page-size changes without duplicates", () -> {
            var f=new Fixture(); seed(f,6); var seen=new ArrayList<Long>();
            var page=f.editor.history("night",1,null); seen.addAll(revisions(page));
            var second=f.editor.history("night",3,page.nextCursor()); seen.addAll(revisions(second));
            var last=f.editor.history("night",50,second.nextCursor()); seen.addAll(revisions(last));
            equal(List.of(6L,5L,4L,3L,2L,1L),seen); equal(null,last.nextCursor());
        }));
        cases.add(new Case("Catalog history cursor replay is stable after new writes", () -> {
            var f=new Fixture(); seed(f,4); var cursor=f.editor.history("night",2,null).nextCursor(); var before=f.editor.history("night",1,cursor);
            f.save(4,4,"Five"); f.publish(5,4); equal(before,f.editor.history("night",1,cursor));
            equal(5L,f.editor.history("night",1,null).snapshotRevision());
        }));
        cases.add(new Case("Catalog history is ordered by revision even with backwards clocks", () -> {
            var f=new Fixture(); seed(f,1); f.clock.advance(Duration.ofHours(-1)); f.save(1,1,"Two"); f.publish(2,1);
            equal(List.of(2L,1L),revisions(f.editor.history("night",10,null)));
        }));
        cases.add(new Case("Catalog history result collections are immutable", () -> {
            var f=new Fixture(); seed(f,1); boolean blocked=false;
            try { f.editor.history("night",10,null).items().clear(); } catch (UnsupportedOperationException e) { blocked=true; }
            check(blocked); equal(1,f.editor.history("night",10,null).items().size());
        }));
        for(int limit:List.of(-1,0,51,Integer.MAX_VALUE)) cases.add(new Case("Catalog rejects history limit "+limit, () -> {
            var f=new Fixture(); failure(INVALID_INPUT,()->f.editor.history("night",limit,null));
        }));
        for(String cursor:List.of("", "not:a:cursor", "x".repeat(257), token("v2:night:3:2"), token("v1:other:3:2"),
            token("v1:night:03:2"),token("v1:night:3:0"),token("v1:night:2:3"),token("v1:night:999:2"),
            token("v1:night:3:9223372036854775808"),token("v1:night:3:2")+"=")) {
            cases.add(new Case("Catalog rejects malformed or unrelated cursor "+cursor.substring(0,Math.min(36,cursor.length())), () -> {
                var f=new Fixture(); seed(f,3); failure(INVALID_INPUT,()->f.editor.history("night",1,cursor));
            }));
        }
        cases.add(new Case("Catalog history cursor at last retained row yields empty tail", () -> {
            var f=new Fixture(); seed(f,2); var last=f.editor.history("night",1,token("v1:night:2:1"));
            equal(List.of(),last.items()); equal(null,last.nextCursor()); equal(2L,last.snapshotRevision());
        }));
        cases.add(new Case("Catalog cursor survives service and fixture-store reconstruction", () -> {
            var f=new Fixture(); seed(f,3); var cursor=f.editor.history("night",1,null).nextCursor();
            var reconstructed=new CatalogEditorial.Service(f.store.restartCopy(),f.clock);
            equal(f.editor.history("night",2,cursor),reconstructed.history("night",2,cursor));
            equal(f.editor.latestDraft("night"),reconstructed.latestDraft("night"));
        }));
        cases.add(new Case("Catalog old snapshots expose missing audit without fabrication", () -> {
            var f=new Fixture(); f.guest.publish(new Product("night",1,"Legacy",Kind.EVENT,Set.of(Variant.ADULT)));
            var entry=f.editor.history("night",1,null).items().getFirst();
            equal(CatalogEditorial.AuditStatus.LEGACY_UNAVAILABLE,entry.auditStatus()); equal(null,entry.audit()); equal(null,entry.sourceDraftVersion());
            f.save(0,1,"New"); f.publish(1,1); equal(CatalogEditorial.AuditStatus.RECORDED,f.editor.history("night",2,null).items().getFirst().auditStatus());
            equal("Legacy",f.guest.revision("night",1).name());
        }));
        cases.add(new Case("Catalog direct publication remains compatible and records actor", () -> {
            var f=new Fixture(); var p=new Product("night",1,"Direct",Kind.EVENT,Set.of(Variant.ADULT));
            equal(p,f.editor.publishDirect(p,"employee-z")); var entry=f.editor.history("night",1,null).items().getFirst();
            equal(CatalogEditorial.Operation.DIRECT_PUBLISH,entry.audit().operation()); equal("employee-z",entry.audit().actor()); equal(null,entry.sourceDraftVersion());
        }));
        cases.add(new Case("Catalog audit records stream-specific old and new revisions", () -> {
            var f=new Fixture(); f.save(0,0,"First"); var d=f.save(1,0,"Second draft"); var p=f.publish(2,0);
            equal(1L,d.audit().previousVersion()); equal(2L,d.audit().version()); equal(CatalogEditorial.Operation.SAVE_DRAFT,d.audit().operation());
            equal(0L,p.audit().previousVersion()); equal(1L,p.audit().version()); equal(CatalogEditorial.Operation.PUBLISH_DRAFT,p.audit().operation());
            equal("night",p.audit().productId());
        }));
        cases.add(new Case("Catalog timestamps normalize to Mongo-compatible millisecond precision", () -> {
            var f=new Fixture(); var clock=Clock.fixed(NOW.plusNanos(123456789),ZoneOffset.UTC);
            var editor=new CatalogEditorial.Service(f.store,clock);
            equal(NOW.plusMillis(123),editor.saveDraft("night",new CatalogEditorial.SaveDraft(0,0,content("One")),"employee-a").audit().occurredAt());
        }));
        for(String actor:Arrays.asList(null,"", " ","x".repeat(101),"employee\nforged")) cases.add(new Case("Catalog rejects invalid actor "+Objects.toString(actor,"null").replace("\n","\\n"),()->{
            var f=new Fixture(); failure(INVALID_INPUT,()->f.editor.saveDraft("night",new CatalogEditorial.SaveDraft(0,0,content("One")),actor));
            equal(List.of(),f.guest.products()); failure(NOT_FOUND,()->f.editor.latestDraft("night"));
        }));
        cases.add(new Case("Catalog draft validates content and defensive variant copies", () -> {
            failure(INVALID_INPUT,()->content("")); failure(INVALID_INPUT,()->new CatalogEditorial.Content("X",null,Set.of(Variant.ADULT)));
            failure(INVALID_INPUT,()->new CatalogEditorial.Content("X",Kind.EVENT,Set.of()));
            var variants=EnumSet.allOf(Variant.class); var input=new CatalogEditorial.Content("X",Kind.EVENT,variants); variants.clear();
            equal(2,input.variants().size());
        }));
        cases.add(new Case("Catalog rejects invalid identifiers and negative input versions", () -> {
            var f=new Fixture(); failure(INVALID_INPUT,()->f.editor.saveDraft("../x",new CatalogEditorial.SaveDraft(0,0,content("X")),"employee-a"));
            failure(INVALID_INPUT,()->new CatalogEditorial.SaveDraft(-1,0,content("X")));
            failure(INVALID_INPUT,()->new CatalogEditorial.SaveDraft(0,-1,content("X")));
            failure(INVALID_INPUT,()->new CatalogEditorial.PublishDraft(0,0)); failure(INVALID_INPUT,()->new CatalogEditorial.PublishDraft(1,-1));
        }));
        cases.add(new Case("Catalog null commands fail as invalid input", () -> {
            var f=new Fixture(); failure(INVALID_INPUT,()->f.editor.saveDraft("night",null,"employee-a"));
            failure(INVALID_INPUT,()->f.editor.publishDraft("night",null,"employee-a"));
            failure(INVALID_INPUT,()->f.editor.publishDirect(null,"employee-a"));
        }));
        cases.add(new Case("Catalog missing drafts and revisions do not create publications", () -> {
            var f=new Fixture(); failure(NOT_FOUND,()->f.editor.latestDraft("night"));
            failure(NOT_FOUND,()->f.editor.draft("night",1)); failure(NOT_FOUND,()->f.publish(1,0));
            failure(INVALID_INPUT,()->f.editor.draft("night",0)); equal(List.of(),f.guest.products());
        }));
        cases.add(new Case("Catalog missing published base cannot be forged", () -> {
            var f=new Fixture(); failure(CONFLICT,()->f.save(0,99,"X")); failure(NOT_FOUND,()->f.editor.latestDraft("night"));
        }));
        cases.add(new Case("Catalog exhausted revision counter cannot wrap", () -> {
            var f=new Fixture(); f.store.insert(new Product("night",Long.MAX_VALUE,"Last",Kind.EVENT,Set.of(Variant.ADULT)));
            failure(CONFLICT,()->f.editor.publishDirect(new Product("night",1,"Wrap",Kind.EVENT,Set.of(Variant.ADULT)),"employee-a"));
            f.save(0,Long.MAX_VALUE,"Pending"); failure(CONFLICT,()->f.publish(1,Long.MAX_VALUE));
        }));
        cases.add(new Case("Catalog exhausted draft counter cannot wrap", () -> {
            var f=new Fixture(); f.store.insertDraft(new CatalogEditorial.Draft("night",Long.MAX_VALUE,0,content("Last"),
                new CatalogEditorial.Audit("employee-a",CatalogEditorial.Operation.SAVE_DRAFT,"night",Long.MAX_VALUE-1,Long.MAX_VALUE,NOW)));
            failure(CONFLICT,()->f.save(Long.MAX_VALUE,0,"Wrap")); equal(Long.MAX_VALUE,f.editor.latestDraft("night").version());
        }));
        return cases;
    }
    static void seed(Fixture f,int count) {
        for(int i=1;i<=count;i++) { f.save(i-1,i-1,"Revision "+i); f.publish(i,i-1); }
    }
    static List<Long> revisions(CatalogEditorial.HistoryPage page) { return page.items().stream().map(p->p.product().revision()).toList(); }
    static String token(String raw) { return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(java.nio.charset.StandardCharsets.US_ASCII)); }
    static void await(CyclicBarrier gate) {
        try { gate.await(10,TimeUnit.SECONDS); } catch(Exception e) { throw new AssertionError(e); }
    }
    static int race(int n,Checked action) throws Exception {
        try(var pool=Executors.newFixedThreadPool(n)) {
            List<Callable<Boolean>> tasks=new ArrayList<>();
            for(int i=0;i<n;i++) tasks.add(()->{try { action.run(); return true; } catch(Failure e) { equal(CONFLICT,e.code()); return false; }});
            int winners=0; for(var future:pool.invokeAll(tasks,20,TimeUnit.SECONDS)) if(future.get()) winners++;
            return winners;
        }
    }
    /** Interleaves operations before the actual store insert, after service-level version checks. */
    static final class Forwarding implements CatalogEditorial.Store {
        final CatalogEditorial.Store delegate; Runnable beforeDraft=()->{}; Runnable beforePublication=()->{};
        Forwarding(CatalogEditorial.Store delegate) { this.delegate=delegate; }
        public Optional<Product> latest(String id) { return delegate.latest(id); }
        public Optional<Product> revision(String id,long version) { return delegate.revision(id,version); }
        public List<Product> latestProducts() { return delegate.latestProducts(); }
        public void insert(Product product) { delegate.insert(product); }
        public Optional<CatalogEditorial.Draft> latestDraft(String id) { return delegate.latestDraft(id); }
        public Optional<CatalogEditorial.Draft> draft(String id,long version) { return delegate.draft(id,version); }
        public void insertDraft(CatalogEditorial.Draft draft) { beforeDraft.run(); delegate.insertDraft(draft); }
        public void insertPublication(CatalogEditorial.Publication p) { beforePublication.run(); delegate.insertPublication(p); }
        public Optional<CatalogEditorial.Publication> publication(String id,long revision) { return delegate.publication(id,revision); }
        public List<CatalogEditorial.Publication> publications(String id,long snapshot,long before,int limit) { return delegate.publications(id,snapshot,before,limit); }
    }
}
