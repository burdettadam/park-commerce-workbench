package dev.parkcommerce.booking;

import dev.parkcommerce.core.*;
import java.net.URI;
import java.net.http.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.*;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.mysql.MySQLContainer;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.*;

/** Real Spring/MySQL HTTP tests with a controlled clock and a test-double pricing
 * client. No disabledWithoutDocker flag: unavailable infrastructure must fail.
 */
@Testcontainers
@Import(CheckoutRecoveryIT.RecoveryInputs.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,properties={
    "spring.profiles.active=local",
    "DEMO_EMPLOYEE_TOKEN=test_employee_123456789012345678901234",
    "DEMO_GUEST_TOKEN=test_guest_123456789012345678901234567",
    "DEMO_OTHER_GUEST_TOKEN=test_other_123456789012345678901234567",
    "DEMO_SERVICE_TOKEN=test_service_1234567890123456789012345",
    "management.tracing.sampling.probability=0.0"
})
class CheckoutRecoveryIT {
    static final Instant NOW=Instant.parse("2030-10-01T12:00:00Z");
    static final String GUEST="test_guest_123456789012345678901234567";
    static final String OTHER="test_other_123456789012345678901234567";
    static final String EMPLOYEE="test_employee_123456789012345678901234";
    @Container static MySQLContainer mysql=new MySQLContainer("mysql:8.4.11");
    @DynamicPropertySource static void database(DynamicPropertyRegistry r){
        r.add("spring.datasource.url",mysql::getJdbcUrl);r.add("spring.datasource.username",mysql::getUsername);r.add("spring.datasource.password",mysql::getPassword);
    }
    static final class RecoveryClock extends Clock {
        final AtomicReference<Instant> now=new AtomicReference<>(NOW);
        @Override public Instant instant(){return now.get();}
        @Override public ZoneId getZone(){return ZoneOffset.UTC;}
        @Override public Clock withZone(ZoneId zone){return Clock.fixed(now.get(),zone);}
    }
    static final class QuoteFixture implements Pricing.Client {
        final Map<UUID,Pricing.Quote> values=new ConcurrentHashMap<>();
        @Override public Pricing.Quote quote(UUID id){return values.get(id);}
    }
    @TestConfiguration(proxyBeanMethods=false) static class RecoveryInputs {
        @Bean @Primary RecoveryClock recoveryClock(){return new RecoveryClock();}
        @Bean @Primary QuoteFixture recoveryPricing(){return new QuoteFixture();}
    }
    @Autowired Booking.Service service; @Autowired JdbcTemplate jdbc;
    @Autowired Environment env; @Autowired ObjectMapper json;
    @Autowired RecoveryClock clock; @Autowired QuoteFixture quotes;
    @BeforeEach void setup(){
        jdbc.update("DELETE FROM orders");jdbc.update("DELETE FROM holds");jdbc.update("DELETE FROM events");
        clock.now.set(NOW);quotes.values.clear();service.create(new Booking.Event("night","night",LocalDate.of(2030,10,31),3));
    }
    Pricing.Quote quote(){
        var quote=new Pricing.Quote(UUID.randomUUID(),"guest","night","Night",Catalog.Kind.EVENT,1,LocalDate.of(2030,10,31),1,1,
            List.of(new Pricing.Line(Catalog.Variant.ADULT,1,new Money(10000),new Money(10000),new Money(0),new Money(10000))),
            null,new Money(10000),new Money(0),new Money(10000),NOW,NOW.plusSeconds(900));
        quotes.values.put(quote.id(),quote);return quote;
    }
    Booking.Hold hold(){return service.reserve("night",quote().id(),"guest");}
    HttpResponse<String> reserve(Pricing.Quote quote)throws Exception {
        var builder=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+env.getRequiredProperty("local.server.port")+"/api/holds"))
            .timeout(Duration.ofSeconds(10)).header("Authorization","Bearer "+GUEST).header("Content-Type","application/json")
            .POST(HttpRequest.BodyPublishers.ofString("{\"eventId\":\"night\",\"quoteId\":\""+quote.id()+"\"}"));
        try(var client=HttpClient.newHttpClient()){return client.send(builder.build(),HttpResponse.BodyHandlers.ofString());}
    }
    HttpResponse<String> request(String path,String token,String key,UUID hold)throws Exception {
        var builder=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+env.getRequiredProperty("local.server.port")+path)).timeout(Duration.ofSeconds(10));
        if(token!=null)builder.header("Authorization","Bearer "+token);
        if(key!=null)builder.header("Idempotency-Key",key);
        if(hold!=null)builder.header("Content-Type","application/json").POST(HttpRequest.BodyPublishers.ofString("{\"holdId\":\""+hold+"\"}"));
        else builder.GET();
        try(var client=HttpClient.newHttpClient()){return client.send(builder.build(),HttpResponse.BodyHandlers.ofString());}
    }
    Booking.Recovery recover(Booking.Hold h)throws Exception {
        var response=request("/api/orders/recovery",GUEST,"same",h.id());assertEquals(200,response.statusCode(),response.body());
        assertTrue(response.headers().firstValue("Cache-Control").orElse("").contains("no-store"));
        return json.readValue(response.body(),Booking.Recovery.class);
    }
    @Test void sessionUsesAuthenticatedGuestSubjectNotCallerInput()throws Exception {
        var response=request("/api/session",GUEST,null,null);assertEquals(200,response.statusCode());
        assertEquals(Map.of("subject","guest"),json.readValue(response.body(),Map.class));assertFalse(response.body().contains(GUEST));
        assertEquals(401,request("/api/session",null,null,null).statusCode());
        assertEquals(403,request("/api/session",EMPLOYEE,null,null).statusCode());
    }
    @Test void activeRecoveryCannotConfirmAnOrder()throws Exception {
        var h=hold();var recovered=recover(h);assertEquals(h,recovered.hold());assertNull(recovered.order());
        assertEquals(0,jdbc.queryForObject("SELECT COUNT(*) FROM orders",Integer.class));assertEquals(2,service.availability("night").available());
    }
    @Test void authenticationAndOwnershipAreCheckedByRealHttpFilters()throws Exception {
        var h=hold();assertEquals(401,request("/api/orders/recovery",null,"same",h.id()).statusCode());
        assertEquals(404,request("/api/orders/recovery",OTHER,"same",h.id()).statusCode());
        assertEquals(Booking.HoldStatus.ACTIVE,recover(h).hold().status());
        assertEquals(0,jdbc.queryForObject("SELECT COUNT(*) FROM orders",Integer.class));
    }
    @Test void discardedResponseCanBeRecoveredAfterQuoteExpiry()throws Exception {
        var h=hold();assertEquals(200,request("/api/orders",GUEST,"same",h.id()).statusCode()); // Deliberately discard receipt.
        clock.now.set(NOW.plusSeconds(3600));quotes.values.clear();
        var recovered=recover(h);assertNotNull(recovered.order());assertEquals(Booking.HoldStatus.CONFIRMED,recovered.hold().status());
        var replay=request("/api/orders",GUEST,"same",h.id());assertEquals(200,replay.statusCode());
        assertEquals(recovered.order(),json.readValue(replay.body(),Booking.Order.class));
        assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM orders",Integer.class));assertEquals(2,service.availability("night").available());
    }
    @Test void expiredRecoveryPersistsExpiryWithoutCreatingAnOrder()throws Exception {
        var h=hold();clock.now.set(NOW.plusSeconds(300));var state=recover(h);
        assertEquals(Booking.HoldStatus.EXPIRED,state.hold().status());assertNull(state.order());assertEquals(state,recover(h));
        assertEquals("EXPIRED",jdbc.queryForObject("SELECT status FROM holds WHERE id=?",String.class,h.id().toString()));
        assertEquals(0,jdbc.queryForObject("SELECT COUNT(*) FROM orders",Integer.class));assertEquals(3,service.availability("night").available());
    }
    @Test void newHoldAtExactQuoteExpiryHasPublicExpiredErrorShape()throws Exception {
        var justBefore=quote();clock.now.set(justBefore.expiresAt().minusNanos(1));
        var accepted=reserve(justBefore);assertEquals(201,accepted.statusCode(),accepted.body());
        var atExpiry=quote();clock.now.set(atExpiry.expiresAt());
        var rejected=reserve(atExpiry);assertEquals(410,rejected.statusCode(),rejected.body());
        var problem=json.readValue(rejected.body(),Map.class);
        assertEquals("EXPIRED",problem.get("code"));assertEquals("EXPIRED",problem.get("title"));
        assertEquals(410,((Number)problem.get("status")).intValue());
        assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM holds",Integer.class));
    }
    @Test void changedKeysAndChangedHoldIdentityAreConflicts()throws Exception {
        var h=hold();service.confirm(h.id(),"guest","same");var second=hold();
        assertEquals(409,request("/api/orders/recovery",GUEST,"different",h.id()).statusCode());
        assertEquals(409,request("/api/orders/recovery",GUEST,"same",second.id()).statusCode());
        assertEquals(400,request("/api/orders/recovery",GUEST,"bad key",h.id()).statusCode());
        assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM orders",Integer.class));
    }
    @Test void interleavedHttpRecoveryAndConfirmationProduceOneOrder()throws Exception {
        var h=hold();
        try(var pool=Executors.newFixedThreadPool(6)) {
            var tasks=new ArrayList<Callable<Integer>>();
            for(int i=0;i<18;i++){final String path=i%3==0?"/api/orders":"/api/orders/recovery";tasks.add(()->request(path,GUEST,"same",h.id()).statusCode());}
            for(var result:pool.invokeAll(tasks,30,TimeUnit.SECONDS))assertEquals(200,result.get());
        }
        assertNotNull(recover(h).order());assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM orders",Integer.class));
        assertEquals(2,service.availability("night").available());
    }
}
