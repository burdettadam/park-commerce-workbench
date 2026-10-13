# G-0014 — Finish the Angular journey and uncertain-checkout recovery

Milestone: M4
Depends on: G-0012
Proposal revision: proposal-2
Plan authority: NOT ACCEPTED
Risk profile: high

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0014/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

A guest selects valid admission/event options and can recover an uncertain checkout after a page reload without another charge or order.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Add event selection, bounded cart UI, order status and owner-bound durable retry metadata; design credential handling separately from retry keys.

## Exclusions

Do not store bearer tokens in localStorage, restore another user's pending checkout, or regenerate a key while the old outcome is uncertain.

## Preconditions and authority boundaries

Local synthetic-data work only; no deployment or external write authorization.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Angular complete guest flow and safe checkout-state persistence.
- Browser tests covering server-confirmed outcomes, not just local state objects.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0014-AC01

A real browser selects a valid event from server data, purchases the bounded cart and renders the backend's itemized quote and confirmed order.

Required evidence: `G-0014-A01` plus the relevant raw check, source S and independent review coverage.

### G-0014-AC02

Drop the checkout response, reload, restore owner/hold/key-bound nonsecret state after authentication and resolve the same outcome; no second order or extra allocation occurs.

Required evidence: `G-0014-A02` plus the relevant raw check, source S and independent review coverage.

### G-0014-AC03

Switch user, expire the hold, log out and simulate an unavailable backend. No cross-user recovery or stale-price confirmation occurs, and retry state is cleared/retained according to explicit rules.

Required evidence: `G-0014-A02` plus the relevant raw check, source S and independent review coverage.

### G-0014-AC04

Complete the main flow by keyboard and at a narrow viewport; retain automated accessibility findings and manual observations rather than claiming certification.

Required evidence: `G-0014-A03` plus the relevant raw check, source S and independent review coverage.

## Required goal-specific artifacts

Relative to the eventual candidate evidence directory, for example `goals/G-0014/evidence/<candidate>/`. These are output requirements, not generated evidence.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0014-A01` | `guest-browser-tests.md` | Actual selection/cart/confirmation path and recording. |
| `G-0014-A02` | `reload-and-retry-matrix.md` | Response-loss, reload, identity switch and expiry scenarios. |
| `G-0014-A03` | `guest-accessibility.md` | Keyboard, viewport and accessibility checks. |

The common manifest, environment, raw checks, independent contract/adversarial review reports, intended-use validation, decision and handoff are also mandatory. Start from [the unfilled manifest template](manifest.template.json) and [artifact contract](../../ARTIFACT-CONTRACT.md). The unfilled template deliberately fails the readiness checker.

## Validation commands and new tests

- `(cd storefront && npm ci && npm run build)` — existing. Uses the committed lockfile; repeat on the accepted candidate rather than relying on earlier CI.
- `(cd storefront && npm run start)` — existing. Real services and reviewed dependencies required; loopback development server.
- `node --test storefront/src/app/checkout-state.test.mjs` — existing. Existing dependency-free client-state tests, not Angular browser tests.
- `node scripts/demo-http.mjs` — existing. Requires running local services and .env; creates synthetic data.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0014/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0014/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0014` | Unassigned integration owner | Not created |

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

[Milestone M4](../../milestones/M4.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
