# G-0016 — Verify cross-service contracts and bounded dependency failures

Milestone: M5
Depends on: G-0015
Proposal revision: proposal-1
Plan authority: NOT ACCEPTED
Risk profile: high

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0016/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

A stale or failing service cannot cause silent price corruption, unauthorized results or unbounded waits.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Add provider/consumer contract checks, deterministic HTTP fault injection, timeout budgets, request/query limits and safe error correlation.

## Exclusions

No silent retry of non-idempotent operations, massive generic resilience framework or change of success criteria to fit observed failures.

## Preconditions and authority boundaries

Local synthetic-data work only; no deployment or external write authorization.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Real HTTP contract/fault tests, bounded request/timeout policies and safe errors.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0016-AC01

Consumer/provider tests reject incompatible quote fields, bad identity/date/quantity/amount and error shapes before booking mutation; retain negative fixtures.

Required evidence: `G-0016-A01` plus the relevant raw check, source S and independent review coverage.

### G-0016-AC02

Timeout, disconnect, malformed response and dependency 5xx exercises complete within the accepted timeout budget and leave inventory/order state unchanged when confirmation is not committed.

Required evidence: `G-0016-A02` plus the relevant raw check, source S and independent review coverage.

### G-0016-AC03

Retry policy identifies retryable/idempotent operations and preserves keys. Large bodies and excessive GraphQL complexity are rejected under reviewed numeric limits without leaking internal exceptions.

Required evidence: `G-0016-A02` plus the relevant raw check, source S and independent review coverage.

### G-0016-AC04

Relevant provider changes invalidate dependent consumer evidence; retain a compatibility diff and the exact dependent revisions rerun.

Required evidence: `G-0016-A03` plus the relevant raw check, source S and independent review coverage.

## Review evidence outputs (temporary and ignored)

The following IDs name expected review outputs, not files to commit. If generated, keep them under ignored `goals/G-0016/evidence/<candidate>/` only while the PR is open. Record concise findings and hosted check links in the PR; remove local outputs after merge or terminal cancellation/supersession.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0016-A01` | `contract-suite.md` | Provider/consumer results and known-invalid payload fixtures. |
| `G-0016-A02` | `dependency-faults.json` | Injected failure, expected state, observed timing and outcome. |
| `G-0016-A03` | `compatibility-impact.md` | API diffs, dependencies and refreshed evidence coverage. |

The accepted outcome still requires actual checks, independent reviews, intended-use validation and a decision. [The legacy manifest template](manifest.template.json) documents the older file format; do not fill or commit an evidence manifest. Follow the current [review evidence policy](../../ARTIFACT-CONTRACT.md) and link PR/CI records.

## Validation commands and new tests

- `bash scripts/verify.sh --integration` — existing. Maven/JDK/Docker required; real database tests must not silently skip.
- `node scripts/demo-http.mjs` — existing. Requires running local services and .env; creates synthetic data.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0016/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0016/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0016` | Unassigned integration owner | Not created |

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

[Milestone M5](../../milestones/M5.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
