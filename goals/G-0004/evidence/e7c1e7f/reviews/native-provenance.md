# G-0004 native review provenance

Read-only Codex CLI parent `01a13310-90ea-7bb2-90ce-787b2bbb7658` used the configured native `contract-reviewer` and `adversarial-reviewer` types in distinct child threads for round 1. Assignments were separate; the parent collected both first reports before reconciling. The independent raw rollouts are local-only because they include host paths. The public [first reports](round1-contract.md) and [adversarial first report](round1-adversarial.md) are sanitized direct extracts of the respective child final messages.

| Role | Native child thread | Local raw rollout SHA-256 | Result |
| --- | --- | --- | --- |
| `contract-reviewer` | `01a13310-cc34-79b2-a9ce-6101d29ffc3c` | `0f072b5754bda5bdb43f9afbdc30ad9ee1eff821059034fcf5b64e43082bf576` | One low provenance finding, no source defect; requested link correction. |
| `adversarial-reviewer` | `01a13310-dd0d-7910-8fae-4baaa63a6bfb` | `d5797e297b2ce182f60bd68478108bac079bd1d3b51fbfde2d02f29b0e7f990f` | Run provenance, clean-checkout proof and remote retrieval were readiness blockers at first review. |

Round 2 read-only parent `01a13318-a29a-7562-8612-6705a0883d32` used the same configured native roles on S/P/base and report E2 `5e6f9b2c99596e5d74f65ce957f72e7534b147b0`. It collected separate first findings before reconciliation. Both found zero open AC01–AC04 technical/evidence blockers in the inspected scope and identified final manifest/public retrieval as the remaining readiness work.

| Role | Native child thread | Local raw rollout SHA-256 | Retained assessment |
| --- | --- | --- | --- |
| `contract-reviewer` | `01a13318-e0b8-7b83-889e-2626cda2b830` | `bfaea69791b9f195e25abdc1be50e35f54ec2db749d3acf4ce4fbc1f3c427f8e` | [Round 2 contract](round2-contract.md) |
| `adversarial-reviewer` | `01a13318-f0f2-7123-bc43-bf6b38dbde4d` | `1999cef8751d7020acb9441b726d4fb62212a7c2f10169278354e2ae3e23d135` | [Round 2 adversarial](round2-adversarial.md) |

The parent local event logs and last responses are retained in ignored `.tmp`; their SHA-256 values can be recomputed locally. Reviewer child threads did not edit implementation or grant owner/release approval. Final report/manifest inspection and remote retrieval remain separate actions.
