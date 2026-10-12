> **Local candidate available:** starter code now includes the G-0002 guard repair and a preparatory G-0008 pricing preview. See [changes and evidence](../changes/starter-code/README.md). These are not accepted/completed goals; original proposal criteria and dependencies below remain in force. The retained red commit supports the original regression exercise.

# Park Commerce Workbench — milestone roadmap

**10 milestones · 30 goal proposals · 120 acceptance criteria · 90 goal-specific artifact requirements**

Revision: `proposal-2`, updated 12 October 2026 for the public checkout. The owner has since advanced [G-0001](../../goals/G-0001/README.md) to ready and activated [G-0002](../../goals/G-0002/README.md) from their proposals. See [the starting audit](CURRENT-STATE-2026-10-12.md). **No milestone is claimed complete.** Earlier source, local runs and hosted CI are reusable evidence only when their inputs and limits match an accepted goal; they do not establish independent reviews or human acceptance.

This roadmap serves two purposes: build a credible Java/Spring commerce portfolio and test whether the existing goal-driven workflow produces correct, reviewable, recoverable work. Each goal has a bounded outcome, prerequisites, exclusions, expected results and named evidence. Existing source is a candidate to validate or extend, not a reason to regenerate everything.

## Milestones at a glance

| Milestone | Goals | Observable outcome | Evidence that closes the milestone |
| --- | --- | --- | --- |
| [M0 — Prove the goal-driven workflow](milestones/M0.md) | G-0001–G-0003 | Adopt the existing initializer guidance, run a controlled red/green regression challenge, and complete a recoverable multi-worktree handoff. | Reviewed adoption record; current-version guard challenge; branch-and-artifact recovery drill. |
| [M1 — Verify the full-stack baseline](milestones/M1.md) | G-0004–G-0006 | The source candidate actually builds and completes its original event purchase path against MongoDB/MySQL and both UIs. | Locked builds; database reports; live API results; browser walkthrough. |
| [M2 — Complete catalog and pricing administration](milestones/M2.md) | G-0007–G-0009 | An employee can draft, preview, publish, and explain versioned prices without rewriting existing quotes. | Publication audit; preview scenarios; deterministic pricing fixture pack. |
| [M3 — Complete a bounded commerce journey](milestones/M3.md) | G-0010–G-0012 | Admission plus one optional evening event can be purchased and cancelled with consistent inventory and idempotent outcomes. | Concurrency evidence; admission entitlement model; cart/cancellation state machine. |
| [M4 — Make both interfaces usable and safely deployable](milestones/M4.md) | G-0013–G-0015 | Employees and guests use their own complete flows, with resumable checkout and actual identity/access enforcement. | Vaadin and Angular browser tests; auth threat model; keyboard/accessibility evidence. |
| [M5 — Prove observable failure behavior](milestones/M5.md) | G-0016–G-0018 | Contracts, timeouts, process failures, SQL contention, and telemetry can be reproduced and investigated. | Contract suite; correlated traces/dashboards; failure and SQL reports. |
| [M6 — Deliver and promote identifiable artifacts](milestones/M6.md) | G-0019–G-0021 | Clean source builds into traceable images, passes actual CI, and rolls forward/back on local Kubernetes. | Image digests and SBOMs; Jenkins/GitHub run evidence; Kubernetes rollout and rollback. |
| [M7 — Operate a controlled private AWS demonstration](milestones/M7.md) | G-0022–G-0024 | Explicitly approved resources run the stack on EKS/Aurora and are restored and torn down from retained inputs. | Approved budget/design; private deployment; restore and teardown reports. |
| [M8 — Demonstrate the remaining named tools](milestones/M8.md) | G-0025–G-0027 | Splunk, AppDynamics, and a bounded Neo4j exercise produce their own executed evidence. | Saved SPL searches; traced business transaction; tested graph queries. |
| [M9 — Assess the workflow and prepare a private release candidate](milestones/M9.md) | G-0028–G-0030 | A repeatable benchmark, process assessment, and truthful portfolio package are reviewed against fixed artifacts. | Benchmark dataset; workflow experiment report; private release-candidate dossier. |

## Execution order and useful checkpoints

