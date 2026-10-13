# G-0007 — Add reviewable catalog drafts and publication history

Status: **ready** (accepted goal gate; not merged or released). Milestone: M2. Dependency: [G-0006 ready](../G-0006/README.md), promoted by [PR #8](https://github.com/burdettadam/park-commerce-workbench/pull/8) to `main` at `c734a95836388ec8316a06bac8c7361d9ef8b0de`.

## Accepted work contract

- **Authority and revision:** Adam Burdett's 2026-10-13 instruction to activate the next goal accepts the unchanged [G-0007 proposal-1](../../docs/roadmap/goals/G-0007/README.md), SHA-256 `6e7f3acadcd7b5110efb362280b85541573788dda1a9162a94e61a5646ac31d`. This active record is `G-0007-active-1`. Record its activation commit P and exact plan digest in [bootstrap](work/bootstrap.md) after Git creates P. Material changes to scope or criteria require Adam's decision.
- **Governing baseline:** Root `AGENTS.md`, `GOVERNANCE.md`, `DEVELOPMENT.md`, `ARCHITECTURE.md`, `PLANS.md`, `.agents/skills/work-goal/`, `.agents/skills/worktree-flow/`, and configured native `contract-reviewer` and `adversarial-reviewer` roles at exact base `c734a95836388ec8316a06bac8c7361d9ef8b0de`. The root is the only applicable repository `AGENTS.md`; no nested override is known. Review control changes under this baseline.
- **Risk and review:** medium risk for catalog publication, concurrency, persistent history and guest visibility. Two independent native reviewers, contract and adversarial, at most three rounds. Freeze a source candidate before acceptance checks and reviews; relevant changes reopen affected coverage.
- **Canonical repository/ref/path:** public `burdettadam/park-commerce-workbench`, `goal/G-0007`, this file. The current Codex session is integration writer and sole assigned branch writer. Adam retains scope, architecture and repository administration authority. No additional branch or writer is assigned.
- **Intended-use validation:** Adam Burdett owns validation of draft review, publication, conflict and history behavior, or may explicitly delegate this goal's check. Delegation for G-0006 does not imply G-0007 delegation. Human tagged-release review remains separate.
- **Cost boundary:** use local tools and the public repository's standard free GitHub runners only. Do not start a paid runner, browser service or workflow.

## Intended user and observable outcome

An employee distinguishes drafts from published catalog revisions and can inspect who changed what.

Reviewer roles are separate from the implementation writer.

## Scope

Verify and complete draft lifecycle, optimistic version checks, stable paginated history and audit metadata around catalog publication. The imported candidate may already implement part of this behavior; do not duplicate it.

## Exclusions

No silent rewriting of published documents, arbitrary cross-park catalog engine or real customer data.

## Preconditions and authority boundaries

Local synthetic-data work only; no deployment or external write authorization.

The ready G-0006 candidate and its predecessor goals are included in the exact `main` base above. The baseline already contains a catalog-publication implementation candidate and [contract](../../docs/project/catalog-publication.md); its prior in-memory/domain observations are not G-0007 acceptance evidence. Inspect and reuse the canonical implementation, then run the accepted real MongoDB/API and employee workflow checks at a fixed source revision. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Catalog draft/publication/history API and Mongo indexes.
- Contracts and tests for concurrency, guest visibility and audit.

## Acceptance criteria

The following outcomes are accepted verbatim from proposal-1. They are not yet observed G-0007 results. Do not weaken or delete a criterion because the current implementation fails it.

### G-0007-AC01

Draft changes are invisible to guest reads until an authorized publication; publication creates one new immutable revision and retains the previous revision.

Required evidence: `G-0007-A01` plus the relevant raw check, source S and independent review coverage.

### G-0007-AC02

Two employee writes based on the same revision cannot silently overwrite one another; one receives a documented conflict and no duplicate published revision is created.

Required evidence: `G-0007-A02` plus the relevant raw check, source S and independent review coverage.

### G-0007-AC03

History pagination has a stable documented ordering/cursor and deterministic behavior while new revisions arrive; exercise first/last/empty pages and invalid cursors.

Required evidence: `G-0007-A02` plus the relevant raw check, source S and independent review coverage.

### G-0007-AC04

Audit records identify authenticated actor, operation, product, old/new revisions and timestamp without storing bearer tokens or full sensitive payloads.

Required evidence: `G-0007-A03` plus the relevant raw check, source S and independent review coverage.

## Required goal-specific artifacts

Relative to the eventual candidate evidence directory, for example `goals/G-0007/evidence/<candidate>/`. These are output requirements, not generated evidence.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0007-A01` | `publication-scenarios.md` | Draft/publish/guest-visibility examples and tests. |
| `G-0007-A02` | `history-and-conflicts.md` | Concurrent publication and pagination evidence. |
| `G-0007-A03` | `audit-example.json` | Sanitized representative audit events, with provenance. |

The common manifest, environment, raw checks, independent contract/adversarial review reports, intended-use validation, decision and handoff are also mandatory. Start from [the unfilled manifest template](../../docs/roadmap/goals/G-0007/manifest.template.json) and [artifact contract](../../docs/roadmap/ARTIFACT-CONTRACT.md). The unfilled template deliberately fails the readiness checker.

## Validation commands and new tests

- `bash scripts/verify.sh --integration` — existing. Maven/JDK/Docker required; real database tests must not silently skip.
- `node scripts/demo-http.mjs` — existing. Requires running local services and .env; creates synthetic data.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove this goal. Select focused G-0007 commands after inspecting the actual code and retain executed commands, working directory and inputs. The imported candidate's prior in-memory runs are exploratory until verified at a clean fixed source with real MongoDB and HTTP behavior.

## Execution and ownership

| Slice | Ref | Writer | Included commit |
| --- | --- | --- | --- |
| Canonical integration, implementation and tests | `goal/G-0007` | Current Codex session | Base `c734a95836388ec8316a06bac8c7361d9ef8b0de`; P recorded in [bootstrap](work/bootstrap.md) |
| Additional independent work | Not assigned | Unassigned | Not created |

Start with one branch. Assign separate writers and refs only when useful; record exact included revisions and disposition for abandoned alternatives. Native reviewer roles remain independent of the branch writer.

## Review and stop rules

**Adopted G-0007-only exception, 2026-10-13:** Adam Burdett authorized one new round after the third-round privacy blocker and evidence replacement. The [decision artifact](evidence/15c70f1/review-exception.md) records scope, preceding-baseline control assessments, owner, mitigation and expiry. G-0007's maximum is now four rounds; the new final pair must both report round four with distinct configured native sessions and inspect the sanitized evidence at source S. Every other review and acceptance rule below remains effective. A surviving valid blocker after round four returns this goal to `needs-decision`. At that checkpoint no fifth round was authorized.

The accepted baseline is two independent reviewers (contract and adversarial) and at most three rounds. Collect first findings independently. A relevant code/test/configuration/input change creates a new candidate and reopens affected verification and review. Retain valid blockers even when another reviewer disagrees. Missing reviews are not passes; unresolved blockers after the review limit require `needs-decision`.

Changes to these rules are reviewed under the previous accepted rules, never self-approved by the candidate. Version-producing merges require an actual human decision. Merely reaching `ready` does not create a release, deploy anything or change visibility.

## Handoff and recovery

Retain source/branch/artifact checkpoints before the canonical coordination record, identify every indispensable component, then prove retrieval. Until privacy is verified, use local artifacts and label coverage `local_only`; do not claim off-device recovery. Record source S separately from later evidence/report commit E so the report does not need its own future commit hash.

## Activation and next action

- Accepted plan P `e5add22f8777c358e0a27981b1bb74e03f52bb47`; exact plan SHA-256 `c2c888455143d271dd2a7d6e9a41137a28d68dc522cb2b04a80aeb83df28ae23` is recorded in [bootstrap](work/bootstrap.md). Draft [PR #9](https://github.com/burdettadam/park-commerce-workbench/pull/9) targets `main`.
- Source candidate S: not selected. Base `c734a95836388ec8316a06bac8c7361d9ef8b0de` contains the imported catalog-publication candidate; its source and prior tests need a G-0007 gap audit before acceptance claims.
- Runtime: no G-0007-owned Compose project, database, browser or API instance has been started. Existing G-0006 local processes/volumes are not assigned to this goal; verify ownership and isolation before reuse.
- Verification, independent reviews, intended-use validation, readiness and release: not performed for G-0007 at activation. No paid workflow or service has been used.
- The public activation checkpoint `949fd4a9b9a030755d4de31dc0f735f8fd36bc50` was retrieved cleanly on a fresh path with the exact plan digest and base ancestry. Next action: inspect the existing catalog-publication code/contracts/tests for AC01–AC04 and DRY/architecture risks, then select focused real MongoDB/API and employee workflow checks before freezing S.

[Milestone M2](../../docs/roadmap/milestones/M2.md) · [Roadmap](../../docs/roadmap/README.md) · [Commands](../../docs/roadmap/COMMANDS.md)

## Current checkpoint after verification

Fixed source S `15c70f15154997f6eda7b72ad70aa3eba76e5fdd` passed 280 core and 67 real database tests, 43 live HTTP checks, and a focused employee browser flow. The production Vaadin build and public hosted CI also passed. [Verification work](work/verification.md) and [candidate evidence](evidence/15c70f1/) retain the results, including the failed local browser attempts and their limits. Adam delegated this goal's intended-use check to Codex; that validation accepted AC01–AC04 for synthetic local use.

The accepted third independent review round found local username paths in a published compressed test log. The archive was replaced and the post-E1 branch history containing it was withdrawn from the active PR ref, but at that checkpoint no independent reviewer had cleared the replacement. Adam authorized one new review round; the preceding-baseline control assessment and scoped validator-contract amendment are recorded above. The prior [decision record](evidence/15c70f1/decision.md) retains the failed third round; the new pair had not yet cleared the replacement. PR #9 was draft and unmerged. No release or paid workflow is requested.

## Fourth-round disposition

The owner-authorized fourth pair inspected the fixed product S and sanitized replacement at public evidence checkpoint `e7108e16b50fcb8baff61d4340ddc951dba52186`. Both configured native reviewers blocked readiness because the first failed browser attempt's raw transcript was overwritten; the accepted evidence contract requires retaining failed attempts and that output cannot be recreated from the later runs. The [round-four disposition](evidence/15c70f1/reviews/round4-disposition.md) retains each finding and the retrieval limits. The one additional round is consumed. G-0007 was `needs-decision` at that checkpoint; there was no fifth round, ready claim, merge or release approval then.

The [owner decision record](work/lost-browser-transcript-decision.md) describes the narrow evidence exception and one further review pair subsequently authorized and adopted below.

## Fifth-round scoped exception

Adam Burdett authorized a one-time waiver for the irretrievably overwritten raw output of the first exploratory browser failure and one further independent contract/adversarial pair. The [decision artifact](evidence/15c70f1/lost-transcript-exception.md), [control assessments](work/lost-browser-transcript-decision.md), and [artifact-contract amendment](../../docs/roadmap/ARTIFACT-CONTRACT.md) were reviewed under the preceding accepted R=4 control and are adopted for G-0007 only. The final pair both reported round five against the fixed product S and current evidence. AC01 through AC04, selected checks, privacy, DRY/architecture review, intended-use validation and the distinct human tagged-release rule remain in force. The waiver applies to that single past raw transcript only. No valid blocker remained after that pair; no sixth round is authorized. PR #9 remains draft pending the final merge gate.

## Ready evidence and final retrieval

The [ready manifest](evidence/15c70f1/manifest.json) declares fixed product S, the accepted plan/configuration digests, 3 selected checks, 34 retained artifacts, two passing independent fifth-round reviews, delegated intended-use validation, the scoped exception and the integration writer's readiness decision. A clean fresh public clone of evidence commit `57a5bda317281e53f01ee474ca35dce9afb2fe47` passed `node scripts/validate-evidence.mjs goals/G-0007/evidence/15c70f1/manifest.json`; all 34 artifact digests matched, and the five compressed archives had no known local username or Windows user-path prefix. This is a ready goal checkpoint, not a merge, tagged version, deployment or human release approval. The first exploratory browser failure's raw output remains lost under the narrow owner exception; the exact originating audit API response is unavailable. PR #9 targets main and is still draft at this record.

## Main promotion

[PR #9](https://github.com/burdettadam/park-commerce-workbench/pull/9) was squash-merged to `main` on 2026-10-13 at M `1488fe0ba5aee271097bdcb0f03b2d930423e5b3` after all three free public CI jobs passed at head `8a4c3551144e993c6675cc67eac3d47da631cb9a` ([run 38455699530](https://github.com/burdettadam/park-commerce-workbench/actions/runs/38455699530)). The squash commit M is separate from product source S `15c70f15154997f6eda7b72ad70aa3eba76e5fdd` and ready evidence E `57a5bda317281e53f01ee474ca35dce9afb2fe47`; the retained `goal/G-0007` branch preserves those original identities. The merge created no tag, release artifact or deployment. Distinct eligible-human review remains required before any future tagged-version merge.
