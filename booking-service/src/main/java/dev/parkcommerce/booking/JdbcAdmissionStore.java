package dev.parkcommerce.booking;

import dev.parkcommerce.core.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.*;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.databind.ObjectMapper;

/** One immutable SQL row contains the receipt and its ENTIRE entitlement set.
 *  This adapter never reads or mutates events, holds or event orders.
 */
@Repository
public class JdbcAdmissionStore implements Admission.Store {
    private final JdbcTemplate jdbc;
    private final ObjectMapper json;
    private final TransactionTemplate transactions;

    public JdbcAdmissionStore(JdbcTemplate jdbc, ObjectMapper json, PlatformTransactionManager manager) {
        this.jdbc = jdbc; this.json = json;
        transactions = new TransactionTemplate(manager);
        transactions.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactions.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactions.setTimeout(10);
    }
    @Override public Optional<Admission.Order> order(UUID id) {
        return jdbc.query("SELECT * FROM admission_orders WHERE id=?", this::read, id.toString()).stream().findFirst();
    }
    @Override public Optional<Admission.Order> byKey(String owner, String key) {
        return jdbc.query("SELECT * FROM admission_orders WHERE owner=? AND idempotency_key=?",
            this::read, utf8(owner), utf8(key)).stream().findFirst();
    }
    @Override public Optional<Admission.Order> byQuote(UUID quoteId) {
        return jdbc.query("SELECT * FROM admission_orders WHERE quote_id=?", this::read, quoteId.toString()).stream().findFirst();
    }
    @Override public Admission.Order create(UUID quoteId, String owner, String key, Supplier<Admission.Order> factory) {
        try {
            return transactions.execute(status -> {
                var prior = byKey(owner, key);
                if (prior.isPresent()) return Admission.sameRequest(prior.get(), quoteId, owner, key);
                var consumed = byQuote(quoteId);
                if (consumed.isPresent()) return Admission.sameRequest(consumed.get(), quoteId, owner, key);
                // No network in factory. It rechecks the clock before this new attempt.
                var order = Admission.sameRequest(factory.get(), quoteId, owner, key);
                var q = order.quote();
                jdbc.update("""
                    INSERT INTO admission_orders
                      (id,quote_id,owner,idempotency_key,product_id,visit_date,quantity,total_cents,confirmed_epoch_ms,payload)
                    VALUES (?,?,?,?,?,?,?,?,?,?)
                    """, order.id().toString(), quoteId.toString(), utf8(owner), utf8(key), q.productId(),
                    q.visitDate(), q.quantity(), q.total().cents(), order.confirmedAt().toEpochMilli(), write(order));
                return order;
            });
        } catch (DuplicateKeyException duplicate) {
            // Outside the failed REQUIRES_NEW transaction. Do not continue inside a failed insert.
            // The unique indexes choose a winner; discarded candidate IDs are never returned.
            var winner = byKey(owner, key);
            if (winner.isPresent()) return Admission.sameRequest(winner.get(), quoteId, owner, key);
            throw new Failure(Failure.Code.CONFLICT, "Admission quote or retry identity was already used");
        } catch (ConcurrencyFailureException contention) {
            // Bounded behavior: do not loop until green. Caller must retain and retry the SAME key.
            var winner = byKey(owner, key);
            if (winner.isPresent()) return Admission.sameRequest(winner.get(), quoteId, owner, key);
            throw new Failure(Failure.Code.DEPENDENCY_UNAVAILABLE, "Admission confirmation uncertain; retry the same quote and key");
        }
    }
    private String write(Admission.Order order) {
        try { return json.writeValueAsString(order); }
        catch (Exception invalid) { throw new IllegalStateException("Admission snapshot serialization failed", invalid); }
    }
    private Admission.Order read(ResultSet row, int index) throws SQLException {
        final Admission.Order order;
        try { order = json.readValue(row.getString("payload"), Admission.Order.class); }
        catch (Exception invalid) { throw new IllegalStateException("Stored admission snapshot is invalid", invalid); }
        var q = order.quote();
        if (!order.id().toString().equals(row.getString("id")) || !q.id().toString().equals(row.getString("quote_id"))
                || !Arrays.equals(utf8(q.owner()), row.getBytes("owner"))
                || !Arrays.equals(utf8(order.idempotencyKey()), row.getBytes("idempotency_key"))
                || !q.productId().equals(row.getString("product_id")) || !q.visitDate().equals(row.getObject("visit_date", LocalDate.class))
                || q.quantity() != row.getInt("quantity") || q.total().cents() != row.getLong("total_cents")
                || order.confirmedAt().toEpochMilli() != row.getLong("confirmed_epoch_ms"))
            throw new IllegalStateException("Admission snapshot and index identity disagree");
        return order;
    }
    private static byte[] utf8(String value) { return value.getBytes(StandardCharsets.UTF_8); }
}
