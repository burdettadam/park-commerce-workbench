# G-0009 — Freeze the price and quote contract with independent examples

Milestone: M2
Depends on: G-0008
Proposal revision: proposal-1
Plan authority: NOT ACCEPTED
Risk profile: high

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0009/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

Guest-facing prices have explicit, reproducible rules for amounts, discounts, dates, revisions and expiry.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Document and test price-calendar boundaries, park-local dates, rounding, offer combination policy, immutable quote identity and expiry.

## Exclusions

USD synthetic amounts only; no tax-compliance claim, real residency/age proof, multicurrency or dynamic revenue-management algorithm.

## Preconditions and authority boundaries

Local synthetic-data work only; no deployment or external write authorization.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Versioned pricing/quote contracts, fixture pack, boundary and consumer tests.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0009-AC01

A reviewed fixture pack covers no offer, exact threshold, one-below threshold, mixed adult/child quantities and every applicable rounding boundary using expected cents calculated independently of implementation.

Required evidence: `G-0009-A01` plus the relevant raw check, source S and independent review coverage.

### G-0009-AC02

Test date-range endpoints, invalid dates, park-local timezone transitions, exact quote-expiry instant and expired/unknown quote errors through the public API.

Required evidence: `G-0009-A02` plus the relevant raw check, source S and independent review coverage.

### G-0009-AC03

A quote identifies catalog and pricing versions and cannot be rewritten by new publications. Own/other-user/service-role access behaves consistently across REST and GraphQL.

Required evidence: `G-0009-A02` plus the relevant raw check, source S and independent review coverage.

### G-0009-AC04

Publish contract examples and explicit exclusions; retain schema/consumer tests that fail for an incompatible quote or error-shape change.

Required evidence: `G-0009-A03` plus the relevant raw check, source S and independent review coverage.

## Required goal-specific artifacts

Relative to the eventual candidate evidence directory, for example `goals/G-0009/evidence/<candidate>/`. These are output requirements, not generated evidence.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0009-A01` | `pricing-contract-fixtures.json` | Independent price inputs and expected outputs. |
| `G-0009-A02` | `quote-boundary-tests.md` | Actual time/ownership/version and API boundary results. |
| `G-0009-A03` | `contract-examples.md` | Documented public request/response/error examples and compatibility tests. |

The common manifest, environment, raw checks, independent contract/adversarial review reports, intended-use validation, decision and handoff are also mandatory. Start from [the unfilled manifest template](manifest.template.json) and [artifact contract](../../ARTIFACT-CONTRACT.md). The unfilled template deliberately fails the readiness checker.

## Validation commands and new tests

- `bash scripts/test-core.sh --demo` — existing. JDK 21 and Bash; in-memory domain adapters only.
- `bash scripts/verify.sh --integration` — existing. Maven/JDK/Docker required; real database tests must not silently skip.
- `node scripts/demo-http.mjs` — existing. Requires running local services and .env; creates synthetic data.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0009/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0009/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0009` | Unassigned integration owner | Not created |

Use one branch when that is sufficient; multiple branches are allowed, not mandatory overhead. Each writer updates only their branch record. Record exact included revisions and disposition for abandoned alternatives. A proposed branch name is not an existing branch or permission to push.

## Review and stop rules

The proposed baseline is two independent reviewers (contract and adversarial) and at most three rounds, subject to the actually accepted governance. Collect first findings independently. A relevant code/test/configuration/input change creates a new candidate and reopens affected verification and review. Retain valid blockers even when another reviewer disagrees. Missing reviews are not passes; unresolved blockers after the review limit require `needs-decision`.

Changes to these rules are reviewed under the previous accepted rules, never self-approved by the candidate. Version-producing merges require an actual human decision. Merely reaching `ready` does not create a release, deploy anything or change visibility.

## Handoff and recovery

Retain source/branch/artifact checkpoints before the canonical coordination record, identify every indispensable component, then prove retrieval. Until privacy is verified, use local artifacts and label coverage `local_only`; do not claim off-device recovery. Record source S separately from later evidence/report commit E so the report does not need its own future commit hash.

## Activation record — intentionally unfilled

- Accepted authority, plan commit and governing baseline: not recorded.
- Integration owner, actual ref and active writers: unassigned.
- Source candidate S / base / configuration / accepted plan identity: not recorded.
- Runtime state: not created; after adoption use the upstream planned/active/blocked/paused/ready/needs-decision/cancelled/superseded vocabulary.
- Verification, independent reviews, human validation and release: not performed by this proposal.
- Next action: accept the plan and satisfy dependencies, then select a bounded authorized slice.

[Milestone M2](../../milestones/M2.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
