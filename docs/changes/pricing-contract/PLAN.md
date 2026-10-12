# Pricing and quote contract — bounded local implementation candidate

Base: local catalog-publication commit `a9119af6db19e141d569bb97075243616436935a`.
Branch: `local/pricing-contract`; no remote is configured.
Authority: the user requested the next local development slice. This permits
candidate implementation, not adoption of the outstanding roadmap prerequisites,
independent reviewer approval, release, deployment, or publication.
Roadmap contribution: G-0009-AC01..AC04; G-0001..G-0008 acceptance is still outstanding.

## Observable outcome and preimplementation decisions

- Quote issuance uses exactly one server evaluation instant for park-local date
  eligibility, creation time and the existing fifteen-minute deadline. A slow
  dependency must not refresh the deadline or evaluate the same request on two dates.
  Existing previews use that same date-validation rule without issuing a quote.
- Add a read-only, owner-scoped quote status observation (UNEXPIRED or EXPIRED),
  including the immutable quote, evaluation instant and configured park zone.
  Expiry equality is expired. This is not a reservation, purchase, price refresh,
  proof of inventory, or guarantee the quote has not already been used.
- Retain existing Quote JSON fields and historical reads (including expired quotes),
  internal booking reads, and successful checkout recovery after quote expiry.
- REST and GraphQL quote input adapters share strict date/UUID parsing and domain
  failures. Valid existing requests are compatible. Malformed IDs and dates must
  fail as INVALID_INPUT, never be normalized to an unrelated UUID or a 500 error.
- USD only. Existing policy stays: at most twenty tickets, inclusive rate endpoints,
  one offer, threshold across party size, half-up discount per variant line, sum
  line discounts, zero-rate offer is not labeled applied, no tax/fee/currency claims.
- Preserve exact catalog and price versions and original snapshot amounts on reads.
- Retain hand-entered cents fixtures and an independent BigInt rational oracle,
  exercised against the actual Java preview/quote calculation. No new product
  dependencies, agent framework, paid infrastructure, or real identities/payments.

## Required assertions and artifacts

1. Reproduce the multi-read/midnight and dependency-delay defects before fixing.
2. Rate endpoint/gap/overlap, DST, park-midnight, exact expiry, ownership, snapshot
   and original-order recovery regressions; no deadline extension or status writes.
3. Hand-entered no-offer/threshold/mixed-party/rounding/maximum fixture outputs agree
   with an independently implemented integer oracle and both Java pricing paths.
4. Candidate REST/GraphQL tests cover field parity, authorization, malformed input,
   missing/expired state and immutable versions; mark unexecuted without real stack.
5. Run retained core/client/tooling/roadmap suites without weakening their gates.
6. Deliver fixed source S, raw commands/results, fixture pack, boundary/error contract,
   coverage limitations, later evidence E, full ZIP, patch and recoverable local bundle.

Two independent reviews and human acceptance remain pending. Writer self-checks
are not independent reviews. No goal is marked ready by this slice.
