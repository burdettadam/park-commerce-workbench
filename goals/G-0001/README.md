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
| Bootstrap adoption and coordination | `goal/G-0001` | Current Codex session; one writer | `G-0001-active-1` | Proposal commit `0a68d87` and reviewed local initializer | `c8c46a2` plan, `1ba4cac` candidate | [bootstrap progress](work/bootstrap.md), [candidate observations](evidence/1ba4cac/adoption.md) |

Source candidate S: `1ba4cace00f0d70ec596fccb85d84df0ba47ec00`; base and accepted active-plan commit P: `c8c46a2824f558e33c9e640fe6fc82dfdf705399`. Git Bash/Git/Codex versions and the reviewed initializer executable digest are recorded in [adoption observations](evidence/1ba4cac/adoption.md). The worktree was clean when the fixed-candidate initializer and tooling checks ran. This later report commit E describes S; it does not claim to be S. Earlier CI observations remain historical context.

## Progress, review and recovery

- **Current state:** active. The owner [adopted the first-local-goal choices](work/owner-decisions.md); their maintained-document implementation is being verified. AC01 has historical fixed-source command observations, and AC02/AC04 have partial observations.
- **Verification/validation:** exact version output, real preview/rerun and 29 mock tooling checks were observed on S. Preview and apply both exited 3 because the preserved generic README proposal needs a disposition; this is not a command failure hidden as green. No criterion is marked passed yet. Fresh role invocation, project policy adoption, independent reviews and intended-use validation remain open.
- **Reviews:** 0 of 2 current-candidate independent reviews; round 0 of at most 3. No reviewer finding or human approval is invented.
- **Exceptions/debt:** no waiver. Adam Burdett owns the adopted first-local-goal policy. Native custom-role selection and an eligible hosted human approval route remain open, with no false enforcement claim.
- **Release:** no version-producing merge approval requested; no merge M, distributable D, tag, deployment or health claim.
- **Recovery:** the branch is pushed through report commit `c19c1bd`; a clean public clone retrieved the 424-entry manifest with zero mismatches. The adopted one-hour active-work RPO and one-working-day RTO are targets, not measured achievements; private settings and current unpushed changes are not covered by that clone.
- **Next action:** verify the adopted-document candidate with the actual initializer, update discovery evidence, obtain two independent current-candidate reviews, and resolve native custom-role selection before claiming readiness.
