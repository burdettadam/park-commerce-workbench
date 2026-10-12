# G-0011 — Purchase admission as a separate entitlement type

Milestone: M3
Depends on: G-0010
Proposal revision: proposal-1
Plan authority: NOT ACCEPTED
Risk profile: high

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0011/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

A guest can buy dated admission without incorrectly consuming evening-event capacity.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Add dated admission checkout and a fictional admission entitlement record owned by booking; preserve the event flow.

## Exclusions

No QR scanner, mobile credential, real park access, per-person identity proof, external payment or invented admission capacity.

## Preconditions and authority boundaries

Local synthetic-data work only; no deployment or external write authorization.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Booking admission entitlement model/migration/API and tests.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0011-AC01

An admission-only quote confirms an owner-scoped order and one defined entitlement set; repeating the key returns the original result.

Required evidence: `G-0011-A01` plus the relevant raw check, source S and independent review coverage.

### G-0011-AC02

Admission checkout never reads or decrements an unrelated event capacity pool. Invalid product/date/version and another owner's quote are rejected before mutation.

Required evidence: `G-0011-A02` plus the relevant raw check, source S and independent review coverage.

### G-0011-AC03

Document active/void entitlement states and audit links to order/quote; the future cancellation contract is explicit and old event clients remain compatible.

Required evidence: `G-0011-A03` plus the relevant raw check, source S and independent review coverage.

### G-0011-AC04

Migrate existing event-only data in a disposable upgrade fixture and demonstrate both old event and new admission orders can be retrieved correctly.

Required evidence: `G-0011-A02` plus the relevant raw check, source S and independent review coverage.

## Required goal-specific artifacts

Relative to the eventual candidate evidence directory, for example `goals/G-0011/evidence/<candidate>/`. These are output requirements, not generated evidence.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0011-A01` | `admission-examples.json` | Synthetic quote/order/entitlement examples and expected counts. |
| `G-0011-A02` | `admission-isolation-tests.md` | No event-capacity consumption, ownership and upgrade evidence. |
| `G-0011-A03` | `entitlement-contract.md` | Bounded entitlement lifecycle and audit rules. |

The common manifest, environment, raw checks, independent contract/adversarial review reports, intended-use validation, decision and handoff are also mandatory. Start from [the unfilled manifest template](manifest.template.json) and [artifact contract](../../ARTIFACT-CONTRACT.md). The unfilled template deliberately fails the readiness checker.

## Validation commands and new tests

- `bash scripts/verify.sh --integration` — existing. Maven/JDK/Docker required; real database tests must not silently skip.
- `node scripts/demo-http.mjs` — existing. Requires running local services and .env; creates synthetic data.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0011/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0011/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0011` | Unassigned integration owner | Not created |

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

[Milestone M3](../../milestones/M3.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
