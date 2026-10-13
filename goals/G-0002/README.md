# G-0002 - Validate the existing version guard with a red/green challenge

Status: **ready** for the accepted local G-0002 goal. Milestone: M0. Dependency: [G-0001 ready](../G-0001/README.md) on its retained branch; neither goal is merged into `main`.

## Accepted work contract

- **Authority:** Adam Burdett's 12 October 2026 request to make the next goal active accepts the unchanged [G-0002 proposal-2](../../docs/roadmap/goals/G-0002/README.md) at `0a68d87d2312a1dd40f47a6ef2cd0bdaa938b80c`. This active record is revision `G-0002-active-1`, frozen at activation commit P `56ea84e4e0d0429e3c7069c7a48efd1d903b9bec`. The proposal is historical navigation; changing the four criteria requires a recorded owner decision.
- **Governing baseline:** `AGENTS.md`, `PLANS.md`, `GOVERNANCE.md`, `DEVELOPMENT.md`, `ARCHITECTURE.md`, the two project skills and configured reviewer roles at the G-0001 ready checkpoint `f4ca6dc02d96a309763b1725b2eb3782cadd8f76`. The new branch starts at that exact commit. Its G-0001 source/evidence is a prerequisite, not a fresh G-0002 check.
- **Intended user and outcome:** the project helper accepts only reviewed `git-agent 0.2.0` while preserving its existing root, argument, configuration and exit-code behavior. The goal tests the current guard; it changes product code only if the exercise reveals a real defect.
- **Conditions and limits:** local synthetic-data exercise on the documented Windows/Git Bash pilot host. Use a disposable, committed local-only mutation branch/worktree for the deliberately red challenge, and keep the unchanged current guard as the green candidate. The reviewed sibling executable may be used for preview; never vendor or publish its private source or executable. No deployment, cloud work, version release or repository visibility change is in scope.
- **Owners and review:** Adam Burdett owns scope decisions and intended-use validation. The current Codex session is the sole writer and integration record maintainer for `goal/G-0002` until handoff. Medium risk; adopted policy requires independent native `contract-reviewer` and `adversarial-reviewer` assignments, N=2, at most R=3 rounds. Their initial findings must be independent; the writer decides readiness only after required evidence and blocker disposition. A later version-producing merge still needs distinct eligible human approval.
- **Validation method:** Adam explicitly delegated the final check to Codex. The [delegated result](evidence/72ad591/validation.md) records the red/green matrix and real preview on final S; no personal operation or release approval is attributed to Adam.
- **Canonical repository/ref/path:** public `burdettadam/park-commerce-workbench`, `goal/G-0002`, this file. Keep G-0001's ready ref and evidence retained as the dependency. This branch is stacked on G-0001; any later merge/rebase needs renewed affected checks and the applicable human gate.

## Acceptance criteria and independent expected results

All four criteria below are accepted from proposal-2 and passed on the [final evidence manifest](evidence/72ad591/manifest.json). Their independent expected results remain as accepted; the linked observed results, reviews and delegated validation support the ready decision.

### G-0002-AC01

In a disposable worktree, commit a deliberately restored old substring check on a local-only challenge branch and show that the existing mock tests fail for `0.2.01`, `10.2.0` and `0.2.0-rc.1`. Retain that exact challenge commit, failing assertions and exit status. Archived pre-fix evidence is historical context, not a new goal run or a new product defect.

Required artifact: `G-0002-A01` (`red-before.txt`) and its actual raw check.

### G-0002-AC02

On unchanged current source, exact reviewed version `0.2.0` reaches mock init, while incompatible, prefix/suffix look-alikes, empty and malformed outputs are rejected before init is invoked. Also run a preview with the reviewed real initializer on the current worktree; distinguish that observation from mock coverage.

Required artifact: `G-0002-A02` (`green-after.txt`) and its actual raw checks.

### G-0002-AC03

All existing tooling checks remain selected and passing on the intended host, including project-root protection, extra-argument rejection, preservation and forwarding of initializer exit code 3. Record any host-specific skipped check explicitly; do not replace assertions with snapshots.

Required artifact: `G-0002-A02` (`green-after.txt`) and the retained tooling suite output.

### G-0002-AC04

Obtain independent contract and adversarial reviews of fixed candidate S, including the challenge method and any actual product diff. A relevant source or test change invalidates stale review coverage. Record actual acceptance separately; no reviewer, tag or approval is fabricated.

Required artifact: `G-0002-A03` (`scope-and-review.md`) and the separate role reports.

The common [artifact contract](../../docs/roadmap/ARTIFACT-CONTRACT.md) and [unfilled manifest template](../../docs/roadmap/goals/G-0002/manifest.template.json) also apply. Freeze accepted-plan bytes, source S, environment/configuration, timed check outputs, review reports, validation, decision and handoff for the eventual candidate.

## Branch and progress

| Slice | Ref | Writer | Revision and included source | Evidence |
| --- | --- | --- | --- | --- |
| Activation and integration | `goal/G-0002` | Codex integration writer | `G-0002-active-1`, base `f4ca6dc`, final S `72ad591` | [bootstrap](work/bootstrap.md), [ready evidence](evidence/72ad591/manifest.json) |
| Disposable red challenge | Local-only `challenge/G-0002-old-substring` | Same writer | R `79560f4`, direct child of earlier green S0 `df6e9b9`; never in S | [red check](evidence/72ad591/red-before.txt) |

**Current state:** ready for G-0002's local guard-validation outcome at clean S `72ad59192e667e9e1048c5f3f65efc21e087005a`, true G-0001 base `f4ca6dc02d96a309763b1725b2eb3782cadd8f76`, accepted P `56ea84e4e0d0429e3c7069c7a48efd1d903b9bec`. R `79560f4fc6692584f414e6851122bbc2058609dc` is a separate local-only negative control derived from S0 `df6e9b9819cda0170bd9233387f72e16aa73573f`. The product wrapper and mock suite are unchanged from the ready G-0001 base. S adds narrow evidence-validator support for that distinct R identity under the previous review rules. The criteria were not changed.

**Verification and reviews:** R's old substring guard produced the three required look-alike failures and eight further rejection failures; its mock-suite exit was 1. Clean S passed 29 of 29 mock tooling checks, the reviewed real executable reported exact `git-agent 0.2.0`, and its real initializer preview exited 0 without writes. The evidence-validator test run passed 46 of 47 selected cases with zero failures; Windows denied the existing symlink fixture (one explicit skip), while all new negative-control cases passed. Two distinct native reviewer roles closed their round-2 source/control and report findings with zero remaining local blockers. [Contract](evidence/72ad591/reviews/contract.md), [adversarial](evidence/72ad591/reviews/adversarial.md), [provenance](evidence/72ad591/reviews/native-provenance.md) and [decision](evidence/72ad591/decision.md) retain their separate results. The final manifest passed locally and in a fresh public clone.

**Release and recovery:** S `72ad591`, first evidence E1 `4e0133e`, and final validated evidence E2 `ce305c441b24727eb242bf325f9c2470df56881a` were pushed. A fresh public HTTPS clone of E2 verified all 467 S source-manifest entries, 14 evidence files byte-for-byte, a passing manifest validator and no `.env`. [Handoff](evidence/72ad591/handoff.md) names local-only raw logs and R's nonrecoverable Git object; this branch record does not claim to back those up. No version-producing merge, human merge approval, artifact, tag or deployment is claimed. Keep the G-0001 prerequisite and G-0002 refs/evidence; resolve the distinct eligible human approval path before a future version-producing merge.
