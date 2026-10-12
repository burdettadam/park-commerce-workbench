package dev.parkcommerce.catalog;

import dev.parkcommerce.core.*;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.*;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.mongodb.MongoDBContainer;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.*;

/** Real Spring security/serialization and MongoDB when executed; no auto-skip without Docker. */
@Testcontainers
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,properties={
    "spring.profiles.active=local",
    "DEMO_EMPLOYEE_TOKEN=test_employee_123456789012345678901234",
    "DEMO_GUEST_TOKEN=test_guest_123456789012345678901234567",
    "DEMO_OTHER_GUEST_TOKEN=test_other_123456789012345678901234567",
    "DEMO_SERVICE_TOKEN=test_service_1234567890123456789012345",
    "management.tracing.sampling.probability=0.0"
})
class CatalogEditorialIT {
    static final String EMPLOYEE="test_employee_123456789012345678901234";
    static final String GUEST="test_guest_123456789012345678901234567";
    static final String INTERNAL="test_service_1234567890123456789012345";
    @Container static MongoDBContainer mongo=new MongoDBContainer("mongo:8.0.26");
    @DynamicPropertySource static void database(DynamicPropertyRegistry r) {
        r.add("spring.mongodb.uri",mongo::getConnectionString);
        r.add("spring.mongodb.database",() -> "catalog_editorial_it");
    }
    @Autowired Environment env; @Autowired ObjectMapper json; @Autowired MongoTemplate template;
    @BeforeEach void clean() {
        // Preserve the indexes established by the application, unlike dropCollection.
        template.getCollection("product_revisions").deleteMany(new org.bson.Document());
        template.getCollection("product_drafts").deleteMany(new org.bson.Document());
    }
    HttpResponse<String> request(String path,String token,String body)throws Exception {
        var builder=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+env.getRequiredProperty("local.server.port")+path)).timeout(Duration.ofSeconds(10));
        if(token!=null)builder.header("Authorization","Bearer "+token);
        if(body==null)builder.GET();else builder.header("Content-Type","application/json").POST(HttpRequest.BodyPublishers.ofString(body));
        try(var client=HttpClient.newHttpClient()) { return client.send(builder.build(),HttpResponse.BodyHandlers.ofString()); }
    }
    String draft(long previous,long base,String name)throws Exception {
        return json.writeValueAsString(new CatalogEditorial.SaveDraft(previous,base,
            new CatalogEditorial.Content(name,Catalog.Kind.EVENT,Set.of(Catalog.Variant.ADULT,Catalog.Variant.CHILD))));
    }
    CatalogEditorial.Draft save(long previous,long base,String name)throws Exception {
        var response=request("/api/admin/products/night/drafts",EMPLOYEE,draft(previous,base,name));
        assertEquals(201,response.statusCode(),response.body());
        assertTrue(response.headers().firstValue("Cache-Control").orElse("").contains("no-store"));
        return json.readValue(response.body(),CatalogEditorial.Draft.class);
    }
    CatalogEditorial.Publication publish(long draft,long base)throws Exception {
        var response=request("/api/admin/products/night/publications",EMPLOYEE,
            "{\"draftVersion\":"+draft+",\"expectedPublishedRevision\":"+base+"}");
        assertEquals(201,response.statusCode(),response.body());
        return json.readValue(response.body(),CatalogEditorial.Publication.class);
    }
    @Test void actualFiltersDenyAnonymousGuestAndServiceEditorialAccess()throws Exception {
        for(String path:List.of("/api/admin/products/night/drafts/latest","/api/admin/products/night/history")) {
            assertEquals(401,request(path,null,null).statusCode());
            assertEquals(403,request(path,GUEST,null).statusCode());assertEquals(403,request(path,INTERNAL,null).statusCode());
        }
        assertEquals(403,request("/api/admin/products/night/drafts",GUEST,draft(0,0,"Hidden")).statusCode());
        assertEquals(401,request("/api/admin/products/night/drafts",null,draft(0,0,"Hidden")).statusCode());
        assertEquals(403,request("/api/admin/products/night/publications",GUEST,"{\"draftVersion\":1,\"expectedPublishedRevision\":0}").statusCode());
        assertEquals(0L,template.getCollection("product_drafts").countDocuments());
    }
    @Test void guestRestAndGraphqlCannotReadDrafts()throws Exception {
        save(0,0,"Hidden");assertEquals(404,request("/api/products/night",null,null).statusCode());
        assertEquals("[]",request("/api/products",null,null).body());
        var graph=request("/graphql",null,"{\"query\":\"{products{id name}}\"}");assertEquals(200,graph.statusCode());
        assertTrue(json.readTree(graph.body()).path("data").path("products").isEmpty());assertFalse(graph.body().contains("Hidden"));
        publish(1,0);assertEquals(200,request("/api/products/night",null,null).statusCode());
        var publicBody=request("/api/products/night",null,null).body();assertFalse(publicBody.contains("audit"));assertFalse(publicBody.contains("employee"));
    }
    @Test void actorAndTimestampAreServerOwnedAndStoredWithSnapshot()throws Exception {
        var saved=save(0,0,"One");var published=publish(1,0);
        assertEquals("employee",saved.audit().actor());assertEquals("employee",published.audit().actor());assertNotNull(published.audit().occurredAt());
        assertEquals(CatalogEditorial.Operation.PUBLISH_DRAFT,published.audit().operation());
        var document=template.getCollection("product_revisions").find(new org.bson.Document("_id","night:1")).first();
        assertNotNull(document);assertNotNull(document.get("audit"));assertFalse(document.toJson().contains(EMPLOYEE));
        var forged=draft(1,1,"Forged");forged=forged.substring(0,forged.length()-1)+",\"actor\":\"someone-else\"}";
        assertEquals(400,request("/api/admin/products/night/drafts",EMPLOYEE,forged).statusCode());
        assertEquals(1L,template.getCollection("product_drafts").countDocuments());
    }
    @Test void reviewedDraftIsPublishedEvenWhenANewerDraftExists()throws Exception {
        var first=save(0,0,"Reviewed");var later=save(1,0,"Later edit");var publication=publish(1,0);
        assertEquals("Reviewed",publication.product().name());assertEquals(Long.valueOf(1),publication.sourceDraftVersion());
        var latest=request("/api/admin/products/night/drafts/latest",EMPLOYEE,null);
        assertEquals(later,json.readValue(latest.body(),CatalogEditorial.Draft.class));
        assertEquals(first,json.readValue(request("/api/admin/products/night/drafts/1",EMPLOYEE,null).body(),CatalogEditorial.Draft.class));
        assertEquals(409,request("/api/admin/products/night/publications",EMPLOYEE,"{\"draftVersion\":2,\"expectedPublishedRevision\":0}").statusCode());
    }
    @Test void staleWritesAre409AndNeverSilentlyRebase()throws Exception {
        save(0,0,"One");publish(1,0);
        assertEquals(409,request("/api/admin/products/night/drafts",EMPLOYEE,draft(0,1,"Wrong draft predecessor")).statusCode());
        assertEquals(409,request("/api/admin/products/night/drafts",EMPLOYEE,draft(1,0,"Wrong published base")).statusCode());
        assertEquals(409,request("/api/admin/products/night/publications",EMPLOYEE,"{\"draftVersion\":1,\"expectedPublishedRevision\":1}").statusCode());
        assertEquals(1L,template.getCollection("product_revisions").countDocuments());
        save(1,1,"Rebased");assertEquals(2L,publish(2,1).product().revision());
    }
    @Test void historyHttpCursorIsPinnedAndBadInputsAre400()throws Exception {
        save(0,0,"One");publish(1,0);save(1,1,"Two");publish(2,1);
        var first=request("/api/admin/products/night/history?limit=1",EMPLOYEE,null);
        var page=json.readValue(first.body(),CatalogEditorial.HistoryPage.class);assertNotNull(page.nextCursor());
        save(2,2,"Three");publish(3,2);
        var second=request("/api/admin/products/night/history?limit=1&cursor="+page.nextCursor(),EMPLOYEE,null);
        var tail=json.readValue(second.body(),CatalogEditorial.HistoryPage.class);
        assertEquals(2L,tail.snapshotRevision());assertEquals(1L,tail.items().getFirst().product().revision());assertNull(tail.nextCursor());
        assertTrue(second.headers().firstValue("Cache-Control").orElse("").contains("no-store"));
        for(String query:List.of("limit=0","limit=51","limit=text","cursor=","cursor=bad"))
            assertEquals(400,request("/api/admin/products/night/history?"+query,EMPLOYEE,null).statusCode());
        assertEquals(400,request("/api/admin/products/other/history?cursor="+page.nextCursor(),EMPLOYEE,null).statusCode());
    }
    @Test void explicitVersionFieldsAreRequiredAndFractionalVersionsRejected()throws Exception {
        for(String body:List.of("{}","{\"content\":{\"name\":\"X\",\"kind\":\"EVENT\",\"variants\":[\"ADULT\"]}}",
            "{\"expectedDraftVersion\":null,\"basePublishedRevision\":0,\"content\":null}",
            "{\"expectedDraftVersion\":0.5,\"basePublishedRevision\":0,\"content\":{\"name\":\"X\",\"kind\":\"EVENT\",\"variants\":[\"ADULT\"]}}"))
            assertEquals(400,request("/api/admin/products/night/drafts",EMPLOYEE,body).statusCode());
        save(0,0,"One");
        assertEquals(400,request("/api/admin/products/night/publications",EMPLOYEE,"{\"draftVersion\":1}").statusCode());
        assertEquals(400,request("/api/admin/products/night/publications",EMPLOYEE,"{\"draftVersion\":1,\"expectedPublishedRevision\":null}").statusCode());
        assertEquals(404,request("/api/admin/products/night/publications",EMPLOYEE,"{\"draftVersion\":99,\"expectedPublishedRevision\":0}").statusCode());
        assertEquals(0L,template.getCollection("product_revisions").countDocuments());
    }
    @Test void legacyImmediatePublicationRouteRetainsDtoAndAddsAudit()throws Exception {
        var original=new Catalog.Product("night",1,"Immediate",Catalog.Kind.EVENT,Set.of(Catalog.Variant.ADULT));
        var response=request("/api/admin/products",EMPLOYEE,json.writeValueAsString(original));assertEquals(201,response.statusCode());
        assertEquals(original,json.readValue(response.body(),Catalog.Product.class));
        var history=json.readValue(request("/api/admin/products/night/history",EMPLOYEE,null).body(),CatalogEditorial.HistoryPage.class);
        assertEquals(CatalogEditorial.Operation.DIRECT_PUBLISH,history.items().getFirst().audit().operation());
        assertEquals("employee",history.items().getFirst().audit().actor());
    }
}
