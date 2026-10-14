# G-0010 planning checkpoint

- Status: **blocked** on G-0009 acceptance and an exact prerequisite revision. The user activated the next three roadmap goals for subagent work on 2026-10-13; this record is planning, not product execution or acceptance.
- Writer and integration owner: assigned G-0010 Codex subagent; sole writer of `goal/G-0010` in `.tmp/g0010-planning`.
- Base: `bd26cfbd062228c228223518d3800cd125c4e9b1` (`main` at assignment).
- Accepted plan commit: `2e5e740ca5eeeb1ffed0a688c158a01e63fbb0dd` (`goals/G-0010/README.md`). Initial plan file SHA-256: `afa91986e407058b9d3fbd1b74fb49a9d0e484d3b276157bcd233925edc7f6a4`. Current plan file SHA-256 after non-criterion planning corrections: `48eaf29338aa6c9ce5379b2b9863870649236d424f9603d495aab45ac6733f09`.
- Historical proposal: `docs/roadmap/goals/G-0010/README.md` at base; SHA-256 `352c315ef7851e98a15fe7f342f69c383f0813ed5aa7990e93bd8e8bf435be8b`. AC01–AC04 are copied unchanged.
- Governing baseline: root `AGENTS.md`, `GOVERNANCE.md`, `DEVELOPMENT.md`, `ARCHITECTURE.md`, `PLANS.md`, and `.agents/skills/work-goal` and `worktree-flow` at the base. N=2 independent contract/adversarial reviewers, R=3 rounds. No exception is requested.
- Required goal evidence after unblocking: `contention-results.json`, `interruption-recovery.md`, `transaction-design.md`, fixed candidate and environment/configuration identity, actual raw checks, two independent reviews, intended-use validation, decision, and recoverable handoff. The roadmap manifest template is intentionally unfilled; none of these acceptance artifacts exists for G-0010.
- Cost/resource boundary: synthetic local data, JDK 21, Maven 3.9+, Docker/Testcontainers or scoped local Compose, and existing standard public GitHub runners only. No paid workflow or service. No mutable service was started for this audit.

## Read-only gap audit

`booking-service/src/test/java/dev/parkcommerce/booking/JdbcBookingIT.java` already uses MySQL 8.4 Testcontainers. It races 24 direct service hold calls for three seats and 24 checkout calls with one key; it also checks separate-connection rollback. `CheckoutRecoveryIT.java` uses real Spring HTTP and MySQL with an injected pricing fixture, and tests 18 interleaved recovery/confirmation calls plus a discarded response. These are reusable foundations, but they do not execute 100 HTTP holds for 10 seats, reconcile every final SQL ledger count, or terminate and restart the application process around commit/response.

`core/src/main/java/dev/parkcommerce/core/Booking.java` contains the canonical ledger rules. `Booking.Service.reserve` obtains the quote before `store.transact`; `JdbcBookingStore` then uses `READ COMMITTED`, locks one event row with `SELECT ... FOR UPDATE`, reads its full holds/orders history, and writes transitions. `V1__event_booking.sql` defines unique quote, hold/order, and owner/key constraints, plus event indexes. Keep this one source of truth. Extend the existing HTTP/JDBC test seams and transaction evidence before changing the inventory model. The full ledger load under an event lock is a bounded portfolio design, not a throughput result.

The Hikari pool has 10 connections with a five-second acquisition timeout; the transaction template has a ten-second timeout. A 100-way workload may produce timeouts or overload in addition to expected sold-out responses. Record every request outcome, timing and SQL state; never count a timeout as an expected capacity rejection or hide retries. Use barriers and distinct connections for actual overlap, and preserve failed attempts.

## Next execution design, after prerequisite acceptance

1. Pin G-0009's accepted source/contract revision and compare its quote identity, expiry, and error shapes with the G-0010 plan. Refresh the branch and select a stable combined candidate.
2. Extend the existing Spring HTTP/MySQL harness to create ten seats and at least 100 independently authenticated or correctly scoped synthetic quote requests. Assert successful held quantity is at most ten, expected rejections are classified, and SQL holds/orders/availability reconcile. Add same-owner/key concurrent checkout, changed-hold conflict, and uncertain-response replay.
3. Exercise expiry/confirmation races and rollback with controlled synchronization. Use a separately launched booking process against a scoped persistent MySQL test database for interruption before/after commit response, then restart without database edits and reconcile outcomes. Avoid terminating a shared service or volume.
4. Record lock acquisition, SQL isolation and query/constraint observations, and prove a dependency quote fetch completes before taking the booking event lock. A test double alone cannot prove real HTTP dependency ordering. Prefer instrumentation of the existing remote client/transaction boundary over a second booking implementation.
5. Run `bash scripts/verify.sh --integration` and the added focused harness on frozen inputs, retain raw failures as well as passes, seek two independent reviews, and perform delegated intended-use validation. Readiness and release remain separate decisions.

No product code, test harness, service, or release was changed at this checkpoint. Next action: wait for G-0009 acceptance and exact input identity, then assign an implementation slice and revisit resource isolation and CI time bounds.

## Planning correction after checkpoint

The proposal's suggested `goal/G-0010/implementation` and `goal/G-0010/validation` refs conflict with the actual canonical `goal/G-0010` ref under Git's ref namespace. The active record uses `goal/G-0010-implementation` and `goal/G-0010-validation` as unassigned, uncreated suggestions instead. This changes no acceptance criterion, scope, evidence gate, or implementation authorization. Both the initial and current plan file digests are recorded above; Git history identifies the correction commit.
