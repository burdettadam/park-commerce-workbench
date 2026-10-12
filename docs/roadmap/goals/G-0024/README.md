# G-0024 — Restore the platform and verify cloud teardown

Milestone: M7
Depends on: G-0023
Proposal revision: proposal-1
Plan authority: NOT ACCEPTED
Risk profile: high

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0024/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

The owner can recover consistent commerce data and remove the paid demonstration with a retained audit trail.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Run actual backup/restore of the approved stores, rebuild deployment inputs and verify resource teardown with protected retained evidence.

## Exclusions

No destructive reset of unrelated resources, no claim of zero final charges based only on an empty resource list, and no untested backup-as-recovery claim.

## Preconditions and authority boundaries

Restore targets and deletion require explicit ownership and authorization; preserve required backups/evidence before destructive actions.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Executed backup/restore and teardown runbooks with durable evidence.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0024-AC01

Restore the selected catalog, price/quote and booking checkpoints into an isolated approved target and reconcile cross-service identities and order/capacity/entitlement totals.

Required evidence: `G-0024-A01` plus the relevant raw check, source S and independent review coverage.

### G-0024-AC02

Measure actual data loss/recovery time against accepted G-0022 RPO/RTO; a missed target is a failed criterion or a separately approved changed plan, not silently redefined success.

Required evidence: `G-0024-A01` plus the relevant raw check, source S and independent review coverage.

### G-0024-AC03

Rebuild application configuration and artifact identity from retained references without chat history or workstation-local-only state; verify indispensable artifact downloads/digests.

Required evidence: `G-0024-A02` plus the relevant raw check, source S and independent review coverage.

### G-0024-AC04

After human-authorized teardown, enumerate remaining compute, database, load balancer, storage, snapshots/logs and identity resources, with intentional retained items and billing follow-up owner.

Required evidence: `G-0024-A03` plus the relevant raw check, source S and independent review coverage.

## Required goal-specific artifacts

Relative to the eventual candidate evidence directory, for example `goals/G-0024/evidence/<candidate>/`. These are output requirements, not generated evidence.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0024-A01` | `restore-results.md` | Observed recovery time/data loss and invariant reconciliation. |
| `G-0024-A02` | `environment-reconstruction.md` | Fresh restoration of configuration/artifact references. |
| `G-0024-A03` | `teardown-inventory.md` | Removed/retained resources, cost consequences and follow-up. |

The common manifest, environment, raw checks, independent contract/adversarial review reports, intended-use validation, decision and handoff are also mandatory. Start from [the unfilled manifest template](manifest.template.json) and [artifact contract](../../ARTIFACT-CONTRACT.md). The unfilled template deliberately fails the readiness checker.

## Validation commands and new tests

No executable goal-specific command is claimed yet. Implement/review a reproducible harness or recorded manual procedure within this goal before asserting acceptance.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0024/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0024/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0024` | Unassigned integration owner | Not created |

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

[Milestone M7](../../milestones/M7.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
