# Test the goal-driven development process

## Question and boundaries

Can the adopted `work-goal`/`worktree-flow` process turn accepted intent into correct, reviewable, recoverable commerce changes without relying on conversation memory?

This is a practical engineering evaluation, not a controlled study proving that a model is faster or better than another. Do not invent baseline timings, costs, reviewer independence or deterministic behavior. Record actual observed model/tool settings where available; unavailable settings remain unknown.

## Experiment units

One **accepted goal candidate** is a unit of observation. One goal may have multiple candidates, branches, failed checks and review rounds. Preserve these distinctions. Count repeated executions as repeated runs, not distinct tests or new completed goals.

Start with G-0002's controlled version-guard challenge, then G-0003's handoff/branch exercise. The guard was fixed before the public snapshot, so this is a mutation/regression exercise rather than a newly repaired defect. Audit at least five different executed goals by G-0029: a bounded defect or regression challenge, framework/database integration, commerce behavior, artifact delivery, and platform recovery.

## What to record while working

| Measurement | Definition / denominator | Source |
| --- | --- | --- |
| Criterion coverage | Accepted criteria with current evidence / all accepted mandatory criteria | Plan and manifest |
| Review coverage | Required independent assignments with current-candidate reports / required assignments | Independent review reports |
| Review rounds | Reconciled review/fix cycles per goal; preserve first independent findings | Goal record |
| Escaped regressions | Reopened correctness failures attributable to a previously accepted candidate | Incident/defect records |
| Scope drift | Material changes attempted, approved, rejected and silently applied | Scope decisions/diffs |
| False readiness | Candidate reported ready despite missing/stale/failed mandatory evidence | Gate experiment |
| Handoff recovery | Time and steps to restore all indispensable refs/artifacts, plus unrecoverable items | Fresh-session drill |
| Retention coverage | Indispensable components retrieved with matching identity / all indispensable components | Checkpoint inventory |
| Effort/cost | Observed human/agent elapsed or active time, model charges and cloud expense; keep them separate | Recorded run/billing evidence |

All ratios should disclose the numerator and denominator. A zero denominator is not 100% success. Unknown measurements remain unknown. Comparison across different goal sizes is descriptive, not a causal productivity result.

## Controlled exercises and independent expected outcomes

| Exercise | Setup | Expected outcome | Goal |
| --- | --- | --- | --- |
| Known bounded regression challenge | Disposable copy uses the old substring guard with `0.2.01`, `10.2.0`, `0.2.0-rc.1` | Existing tests fail on the mutation and pass on the unchanged current guard; no new fix is claimed | G-0002 |
| Scope expansion | A writer proposes an unrelated capability during a bounded goal | Proposal/decision retained; unauthorized implementation is not silently merged | G-0003 |
| Stale review | Present report for candidate S1 after a relevant change creates S2 | Ready gate refuses stale coverage and requests applicable re-review | G-0003 / G-0029 |
| Missing evidence | Omit a required log, criterion or indispensable artifact | Remains blocked/needs-decision; no empty-report pass | G-0003 / G-0029 |
| Multiple worktrees | Separate writer branches with real conflicting port/volume risks | Declared owners, isolated resources and exact combined candidate | G-0003 |
| Interrupted session | Stop with local progress and resume without old chat context | Resume from retained records or explicitly report uncovered state | G-0003 / G-0029 |
| Known pipeline failure | Introduce one harmless failing assertion on a scoped test branch | Actual CI fails and promotion is denied | G-0020 |
| Recovery challenge | Restore into a fresh directory/environment from retained refs/artifacts | Digests and invariants reconcile; missing component is not “recovered” | G-0024 |
| Control-change proposal | Candidate proposes relaxing a required gate | Evaluated under previous accepted rules; not self-approved | G-0029 |

Do not introduce dangerous defects into live deployments. Use disposable fixtures/scoped branches and authorized synthetic datasets. Keep challenge expectations separate from the implementer/reviewer's first analysis where feasible, then retain the revealed oracle after the experiment.

## Proposed process success criteria

Every selected goal has traceable current evidence for every mandatory accepted criterion, the required independent reviews, actual validation/decision records and no unresolved valid blocker. All controlled stale/missing/failure challenges are handled as expected. Fresh-session recovery reports complete coverage only when retrieval has been demonstrated. No invented approvals or omitted mandatory tests.

One failed challenge is a process defect to record and repair; it cannot be concealed by averaging with successful goals. Maximum three review rounds remains the proposed escalation threshold, not a demand to manufacture findings or waste all three rounds.

## Decision after the experiment

G-0029 produces a report with observations, denominator/sample limits, failures and proposed changes. The owner records whether to retain, revise or restrict the workflow. Model/guide changes require applicable known-good/bad calibration; equivalent behavior is not assumed. The process may need improvement even when the product demo succeeds, and vice versa.
