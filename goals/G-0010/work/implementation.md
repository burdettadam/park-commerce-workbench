# G-0010 implementation progress

- Sole writer/integration owner: assigned G-0010 Codex subagent; `goal/G-0010` in `.tmp/g0010-planning`.
- Accepted G-0010 plan P: `2e5e740ca5eeeb1ffed0a688c158a01e63fbb0dd`; unchanged AC01–AC04. The current living goal record SHA-256 at this increment is `0ca23e84d77d4c7d8e36abe5f7477840563d9e934385a6760a023f4df61c00b9`. It contains later state updates, not a new accepted criterion set.
- G-0009 exact prerequisite: P `ae52a4564ff732994578a7d698713d042e2bf287`, S `6578753c94674d95793f3ffd82ad7e67d9e98c4e`, E `a59360a3366d4db0a212972d7d07f3dc0bb07445`, branch head `a439342d367a4fa44ae584a141466ebb5f723eaa`, main M `15010a258ac6618045d5afb35fb971127ce3ae7c`. G-0010 merged M locally before this slice. The later G-0009 documentation merge will be incorporated before source freeze.
- Candidate S: not frozen. This is a mutable implementation increment. No independent review, intended-use validation or readiness claim yet.

## Reuse and first check

Reused `CheckoutRecoveryIT`'s real Spring HTTP, authenticated guest, MySQL 8.4 Testcontainer, controlled clock and pricing fixture rather than adding a second transaction model. Added a shared HTTP client, a 100-request barrier for ten seats, SQL outcome reconciliation, concurrent same-key checkout/recovery/conflict, expiry at the exact boundary, and a gated dependency call with an independent `FOR UPDATE NOWAIT` observer. Booking `Ledger`, `Service`, JDBC store and migrations are unchanged.

First focused check on Windows with process-local Microsoft JDK 21.0.12.1, Maven 3.9.16, Docker Desktop 29.7.2 and synthetic data:

`mvn -B -ntp -Pintegration -pl booking-service -am -Dit.test=CheckoutRecoveryIT -Dfailsafe.failIfNoSpecifiedTests=false verify`

It exited 0: 12 tests passed, zero failures/errors/skips. The contention test submitted 100 real HTTP requests, observed ten accepted and 90 `SOLD_OUT`, and reconciled ten active hold rows/quantity, zero orders and zero availability. The lock test held the pricing fixture call open while a separate JDBC connection acquired the event row `FOR UPDATE NOWAIT`; `EXPLAIN` chose `ix_holds_event_status_expiry` for the event ledger read. Full initial framework output is local at `%TEMP%/g0010-contention-first-run.txt`; it is exploratory because source was not yet frozen. Retain a sanitized summary/raw test report for the fixed candidate.

Next: add a separate booking application process against persistent scoped MySQL, interrupt after committed checkout but before forwarding its response, restart without database edits, and recover the original order. Then refresh against the G-0009 documentation-only main commit, freeze candidate S, run selected checks and obtain independent reviews.
