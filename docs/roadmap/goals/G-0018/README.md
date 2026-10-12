# G-0018 — Measure SQL contention and process-level recovery

Milestone: M5
Depends on: G-0017
Proposal revision: proposal-1
Plan authority: NOT ACCEPTED
Risk profile: high

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0018/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

The operator can explain the bottleneck and recover after an application/database fault with reconciled commerce state.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Profile the real booking/price queries, test indexing and actual process interruption, and record a bounded reference workload.

## Exclusions

No production SLA claims, destructive fault injection on shared/cloud data, or substitution of object reconstruction for process recovery.

## Preconditions and authority boundaries

Local synthetic-data work only; no deployment or external write authorization.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Operational SQL/report tests, reproducible workload/fault fixtures and recovery runbook.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0018-AC01

Retain EXPLAIN/query-plan output and indexes for a representative operational report and the critical booking path; document examined cardinalities and lock waits.

Required evidence: `G-0018-A01` plus the relevant raw check, source S and independent review coverage.

### G-0018-AC02

Run a fixed concurrent workload with declared dataset, machine, client count and duration; retain raw counts/latency/error distributions, including expected sellouts separately from unexpected errors.

Required evidence: `G-0018-A02` plus the relevant raw check, source S and independent review coverage.

### G-0018-AC03

Interrupt a local application process and a disposable database at specified points, restore service, and reconcile orders/holds/entitlements/capacity against an independent ledger oracle.

Required evidence: `G-0018-A03` plus the relevant raw check, source S and independent review coverage.

### G-0018-AC04

A proposed performance change is compared against the same inputs, with correctness suites rerun. Negative/no-improvement results remain reported rather than selectively discarded.

Required evidence: `G-0018-A02` plus the relevant raw check, source S and independent review coverage.

## Required goal-specific artifacts

Relative to the eventual candidate evidence directory, for example `goals/G-0018/evidence/<candidate>/`. These are output requirements, not generated evidence.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0018-A01` | `sql-analysis.md` | Queries, indexes, actual plans and lock-wait observations. |
| `G-0018-A02` | `reference-workload.json` | Fixed workload inputs and measured results, including failed attempts. |
| `G-0018-A03` | `process-recovery.md` | Actual interruption, restoration and invariant reconciliation. |

The common manifest, environment, raw checks, independent contract/adversarial review reports, intended-use validation, decision and handoff are also mandatory. Start from [the unfilled manifest template](manifest.template.json) and [artifact contract](../../ARTIFACT-CONTRACT.md). The unfilled template deliberately fails the readiness checker.

## Validation commands and new tests

- `bash scripts/verify.sh --integration` — existing. Maven/JDK/Docker required; real database tests must not silently skip.
- `node scripts/demo-http.mjs` — existing. Requires running local services and .env; creates synthetic data.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0018/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0018/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0018` | Unassigned integration owner | Not created |

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

[Milestone M5](../../milestones/M5.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
