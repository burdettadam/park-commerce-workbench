> **Current public checkout:** the version guard, Windows local setup, Angular lockfile, and hosted CI already exist. The original red commit is historical evidence in the private archive, not a new goal run. See [the current-state audit](CURRENT-STATE-2026-10-12.md) before starting a trial.

> **Active goal:** [G-0001](../../goals/G-0001/README.md) is now the canonical scope and progress record. The proposal card below remains a historical input; follow the active record for actual status.

# Start the roadmap locally

## Use the existing checkout

The project is already a public GitHub repository with imported source and project-local agent guidance. Use its current clone and a task branch. Do not overlay the historical ZIP or replay its patch. The private archive retains earlier history; neither it nor the initializer executable belongs in this public source tree.

## Inspect the plan package

From the extracted project root, with the project's Node runtime available:

```bash
node scripts/validate-roadmap.mjs
node --test scripts/validate-roadmap.test.mjs scripts/validate-evidence.test.mjs
```

These check the proposed roadmap/evidence-contract structure using offline fixtures. They do not adopt a plan, execute a native agent or pass a product milestone.

## Work G-0001 under its active record

Read the [active G-0001 contract](../../goals/G-0001/README.md), `GOVERNANCE.md`, and `docs/development-bootstrap.md`. On this Windows host, the reviewed local `../git-agent/bin/git-agent` reports `git-agent 0.2.0`, but `git agent` is not on the normal shell `PATH`. Install the reviewed command following the sibling repository's instructions or temporarily add its `bin` directory to Git Bash `PATH` for this session. Then verify:

```bash
git agent --version
```

The current helper rejects look-alike versions; its old substring defect was repaired before this public snapshot. Verify the executable identity and exact output anyway. A real dry run on 12 October 2026 reported template `0.2.0`, preserved existing guidance, and listed adoption gaps. Repeat the preview against the trial source revision.

After that manual check, from the intended Git worktree root:

```bash
bash scripts/init-agent.sh --preview
git status --short
```

Review actual generated guidance and adoption gaps. If rerunning `--apply`, inspect the preview file plan first: this checkout already has guidance, and rerun proposals are not automatic adoption. Native discovery and human acceptance must be real observations. `PLANS.md` links the active G-0001 record; project-wide governing policy adoption is still one of that goal's open human decisions.

## First bounded post-adoption work-goal: G-0002

Once G-0001 is genuinely accepted, create an active goal record from `docs/roadmap/goals/G-0002/README.md`, record accepted authority/plan commit/owners, and select a small work slice. Do not mark it active merely by copying a template.

Use this instruction with the existing native skill after it is discovered:

```text
Use $work-goal to work on the accepted G-0002 contract in goals/G-0002/README.md.
Read the accepted governance and plan revision first. Keep the scope to the
existing initializer helper's exact-version guard and its regression tests.
In a disposable worktree, commit the old substring defect on a local-only
challenge branch and observe the expected red assertions; then verify green
on the unchanged current source. Do not
claim the historical repair as a new code change. Retain raw outputs and a fixed candidate.
Obtain independent contract and adversarial reviews under the adopted policy.
Do not invent reviewers, approvals or test results. Publish a sanitized trial
branch only under actual accepted authority. Do not tag, deploy, change
visibility, add a new agent command layer or expand scope.
Report actual readiness, blockers, retained artifacts and the next action.
```

`$work-goal` above is a native skill reference, not a shell command. The actual accepted goal/authority must exist before execution. G-0001 itself is a bootstrap adoption step with a human decision; it cannot use unadopted rules to approve itself.

## Capture, validate and hand off

Create a candidate evidence directory only when work occurs. Copy its `manifest.template.json` as a starting contract, retain real outputs, populate actual identities/results and validate them:

```bash
node scripts/validate-evidence.mjs goals/G-0002/evidence/CANDIDATE/manifest.json
```

The example directory does not exist yet. Templates fail deliberately. A structural pass is not authentication of the reports or actual human approval. Retain failed attempts and reviewer limitations, and follow the component-before-coordinator checkpoint order.

Then perform G-0003's recovery drill. M1/G-0004 starts full Spring/UI build validation after the workflow proof; it does not regenerate the entire existing application. Independent future tracks can proceed when their precise prerequisites and authority are satisfied.
