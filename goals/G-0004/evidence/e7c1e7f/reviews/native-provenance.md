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

Round 3 read-only parent `01a1331f-d323-7591-9c61-ca98385fd3ef` assigned the same two native roles independently against fixed S/P/base and published preflight E3 `dcf3b2c946c2b3382939d7a9e84074175934e095`. The adversarial child returned a final report. The contract child's first turn ended `turn_aborted` before its final report, so that message was **not** counted. The parent was resumed and explicitly continued the same contract child thread, which then returned its own complete final report. No fourth review round or substitute parent finding was counted. Both final child reports found zero open AC01–AC04 blockers. The three intentional preflight readiness fields and final remote retrieval remain integration-writer actions.

| Role | Native child thread | Local raw rollout SHA-256 | Final result |
| --- | --- | --- | --- |
| `contract-reviewer` | `01a13320-0e35-7600-8c62-2767a6e538e1` | `f222972d8712bb088b3f560863af130f5f2b2b70f36aaa0a0f37fa6507601eee` | [Final contract review](contract.md), zero open blockers after same-thread continuation |
| `adversarial-reviewer` | `01a13320-2091-71d1-8147-e081a46d2add` | `42415dcc5d4a1e11d01dcffbc38d0940da090a67c9ee201851a780b96df0b27f` | [Final adversarial review](adversarial.md), zero open blockers |

The parent local event logs and last responses are retained in ignored `.tmp`; their SHA-256 values can be recomputed locally. Reviewer child threads did not edit implementation or grant owner/release approval. Publication and remote retrieval of the final filled manifest remain separate actions.
