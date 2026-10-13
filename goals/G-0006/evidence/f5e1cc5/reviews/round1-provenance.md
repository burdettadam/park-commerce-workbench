# G-0006 native first-review provenance

Read-only Codex CLI parent `01a134e0-527e-7812-8936-da1cc365ff3c` assigned two distinct configured native roles against fixed source S `f5e1cc5e576c45a785199374031e5853a4dc7606` and published evidence E1 `4900d4a2c36189746f95567a71e48359c0b2bb04`. The child session metadata authenticates role, task path and parent. The two final responses were independently completed before reconciliation. Raw rollouts stay local because they contain host paths; public reports preserve each final response with only local path links normalized where noted.

| Role | Native child thread | Task path | Raw rollout SHA-256 | Report |
| --- | --- | --- | --- | --- |
| `contract-reviewer` | `01a134e0-ad3e-73a0-8004-086e53139028` | `/root/g0006_contract_round1` | `b38a33028c11b881d518f0777cb6664e251f2a3d8c31002249df42e97618b25b` | [Contract](round1-contract.md) |
| `adversarial-reviewer` | `01a134e0-deb2-7c00-80fa-a18500302246` | `/root/g0006_adversarial_round1` | `12e4ec55a58e99ed70cc02f19d2e12625d1b435c77f94ad32c1e793369859a14` | [Adversarial](round1-adversarial.md) |

Neither reviewer changed S, ran mutable tests, inspected demo credentials, validated intended use, or granted release approval. Both found the same post-publication UI evidence gap and the expected missing final readiness records at E1. Those findings are retained independently; [disposition](round1-disposition.md) records subsequent work.
