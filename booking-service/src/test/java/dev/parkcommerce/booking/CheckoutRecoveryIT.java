package dev.parkcommerce.booking;

import dev.parkcommerce.core.*;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.*;
import java.net.ServerSocket;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import javax.sql.DataSource;
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
    "spring.datasource.hikari.maximum-pool-size=32",
    "management.tracing.sampling.probability=0.0"
})
class CheckoutRecoveryIT {
    static final Instant NOW=Instant.parse("2030-10-01T12:00:00Z");
    static final String GUEST="test_guest_123456789012345678901234567";
    static final String OTHER="test_other_123456789012345678901234567";
    static final String EMPLOYEE="test_employee_123456789012345678901234";
    static final HttpClient HTTP=HttpClient.newHttpClient();
    @Container static MySQLContainer mysql=new MySQLContainer("mysql:8.4.11");
    @DynamicPropertySource static void database(DynamicPropertyRegistry r){
        r.add("spring.datasource.url",mysql::getJdbcUrl);r.add("spring.datasource.username",mysql::getUsername);r.add("spring.datasource.password",mysql::getPassword);
    }
    static final class RecoveryClock extends Clock {
        final AtomicReference<Instant> now=new AtomicReference<>(NOW);
        final AtomicReference<CountDownLatch> entered=new AtomicReference<>(),release=new AtomicReference<>();
        @Override public Instant instant(){
            var signal=entered.get();var gate=release.get();
            if(signal!=null&&gate!=null){signal.countDown();try{
                if(!gate.await(15,TimeUnit.SECONDS))throw new AssertionError("Clock gate timed out");
            }catch(InterruptedException e){Thread.currentThread().interrupt();throw new AssertionError(e);}}
            return now.get();
        }
        @Override public ZoneId getZone(){return ZoneOffset.UTC;}
        @Override public Clock withZone(ZoneId zone){return Clock.fixed(now.get(),zone);}
    }
    static final class QuoteFixture implements Pricing.Client {
        final Map<UUID,Pricing.Quote> values=new ConcurrentHashMap<>();
        final AtomicReference<CountDownLatch> entered=new AtomicReference<>(),release=new AtomicReference<>();
        @Override public Pricing.Quote quote(UUID id){
            var signal=entered.get();var gate=release.get();
            if(signal!=null&&gate!=null){signal.countDown();try{
                if(!gate.await(15,TimeUnit.SECONDS))throw new AssertionError("Pricing fixture gate timed out");
            }catch(InterruptedException e){Thread.currentThread().interrupt();throw new AssertionError(e);}}
            return values.get(id);
        }
    }
    @TestConfiguration(proxyBeanMethods=false) static class RecoveryInputs {
        @Bean @Primary RecoveryClock recoveryClock(){return new RecoveryClock();}
        @Bean @Primary QuoteFixture recoveryPricing(){return new QuoteFixture();}
    }
    @Autowired Booking.Service service; @Autowired JdbcBookingStore store;
    @Autowired JdbcTemplate jdbc; @Autowired DataSource dataSource;
    @Autowired Environment env; @Autowired ObjectMapper json;
    @Autowired RecoveryClock clock; @Autowired QuoteFixture quotes;
    @BeforeEach void setup(){
        jdbc.update("DELETE FROM orders");jdbc.update("DELETE FROM holds");jdbc.update("DELETE FROM events");
        clock.now.set(NOW);clock.entered.set(null);clock.release.set(null);
        quotes.values.clear();quotes.entered.set(null);quotes.release.set(null);
        service.create(new Booking.Event("night","night",LocalDate.of(2030,10,31),3));
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
        return HTTP.send(builder.build(),HttpResponse.BodyHandlers.ofString());
    }
    HttpResponse<String> request(String path,String token,String key,UUID hold)throws Exception {
        return requestAt(Integer.parseInt(env.getRequiredProperty("local.server.port")),path,token,key,hold);
    }
    HttpResponse<String> requestAt(int port,String path,String token,String key,UUID hold)throws Exception {
        var builder=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+path)).timeout(Duration.ofSeconds(15));
        if(token!=null)builder.header("Authorization","Bearer "+token);
        if(key!=null)builder.header("Idempotency-Key",key);
        if(hold!=null)builder.header("Content-Type","application/json").POST(HttpRequest.BodyPublishers.ofString("{\"holdId\":\""+hold+"\"}"));
        else builder.GET();
        return HTTP.send(builder.build(),HttpResponse.BodyHandlers.ofString());
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
        var h=hold();assertEquals(200,request("/api/orders",GUEST,"same",h.id()).statusCode());
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

    @Test void oneHundredRealHttpHoldsForTenSeatsReconcileWithSql()throws Exception {
        jdbc.update("UPDATE events SET capacity=10 WHERE id='night'");
        var candidates=new ArrayList<Pricing.Quote>();
        for(int i=0;i<100;i++)candidates.add(quote());
        var ready=new CountDownLatch(100);var start=new CountDownLatch(1);
        var outcomes=new ArrayList<HttpResponse<String>>();
        try(var pool=Executors.newFixedThreadPool(100)){
            var tasks=new ArrayList<Callable<HttpResponse<String>>>();
            for(var candidate:candidates)tasks.add(()->{ready.countDown();
                assertTrue(start.await(15,TimeUnit.SECONDS));return reserve(candidate);});
            var pending=new ArrayList<Future<HttpResponse<String>>>();
            for(var task:tasks)pending.add(pool.submit(task));
            assertTrue(ready.await(15,TimeUnit.SECONDS));start.countDown();
            for(var result:pending)outcomes.add(result.get(60,TimeUnit.SECONDS));
        }finally{start.countDown();}
        int accepted=0,soldOut=0;
        for(var result:outcomes){
            if(result.statusCode()==201)accepted++;
            else if(result.statusCode()==409){
                assertEquals("SOLD_OUT",json.readValue(result.body(),Map.class).get("code"));soldOut++;
            }else fail("Unexpected hold status "+result.statusCode()+": "+result.body());
        }
        var held=jdbc.queryForObject("SELECT COALESCE(SUM(quantity),0) FROM holds WHERE event_id='night' AND status='ACTIVE'",Integer.class);
        var confirmed=jdbc.queryForObject("SELECT COALESCE(SUM(quantity),0) FROM holds WHERE event_id='night' AND status='CONFIRMED'",Integer.class);
        var count=jdbc.queryForObject("SELECT COUNT(*) FROM holds WHERE event_id='night'",Integer.class);
        var orders=jdbc.queryForObject("SELECT COUNT(*) FROM orders WHERE event_id='night'",Integer.class);
        var availability=service.availability("night");
        System.out.println("G-0010 HTTP contention: submitted=100 accepted="+accepted+" soldOut="+soldOut+
            " other=0 transportErrors=0 timeouts=0 held="+held+" confirmed="+confirmed+" holdRows="+count+" orderRows="+orders+
            " available="+availability.available());
        assertEquals(100,outcomes.size());assertEquals(10,accepted);assertEquals(90,soldOut);
        assertEquals(accepted,held);assertEquals(accepted,count);assertEquals(0,confirmed);
        assertEquals(0,orders);assertEquals(0,availability.available());
        assertEquals(10,availability.capacity());assertEquals(held.intValue(),availability.held());
    }

    @Test void concurrentAuthenticatedCheckoutAndUncertainReplayConsumeOnce()throws Exception {
        var first=hold();var second=hold();
        var ready=new CountDownLatch(24);var start=new CountDownLatch(1);
        var receipts=new HashSet<UUID>();
        try(var pool=Executors.newFixedThreadPool(24)){
            var pending=new ArrayList<Future<HttpResponse<String>>>();
            for(int i=0;i<24;i++)pending.add(pool.submit(()->{ready.countDown();
                assertTrue(start.await(15,TimeUnit.SECONDS));return request("/api/orders",GUEST,"same",first.id());}));
            assertTrue(ready.await(15,TimeUnit.SECONDS));start.countDown();
            for(var result:pending){var response=result.get(30,TimeUnit.SECONDS);
                assertEquals(200,response.statusCode(),response.body());
                receipts.add(json.readValue(response.body(),Booking.Order.class).id());}
        }finally{start.countDown();}
        assertEquals(1,receipts.size());
        var recovered=recover(first);assertEquals(receipts.iterator().next(),recovered.order().id());
        var replay=request("/api/orders",GUEST,"same",first.id());
        assertEquals(200,replay.statusCode());assertEquals(recovered.order(),json.readValue(replay.body(),Booking.Order.class));
        var conflict=request("/api/orders",GUEST,"same",second.id());
        assertEquals(409,conflict.statusCode());assertEquals("CONFLICT",json.readValue(conflict.body(),Map.class).get("code"));
        assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM orders",Integer.class));
        assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM holds WHERE status='CONFIRMED'",Integer.class));
        assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM holds WHERE status='ACTIVE'",Integer.class));
        assertEquals(1,service.availability("night").available());
    }

    @Test void expiryAndConfirmationAtBoundaryHaveOneReconciledOutcome()throws Exception {
        var expiring=hold();var entered=new CountDownLatch(1);var release=new CountDownLatch(1);
        clock.entered.set(entered);clock.release.set(release);
        HttpResponse<String> rejected;
        try(var pool=Executors.newSingleThreadExecutor()){
            var pending=pool.submit(()->request("/api/orders",GUEST,"boundary",expiring.id()));
            assertTrue(entered.await(10,TimeUnit.SECONDS),"Checkout did not reach transactional clock");
            clock.now.set(expiring.expiresAt());
            release.countDown();
            rejected=pending.get(20,TimeUnit.SECONDS);
        }finally{release.countDown();clock.entered.set(null);clock.release.set(null);}
        assertEquals(410,rejected.statusCode(),rejected.body());
        assertEquals("EXPIRED",json.readValue(rejected.body(),Map.class).get("code"));
        assertEquals(Booking.HoldStatus.EXPIRED,recoverWithKey(expiring,"boundary").hold().status());
        assertEquals(0,jdbc.queryForObject("SELECT COUNT(*) FROM orders",Integer.class));
        assertEquals(3,service.availability("night").available());
        clock.now.set(NOW);var confirmed=hold();
        assertEquals(200,request("/api/orders",GUEST,"before-boundary",confirmed.id()).statusCode());
        clock.now.set(confirmed.expiresAt());
        assertEquals(200,request("/api/orders",GUEST,"before-boundary",confirmed.id()).statusCode());
        assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM orders",Integer.class));
        assertEquals(2,service.availability("night").available());
    }

    Booking.Recovery recoverWithKey(Booking.Hold hold,String key)throws Exception {
        var response=request("/api/orders/recovery",GUEST,key,hold.id());
        assertEquals(200,response.statusCode(),response.body());
        return json.readValue(response.body(),Booking.Recovery.class);
    }

    @Test void dependencyFetchFinishesBeforeEventRowLock()throws Exception {
        var candidate=quote();var entered=new CountDownLatch(1);var release=new CountDownLatch(1);
        quotes.entered.set(entered);quotes.release.set(release);
        try(var pool=Executors.newSingleThreadExecutor()){
            var pending=pool.submit(()->reserve(candidate));
            assertTrue(entered.await(10,TimeUnit.SECONDS),"Booking did not reach pricing dependency");
            try(var connection=dataSource.getConnection()){
                connection.setAutoCommit(false);
                try(var statement=connection.prepareStatement("SELECT id FROM events WHERE id=? FOR UPDATE NOWAIT")){
                    statement.setString(1,"night");
                    try(var rows=statement.executeQuery()){assertTrue(rows.next());}
                }finally{connection.rollback();}
            }
            release.countDown();
            var result=pending.get(20,TimeUnit.SECONDS);
            assertEquals(201,result.statusCode(),result.body());
        }finally{release.countDown();quotes.entered.set(null);quotes.release.set(null);}
        var indexes=jdbc.queryForList("EXPLAIN SELECT * FROM holds WHERE event_id='night'");
        assertFalse(indexes.isEmpty());
        var isolation=store.transact("night",ledger->jdbc.queryForObject("SELECT @@transaction_isolation",String.class));
        assertEquals("READ-COMMITTED",isolation);
        System.out.println("G-0010 lock ordering: pricing blocked, independent event FOR UPDATE NOWAIT succeeded; isolation="+
            isolation+" hold query EXPLAIN="+indexes);
    }

    @Test void killedApplicationAfterCommitBeforeClientResponseRecoversAfterRestart()throws Exception {
        var reserved=hold();int port=freePort();
        var forwarded=new CountDownLatch(1);var dropResponse=new CountDownLatch(1);
        var forwardedStatus=new AtomicReference<Integer>();var proxyError=new AtomicReference<Throwable>();
        var proxy=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
        Process child=null;
        proxy.createContext("/api/orders",exchange->{
            try{
                var body=exchange.getRequestBody().readAllBytes();
                var outgoing=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+"/api/orders"))
                    .timeout(Duration.ofSeconds(15))
                    .header("Authorization",exchange.getRequestHeaders().getFirst("Authorization"))
                    .header("Idempotency-Key",exchange.getRequestHeaders().getFirst("Idempotency-Key"))
                    .header("Content-Type","application/json")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body)).build();
                forwardedStatus.set(HTTP.send(outgoing,HttpResponse.BodyHandlers.ofString()).statusCode());
                forwarded.countDown();
                dropResponse.await(15,TimeUnit.SECONDS);
            }catch(Throwable e){proxyError.set(e);forwarded.countDown();}
            finally{exchange.close();}
        });
        proxy.start();
        try(var pool=Executors.newSingleThreadExecutor()){
            child=startBookingProcess(port);awaitChild(port,child);
            var pending=pool.submit(()->requestAt(proxy.getAddress().getPort(),"/api/orders",GUEST,"after-commit",reserved.id()));
            assertTrue(forwarded.await(20,TimeUnit.SECONDS),"Proxy did not observe committed checkout");
            assertNull(proxyError.get(),"Proxy forwarding failed: "+proxyError.get());
            assertEquals(200,forwardedStatus.get());
            assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM orders WHERE hold_id=?",Integer.class,reserved.id().toString()));
            System.out.println("G-0010 proxy observed committed response status=200; mysqlContainer="+mysql.getContainerId()+
                " heldResponseAt="+Instant.now()+" childPid="+child.pid());
            child.destroyForcibly();assertTrue(child.waitFor(10,TimeUnit.SECONDS));
            System.out.println("G-0010 child killed pid="+child.pid()+" exit="+child.exitValue()+" at="+Instant.now());
            dropResponse.countDown();
            assertThrows(ExecutionException.class,()->pending.get(20,TimeUnit.SECONDS),
                "Proxy must drop the response so the client has an uncertain result");
            proxy.stop(0);
            child=startBookingProcess(port);awaitChild(port,child);
            var recovered=requestAt(port,"/api/orders/recovery",GUEST,"after-commit",reserved.id());
            assertEquals(200,recovered.statusCode(),recovered.body());
            var state=json.readValue(recovered.body(),Booking.Recovery.class);
            assertEquals(Booking.HoldStatus.CONFIRMED,state.hold().status());
            assertNotNull(state.order());
            var replay=requestAt(port,"/api/orders",GUEST,"after-commit",reserved.id());
            assertEquals(200,replay.statusCode(),replay.body());
            assertEquals(state.order(),json.readValue(replay.body(),Booking.Order.class));
            assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM orders",Integer.class));
            assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM holds WHERE status='CONFIRMED'",Integer.class));
            assertEquals(2,service.availability("night").available());
            System.out.println("G-0010 process interruption: committed order="+state.order().id()+
                " recovered after child restart; orders=1 confirmed=1 available=2");
        }finally{
            dropResponse.countDown();proxy.stop(0);
            if(child!=null&&child.isAlive()){child.destroyForcibly();child.waitFor(10,TimeUnit.SECONDS);}
        }
    }

    static int freePort()throws Exception{
        try(var socket=new ServerSocket(0)){return socket.getLocalPort();}
    }
    Process startBookingProcess(int port)throws Exception{
        var jar=Path.of("target","app.jar").toAbsolutePath();assertTrue(Files.isRegularFile(jar),jar.toString());
        var java=Path.of(System.getProperty("java.home"),"bin","java").toString();
        var output=Files.createTempFile("g0010-booking-child-",".log");
        var builder=new ProcessBuilder(java,"-jar",jar.toString(),"--server.address=127.0.0.1","--server.port="+port);
        builder.redirectErrorStream(true).redirectOutput(output.toFile());
        var environment=builder.environment();
        environment.put("SPRING_PROFILES_ACTIVE","local");
        environment.put("JDBC_URL",mysql.getJdbcUrl());
        environment.put("DB_USER",mysql.getUsername());
        environment.put("DB_PASSWORD",mysql.getPassword());
        environment.put("DEMO_EMPLOYEE_TOKEN",EMPLOYEE);
        environment.put("DEMO_GUEST_TOKEN",GUEST);
        environment.put("DEMO_OTHER_GUEST_TOKEN",OTHER);
        environment.put("DEMO_SERVICE_TOKEN","test_service_1234567890123456789012345");
        environment.put("PRICING_URL","http://127.0.0.1:9");
        var process=builder.start();
        System.out.println("G-0010 child process pid="+process.pid()+" port="+port+" at="+Instant.now()+" localLog="+output);
        return process;
    }
    void awaitChild(int port,Process child)throws Exception{
        long deadline=System.nanoTime()+TimeUnit.SECONDS.toNanos(45);
        while(System.nanoTime()<deadline){
            assertTrue(child.isAlive(),"Booking child exited before readiness");
            try{if(requestAt(port,"/api/session",GUEST,null,null).statusCode()==200)return;}
            catch(Exception ignored){Thread.sleep(100);}
        }
        fail("Booking child readiness timed out");
    }
}
