# G-0001 adopted-candidate initializer and decisions

Fixed source S: `8e2e18f04906cd9bdbd8344c74d3fd222ebd4e2e`; base P: `c8c46a2824f558e33c9e640fe6fc82dfdf705399`. The worktree was clean before and after the successful native checks. Work ran from the repository root on Windows using Git for Windows Bash 5.2.37 and Git 2.53.0.windows.2. The reviewed sibling `git-agent` source remains `4c1bc45e44f7ce32e696dd338ddb3050fdb271d9`.

With the reviewed executable directory temporarily first on `PATH`, Git Bash `command -v git-agent` resolved the sibling `git-agent/bin/git-agent`. The exact resolved file's SHA-256 was `1339c03a6798b55cc8aaac9416601f82802cba522f5d3a4083026ffc56cb1451`, matching the reviewed digest. `git agent --version` then printed exactly `git-agent 0.2.0` and exited 0 before either wrapper invocation. The absolute path is kept in ignored `.tmp/g0001-adopted-resolved-path.txt`; only its repository-relative tail is published here.

| Command from repository root | Exit | Observed result | Sanitized transcript |
| --- | ---: | --- | --- |
| `git agent --version` | 0 | Exact reviewed version `git-agent 0.2.0` | [version.txt](version.txt) |
| `C:\Program Files\Git\bin\bash.exe scripts/init-agent.sh --preview` | 0 | Existing files preserved; current product README now matches the proposed digest; no proposal/conflict remains | [preview.txt](preview.txt) |
| `C:\Program Files\Git\bin\bash.exe scripts/init-agent.sh --apply` | 0 | Safe rerun; no files staged, committed or pushed by initializer; worktree remained clean | [apply.txt](apply.txt) |
| `C:\Program Files\Git\bin\bash.exe scripts/test-tooling.sh` | 0 | 29 mock tooling checks passed, including look-alike/prerelease version rejection and exit forwarding | [tooling.txt](tooling.txt) |

An initial attempt resolved PowerShell's `bash` to Windows WSL `bash.exe`; it failed with `Bash/Service/0x8007274c` before reaching `git-agent`. It did not change tracked files. The successful commands above explicitly selected Git for Windows Bash. Unredacted preview and apply outputs stay in ignored `.tmp/`, with SHA-256 `6b8a0925e1a18c8966231218990e6a9be01ae0035ac0f1e101466fb5929f65e2` and `d8d64e477741b1b13f28e258bbe3b6890da8f4aba4da57b0cac51deea68f1717`. The redacted public transcripts replace only the absolute worktree path.

Adam Burdett answered “Adopt all proposed decisions” on 12 October 2026 for the seven groups in [the owner decision record](../../work/owner-decisions.md): pilot commands/support, named authority, N=2/R=3 reviews, one owned local instance with serialized shared-state tests, one-hour active-work RPO and one-working-day RTO targets, distinct eligible human review before a version-producing merge, and the product README with the initializer digest. `GOVERNANCE.md`, `DEVELOPMENT.md`, `ARCHITECTURE.md`, `PLANS.md` and `docs/agent-init/README.md` at S reflect those decisions. First-local-goal required `UNCONFIGURED` values have scoped decisions; later production lifecycle choices remain explicitly outside G-0001. Adoption is not proof that RPO/RTO was achieved, a reviewer role loaded, or a release was approved.

The [earlier candidate's adoption observations](../1ba4cac/adoption.md) remain historical evidence of the initial exit-3 README proposal and 29-check tooling run. The proposal file is retained for provenance, while the product-specific README is authoritative. No private initializer executable was copied into this repository.
