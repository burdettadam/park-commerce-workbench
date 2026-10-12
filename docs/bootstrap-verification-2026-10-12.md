# Git-agent bootstrap verification

Date: 2026-10-12

Repository base: `4fef413ef69fefe44ae15e3b875973e774d44d68` (`main`)

Setup branch: `codex/git-agent-bootstrap-20261012`

Initializer: locally reviewed `git-agent` 0.2.0

## Observed checks

| Check | Result |
| --- | --- |
| Git for Windows / Git Bash | Git `2.53.0.windows.2`, Bash `5.2.37(1)-release` |
| Native `git agent --version` | `git-agent 0.2.0` from the recorded private checkout |
| `git agent init . --dry-run` | Planned 15 created files; reported no target or Git writes |
| `git agent init .` | Created 15 guidance, skill, role, and config files; did not stage or commit them |
| Second `git agent init . --dry-run` | Reported all 15 files unchanged |
| Skill format validation | `work-goal` and `worktree-flow` passed `quick_validate.py` |
| Codex TOML parse | `.codex/config.toml` and both reviewer role files parsed with Python `tomllib` |
| Codex prompt discovery | `codex debug prompt-input` included project `AGENTS.md` content and the `work-goal` and `worktree-flow` skill names; only boolean results were recorded |
| Tracked Git LFS objects | `git lfs ls-files` returned no files; the initializer's LFS warning appears to reflect available Git LFS configuration, not a tracked project object |

## Current limits

The checked-out GitHub repository contained only `.gitignore`, `README.md`, and two planning documents before initialization. No runnable application, tests, or product build commands were available to execute. The proposed governance has not been accepted by a designated project owner. Role discovery and skill execution in an actual Codex session, reviewer behavior, hosted checks, branch rules, and clean-machine recovery have not yet been demonstrated. Do not treat scaffold creation as product readiness.

The starter-code handoff must be located and reviewed before application files or test results can be added to this record.

The handoff was subsequently located in a local handoff directory and imported. See [import verification](import-verification-2026-10-12.md) for the later observations; the statements above describe the earlier bootstrap checkpoint.
