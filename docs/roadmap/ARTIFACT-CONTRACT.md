# Artifact contract for an accepted goal

**Proposed project convention, not a native Codex configuration format.** Adopt it under the real generated governance. It defines outputs/evidence; it does not add a scheduler, state database, agent runner or automatic release authority.

## Three kinds of artifact

| Kind | Examples | What it establishes |
| --- | --- | --- |
| Delivery | Source change, migration, API schema, UI, image, dashboard/query definition, runbook | What was built or changed |
| Verification | Raw test output, request/response fixture, browser recording, trace export, SQL plan, load samples | What was actually exercised and observed |
| Decision and recovery | Accepted plan, independent review, human validation, scope decision, checkpoint/digest inventory | Why a candidate is acceptable and how another session retrieves it |

A screenshot cannot replace a required assertion, a successful core test cannot replace a database run, and a configuration file cannot replace an executed deployment. Empty or unavailable outputs are gaps. Planned filenames in goal proposals are not claims that those outputs already exist.

## Suggested retained layout — create only for actual work

```text
goals/G-0002/
  README.md                         Canonical accepted goal and live coordination
  work/implementation.md            That branch writer's progress
  work/validation.md                Another writer's bounded slice, when needed
  evidence/<candidate>/
    manifest.json                   Filled, actual identities and criterion coverage
    accepted-plan.md                Frozen accepted scope/criteria snapshot
    configuration.json              Redacted reconstructable inputs, not secret values
    environment.txt                 OS, runtime/tool versions, machine/resource limits
    red-before.txt                  Goal-specific evidence (example G-0002)
    green-after.txt
    scope-and-review.md
    checks/command.txt               Raw command output; retain multiple runs separately
    reviews/contract.md              Independent initial findings and final disposition
    reviews/adversarial.md           Separate reviewer assignment/session
    validation.md                   Actual intended-use validation and limitations
    decision.md                     Actual readiness/acceptance decision
    handoff.md                      Included refs, artifacts, ownership and retrieval
```

Use the goal's `manifest.template.json` as an output contract. Paths are relative to the candidate directory. Add more check-output artifacts rather than overwriting failed runs. Large videos/traces/images may be in an approved artifact store; retain a small local report that names the exact external digest, owner, access, retention and demonstrated retrieval. The local validator checks the report's bytes, **not** the remote object's authenticity or continued availability.

## Source S, plan P, evidence commit E and distributable D

Freeze source candidate **S**, relevant base, accepted plan **P**, toolchain/configuration and environment before selected acceptance checks. Record whether the working tree was clean. A dirty working tree is exploratory unless a separately accepted exact snapshot identity is used; this version of the readiness manifest requires a clean Git source commit.

The historical proposal ZIP had no Git history. Its original manifest and ZIP digest are retained under `baseline/`; the current public checkout has its own Git history. Use the actual current source commit and preserve the private archive's earlier source identity separately. Never invent a commit SHA to fill a required field.

Retain the accepted plan's exact bytes/digest as `accepted-plan.md`; subsequent progress/status updates do not retrospectively alter that contract. Changes to accepted criteria require a new decision/plan revision and renewed affected checks.

Evidence can be committed later as **E** describing **S**. E must not claim to be S, and a report does not need to contain its own future commit hash. Record report-only changes explicitly. Built artifact **D** has its own digest and source/configuration provenance. Merge **M**, release tag, deployment ID and observed health are separate actual facts.

## Evidence manifest semantics

Every criterion references at least one check and its required goal-specific artifact. All check and artifact references must exist, be nonempty and be tied to the same candidate. Automated acceptance checks must select tests and have no unexplained failures/skips. A deliberately red reproduction is declared `expected_failure`, with a nonzero expected exit and retained failure assertion; it does not count as the repaired green run. Its check and raw output may identify the recorded pre-fix base commit rather than S. A disposable negative-control commit can instead be declared as `candidate.negative_control` with its distinct `commit`, `derived_from_commit` and `purpose`; only a red check and its output may use that commit. Record and independently inspect the actual Git lineage: the structural validator checks the declared identities but does not authenticate Git ancestry. All ordinary acceptance checks and both reviews still cover S, and at least one actual current-candidate acceptance check is required. Never relabel an old red run as if it executed the repaired source. A manual validation is labeled `manual` and records a real observer/procedure/outcome, not invented test counts or exit codes.

