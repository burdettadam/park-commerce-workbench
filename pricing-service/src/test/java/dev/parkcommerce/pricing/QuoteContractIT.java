package dev.parkcommerce.pricing;

import dev.parkcommerce.core.*;
import java.net.URI;
import java.net.http.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.test.context.*;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.mysql.MySQLContainer;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.*;

/** Real MySQL + Spring HTTP/GraphQL tests; catalog and clock are explicit controlled fixtures.
 * Do not count these as executed until Maven and Docker actually run this class.
 */
@Testcontainers
@Import(QuoteContractIT.Fixtures.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, properties={
    "spring.profiles.active=local",
    "DEMO_EMPLOYEE_TOKEN=test_employee_123456789012345678901234",
    "DEMO_GUEST_TOKEN=test_guest_123456789012345678901234567",
    "DEMO_OTHER_GUEST_TOKEN=test_other_123456789012345678901234567",
    "DEMO_SERVICE_TOKEN=test_service_1234567890123456789012345",
    "management.tracing.sampling.probability=0.0"
})
class QuoteContractIT {
    private static final String GUEST="test_guest_123456789012345678901234567";
    private static final String OTHER="test_other_123456789012345678901234567";
    private static final String EMPLOYEE="test_employee_123456789012345678901234";
    private static final String INTERNAL="test_service_1234567890123456789012345";
    private static final LocalDate DAY=LocalDate.of(2030,10,31);
    private static final String STATUS="query($id:ID!){quoteStatus(id:$id){state evaluatedAt parkTimeZone quote{id productName catalogRevision pricingVersion createdAt expiresAt kind total{cents amount currency}}}}";
    @Container static MySQLContainer mysql=new MySQLContainer("mysql:8.4.11");
    @DynamicPropertySource static void database(DynamicPropertyRegistry r){
        r.add("spring.datasource.url",mysql::getJdbcUrl);r.add("spring.datasource.username",mysql::getUsername);r.add("spring.datasource.password",mysql::getPassword);
    }
    static final class TestClock extends Clock {
        final AtomicReference<Instant> now=new AtomicReference<>(Instant.parse("2030-10-01T12:00:00Z"));
        @Override public ZoneId getZone(){return ZoneOffset.UTC;}
        @Override public Clock withZone(ZoneId zone){return Clock.fixed(now.get(),zone);}
        @Override public Instant instant(){return now.get();}
    }
    static final class FixtureCatalog implements Catalog.Client {
        volatile boolean unavailable;
        @Override public Catalog.Product revision(String id,long revision){
            if(unavailable)throw new Failure(Failure.Code.DEPENDENCY_UNAVAILABLE,"Catalog unavailable");
            if(!id.equals("night") || revision<1 || revision>2)throw new Failure(Failure.Code.NOT_FOUND,"No fixture product");
            return new Catalog.Product("night",revision,"Night "+revision,Catalog.Kind.EVENT,EnumSet.allOf(Catalog.Variant.class));
        }
    }
    @TestConfiguration(proxyBeanMethods=false) static class Fixtures {
        @Bean @Primary TestClock contractClock(){return new TestClock();}
        @Bean @Primary FixtureCatalog contractCatalog(){return new FixtureCatalog();}
    }
    @Autowired Pricing.Service service; @Autowired PriceBooks books; @Autowired Quotes quotes;
    @Autowired TestClock clock; @Autowired FixtureCatalog catalog; @Autowired ObjectMapper json; @Autowired Environment environment;
    @BeforeEach void reset(){
        quotes.deleteAll();books.deleteAll();catalog.unavailable=false;clock.now.set(Instant.parse("2030-10-01T12:00:00Z"));
        service.publish(book(1,1,5));
    }
    private Pricing.PriceBook book(long version,long revision,long cents){
        return new Pricing.PriceBook("night",version,revision,List.of(
            new Pricing.Rate(Catalog.Variant.ADULT,DAY,DAY,new Money(cents)),new Pricing.Rate(Catalog.Variant.CHILD,DAY,DAY,new Money(cents))),new Pricing.Offer("tiny",1,1000));
    }
    private Pricing.Quote quote(){return service.create(new Pricing.Request("night",DAY,1,1),"guest");}
    private Map<String,Object> input(String day){return Map.of("productId","night","visitDate",day,"adults",1,"children",1);}

