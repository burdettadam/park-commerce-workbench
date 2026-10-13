# G-0001 fresh-session intended-use validation

Status: **delegated check completed; evidence retained**. The owner adopted the first-local-goal choices and then directed the current agent, “you do the check.” The available computer-use inventory exposed no IDE or browser surface, so the agent used a fresh native Codex CLI session from the repository root. Its [validation record](../evidence/8e2e18f/validation.md) reports the observed result and limits. This is delegated execution, not a claim that Adam personally operated the client or approved a release.

On a fresh IDE/Codex session opened at the `park-commerce-workbench` repository root, ask the session to identify:

1. Its loaded project instruction entry point (`AGENTS.md`) and the accepted owner/rule and active-goal locations it follows.
2. The `work-goal` and `worktree-flow` skills it can actually use.
3. Whether it can select the project custom `contract-reviewer` and `adversarial-reviewer` roles as native subagents, and how it confirms their instructions loaded.

The initial installed CLI and two isolated CLI trials at versions `0.162.1` and `0.163.0-alpha.5` found both skills but exposed no custom-role selector before the interactive client accepted project trust. After that trust step, a fresh installed CLI session selected both custom roles with actual `agent_type` values; the [discovery evidence](../evidence/8e2e18f/discovery.txt) retains the new observation. A role filename or a generic subagent task name alone would still be insufficient proof. Trust is machine state and must be rechecked after a clean restore.

Two native reviewers received fixed source S `8e2e18f04906cd9bdbd8344c74d3fd222ebd4e2e`, base `c8c46a2824f558e33c9e640fe6fc82dfdf705399`, the accepted G-0001 criteria and assigned guide sections, and current evidence. Their independent first reports have open evidence blockers, so further package work and reviewer disposition are required before readiness. Intended-use validation is separate from agent review and from later version-merge approval.