Start with M0, then M1. The initializer wrapper's former substring defect was repaired before this public snapshot. G-0002 now challenges the current regression tests with a disposable mutation and checks the unchanged guard; a real new defect would require its own observed fix. The [planning discovery](planning-discovery.txt) and [starter-code evidence](../changes/starter-code/README.md) are historical observations, not new goal runs or completed goals.

M1 verifies the preexisting candidate before new product work. M2/M3 complete the bounded commerce domain. M4 makes the UIs and identity boundary fit for later deployment. M5 proves diagnosability and failure handling. M6 proves delivery locally before M7 incurs authorized AWS spend.

M8 is a parallel evidence track: Splunk and AppDynamics start after G-0017, and Neo4j after G-0009. Unavailable access remains a blocker for **full role coverage**, but it does not prevent useful work on independent core goals. The exact graph is in each goal and [the dependency table](DEPENDENCIES.md); milestone order is a reading order, not a blanket restriction on all parallel work.

Proposed checkpoints are **workflow proof** (M0), **working local baseline** (M1), **complete authenticated observable local demo** (M0–M5), **private platform demo** (M0–M7), and **full role-complete private candidate** (M0–M9). These labels are not release tags. Partial checkpoints must disclose incomplete tooling/cloud evidence; do not relabel an omitted required tool as complete.

## Goal inventory

| Goal | Bounded outcome | Depends on |
| --- | --- | --- |
| [G-0001](goals/G-0001/README.md) | Adopt the actual initializer and accepted project rules | Human bootstrap |
| [G-0002](goals/G-0002/README.md) | Validate the existing version guard with a red/green challenge | G-0001 |
| [G-0003](goals/G-0003/README.md) | Recover a goal across two worktrees and a session interruption | G-0002 |
| [G-0004](goals/G-0004/README.md) | Resolve and lock the actual Java and UI builds | G-0003 |
| [G-0005](goals/G-0005/README.md) | Verify persistence against real MongoDB and MySQL | G-0004 |
| [G-0006](goals/G-0006/README.md) | Demonstrate the original live event purchase flow | G-0005 |
| [G-0007](goals/G-0007/README.md) | Add reviewable catalog drafts and publication history | G-0006 |
| [G-0008](goals/G-0008/README.md) | Preview unpublished prices with the production calculation path | G-0007 |
| [G-0009](goals/G-0009/README.md) | Freeze the price and quote contract with independent examples | G-0008 |
| [G-0010](goals/G-0010/README.md) | Prove capacity and idempotency under real contention | G-0009 |
| [G-0011](goals/G-0011/README.md) | Purchase admission as a separate entitlement type | G-0010 |
| [G-0012](goals/G-0012/README.md) | Buy and cancel one bounded admission/event cart | G-0011 |
| [G-0013](goals/G-0013/README.md) | Finish the Vaadin employee workbench | G-0012 |
| [G-0014](goals/G-0014/README.md) | Finish the Angular journey and uncertain-checkout recovery | G-0012 |
| [G-0015](goals/G-0015/README.md) | Replace demo tokens with a deployable identity boundary | G-0013, G-0014 |
| [G-0016](goals/G-0016/README.md) | Verify cross-service contracts and bounded dependency failures | G-0015 |
| [G-0017](goals/G-0017/README.md) | Make requests traceable through the observability stack | G-0016 |
| [G-0018](goals/G-0018/README.md) | Measure SQL contention and process-level recovery | G-0017 |
| [G-0019](goals/G-0019/README.md) | Build traceable distributable artifacts from clean source | G-0018 |
| [G-0020](goals/G-0020/README.md) | Execute the GitHub and Jenkins delivery checks | G-0019 |
| [G-0021](goals/G-0021/README.md) | Roll forward and back on local Kubernetes | G-0020 |
| [G-0022](goals/G-0022/README.md) | Approve a bounded private AWS experiment | G-0021 |
| [G-0023](goals/G-0023/README.md) | Deploy the approved private EKS/Aurora environment | G-0022 |
| [G-0024](goals/G-0024/README.md) | Restore the platform and verify cloud teardown | G-0023 |
| [G-0025](goals/G-0025/README.md) | Investigate commerce failures in Splunk | G-0017 |
| [G-0026](goals/G-0026/README.md) | Diagnose a Java business transaction in AppDynamics | G-0017 |
| [G-0027](goals/G-0027/README.md) | Demonstrate graph-data compatibility separately | G-0009 |
| [G-0028](goals/G-0028/README.md) | Benchmark the fixed release candidate honestly | G-0021 |
| [G-0029](goals/G-0029/README.md) | Evaluate goal-driven development using retained evidence | G-0003, G-0006, G-0012, G-0020, G-0024 |
| [G-0030](goals/G-0030/README.md) | Prepare a truthful private release-candidate dossier | G-0024, G-0025, G-0026, G-0027, G-0028, G-0029 |

