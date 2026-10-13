package dev.parkcommerce.pricing;
import dev.parkcommerce.core.*;
import java.net.URI;
import java.net.http.*;
import java.sql.SQLException;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import tools.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;
import org.springframework.test.context.*;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.mysql.MySQLContainer;
import static org.junit.jupiter.api.Assertions.*;
@Testcontainers
@Import(JpaPricingIT.TestCatalog.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,properties={
"spring.profiles.active=local",
    "DEMO_EMPLOYEE_TOKEN=test_employee_123456789012345678901234",
    "DEMO_GUEST_TOKEN=test_guest_123456789012345678901234567",
    "DEMO_OTHER_GUEST_TOKEN=test_other_123456789012345678901234567",
    "DEMO_SERVICE_TOKEN=test_service_1234567890123456789012345",
    "management.tracing.sampling.probability=0.0"
})
class JpaPricingIT {
    @Container static MySQLContainer mysql=new MySQLContainer("mysql:8.4.11");
    @DynamicPropertySource static void database(DynamicPropertyRegistry r){
        r.add("spring.datasource.url",mysql::getJdbcUrl);r.add("spring.datasource.username",mysql::getUsername);r.add("spring.datasource.password",mysql::getPassword);
    }
    @Autowired JpaPricingStore store;@Autowired PriceBooks books;@Autowired Quotes quotes;@Autowired Environment environment;
    @Autowired JdbcTemplate jdbc;
    @Autowired ObjectMapper json;
    @TestConfiguration(proxyBeanMethods=false)
    static class TestCatalog {
        @Bean @Primary Catalog.Client previewCatalog() {
            return (id, revision) -> {
                if (!id.equals("night") || revision != 1) throw new Failure(Failure.Code.NOT_FOUND,"Unknown fixture product");
                return new Catalog.Product("night",1,"Night",Catalog.Kind.EVENT,Set.of(Catalog.Variant.ADULT));
            };
        }
        @Bean @Primary Clock previewClock() { return Clock.fixed(Instant.parse("2030-10-01T12:00:00Z"), ZoneOffset.UTC); }
    }
    Pricing.Service service;LocalDate day=LocalDate.of(2030,10,31);
    @BeforeEach void setup(){
        quotes.deleteAll();books.deleteAll();
        Catalog.Product product=new Catalog.Product("night",1,"Night",Catalog.Kind.EVENT,Set.of(Catalog.Variant.ADULT));
        service=new Pricing.Service(store,(id,version)->product,Clock.fixed(Instant.parse("2030-10-01T12:00:00Z"),ZoneOffset.UTC),ZoneId.of("America/New_York"));
    }
    Pricing.PriceBook book(long version,long price){return new Pricing.PriceBook("night",version,1,List.of(new Pricing.Rate(Catalog.Variant.ADULT,day,day,new Money(price))),null);}
    @Test void immutableQuoteAndMoneyRoundTrip(){
        service.publish(book(1,10000));var q=service.create(new Pricing.Request("night",day,1,0),"guest");
        assertEquals(book(1,10000),store.latest("night").orElseThrow());
        assertEquals(day,q.visitDate());assertEquals(1,q.catalogRevision());assertEquals(1,q.pricingVersion());
        assertEquals(new Money(10000),q.total());assertEquals(q,store.quote(q.id()).orElseThrow());
        service.publish(book(2,12500));assertEquals(q,service.get(q.id(),"guest"));
        assertEquals(2,store.latest("night").orElseThrow().version());
        var newer=service.create(new Pricing.Request("night",day,1,0),"guest");
        assertEquals(day,newer.visitDate());assertEquals(2,newer.pricingVersion());assertEquals(new Money(12500),newer.total());
    }
    @Test void emptyDatabaseMigrationCreatesPricingConstraints(){
        assertEquals(List.of("1"),jdbc.queryForList("SELECT version FROM flyway_schema_history WHERE success=1 ORDER BY installed_rank",String.class));
        assertEquals("InnoDB",jdbc.queryForObject("SELECT ENGINE FROM information_schema.TABLES WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='price_books'",String.class));
        assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='price_books' AND CONSTRAINT_NAME='uk_price_book_version'",Integer.class));
        var violation=assertThrows(DataAccessException.class,()->jdbc.update(
            "INSERT INTO price_books (id,product_id,pricing_version,payload) VALUES (?,?,?,?)",
            UUID.randomUUID().toString(),"night",99,"not json"));
        assertEquals(3819,((SQLException)violation.getRootCause()).getErrorCode());
        assertTrue(violation.getMessage().contains("ck_price_book_json"));
        assertEquals(0,books.count());
    }
    @Test void databasePreventsDuplicateVersion(){
        store.insert(book(1,10000));assertEquals(Failure.Code.CONFLICT,assertThrows(Failure.class,()->store.insert(book(1,12500))).code());
    }
    @Test void httpSecurityRequiresEmployeeForPublication()throws Exception{
        String base="http://127.0.0.1:"+environment.getRequiredProperty("local.server.port");
        try(var client=HttpClient.newHttpClient()){
            var anonymous=client.send(HttpRequest.newBuilder(URI.create(base+"/api/admin/price-books/night")).GET().build(),HttpResponse.BodyHandlers.ofString());
            assertEquals(401,anonymous.statusCode());
            var guest=client.send(HttpRequest.newBuilder(URI.create(base+"/api/admin/price-books/night")).header("Authorization","Bearer test_guest_123456789012345678901234567").GET().build(),HttpResponse.BodyHandlers.ofString());
            assertEquals(403,guest.statusCode());
        }
    }
    @Test void previewLeavesPublishedBooksAndQuotesUnchanged() {
        service.publish(book(1,10000)); var original=service.create(new Pricing.Request("night",day,1,0),"guest");
        var p=service.preview(new Pricing.PreviewRequest(book(2,12500),new Pricing.Request("night",day,1,0)));
        assertEquals(12500,p.total().cents()); assertEquals(1,books.count()); assertEquals(1,quotes.count());
        assertEquals(1,store.latest("night").orElseThrow().version()); assertEquals(original,service.get(original.id(),"guest"));
        service.publish(book(2,12500)); var issued=service.create(p.request(),"guest");
        assertEquals(p.lines(),issued.lines()); assertEquals(p.total(),issued.total());
    }
    @Test void httpPreviewIsEmployeeOnlyAndDoesNotIssueAQuote() throws Exception {
        String payload=json.writeValueAsString(new Pricing.PreviewRequest(book(1,10000),new Pricing.Request("night",day,1,0)));
        assertEquals(401,post("/api/admin/price-books/preview",null,payload).statusCode());
        for(String token:List.of("test_guest_123456789012345678901234567","test_other_123456789012345678901234567","test_service_1234567890123456789012345"))
            assertEquals(403,post("/api/admin/price-books/preview",token,payload).statusCode());
        var response=post("/api/admin/price-books/preview","test_employee_123456789012345678901234",payload);
        assertEquals(200,response.statusCode(),response.body());
        var p=json.readValue(response.body(),Pricing.Preview.class);
        assertEquals(Pricing.PreviewMode.UNPUBLISHED_INPUT,p.mode()); assertEquals(10000,p.total().cents());
        assertTrue(p.candidateId().matches("sha256:[0-9a-f]{64}")); assertFalse(json.readValue(response.body(),Map.class).containsKey("id"));
        assertEquals(0,books.count()); assertEquals(0,quotes.count());
    }
    @Test void httpPreviewAndIssuedQuoteHaveIdenticalCalculation() throws Exception {
        String token="test_employee_123456789012345678901234";
        var input=new Pricing.PreviewRequest(book(1,10000),new Pricing.Request("night",day,2,0));
        var previewResponse=post("/api/admin/price-books/preview",token,json.writeValueAsString(input));
        assertEquals(200,previewResponse.statusCode()); var preview=json.readValue(previewResponse.body(),Pricing.Preview.class);
        assertEquals(201,post("/api/admin/price-books",token,json.writeValueAsString(input.candidate())).statusCode());
        var quoteResponse=post("/api/quotes","test_guest_123456789012345678901234567",json.writeValueAsString(input.request()));
        assertEquals(201,quoteResponse.statusCode()); var q=json.readValue(quoteResponse.body(),Pricing.Quote.class);
        assertEquals(preview.lines(),q.lines()); assertEquals(preview.total(),q.total()); assertNotNull(q.id()); assertNotNull(q.expiresAt());
    }
    @Test void httpPreviewRejectsMalformedAndOverlappingInputsWithoutWrites() throws Exception {
        String token="test_employee_123456789012345678901234";
        var input=new Pricing.PreviewRequest(book(1,10000),new Pricing.Request("night",day,1,0));
        String rate=json.writeValueAsString(input.candidate().rates().get(0));
        String request=json.writeValueAsString(input.request());
        // Deliberately invalid wire payloads; constructing validated records would reject them before HTTP.
        String overlapping="{\"candidate\":{\"productId\":\"night\",\"version\":1,\"catalogRevision\":1,\"rates\":["+rate+","+rate+"],\"offer\":null},\"request\":"+request+"}";
        String fractional="{\"candidate\":"+json.writeValueAsString(input.candidate())+",\"request\":{\"productId\":\"night\",\"visitDate\":\"2030-10-31\",\"adults\":1.5,\"children\":0}}";
        for(String body:List.of("{}","{\"candidate\":null,\"request\":null}",overlapping,fractional))
            assertEquals(400,post("/api/admin/price-books/preview",token,body).statusCode(),body);
        assertEquals(0,books.count()); assertEquals(0,quotes.count());
    }
    private HttpResponse<String> post(String path,String token,String payload) throws Exception {
        var builder=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+environment.getRequiredProperty("local.server.port")+path))
            .timeout(Duration.ofSeconds(10)).header("Content-Type","application/json").POST(HttpRequest.BodyPublishers.ofString(payload));
        if(token!=null)builder.header("Authorization","Bearer "+token);
        try(var client=HttpClient.newHttpClient()){return client.send(builder.build(),HttpResponse.BodyHandlers.ofString());}
    }

}
