> **Historical handoff:** This document records the original source handoff. The public repository is a sanitized source snapshot with its own clean history. See [development bootstrap](development-bootstrap.md) and [import verification](import-verification-2026-10-12.md) for historical checks.

> **Latest local candidate:** [pricing-contract handoff](changes/pricing-contract/README.md) advances G-0009. It does not adopt roadmap prerequisites or mark a goal ready. Retain real framework/DB and independent-review gaps when initializing the agent workflow.

> **Current candidate:** see [checkout-recovery handoff](changes/checkout-recovery/README.md) and its verification record. Prior counts and observations below are historical unless explicitly updated.

# Workstation agent workflow handoff

## What this delivery did

The source and executable domain tests were created locally. The established remote README/architecture goals were used as the baseline. No remote settings, branch, file, tag, or release were changed by this delivery. No Codex roles were executed and no independent reviewer approval is claimed.

Native agent paths are intentionally absent: `AGENTS.md`, `GOVERNANCE.md`, `DEVELOPMENT.md`, `ARCHITECTURE.md`, `PLANS.md`, `.codex/`, `.agents/`, and `docs/agent-init/`. This lets your actual initializer generate its own reviewed templates. The project-specific documents under `docs/project/` supply the product decisions to adopt afterward, rather than impersonating initializer output.

## Optional initializer

The project-local guidance has already been generated. The application builds and runs without the external initializer. If you have a reviewed `git-agent` 0.2.0 installation, the guarded wrapper below can preview its output before any local update. Obtain and review that tool independently.

## Apply locally

From your product's Git worktree root:

```bash
git agent --version
bash scripts/init-agent.sh --preview
bash scripts/init-agent.sh --apply
git status --short
```

The wrapper requires version 0.2.0, refuses parent-worktree initialization, and forwards the initializer's exit status. It does not stage or commit files. A different version needs an explicit compatibility review; update this handoff before using it.

Review `docs/agent-init/README.md`, proposed guidance, recognized existing content, model/tool choices, and any `UNCONFIGURED` commands. Exit 0 can still contain adoption gaps. Exit 3 means proposals require an adoption decision. Rerunning does not approve anything.

## Product-specific choices to adopt

| Generated responsibility | Adopt from this ZIP |
| --- | --- |
| Product architecture | `docs/project/architecture.md`, `docs/project/decisions.md` |
| Accepted functional scope | `docs/project/requirements.md`, `docs/project/api-contracts.md` |
| Shared commands | `docs/START-HERE.md`, scripts listed below |
| Security and isolation | `docs/project/security.md` |
| Proposed roadmap/goals | `docs/roadmap/README.md`, per-goal proposals and `manifest.template.json`; active accepted goal becomes canonical |
| Evidence and incomplete checks | `docs/verification.md`, `docs/evidence/` |
| Requirement-to-stack mapping | `docs/project/sources.md` and root README |

Commands that actually exist:

```bash
bash scripts/test-core.sh --demo
node --test storefront/src/app/checkout-state.test.mjs
bash scripts/verify.sh --java
bash scripts/verify.sh --integration
bash scripts/configure-local.sh
docker compose config --quiet
docker compose up --build -d --wait
node scripts/demo-http.mjs
docker compose down
```

Do not mark integration or UI commands as passing from the core-only results. Each goal's evidence must record the exact source revision, environment, command, observed outcome, and limitations.

## Review and release policy

Adopt the upstream contract-reviewer and adversarial-reviewer workflow through your actual initializer. The prior proposal uses two independent reviewers and no more than three rounds; this ZIP does not claim those reviews occurred. A human must approve version-producing merges and releases. Native model configuration must remain supported and configurable, with model changes evaluated rather than presumed equivalent.

No GitHub ruleset, branch protection, environment approval, signing policy, or release gate has been configured. Candidate CI source is not enforcement. Verify hosted controls separately before release work.

## Roadmap adoption and first bounded work-goal

Adopt [the milestone roadmap](roadmap/README.md) as planning input, not completed work. Link it from the generated `PLANS.md`. Create a canonical active goal record only when its authority/scope are actually accepted; keep static proposals under `docs/roadmap/`.

The starter-code candidate repairs the helper's substring match and requires the exact `git-agent 0.2.0` output. Review [the starter-code evidence](changes/starter-code/README.md), then decide how to adopt G-0002 under the actual governance. Offline mock tests are not native initializer validation. G-0003 still tests multi-worktree handoff/recovery. The early G-0008 implementation is a candidate only; prerequisite milestones are not declared passed.

Then **M1/G-0004–G-0006** compiles and validates the existing candidate against real databases and both UI frameworks before scope expansion. The [first-goal instructions](roadmap/START-FIRST-GOAL.md) include the native skill handoff. This planning update did not run the initializer, independent reviewers or native Codex discovery.


## Additional local candidate: catalog publication

The latest source includes immutable draft/publication/history work toward G-0007.
Read [the bounded plan](changes/catalog-publication/PLAN.md),
[verification and limitations](changes/catalog-publication/VERIFICATION.md), and
[the concurrency/cursor contract](project/catalog-publication.md).
No roadmap prerequisite, native agent adoption, review independence, or release approval
was fabricated; integrate only after resolving the actual acceptance decisions.
