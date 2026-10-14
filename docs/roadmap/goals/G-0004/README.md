# G-0004 — Resolve and lock the actual Java and UI builds

Milestone: M1
Depends on: G-0003
Proposal revision: proposal-2
Plan authority: NOT ACCEPTED
Risk profile: medium

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0004/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

A clean machine can compile the existing Spring services and both UI frameworks from reviewed, reproducible inputs.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Recheck the existing dependency/toolchain baseline from clean inputs; repair remaining compilation issues, including the optional Vaadin path; retain dependency trees, lockfile provenance and build commands.

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

Review the existing Angular lockfile and resolve the Vaadin frontend lock strategy from actual generated inputs. A second clean dependency installation/build uses retained inputs and succeeds without an unexplained lockfile change.

Required evidence: `G-0004-A02` plus the relevant raw check, source S and independent review coverage.

### G-0004-AC03

Existing core, client-state and tooling suites run with actual result/selected-test counts. Skipped application tests during a packaging command are not recorded as acceptance passes.

Required evidence: `G-0004-A01` plus the relevant raw check, source S and independent review coverage.

### G-0004-AC04

Document every candidate version correction and dependency decision; retain unresolved vulnerability or compatibility findings with owner and disposition before this goal becomes ready.

Required evidence: `G-0004-A03` plus the relevant raw check, source S and independent review coverage.

## Review evidence outputs (temporary and ignored)

The following IDs name expected review outputs, not files to commit. If generated, keep them under ignored `goals/G-0004/evidence/<candidate>/` only while the PR is open. Record concise findings and hosted check links in the PR; remove local outputs after merge or terminal cancellation/supersession.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0004-A01` | `build-results.md` | Commands, logs, exact versions and observed build outcomes for each module/UI. |
| `G-0004-A02` | `dependency-locks.md` | Lockfile provenance, clean rebuild and dependency-tree references. |
| `G-0004-A03` | `compatibility-decisions.md` | Verified compatibility choices and unresolved findings. |

The accepted outcome still requires actual checks, independent reviews, intended-use validation and a decision. [The legacy manifest template](manifest.template.json) documents the older file format; do not fill or commit an evidence manifest. Follow the current [review evidence policy](../../ARTIFACT-CONTRACT.md) and link PR/CI records.

## Validation commands and new tests

- `bash scripts/verify.sh --java` — existing. Maven/JDK/repository access required; does NOT execute *IT suites.
- `mvn -B -ntp -Pui -pl admin-console -am verify` — proposed_validation. Uses existing Maven profile; must be executed and repaired in G-0004. Do not skip required tests.
- `(cd storefront && npm ci && npm run build)` — existing. Node 24 baseline and committed lockfile; package scripts need normal dependency trust review.
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

Push source and branch checkpoints before the canonical coordination record, identify every required source component, and verify retrieval. Keep generated evidence ignored and local while the PR is open, with concise findings and CI links in the PR. After feature code merges, remove local generated outputs. Record exact source S and merged M separately; do not create an evidence-only commit E.

## Activation record — intentionally unfilled

- Accepted authority, plan commit and governing baseline: not recorded.
- Integration owner, actual ref and active writers: unassigned.
- Source candidate S / base / configuration / accepted plan identity: not recorded.
- Runtime state: not created; after adoption use the upstream planned/active/blocked/paused/ready/needs-decision/cancelled/superseded vocabulary.
- Verification, independent reviews, human validation and release: not performed by this proposal.
- Next action: accept the plan and satisfy dependencies, then select a bounded authorized slice.

[Milestone M1](../../milestones/M1.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
