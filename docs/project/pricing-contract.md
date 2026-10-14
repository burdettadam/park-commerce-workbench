# Pricing and quote contract — local G-0009 candidate

This documents implemented local behavior under the accepted **G-0009** plan.
The [G-0009 evidence](../../goals/G-0009/README.md) identifies the fixed source,
real framework checks, reviews and remaining acceptance gates. No version release
or deployment is claimed. The contract uses synthetic USD amounts and an explicit
park zone (`America/New_York` in the current service wiring).

## Evaluation time and calendar

At the start of pricing evaluation, `Pricing.Service.create` obtains **one**
`Clock.instant()`. Date eligibility, `createdAt`, and `expiresAt = createdAt + 900
seconds` use that same instant. Catalogue or price-store latency never refreshes
this deadline. A sufficiently slow operation can return a quote that has already
expired; it is not granted a new lifetime. A downstream purchase must still check
its actual current time. Local service HTTP timeouts remain separate controls.

The visit date is a **park-local calendar date**, not a UTC date or an instant.
Today and future dates are eligible; yesterday fails before dependency reads.
Daylight-saving changes do not change the 900-second duration. A request that
started just before park midnight is consistently evaluated against that earlier
date. This does not model park closing hours, event start times, or entry eligibility.

An unpublished preview samples one instant for the same date check. It continues
to have no quote identity, expiration or reservation effect.

## Price and money rules

The fixture pack is [pricing-contract-fixtures.json](../../contracts/pricing/pricing-contract-fixtures.json).
All amounts are integer cents. One rate must cover each requested variant and
visit date. The `from` and `through` endpoints are inclusive. Same-variant ranges
may be adjacent on different dates but cannot overlap or share a date. A missing
rate is `NOT_FOUND`; an absent zero-quantity variant does not need a rate.

There are 1–20 total tickets and at most USD 10,000 per ticket. At most one offer
exists per price book. Its eligibility threshold counts all requested tickets.
A zero-percent offer is not labeled applied. An eligible nonzero-percent offer
can be labeled applied even when a free product or rounding produces zero discount.
There is no offer stacking, per-person eligibility proof, tax, fee, or foreign currency.

For each nonempty variant line:

1. Multiply unit cents by that variant's quantity.
2. Calculate `line_subtotal * basis_points / 10000` and round **the discount**
   half-up to a whole cent.
3. Subtract that line discount. Sum the line subtotals and discounts for the quote.

This is intentionally neither per-ticket nor whole-cart rounding. For example,
with a 10% offer, one adult at 5 cents plus one child at 5 cents produces a 1-cent
discount on each line: **10 − 2 = 8 cents**. Two adults at 5 cents are a single
10-cent line and produce **10 − 1 = 9 cents**. These are fictional engineering
fixtures, not legal/tax or real ticket-pricing advice.

## Immutable quote versus current observation

The stored `Pricing.Quote` shape is unchanged. Its ID, owner, product kind/name,
catalog revision, pricing version, amounts, lines, visit date and original times
are snapshots. New catalog publications, new rates, reads, and status queries do
not rewrite these fields. Existing owner-scoped historical reads still return
expired snapshots. An unknown quote and another owner's quote both return
`NOT_FOUND`. Internal booking reads remain restricted to the internal service role.

New read-only endpoints:

```http
GET /api/quotes/{full-uuid}/status
Authorization: Bearer <authenticated guest or employee token>
```

```graphql
query QuoteObservation($id: ID!) {
  quoteStatus(id: $id) {
    state
    evaluatedAt
    parkTimeZone
    quote {
      id productName kind catalogRevision pricingVersion createdAt expiresAt
      total { cents amount currency }
    }
  }
}
```

The status envelope contains `quote`, `state`, `evaluatedAt`, `parkTimeZone`.
Only the quote is persisted. `UNEXPIRED` means the server observation instant is
strictly before its deadline; equality is `EXPIRED`. Status samples the clock
after retrieving and authorizing the stored quote. It never reads current catalog
or rate books, writes a quote, creates a hold, renews a hold, or confirms an order.
Use a reliably synchronized server clock; clock rollback before quote creation is
not modeled as a separate state by this expiration-only observation.

**UNEXPIRED is not AVAILABLE or UNUSED.** Inventory might be exhausted, the quote
might already have a hold, or a purchase may have been confirmed. An expired quote
cannot create a new event hold, but an already confirmed order remains recoverable
with its original hold and retry key. This preserves uncertain-checkout recovery.

## Wire formats and intentional input tightening

`QuoteContract.Input` is shared by REST quote creation and the GraphQL resolver.
The request is `{productId, visitDate, adults, children}`. Both quantities must be
present as integers, including an explicit zero. The date must be exactly ten ASCII
characters `YYYY-MM-DD`, a real calendar date and year 0001–9999. Time strings,
whitespace, non-padded dates, year zero and extended years are rejected.

REST quote get/status/internal routes and GraphQL quote/status use the same full
hyphenated UUID parser. Hexadecimal case does not change identity. Abbreviated
UUID groups and surrounding punctuation/whitespace are rejected, not normalized.

