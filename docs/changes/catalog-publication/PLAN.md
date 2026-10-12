# Catalog publication — bounded local implementation candidate

Input: checkout-recovery archive and local commit `9686f2a` (full identity in verification).
Work branch: `local/catalog-publication`. No remote is configured.
Authority: the user's request to continue local development authorizes this implementation slice,
not acceptance of outstanding prerequisites, independent reviews, a release, or remote publication.
Roadmap contribution: G-0007-AC01..AC04. G-0001..G-0006 and goal adoption remain unresolved.

## Observable outcome

An employee saves immutable, versioned catalog drafts, reviews one exact draft,
publishes that content against its recorded published base, and pages through
publication history. Drafts never appear in guest catalog/GraphQL reads.

## Boundaries decided before implementation

- Draft versions and published revisions are distinct append-only streams per product.
  Each stream rejects two writes with the same expected predecessor using unique keys.
- Publishing is an explicit choice of a retained draft version, not an alias for latest.
  A newer draft can coexist; publication cannot mark or delete that newer draft.
- A draft pins its base published revision. A competing publication makes that draft
  stale; the user must save a new draft based on the new published revision. No automatic rebase.
- Saving and publishing different immutable records can interleave. This is not an
  atomic cross-stream transaction. A just-saved draft may already need rebase if
  publication raced its base check. Publishing still fails closed on a stale base.
- Audit metadata is embedded in the same insert as its draft or published snapshot.
  No second audit write, mutable published flag, unbounded aggregate, or new transaction infrastructure.
- Keep existing guest routes/DTOs and immediate employee publication endpoint compatible.
  New HTTP writes derive actor from authenticated Principal; no client actor field.
  Legacy snapshots without audit data remain readable and explicitly identify missing audit.
- Publication history uses bounded keyset pagination, descending revision, and a pinned
  upper revision. Later inserts do not change an existing traversal. Cursor is validated,
  product-bound, and opaque to clients, but NOT a credential or cryptographically signed claim.
- No deletions, real identities/payments, deployment, external writes, new dependencies,
  or claims that in-memory checks prove Mongo/Spring/Vaadin behavior.

## Expected acceptance assertions

1. Draft save is guest-invisible; publishing exact content creates one immutable revision.
2. Duplicate/stale draft saves and publications yield conflicts without overwrite.
3. Independent clocks or newly arriving revisions do not reorder/duplicate history pages;
   first, middle, last, empty, malformed, wrong-product and out-of-range cursors are covered.
4. New draft/publication records contain authenticated actor, operation, product,
   previous/new version and server timestamp; old records do not acquire invented audit.
5. Existing pricing, checkout, client recovery, tooling and roadmap suites still pass.
6. Deliver real-Mongo/HTTP test source and live script checks, but label them unexecuted
   unless the actual infrastructure becomes available.

## Evidence and handoff

Retain failing missing-feature tests before implementation; fixed source S, raw commands,
results and actual toolchain; later report-only E; scope/contract and concurrency notes;
coverage and independent-review gaps. Run existing validators without modifying their gates.
Package full ZIP, incremental patch against checkout-recovery, and separate local Git bundle.
