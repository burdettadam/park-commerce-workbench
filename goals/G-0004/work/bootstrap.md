# G-0004 activation and handoff

Integration writer: current Codex session on `goal/G-0004`; Adam Burdett owns scope, architecture, intended-use validation and repository administration. One branch writer is assigned at activation. Additional slices and worktrees remain unassigned.

The accepted historical input is unchanged proposal-2 `docs/roadmap/goals/G-0004/README.md`, SHA-256 `5a8819f52d13fd43f662f53b63439b5798b67af091c799d18e317cb7f6a9c40c`. The G-0003 ready prerequisite and prior stacked branches were promoted to public `main` at `d7ed805158bd6a239480ce7ae03312c139f2acc7`, the exact base of `goal/G-0004`. The accepted activation commit P is `5ec839681f563b4267a912e66f5969c1f1d9ac24`; the active plan file at P has SHA-256 `203b8b5e365c7626e68611fb49921d07e96e1a0b872c203e9294b43bae2b36df`. No G-0004 candidate source S exists yet.

The primary checkout is assigned to `goal/G-0004`. The retained G-0003 implementation/validation worktrees and G-0002 negative challenge worktree are historical evidence resources, not G-0004 writers or build results. The new branch begins clean from merged `main`; no G-0004 instance or dependency install was started at activation. Public checkpoints must contain only sanitized source and reports. Private `.env`, dependency caches and database volumes require separate local custody.

Activation P was pushed to `origin/goal/G-0004` and retrieved in a fresh public HTTPS clone at `.tmp/g0004-activation-retrieval`. Its HEAD matched exact P, the checkout was clean, the active plan SHA-256 matched, and `node scripts/validate-roadmap.mjs` passed in that clone. This report-only handoff update also requires push and remote identity verification; it does not change P or acceptance criteria.

Next: inspect installed JDK/Maven/Node/npm and native dependency inputs. Reproduce the actual optional Vaadin and Angular build gaps from clean inputs before choosing repairs. G-0004 acceptance checks, independent reviews, Adam's intended-use validation, release and deployment remain open.
