# Architecture and invariants

## Boundaries

`catalog-service` owns MongoDB `product_revisions`. `pricing-service` owns MySQL `price_books` and `quotes`. `booking-service` owns MySQL `events`, `holds`, event `orders`, and separate `admission_orders`. Services have separate database credentials and no cross-service table access. Compose uses two separate MySQL containers for a clear local ownership boundary; Aurora topology is a later deployment decision.

The small `core` module contains immutable records, business services, storage ports, and aggregate logic. It is a shared source-level library, not a shared database, a runtime service, or an excuse to bypass HTTP contracts. Its version travels with this monorepo candidate. Independent binary/API evolution should be revisited before splitting repositories.

`service-support` supplies local authentication, safe API errors, remote-call timeouts, query limits, and observability dependencies. Vaadin is a separate HTTP client, not an internal privileged database console. Angular consumes GraphQL for pricing and REST for commands.

## Catalog and pricing

Published catalog revisions are immutable; publication requires the next revision. The MongoDB identifier and compound unique index protect against competing publication requests. The current catalog browser returns up to 100 latest product snapshots; pagination is future work.

A price book pins one catalog revision, holds nonoverlapping inclusive date ranges by variant, and optionally applies one offer. The current offer model has a party-size threshold and percentage, not an arbitrary rule language. Pricing publication requires the next version; a SQL unique constraint rejects competing writers.

Quotes snapshot the product name/type/revision, selected date, price version, quantities, line amounts, discount, total, owner, creation time, and expiry. They are persisted, not calculated again during booking. There is no inventory guarantee until a hold is acquired.

Money is nonnegative integer cents. Decimal input must be exactly representable in cents; no binary floating-point arithmetic is used in the core. Discounts are basis points, rounded half-up on each variant line. Totals are the sum of line totals. API consumers do not supply the authoritative checkout amount.

Visit dates use the park's `America/New_York` calendar. Expiry uses UTC instants. The service clock uses millisecond precision to match the persisted epoch-millisecond booking timestamps. A quote is valid strictly before its expiry, not at the boundary.

## Booking transaction

1. Fetch the immutable quote from pricing **before** taking an inventory lock.
2. Validate the returned quote identity and required fields; reject broken dependency responses without changing inventory.
3. Start a MySQL `READ COMMITTED` transaction and select the event row `FOR UPDATE`.
4. Load that event's holds/orders, expire eligible active holds, and evaluate remaining shared capacity.
5. Create a hold only when the quote owner, product, visit date, and remaining capacity match.
6. Persist the result and commit. Errors roll the transaction back.

Each quote can acquire only one hold globally. Reservation retries for the same still-active quote/occurrence return the same hold. A consumed or expired hold cannot be recycled with the same quote.

Confirmation is scoped to `(authenticated owner, Idempotency-Key)` and bound to one hold. The event lock serializes confirmations within that event; unique constraints additionally prevent owner/key reuse across events, multiple orders per hold, or a quote held twice. A unique-constraint failure occurs outside the failed transaction's result handling, so no partially confirmed status is accepted. Owners and keys use case-sensitive database collations.

A confirmed hold continues consuming capacity after its original expiry. Repeating a successful confirmation returns the existing order, even after expiry. Availability calls and new reservations expire old active holds lazily. There is no background cleanup scheduler; a stale ACTIVE label may persist until the next ledger operation, but availability calculation applies the current clock before allocating.

## Deliberate bounded implementation

The booking adapter currently loads the full event ledger under a single row lock. That makes the correctness boundary inspectable for a small portfolio demonstration; it is **not a throughput claim**. A busy real event would require bounded SQL accounting, idempotency/retention policy, measured contention, archival, and possibly a different inventory model. No load-test throughput or production-scale capacity is claimed.

The pricing JPA adapter stores immutable snapshot JSON with indexed product/version/owner metadata. SQL is used deliberately for version lookup and booking transactions. A richer operational reporting/read model remains future work.

## Failure behavior

Business errors have safe codes and HTTP status mappings. REST does not echo SQL, credentials, raw exception text, or stack traces. GraphQL resolves domain errors with explicit codes and leaves unexpected errors sanitized by Spring. Outbound HTTP connections time out after two seconds and requests after five seconds. Dependencies are not called while the booking event lock is held. No automatic circuit breaker, durable message bus, distributed transaction, or invented exactly-once network guarantee is claimed.

## Recovery boundary

Core tests reconstruct a service using copied in-memory test state; SQL test source reconstructs a service over the same database. Neither is a demonstrated backup restore, forced process crash, storage failure, or regional recovery. Those require separate execution and evidence. The database is the intended authoritative state, never a UI screenshot or a client cache.

## Admission-only issuance (new candidate)

See [the bounded admission contract](admission-checkout.md). It stores a complete
immutable receipt and entitlement set in one SQL row, with unique quote and
route-scoped owner/key indexes. No inventory API, event table or event-row lock
is involved. Both styles still live inside the booking service. Event response
shapes are unchanged; admission has its own routes. Combined-cart atomicity,
cancellations and admission browser rendering remain explicitly unimplemented.
