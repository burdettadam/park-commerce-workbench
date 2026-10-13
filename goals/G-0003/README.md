# G-0003 - Recover a goal across two worktrees and a session interruption

Status: **ready**. Milestone: M0. Dependency: [G-0002 ready](../G-0002/README.md) on retained `goal/G-0002`; neither prerequisite is merged into `main`.

## Accepted work contract

- **Authority:** Adam Burdett's 12 October 2026 instruction to make the next goal active accepts the unchanged [G-0003 proposal-1](../../docs/roadmap/goals/G-0003/README.md), last changed at `e9b3fa9acf6d8c5dc2a0bde9c0c40f1913ca5c7d`. This active revision is `G-0003-active-1`. The exact activation commit P is recorded in [bootstrap](work/bootstrap.md) after Git creates it. The proposal remains historical navigation; changes to the four criteria need an owner decision.
- **Governing baseline:** `AGENTS.md`, `GOVERNANCE.md`, `DEVELOPMENT.md`, `ARCHITECTURE.md`, `PLANS.md`, both project skills and the two configured native reviewer roles as present at G-0002 ready checkpoint `a0cc50650a34c1f131eef824d9a074bd0e945605`. The public `goal/G-0003` branch starts at that exact commit. G-0002's results are prerequisite evidence, not new G-0003 checks.
- **Intended user and outcome:** a fresh Codex session can resume this goal from retained plan, branch and artifact records without relying on this chat or guessing which work was included. Demonstrate two independently scoped development worktrees and a measured handoff.
- **Conditions and limits:** controlled Windows/Git Bash drill with synthetic local data. Use a local bundle/ZIP or a verified-private remote for the reconstruction exercise. Publish only sanitized source and reports to the existing public project branch. A local bundle is not an off-device backup. Do not remove worktrees, bundles or required evidence until retrieval is demonstrated. No new scheduler, command protocol, task database, cloud deployment, version release or public product-contract change is authorized.
- **Ownership:** Adam Burdett owns scope, architecture and repository administration decisions and delegated this goal's intended-use check to Codex. The current Codex session owns `goal/G-0003` integration and this canonical record. Implementation/configuration and independent test/documentation slices have separate named worktree assignments and recorded branch heads below.
- **Risk and review:** high risk because the goal tests isolation and recoverability. Two independent native reviewers (`contract-reviewer`, `adversarial-reviewer`), at most three rounds, under the accepted [review guide](../../.agents/skills/work-goal/references/review-guide.md). Record independent first findings, actual stale/missing-evidence blockers, and their disposition before readiness. A version-producing merge still requires a distinct eligible human review.
- **Validation method:** show Adam the branch map, measured fresh-session restore, deliberate gate challenges and remaining local-only limits. Record his actual validation or explicit delegation. No intended-use validation is claimed at activation.
- **Canonical repository/ref/path:** public `burdettadam/park-commerce-workbench`, `goal/G-0003`, this file. This stacked branch is not a merge into `main`.

## Acceptance criteria and independent expected results

The four outcomes below are accepted verbatim from proposal-1 and passed the bounded drill. Their checks and limits are in the [final evidence manifest](evidence/4a3d26e/manifest.json).

### G-0003-AC01

Two named worktrees have nonconflicting ports, Compose project names and data volumes; each has one identified writer and progress record based on the same accepted plan revision.

Required artifact: `G-0003-A01` (`branch-map.md`) and actual isolation checks.

### G-0003-AC02

Record at least two included branch commits, one explicit nonincluded alternative or change proposal, and the assembled candidate. An unaccepted scope change must not silently modify success criteria.

Required artifact: `G-0003-A01` (`branch-map.md`) and exact Git identities.

### G-0003-AC03

Interrupt the authoring session, preserve a checkpoint, and resume in a fresh directory/session using only retained records. Retrieve every indispensable artifact and match its digest before reporting complete recovery.

Required artifact: `G-0003-A02` (`restore-drill.md`) and actual retrieval checks.

### G-0003-AC04

Deliberately present one stale review and one missing required artifact to the review process. Both remain blockers. Record actual gate observations, handoff duration and unresolved items without pretending this is a statistical model evaluation.

Required artifact: `G-0003-A03` (`workflow-observations.md`) and retained gate outputs.

The [artifact contract](../../docs/roadmap/ARTIFACT-CONTRACT.md) and deliberately [unfilled manifest template](../../docs/roadmap/goals/G-0003/manifest.template.json) apply. Freeze source S, accepted P, environment/configuration and actual checks before final reviews. Keep S distinct from later report commit E.

## Branch, resource and recovery setup

| Slice | Ref | Writer | Included revision | Record |
| --- | --- | --- | --- | --- |
| Canonical integration | `goal/G-0003` | Current Codex session | Source S `4a3d26e` assembled from G-0002 ready base `a0cc506` | [bootstrap](work/bootstrap.md), [integration](work/integration.md) |
| Implementation/configuration | `goal/G-0003-implementation` | Codex integration writer | `ecc1439` included via `f8098e4` | [implementation](work/implementation.md) |
| Independent test/documentation | `goal/G-0003-validation` | Independent Codex writer | `7eed2b0` included via `12a67c6` | [validation](work/validation.md) |

The two worktrees used different Compose project names, host-port sets and private `.env` files. Actual container labels, service health, endpoint identity, cross-catalog reads and mounted volume names were checked in the [pair identity](evidence/4a3d26e/checks/pair-identity.json) and [cross-catalog](evidence/4a3d26e/checks/catalog-cross.json) outputs.

**Current state:** ready for the bounded local workflow drill. Both slice branches are integrated in fixed source S 4a3d26e. Two isolated six-service stacks each passed 43 HTTP checks; a fresh session recovered 23 indispensable files and verified 487 source hashes. The first native reviews blocked the stale-review and missing-artifact negative fixture. The final third-round reviews passed the inspected evidence scope with zero open blockers. Adam delegated intended-use validation to Codex; the [validation](evidence/4a3d26e/validation.md) and [readiness decision](evidence/4a3d26e/decision.md) record the bounded result. The root development stack is healthy after a documented cleanup error; both drill stacks are stopped with named volumes retained.

**Next action:** start G-0004 for local build/toolchain and product work. Preserve local recovery materials until their retention disposition is recorded.

**Release and recovery:** no version-producing merge, artifact, tag or deployment. Activation publication and clean retrieval are recorded in [bootstrap](work/bootstrap.md); local drill bundles, credentials and raw logs must be identified separately. Ready status requires all four criteria, two final native reviews, actual intended-use validation and recoverable indispensable evidence.
