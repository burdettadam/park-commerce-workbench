# G-0005 activation and handoff

Integration writer: current Codex session on `goal/G-0005`; Adam Burdett owns scope, architecture, intended-use validation and repository administration. One branch writer is assigned. The retained earlier goal branches and worktrees are historical evidence resources, not G-0005 writers or test results.

The accepted historical input is unchanged proposal-2 `docs/roadmap/goals/G-0005/README.md`, SHA-256 `12ae39ed3093f40ae0a37895380b1dccf5391344064bc635fc974abb90683b09`. G-0004 source/evidence was promoted to public `main` by PR #5 at `c873cfe1b8dd93732ef0d63f1b890b9774174934`; PR #6 recorded that merge. The exact G-0005 base is `575d44ad21e6ed6fc0e72de4f6f27060a324bfcb`.

The accepted activation commit P is `30ffac8304f0ddf40a4845bb19e32393b1f0a516`; `goals/G-0005/README.md` at P has SHA-256 `98888b8e7e345b959de19769d6788fd639fe215564b58ec8e033027b23424bc7`. No G-0005 candidate source S exists yet. No G-0005 database instance, migration or integration command has run. Existing G-0004 CI and integration results are prerequisite context, not G-0005 acceptance evidence.

The primary checkout is assigned to `goal/G-0005`. Public checkpoints may contain sanitized source and reports only. Private `.env`, dependency caches and database volumes require separate local custody. Activation P was pushed to `origin/goal/G-0005` and retrieved in a fresh public HTTPS clone at `.tmp/g0005-activation-retrieval`. Its HEAD matched exact P, the checkout was clean, the active plan SHA-256 matched, and `node scripts/validate-roadmap.mjs` passed in that clone. This report-only handoff update also requires push and remote identity verification; it does not change P or acceptance criteria.

Next: inspect existing database suites, adapters and migrations; establish nonempty selected suite counts and baseline behavior against disposable MongoDB/MySQL through `bash scripts/verify.sh --integration`. No independent G-0005 review, intended-use validation, release or deployment is complete.
