> **Local candidate available:** starter code now includes the G-0002 guard repair and a preparatory G-0008 pricing preview. See [changes and evidence](../changes/starter-code/README.md). These are not accepted/completed goals; original proposal criteria and dependencies below remain in force. The retained red commit supports the original regression exercise.

# Start the roadmap locally

## Import without changing the remote

The updated ZIP retains the previous source and adds planning/validation artifacts. It contains no `.git`, native initializer output, private executable, usable secrets or installed dependencies. Overlay into your local clone, review the diff, and keep the remote untouched until you verify the repository is private. This guide does not change GitHub visibility.

## Inspect the plan package

From the extracted project root, with the project's Node runtime available:

```bash
node scripts/validate-roadmap.mjs
node --test scripts/validate-roadmap.test.mjs scripts/validate-evidence.test.mjs
```

These check the proposed roadmap/evidence-contract structure using offline fixtures. They do not adopt a plan, execute a native agent or pass a product milestone.

## Bootstrap G-0001 with actual authority

Read `goals/G-0001/README.md` under this roadmap and `docs/agent-handoff.md`. Before using the existing wrapper, manually confirm the reviewed private initializer's **exact** version:

```bash
git agent --version
```

The current helper has a documented substring-version defect: look-alikes can pass its check. Do not rely on that guard until G-0002 is fixed. Review the exact output and source identity yourself during bootstrap; do not test unknown versions against your real project.

After that manual check, from the intended Git worktree root:

```bash
bash scripts/init-agent.sh --preview
bash scripts/init-agent.sh --apply
git status --short
```

Review actual generated guidance and adoption gaps. Native discovery and human acceptance must be real observations. Add a reference to `docs/roadmap/README.md` in the adopted `PLANS.md` and use the actual governance for canonical goals and branch records. Do not blindly replace initializer templates with files from this roadmap.

## First bounded post-adoption work-goal: G-0002

Once G-0001 is genuinely accepted, create an active goal record from `docs/roadmap/goals/G-0002/README.md`, record accepted authority/plan commit/owners, and select a small work slice. Do not mark it active merely by copying a template.

Use this instruction with the existing native skill after it is discovered:

```text
Use $work-goal to work on the accepted G-0002 contract in goals/G-0002/README.md.
Read the accepted governance and plan revision first. Keep the scope to the
initializer helper's exact-version guard and its mock-based regression tests.
Reproduce the known failure before fixing it. Preserve existing behavior and
exit-status forwarding. Retain raw red/green outputs and a fixed source candidate.
Obtain independent contract and adversarial reviews under the adopted policy.
Do not invent reviewers, approvals or test results. Do not push, tag, deploy,
change visibility, add a new agent command layer or expand scope.
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
