# G-0010 — Prove capacity and idempotency under real contention

Milestone: M3
Depends on: G-0009
Accepted plan: unchanged `proposal-1` by the owner's 2026-10-13 request to make the next three roadmap goals active for subagent work.
Source proposal: `docs/roadmap/goals/G-0010/README.md` at base `bd26cfbd062228c228223518d3800cd125c4e9b1`; SHA-256 `352c315ef7851e98a15fe7f342f69c383f0813ed5aa7990e93bd8e8bf435be8b`.
Governing baseline: `GOVERNANCE.md`, `DEVELOPMENT.md`, `ARCHITECTURE.md`, and `PLANS.md` at that base, with the unchanged accepted criteria below.
Risk profile: high

This is the active canonical record on `goal/G-0010`, owned for integration and writing by the assigned G-0010 Codex subagent. **Execution is blocked:** accept G-0009 at an exact revision, refresh this branch against its accepted inputs, and record those identities before product implementation, acceptance checks, or readiness claims. The roadmap proposal remains the historical source. The actual plan commit is recorded in `work/bootstrap.md`.

## Intended user and observable outcome

Real database concurrency and retries cannot oversell an event, lose an accepted order or consume capacity twice.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Stress actual booking transactions over HTTP and separate database connections; cover expiry/confirmation races and application interruption.

## Exclusions

No throughput extrapolation from synchronized in-memory stores; no network call while holding an inventory row lock.

## Preconditions and authority boundaries

Local synthetic-data work only; no deployment or external write authorization. Use only free local tooling and free public-repository GitHub Actions standard runners; no paid workflow, hosted service, or paid runner is authorized.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Real contention/failure harness, invariant assertions and any transactional repairs.

## Acceptance criteria

The outcomes below are accepted contractual tests, not observations of completed work. Do not weaken or delete a criterion because the current implementation fails it.

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

## Required goal-specific artifacts

Relative to the eventual candidate evidence directory, for example `goals/G-0010/evidence/<candidate>/`. These are output requirements, not generated evidence.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0010-A01` | `contention-results.json` | Workload, request outcomes and reconciled ledger counts. |
| `G-0010-A02` | `interruption-recovery.md` | Kill/restart and expiry/confirmation-race evidence. |
| `G-0010-A03` | `transaction-design.md` | Isolation, lock scope, query and constraint analysis. |

The common manifest, environment, raw checks, independent contract/adversarial review reports, intended-use validation, decision and handoff are also mandatory. Start from [the unfilled manifest template](../../docs/roadmap/goals/G-0010/manifest.template.json) and [artifact contract](../../docs/roadmap/ARTIFACT-CONTRACT.md). The unfilled template deliberately fails the readiness checker.

## Validation commands and new tests

- `bash scripts/verify.sh --integration` — existing. Maven/JDK/Docker required; real database tests must not silently skip.
- `node scripts/demo-http.mjs` — existing. Requires running local services and .env; creates synthetic data.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0010-implementation` | Unassigned pending G-0009 acceptance | Not created |
| Tests/evidence or independent alternative | `goal/G-0010-validation` | Unassigned pending G-0009 acceptance | Not created |
| Canonical integration | `goal/G-0010` | Assigned G-0010 Codex subagent | Planning record only |

Use one branch when that is sufficient; multiple branches are allowed, not mandatory overhead. Each writer updates only their branch record. Record exact included revisions and disposition for abandoned alternatives. A proposed branch name is not an existing branch or permission to push.

## Review and stop rules

The accepted baseline is two independent reviewers (contract and adversarial) and at most three rounds, subject to the governing policy. Collect first findings independently. A relevant code/test/configuration/input change creates a new candidate and reopens affected verification and review. Retain valid blockers even when another reviewer disagrees. Missing reviews are not passes; unresolved blockers after the review limit require `needs-decision`.

Changes to these rules are reviewed under the previous accepted rules, never self-approved by the candidate. Version-producing merges require an actual human decision. Merely reaching `ready` does not create a release, deploy anything or change visibility.

## Handoff and recovery

Retain source/branch/artifact checkpoints before the canonical coordination record, identify every indispensable component, then prove retrieval. Until privacy is verified, use local artifacts and label coverage `local_only`; do not claim off-device recovery. Record source S separately from later evidence/report commit E so the report does not need its own future commit hash.

## Activation and dependency record

- Accepted authority: owner's 2026-10-13 request for the next three roadmap goals to become active for subagent work; unchanged `proposal-1` acceptance criteria. Exact plan commit and digest are in `work/bootstrap.md`.
- Integration owner, actual ref and active writer: assigned G-0010 Codex subagent, `goal/G-0010`. No implementation or validation writer is assigned while the prerequisite remains open.
- Dependency gate: G-0009 is not accepted at an exact revision. Do not claim its pricing/quote contract, evidence, or independent reviews as settled.
- Source candidate S / base / configuration / accepted plan identity: not recorded.
- Runtime state: **blocked** on G-0009 acceptance and input pinning; this planning branch is an active coordination record only.
- Verification, independent reviews, human validation and release: not performed by this planning record.
- Next action: retain the read-only gap audit in `work/bootstrap.md`; after G-0009 acceptance, pin its exact revision and select a bounded authorized slice.

[Milestone M3](../../docs/roadmap/milestones/M3.md) · [Roadmap](../../docs/roadmap/README.md) · [Commands](../../docs/roadmap/COMMANDS.md)
