# Native G-0005 review provenance

Read-only Codex CLI parent `01a13357-a14d-7411-a4c0-85bc28152d12` spawned two distinct configured native subagent types concurrently, collected independent first findings, and then reconciled them. The parent rollout records `spawn_agent` calls with `agent_type=contract-reviewer` and `agent_type=adversarial-reviewer`; child session metadata confirms each role, parent and task path. Raw rollouts are local-only because they include host paths. The public role reports are sanitized extracts of the child final responses, not parent-authored substitute reviews.

| Role | Native child thread | Task path | Local raw rollout SHA-256 | Result |
| --- | --- | --- | --- | --- |
| `contract-reviewer` | `01a13357-eb04-7ea0-8f05-a9fbc3ea2f5a` | `/root/contract_first` | `51c7fc8da015d3b8bf759a9e10e5e26b5d845e8ae5fdd3375dd4b2bc6a71e4c9` | [First report](round1-contract.md), no source/AC blocker. |
| `adversarial-reviewer` | `01a13358-0fec-7d82-95cc-5da50f96c4a1` | `/root/adversarial_first` | `6254657a1f9a1a0295859d49cc106c19121ff38ef200738138e47550778e4805` | [First report](round1-adversarial.md), one handoff record correction. |

Fixed S `1dd1ef28d3d5db6bcd4e47255c074ed6205c1627`, base `575d44ad21e6ed6fc0e72de4f6f27060a324bfcb`, plan P `30ffac8304f0ddf40a4845bb19e32393b1f0a516`, and published evidence E1 `a98fb15946f0c5abf6d9f8fb79a15465e346932b` were supplied. The reviewers did not edit implementation or grant owner/release approval. Later report/manifest changes require accurate final review coverage; first-round results alone do not mark the goal ready.
