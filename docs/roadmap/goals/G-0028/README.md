# G-0028 — Benchmark the fixed release candidate honestly

Milestone: M9
Depends on: G-0021
Proposal revision: proposal-1
Plan authority: NOT ACCEPTED
Risk profile: high

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0028/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

The portfolio reports reproducible workload results and remaining performance limits for the actual artifact.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Benchmark a frozen candidate on an explicitly recorded local/private environment, including steady workload, contention and operational errors.

## Exclusions

No extrapolated production throughput or cloud rerun without authorization; no post-hoc lowering of thresholds to declare a pass.

## Preconditions and authority boundaries

Default local/private benchmark only. Cloud cost or higher load on any shared resource needs separate approval.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Repeatable load harness/dataset and measured performance report.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0028-AC01

Before execution, accept hardware/dataset/warm-up and workload limits. Proposed starting target: 20 concurrent clients for 10 minutes after warm-up; quote p95 <=500 ms, checkout p95 <=1000 ms and unexpected error rate <0.1%. These are proposed project targets, not production SLAs.

Required evidence: `G-0028-A01` plus the relevant raw check, source S and independent review coverage.

### G-0028-AC02

Retain raw samples/counts for three runs with identical inputs, including p50/p95/p99, throughput and errors. Expected sellouts/conflicts are reported separately rather than hidden in the error denominator.

Required evidence: `G-0028-A02` plus the relevant raw check, source S and independent review coverage.

### G-0028-AC03

After every run independently reconcile orders, entitlements, holds and capacity: zero oversell, duplicate confirmation or cross-owner leakage is mandatory regardless of latency.

Required evidence: `G-0028-A02` plus the relevant raw check, source S and independent review coverage.

### G-0028-AC04

Publish all run results, limitations and any target miss. A changed workload/target/hardware requires a new accepted plan revision and new evidence, not relabeling the old result.

Required evidence: `G-0028-A03` plus the relevant raw check, source S and independent review coverage.

## Required goal-specific artifacts

Relative to the eventual candidate evidence directory, for example `goals/G-0028/evidence/<candidate>/`. These are output requirements, not generated evidence.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0028-A01` | `benchmark-plan.md` | Accepted workload, hardware, independent targets and expected outcomes. |
| `G-0028-A02` | `benchmark-results.md` | Raw dataset references and complete observed distributions/invariant checks. |
| `G-0028-A03` | `benchmark-analysis.md` | Reproducibility, limitations and all failed or revised attempts. |

The common manifest, environment, raw checks, independent contract/adversarial review reports, intended-use validation, decision and handoff are also mandatory. Start from [the unfilled manifest template](manifest.template.json) and [artifact contract](../../ARTIFACT-CONTRACT.md). The unfilled template deliberately fails the readiness checker.

## Validation commands and new tests

- `node scripts/demo-http.mjs` — existing. Requires running local services and .env; creates synthetic data.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0028/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0028/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0028` | Unassigned integration owner | Not created |

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

[Milestone M9](../../milestones/M9.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
