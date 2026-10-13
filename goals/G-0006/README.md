# G-0006 — Demonstrate the original live event purchase flow

Status: **active**. Milestone: M1. Dependency: [G-0005 ready](../G-0005/README.md) at `f95ca02f1f54b4010c0e9b4c00cba6fa685d689e`, still on draft PR #7 and not merged to `main`.

## Accepted work contract

- **Authority and revision:** Adam Burdett's instruction to activate the next goal accepts the unchanged [G-0006 proposal-2](../../docs/roadmap/goals/G-0006/README.md), SHA-256 `5f4ab82c85ff2c0d1b87ede53492c14627b79fff270232e1d28f4c79fac70ea`. This active record is `G-0006-active-1`; [bootstrap](work/bootstrap.md) records the activation commit P after Git creates it. The proposal remains historical input. Material scope or criterion changes require Adam's decision.
- **Governing baseline:** `AGENTS.md`, `GOVERNANCE.md`, `DEVELOPMENT.md`, `ARCHITECTURE.md`, `PLANS.md`, both project skills and two configured native reviewer roles at the exact G-0006 base `f95ca02f1f54b4010c0e9b4c00cba6fa685d689e`. The only repository `AGENTS.md` is at the root. Review any control change under this baseline.
- **Risk and review:** high risk for the full-stack purchase path, mutable inventory, price/version behavior and browser/API consistency. Two independent native reviewers (`contract-reviewer` and `adversarial-reviewer`), at most three rounds, under the adopted review guide. Freeze a source candidate before acceptance checks and reviews; relevant changes reopen affected coverage.
- **Canonical repository/ref/path:** public `burdettadam/park-commerce-workbench`, `goal/G-0006`, this file. The current Codex session is the integration writer and sole assigned branch writer. Adam retains scope, architecture and repository administration authority. Additional branches remain unassigned.
- **Intended-use validation:** Adam Burdett owns validation of the actual local employee-to-guest browser purchase and restart recovery. Present the executed browser/API evidence for his validation or explicit delegation; naming him here grants no approval. Human release review remains separate.

## Intended user and observable outcome

An employee publication produces a real guest quote, hold and retry-safe order through actual services and browsers.

Reviewer roles are separate from the implementation writer.

## Scope

Run the Compose stack, existing live HTTP exercise, Vaadin console and Angular storefront for the existing one-event quote boundary.

## Exclusions

Do not introduce admission checkout or a combined cart yet. A source screenshot is not a running browser demonstration.

## Preconditions and authority boundaries

Local synthetic-data work only; no deployment or external write authorization.

The G-0005 candidate is ready and remotely retrievable at the exact base above. G-0006 is stacked on that unmerged branch; `main` is still `575d44ad21e6ed6fc0e72de4f6f27060a324bfcb` at activation. Before any later integration to `main`, reconcile the dependency and renew affected checks if inputs change. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Working local Compose/HTTP/browser path; framework defects fixed within scope.
- Updated START-HERE instructions based on actual execution.

## Acceptance criteria

The following four outcomes are accepted verbatim from proposal-2. They are not yet observed G-0006 results. Do not weaken or delete a criterion because the current implementation fails it.

### G-0006-AC01

Compose services become healthy and all live HTTP assertions selected at the accepted source revision execute, including role checks, ownership, price-version preservation and competing holds. Record the actual count rather than using the foundation count.

Required evidence: `G-0006-A01` plus the relevant raw check, source S and independent review coverage.

### G-0006-AC02

Through the real Vaadin interface publish a product/rate/event, and through Angular obtain a quote and confirm an order. Record the browser steps and corresponding API/order identifiers.

Required evidence: `G-0006-A02` plus the relevant raw check, source S and independent review coverage.

### G-0006-AC03

Publish a new price after an earlier quote and prove that the still-valid earlier quote keeps its original total. Retry the same checkout and observe one order and one capacity consumption.

Required evidence: `G-0006-A01` plus the relevant raw check, source S and independent review coverage.

### G-0006-AC04

Stop the local stack without deleting volumes; restart and retrieve the confirmed order. Record framework/binding repairs and any remaining demo-only auth limitations.