## Definition of an acceptable goal

A goal can become `ready` only for a **fixed candidate** with accepted scope, independent expected results, selected nonempty checks, actual evidence for every criterion, current independent reviews, resolved blockers and retained/retrievable artifacts. Goal-specific criteria and common evidence requirements both apply. A passing script or a model's confidence is not an acceptance decision.

Human approval, a version-producing merge, artifact publication, deployment health and recovery coverage remain separate records. There is no universal “done” flag that collapses them. No deadline, automatic model run, cloud spend, public publication or merge is authorized by this roadmap.

## Artifacts and workflow test

Every goal proposal has its own `manifest.template.json`; these are unfilled output contracts and intentionally fail the evidence checker. Each goal requires **delivery artifacts** (working source/configuration/docs), **verification artifacts** (raw checks tied to S), and **decision/recovery artifacts** (independent reviews, validation, actual authority, handoff).

Use [ARTIFACT-CONTRACT.md](ARTIFACT-CONTRACT.md) for source S versus evidence commit E, retention, privacy and review semantics; use [PROCESS-EXPERIMENT.md](PROCESS-EXPERIMENT.md) to measure the workflow. The read-only validators check structure/hashes/declared coverage; they do not run agents, authenticate reviewer independence or grant approval.

```bash
node scripts/validate-roadmap.mjs
node --test scripts/validate-roadmap.test.mjs scripts/validate-evidence.test.mjs
```

After collecting real evidence for an adopted goal:

```bash
node scripts/validate-evidence.mjs goals/G-0002/evidence/CANDIDATE/manifest.json
```

The last path is a future example, not an existing run. Do not make templates pass by inserting invented reviewers/results.

## Adopt the existing public checkout without replacing git-agent

Use [START-FIRST-GOAL.md](START-FIRST-GOAL.md). Run a fresh preview of the reviewed initializer, review its output, and adopt the unresolved product decisions in `GOVERNANCE.md`/`DEVELOPMENT.md`. `PLANS.md` already links this roadmap. Keep proposals under `docs/roadmap/`; create an active `goals/G-xxxx/README.md` only when work is accepted. The active accepted copy is then the scope/state authority; the static catalog is navigation/dependency metadata.

The public checkout already contains generated `AGENTS.md`, `PLANS.md`, `.codex/` and `.agents/` guidance. There is no additional scheduler, skill or agent execution layer. Standard Git, OS, editor, Codex and maintained project validation commands remain the machinery.

## Current evidence boundary

The original ZIP reports 77 JDK core scenarios, 10 Node client-state tests and 11 tooling checks. Those observations are scoped to the original candidate and mostly use test-only in-memory stores/mocks; they do not establish Spring/UI/database/runtime success. The original manifest and ZIP digest are retained in [baseline provenance](baseline/provenance.json). See [original verification](../verification.md) and [proposal-1 delivery checks](DELIVERY-VERIFICATION.md) for historical limits; the [current-state audit](CURRENT-STATE-2026-10-12.md) names later evidence.

G-0001 is active at [its canonical record](../../goals/G-0001/README.md); the other roadmap goals remain proposals until adopted. The project repository is public and its original history is in a separate private archive. A public source snapshot and later Windows-fix branch were pushed; no release, cloud deployment, real payment, or licensed tool activation is claimed by this roadmap.

## Navigation

[First goal](START-FIRST-GOAL.md) · [Artifact contract](ARTIFACT-CONTRACT.md) · [Process experiment](PROCESS-EXPERIMENT.md) · [Requirement coverage](TRACEABILITY.md) · [Commands](COMMANDS.md) · [Dependencies](DEPENDENCIES.md) · [Templates](templates/README.md) · [Source alignment](SOURCE-ALIGNMENT.md)
