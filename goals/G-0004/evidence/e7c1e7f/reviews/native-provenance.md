# G-0004 native review provenance

Read-only Codex CLI parent `01a13310-90ea-7bb2-90ce-787b2bbb7658` used the configured native `contract-reviewer` and `adversarial-reviewer` types in distinct child threads for round 1. Assignments were separate; the parent collected both first reports before reconciling. The independent raw rollouts are local-only because they include host paths. The public [first reports](round1-contract.md) and [adversarial first report](round1-adversarial.md) are sanitized direct extracts of the respective child final messages.

| Role | Native child thread | Local raw rollout SHA-256 | Result |
| --- | --- | --- | --- |
| `contract-reviewer` | `01a13310-cc34-79b2-a9ce-6101d29ffc3c` | `0f072b5754bda5bdb43f9afbdc30ad9ee1eff821059034fcf5b64e43082bf576` | One low provenance finding, no source defect; requested link correction. |
| `adversarial-reviewer` | `01a13310-dd0d-7910-8fae-4baaa63a6bfb` | `d5797e297b2ce182f60bd68478108bac079bd1d3b51fbfde2d02f29b0e7f990f` | Run provenance, clean-checkout proof and remote retrieval were readiness blockers at first review. |

The parent local event log and last response are retained in ignored `.tmp`; their SHA-256 values can be recomputed locally. Reviewer child threads did not edit implementation or grant owner/release approval. Later review rounds and their identities are recorded below when performed.
