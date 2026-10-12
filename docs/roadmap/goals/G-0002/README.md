# G-0002 — Repair exact-version matching through one real red/green goal

Milestone: M0
Depends on: G-0001
Proposal revision: proposal-1
Plan authority: NOT ACCEPTED
Risk profile: medium

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0002/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

The project helper invokes only the reviewed git-agent version, while retaining its current preservation and exit-code behavior.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Fix the concrete substring-version defect in scripts/init-agent.sh and extend the existing mock-based tooling tests. Use this bounded change to exercise a complete goal loop.

## Exclusions

Do not alter private git-agent, broaden supported versions, rewrite unrelated setup logic, or call mock tests proof of actual initializer behavior.

## Preconditions and authority boundaries

Local synthetic-data work only; no deployment or external write authorization.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- scripts/init-agent.sh exact-version guard.
- scripts/test-tooling.sh targeted version-output cases.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0002-AC01

Before the fix, independently reproduce rejection failures for 0.2.01, 10.2.0 and 0.2.0-rc.1 in disposable worktrees with a mock command. Preserve the expected-failing run and its exit status; the planning discovery is context, not a substitute for the goal run.

Required evidence: `G-0002-A01` plus the relevant raw check, source S and independent review coverage.

### G-0002-AC02

After the fix, exact reviewed version 0.2.0 reaches mock init, while incompatible, prefix/suffix look-alikes, empty and malformed outputs are rejected before init is invoked.

Required evidence: `G-0002-A02` plus the relevant raw check, source S and independent review coverage.

### G-0002-AC03

All existing tooling checks remain selected and passing, including project-root protection, extra-argument rejection, preservation and forwarding of initializer exit code 3. Add explicit assertions instead of only updating expected snapshots.

Required evidence: `G-0002-A02` plus the relevant raw check, source S and independent review coverage.

### G-0002-AC04

Obtain independent contract and adversarial reviews of fixed candidate S; a fix or relevant test change invalidates stale review coverage. Record the diff and actual acceptance separately; no reviewer, tag or approval is fabricated.

Required evidence: `G-0002-A03` plus the relevant raw check, source S and independent review coverage.

## Required goal-specific artifacts

Relative to the eventual candidate evidence directory, for example `goals/G-0002/evidence/<candidate>/`. These are output requirements, not generated evidence.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0002-A01` | `red-before.txt` | Actual defect reproduction with the expected failing assertion. |
| `G-0002-A02` | `green-after.txt` | Post-fix regression matrix and retained tooling suite. |
| `G-0002-A03` | `scope-and-review.md` | Bounded source diff, candidate identity and reviewer dispositions. |

The common manifest, environment, raw checks, independent contract/adversarial review reports, intended-use validation, decision and handoff are also mandatory. Start from [the unfilled manifest template](manifest.template.json) and [artifact contract](../../ARTIFACT-CONTRACT.md). The unfilled template deliberately fails the readiness checker.

## Validation commands and new tests

- `bash scripts/test-tooling.sh` — existing. Disposable fixtures/mock initializer only; not real upstream adoption.
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

Retain source/branch/artifact checkpoints before the canonical coordination record, identify every indispensable component, then prove retrieval. Until privacy is verified, use local artifacts and label coverage `local_only`; do not claim off-device recovery. Record source S separately from later evidence/report commit E so the report does not need its own future commit hash.

## Activation record — intentionally unfilled

- Accepted authority, plan commit and governing baseline: not recorded.
- Integration owner, actual ref and active writers: unassigned.
- Source candidate S / base / configuration / accepted plan identity: not recorded.
- Runtime state: not created; after adoption use the upstream planned/active/blocked/paused/ready/needs-decision/cancelled/superseded vocabulary.
- Verification, independent reviews, human validation and release: not performed by this proposal.
- Next action: accept the plan and satisfy dependencies, then select a bounded authorized slice.

[Milestone M0](../../milestones/M0.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
