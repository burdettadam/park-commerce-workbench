> **Pricing contract extension:** [status query, strict input parsing, time and monetary rules](pricing-contract.md). Existing Quote fields/historical reads remain; intentionally tightened inputs require explicit quantities and full IDs.

# API contracts

The following describes the local candidate source. G-0009 has executed real MySQL/Spring integration and live HTTP checks for the quote boundary; their exact source, coverage and limits are in [G-0009 evidence](../../goals/G-0009/evidence/bede018/). Other interfaces still require the checks selected by their own goals.

## Authentication

In the explicit `local` Spring profile only, use `Authorization: Bearer <token>`. `scripts/configure-local.sh` creates four distinct random tokens: employee, guest, other-guest, and service. Subjects are fixed synthetic identities; tokens are not customer accounts or production OAuth credentials.

Public catalog reads and event availability require no token. Publication requires EMPLOYEE; internal quote access requires INTERNAL. Normal quote/hold/order operations require GUEST or EMPLOYEE and apply owner checks. Private GraphQL resolvers use method authorization in addition to the public GraphQL HTTP endpoint. Without `local`, backend requests are denied by default.

## Catalog · port 8081

| Method/path | Role | Payload / result |
| --- | --- | --- |
| `GET /api/products` | Public | Up to 100 latest product snapshots |
| `GET /api/products/{id}` | Public | Latest product snapshot |
| `GET /api/products/{id}/revisions/{revision}` | Public | Immutable historical snapshot |
| `POST /api/admin/products` | Employee | Product; HTTP 201 |
| `POST /graphql` | Public read schema | Catalog queries |

```json
{"id":"lantern-evening","revision":1,"name":"Lantern Evening","kind":"EVENT","variants":["ADULT","CHILD"]}
```

## Pricing · port 8082

| Method/path | Role | Payload / result |
| --- | --- | --- |
| `POST /api/admin/price-books` | Employee | PriceBook; HTTP 201 |
| `GET /api/admin/price-books/{productId}` | Employee | Latest PriceBook |
| `POST /api/quotes` | Guest / employee | QuoteRequest; HTTP 201 with immutable Quote |
| `GET /api/quotes/{uuid}` | Owner | Quote, including expired history |
| `GET /api/internal/quotes/{uuid}` | Service | Internal immutable quote |
| `POST /graphql` | Resolver-dependent | `createQuote(input)` and owner-only `quote(id)` |

```json
{"productId":"lantern-evening","version":1,"catalogRevision":1,"rates":[{"variant":"ADULT","from":"2030-10-31","through":"2030-10-31","unitPrice":{"cents":10000}},{"variant":"CHILD","from":"2030-10-31","through":"2030-10-31","unitPrice":{"cents":7000}}],"offer":{"code":"family-four","minimumTickets":4,"discountBasisPoints":1000}}
```

```json
{"productId":"lantern-evening","visitDate":"2030-10-31","adults":2,"children":2}
```

Choose a real future date inside your published rate window when running manually. The live demonstration generates a date automatically.

REST money is `{"cents":10000}`. GraphQL deliberately exposes cents as a string, with a formatted decimal amount and currency, avoiding GraphQL's signed 32-bit `Int` monetary limit. Versions are also GraphQL strings. The authoritative calculations remain in Java.

## Booking · port 8083

| Method/path | Role | Payload / result |
| --- | --- | --- |
| `POST /api/admin/events` | Employee | Event; HTTP 201 |
| `GET /api/events/{id}/availability` | Public | Capacity, confirmed, held, available |
| `POST /api/holds` | Quote owner | `{eventId, quoteId}`; HTTP 201 |
| `POST /api/orders` | Hold owner | `{holdId}`, `Idempotency-Key` header; HTTP 200 for initial and replay |
| `GET /api/orders/{uuid}` | Owner | Stored order |

```json
{"id":"lantern-oct31","productId":"lantern-evening","visitDate":"2030-10-31","capacity":20}
```

Event **`visitDate`** is the JSON property; the event occurrence ID is distinct from its product ID. The API never accepts a client-supplied final order amount.

Idempotency keys are 1-100 characters from `[A-Za-z0-9._-]`, case-sensitive, owner-scoped, and bound to the hold. Client retries must reuse the same key. A successful order retry after the hold's expiry still returns the previously completed order. A new key for an already confirmed hold conflicts.

## Errors

REST returns problem JSON. Domain codes: `INVALID_INPUT` 400; `NOT_FOUND` 404; `FORBIDDEN` 403; `CONFLICT` or `SOLD_OUT` 409; `EXPIRED` 410; `DEPENDENCY_UNAVAILABLE` 503. Unauthenticated protected requests return 401. Wrong-owner resources are hidden as 404. GraphQL carries resolver failures in `errors`; an HTTP 200 GraphQL response alone is not success.

No reserved capacity is promised by a quote. No payment is charged by confirmation. Repeating a product or price publication is a version conflict, not a retry-idempotent create. Publication retry reconciliation is future work.


## Catalog editorial extension

The original guest Product DTO and immediate employee publication route remain compatible.
Employee-only drafts, pinned publication and history are specified in [catalog-publication.md](catalog-publication.md).
New HTTP writes record Principal-derived audit; legacy snapshots without provenance remain explicitly unknown.
