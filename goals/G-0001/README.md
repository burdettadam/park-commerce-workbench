# G-0001 — Adopt the actual initializer and accepted project rules

Status: **active**. Milestone: M0. Dependencies: none; human bootstrap authority is required.

## Accepted work contract

- **Authority to start:** the project owner's 12 October 2026 request to make an active goal from the first documented goal and achieve its proposed plan.
- **Accepted source proposal:** [G-0001 proposal-2](../../docs/roadmap/goals/G-0001/README.md) at `0a68d87d2312a1dd40f47a6ef2cd0bdaa938b80c`. This active record is revision `G-0001-active-1`. The proposal remains historical navigation; changes to the four criteria require a recorded human scope decision.
- **Governing baseline:** `AGENTS.md`, `PLANS.md`, `GOVERNANCE.md`, `DEVELOPMENT.md`, and `ARCHITECTURE.md` at the accepted proposal commit. Adam Burdett subsequently [adopted the seven first-local-goal choices](work/owner-decisions.md) on 12 October 2026. Adoption does not grant agent release authority or waive candidate verification and review.
- **Intended user and outcome:** a fresh Codex/editor session can discover the real `work-goal` and `worktree-flow` skills and reviewer roles, locate accepted project rules, and resume from Git without prior chat.
- **Operating conditions:** public repository; Windows development host with Git for Windows Bash and the reviewed sibling `git-agent 0.2.0`; synthetic/local data only. Run actual commands from this worktree root. Scrub outputs before publishing to the public repository.
- **Quality limits and exclusions:** preserve existing guidance and secrets; do not copy the private initializer executable, add an agent execution layer, change repository visibility, publish a version, deploy, or claim a clean restore from a local-only copy. Existing build/CI observations may inform this goal but do not replace its own current evidence.
- **Risk/review profile:** high; select the proposal's contract and adversarial reviewer assignments, N=2, R≤3 for this goal. Their independence and current-candidate coverage must be demonstrated before `ready`. Formal project-wide review policy is an AC03 adoption decision, not an inference from these words.
- **Validation owner and method:** Adam Burdett, as the human project owner named by the proposal, decides project rule adoption and checks that a fresh native session finds the adopted guidance. No human validation is recorded yet.
- **Canonical repository/ref/path:** `burdettadam/park-commerce-workbench`, `goal/G-0001`, this file. The current Codex session is the sole branch writer and interim integration record maintainer; human project decisions belong to Adam Burdett. Reconcile ownership before another writer takes over.

## Acceptance criteria and expected results

These four criteria are copied from the accepted proposal; all remain open until supported by candidate-specific evidence.

### G-0001-AC01

Record the exact output of git agent --version and verify the reviewed version is exactly 0.2.0 before invoking the current wrapper; reject look-alike/prerelease versions. Run a fresh preview and safe rerun against this worktree root, retaining exit codes and adoption gaps. Link the historical first application separately.

Required goal artifact: `G-0001-A01` (`adoption.md`), raw command results and independent review coverage.

### G-0001-AC02

A fresh native agent/editor session identifies the generated instruction entry point, work-goal/worktree-flow skills and two reviewer roles; retain observable discovery output rather than merely listing files.

Required goal artifact: `G-0001-A02` (`discovery.txt`), raw observation and independent review coverage.

### G-0001-AC03

The human adopts or explicitly rejects each product-command, owner, review, isolation and release-control choice. No required UNCONFIGURED field remains for the first local goal; unresolved hosted controls are recorded as remote-work blockers.

Required goal artifact: `G-0001-A01` (`adoption.md`), actual human decisions and independent review coverage.

### G-0001-AC04

The accepted PLANS guidance points to this roadmap and one canonical active-goal location. No .env, private executable or secret enters tracked files. Record the actual public repository, protected-branch, CI, and human-review controls before claiming remote recovery or release safety.

