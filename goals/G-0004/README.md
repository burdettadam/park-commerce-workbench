# G-0004 — Resolve and lock the actual Java and UI builds

Status: **ready** for the accepted build scope. Milestone: M1. Dependency: [G-0003 ready](../G-0003/README.md), included in `main` at `d7ed805158bd6a239480ce7ae03312c139f2acc7`.

## Accepted work contract

- **Authority and revision:** Adam Burdett's instruction to merge completed branches and make the next goal active accepts [G-0004 proposal-2](../../docs/roadmap/goals/G-0004/README.md) without changing its four criteria. The proposal bytes on the merged base have SHA-256 `5a8819f52d13fd43f662f53b63439b5798b67af091c799d18e317cb7f6a9c40c`. This active record is `G-0004-active-1`; [bootstrap](work/bootstrap.md) records its activation commit after Git creates it. The proposal remains historical input. Material scope or criterion changes require Adam's decision.
- **Governing baseline:** `AGENTS.md`, `GOVERNANCE.md`, `DEVELOPMENT.md`, `ARCHITECTURE.md`, `PLANS.md`, the two project skills and two configured native reviewer roles on merged `main` commit `d7ed805158bd6a239480ce7ae03312c139f2acc7`. Review changes to controls under that baseline.
- **Intended user and outcome:** a developer with a clean machine can compile the existing Spring services and the optional Vaadin admin console, build the Angular storefront, and rerun the retained test suites from reviewed reproducible inputs.
- **Scope:** recheck the actual dependency and toolchain baseline, repair remaining compilation issues, resolve the Vaadin frontend lock strategy from generated inputs, and retain dependency trees, lockfile provenance, exact versions and build results. Search existing dependency declarations and build behavior before adding new configuration; keep native manifests authoritative.
- **Conditions and exclusions:** local synthetic-data development. No fake lockfiles, unreviewed stack replacement, new product feature, cloud deployment, or successful-build claim from core-only tests. No material architecture or public-contract change is preapproved. No release, tag or version-producing merge is part of activation.
- **Quality and review:** medium risk because this goal changes build inputs and reproducibility across Java, Vaadin and Angular. Use the adopted two independent native reviewers (`contract-reviewer`, `adversarial-reviewer`), with at most three rounds. Freeze a source candidate before selected checks and reviews; changed relevant inputs reopen coverage. Record dependency security and compatibility findings with owner/disposition.
- **Validation:** Adam Burdett owns intended-use validation and explicitly delegated the G-0004 check to Codex after the clean-build results were shown. The bounded [validation](https://github.com/burdettadam/park-commerce-workbench/blob/90ae8eff6485c950c8d02c8d3a33893059f5d82e/goals/G-0004/evidence/e7c1e7f/validation.md) passed; release approval remains separate.
- **Canonical repository/ref/path:** public `burdettadam/park-commerce-workbench`, `goal/G-0004`, this file. The current Codex session is the initial integration writer and sole branch writer. Adam retains scope, architecture and repository-administration authority. Additional worktrees are assigned only if actual work benefits from them.

## Acceptance criteria and independent expected results

The four outcomes are accepted from proposal-2 and passed for fixed source S `e7c1e7f309bd976b6bb92e575374c1bb1add316d`. The [filled evidence manifest](https://github.com/burdettadam/park-commerce-workbench/blob/90ae8eff6485c950c8d02c8d3a33893059f5d82e/goals/G-0004/evidence/e7c1e7f/manifest.json) records actual checks, artifacts, reviews and limits.

### G-0004-AC01

Maven compiles all backend modules and optional Vaadin module; Angular production build completes. Record the actual JDK/Maven/Node/npm and framework versions rather than assuming candidate pins are compatible.

Required evidence: `G-0004-A01` (`build-results.md`), raw build outputs, source S and independent review coverage.

### G-0004-AC02

Review the existing Angular lockfile and resolve the Vaadin frontend lock strategy from actual generated inputs. A second clean dependency installation/build uses retained inputs and succeeds without an unexplained lockfile change.

Required evidence: `G-0004-A02` (`dependency-locks.md`), clean rebuild output, source S and independent review coverage.

### G-0004-AC03

Existing core, client-state and tooling suites run with actual result/selected-test counts. Skipped application tests during a packaging command are not recorded as acceptance passes.

Required evidence: `G-0004-A01` (`build-results.md`), raw suite outputs, source S and independent review coverage.

### G-0004-AC04

Document every candidate version correction and dependency decision; retain unresolved vulnerability or compatibility findings with owner and disposition before this goal becomes ready.

Required evidence: `G-0004-A03` (`compatibility-decisions.md`), dependency/compatibility observations, source S and independent review coverage.

The [artifact contract](../../docs/roadmap/ARTIFACT-CONTRACT.md) and [unfilled G-0004 manifest template](../../docs/roadmap/goals/G-0004/manifest.template.json) apply. Common environment, plan, configuration, raw checks, role-specific reviews, intended-use validation, decision and handoff artifacts are required for a ready claim. An empty template or prior goal evidence does not pass G-0004.

## Execution and status

| Slice | Ref | Writer | Included revision | Record |
| --- | --- | --- | --- | --- |
| Canonical integration and implementation | `goal/G-0004` | Codex integration writer | S `e7c1e7f309bd976b6bb92e575374c1bb1add316d` from merged base; final report E `3eff0a06f02110f19f871084ccbe003c9beaa52a` | [bootstrap](work/bootstrap.md), [build investigation](work/build-investigation.md), [final retrieval](work/final-retrieval.md) |

**Verification:** [S CI run](https://github.com/burdettadam/park-commerce-workbench/actions/runs/38336113859) passed Java integration, Angular client and forced Vaadin frontend jobs. The selected suites include 280 core, 64 database integration, 29 pricing, 66 client state and 29 tooling checks, with zero failures; the optional admin module has no application tests. Two separate clean S checkouts ran Vaadin `npm ci` and Vite with unchanged retained locks; the later run captured checkout and volume provenance. A fresh public clone also passed storefront `npm ci`, strict types, production build and 66 state tests. [A01](https://github.com/burdettadam/park-commerce-workbench/blob/90ae8eff6485c950c8d02c8d3a33893059f5d82e/goals/G-0004/evidence/e7c1e7f/build-results.md), [A02](https://github.com/burdettadam/park-commerce-workbench/blob/90ae8eff6485c950c8d02c8d3a33893059f5d82e/goals/G-0004/evidence/e7c1e7f/dependency-locks.md) and [A03](https://github.com/burdettadam/park-commerce-workbench/blob/90ae8eff6485c950c8d02c8d3a33893059f5d82e/goals/G-0004/evidence/e7c1e7f/compatibility-decisions.md) retain exact versions, security/compatibility dispositions and raw outputs.

**Review, validation and recovery:** Two configured native reviewers closed the first-round provenance findings and found zero open blockers in the third and final round. The interrupted third-round contract thread was resumed and returned its own final report. Adam delegated the bounded intended-use check, which passed. The manifest validator passed with six declared checks and 29 local artifacts. Final report E `3eff0a06f02110f19f871084ccbe003c9beaa52a` was pushed and freshly retrieved: 35/35 committed evidence files matched, S was present, the validator passed in that clone, and no `.env` was tracked. [Final retrieval](work/final-retrieval.md) and [handoff](https://github.com/burdettadam/park-commerce-workbench/blob/90ae8eff6485c950c8d02c8d3a33893059f5d82e/goals/G-0004/evidence/e7c1e7f/handoff.md) distinguish remotely retained files from local-only toolchains, caches and raw rollouts.

**Promotion:** [PR #5](https://github.com/burdettadam/park-commerce-workbench/pull/5) was squash-merged into `main` at M `c873cfe1b8dd93732ef0d63f1b890b9774174934` on 13 October 2026 UTC. The reviewed branch head `e98547428cb1bd9ab06718926453a613d253ec06` remains retained at `origin/goal/G-0004`. The merge produced no version tag, release artifact or deployment; separate eligible human review is still required before any future version-producing merge. This paragraph is a post-merge status update, not a change to source S, plan P or its acceptance evidence.