    @Test void exactExpiryIsObservedWithoutUpdatingStoredSnapshot() throws Exception {
        var q=quote();clock.now.set(q.expiresAt().minusNanos(1));
        var before=json.readValue(send("/api/quotes/"+q.id()+"/status",GUEST,null).body(),Pricing.QuoteStatus.class);
        assertEquals(Pricing.QuoteState.UNEXPIRED,before.state());assertEquals(q,before.quote());
        clock.now.set(q.expiresAt());var response=send("/api/quotes/"+q.id()+"/status",GUEST,null);assertEquals(200,response.statusCode());
        var at=json.readValue(response.body(),Pricing.QuoteStatus.class);
        assertEquals(Pricing.QuoteState.EXPIRED,at.state());assertEquals(q.expiresAt(),at.evaluatedAt());assertEquals(q,at.quote());
        assertEquals(q,json.readValue(send("/api/quotes/"+q.id(),GUEST,null).body(),Pricing.Quote.class));assertEquals(1L,quotes.count());
    }
    @Test void statusReadHidesOtherOwnersAndRequiresAppropriateAuthentication() throws Exception {
        var q=quote();String path="/api/quotes/"+q.id()+"/status";
        assertEquals(401,send(path,null,null).statusCode());assertEquals(403,send(path,INTERNAL,null).statusCode());
        for(String token:List.of(OTHER,EMPLOYEE)){var response=send(path,token,null);assertEquals(404,response.statusCode());assertEquals("NOT_FOUND",body(response).get("code"));}
        assertEquals(404,send("/api/quotes/"+UUID.randomUUID()+"/status",GUEST,null).statusCode());
    }
    @Test void graphStatusAgreesWithRestAndPreservesStringMoneyAndVersionFields() throws Exception {
        var q=quote();var result=graph(STATUS,Map.of("id",q.id().toString()),GUEST);assertFalse(result.containsKey("errors"),result.toString());
        var status=map(map(result,"data"),"quoteStatus");var snapshot=map(status,"quote");
        assertEquals("UNEXPIRED",status.get("state"));assertEquals("America/New_York",status.get("parkTimeZone"));
        assertEquals(q.createdAt().toString(),snapshot.get("createdAt"));assertEquals("EVENT",snapshot.get("kind"));
        assertEquals("1",snapshot.get("catalogRevision"));assertEquals("1",snapshot.get("pricingVersion"));
        assertEquals("8",map(snapshot,"total").get("cents"));assertEquals("0.08",map(snapshot,"total").get("amount"));
        assertEquals("USD",map(snapshot,"total").get("currency"));
        assertEquals(q.id().toString(),snapshot.get("id"));assertEquals(q.expiresAt().toString(),snapshot.get("expiresAt"));
    }
    @Test void graphStatusReportsExactExpiryAndSameHistoricalQuote() throws Exception {
        var q=quote();clock.now.set(q.expiresAt());var result=graph(STATUS,Map.of("id",q.id().toString()),GUEST);
        assertFalse(result.containsKey("errors"),result.toString());var status=map(map(result,"data"),"quoteStatus");
        assertEquals("EXPIRED",status.get("state"));assertEquals(q.expiresAt().toString(),status.get("evaluatedAt"));
        assertEquals(q.id().toString(),map(status,"quote").get("id"));assertEquals(1L,quotes.count());
    }
    @Test void graphStatusAuthorizationPreventsCrossAccountAndInternalReads() throws Exception {
        var q=quote();for(String token:List.of(OTHER,EMPLOYEE))assertEquals("NOT_FOUND",graphCode(graph(STATUS,Map.of("id",q.id().toString()),token)));
        assertEquals("FORBIDDEN",graphCode(graph(STATUS,Map.of("id",q.id().toString()),INTERNAL)));
        assertTrue(graph(STATUS,Map.of("id",q.id().toString()),null).containsKey("errors"));
    }
    @Test void malformedDatesAndIdsShareDomainErrorCodeAcrossTransports() throws Exception {
        for(String day:List.of("2030-02-29","2030-2-03","2030-10-31T00:00:00Z","+10000-01-01","0000-01-01")){
            var response=send("/api/quotes",GUEST,input(day));assertEquals(400,response.statusCode(),response.body());assertEquals("INVALID_INPUT",body(response).get("code"));
            assertEquals("INVALID_INPUT",graphCode(graph("mutation($input:QuoteInput!){createQuote(input:$input){id}}",Map.of("input",input(day)),GUEST)));
        }
        for(String id:List.of("1-1-1-1-1","not-a-uuid")){
            for(String suffix:List.of("","/status")){var response=send("/api/quotes/"+id+suffix,GUEST,null);assertEquals(400,response.statusCode());assertEquals("INVALID_INPUT",body(response).get("code"));}
            assertEquals("INVALID_INPUT",graphCode(graph(STATUS,Map.of("id",id),GUEST)));
            assertEquals("INVALID_INPUT",graphCode(graph("query($id:ID!){quote(id:$id){id}}",Map.of("id",id),GUEST)));
        }
        assertEquals(0L,quotes.count());
    }
    @Test void missingOrFractionalQuantityIsRejectedWithoutCreatingQuotes() throws Exception {
        for(Object bad:List.of(Map.of("productId","night","visitDate",DAY.toString(),"adults",1),
            Map.of("productId","night","visitDate",DAY.toString(),"adults",1.5,"children",0))){
            assertEquals(400,send("/api/quotes",GUEST,bad).statusCode());
            // GraphQL scalar/schema failures occur before the domain resolver; no domain code is promised.
            assertTrue(graph("mutation($input:QuoteInput!){createQuote(input:$input){id}}",Map.of("input",bad),GUEST).containsKey("errors"));
        }
        assertEquals(0L,quotes.count());
    }
    @Test void realJsonAndGraphQlProduceTheIndependentHalfCentFixture() throws Exception {
        var response=send("/api/quotes",GUEST,input(DAY.toString()));assertEquals(201,response.statusCode(),response.body());
        var rest=json.readValue(response.body(),Pricing.Quote.class);assertEquals(10L,rest.subtotal().cents());assertEquals(2L,rest.discount().cents());assertEquals(8L,rest.total().cents());
        var result=graph("mutation($input:QuoteInput!){createQuote(input:$input){id subtotal{cents} discount{cents} total{cents} lines{variant quantity discount{cents}}}}",Map.of("input",input(DAY.toString())),GUEST);
        assertFalse(result.containsKey("errors"),result.toString());var value=map(map(result,"data"),"createQuote");
        assertEquals("10",map(value,"subtotal").get("cents"));assertEquals("2",map(value,"discount").get("cents"));assertEquals("8",map(value,"total").get("cents"));assertEquals(2L,quotes.count());
    }
    @Test void priorQuoteVersionsAndNamesSurviveLaterPricePublication() throws Exception {
        var q=quote();service.publish(book(2,2,10000));catalog.unavailable=true;
        var result=graph(STATUS,Map.of("id",q.id().toString()),GUEST);assertFalse(result.containsKey("errors"),result.toString());
        var snapshot=map(map(map(result,"data"),"quoteStatus"),"quote");
        assertEquals("1",snapshot.get("pricingVersion"));assertEquals("1",snapshot.get("catalogRevision"));assertEquals("Night 1",snapshot.get("productName"));assertEquals("8",map(snapshot,"total").get("cents"));
        assertEquals(q,json.readValue(send("/api/internal/quotes/"+q.id(),INTERNAL,null).body(),Pricing.Quote.class));
    }
    @Test void expiredAndUnknownAreDifferentButUnknownAndOtherOwnerAreNot() throws Exception {
        var q=quote();clock.now.set(q.expiresAt().plusSeconds(1));
        var status=graph(STATUS,Map.of("id",q.id().toString()),GUEST);assertFalse(status.containsKey("errors"));
        assertEquals("EXPIRED",map(map(status,"data"),"quoteStatus").get("state"));
        assertEquals("NOT_FOUND",graphCode(graph(STATUS,Map.of("id",UUID.randomUUID().toString()),GUEST)));
        assertEquals("NOT_FOUND",graphCode(graph(STATUS,Map.of("id",q.id().toString()),OTHER)));
    }
    @Test void missingRateAndUnavailableCatalogHaveStableDifferentErrorCodes() throws Exception {
        var missing=send("/api/quotes",GUEST,input("2030-11-01"));assertEquals(404,missing.statusCode());assertEquals("NOT_FOUND",body(missing).get("code"));
        catalog.unavailable=true;var response=send("/api/quotes",GUEST,input(DAY.toString()));assertEquals(503,response.statusCode());
        assertEquals("DEPENDENCY_UNAVAILABLE",body(response).get("code"));
        assertEquals("DEPENDENCY_UNAVAILABLE",graphCode(graph("mutation($input:QuoteInput!){createQuote(input:$input){id}}",Map.of("input",input(DAY.toString())),GUEST)));
        assertEquals(0L,quotes.count());
    }
    @Test void parkLocalMidnightEligibilityIsConsistentThroughHttp() throws Exception {
        clock.now.set(Instant.parse("2030-11-01T03:59:59.999Z"));assertEquals(201,send("/api/quotes",GUEST,input(DAY.toString())).statusCode());
        clock.now.set(Instant.parse("2030-11-01T04:00:00Z"));assertEquals(400,send("/api/quotes",GUEST,input(DAY.toString())).statusCode());
        assertEquals("INVALID_INPUT",graphCode(graph("mutation($input:QuoteInput!){createQuote(input:$input){id}}",Map.of("input",input(DAY.toString())),GUEST)));
        assertEquals(1L,quotes.count());
    }
    private HttpResponse<String> send(String path,String token,Object payload) throws Exception {
        var builder=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+environment.getRequiredProperty("local.server.port")+path)).timeout(Duration.ofSeconds(10));
        if(token!=null)builder.header("Authorization","Bearer "+token);
        if(payload==null)builder.GET();else builder.header("Content-Type","application/json").POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(payload)));
        try(var client=HttpClient.newHttpClient()){return client.send(builder.build(),HttpResponse.BodyHandlers.ofString());}
    }
    @SuppressWarnings("unchecked") private Map<String,Object> body(HttpResponse<String> response) throws Exception {return json.readValue(response.body(),Map.class);}
    private Map<String,Object> graph(String query,Map<String,?> variables,String token) throws Exception {
        var response=send("/graphql",token,Map.of("query",query,"variables",variables));assertEquals(200,response.statusCode(),response.body());return body(response);
    }
    private static Map<?,?> map(Map<?,?> from,String key){assertInstanceOf(Map.class,from.get(key),key);return (Map<?,?>)from.get(key);}
    private static String graphCode(Map<?,?> result){
        var errors=assertInstanceOf(List.class,result.get("errors"),result.toString());assertFalse(errors.isEmpty());
        var first=assertInstanceOf(Map.class,errors.get(0));return String.valueOf(map(first,"extensions").get("code"));
    }
}