Required goal artifact: `G-0001-A03` (`controls-and-privacy.md`), observed controls and independent review coverage.

The common [artifact contract](../../docs/roadmap/ARTIFACT-CONTRACT.md) and [unfilled manifest template](../../docs/roadmap/goals/G-0001/manifest.template.json) also apply. Planned filenames are requirements, not evidence that work has passed.

## Scope and decision boundary

Run an exact-version check, real initializer preview and safe rerun; inspect adopted/proposed/conflicting guidance; decide product commands, ownership, reviewers, isolation and recovery; verify native discovery and hosted controls. Reuse the generated guidance and existing product commands. A new dependency, public-contract change, policy relaxation or version-producing merge needs its own applicable authority. The accepted scope does not include product feature work.

The owner has decided the scope/architecture/repository/release authority, N/R policy, first-local-goal isolation and recovery targets, and the distinct-human requirement for a version-producing merge. A specific eligible human reviewer and an enforceable hosted approval route are still unresolved; that later merge is blocked until resolved. These open hosted controls are not exceptions to the adopted policy.

## Branch and candidate record

| Slice | Actual ref | Writer/role | Plan revision | Dependencies | Included revision | Work/evidence |
| --- | --- | --- | --- | --- | --- | --- |
| Bootstrap adoption and coordination | `goal/G-0001` | Current Codex session; one writer | `G-0001-active-1` | Proposal commit `0a68d87` and reviewed local initializer | `c8c46a2` plan, `8e2e18f` adopted candidate | [bootstrap progress](work/bootstrap.md), [current candidate observations](evidence/8e2e18f/adoption.md) |

Source candidate S: `8e2e18f04906cd9bdbd8344c74d3fd222ebd4e2e`; base and accepted active-plan commit P: `c8c46a2824f558e33c9e640fe6fc82dfdf705399`. Git Bash/Git/Codex versions, resolved reviewed executable digest, exact version, successful preview/apply, and tooling checks are in [current adoption observations](evidence/8e2e18f/adoption.md). The worktree was clean when the fixed-candidate initializer and tooling checks ran. This later report commit E describes S; it does not claim to be S. [Earlier candidate evidence](evidence/1ba4cac/adoption.md) remains historical.

## Progress, review and recovery

- **Current state:** active. The owner [adopted the first-local-goal choices](work/owner-decisions.md) and they are represented in S. AC01 has fresh fixed-source command observations; AC02/AC04 have partial observations. The four criteria await final current-candidate review and validation.
- **Verification/validation:** exact reviewed executable/version output, real preview/rerun exits 0 and 29 mock tooling checks were observed on S. The earlier exit-3 proposal is resolved by the owner-adopted digest line. Native role invocation, two counted reviews and intended-use human validation remain open; S was independently retrieved from the remote.
- **Reviews:** 0 of 2 required native-role current-candidate reviews; round 0 of at most 3. Two independent generic collaboration reviews of S are [retained as preliminary findings](evidence/8e2e18f/preliminary-reviews.md) and do not prove custom-role loading or grant human approval.
- **Exceptions/debt:** no waiver. Adam Burdett owns the adopted first-local-goal policy. Native custom-role selection and an eligible hosted human approval route remain open, with no false enforcement claim.
- **Release:** no version-producing merge approval requested; no merge M, distributable D, tag, deployment or health claim.
- **Recovery:** S `8e2e18f`, report commit `9ff6fb9`, and handoff commit `becd323` were pushed. Separate clean public clones retrieved their tracked content with respectively 425, 433 and 434 manifest entries matching. The adopted one-hour active-work RPO and one-working-day RTO are targets, not measured achievements; private settings are not covered by the public clone.
- **Next action:** use the [fresh-session validation procedure](work/validation-request.md), resolve native custom-role selection, obtain two counted current-candidate reviews and intended-use human validation, then check all four criteria against pushed current-source evidence before claiming readiness.
