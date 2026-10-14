# G-0003 — Recover a goal across two worktrees and a session interruption

Milestone: M0
Depends on: G-0002
Proposal revision: proposal-1
Plan authority: NOT ACCEPTED
Risk profile: high

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0003/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

Another session resumes from retained goal, branch and artifact records without guessing which work is current.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Run a controlled workflow drill with an implementation slice and an independent test/documentation slice; record integration ownership, separate branch progress and a new clean reconstruction.

## Exclusions

No custom scheduler, agent command protocol, task database or automatic scope approval. No destructive cleanup until retrieval is proven.

## Preconditions and authority boundaries

Use local bundles/ZIPs or a verified-private remote only. A local archive is not automatically an off-device backup.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Active goal/slice records created under the adopted PLANS convention.
- Reviewed worktree resource-isolation notes and repeatable handoff instructions.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0003-AC01

Two named worktrees have nonconflicting ports, Compose project names and data volumes; each has one identified writer and progress record based on the same accepted plan revision.

Required evidence: `G-0003-A01` plus the relevant raw check, source S and independent review coverage.

### G-0003-AC02

Record at least two included branch commits, one explicit nonincluded alternative or change proposal, and the assembled candidate. An unaccepted scope change must not silently modify success criteria.

Required evidence: `G-0003-A01` plus the relevant raw check, source S and independent review coverage.

### G-0003-AC03

Interrupt the authoring session, preserve a checkpoint, and resume in a fresh directory/session using only retained records. Retrieve every indispensable artifact and match its digest before reporting complete recovery.

Required evidence: `G-0003-A02` plus the relevant raw check, source S and independent review coverage.

### G-0003-AC04

Deliberately present one stale review and one missing required artifact to the review process. Both remain blockers. Record actual gate observations, handoff duration and unresolved items without pretending this is a statistical model evaluation.

Required evidence: `G-0003-A03` plus the relevant raw check, source S and independent review coverage.

## Review evidence outputs (temporary and ignored)

The following IDs name expected review outputs, not files to commit. If generated, keep them under ignored `goals/G-0003/evidence/<candidate>/` only while the PR is open. Record concise findings and hosted check links in the PR; remove local outputs after merge or terminal cancellation/supersession.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0003-A01` | `branch-map.md` | Owners, worktree resources, input revisions and included/excluded branch commits. |
| `G-0003-A02` | `restore-drill.md` | Fresh reconstruction steps, digests, timing and remaining local-only state. |
| `G-0003-A03` | `workflow-observations.md` | Scope-change, stale-review and missing-evidence controls observed in the drill. |

The accepted outcome still requires actual checks, independent reviews, intended-use validation and a decision. [The legacy manifest template](manifest.template.json) documents the older file format; do not fill or commit an evidence manifest. Follow the current [review evidence policy](../../ARTIFACT-CONTRACT.md) and link PR/CI records.

## Validation commands and new tests

- `bash scripts/test-core.sh --demo` — existing. JDK 21 and Bash; in-memory domain adapters only.
- `node --test storefront/src/app/checkout-state.test.mjs` — existing. Existing dependency-free client-state tests, not Angular browser tests.
- `bash scripts/test-tooling.sh` — existing. Disposable fixtures/mock initializer only; not real upstream adoption.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0003/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0003/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0003` | Unassigned integration owner | Not created |

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

[Milestone M0](../../milestones/M0.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
