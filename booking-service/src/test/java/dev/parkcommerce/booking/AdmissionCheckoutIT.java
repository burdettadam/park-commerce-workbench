package dev.parkcommerce.booking;

import dev.parkcommerce.core.*;
import java.net.URI;
import java.net.http.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.*;
import org.springframework.test.context.*;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.mysql.MySQLContainer;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.*;

/** Real MySQL/Spring HTTP candidate tests. Pricing/clock are controlled test inputs.
 *  No infrastructure skip flag. Missing Docker must fail, not count as a pass.
 */
@Testcontainers
@Import(AdmissionCheckoutIT.Inputs.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, properties={
    "spring.profiles.active=local",
    "DEMO_EMPLOYEE_TOKEN=test_employee_123456789012345678901234",
    "DEMO_GUEST_TOKEN=test_guest_123456789012345678901234567",
    "DEMO_OTHER_GUEST_TOKEN=test_other_123456789012345678901234567",
    "DEMO_SERVICE_TOKEN=test_service_1234567890123456789012345",
    "management.tracing.sampling.probability=0.0"
})
class AdmissionCheckoutIT {
    static final Instant NOW=Instant.parse("2030-10-01T12:00:00Z");
    static final LocalDate VISIT=LocalDate.of(2030,10,31);
    static final String GUEST="test_guest_123456789012345678901234567";
    static final String OTHER="test_other_123456789012345678901234567";
    static final String EMPLOYEE="test_employee_123456789012345678901234";
    @Container static MySQLContainer mysql=new MySQLContainer("mysql:8.4.11");
    @DynamicPropertySource static void database(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url",mysql::getJdbcUrl);r.add("spring.datasource.username",mysql::getUsername);r.add("spring.datasource.password",mysql::getPassword);
    }
    static final class TestClock extends Clock {
        final AtomicReference<Instant> now=new AtomicReference<>(NOW);
        @Override public Instant instant(){return now.get();}
        @Override public ZoneId getZone(){return ZoneOffset.UTC;}
        @Override public Clock withZone(ZoneId zone){return Clock.fixed(now.get(),zone);}
    }
    static final class QuoteFixture implements Pricing.Client {
        final Map<UUID,Pricing.Quote> values=new ConcurrentHashMap<>();
        @Override public Pricing.Quote quote(UUID id){return values.get(id);}
    }
    @TestConfiguration(proxyBeanMethods=false) static class Inputs {
        @Bean @Primary TestClock admissionClock(){return new TestClock();}
        @Bean @Primary QuoteFixture admissionPricing(){return new QuoteFixture();}
    }
    @Autowired Admission.Service service; @Autowired JdbcAdmissionStore store;
    @Autowired JdbcTemplate jdbc; @Autowired Booking.Service events;
    @Autowired Environment env; @Autowired ObjectMapper json;
    @Autowired TestClock clock; @Autowired QuoteFixture quotes;

