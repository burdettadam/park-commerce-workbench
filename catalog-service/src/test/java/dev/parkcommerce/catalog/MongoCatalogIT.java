package dev.parkcommerce.catalog;
import com.mongodb.client.MongoClients;
import dev.parkcommerce.core.*;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.mongodb.MongoDBContainer;
import static org.junit.jupiter.api.Assertions.*;
@Testcontainers
class MongoCatalogIT {
    @Container static MongoDBContainer mongo=new MongoDBContainer("mongo:8.0.26");
    MongoCatalogStore store;Catalog.Service service;
    com.mongodb.client.MongoClient client;
    @BeforeEach void setup(){
        client=MongoClients.create(mongo.getConnectionString());
        var template=new MongoTemplate(client,"catalog_it");template.dropCollection(MongoCatalogStore.Snapshot.class);template.dropCollection(MongoCatalogStore.DraftSnapshot.class);
        store=new MongoCatalogStore(template);store.indexes();service=new Catalog.Service(store);
    }
    @AfterEach void close(){client.close();}
    Catalog.Product product(long revision){return new Catalog.Product("night",revision,"Night "+revision,Catalog.Kind.EVENT,Set.of(Catalog.Variant.ADULT));}
    CatalogEditorial.Service editor(){return new CatalogEditorial.Service(store,java.time.Clock.fixed(java.time.Instant.parse("2030-10-01T12:00:00Z"),java.time.ZoneOffset.UTC));}
    CatalogEditorial.SaveDraft draft(long expected,long base,String name){
        return new CatalogEditorial.SaveDraft(expected,base,new CatalogEditorial.Content(name,Catalog.Kind.EVENT,Set.of(Catalog.Variant.ADULT)));
    }
    @Test void draftsRoundTripWithoutAppearingInPublicAggregation(){
        var saved=editor().saveDraft("night",draft(0,0,"Hidden"),"employee-a");
        assertEquals(saved,editor().draft("night",1));assertTrue(service.products().isEmpty());
        assertEquals(Failure.Code.NOT_FOUND,assertThrows(Failure.class,()->service.latest("night")).code());
        var publication=editor().publishDraft("night",new CatalogEditorial.PublishDraft(1,0),"employee-b");
        assertEquals(publication,editor().history("night",10,null).items().getFirst());
        assertEquals(saved,editor().draft("night",1));assertEquals("Hidden",service.latest("night").name());
    }
    @Test void draftUniqueIndexRejectsDuplicateContentVersion(){
        var saved=editor().saveDraft("night",draft(0,0,"One"),"employee-a");
        assertEquals(Failure.Code.CONFLICT,assertThrows(Failure.class,()->store.insertDraft(saved)).code());
        assertEquals(saved,editor().latestDraft("night"));
    }
    @Test void publicationHistoryIsPinnedAcrossNewInserts(){
        for(int i=1;i<=4;i++){editor().saveDraft("night",draft(i-1,i-1,"Name "+i),"employee-a");editor().publishDraft("night",new CatalogEditorial.PublishDraft(i,i-1),"employee-b");}
        var first=editor().history("night",2,null);assertEquals(List.of(4L,3L),first.items().stream().map(p->p.product().revision()).toList());
        editor().saveDraft("night",draft(4,4,"Five"),"employee-a");editor().publishDraft("night",new CatalogEditorial.PublishDraft(5,4),"employee-b");
        var tail=editor().history("night",2,first.nextCursor());assertEquals(4L,tail.snapshotRevision());
        assertEquals(List.of(2L,1L),tail.items().stream().map(p->p.product().revision()).toList());assertNull(tail.nextCursor());
    }
    @Test void realLegacyDocumentWithoutNewFieldsRemainsReadable(){
        var template=new MongoTemplate(client,"catalog_it");
        // Literal schema used before this slice: no sourceDraftVersion or audit keys.
        template.getCollection("product_revisions").insertOne(org.bson.Document.parse(
            "{\"_id\":\"night:1\",\"productId\":\"night\",\"revision\":1,\"product\":{\"id\":\"night\",\"revision\":1,\"name\":\"Legacy\",\"kind\":\"EVENT\",\"variants\":[\"ADULT\"]}}"));
        assertEquals("Legacy",service.latest("night").name());
        // Earlier Spring Data mapping can also persist a nested domain id as _id.
        template.getCollection("product_revisions").insertOne(org.bson.Document.parse(
            "{\"_id\":\"dusk:1\",\"productId\":\"dusk\",\"revision\":1,\"product\":{\"_id\":\"dusk\",\"revision\":1,\"name\":\"Mapped legacy\",\"kind\":\"EVENT\",\"variants\":[\"ADULT\"]}}"));
        assertEquals("Mapped legacy",service.latest("dusk").name());
        var entry=editor().history("night",10,null).items().getFirst();
        assertEquals(CatalogEditorial.AuditStatus.LEGACY_UNAVAILABLE,entry.auditStatus());assertNull(entry.audit());
        editor().saveDraft("night",draft(0,1,"New"),"employee-a");editor().publishDraft("night",new CatalogEditorial.PublishDraft(1,1),"employee-b");
        assertEquals("Legacy",service.revision("night",1).name());assertEquals(2,editor().history("night",10,null).items().size());
    }
    @Test void simultaneousEditorialSavesAndPublicationsHaveOneWinner()throws Exception {
        try(var pool=Executors.newFixedThreadPool(8)){
            List<Callable<Boolean>> saves=new ArrayList<>();for(int i=0;i<16;i++)saves.add(()->{
                try{editor().saveDraft("night",draft(0,0,"Candidate"),"employee-a");return true;}
                catch(Failure e){assertEquals(Failure.Code.CONFLICT,e.code());return false;}});
            int saved=0;for(var f:pool.invokeAll(saves,30,TimeUnit.SECONDS))if(f.get())saved++;assertEquals(1,saved);
            List<Callable<Boolean>> publications=new ArrayList<>();for(int i=0;i<16;i++)publications.add(()->{
                try{editor().publishDraft("night",new CatalogEditorial.PublishDraft(1,0),"employee-b");return true;}
                catch(Failure e){assertEquals(Failure.Code.CONFLICT,e.code());return false;}});
            int published=0;for(var f:pool.invokeAll(publications,30,TimeUnit.SECONDS))if(f.get())published++;assertEquals(1,published);
            assertEquals(1,editor().history("night",50,null).items().size());
            assertEquals(1L,new MongoTemplate(client,"catalog_it").getCollection("product_drafts").countDocuments());
        }
    }
    @Test void recreatedMongoAdapterRetainsDraftAndHistoryIdentities(){
        var saved=editor().saveDraft("night",draft(0,0,"One"),"employee-a");
        var publication=editor().publishDraft("night",new CatalogEditorial.PublishDraft(1,0),"employee-b");
        var restored=new CatalogEditorial.Service(new MongoCatalogStore(new MongoTemplate(client,"catalog_it")),java.time.Clock.systemUTC());
        assertEquals(saved,restored.draft("night",1));assertEquals(publication,restored.history("night",1,null).items().getFirst());
    }
    @Test void snapshotsRoundTripAndAggregateLatest(){
        service.publish(product(1));service.publish(product(2));
        assertEquals(product(1),service.revision("night",1));assertEquals(List.of(product(2)),service.products());
    }
    @Test void indexRejectsDuplicateRevision(){
        store.insert(product(1));var failure=assertThrows(Failure.class,()->store.insert(product(1)));
        assertEquals(Failure.Code.CONFLICT,failure.code());
    }
    @Test void competingPublishersCannotOverwrite(){
        service.publish(product(1));
        try(var executor=Executors.newFixedThreadPool(8)){
            List<Callable<Boolean>> jobs=new ArrayList<>();
            for(int i=0;i<16;i++)jobs.add(()->{try{service.publish(product(2));return true;}catch(Failure e){assertEquals(Failure.Code.CONFLICT,e.code());return false;}});
            int winners=0;for(var future:executor.invokeAll(jobs))if(future.get())winners++;
            assertEquals(1,winners);assertEquals(2L,service.latest("night").revision());
        }catch(Exception e){throw new AssertionError(e);}
    }
}
