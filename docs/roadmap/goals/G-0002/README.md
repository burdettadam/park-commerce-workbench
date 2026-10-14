# G-0002 — Validate the existing version guard with a red/green challenge

Milestone: M0
Depends on: G-0001
Proposal revision: proposal-2
Plan authority: NOT ACCEPTED
Risk profile: medium

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0002/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

The project helper invokes only the reviewed git-agent version, while retaining its current preservation and exit-code behavior.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Validate the already repaired exact-version guard in scripts/init-agent.sh. Use a disposable, deliberately weakened copy to demonstrate that the regression test catches the old defect, then verify the unchanged current guard. Make a product change only if the exercise reveals a real defect. Use this bounded exercise to test the goal loop without claiming a second repair.

## Exclusions

Do not alter private git-agent, broaden supported versions, rewrite unrelated setup logic, or call mock tests proof of actual initializer behavior.

## Preconditions and authority boundaries

Local synthetic-data work only; no deployment or external write authorization.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- A retained red/green challenge tied to exact source revisions and a disposable mutation.
- Any focused regression fix and tests required by a newly observed defect; otherwise an evidence-only candidate.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0002-AC01

In a disposable worktree, commit a deliberately restored old substring check on a local-only challenge branch and show that the existing mock tests fail for 0.2.01, 10.2.0 and 0.2.0-rc.1. Retain that exact challenge commit, failing assertions and exit status. Archived pre-fix evidence is historical context, not a new goal run or a new product defect.

Required evidence: `G-0002-A01` plus the relevant raw check, source S and independent review coverage.

### G-0002-AC02

On the unchanged current source, exact reviewed version 0.2.0 reaches mock init, while incompatible, prefix/suffix look-alikes, empty and malformed outputs are rejected before init is invoked. Also run a preview with the reviewed real initializer on the current worktree; distinguish that observation from mock coverage.

Required evidence: `G-0002-A02` plus the relevant raw check, source S and independent review coverage.

### G-0002-AC03

All existing tooling checks remain selected and passing on the intended host, including project-root protection, extra-argument rejection, preservation and forwarding of initializer exit code 3. Record any host-specific skipped check explicitly; do not replace assertions with snapshots.

Required evidence: `G-0002-A02` plus the relevant raw check, source S and independent review coverage.

### G-0002-AC04

Obtain independent contract and adversarial reviews of fixed candidate S, including the challenge method and any actual product diff. A relevant source or test change invalidates stale review coverage. Record actual acceptance separately; no reviewer, tag or approval is fabricated.

Required evidence: `G-0002-A03` plus the relevant raw check, source S and independent review coverage.

## Review evidence outputs (temporary and ignored)

The following IDs name expected review outputs, not files to commit. If generated, keep them under ignored `goals/G-0002/evidence/<candidate>/` only while review or a live handoff needs them. Record concise findings and hosted check links in the PR; remove local outputs after merge or terminal cancellation/supersession.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0002-A01` | `red-before.txt` | Disposable mutation, observed failing assertions and historical-source distinction. |
| `G-0002-A02` | `green-after.txt` | Current-source regression matrix, real initializer preview and retained tooling suite. |
| `G-0002-A03` | `scope-and-review.md` | Actual product diff or explicit evidence-only result, candidate identity and reviewer dispositions. |

The accepted outcome still requires actual checks, independent reviews, intended-use validation and a decision. [The legacy manifest template](manifest.template.json) documents the older file format; do not fill or commit an evidence manifest. Follow the current [review evidence policy](../../ARTIFACT-CONTRACT.md) and link PR/CI records.

## Validation commands and new tests

- `bash scripts/test-tooling.sh` — existing. Disposable fixtures/mock initializer only; not real upstream adoption.
- `bash scripts/init-agent.sh --preview` — existing. Requires reviewed `git-agent 0.2.0` on Git Bash `PATH`; records real initializer observation without writing guidance.
- `bash scripts/test-core.sh --demo` — existing. JDK 21 and Bash; in-memory domain adapters only.
- `node scripts/validate-roadmap.mjs` — added_by_this_delivery. Read-only structural checks; no execution or adoption of goals.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0002/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0002/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0002` | Unassigned integration owner | Not created |

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