**Compatibility:** stored quote fields and existing complete requests are retained.
This slice intentionally tightens inputs: REST previously could default an omitted
count to zero; callers must now send both counts. Previously accepted abbreviated
UUIDs and extended/noncontract date forms must be corrected. Repository clients
already send full IDs and both counts. External consumer acceptance remains pending.

REST `Money` continues to use `{cents: <JSON integer>}`. GraphQL `Money.cents`,
`catalogRevision`, and `pricingVersion` remain **strings**; `amount` is a two-decimal
string and `currency` is USD. Existing GraphQL Quote fields are retained; `createdAt`
and `kind` are additive. The status query is additive. Do not assume the two
transports have identical envelopes or number representations.

### Synthetic consumer examples

An owner creates a one-adult/one-child quote from a published 5-cent rate per
variant with a 10% offer. The following fields are excerpts; the service also
returns the other documented quote snapshot fields. IDs and timestamps below
illustrate shape rather than a reusable quote:

```http
POST /api/quotes
Authorization: Bearer <guest token>
Content-Type: application/json

{"productId":"night","visitDate":"2030-10-31","adults":1,"children":1}
```

```json
{"id":"00000000-0000-0000-0000-000000000001","owner":"guest","catalogRevision":1,"pricingVersion":1,"subtotal":{"cents":10},"discount":{"cents":2},"total":{"cents":8},"createdAt":"2030-10-01T12:00:00Z","expiresAt":"2030-10-01T12:15:00Z"}
```

For an unknown or another owner's full quote ID, REST returns HTTP 404 with a
problem detail carrying `{"title":"NOT_FOUND","status":404,"code":"NOT_FOUND"}`.
The same resolver condition can have HTTP 200 but carries
`{"errors":[{"extensions":{"code":"NOT_FOUND"}}]}` in GraphQL. A GraphQL
quote selection exposes string `catalogRevision`, `pricingVersion` and
`Money.cents`, plus `Money.amount` and `Money.currency`; it does not expose the
stored owner. Invalid GraphQL selections fail before quote issuance.

These examples and [the pinned shape](../../contracts/pricing/quote-api-v1.json)
are compatibility inputs. Real MySQL/Spring consumer tests check serialized
field names, number/string representations, role boundaries and domain error
codes; source-only SDL inspection is a narrower check.

## Error contract and disclosure boundaries

| Condition | REST behavior | GraphQL resolver behavior |
| --- | --- | --- |
| Valid owned historical quote | 200, including after expiry | Quote result |
| Valid owned status observation | 200, UNEXPIRED or EXPIRED | QuoteStatus result |
| Unknown quote / other owner's quote | 404, code NOT_FOUND | errors[].extensions.code = NOT_FOUND |
| Bad date/ID or domain party count | 400, code INVALID_INPUT | INVALID_INPUT where resolver executes |
| No dated rate | 404, code NOT_FOUND | NOT_FOUND |
| Catalogue dependency unavailable | 503, code DEPENDENCY_UNAVAILABLE | DEPENDENCY_UNAVAILABLE, internal-error classification |
| Anonymous / wrong role | 401 / 403 from security filter | Denied private resolver; no quote data |
| New event hold from expired quote | 410, code EXPIRED (booking) | No booking mutation in this schema |

GraphQL syntax, missing required variables, and scalar-coercion errors occur before
our domain resolver. They use the engine's error envelope; no custom domain code is
promised for those errors. GraphQL resolver errors can return HTTP 200 with `errors`,
so a client must examine both. Validation details must not expose tokens or internals.
Spring argument-binding failures are sanitized to INVALID_INPUT rather than
surfacing field values. Anonymous GraphQL and REST authentication envelopes are
not promised to be byte-identical.

## Verification strategy and limits

- JDK core scenarios: fixed/stepping clocks, exact expiry, DST/park midnight,
  eligibility, range boundaries, immutable snapshots, read-only observation,
  original-order recovery, strict parsers and quote record-shape compatibility.
- `node --test scripts/pricing-contract.test.mjs`: 22 hand-entered examples plus
  384 deterministic bounded parties. A **separate BigInt rational oracle** checks
  literal expectations, then compares all monetary lines from the actual Java
  preview and quote calculation. The Java stores are test-only in-memory fixtures.
- SDL-source field checks retain pinned names/types from `quote-api-v1.json`.
  These are not a GraphQL engine, resolver build, serialization or browser test.
- `QuoteContractIT`: real MySQL/Spring HTTP/GraphQL tests with an explicit test
  catalogue and mutable clock. `scripts/demo-http.mjs` exercises the actual
  three-service stack. Both were executed for the fixed G-0009 source recorded
  in the [G-0009 review checkpoint](../../goals/G-0009/work/review-checkpoint.md); local targeted and
  public hosted results, source identities and limits are retained there.

## Primary implementation references

These clarify platform behavior; monetary and business rules above are project choices.

- Java 21 Clock / InstantSource: https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/InstantSource.html
- Java 21 ZoneId: https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/ZoneId.html
- Spring GraphQL annotated controllers and binding failures: https://docs.spring.io/spring-graphql/reference/controllers.html

Relevant framework versions are inherited from the existing candidate. No dependency
version was upgraded and no new product dependency was added in this slice.
