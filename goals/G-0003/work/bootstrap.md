# G-0003 activation and writer handoff

Integration writer: current Codex session on `goal/G-0003`. Scope, architecture and repository administration owner: Adam Burdett. No implementation or independent test/documentation slice writer is assigned yet.

The accepted historical input is unchanged proposal-1 `docs/roadmap/goals/G-0003/README.md`, last changed at `e9b3fa9acf6d8c5dc2a0bde9c0c40f1913ca5c7d`. The G-0002 ready prerequisite is `a0cc50650a34c1f131eef824d9a074bd0e945605`, the exact starting commit of `goal/G-0003`. The accepted activation commit P will be recorded in a report-only update after the commit exists. G-0001 and G-0002 remain unmerged stacked prerequisites.

Existing Git worktrees at activation: primary integration checkout on the newly created `goal/G-0003`, and a clean local-only `challenge/G-0002-old-substring` worktree retained for G-0002. There is no G-0003 slice worktree yet. `compose.yml` exposes six host ports through environment variables and declares three named data volumes; distinct Compose project names should namespace the volumes, but that is only a configuration hypothesis until two real instances are checked. Each slice will need private `.env` and scoped process/container records. No product instance was started by activation.

The active source and goal state are not yet G-0003 acceptance evidence. Next: checkpoint this accepted plan; then assign two slice writers/refs, choose nonconflicting ports and run the controlled two-worktree drill. Use a local bundle/ZIP or verified-private remote for reconstruction, with a fresh directory/session and measured digest retrieval. Preserve the G-0002 red worktree and all unfinished work. Public checkpoints contain only sanitized reports.

Activation push, exact P and fresh retrieval: pending until the activation commit exists. No G-0003 check, independent review, intended-use acceptance, release or deployment is claimed.
