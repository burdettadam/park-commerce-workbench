# G-0020 — Execute the GitHub and Jenkins delivery checks

Milestone: M6
Depends on: G-0019
Proposal revision: proposal-2
Plan authority: NOT ACCEPTED
Risk profile: high

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0020/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

An actual pipeline verifies a fixed candidate and retains reports without granting itself merge or release authority.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Run GitHub repository checks and Jenkins application build/test/publish stages using scoped credentials and candidate artifact identity.

## Exclusions

No unexecuted Jenkinsfile-as-proof, always-green wrapper, branch-rule bypass, paid runner or unapproved registry write.

## Preconditions and authority boundaries

Remote changes require repository PRIVATE confirmed at the time of the write. Any paid CI/registry resource requires explicit authorization.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Executed Jenkins and GitHub workflows with retained native reports.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0020-AC01

Execute both pipelines and retain run URLs/IDs, source S, selected test counts and artifact digests. A required missing/failed/skipped suite makes the actual run fail.

Required evidence: `G-0020-A01` plus the relevant raw check, source S and independent review coverage.

### G-0020-AC02

Inject a harmless known test failure on a scoped branch and demonstrate that promotion is blocked; retain the failed run as well as the repaired run.

Required evidence: `G-0020-A02` plus the relevant raw check, source S and independent review coverage.

### G-0020-AC03

Credentials are least-privilege and untrusted changes cannot access publishing/deployment secrets; record the actual public-repository, protected-branch, workflow-permission and applicable environment-control observations.

Required evidence: `G-0020-A03` plus the relevant raw check, source S and independent review coverage.

### G-0020-AC04

Human review for a version-producing merge remains external to agent/model agreement. Pipeline evidence and artifact retrieval are linked from the canonical goal record only after retention is confirmed.

Required evidence: `G-0020-A03` plus the relevant raw check, source S and independent review coverage.

## Review evidence outputs (temporary and ignored)

The following IDs name expected review outputs, not files to commit. If generated, keep them under ignored `goals/G-0020/evidence/<candidate>/` only while the PR is open. Record concise findings and hosted check links in the PR; remove local outputs after feature code merges.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0020-A01` | `pipeline-runs.md` | Actual GitHub/Jenkins runs, counts, source and artifacts. |
| `G-0020-A02` | `negative-gate-test.md` | Known failed check and observed promotion denial. |
| `G-0020-A03` | `pipeline-controls.md` | Permissions, trust boundaries, public visibility and human gates. |

The accepted outcome still requires actual checks, independent reviews, intended-use validation and a decision. [The legacy manifest template](manifest.template.json) documents the older file format; do not fill or commit an evidence manifest. Follow the current [review evidence policy](../../ARTIFACT-CONTRACT.md) and link PR/CI records.

## Validation commands and new tests

- `bash scripts/verify.sh --integration` — existing. Maven/JDK/Docker required; real database tests must not silently skip.
- `node scripts/validate-roadmap.mjs` — added_by_this_delivery. Read-only structural checks; no execution or adoption of goals.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0020/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0020/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0020` | Unassigned integration owner | Not created |

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

[Milestone M6](../../milestones/M6.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
