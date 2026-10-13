# G-0004 — Resolve and lock the actual Java and UI builds

Status: **active**. Milestone: M1. Dependency: [G-0003 ready](../G-0003/README.md), now included in `main` at `d7ed805158bd6a239480ce7ae03312c139f2acc7`.

## Accepted work contract

- **Authority and revision:** Adam Burdett's instruction to merge completed branches and make the next goal active accepts [G-0004 proposal-2](../../docs/roadmap/goals/G-0004/README.md) without changing its four criteria. The proposal bytes on the merged base have SHA-256 `5a8819f52d13fd43f662f53b63439b5798b67af091c799d18e317cb7f6a9c40c`. This active record is `G-0004-active-1`; [bootstrap](work/bootstrap.md) records its activation commit after Git creates it. The proposal remains historical input. Material scope or criterion changes require Adam's decision.
- **Governing baseline:** `AGENTS.md`, `GOVERNANCE.md`, `DEVELOPMENT.md`, `ARCHITECTURE.md`, `PLANS.md`, the two project skills and two configured native reviewer roles on merged `main` commit `d7ed805158bd6a239480ce7ae03312c139f2acc7`. Review changes to controls under that baseline.
- **Intended user and outcome:** a developer with a clean machine can compile the existing Spring services and the optional Vaadin admin console, build the Angular storefront, and rerun the retained test suites from reviewed reproducible inputs.
- **Scope:** recheck the actual dependency and toolchain baseline, repair remaining compilation issues, resolve the Vaadin frontend lock strategy from generated inputs, and retain dependency trees, lockfile provenance, exact versions and build results. Search existing dependency declarations and build behavior before adding new configuration; keep native manifests authoritative.
- **Conditions and exclusions:** local synthetic-data development. No fake lockfiles, unreviewed stack replacement, new product feature, cloud deployment, or successful-build claim from core-only tests. No material architecture or public-contract change is preapproved. No release, tag or version-producing merge is part of activation.
- **Quality and review:** medium risk because this goal changes build inputs and reproducibility across Java, Vaadin and Angular. Use the adopted two independent native reviewers (`contract-reviewer`, `adversarial-reviewer`), with at most three rounds. Freeze a source candidate before selected checks and reviews; changed relevant inputs reopen coverage. Record dependency security and compatibility findings with owner/disposition.
- **Validation:** Adam Burdett owns intended-use validation. No G-0004 validation or delegation has occurred. Show the exact clean-build and reinstall results before requesting his validation or explicit delegation.
- **Canonical repository/ref/path:** public `burdettadam/park-commerce-workbench`, `goal/G-0004`, this file. The current Codex session is the initial integration writer and sole branch writer. Adam retains scope, architecture and repository-administration authority. Additional worktrees are assigned only if actual work benefits from them.

## Acceptance criteria and independent expected results

The four outcomes are accepted from proposal-2. All are **open**; their text is not an execution result.

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
| Canonical integration and initial implementation | `goal/G-0004` | Current Codex session | None beyond merged base; no G-0004 candidate yet | [bootstrap](work/bootstrap.md) |

Planned checks, to be selected and run against fixed inputs: `bash scripts/verify.sh --java`, `mvn -B -ntp -Pui -pl admin-console -am verify`, `cd storefront && npm ci && npm run build`, `node --test storefront/src/app/checkout-state.test.mjs`, `bash scripts/test-core.sh --demo`, and `bash scripts/test-tooling.sh`. Record exact commands, working directories, tool versions, selected counts, exit codes, skipped tests and raw outputs. Add focused checks where the actual fixes require them. No command is claimed passed at activation.

**Current state:** active plan only. The merged base contains the G-0001–G-0003 ready records and prior CI, but no G-0004 source candidate, clean reinstall proof, final reviews or intended-use validation. The known Windows host JDK 17 versus required JDK 21 gap and optional Vaadin/UI build path are initial investigation targets; the Java 21 container core pass from G-0003 is historical, narrower evidence.

**Next action:** inventory local and clean-container toolchain versions, dependency/lock inputs and current build failures; choose the smallest repair slice, then run the maintained builds and suites. Preserve the accepted four criteria. No release or deployment is requested.
