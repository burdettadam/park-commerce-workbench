# G-0005 — Verify persistence against real MongoDB and MySQL

Status: **active**. Milestone: M1. Dependency: [G-0004 ready and promoted](../G-0004/README.md) to `main` at `c873cfe1b8dd93732ef0d63f1b890b9774174934`.

## Accepted work contract

- **Authority and revision:** Adam Burdett's instruction to merge the last work and activate the next goal accepts the unchanged [G-0005 proposal-2](../../docs/roadmap/goals/G-0005/README.md), SHA-256 `12ae39ed3093f40ae0a37895380b1dccf5391344064bc635fc974abb90683b09`. This active record is `G-0005-active-1`; [bootstrap](work/bootstrap.md) records its activation commit P after Git creates it. The proposal remains historical input. Material scope or criterion changes require Adam's decision.
- **Governing baseline:** `AGENTS.md`, `GOVERNANCE.md`, `DEVELOPMENT.md`, `ARCHITECTURE.md`, `PLANS.md`, both project skills and two configured native reviewer roles at `main` commit `575d44ad21e6ed6fc0e72de4f6f27060a324bfcb`. Review any control changes under that baseline.
- **Risk and review:** high risk for persistent state, database constraints and transactional behavior. Two independent native reviewers (`contract-reviewer` and `adversarial-reviewer`), at most three rounds, under the adopted review guide. Freeze a source candidate before acceptance checks and reviews; relevant changes reopen affected coverage.
- **Canonical repository/ref/path:** public `burdettadam/park-commerce-workbench`, `goal/G-0005`, this file. The current Codex session is the integration writer and sole assigned branch writer. Adam retains scope, architecture and repository administration authority. Additional branches remain unassigned.
- **Validation:** Adam Burdett owns intended-use validation. Present real-datastore test reports, migration/constraint results and the missing-Docker negative check for his validation or explicit delegation. No validation or release approval is claimed at activation.

## Intended user and observable outcome

Catalog versions, pricing snapshots and booking transactions survive real datastore operations with the intended constraints.

Reviewer roles are separate from the implementation writer. Naming Adam as validation owner grants no approval.

## Scope

Run and repair the existing Testcontainers suites, migration paths and transactional adapters against real disposable MongoDB/MySQL instances.

## Exclusions

In-memory concurrency results do not establish InnoDB behavior, Aurora compatibility or process durability.

## Preconditions and authority boundaries

Local synthetic-data work only; no deployment or external write authorization.

G-0004 is ready and promoted; the exact G-0005 base is `575d44ad21e6ed6fc0e72de4f6f27060a324bfcb`. Unresolved access, budget, toolchain or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Verified Mongo/JPA/JDBC adapters and repaired integration tests/migrations.
- Database test execution wired into required local validation.

## Acceptance criteria

The following four outcomes are accepted verbatim from proposal-2. They are not yet observed G-0005 results. Do not weaken or delete a criterion because the current implementation fails it.

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

## Required goal-specific artifacts

Relative to the eventual candidate evidence directory, for example `goals/G-0005/evidence/<candidate>/`. These are output requirements, not generated evidence.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0005-A01` | `database-tests.md` | JUnit/Failsafe raw-report references and nonempty suite counts. |
| `G-0005-A02` | `persistence-invariants.md` | Round-trip, rollback, unique-index and connection-level evidence. |
| `G-0005-A03` | `migration-and-fail-closed.md` | Schema provenance and missing-runtime negative check. |

The common manifest, environment, raw checks, independent contract/adversarial review reports, intended-use validation, decision and handoff are also mandatory. Start from [the unfilled manifest template](../../docs/roadmap/goals/G-0005/manifest.template.json) and [artifact contract](../../docs/roadmap/ARTIFACT-CONTRACT.md). The unfilled template deliberately fails the readiness checker.

## Validation commands and new tests

- `bash scripts/verify.sh --integration` — existing. Maven/JDK/Docker required; real database tests must not silently skip.
- `bash scripts/verify.sh --java` — existing. Maven/JDK/repository access required; does NOT execute *IT suites.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Select focused commands after inspecting the actual existing suites and retain executed commands, working directory and inputs.

## Execution and ownership

| Slice | Ref | Writer | Included commit |
| --- | --- | --- | --- |
| Canonical integration, implementation and tests | `goal/G-0005` | Current Codex session | Activation P recorded in [bootstrap](work/bootstrap.md); source S not selected |
| Additional independent work | Not assigned | Unassigned | Not created |

Start with one branch. Assign separate writers and refs only when useful; record exact included revisions and disposition for abandoned alternatives.

## Review and stop rules

The accepted baseline is two independent reviewers (contract and adversarial) and at most three rounds. Collect first findings independently. A relevant code/test/configuration/input change creates a new candidate and reopens affected verification and review. Retain valid blockers even when another reviewer disagrees. Missing reviews are not passes; unresolved blockers after the review limit require `needs-decision`.

Changes to these rules are reviewed under the previous accepted rules, never self-approved by the candidate. Version-producing merges require an actual human decision. Merely reaching `ready` does not create a release, deploy anything or change visibility.

## Handoff and recovery

Retain source/branch/artifact checkpoints before the canonical coordination record, identify every indispensable component, then prove retrieval. Until privacy is verified, use local artifacts and label coverage `local_only`; do not claim off-device recovery. Record source S separately from later evidence/report commit E so the report does not need its own future commit hash.

## Activation and next action

- Accepted plan commit P and file digest: recorded in [bootstrap](work/bootstrap.md) after activation commit.
- Source candidate S: `1dd1ef28d3d5db6bcd4e47255c074ed6205c1627` from base `575d44ad21e6ed6fc0e72de4f6f27060a324bfcb`; the fixed candidate adds datastore assertions and a required nonempty integration-suite gate. [Verification work](work/persistence-verification.md) records the implementation and attempted checks.
- Runtime: disposable MongoDB/MySQL Testcontainers and temporary Spring test contexts were started for synthetic-data verification; no long-lived G-0005 development instance was launched.
- Verification: [candidate evidence](evidence/1dd1ef2/database-tests.md) records a clean-clone 67/67 local run, hosted CI and deliberate missing-Docker/empty/skipped-suite failures. Independent reviews and intended-use validation are pending. No release, tag, artifact or deployment has occurred.
- Next action: obtain two independent reviews of fixed S and its retained evidence, resolve any findings, then complete intended-use validation and recoverable handoff before considering ready.

[Milestone M1](../../docs/roadmap/milestones/M1.md) · [Roadmap](../../docs/roadmap/README.md) · [Commands](../../docs/roadmap/COMMANDS.md)
