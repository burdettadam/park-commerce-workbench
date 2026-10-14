# G-0030 — Prepare a truthful private release-candidate dossier

Milestone: M9
Depends on: G-0024, G-0025, G-0026, G-0027, G-0028, G-0029
Proposal revision: proposal-1
Plan authority: NOT ACCEPTED
Risk profile: high

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0030/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

A reviewer can run the demonstration, inspect the code/evidence and distinguish implemented capability from remaining limitations.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Package the fixed source/artifacts, demo, architecture and capability map; obtain actual independent/human decisions for a private release candidate.

## Exclusions

No automatic public visibility change, fabricated work history, third-party affiliation, license choice, real payment capability or tag/merge authorization inferred from this roadmap.

## Preconditions and authority boundaries

Explicit human authority is required for version-producing merges/tags and separately for public publication. No visibility change is authorized by this goal proposal.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Private release-candidate ZIP/images, demo recording, architecture narrative and evidence index.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0030-AC01

From a clean supported machine, follow the retained instructions and reproduce the accepted demonstration using the exact source/artifact versions. Record required private/licensed dependencies separately.

Required evidence: `G-0030-A01` plus the relevant raw check, source S and independent review coverage.

### G-0030-AC02

Every portfolio capability claim points to a completed goal and executed evidence; all remaining access, deployment, security and scope limitations remain visible. The full role-complete label requires M0-M9 gates, including Splunk/AppDynamics/Neo4j.

Required evidence: `G-0030-A02` plus the relevant raw check, source S and independent review coverage.

### G-0030-AC03

Inspect source/assets, secrets, logs, license/provenance and downloadable artifacts for a potential future public release; retain findings and actual owner decisions without publishing.

Required evidence: `G-0030-A02` plus the relevant raw check, source S and independent review coverage.

### G-0030-AC04

Record current independent reviews, human authorization before any version-producing merge, actual merged revision/artifact/tag only if performed, and retrieval/rollback ownership. Public publication remains a separate explicit decision.

Required evidence: `G-0030-A03` plus the relevant raw check, source S and independent review coverage.

## Review evidence outputs (temporary and ignored)

The following IDs name expected review outputs, not files to commit. If generated, keep them under ignored `goals/G-0030/evidence/<candidate>/` only while the PR is open. Record concise findings and hosted check links in the PR; remove local outputs after feature code merges.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0030-A01` | `reproduction-and-demo.md` | Fresh-machine result, runbook and actual demo recording. |
| `G-0030-A02` | `portfolio-claims-and-audit.md` | Capability-to-evidence map and privacy/license/source audit. |
| `G-0030-A03` | `release-decision.md` | Actual private-candidate review/approval and separate publication decision status. |

The accepted outcome still requires actual checks, independent reviews, intended-use validation and a decision. [The legacy manifest template](manifest.template.json) documents the older file format; do not fill or commit an evidence manifest. Follow the current [review evidence policy](../../ARTIFACT-CONTRACT.md) and link PR/CI records.

## Validation commands and new tests

- `bash scripts/test-core.sh --demo` — existing. JDK 21 and Bash; in-memory domain adapters only.
- `node --test storefront/src/app/checkout-state.test.mjs` — existing. Existing dependency-free client-state tests, not Angular browser tests.
- `bash scripts/test-tooling.sh` — existing. Disposable fixtures/mock initializer only; not real upstream adoption.
- `bash scripts/verify.sh --integration` — existing. Maven/JDK/Docker required; real database tests must not silently skip.
- `node scripts/demo-http.mjs` — existing. Requires running local services and .env; creates synthetic data.
- `node scripts/validate-roadmap.mjs` — added_by_this_delivery. Read-only structural checks; no execution or adoption of goals.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0030/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0030/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0030` | Unassigned integration owner | Not created |

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

[Milestone M9](../../milestones/M9.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
