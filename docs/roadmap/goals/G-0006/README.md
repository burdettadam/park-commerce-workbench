# G-0006 — Demonstrate the original live event purchase flow

Milestone: M1
Depends on: G-0005
Proposal revision: proposal-1
Plan authority: NOT ACCEPTED
Risk profile: high

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0006/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

An employee publication produces a real guest quote, hold and retry-safe order through actual services and browsers.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Run the Compose stack, existing live HTTP exercise, Vaadin console and Angular storefront for the existing one-event quote boundary.

## Exclusions

Do not introduce admission checkout or a combined cart yet. A source screenshot is not a running browser demonstration.

## Preconditions and authority boundaries

Local synthetic-data work only; no deployment or external write authorization.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Working local Compose/HTTP/browser path; framework defects fixed within scope.
- Updated START-HERE instructions based on actual execution.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0006-AC01

Compose services become healthy and all existing live HTTP assertions execute (13 in the foundation), including role checks, ownership, price-version preservation and competing holds.

Required evidence: `G-0006-A01` plus the relevant raw check, source S and independent review coverage.

### G-0006-AC02

Through the real Vaadin interface publish a product/rate/event, and through Angular obtain a quote and confirm an order. Record the browser steps and corresponding API/order identifiers.

Required evidence: `G-0006-A02` plus the relevant raw check, source S and independent review coverage.

### G-0006-AC03

Publish a new price after an earlier quote and prove that the still-valid earlier quote keeps its original total. Retry the same checkout and observe one order and one capacity consumption.

Required evidence: `G-0006-A01` plus the relevant raw check, source S and independent review coverage.

### G-0006-AC04

Stop the local stack without deleting volumes; restart and retrieve the confirmed order. Record framework/binding repairs and any remaining demo-only auth limitations.

Required evidence: `G-0006-A03` plus the relevant raw check, source S and independent review coverage.

## Required goal-specific artifacts

Relative to the eventual candidate evidence directory, for example `goals/G-0006/evidence/<candidate>/`. These are output requirements, not generated evidence.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0006-A01` | `live-http.txt` | Executed live HTTP assertions and synthetic request/order IDs. |
| `G-0006-A02` | `browser-walkthrough.md` | Actual Vaadin/Angular steps with screenshots/video references. |
| `G-0006-A03` | `restart-and-limitations.md` | Volume-preserving restart and current local-only limitations. |

The common manifest, environment, raw checks, independent contract/adversarial review reports, intended-use validation, decision and handoff are also mandatory. Start from [the unfilled manifest template](manifest.template.json) and [artifact contract](../../ARTIFACT-CONTRACT.md). The unfilled template deliberately fails the readiness checker.

## Validation commands and new tests

- `bash scripts/configure-local.sh` — existing. Generates local secrets; refuses existing .env; do not log or commit secrets.
- `docker compose config --quiet` — existing. Validate without printing rendered secrets; runtime still unverified.
- `docker compose up --build -d --wait` — existing. Local containers/downloads; own project/ports/volumes required.
- `node scripts/demo-http.mjs` — existing. Requires running local services and .env; creates synthetic data.
- `mvn -Pui -pl admin-console spring-boot:run` — existing. Requires installed sibling modules/running APIs as documented in START-HERE.
- `(cd storefront && npm run start)` — existing. Real services and reviewed dependencies required; loopback development server.
- `docker compose down` — existing. Stops project without intentionally deleting named volumes; never add -v casually.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0006/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0006/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0006` | Unassigned integration owner | Not created |

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

[Milestone M1](../../milestones/M1.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
