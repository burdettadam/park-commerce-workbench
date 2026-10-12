# G-0001 — Adopt the actual initializer and accepted project rules

Milestone: M0
Depends on: none
Proposal revision: proposal-1
Plan authority: NOT ACCEPTED
Risk profile: high

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0001/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

A new agent session discovers the real work-goal/worktree-flow skills and can identify the accepted project rules without chat history.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Import the source into the intended local worktree; manually verify the exact reviewed initializer version; run preview/application; adopt product documents, real commands, reviewers, and recovery responsibilities.

## Exclusions

Do not reimplement git-agent, copy its private executable into this project, invent a third skill, or change repository visibility as an implied step.

## Preconditions and authority boundaries

Bootstrap exception: the human reviews and adopts initial rules explicitly; an unadopted policy cannot approve itself. Manually inspect the exact version before using the known-weak wrapper.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Generated native guidance from the real initializer, not this planning package.
- Reviewed links to docs/project/* and docs/roadmap/* from adopted PLANS/DEVELOPMENT guidance.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0001-AC01

Record the exact output of git agent --version and verify the reviewed version is exactly 0.2.0 before invoking the current wrapper; reject look-alike/prerelease versions. Run preview and application against this worktree root, retaining actual exit codes and proposed adoption gaps.

Required evidence: `G-0001-A01` plus the relevant raw check, source S and independent review coverage.

### G-0001-AC02

A fresh native agent/editor session identifies the generated instruction entry point, work-goal/worktree-flow skills and two reviewer roles; retain observable discovery output rather than merely listing files.

Required evidence: `G-0001-A02` plus the relevant raw check, source S and independent review coverage.

### G-0001-AC03

The human adopts or explicitly rejects each product-command, owner, review, isolation and release-control choice. No required UNCONFIGURED field remains for the first local goal; unresolved hosted controls are recorded as remote-work blockers.

Required evidence: `G-0001-A01` plus the relevant raw check, source S and independent review coverage.

### G-0001-AC04

The accepted PLANS guidance points to this roadmap and one canonical active-goal location. No .env, private executable or secret enters tracked files, and every remote write remains blocked until current privacy is verified.

Required evidence: `G-0001-A03` plus the relevant raw check, source S and independent review coverage.

## Required goal-specific artifacts

Relative to the eventual candidate evidence directory, for example `goals/G-0001/evidence/<candidate>/`. These are output requirements, not generated evidence.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0001-A01` | `adoption.md` | Initializer invocation, actual output, gap decisions and accepted authority. |
| `G-0001-A02` | `discovery.txt` | Observed native instruction/skill/role discovery, with unavailable checks stated. |
| `G-0001-A03` | `controls-and-privacy.md` | Local-only boundary, required external controls and current privacy observation when available. |

The common manifest, environment, raw checks, independent contract/adversarial review reports, intended-use validation, decision and handoff are also mandatory. Start from [the unfilled manifest template](manifest.template.json) and [artifact contract](../../ARTIFACT-CONTRACT.md). The unfilled template deliberately fails the readiness checker.

## Validation commands and new tests

- `git agent --version` — existing. Reviewed private initializer must already be installed; manually verify exact 0.2.0.
- `bash scripts/init-agent.sh --preview` — existing. Requires the intended Git worktree and exact reviewed initializer; wrapper defect G-0002 remains until repaired.
- `bash scripts/init-agent.sh --apply` — existing. Writes actual guidance locally; only after manual exact-version and preview review.
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

Retain source/branch/artifact checkpoints before the canonical coordination record, identify every indispensable component, then prove retrieval. Until privacy is verified, use local artifacts and label coverage `local_only`; do not claim off-device recovery. Record source S separately from later evidence/report commit E so the report does not need its own future commit hash.

## Activation record — intentionally unfilled

- Accepted authority, plan commit and governing baseline: not recorded.
- Integration owner, actual ref and active writers: unassigned.
- Source candidate S / base / configuration / accepted plan identity: not recorded.
- Runtime state: not created; after adoption use the upstream planned/active/blocked/paused/ready/needs-decision/cancelled/superseded vocabulary.
- Verification, independent reviews, human validation and release: not performed by this proposal.
- Next action: accept the plan and satisfy dependencies, then select a bounded authorized slice.

[Milestone M0](../../milestones/M0.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