Record actual start/end timestamps, command, working directory, observed result, environment and source. Keep raw output, including failed attempts; do not rerun until green and discard history. Environment absence, omitted suites, stale data, unsupported platforms and access failures remain visible.

The supplied JSON is deliberately unfilled (`claim: not_ready`, null identities and not_run outcomes). `node scripts/validate-evidence.mjs <manifest>` rejects it. That is expected, not a setup error. Fill fields only from observed evidence, then validate before requesting an actual readiness decision.

## Independent review and acceptance

Proposed policy: **two independent reviewers, at most three rounds**. One checks contract/expected results; one challenges assumptions, boundaries, concurrency, recovery and evidence. They do not write the implementation or manufacture human approval. Collect first findings independently before reconciliation; sequential sessions are acceptable when parallel capacity is unavailable, provided assignments remain independent.

Each report identifies candidate S, plan/guides, inspected scope, limitations, findings, severity, reproduction and disposition. An unresolved valid blocker prevents readiness regardless of majority opinion. A fix creates a new candidate and invalidates affected tests/reviews. Unresolved blockers after three rounds require `needs-decision`, not an automatic pass.

Record intended-use validation and the integration owner's actual readiness decision separately. Human approval is required before any version-producing merge. Public publication, paid services, cloud account changes and network exposure need their own explicit authority.

## State and approval are not one Boolean

After adoption, use the upstream goal states: `planned`, `active`, `blocked`, `paused`, `ready`, `needs-decision`, `cancelled`, `superseded`. These proposals are not active records. Ready means the accepted candidate passed the goal gate; it does not mean merged, released, deployed, healthy or remotely recoverable.

The supplied validators implement the proposal-2 catalog and the proposed two-reviewer/three-round baseline. A later accepted policy or material criterion change requires an explicit reviewed update to the coverage catalog/validator contract under the prior rules; the active accepted goal remains the authority. They are not a general automatic parser of native governance.

G-0007 has one owner-authorized exception to the round ceiling after its third round found a privacy blocker. A ready G-0007 manifest may report round four only with `review_policy_exception: {"max_round":4,"decision_artifact_id":"REVIEW-EXCEPTION","authority_id":"Adam Burdett"}` and a nonempty, digest-checked `REVIEW-EXCEPTION` artifact recording that decision and the preceding-baseline control assessment. Both final independent contract/adversarial reports must be from round four; an older report cannot be paired with a new one. The exception changes no criterion, check, privacy, or release rule. Other goals retain the three-round ceiling, and G-0007 round five is invalid. The checker verifies the literal authority declaration and artifact bytes, not the owner's identity or the decision's authority; reviewers inspect those claims.

The manifest checker verifies structure, declared candidate consistency, coverage, counts and local file digests. It does **not** authenticate reviewers/owners, verify Git objects against a remote, prove that commands really ran, read the semantics of every report, enforce hosted controls or grant an approval. Synthetic validator fixtures can pass structural checks; this is why independent review and actual authority remain necessary.

## Retention, privacy and recovery

Keep compact approved reports in Git and record external artifacts' owners, storage, access, expiry, renewal/retrieval procedure and digests. Before declaring a whole goal recoverable, retrieve every indispensable component on a fresh path/session and compare identities. Retain branch/artifact checkpoints first; update the coordinating goal record afterward.

This repository is public. Publish only sanitized evidence and source; keep sensitive local artifacts out of Git and label them `local_only` unless an approved private retained location and retrieval are verified. Local files are not automatically off-device backups. Never push secrets, `.env`, tokens, real customers, private initializer source, vendor credentials or unreviewed logs. Do not delete worktrees/volumes/backups before the required recovery checks and owner decisions.

## Milestone gate

A milestone identifies its exact combined candidate and included goal revisions, reruns affected combined checks and obtains its own actual acceptance record. Individual branch passes alone do not prove the integration. Use `templates/milestone-acceptance.md` and retain unfinished/blocked work without relabeling a partial checkpoint as full completion.
