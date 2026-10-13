# G-0009 activation, dependency and preparatory audit

## Authority and ownership

The user's 2026-10-13 request to make an active goal for subagents on the next three goals adopts the unchanged G-0009 roadmap proposal-1. Proposal SHA-256: `f506c61802cc81355f76ba7132ef57e1042022506df2b2f84699bd8b4fcc4a67`. Accepted activation commit P: `ae52a4564ff732994578a7d698713d042e2bf287`. The `goals/G-0009/README.md` bytes at P have SHA-256 `09b837e4294b77cc79e4e12e13eccba389de2e540b9639fc7726169b668e501d`. All four criterion outcome and required-evidence paragraphs match the proposal verbatim. The active plan adds assigned ownership, baseline, cost boundary and a dependency gate.

The assigned G-0009 subagent is sole writer and integration owner of `goal/G-0009` in `.tmp/g0009-planning`, based on `bd26cfbd062228c228223518d3800cd125c4e9b1`. Adam Burdett retains scope, architecture, repository administration and proposed intended-use validation authority. No implementation or validation subbranch has been assigned. The current checkout was clean before the activation file was added. Applicable project instructions are root `AGENTS.md` and its linked governance, development, architecture and plan files; no nested `AGENTS.md` was observed in the affected paths. The `work-goal` and `worktree-flow` skills apply. No changes to these controls are proposed.

## Dependency and execution state

Status: **blocked** for product execution. G-0008 must deliver an accepted fixed candidate with its exact source and plan revisions. At this checkpoint G-0008 has an implementation worktree and an integration worktree, but neither is represented here as an accepted result. The integration owner must record accepted G-0008 identities, inspect the actual diff and refresh this branch before selecting product work. No pricing service, database, browser, paid runner or external endpoint was started for G-0009 planning. The local `.env` and other goals' services are not assigned to this goal.

The free-only boundary allows local tools and standard free runners for this public repo. This planning record claims no G-0009 source candidate S, executed product test, independent review, intended-use validation, readiness decision, merge, release or deployment. Its source publication checkpoint and retrieval must be recorded only after the branch is actually pushed and checked.

## Existing candidate and focused gaps

The base already contains a pricing candidate. `core/src/main/java/dev/parkcommerce/core/Pricing.java` uses one `calculate` path for preview and quote; `Money.java` uses integer cents and half-up discount rounding on each variant line. `QuoteContract.java` shares strict date and UUID parsing between REST and GraphQL adapters. `contracts/pricing/pricing-contract-fixtures.json` has 22 hand-entered cases, and `scripts/pricing-contract.test.mjs` uses a separate BigInt rational oracle plus 384 generated parties. These are candidate source, not G-0009 acceptance evidence or reviewer approval.

`core/src/test/java/dev/parkcommerce/core/PricingContractScenarios.java` covers range endpoints, gaps and adjacency, literal park-local/DST instants, quote deadline equality and immutable snapshots with in-memory stores. `pricing-service/src/test/java/dev/parkcommerce/pricing/QuoteContractIT.java` contains real MySQL/Spring REST/GraphQL boundary tests, but its rate fixture is one day. AC02 still needs an executed public API test of multi-day inclusive endpoints and gaps. `contracts/pricing/quote-api-v1.json` and the Node tests pin GraphQL SDL source fields; source text checks alone do not establish AC04's serialized consumer/error-shape compatibility. Add executable REST and GraphQL response/error checks that would fail for incompatible changes, preserving their intentionally different money representations and envelopes. Check actual selected test counts and any skips when execution begins; the candidate documentation says the integration tests were not executed for this slice.

Architectural boundary: keep monetary calculation canonical in core and keep the independent BigInt oracle test-only. REST and GraphQL should delegate common parsing, ownership and quote retrieval to the same domain rules; do not clone formulas or turn status reads into persisted quote updates. Reassess these observations against the accepted G-0008 source rather than assuming the base stays fixed. Treat hardcoded `America/New_York` as the currently documented single-park zone, not a validated multi-park abstraction.

## Bounded next work and evidence

After the G-0008 gate, freeze accepted plan P and candidate S, select focused core/fixture and API tests, execute the real integration lane without silently skipped MySQL tests, and use an owned local stack for live HTTP exercise if required. Retain `G-0009-A01` independent price inputs/results, `G-0009-A02` API calendar/expiry/ownership/version observations and `G-0009-A03` public contract examples/consumer checks with raw logs, environment and source identities. Then obtain both independent reviewers under the three-round ceiling and an actual intended-use validation outcome or explicit delegation. Publication of this planning branch alone is not off-device recovery of later source/evidence components.

Planning-only check: `node scripts/validate-roadmap.mjs` passed with 10 milestones, 30 goals, 120 criteria and 90 goal-specific artifact requirements. A direct comparison found AC01–AC04 outcome and required-evidence text identical to proposal-1. Neither check ran G-0009 product behavior.

Next action: G-0008 integration owner supplies accepted exact revisions; G-0009 integration owner records the dependency, refreshes this branch and assigns a bounded product slice.
