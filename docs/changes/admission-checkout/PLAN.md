# Admission-only checkout — bounded local candidate

Base: pricing-contract delivery `48bcaa5` (full base recorded in verification).
Branch: `local/admission-checkout`; no remote configured.
Authority: user requested continued local development and a ZIP handoff. This
permits candidate code, not adoption of roadmap prerequisites, independent review,
release, spending, cloud deployment, or remote publication.
Contribution: G-0011. G-0010 real-database acceptance and prior prerequisites remain
outstanding. This is NOT an accepted/ready goal record.

## Contract fixed before implementation

- Confirm a server-issued ADMISSION quote using authenticated owner + original
  idempotency key. The client supplies no price, product, date or quantities.
- One immutable admission order holds the exact quote and one entitlement per
  nonempty variant line, with the line quantity. No personal identities or scanner.
- A single durable snapshot includes all entitlement IDs: never publish a partial
  order/entitlement set. Event tables, orders, holds and endpoints remain unchanged.
- New endpoint key scope is `(admission-orders, owner, case-sensitive key)`.
  Event keys remain separate, unchanged. Clients must retain the route as well as
  the key. One admission quote can produce only one order even with a new key.
- An identical retry returns the original order, without pricing access, even
  after quote expiry. A different quote under the same key or a re-keyed consumed
  quote conflicts. Different owners have independent keys.
- Validate upstream snapshot identity, ownership, kind, versions, line/aggregate
  arithmetic and dates before issuing. Capture acceptance time inside the store's
  new-order callback after dependency lookup; equality with expiry is expired.
  This is attempt evaluation time, NOT a promise about database commit latency.
- Admission has no inventory dependency, artificial capacity or event allocation.
- Reads/recovery are owner scoped and never create an order. NOT_CONFIRMED is only
  an observation: it cannot prove that an in-flight confirmation will not commit.
- ACTIVE means issued and not voided, not proof of validity for another date.
  VOID is reserved in the lifecycle contract; no cancellation transition ships.
- Add a new Flyway migration, never edit V1. Prove V1-to-V2 and mixed event/admission
  retrieval in a disposable integration fixture when infrastructure is available.

## Required artifacts and acceptance checks

Core regression cases, literal synthetic examples, persistence/ownership contract,
new HTTP/MySQL tests, additive migration, live demo, source S and later evidence E.
Run existing core/client/pricing/tooling/roadmap checks without dropping tests.
Record actual framework/runtime failures as limitations, not as skipped passes.
No browser admission UI, cart, payment service, cancellation or deployment in scope.
Two independent reviewer reports and human validation remain pending.
