# G-0029 — Evaluate goal-driven development using retained evidence

Milestone: M9
Depends on: G-0003, G-0006, G-0012, G-0020, G-0024
Proposal revision: proposal-1
Plan authority: NOT ACCEPTED
Risk profile: high

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0029/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

The owner can judge whether the workflow produced correct, reviewable and recoverable work rather than only plausible status reports.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Assess the completed goal records and controlled gate/handoff experiments; measure process outcomes without inventing a causal productivity claim.

## Exclusions

No claim that model agreement is correctness, no fabricated independent reviews, and no deterministic-model equivalence assumed from this sample.

## Preconditions and authority boundaries

Local synthetic-data work only; no deployment or external write authorization.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Evidence-backed goal-driven-development assessment and actionable process decisions.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0029-AC01

Audit at least five materially different executed goals, including the small defect, database/full-stack validation, commerce change, delivery and recovery. Trace every accepted criterion to actual current-candidate evidence.

Required evidence: `G-0029-A01` plus the relevant raw check, source S and independent review coverage.

### G-0029-AC02

Run the defined stale-review, missing-evidence, known-defect and interrupted-handoff checks under the adopted policy. All must produce the intended blocked/needs-decision or recoverable outcome; retain negative observations.

Required evidence: `G-0029-A02` plus the relevant raw check, source S and independent review coverage.

### G-0029-AC03

Report actual review rounds, blockers found, regressions, reopened goals, lost/unretrievable artifacts, handoff restoration time and costs where observable. Unknown/missing measurements stay unknown.

Required evidence: `G-0029-A01` plus the relevant raw check, source S and independent review coverage.

### G-0029-AC04

Document process changes as proposals reviewed under the previous rules; retain a human decision and calibration needs for any model/guide change. Do not allow the candidate to weaken its own acceptance conditions.

Required evidence: `G-0029-A03` plus the relevant raw check, source S and independent review coverage.

## Required goal-specific artifacts

Relative to the eventual candidate evidence directory, for example `goals/G-0029/evidence/<candidate>/`. These are output requirements, not generated evidence.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0029-A01` | `workflow-audit.md` | Goal-by-goal traceability, measured process outcomes and missing data. |
| `G-0029-A02` | `control-experiments.md` | Known-good/bad, stale review, missing artifact and handoff trials. |
| `G-0029-A03` | `workflow-decision.md` | Human evaluation, limitations and separately governed improvement proposals. |

The common manifest, environment, raw checks, independent contract/adversarial review reports, intended-use validation, decision and handoff are also mandatory. Start from [the unfilled manifest template](manifest.template.json) and [artifact contract](../../ARTIFACT-CONTRACT.md). The unfilled template deliberately fails the readiness checker.

## Validation commands and new tests

- `node scripts/validate-roadmap.mjs` — added_by_this_delivery. Read-only structural checks; no execution or adoption of goals.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0029/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0029/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0029` | Unassigned integration owner | Not created |

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

[Milestone M9](../../milestones/M9.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
