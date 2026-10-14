# G-0005 — Verify persistence against real MongoDB and MySQL

Milestone: M1
Depends on: G-0004
Proposal revision: proposal-2
Plan authority: NOT ACCEPTED
Risk profile: high

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0005/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

Catalog versions, pricing snapshots and booking transactions survive real datastore operations with the intended constraints.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Run and repair the existing Testcontainers suites, migration paths and transactional adapters against real disposable MongoDB/MySQL instances.

## Exclusions

In-memory concurrency results do not establish InnoDB behavior, Aurora compatibility or process durability.

## Preconditions and authority boundaries

Local synthetic-data work only; no deployment or external write authorization.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Verified Mongo/JPA/JDBC adapters and repaired integration tests/migrations.
- Database test execution wired into required local validation.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0005-AC01

Execute all database integration tests selected at the accepted source revision. Record selected/executed/skipped counts per suite and fail the gate if required suites are empty or skipped; historical foundation counts are not the current expected count.

Required evidence: `G-0005-A01` plus the relevant raw check, source S and independent review coverage.

### G-0005-AC02

Prove round-trip preservation of catalog/price/quote versions, exact monetary values, date boundaries and case-sensitive idempotency identifiers in the actual datastores.

Required evidence: `G-0005-A02` plus the relevant raw check, source S and independent review coverage.

### G-0005-AC03

Exercise transaction rollback and uniqueness conflicts using separate real connections. No partial hold/order/capacity mutation remains after an expected conflict.

Required evidence: `G-0005-A02` plus the relevant raw check, source S and independent review coverage.

### G-0005-AC04

Apply migrations to empty disposable databases and verify the resulting constraints; demonstrate that missing Docker fails the required integration command rather than turning it green.

Required evidence: `G-0005-A03` plus the relevant raw check, source S and independent review coverage.

## Review evidence outputs (temporary and ignored)

The following IDs name expected review outputs, not files to commit. If generated, keep them under ignored `goals/G-0005/evidence/<candidate>/` only while review or a live handoff needs them. Record concise findings and hosted check links in the PR; remove local outputs after merge or terminal cancellation/supersession.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0005-A01` | `database-tests.md` | JUnit/Failsafe raw-report references and nonempty suite counts. |
| `G-0005-A02` | `persistence-invariants.md` | Round-trip, rollback, unique-index and connection-level evidence. |
| `G-0005-A03` | `migration-and-fail-closed.md` | Schema provenance and missing-runtime negative check. |

The accepted outcome still requires actual checks, independent reviews, intended-use validation and a decision. [The legacy manifest template](manifest.template.json) documents the older file format; do not fill or commit an evidence manifest. Follow the current [review evidence policy](../../ARTIFACT-CONTRACT.md) and link PR/CI records.

## Validation commands and new tests

- `bash scripts/verify.sh --integration` — existing. Maven/JDK/Docker required; real database tests must not silently skip.
- `bash scripts/verify.sh --java` — existing. Maven/JDK/repository access required; does NOT execute *IT suites.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0005/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0005/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0005` | Unassigned integration owner | Not created |

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

[Milestone M1](../../milestones/M1.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
