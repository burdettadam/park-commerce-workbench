# G-0004 — Resolve and lock the actual Java and UI builds

Milestone: M1
Depends on: G-0003
Proposal revision: proposal-1
Plan authority: NOT ACCEPTED
Risk profile: medium

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0004/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

A clean machine can compile the existing Spring services and both UI frameworks from reviewed, reproducible inputs.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Resolve the candidate dependency/toolchain baseline; repair compilation issues; generate real frontend locks; retain the dependency tree and build commands.

## Exclusions

No fake lockfiles, unreviewed stack substitution, new features, or successful-build claims based on core-only checks.

## Preconditions and authority boundaries

Local synthetic-data work only; no deployment or external write authorization.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Repaired pom.xml/module bindings as needed; actual storefront and Vaadin locks.
- Reproducible UI build commands added to maintained checks, after execution.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0004-AC01

Maven compiles all backend modules and optional Vaadin module; Angular production build completes. Record the actual JDK/Maven/Node/npm and framework versions rather than assuming candidate pins are compatible.

Required evidence: `G-0004-A01` plus the relevant raw check, source S and independent review coverage.

### G-0004-AC02

Generate and review real Angular/Vaadin frontend lockfiles. A second clean dependency installation/build uses retained locks and succeeds without an unexplained lockfile change.

Required evidence: `G-0004-A02` plus the relevant raw check, source S and independent review coverage.

### G-0004-AC03

Existing core, client-state and tooling suites run with actual result/selected-test counts. Skipped application tests during a packaging command are not recorded as acceptance passes.

Required evidence: `G-0004-A01` plus the relevant raw check, source S and independent review coverage.

### G-0004-AC04

Document every candidate version correction and dependency decision; retain unresolved vulnerability or compatibility findings with owner and disposition before this goal becomes ready.

Required evidence: `G-0004-A03` plus the relevant raw check, source S and independent review coverage.

## Required goal-specific artifacts

Relative to the eventual candidate evidence directory, for example `goals/G-0004/evidence/<candidate>/`. These are output requirements, not generated evidence.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0004-A01` | `build-results.md` | Commands, logs, exact versions and observed build outcomes for each module/UI. |
| `G-0004-A02` | `dependency-locks.md` | Lockfile provenance, clean rebuild and dependency-tree references. |
| `G-0004-A03` | `compatibility-decisions.md` | Verified compatibility choices and unresolved findings. |

The common manifest, environment, raw checks, independent contract/adversarial review reports, intended-use validation, decision and handoff are also mandatory. Start from [the unfilled manifest template](manifest.template.json) and [artifact contract](../../ARTIFACT-CONTRACT.md). The unfilled template deliberately fails the readiness checker.

## Validation commands and new tests

- `bash scripts/verify.sh --java` — existing. Maven/JDK/repository access required; does NOT execute *IT suites.
- `mvn -B -ntp -Pui -pl admin-console -am verify` — proposed_validation. Uses existing Maven profile; must be executed and repaired in G-0004. Do not skip required tests.
- `(cd storefront && npm install && npm run build)` — existing. Node 24 baseline; generate/review real lockfile; package scripts need normal dependency trust review.
- `node --test storefront/src/app/checkout-state.test.mjs` — existing. Existing dependency-free client-state tests, not Angular browser tests.
- `bash scripts/test-core.sh --demo` — existing. JDK 21 and Bash; in-memory domain adapters only.
- `bash scripts/test-tooling.sh` — existing. Disposable fixtures/mock initializer only; not real upstream adoption.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0004/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0004/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0004` | Unassigned integration owner | Not created |

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

[Milestone M1](../../milestones/M1.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
