# Catalog drafts, publication and history

**Status:** implemented local candidate under G-0007 review. At fixed source
`15c70f15154997f6eda7b72ad70aa3eba76e5fdd`, 17 real catalog Mongo/Spring
integration tests, the 43-check live HTTP run, and a focused production Vaadin
browser flow passed on 2026-10-13. The [G-0007 evidence](https://github.com/burdettadam/park-commerce-workbench/tree/90ae8eff6485c950c8d02c8d3a33893059f5d82e/goals/G-0007/evidence/15c70f1)
records the results and the earlier failed browser attempts. Final independent
review found an evidence privacy issue; G-0007 is `needs-decision` after the accepted
round limit. This is independent fictional commerce software.

## Two different counters

A product has a sequence of immutable **draft versions** and a separate sequence
of immutable **published revisions**. Both start at 1. A draft additionally pins
the published revision it was reviewed against; 0 means no publication existed.

Saving a draft appends a new version. It does not modify guest REST/GraphQL reads,
prices, quotes, inventory, or old draft content. Publishing selects an exact retained
draft, derives its product contents on the server, and inserts the next published
revision with its audit metadata. It does not delete or mark other drafts as published.

The old `POST /api/admin/products` request/response remains supported for setup
clients. It publishes immediately and now records the authenticated employee actor.
The new console uses the explicit draft route instead.

## Employee API

Every route below is under `/api/admin/products/{id}` and requires EMPLOYEE access.
New editorial responses send `Cache-Control: no-store`. The service derives actor
from authenticated `Principal`; the request cannot assign an actor or timestamp.
The local demo still has one shared `employee` subject, not production staff identity.

| Method and suffix | Purpose | Success |
| --- | --- | --- |
| `POST /drafts` | Save immutable next draft with two explicit preconditions | 201 + draft |
| `GET /drafts/latest` | Read latest saved draft, even if its base is now stale | 200 + draft |
| `GET /drafts/{version}` | Read one immutable saved draft | 200 + draft |
| `POST /publications` | Publish a selected draft against its pinned published base | 201 + publication |
| `GET /history?limit=10&cursor=...` | Page through published snapshots and audit metadata | 200 + history page |

Initial save body (omit neither version field; explicit 0 is significant):

```json
{
  "expectedDraftVersion": 0,
  "basePublishedRevision": 0,
  "content": {"name": "Lantern Evening", "kind": "EVENT", "variants": ["ADULT", "CHILD"]}
}
```

After reviewing returned draft 1, publish it:

```json
{"draftVersion": 1, "expectedPublishedRevision": 0}
```

After a competing draft save, reload and review before saving with the new
`expectedDraftVersion`. After a competing publication, review the current published
snapshot and save a *new* draft based on it before publication. The server never
silently rebases a command. Numeric version fields are explicit and nonnegative
(with positive selected draft version); missing/null preconditions and fractional
version numbers are rejected. Unknown input fields are rejected by the existing
catalog Jackson configuration.

| Condition | HTTP outcome |
| --- | --- |
| No valid credentials | 401 |
| Guest/internal credential accessing employee API | 403 |
| Missing draft | 404 |
| Stale draft predecessor, stale published base, or duplicate next revision | 409 |
| Invalid command/cursor/limit | 400 |
| Well-formed history request for unknown or draft-only product | 200, empty items, snapshot 0 |

Guest endpoints return the original `Catalog.Product` DTO only, without draft/audit
metadata. Guest GraphQL remains unchanged and cannot query editorial drafts.

## Exact-content and concurrency rules

Saving expects the latest draft version. Two writers that both read version 3
attempt insert of version 4; the unique `(productId, version)` key permits only
one. Publishing similarly appends the next product revision with the existing
unique `(productId, revision)` key. A failed publication does not write a separate
orphan audit event: its audit and product snapshot are in the same document.

**A publication intentionally selects the requested retained draft**, even if a
later draft was saved meanwhile. It never swaps in the newest draft's content.
If the selected draft pins a stale published base, publication fails.

Draft saves and publications are independent append-only streams, not a global
transaction or a lock on both streams. A publication can happen after a save's
base check and before its insert; the returned draft then pins the older base and
must be explicitly rebased before publication. Neither stream overwrites the other.
Tests force this interleaving. No retry loop silently changes expected versions.

A timeout/unknown response is not proof that an insert failed. Read the latest
saved draft or published history before retrying. Repeating a successful command
with its old precondition conflicts rather than creating another revision. There
is no automatic idempotent success replay for editorial commands in this slice.

## History cursor contract

History is ordered by **published revision descending**, never by timestamp.
`limit` is 1-50, default 10. The first page captures the current highest published
revision as `snapshotRevision`. The continuation query is bounded by that snapshot
and the last returned revision, with a `limit+1` lookahead. It uses a range query,
not an offset that shifts when new rows are inserted.

Subsequent pages keep the same upper bound even as new publications arrive. A new
first-page request is required to see newer revisions. Page size may change within
a traversal; repeating a cursor at the same size returns the same retained rows.
`nextCursor: null` means the traversal is finished. The snapshot applies to published
history only, not to a simultaneous snapshot of draft state or any other service.

Cursor encoding is versioned, canonical unpadded URL-safe Base64 containing product
ID, upper revision and exclusive page boundary. Clients should retain the returned
value, not construct one. Server validation rejects malformed/oversized/noncanonical
encodings, unsupported versions, mismatched products, invalid ranges and missing
boundary revisions. Cursors **are not signed**, not confidential, and not credentials;
an employee may intentionally construct another valid range. Authorization still
applies to every request. No cryptographic tamper-resistance claim is made.

The range queries use the existing compound publication index; drafts have a separate
compound index. Snapshot stability assumes normal append-only API writes and retained
history. Administrative deletion or external direct mutation is outside this contract.

## Storage compatibility and audit

`product_revisions` keeps its existing collection, ID (`product:revision`), product
payload, and unique index. Two additive nullable fields carry source draft version
and audit. No destructive migration, collection rewrite, or transaction requirement
was introduced. `product_drafts` is new; each draft, content and audit is one document.

Audit fields are actor subject, operation, product ID, previous/new stream versions,
and millisecond UTC timestamp. For `SAVE_DRAFT` the versions identify drafts;
for `PUBLISH_DRAFT` and `DIRECT_PUBLISH` they identify published revisions. The
publication separately identifies `sourceDraftVersion`. No token or full content
payload is duplicated into the audit object.

Old/unattributed snapshots remain readable with `auditStatus: LEGACY_UNAVAILABLE`,
`audit: null`, and no invented actor/time. New employee HTTP writes record audit.
The old in-process `Catalog.Service.publish`/raw Store insertion remains used by
legacy/bootstrap test fixtures and does not retroactively establish authenticated
provenance. Do not use that route for future production employee writes.

No draft/history deletion, retention policy, change feed, global audit search,
multi-employee identity provider, or external event bus is implemented here.

## Employee console candidate

The Vaadin source now saves drafts, loads their name/kind/variants, publishes the
loaded/saved version only after checking that the displayed fields still match,
and displays publication history with continuation. Draft and published-base
counters are visibly separate. A conflict requests reload/review; the console does
not auto-rebase. A missing response advises inspection before retrying. History
rendering uses text components rather than executing content as markup.

The G-0007 employee browser walkthrough verified edited-form rejection, a stale
employee conflict, reviewed publication, guest invisibility, history and API response
handling. It did not independently test keyboard navigation or both variant
configurations. Existing pricing UI still emits both
adult and child rates; an adults-only product requires a matching price-book request
through the pricing API until that separate editor limitation is addressed.

## Supporting primary references

These explain the design primitives, not evidence that this application's Mongo
adapter has run:

- MongoDB 8.0 atomicity and unique-write semantics:
  https://www.mongodb.com/docs/v8.0/core/write-operations-atomicity/
- Spring Data MongoDB Query, Criteria, sort and limit APIs:
  https://docs.spring.io/spring-data/mongodb/reference/mongodb/template-query-operations.html
- Vaadin checkbox/checkbox-group component:
  https://vaadin.com/docs/latest/components/checkbox

No framework/dependency versions were upgraded by this slice.
