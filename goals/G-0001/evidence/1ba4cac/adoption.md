# G-0001 adoption observations and decisions

Source candidate S: `1ba4cace00f0d70ec596fccb85d84df0ba47ec00`. Accepted active-plan commit P: `c8c46a2824f558e33c9e640fe6fc82dfdf705399`. The worktree was clean before and after these commands. Work ran from the repository root on Windows using Git for Windows Bash 5.2.37 and Git 2.53.0.windows.2. The reviewed sibling `git-agent` source was `4c1bc45e44f7ce32e696dd338ddb3050fdb271d9`; its standalone executable SHA-256 was `1339c03a6798b55cc8aaac9416601f82802cba522f5d3a4083026ffc56cb1451`.

| Command | Exit | Observed result | Public transcript |
| --- | ---: | --- | --- |
| `git agent --version` with the reviewed executable temporarily on Git Bash `PATH` | 0 | Exact output `git-agent 0.2.0` | [version.txt](version.txt) |
| `bash scripts/init-agent.sh --preview` | 3 | Dry run; existing guidance preserved; one adoption README proposal reported | [preview.txt](preview.txt) |
| `bash scripts/init-agent.sh --apply` | 3 | Safe rerun; existing guidance preserved; one proposal already retained; no tracked guidance rewritten | [apply.txt](apply.txt) |
| `bash scripts/test-tooling.sh` | 0 | 29 mock-based checks passed, including look-alike versions and initializer exit-status forwarding | [tooling.txt](tooling.txt) |

The preview and apply transcripts redact only the local absolute target path. Unredacted local-only outputs are retained in ignored `.tmp/` files with SHA-256 digests `4aa87c7ae225a41dc1bfffbde44224ea7b7ab290a11c63e8bfb425e91fa672f1` (preview) and `279c3803ee8a753058e792deb15416fe2372598b9745d8b9c1a85dbf41c5644b` (apply). Local `.tmp/` files are not off-device retention.

The initializer proposed [a generic adoption README](../../../../docs/agent-init/proposals/0.2.0-bc235dc1d675/docs/agent-init/README.md) that adds a template digest line. The existing project-specific README remains authoritative pending a human disposition of the proposal. The initializer did not adopt governance or verify native Codex behavior.

## Human adoption decisions still required for AC03

| Choice | Concrete proposal for review | Current disposition |
| --- | --- | --- |
| Product commands | Use maintained `DEVELOPMENT.md` and `docs/START-HERE.md` commands for the first local goal; keep browser/Vaadin and clean-restore claims open until executed. | Proposed, not adopted |
| Scope/architecture/repository authority | Adam Burdett as named project owner; delegate a branch writer per active goal and record actual repository administration actions. | Proposed, not adopted |
| Independent review | Two distinct contract/adversarial assignments, at most three rounds, with current-candidate reports as in the proposal. | Selected as this goal's conservative gate; project policy adoption pending |
| Runtime isolation | One owned local Compose project for this first goal; do not claim two-instance isolation until G-0003 demonstrates it. | Proposed local limit, not adopted |
| Recovery | Push each indispensable work ref and sanitize retained evidence; set actual RPO/RTO and prove artifact retrieval before claiming recoverability. | Targets and owner commitment pending |
| Release control | No version-producing merge in this goal. Identify an eligible human review path before a later version-producing merge; GitHub currently requires zero approving reviews. | Human gate remains open |

No AC03 human decision is inferred from the user's request to start G-0001. That request accepts this goal's work scope. It does not by itself adopt project-wide controls or close `UNCONFIGURED` fields.
