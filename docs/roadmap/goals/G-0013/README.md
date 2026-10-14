# G-0013 — Finish the Vaadin employee workbench

Milestone: M4
Depends on: G-0012
Proposal revision: proposal-1
Plan authority: NOT ACCEPTED
Risk profile: medium

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0013/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

An employee completes draft, what-if preview, publication, history and event-capacity work without direct API calls.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Wire the completed catalog/pricing/booking capabilities into real Vaadin screens with form validation, conflict handling and audit navigation.

## Exclusions

No ornamental admin mockup, bypass of API authorization, or direct database writes from the UI.

## Preconditions and authority boundaries

Local synthetic-data work only; no deployment or external write authorization.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Vaadin workbench screens and reproducible browser tests.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0013-AC01

A recorded browser test drafts a product, previews a price, publishes a revision, configures an event and finds the resulting audit entry.

Required evidence: `G-0013-A01` plus the relevant raw check, source S and independent review coverage.

### G-0013-AC02

Demonstrate stale-edit conflict, invalid input, unavailable service and forbidden action with recoverable UI state and no silent data loss.

Required evidence: `G-0013-A02` plus the relevant raw check, source S and independent review coverage.

### G-0013-AC03

Core tasks are operable by keyboard with labeled controls and useful focus/error feedback. Record tested viewport sizes and actual accessibility findings.

Required evidence: `G-0013-A03` plus the relevant raw check, source S and independent review coverage.

### G-0013-AC04

The UI displays preview/published identities distinctly and does not report success before the backend confirms it; API tests still deny unauthorized writes.

Required evidence: `G-0013-A02` plus the relevant raw check, source S and independent review coverage.

## Review evidence outputs (temporary and ignored)

The following IDs name expected review outputs, not files to commit. If generated, keep them under ignored `goals/G-0013/evidence/<candidate>/` only while the PR is open. Record concise findings and hosted check links in the PR; remove local outputs after feature code merges.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0013-A01` | `employee-browser-tests.md` | Executed browser flow and recording references. |
| `G-0013-A02` | `employee-error-cases.md` | Conflict, service-failure and permission evidence. |
| `G-0013-A03` | `employee-accessibility.md` | Manual keyboard and automated findings with disposition. |

The accepted outcome still requires actual checks, independent reviews, intended-use validation and a decision. [The legacy manifest template](manifest.template.json) documents the older file format; do not fill or commit an evidence manifest. Follow the current [review evidence policy](../../ARTIFACT-CONTRACT.md) and link PR/CI records.

## Validation commands and new tests

- `mvn -B -ntp -Pui -pl admin-console -am verify` — proposed_validation. Uses existing Maven profile; must be executed and repaired in G-0004. Do not skip required tests.
- `mvn -Pui -pl admin-console spring-boot:run` — existing. Requires installed sibling modules/running APIs as documented in START-HERE.
- `bash scripts/verify.sh --integration` — existing. Maven/JDK/Docker required; real database tests must not silently skip.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0013/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0013/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0013` | Unassigned integration owner | Not created |

Use one branch when that is sufficient; multiple branches are allowed, not mandatory overhead. Each writer updates only their branch record. Record exact included revisions and disposition for abandoned alternatives. A proposed branch name is not an existing branch or permission to push.

## Review and stop rules

The proposed baseline is two independent reviewers (contract and adversarial) and at most three rounds, subject to the actually accepted governance. Collect first findings independently. A relevant code/test/configuration/input change creates a new candidate and reopens affected verification and review. Retain valid blockers even when another reviewer disagrees. Missing reviews are not passes; unresolved blockers after the review limit require `needs-decision`.

Changes to these rules are reviewed under the previous accepted rules, never self-approved by the candidate. Version-producing merges require an actual human decision. Merely reaching `ready` does not create a release, deploy anything or change visibility.

## Handoff and recovery

Push source and branch checkpoints before the canonical coordination record, identify every required source component, and verify retrieval. Keep generated evidence ignored and local while the PR is open, with concise findings and CI links in the PR. After feature code merges, remove local generated outputs. Record exact source S and merged M separately; do not create an evidence-only commit E.

## Activation record — intentionally unfilled

- Accepted authority, plan commit and governing baseline: not recorded.
- Integration owner, actual ref and active writers: unassigned.
- Source candidate S / base / configuration / accepted plan identity: not recorded.
- Runtime state: not created; after adoption use the upstream planned/active/blocked/paused/ready/needs-decision/cancelled/superseded vocabulary.
- Verification, independent reviews, human validation and release: not performed by this proposal.
- Next action: accept the plan and satisfy dependencies, then select a bounded authorized slice.

[Milestone M4](../../milestones/M4.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
