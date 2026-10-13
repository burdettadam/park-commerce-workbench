package dev.parkcommerce.booking;
import dev.parkcommerce.core.*;
import java.sql.SQLException;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import javax.sql.DataSource;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.*;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.mysql.MySQLContainer;
import static org.junit.jupiter.api.Assertions.*;
@Testcontainers
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,properties={
"spring.profiles.active=local",
    "DEMO_EMPLOYEE_TOKEN=test_employee_123456789012345678901234",
    "DEMO_GUEST_TOKEN=test_guest_123456789012345678901234567",
    "DEMO_OTHER_GUEST_TOKEN=test_other_123456789012345678901234567",
    "DEMO_SERVICE_TOKEN=test_service_1234567890123456789012345",
    "management.tracing.sampling.probability=0.0"
})
class JdbcBookingIT {
    @Container static MySQLContainer mysql=new MySQLContainer("mysql:8.4.11");
    @DynamicPropertySource static void database(DynamicPropertyRegistry r){
        r.add("spring.datasource.url",mysql::getJdbcUrl);r.add("spring.datasource.username",mysql::getUsername);r.add("spring.datasource.password",mysql::getPassword);
    }
    @Autowired JdbcBookingStore store;@Autowired JdbcTemplate jdbc;@Autowired DataSource dataSource;
    final Instant now=Instant.parse("2030-10-01T12:00:00Z");final LocalDate day=LocalDate.of(2030,10,31);
    final Map<UUID,Pricing.Quote> quotes=new ConcurrentHashMap<>();Booking.Service service;
    @BeforeEach void setup(){
        jdbc.update("DELETE FROM orders");jdbc.update("DELETE FROM holds");jdbc.update("DELETE FROM events");quotes.clear();
        service=new Booking.Service(store,quotes::get,Clock.fixed(now,ZoneOffset.UTC));
        service.create(new Booking.Event("night","night",day,3));
    }
    Pricing.Quote quote(String owner){
        var q=new Pricing.Quote(UUID.randomUUID(),owner,"night","Night",Catalog.Kind.EVENT,1,day,1,1,
            List.of(new Pricing.Line(Catalog.Variant.ADULT,1,new Money(10000),new Money(10000),new Money(0),new Money(10000))),
            null,new Money(10000),new Money(0),new Money(10000),now,now.plusSeconds(900));quotes.put(q.id(),q);return q;
    }
    @Test void concurrentSqlTransactionsCannotOversell()throws Exception{
        List<Pricing.Quote> candidates=new ArrayList<>();for(int i=0;i<24;i++)candidates.add(quote("buyer-"+i));
        try(var pool=Executors.newFixedThreadPool(8)){
            List<Callable<Boolean>> jobs=new ArrayList<>();for(var q:candidates)jobs.add(()->{try{service.reserve("night",q.id(),q.owner());return true;}catch(Failure f){assertEquals(Failure.Code.SOLD_OUT,f.code());return false;}});
            int winners=0;for(var result:pool.invokeAll(jobs))if(result.get())winners++;
            assertEquals(3,winners);assertEquals(0,service.availability("night").available());
        }
    }
    @Test void concurrentSqlCheckoutIsIdempotent()throws Exception{
        var q=quote("guest");var h=service.reserve("night",q.id(),q.owner());Set<UUID> ids=new HashSet<>();
        try(var pool=Executors.newFixedThreadPool(8)){
            List<Callable<UUID>> jobs=new ArrayList<>();for(int i=0;i<24;i++)jobs.add(()->service.confirm(h.id(),"guest","key-1").id());
            for(var result:pool.invokeAll(jobs))ids.add(result.get());
        }
        assertEquals(1,ids.size());assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM orders",Integer.class));
    }
    @Test void failedCrossEventKeyReuseRollsBackHoldStatus(){
        var h=service.reserve("night",quote("guest").id(),"guest");service.confirm(h.id(),"guest","same");
        service.create(new Booking.Event("second","night",day,3));var h2=service.reserve("second",quote("guest").id(),"guest");
        assertEquals(Failure.Code.CONFLICT,assertThrows(Failure.class,()->store.transact("second",ledger->ledger.confirm(h2.id(),"guest","same",now))).code());
        assertEquals(Booking.HoldStatus.ACTIVE,store.hold(h2.id()).orElseThrow().status());assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM orders",Integer.class));
    }
    @Test void uniquenessConflictRollsBackAcrossSeparateRealConnections()throws Exception{
        var first=service.reserve("night",quote("guest").id(),"guest");service.confirm(first.id(),"guest","same");
        service.create(new Booking.Event("second","night",day,3));
        var second=service.reserve("second",quote("guest").id(),"guest");
        try(var observer=dataSource.getConnection()){
            long observerId;
            try(var statement=observer.createStatement();var result=statement.executeQuery("SELECT CONNECTION_ID()")){
                assertTrue(result.next());observerId=result.getLong(1);
            }
            var actorId=new AtomicLong();
            var failure=assertThrows(Failure.class,()->store.transact("second",ledger->{
                actorId.set(jdbc.queryForObject("SELECT CONNECTION_ID()",Long.class));
                return ledger.confirm(second.id(),"guest","same",now);
            }));
            assertEquals(Failure.Code.CONFLICT,failure.code());
            assertTrue(actorId.get()>0);assertNotEquals(observerId,actorId.get());
            try(var statement=observer.prepareStatement("SELECT status FROM holds WHERE id=?")){
                statement.setString(1,second.id().toString());
                try(var result=statement.executeQuery()){assertTrue(result.next());assertEquals("ACTIVE",result.getString(1));}
            }
            try(var statement=observer.createStatement();var result=statement.executeQuery("SELECT COUNT(*) FROM orders WHERE event_id='second'")){
                assertTrue(result.next());assertEquals(0,result.getInt(1));
            }
        }
        assertEquals(2,service.availability("second").available());
        assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM orders",Integer.class));
    }
    @Test void emptyDatabaseMigrationsCreateBookingConstraints(){
        assertEquals(List.of("1","2"),jdbc.queryForList("SELECT version FROM flyway_schema_history WHERE success=1 ORDER BY installed_rank",String.class));
        assertEquals("InnoDB",jdbc.queryForObject("SELECT ENGINE FROM information_schema.TABLES WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='holds'",String.class));
        var constraints=Set.copyOf(jdbc.queryForList(
            "SELECT CONSTRAINT_NAME FROM information_schema.TABLE_CONSTRAINTS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME IN ('holds','orders','admission_orders')",String.class));
        assertTrue(constraints.containsAll(Set.of("uk_hold_quote","uk_order_owner_key","uk_admission_owner_key","ck_hold_quantity")),constraints.toString());
        var violation=assertThrows(DataAccessException.class,()->jdbc.update(
            "INSERT INTO events (id,product_id,visit_date,capacity) VALUES (?,?,?,?)","invalid","night",day,0));
        assertEquals(3819,((SQLException)violation.getRootCause()).getErrorCode());
        assertTrue(violation.getMessage().contains("ck_event_capacity"));
        assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM events",Integer.class));
    }
    @Test void newlyConstructedServiceReplaysPersistedOrder(){
        var h=service.reserve("night",quote("guest").id(),"guest");var o=service.confirm(h.id(),"guest","same");
        var restarted=new Booking.Service(store,quotes::get,Clock.fixed(now.plusSeconds(3600),ZoneOffset.UTC));
        assertEquals(o,restarted.confirm(h.id(),"guest","same"));
    }
    @Test void expiryAndCaseSensitiveKeysArePreserved(){
        var a=service.reserve("night",quote("guest").id(),"guest");var b=service.reserve("night",quote("guest").id(),"guest");
        service.confirm(a.id(),"guest","Key");service.confirm(b.id(),"guest","key");
        var restarted=new Booking.Service(store,quotes::get,Clock.fixed(now.plusSeconds(300),ZoneOffset.UTC));
        assertEquals(1,restarted.availability("night").available());
        assertEquals(2,jdbc.queryForObject("SELECT COUNT(*) FROM orders",Integer.class));
    }
    @Test void oneQuoteCannotHoldTwoEvents(){
        var q=quote("guest");service.reserve("night",q.id(),"guest");service.create(new Booking.Event("other","night",day,3));
        assertEquals(Failure.Code.CONFLICT,assertThrows(Failure.class,()->service.reserve("other",q.id(),"guest")).code());
        assertEquals(3,service.availability("other").available());
    }
}
