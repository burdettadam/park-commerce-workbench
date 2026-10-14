# G-0001 — Adopt the actual initializer and accepted project rules

Milestone: M0
Depends on: none
Proposal revision: proposal-2
Plan authority: NOT ACCEPTED
Risk profile: high

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0001/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

A new agent session discovers the real work-goal/worktree-flow skills and can identify the accepted project rules without chat history.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Use the existing public worktree and generated guidance; manually verify the exact reviewed initializer version; run a fresh preview and a non-destructive rerun; adopt product documents, real commands, reviewers, and recovery responsibilities. Record the earlier import and initial application as historical evidence, not new work.

## Exclusions

Do not reimplement git-agent, copy its private executable into this project, invent a third skill, or change repository visibility as an implied step.

## Preconditions and authority boundaries

Bootstrap exception: the human reviews and adopts initial rules explicitly; an unadopted policy cannot approve itself. The current wrapper has an exact-version guard, but the reviewed executable must still be identified before it is used.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Generated native guidance from the real initializer, not this planning package.
- Reviewed links to docs/project/* and docs/roadmap/* from adopted PLANS/DEVELOPMENT guidance.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0001-AC01

Record the exact output of git agent --version and verify the reviewed version is exactly 0.2.0 before invoking the current wrapper; reject look-alike/prerelease versions. Run a fresh preview and safe rerun against this worktree root, retaining exit codes and adoption gaps. Link the historical first application separately.

Required evidence: `G-0001-A01` plus the relevant raw check, source S and independent review coverage.

### G-0001-AC02

A fresh native agent/editor session identifies the generated instruction entry point, work-goal/worktree-flow skills and two reviewer roles; retain observable discovery output rather than merely listing files.

Required evidence: `G-0001-A02` plus the relevant raw check, source S and independent review coverage.

### G-0001-AC03

The human adopts or explicitly rejects each product-command, owner, review, isolation and release-control choice. No required UNCONFIGURED field remains for the first local goal; unresolved hosted controls are recorded as remote-work blockers.

Required evidence: `G-0001-A01` plus the relevant raw check, source S and independent review coverage.

### G-0001-AC04

The accepted PLANS guidance points to this roadmap and one canonical active-goal location. No .env, private executable or secret enters tracked files. Record the actual public repository, protected-branch, CI, and human-review controls before claiming remote recovery or release safety.

Required evidence: `G-0001-A03` plus the relevant raw check, source S and independent review coverage.

## Review evidence outputs (temporary and ignored)

The following IDs name expected review outputs, not files to commit. If generated, keep them under ignored `goals/G-0001/evidence/<candidate>/` only while review or a live handoff needs them. Record concise findings and hosted check links in the PR; remove local outputs after merge or terminal cancellation/supersession.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0001-A01` | `adoption.md` | Initializer invocation, actual output, gap decisions and accepted authority. |
| `G-0001-A02` | `discovery.txt` | Observed native instruction/skill/role discovery, with unavailable checks stated. |
| `G-0001-A03` | `controls-and-privacy.md` | Local-only boundary, required external controls and current privacy observation when available. |

The accepted outcome still requires actual checks, independent reviews, intended-use validation and a decision. [The legacy manifest template](manifest.template.json) documents the older file format; do not fill or commit an evidence manifest. Follow the current [review evidence policy](../../ARTIFACT-CONTRACT.md) and link PR/CI records.

## Validation commands and new tests

- `git agent --version` — existing. Reviewed private initializer must already be installed; manually verify exact 0.2.0.
- `bash scripts/init-agent.sh --preview` — existing. Requires the intended Git worktree and exact reviewed initializer; the current exact-version guard has mock coverage.
- `bash scripts/init-agent.sh --apply` — existing. Rerun is allowed after preview review; inspect the file plan and preserve any proposed/conflicting guidance.
- `bash scripts/test-tooling.sh` — existing. Disposable fixtures/mock initializer only; not real upstream adoption.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0001/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0001/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0001` | Unassigned integration owner | Not created |

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

[Milestone M0](../../milestones/M0.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
