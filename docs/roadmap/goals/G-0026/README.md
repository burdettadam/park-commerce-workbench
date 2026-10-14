# G-0026 — Diagnose a Java business transaction in AppDynamics

Milestone: M8
Depends on: G-0017
Proposal revision: proposal-1
Plan authority: NOT ACCEPTED
Risk profile: medium

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0026/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

The project demonstrates actual AppDynamics instrumentation and a repeatable slow/failing transaction investigation.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Verify authorized controller/agent and Java compatibility, instrument a selected Spring service and run one controlled diagnosis.

## Exclusions

Do not treat agent startup, OpenTelemetry output or a vendor screenshot as proof of an AppDynamics transaction trace.

## Preconditions and authority boundaries

No agent license or paid controller provision without explicit approval; never commit controller credentials.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Reviewed AppDynamics configuration and executed operational exercise.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0026-AC01

Verify the selected Java agent/controller combination against the actual JDK/service baseline, then retain a successful connection and detected application transaction.

Required evidence: `G-0026-A01` plus the relevant raw check, source S and independent review coverage.

### G-0026-AC02

Generate a declared slow/failing workload and locate the corresponding business transaction with actual timing and downstream diagnostics.

Required evidence: `G-0026-A02` plus the relevant raw check, source S and independent review coverage.

### G-0026-AC03

Compare the diagnosed cause with the independent injected-fault oracle and record the investigation, overhead observation and any instrumentation conflicts.

Required evidence: `G-0026-A02` plus the relevant raw check, source S and independent review coverage.

### G-0026-AC04

Record licensing/access/credentials/retention decisions and a reversible uninstall/disable procedure; unavailable access remains a visible blocker, not equivalent-tool coverage.

Required evidence: `G-0026-A03` plus the relevant raw check, source S and independent review coverage.

## Review evidence outputs (temporary and ignored)

The following IDs name expected review outputs, not files to commit. If generated, keep them under ignored `goals/G-0026/evidence/<candidate>/` only while the PR is open. Record concise findings and hosted check links in the PR; remove local outputs after feature code merges.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0026-A01` | `appdynamics-setup.md` | Actual compatibility, controller access and transaction discovery. |
| `G-0026-A02` | `transaction-diagnosis.md` | Measured business transaction and induced-fault investigation. |
| `G-0026-A03` | `agent-lifecycle.md` | Access/retention, reversible installation and residual limitations. |

The accepted outcome still requires actual checks, independent reviews, intended-use validation and a decision. [The legacy manifest template](manifest.template.json) documents the older file format; do not fill or commit an evidence manifest. Follow the current [review evidence policy](../../ARTIFACT-CONTRACT.md) and link PR/CI records.

## Validation commands and new tests

- `node scripts/demo-http.mjs` — existing. Requires running local services and .env; creates synthetic data.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0026/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0026/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0026` | Unassigned integration owner | Not created |

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

[Milestone M8](../../milestones/M8.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