Required evidence: `G-0006-A03` plus the relevant raw check, source S and independent review coverage.

## Required goal-specific artifacts

Relative to the eventual candidate evidence directory, for example `goals/G-0006/evidence/<candidate>/`. These are output requirements, not generated evidence.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0006-A01` | `live-http.txt` | Executed live HTTP assertions and synthetic request/order IDs. |
| `G-0006-A02` | `browser-walkthrough.md` | Actual Vaadin/Angular steps with screenshots/video references. |
| `G-0006-A03` | `restart-and-limitations.md` | Volume-preserving restart and current local-only limitations. |

The common manifest, environment, raw checks, independent contract/adversarial review reports, intended-use validation, decision and handoff are also mandatory. Start from [the unfilled manifest template](../../docs/roadmap/goals/G-0006/manifest.template.json) and [artifact contract](../../docs/roadmap/ARTIFACT-CONTRACT.md). The unfilled template deliberately fails the readiness checker.

## Validation commands and new tests

- `bash scripts/configure-local.sh` — existing. Generates local secrets; refuses existing `.env`; do not log or commit secrets.
- `docker compose config --quiet` — existing. Validate without printing rendered secrets; runtime still unverified.
- `docker compose up --build -d --wait` — existing. Local containers/downloads; own project/ports/volumes required.
- `node scripts/demo-http.mjs` — existing. Requires running local services and `.env`; creates synthetic data.
- `mvn -Pui -pl admin-console spring-boot:run` — existing. Requires installed sibling modules/running APIs as documented in START-HERE.
- `(cd storefront && npm run start)` — existing. Real services and reviewed dependencies required; loopback development server.
- `docker compose down` — existing. Stops project without intentionally deleting named volumes; never add `-v` casually.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Select focused commands after inspecting the actual existing scripts and UIs, and retain executed commands, working directory and inputs.

## Execution and ownership

| Slice | Ref | Writer | Included commit |
| --- | --- | --- | --- |
| Canonical integration, implementation and tests | `goal/G-0006` | Current Codex session | Base `f95ca02f1f54b4010c0e9b4c00cba6fa685d689e`; P to be recorded in [bootstrap](work/bootstrap.md) |
| Additional independent work | Not assigned | Unassigned | Not created |

Start with one branch. Assign separate writers and refs only when useful; record exact included revisions and disposition for abandoned alternatives.

## Review and stop rules

The accepted baseline is two independent reviewers (contract and adversarial) and at most three rounds. Collect first findings independently. A relevant code/test/configuration/input change creates a new candidate and reopens affected verification and review. Retain valid blockers even when another reviewer disagrees. Missing reviews are not passes; unresolved blockers after the review limit require `needs-decision`.

Changes to these rules are reviewed under the previous accepted rules, never self-approved by the candidate. Version-producing merges require an actual human decision. Merely reaching `ready` does not create a release, deploy anything or change visibility.

## Handoff and recovery

Retain source/branch/artifact checkpoints before the canonical coordination record, identify every indispensable component, then prove retrieval. Until privacy is verified, use local artifacts and label coverage `local_only`; do not claim off-device recovery. Record source S separately from later evidence/report commit E so the report does not need its own future commit hash.

## Activation and next action

- Accepted plan commit P and exact file digest: record in [bootstrap](work/bootstrap.md) after activation commit.
- Source candidate S: not selected. The base includes G-0005 ready source and evidence at `f95ca02f1f54b4010c0e9b4c00cba6fa685d689e`.
- Runtime: no G-0006-owned instance has been started or verified. Any preexisting Compose containers require ownership and endpoint checks before use.
- Verification, browser validation, independent reviews, final decision and release: not performed for G-0006 at activation.
- Next action: publish and retrieve activation P, inspect the actual Compose/HTTP and both UI paths, then start the bounded live-flow implementation and checks.

[Milestone M1](../../docs/roadmap/milestones/M1.md) · [Roadmap](../../docs/roadmap/README.md) · [Commands](../../docs/roadmap/COMMANDS.md)
