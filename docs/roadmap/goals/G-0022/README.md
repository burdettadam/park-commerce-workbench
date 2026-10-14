# G-0022 — Approve a bounded private AWS experiment

Milestone: M7
Depends on: G-0021
Proposal revision: proposal-1
Plan authority: NOT ACCEPTED
Risk profile: high

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0022/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

The owner can decide the exact cost, exposure and recovery tradeoffs before any cloud resource is created.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Design EKS/Aurora MySQL plus an explicit real-MongoDB deployment choice; enumerate network/identity/secrets/budget/retention/teardown requirements and obtain actual authorization.

## Exclusions

No account creation, cost-incurring apply, paid subscriptions, silent DocumentDB substitution or unapproved public endpoint.

## Preconditions and authority boundaries

Planning only until a separate human decision explicitly authorizes charges/resources. Roadmap adoption is not a cloud-spend approval.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Reviewed infrastructure-as-code proposal and cost/exposure/recovery decision.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0022-AC01

Document actual account/region/network/resource inventory and reviewed compatible versions, including the MongoDB hosting choice; dependency access and licensing are explicit.

Required evidence: `G-0022-A01` plus the relevant raw check, source S and independent review coverage.

### G-0022-AC02

Record priced assumptions with date, resource limits, maximum authorized spend, stop condition, owner and teardown steps; alerts alone are not represented as a guaranteed hard spending cap.

Required evidence: `G-0022-A02` plus the relevant raw check, source S and independent review coverage.

### G-0022-AC03

Specify measurable RPO/RTO and restoration consistency requirements across catalog, pricing and booking; explain backup retention and the consequences of deleting demo data.

Required evidence: `G-0022-A01` plus the relevant raw check, source S and independent review coverage.

### G-0022-AC04

The human records a scoped authorization with approved account, region, resource set, exposure and budget before G-0023 is active. Unapproved or insufficient access remains needs-decision/blocked, not a pass.

Required evidence: `G-0022-A03` plus the relevant raw check, source S and independent review coverage.

## Review evidence outputs (temporary and ignored)

The following IDs name expected review outputs, not files to commit. If generated, keep them under ignored `goals/G-0022/evidence/<candidate>/` only while review or a live handoff needs them. Record concise findings and hosted check links in the PR; remove local outputs after merge or terminal cancellation/supersession.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0022-A01` | `cloud-design.md` | Resources, topology, database choice, identities and recovery targets. |
| `G-0022-A02` | `cost-and-teardown-plan.md` | Dated estimate, limits, stop conditions and teardown inventory. |
| `G-0022-A03` | `cloud-authorization.md` | Actual human decision for scoped cloud operations. |

The accepted outcome still requires actual checks, independent reviews, intended-use validation and a decision. [The legacy manifest template](manifest.template.json) documents the older file format; do not fill or commit an evidence manifest. Follow the current [review evidence policy](../../ARTIFACT-CONTRACT.md) and link PR/CI records.

## Validation commands and new tests

No executable goal-specific command is claimed yet. Implement/review a reproducible harness or recorded manual procedure within this goal before asserting acceptance.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0022/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0022/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0022` | Unassigned integration owner | Not created |

Use one branch when that is sufficient; multiple branches are allowed, not mandatory overhead. Each writer updates only their branch record. Record exact included revisions and disposition for abandoned alternatives. A proposed branch name is not an existing branch or permission to push.

## Review and stop rules

The proposed baseline is two independent reviewers (contract and adversarial) and at most three rounds, subject to the actually accepted governance. Collect first findings independently. A relevant code/test/configuration/input change creates a new candidate and reopens affected verification and review. Retain valid blockers even when another reviewer disagrees. Missing reviews are not passes; unresolved blockers after the review limit require `needs-decision`.

Changes to these rules are reviewed under the previous accepted rules, never self-approved by the candidate. Version-producing merges require an actual human decision. Merely reaching `ready` does not create a release, deploy anything or change visibility.

## Handoff and recovery

Push source and branch checkpoints before the canonical coordination record, identify every required source component, and verify retrieval. Keep generated evidence ignored and local while review or a live handoff needs it, with concise findings and CI links in the PR. After merge or terminal cancellation/supersession, remove local generated outputs. Record exact source S and merged M separately; do not create an evidence-only commit E.

## Activation record — intentionally unfilled

- Accepted authority, plan commit and governing baseline: not recorded.
- Integration owner, actual ref and active writers: unassigned.
- Source candidate S / base / configuration / accepted plan identity: not recorded.
- Runtime state: not created; after adoption use the upstream planned/active/blocked/paused/ready/needs-decision/cancelled/superseded vocabulary.
- Verification, independent reviews, human validation and release: not performed by this proposal.
- Next action: accept the plan and satisfy dependencies, then select a bounded authorized slice.

[Milestone M7](../../milestones/M7.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
