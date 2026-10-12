# G-0002 - Validate the existing version guard with a red/green challenge

Status: **active**. Milestone: M0. Dependency: [G-0001 ready](../G-0001/README.md) on its retained branch; G-0001 is not merged into `main`.

## Accepted work contract

- **Authority:** Adam Burdett's 12 October 2026 request to make the next goal active accepts the unchanged [G-0002 proposal-2](../../docs/roadmap/goals/G-0002/README.md) at `0a68d87d2312a1dd40f47a6ef2cd0bdaa938b80c`. This active record is revision `G-0002-active-1`, frozen at activation commit P `56ea84e4e0d0429e3c7069c7a48efd1d903b9bec`. The proposal is historical navigation; changing the four criteria requires a recorded owner decision.
- **Governing baseline:** `AGENTS.md`, `PLANS.md`, `GOVERNANCE.md`, `DEVELOPMENT.md`, `ARCHITECTURE.md`, the two project skills and configured reviewer roles at the G-0001 ready checkpoint `f4ca6dc02d96a309763b1725b2eb3782cadd8f76`. The new branch starts at that exact commit. Its G-0001 source/evidence is a prerequisite, not a fresh G-0002 check.
- **Intended user and outcome:** the project helper accepts only reviewed `git-agent 0.2.0` while preserving its existing root, argument, configuration and exit-code behavior. The goal tests the current guard; it changes product code only if the exercise reveals a real defect.
- **Conditions and limits:** local synthetic-data exercise on the documented Windows/Git Bash pilot host. Use a disposable, committed local-only mutation branch/worktree for the deliberately red challenge, and keep the unchanged current guard as the green candidate. The reviewed sibling executable may be used for preview; never vendor or publish its private source or executable. No deployment, cloud work, version release or repository visibility change is in scope.
- **Owners and review:** Adam Burdett owns scope decisions and intended-use validation. The current Codex session is the sole writer and integration record maintainer for `goal/G-0002` until handoff. Medium risk; adopted policy requires independent native `contract-reviewer` and `adversarial-reviewer` assignments, N=2, at most R=3 rounds. Their initial findings must be independent; the writer decides readiness only after required evidence and blocker disposition. A later version-producing merge still needs distinct eligible human approval.
- **Validation method:** show Adam the red/green matrix and real preview with exact source identities, then record his actual outcome or delegated check. No intended-use acceptance for G-0002 is recorded yet.
- **Canonical repository/ref/path:** public `burdettadam/park-commerce-workbench`, `goal/G-0002`, this file. Keep G-0001's ready ref and evidence retained as the dependency. This branch is stacked on G-0001; any later merge/rebase needs renewed affected checks and the applicable human gate.

## Acceptance criteria and independent expected results

All four criteria below are accepted from proposal-2. They are **open** until current G-0002 evidence, independent reviews and validation support them. Planned artifact names do not prove execution.

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
| Activation and integration | `goal/G-0002` | Current Codex session, one writer | `G-0002-active-1`, based on `f4ca6dc` | [bootstrap record](work/bootstrap.md) |
| Disposable red challenge | Local-only worktree/ref, to be recorded when created | Same writer unless reassigned | Deliberately weakened copy; never the green candidate | Pending |

**Current state:** active; the unchanged proposal has been accepted and the prerequisite is ready. No G-0002 acceptance check, red challenge, native review or intended-use validation is claimed yet. The accepted active plan is fixed at P `56ea84e4e0d0429e3c7069c7a48efd1d903b9bec`; this later handoff update does not change its criteria.

**Next action:** create an owned disposable challenge worktree, commit the old substring mutation there, capture the three required failures and exit status, then verify the unchanged green source and real initializer preview. Preserve the local-only red ref and any private raw output; publish only sanitized evidence. Follow with fixed-candidate reviews and actual validation before any ready decision.

**Release and recovery:** no version-producing merge, artifact, tag or deployment. Activation commit P was pushed to `origin/goal/G-0002` and retrieved by fresh public HTTPS clone: all 455 source-manifest entries matched, `.env` was absent, and the roadmap structure check passed. This report-only update needs its own push/retrieval confirmation. The planned red challenge is intentionally local-only until its disposition is recorded; it is not an off-device backup. Retain the G-0001 prerequisite ref and do not delete either branch's evidence.
