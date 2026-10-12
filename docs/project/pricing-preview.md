# Unpublished pricing preview

Status: implementation candidate. The shared Java calculation and no-store-access contract have been exercised locally; the Spring HTTP endpoint, authentication filters, MySQL tests and Vaadin client still need a dependency build and live execution.

## Business outcome

An employee can change the rates in the pricing form and see a family's price **before publication**. Preview evaluates submitted input against the pinned immutable catalog revision. It does not consult the latest published price book, issue a quote, or touch booking/inventory.

The endpoint is `POST /api/admin/price-books/preview`, JSON request and HTTP 200 response. Both the existing `/api/admin/**` rule and an employee method-authorization annotation protect it in the local profile. Anonymous callers should receive 401; guests and internal-service credentials should receive 403. Those HTTP assertions are included but unexecuted here. Nonlocal profiles remain closed by default.

## Example request

Publish the referenced fictional catalog product first. This date matches the fixed offline fixture; a live test needs a nonpast visit date in America/New_York and matching rate dates.

```json
{
  "candidate": {
    "productId": "lantern-evening",
    "version": 2,
    "catalogRevision": 1,
    "rates": [
      {"variant":"ADULT","from":"2026-10-31","through":"2026-10-31","unitPrice":{"cents":10000}},
      {"variant":"CHILD","from":"2026-10-31","through":"2026-10-31","unitPrice":{"cents":7000}}
    ],
    "offer": {"code":"family-four","minimumTickets":4,"discountBasisPoints":1000}
  },
  "request": {"productId":"lantern-evening","visitDate":"2026-10-31","adults":2,"children":2}
}
```

The response echoes `candidate` and `request`, with `mode: UNPUBLISHED_INPUT`, `candidateId`, product name/kind, itemized lines, applied offer, subtotal, discount and total. For this input, adult subtotal is 20,000 cents and child subtotal 14,000 cents. The two discounts are 2,000 and 1,400 cents. Total: **30,600 cents ($306.00)**.

There is deliberately no quote ID, owner, creation time or expiry. Booking accepts only an actual persisted quote ID, not the candidate fingerprint. `UNPUBLISHED_INPUT` describes the submitted what-if input: it is not an assertion that an identical payload has never been published by another session.

## Semantics and invariants

- Preview and quote issuance call the same calculation method. Rates are inclusive at both ends; price rules cannot overlap for one variant. No uncovered date or unavailable requested variant receives a guessed rate.
- USD remains the only currency. Each variant subtotal is discounted once and rounded half-up to a cent; discounts are then summed. The response preserves the existing monetary `cents` contract.
- A candidate must reference the same product as the party request and a real, immutable catalog revision. All rates, including unused variants, must be supported by that revision. Missing or mismatched upstream catalog payloads fail closed.
- Parties contain 1–20 total tickets. Past dates are evaluated using America/New_York, not UTC or browser time. The HTTP suite includes fractional-count rejection.
- Preview validates structural and monetary rules, **not publication eligibility**. It may inspect a stale or future version. The existing publication endpoint still checks the next version and handles competing publishers. Preview does not reserve that version.
- Publishing the identical candidate produces matching monetary results for the identical party/date, provided publication succeeds and the visit remains valid. A changed candidate requires a new preview; the UI displays the exact candidate identity to make that distinction visible.
- Existing issued quotes keep their original price. Preview has no access to a booking store and does not call any method on `Pricing.Store`.

## Candidate identity specification

`candidateId` is `sha256:` plus a lowercase 64-hex digest. It is a content identifier, not a signature, access token, accepted-plan identity or authorization to publish.

The bytes are written in this exact order. Integers use Java DataOutputStream big-endian encoding. Text is UTF-8 preceded by its 32-bit byte length; 64-bit values are signed longs.

1. Text `park-commerce-price-book-v1`, product ID, 64-bit proposed pricing version, 64-bit catalog revision.
2. 32-bit count of rates; for each rate **in supplied list order**: variant name text, 64-bit start epoch-day, 64-bit through epoch-day, 64-bit unit cents.
3. One boolean byte for offer presence; if present: offer-code text, 32-bit minimum-ticket count, 32-bit discount basis points.

Rate order is intentionally retained: two reordered payloads may calculate the same price yet have different exact-input identities. Party/date input is echoed separately and is not part of the price-book fingerprint. Future encoding changes need a new version marker and compatibility tests.

The example above has fingerprint `sha256:0cea663b21d35d72cc9ea7bf166890e2195a5fff54fe5649cb7a08ff49b69e3f`. A separate Python byte encoder supplied the golden expected value; the Java test checks it.

## Maintained checks

`bash scripts/test-core.sh --demo` runs the same named cases used by the JUnit bridge, including no-store-call traps, independent expected money, preview/publication parity, timezone boundaries and a coordinated concurrent-publication case.

`bash scripts/verify.sh --integration` includes four new MySQL/HTTP tests for preview in `JpaPricingIT`. The HTTP tests use the real Spring server, filters and MySQL persistence; their catalog client is a fixed test double, not a live MongoDB service.

`node scripts/demo-http.mjs` adds four preview checks to the cross-service exercise, using actual running catalog/pricing/booking services. Unlike the unit preview itself, this demonstration intentionally creates synthetic products, price books, quotes and orders. It takes no real payment.

The Vaadin preview button submits the currently entered rates. It no longer calls `/api/quotes` or creates an employee-owned quote. Browser validation and framework compilation remain outstanding.