    @BeforeEach void reset() {
        jdbc.update("DELETE FROM admission_orders");jdbc.update("DELETE FROM orders");jdbc.update("DELETE FROM holds");jdbc.update("DELETE FROM events");
        clock.now.set(NOW);quotes.values.clear();events.create(new Booking.Event("night","night",VISIT,3));
    }
    Pricing.Quote quote(String owner,Catalog.Kind kind) {
        var adult=new Pricing.Line(Catalog.Variant.ADULT,2,new Money(8000),new Money(16000),new Money(1600),new Money(14400));
        var child=new Pricing.Line(Catalog.Variant.CHILD,1,new Money(5000),new Money(5000),new Money(500),new Money(4500));
        var q=new Pricing.Quote(UUID.randomUUID(),owner,kind==Catalog.Kind.ADMISSION?"park-day":"night","Synthetic admission",kind,7,VISIT,3,3,
            List.of(adult,child),"family",new Money(21000),new Money(2100),new Money(18900),NOW,NOW.plusSeconds(900));
        quotes.values.put(q.id(),q);return q;
    }
    HttpResponse<String> request(String path,String token,String key,String body)throws Exception {
        var b=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+env.getRequiredProperty("local.server.port")+path)).timeout(Duration.ofSeconds(15));
        if(token!=null)b.header("Authorization","Bearer "+token);if(key!=null)b.header("Idempotency-Key",key);
        if(body!=null)b.header("Content-Type","application/json").POST(HttpRequest.BodyPublishers.ofString(body));else b.GET();
        try(var c=HttpClient.newHttpClient()){return c.send(b.build(),HttpResponse.BodyHandlers.ofString());}
    }
    String body(Pricing.Quote q){return "{\"quoteId\":\""+q.id()+"\"}";}
    Admission.Order confirm(Pricing.Quote q,String key)throws Exception {
        var response=request("/api/admission/orders",GUEST,key,body(q));assertEquals(200,response.statusCode(),response.body());
        assertTrue(response.headers().firstValue("Cache-Control").orElse("").contains("no-store"));
        return json.readValue(response.body(),Admission.Order.class);
    }
    @Test void admissionSnapshotAndAllEntitlementsRoundTripWithoutEventAllocation()throws Exception {
        var q=quote("guest",Catalog.Kind.ADMISSION);var order=confirm(q,"first");
        assertEquals(q,order.quote());assertEquals(2,order.entitlements().size());assertEquals(order,service.get(order.id(),"guest"));
        assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM admission_orders",Integer.class));
        assertEquals(0,jdbc.queryForObject("SELECT COUNT(*) FROM holds",Integer.class));
        assertEquals(0,jdbc.queryForObject("SELECT COUNT(*) FROM orders",Integer.class));assertEquals(3,events.availability("night").available());
    }
    @Test void realHttpFiltersAndOwnerChecksProtectAdmissionOperations()throws Exception {
        var q=quote("guest",Catalog.Kind.ADMISSION);
        assertEquals(401,request("/api/admission/orders",null,"key",body(q)).statusCode());
        assertEquals(403,request("/api/admission/orders",EMPLOYEE,"key",body(q)).statusCode());
        assertEquals(404,request("/api/admission/orders",OTHER,"key",body(q)).statusCode());
        var o=confirm(q,"key");
        assertEquals(404,request("/api/admission/orders/"+o.id(),OTHER,null,null).statusCode());
        assertEquals(200,request("/api/admission/orders/"+o.id(),GUEST,null,null).statusCode());
        var hidden=request("/api/admission/orders/recovery",OTHER,"key",body(q));assertEquals(200,hidden.statusCode());
        assertEquals(Admission.RecoveryState.NOT_CONFIRMED,json.readValue(hidden.body(),Admission.Recovery.class).state());
    }
    @Test void recoveryAndReplaySurvivePricingFailureAndQuoteExpiry()throws Exception {
        var q=quote("guest",Catalog.Kind.ADMISSION);var o=confirm(q,"original");
        clock.now.set(NOW.plusSeconds(3600));quotes.values.clear();assertEquals(o,confirm(q,"original"));
        var r=request("/api/admission/orders/recovery",GUEST,"original",body(q));assertEquals(200,r.statusCode());
        assertTrue(r.headers().firstValue("Cache-Control").orElse("").contains("no-store"));
        assertEquals(o,json.readValue(r.body(),Admission.Recovery.class).order());
        var reconstructed=new Admission.Service(store,id->{throw new AssertionError("Pricing unavailable");},clock,ZoneId.of("America/New_York"));
        assertEquals(o,reconstructed.confirm(q.id(),"guest","original"));
    }
    @Test void unknownRecoveryIsReadOnlyAndIsNotAReservation()throws Exception {
        var q=quote("guest",Catalog.Kind.ADMISSION);quotes.values.clear();
        var r=request("/api/admission/orders/recovery",GUEST,"unknown",body(q));assertEquals(200,r.statusCode());
        var data=json.readValue(r.body(),Admission.Recovery.class);assertEquals(Admission.RecoveryState.NOT_CONFIRMED,data.state());assertNull(data.order());
        assertEquals(0,jdbc.queryForObject("SELECT COUNT(*) FROM admission_orders",Integer.class));
    }
    @Test void changedQuoteAndChangedKeyCannotRebindAnOrder()throws Exception {
        var q=quote("guest",Catalog.Kind.ADMISSION);confirm(q,"key");
        assertEquals(409,request("/api/admission/orders",GUEST,"changed",body(q)).statusCode());
        var another=quote("guest",Catalog.Kind.ADMISSION);
        assertEquals(409,request("/api/admission/orders",GUEST,"key",body(another)).statusCode());
        assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM admission_orders",Integer.class));
    }
    @Test void malformedBodiesAndKeysFailWithoutWrites()throws Exception {
        var q=quote("guest",Catalog.Kind.ADMISSION);
        for(String text:List.of("{}","null","{\"quoteId\":\"1-1-1-1-1\"}","{\"quoteId\":3}","{\"quoteId\":\""+q.id()+"\",\"total\":1}"))
            assertEquals(400,request("/api/admission/orders",GUEST,"key",text).statusCode(),text);
        assertEquals(400,request("/api/admission/orders",GUEST,null,body(q)).statusCode());
        assertEquals(400,request("/api/admission/orders",GUEST,"bad key",body(q)).statusCode());
        assertEquals(0,jdbc.queryForObject("SELECT COUNT(*) FROM admission_orders",Integer.class));
    }
    @Test void eventQuotesAndExpiredQuotesCannotIssueAdmission()throws Exception {
        assertEquals(400,request("/api/admission/orders",GUEST,"wrong-kind",body(quote("guest",Catalog.Kind.EVENT))).statusCode());
        var q=quote("guest",Catalog.Kind.ADMISSION);clock.now.set(NOW.plusSeconds(900));
        assertEquals(410,request("/api/admission/orders",GUEST,"late",body(q)).statusCode());
        assertEquals(0,jdbc.queryForObject("SELECT COUNT(*) FROM admission_orders",Integer.class));
    }
    @Test void binaryIdentifiersKeepCaseAndTrailingOwnerSpaceDistinct() {
        var a=quote("guest",Catalog.Kind.ADMISSION);var b=quote("guest",Catalog.Kind.ADMISSION);var c=quote("guest ",Catalog.Kind.ADMISSION);
        service.confirm(a.id(),a.owner(),"Key");service.confirm(b.id(),b.owner(),"key");service.confirm(c.id(),c.owner(),"Key");
        assertEquals(3,jdbc.queryForObject("SELECT COUNT(*) FROM admission_orders",Integer.class));
    }
    @Test void directDuplicateInsertionCannotReplaceTheOriginalEntitlementIds() {
        var q=quote("guest",Catalog.Kind.ADMISSION);var o=service.confirm(q.id(),"guest","same");
        assertEquals(o,store.create(q.id(),"guest","same",()->{throw new AssertionError("Factory must not rerun");}));
        assertEquals(Failure.Code.CONFLICT,assertThrows(Failure.class,()->store.create(q.id(),"guest","different",()->o)).code());
        assertEquals(o,service.get(o.id(),"guest"));
    }
    @Test void failedFactoryLeavesNoPartialSnapshot() {
        var q=quote("guest",Catalog.Kind.ADMISSION);
        assertThrows(IllegalStateException.class,()->store.create(q.id(),"guest","key",()->{throw new IllegalStateException("Injected factory failure");}));
        assertEquals(0,jdbc.queryForObject("SELECT COUNT(*) FROM admission_orders",Integer.class));
        assertEquals(2,service.confirm(q.id(),"guest","key").entitlements().size());
    }
    @Test void corruptedIndexMetadataIsNotReturnedAsAValidReceipt() {
        var q=quote("guest",Catalog.Kind.ADMISSION);var o=service.confirm(q.id(),"guest","key");
        jdbc.update("UPDATE admission_orders SET quantity=1 WHERE id=?",o.id().toString());
        assertThrows(IllegalStateException.class,()->service.get(o.id(),"guest"));
    }
    @Test void concurrentDatabaseConfirmationReconcilesToOneOrder()throws Exception {
        var q=quote("guest",Catalog.Kind.ADMISSION);var attempts=new ArrayList<Callable<Admission.Order>>();
        for(int i=0;i<24;i++)attempts.add(()->{try{return service.confirm(q.id(),"guest","same");}
            catch(Failure f){assertEquals(Failure.Code.DEPENDENCY_UNAVAILABLE,f.code());return null;}});
        var ids=new HashSet<UUID>();int uncertain=0;
        try(var pool=Executors.newFixedThreadPool(12)) {
            for(var result:pool.invokeAll(attempts,45,TimeUnit.SECONDS)){var o=result.get();if(o==null)uncertain++;else ids.add(o.id());}
        }
        // A deadlock/timeout is visible as uncertainty, never fabricated success. Bounded identical replay after contenders finish.
        var finalOrder=service.confirm(q.id(),"guest","same");ids.add(finalOrder.id());assertEquals(1,ids.size());
        assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM admission_orders",Integer.class));assertEquals(2,finalOrder.entitlements().size());
        System.out.println("Admission SQL contenders: 24; uncertain first attempts: "+uncertain+"; distinct committed order IDs: "+ids.size());
    }
    @Test void competingKeysCannotCreateTwoSqlOrdersForOneQuote()throws Exception {
        var q=quote("guest",Catalog.Kind.ADMISSION);var jobs=new ArrayList<Callable<Boolean>>();
        for(int i=0;i<24;i++){String key="compete-"+i;jobs.add(()->{try{service.confirm(q.id(),"guest",key);return true;}
            catch(Failure f){assertTrue(f.code()==Failure.Code.CONFLICT||f.code()==Failure.Code.DEPENDENCY_UNAVAILABLE);return false;}});}
        int successes=0;try(var pool=Executors.newFixedThreadPool(12)){for(var r:pool.invokeAll(jobs,45,TimeUnit.SECONDS))if(r.get())successes++;}
        assertEquals(1,successes);assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM admission_orders",Integer.class));
    }
    @Test void admissionPurchaseDoesNotDependOnAnyEventRows() {
        jdbc.update("DELETE FROM events");var q=quote("guest",Catalog.Kind.ADMISSION);assertEquals(q,service.confirm(q.id(),"guest","no-events").quote());
        assertEquals(0,jdbc.queryForObject("SELECT COUNT(*) FROM events",Integer.class));
    }
    @Test void v1EventDataSurvivesAdditiveV2AndBothOrderTypesRemainReadable() {
        // Separate disposable DB: do not clean/mutate the primary Spring test database's Flyway history.
        try(var upgrade=new MySQLContainer("mysql:8.4.11")) {
            upgrade.start();
            var source=new DriverManagerDataSource(upgrade.getJdbcUrl(),upgrade.getUsername(),upgrade.getPassword());
            Flyway.configure().dataSource(source).locations("classpath:db/migration").target(MigrationVersion.fromVersion("1")).load().migrate();
            var sql=new JdbcTemplate(source);var manager=new DataSourceTransactionManager(source);
            var oldStore=new JdbcBookingStore(sql,manager);var oldService=new Booking.Service(oldStore,quotes,clock);
            oldService.create(new Booking.Event("night","night",VISIT,3));var eq=quote("guest",Catalog.Kind.EVENT);
            var hold=oldService.reserve("night",eq.id(),"guest");var eventOrder=oldService.confirm(hold.id(),"guest","original");
            Flyway.configure().dataSource(source).locations("classpath:db/migration").load().migrate();
            var newStore=new JdbcAdmissionStore(sql,json,manager);
            var upgraded=new Admission.Service(newStore,quotes,clock,ZoneId.of("America/New_York"));
            var aq=quote("guest",Catalog.Kind.ADMISSION);var admission=upgraded.confirm(aq.id(),"guest","original");
            assertEquals(eventOrder,oldService.get(eventOrder.id(),"guest"));assertEquals(admission,upgraded.get(admission.id(),"guest"));
            assertEquals(0,oldService.availability("night").available());assertEquals(1,sql.queryForObject("SELECT COUNT(*) FROM orders",Integer.class));
            assertEquals(1,sql.queryForObject("SELECT COUNT(*) FROM admission_orders",Integer.class));
            assertEquals(2,sql.queryForObject("SELECT COUNT(*) FROM flyway_schema_history WHERE success=1 AND type='SQL'",Integer.class));
        }
    }
}
