# Round 2 native reviewer provenance

Read-only Codex CLI parent thread `01a13368-eadc-7eb2-a4f4-6b8baefc57f9` used two distinct configured native roles concurrently. Native child session metadata identifies the same parent, depth 1, role and path for each. Their findings were delivered independently before parent reconciliation. None edited implementation or granted owner/release approval.

| Native role | Child thread | Native path | Local-only raw rollout SHA-256 | Retained finding |
| --- | --- | --- | --- | --- |
| `contract-reviewer` | `01a13369-1a62-79f2-90e8-0499cc1732bb` | `/root/contract_review` | `97d3828a108ae9564c1a35d19eddd5f054bde5daef3f90eda4f94f5a73dd05e7` | [Contract report](round2-contract.md) |
| `adversarial-reviewer` | `01a13369-3ae8-7840-94a1-3fd1fc9868a3` | `/root/adversarial_review` | `e6cd6dd0ca89e2f572472ab3fba4361f6ce8d192d6875c7b4f698b10689bd1d3` | [Adversarial report](round2-adversarial.md) |

The parent summary and captured JSONL are local-only because they contain host paths. The JSONL capture SHA-256 is `b498ae50675089efa65635438092a2abf2c8f8cb2422020a86c874547ade0a09`; the final-response capture SHA-256 is `e6a08b0a9e67a2c809dd8efe7534d14c5d6091955e153aa7d422761fd3e536cd`.
