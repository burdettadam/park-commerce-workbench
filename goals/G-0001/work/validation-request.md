# G-0001 fresh-session intended-use validation

Status: **requested from Adam Burdett; result pending**. The owner adopted the first-local-goal choices, but has not reported an intended-use validation result. Do not infer this result from project files or an agent's self-report.

On a fresh IDE/Codex session opened at the `park-commerce-workbench` repository root, ask the session to identify:

1. Its loaded project instruction entry point (`AGENTS.md`) and the accepted owner/rule and active-goal locations it follows.
2. The `work-goal` and `worktree-flow` skills it can actually use.
3. Whether it can select the project custom `contract-reviewer` and `adversarial-reviewer` roles as native subagents, and how it confirms their instructions loaded.

Report the actual output and any error, the client/extension version, and whether a new session was used. The installed CLI and two isolated CLI trials at versions `0.162.1` and `0.163.0-alpha.5` found both skills but exposed no custom-role selector. A role filename or a generic subagent task name is insufficient proof. If the IDE cannot invoke them either, retain AC02 open and treat the native role path as a tooling blocker; do not waive the two required independent reviews.

After native role selection works, give both reviewers fixed source S `8e2e18f04906cd9bdbd8344c74d3fd222ebd4e2e`, base `c8c46a2824f558e33c9e640fe6fc82dfdf705399`, the accepted G-0001 criteria and assigned guide sections, and current evidence. Record independent reports, findings and dispositions before readiness. Human intended-use validation is separate from agent review and from later version-merge approval.
