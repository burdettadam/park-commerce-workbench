package dev.parkcommerce.booking;

import dev.parkcommerce.core.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.*;
import org.springframework.transaction.support.TransactionTemplate;

/** MySQL/InnoDB implementation. Lock one event before reading or changing its ledger. */
@Repository
public class JdbcBookingStore implements Booking.Store {
    private final JdbcTemplate jdbc;
    private final TransactionTemplate transactions;
    public JdbcBookingStore(JdbcTemplate jdbc,PlatformTransactionManager manager){
        this.jdbc=jdbc;this.transactions=new TransactionTemplate(manager);
        transactions.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactions.setTimeout(10);
    }
    @Override public void create(Booking.Event event){
        try{jdbc.update("INSERT INTO events (id,product_id,visit_date,capacity) VALUES (?,?,?,?)",event.id(),event.productId(),event.visitDate(),event.capacity());}
        catch(DuplicateKeyException e){throw new Failure(Failure.Code.CONFLICT,"Event already exists; capacity was not changed");}
    }
    @Override public Optional<Booking.Hold> hold(UUID id){return jdbc.query("SELECT * FROM holds WHERE id=?",this::mapHold,id.toString()).stream().findFirst();}
    @Override public Optional<Booking.Order> order(UUID id){return jdbc.query("SELECT * FROM orders WHERE id=?",this::mapOrder,id.toString()).stream().findFirst();}
    @Override public Optional<Booking.Order> orderByKey(String owner,String key){return jdbc.query("SELECT * FROM orders WHERE owner=? AND idempotency_key=?",this::mapOrder,owner,key).stream().findFirst();}
    @Override public <T>T transact(String eventId,Function<Booking.Ledger,T> operation){
        try{return transactions.execute(status->{
            var events=jdbc.query("SELECT * FROM events WHERE id=? FOR UPDATE",(rs,row)->new Booking.Event(rs.getString("id"),rs.getString("product_id"),rs.getObject("visit_date",LocalDate.class),rs.getInt("capacity")),eventId);
            if(events.isEmpty())throw new Failure(Failure.Code.NOT_FOUND,"Event not found");
            var oldHolds=jdbc.query("SELECT * FROM holds WHERE event_id=?",this::mapHold,eventId);
            var oldOrders=jdbc.query("SELECT * FROM orders WHERE event_id=?",this::mapOrder,eventId);
            var ledger=new Booking.Ledger(events.getFirst(),oldHolds,oldOrders);
            T value=operation.apply(ledger);
            Map<UUID,Booking.Hold> held=new HashMap<>();for(var h:oldHolds)held.put(h.id(),h);
            Set<UUID> ordered=new HashSet<>();for(var o:oldOrders)ordered.add(o.id());
            for(var h:ledger.holds()){
                if(!held.containsKey(h.id()))jdbc.update("INSERT INTO holds (id,event_id,owner,quote_id,quantity,total_cents,created_epoch_ms,expires_epoch_ms,status) VALUES (?,?,?,?,?,?,?,?,?)",
                    h.id().toString(),h.eventId(),h.owner(),h.quoteId().toString(),h.quantity(),h.total().cents(),h.createdAt().toEpochMilli(),h.expiresAt().toEpochMilli(),h.status().name());
                else if(held.get(h.id()).status()!=h.status())jdbc.update("UPDATE holds SET status=? WHERE id=?",h.status().name(),h.id().toString());
            }
            for(var o:ledger.orders())if(!ordered.contains(o.id()))jdbc.update("INSERT INTO orders (id,hold_id,event_id,owner,idempotency_key,quote_id,quantity,total_cents,confirmed_epoch_ms) VALUES (?,?,?,?,?,?,?,?,?)",
                o.id().toString(),o.holdId().toString(),o.eventId(),o.owner(),o.idempotencyKey(),o.quoteId().toString(),o.quantity(),o.total().cents(),o.confirmedAt().toEpochMilli());
            return value;
        });}catch(DuplicateKeyException e){
            // Runs outside the failed transaction; no partial status/capacity change can commit.
            throw new Failure(Failure.Code.CONFLICT,"Quote, hold, or idempotency key was already used");
        }
    }
    private Booking.Hold mapHold(ResultSet rs,int row)throws SQLException{
        return new Booking.Hold(UUID.fromString(rs.getString("id")),rs.getString("event_id"),rs.getString("owner"),UUID.fromString(rs.getString("quote_id")),rs.getInt("quantity"),new Money(rs.getLong("total_cents")),Instant.ofEpochMilli(rs.getLong("created_epoch_ms")),Instant.ofEpochMilli(rs.getLong("expires_epoch_ms")),Booking.HoldStatus.valueOf(rs.getString("status")));
    }
    private Booking.Order mapOrder(ResultSet rs,int row)throws SQLException{
        return new Booking.Order(UUID.fromString(rs.getString("id")),UUID.fromString(rs.getString("hold_id")),rs.getString("event_id"),rs.getString("owner"),rs.getString("idempotency_key"),UUID.fromString(rs.getString("quote_id")),rs.getInt("quantity"),new Money(rs.getLong("total_cents")),Instant.ofEpochMilli(rs.getLong("confirmed_epoch_ms")));
    }
}
