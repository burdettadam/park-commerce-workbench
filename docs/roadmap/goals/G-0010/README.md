# G-0010 — Prove capacity and idempotency under real contention

Milestone: M3
Depends on: G-0009
Proposal revision: proposal-1
Plan authority: NOT ACCEPTED
Risk profile: high

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0010/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

Real database concurrency and retries cannot oversell an event, lose an accepted order or consume capacity twice.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Stress actual booking transactions over HTTP and separate database connections; cover expiry/confirmation races and application interruption.

## Exclusions

No throughput extrapolation from synchronized in-memory stores; no network call while holding an inventory row lock.

## Preconditions and authority boundaries

Local synthetic-data work only; no deployment or external write authorization.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Real contention/failure harness, invariant assertions and any transactional repairs.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0010-AC01

Race at least 100 real hold requests for 10 available seats. Exactly 10 or fewer seats may be held/sold, never more; reconcile final ledger totals and expected rejections.

Required evidence: `G-0010-A01` plus the relevant raw check, source S and independent review coverage.

### G-0010-AC02

Replay the same authenticated checkout key concurrently and after an uncertain response. Observe one order, one transition and one capacity consumption; different payload under that key returns a conflict.

Required evidence: `G-0010-A01` plus the relevant raw check, source S and independent review coverage.

### G-0010-AC03

Exercise hold-expiry versus checkout, rollback, and an actual process interruption around commit/response. Restart and reconcile confirmed, expired and available counts without manual database edits.

Required evidence: `G-0010-A02` plus the relevant raw check, source S and independent review coverage.

### G-0010-AC04

Trace/inspect lock scope and prove dependency fetches complete before the booking database lock. Retain transaction isolation/locking rationale and real SQL evidence.

Required evidence: `G-0010-A03` plus the relevant raw check, source S and independent review coverage.

## Review evidence outputs (temporary and ignored)

The following IDs name expected review outputs, not files to commit. If generated, keep them under ignored `goals/G-0010/evidence/<candidate>/` only while the PR is open. Record concise findings and hosted check links in the PR; remove local outputs after merge or terminal cancellation/supersession.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0010-A01` | `contention-results.json` | Workload, request outcomes and reconciled ledger counts. |
| `G-0010-A02` | `interruption-recovery.md` | Kill/restart and expiry/confirmation-race evidence. |
| `G-0010-A03` | `transaction-design.md` | Isolation, lock scope, query and constraint analysis. |

The accepted outcome still requires actual checks, independent reviews, intended-use validation and a decision. [The legacy manifest template](manifest.template.json) documents the older file format; do not fill or commit an evidence manifest. Follow the current [review evidence policy](../../ARTIFACT-CONTRACT.md) and link PR/CI records.

## Validation commands and new tests

- `bash scripts/verify.sh --integration` — existing. Maven/JDK/Docker required; real database tests must not silently skip.
- `node scripts/demo-http.mjs` — existing. Requires running local services and .env; creates synthetic data.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0010/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0010/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0010` | Unassigned integration owner | Not created |

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

[Milestone M3](../../milestones/M3.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
