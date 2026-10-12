# Admission checkout and issuance contract

Status: local code candidate for G-0011; real SQL/HTTP execution, prerequisite adoption,
independent reviews and human acceptance are not complete.

## Observable outcome

A guest confirms a server-issued ADMISSION quote. One immutable admission order
contains the exact quote snapshot and one entitlement group for each nonempty
variant line (ADULT and/or CHILD), with that line's quantity. A party of two adults
and one child receives two entitlement IDs representing three dated admissions.
No names, identities, scanners, QR codes, real payment details or event capacity
are involved. Grouping is not a per-person credential design.

## Request and response

All routes require an authenticated GUEST under the existing local profile. That
profile is not a deployable identity provider. Non-local profiles remain deny-all.

| Method and route | Body/header | Result |
| --- | --- | --- |
| POST `/api/admission/orders` | `{ "quoteId": "full-UUID" }`, original `Idempotency-Key` | 200 immutable issuance receipt for a new confirmation or identical replay |
| POST `/api/admission/orders/recovery` | Same body/header | 200 CONFIRMED + original order, or NOT_CONFIRMED + null order |
| GET `/api/admission/orders/{id}` | Full order UUID | 200 owner-scoped immutable issuance receipt |

Successful responses use Cache-Control: no-store. Client-controlled prices, owners,
quantities and products are not accepted. Missing/extra request fields, malformed
UUIDs and invalid keys are rejected. The internal pricing service supplies the quote.
QuoteContract strict UUID parsing is reused; event endpoints are unchanged.

The receipt has `id`, `quote`, `idempotencyKey`, `confirmedAt`, and `entitlements`.
Each entitlement has `id`, `orderId`, `variant`, `quantity`, `state`, and `issuedAt`.
The embedded quote provides owner, product name/ID, catalog and pricing versions,
visit date, exact line amounts, offer, totals, issue time and expiration. No
catalog or price is silently recalculated during admission confirmation.

## Idempotency and recovery

Admission identity is **(admission-orders route, exact authenticated owner,
case-sensitive key)**, bound to one quote. One admission quote can be consumed only
once, including attempts using different keys. Event keys remain independently
scoped to the existing event-order operation. Retain the operation/route as well
as owner, quote ID and key in any future client recovery record. Do not feed this
receipt into the existing event hold-based Angular recovery coordinator.

Identical committed retries return the original order and all original entitlement
IDs without calling pricing, even after expiry or service reconstruction. A key
bound to another quote, or a consumed own quote retried under a different key,
returns CONFLICT. Two accounts may use the same text key for different quotes.

Recovery performs no write, pricing call, issuance, renewal or reservation.
NOT_CONFIRMED is only a read observation; an in-flight confirmation can still
commit afterward. It is not proof of failure or permission to generate a new key.
Retain the original identity across timeouts, 503s, malformed results, sign-outs
and retries. Another account's quote does not reveal that account's order.

## Eligibility and failure rules

The guest owner is checked before exposing quote contents. New issuance validates
snapshot ID, kind, product ID/name, positive versions, valid date range, quantity,
unique variants, line arithmetic, aggregate arithmetic and the existing 15-minute
TTL. The pricing service remains authoritative for actual offer eligibility and
catalog existence: these checks are not an independent catalog authenticity proof.
The original quote's discount and rounding are preserved, never recomputed here.

For a new order, one clock instant is captured inside the store's factory callback
after network lookup and initial store entry. A future-issued quote is a broken
dependency response. Equality with expiry is expired; a visit date before the
current park-local date is expired. Booking and pricing must use the same configured
PARK_TIME_ZONE (default America/New_York) and synchronized clocks.
This timestamp is **attempt evaluation time**, not a guarantee the SQL commit
finishes before the quote deadline. A successful historical replay bypasses new
eligibility evaluation and does not extend or consume anything again.

| Condition | HTTP |
| --- | --- |
| Missing credentials / wrong role | 401 / 403 |
| Malformed command or EVENT quote on admission route | 400 |
| Another owner's quote/order, or absent order | 404 |
| Changed key/quote binding or consumed own quote | 409 |
| Expired quote or past visit date on a new purchase | 410 |
| Broken pricing snapshot/dependency or unresolved concurrency failure | 503 |

A transient SQL failure is not silently retried in a loop. Where an already
committed identical winner can be read after rollback, return it. Otherwise retain
uncertainty and retry the **same** identity. No exactly-once network-delivery claim.

## Persistence and event compatibility

V2 adds only `admission_orders`; V1 is byte-for-byte unchanged. A single immutable
JSON payload contains the order and entire entitlement set. Indexed scalar columns
support order/key/quote lookups; reads check that those columns match the payload.
There is no separately published child set to become partially issued. Two unique
indexes arbitrate duplicate quote and owner/key attempts. Owner/key binary columns
preserve byte identity, case and trailing spaces; malformed Unicode subjects are
rejected before encoding. Keys permit only 1-100 safe ASCII characters.

JdbcAdmissionStore owns a bounded REQUIRES_NEW transaction and reconciles duplicate
results only after its failed transaction exits. It does not reference the event,
hold or event-order tables. This is a standalone operation, NOT an atomic leg of a
future combined-cart transaction. G-0012 must explicitly design that aggregate
transaction/saga rather than assuming two independent purchases form one cart.
No automatic database durability or Aurora compatibility is claimed by source.

The candidate integration suite includes a fresh migration, a separate V1 database
with a confirmed event order, V2 upgrade, admission issuance and retrieval of both
order types. It has not run here. Existing event clients and responses are untouched.

## Lifecycle and audit

The only issued state is ACTIVE: issued, not voided, for the original dated product.
ACTIVE is not an assertion that entry is valid on another date or a scanner result.
VOID is reserved for future cancellation. There is no VOID transition or endpoint,
refund or event-capacity release in this slice. A future implementation must retain
the original issuance receipt and add an audited, idempotent lifecycle event with
actor, reason, affected entitlement/order, time and retry identity. Current receipt
reads are issuance history, not a future live cancellation-state feed.

Entitlement IDs link to the containing order; its immutable quote and confirmed
owner/time supply the issuance audit. No employee identity or historical event is
invented. Raw evidence remains synthetic and must be checked for credentials before
any remote publication.

## Runbook and limits

`bash scripts/test-core.sh --demo` exercises the real domain with test-only memory
stores. `bash scripts/verify.sh --integration` requires Maven, Docker and network
access and must not silently skip tests. `node scripts/demo-http.mjs` requires the
actual local services and creates additional synthetic records. It does not clean
or overwrite existing records. No admission browser UI has been built in this slice.
