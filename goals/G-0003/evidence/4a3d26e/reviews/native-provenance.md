# G-0003 native review provenance

Read-only Codex CLI parent `01a132b5-a164-71d0-b061-eff758191b04` invoked the configured native `contract-reviewer` and `adversarial-reviewer` agent types in distinct child threads for round 1. Both were assigned the same fixed S, P, base, E and negative fixture independently. The parent collected each first report before reconciling; neither child's findings were given to the other before its report. Both rejected the stale review and missing required artifact as blockers. They share the parent `CODEX_SESSION_ID` in their output, so independence is supported by the distinct native child thread IDs and assignment sequence, not by a claim of distinct session environment variables.

| Role | Native child thread | Local raw rollout SHA-256 | Retained result |
| --- | --- | --- | --- |
| `contract-reviewer` | `01a132b5-eda3-7912-afde-92937931ea34` | `acfb9c9fca884ec8b2e525eeaf837539b10882d37f371132fb4277e58d0280bf` | [First contract findings](round1-contract.md) |
| `adversarial-reviewer` | `01a132b5-ff82-7942-a7a9-f9407c634c53` | `550e51115bf572e23d9c7e3f3cb65e9f0de6e7be6bd9ae4da01f9ec94146b7a6` | [First adversarial findings](round1-adversarial.md) |

Parent JSON event log SHA-256: `7b5d47a47a81604a42bf984ae9fc76745d461f87d519c850937f41b12e083d08`; last message digest: `1de62a4715ca0b9715c642cdf71c9dcc331213bff9c10c8d96e00051a84ce06b`. The raw logs remain local-only in the native Codex session store and ignored `.tmp` because they can contain host paths or unrelated private context. No native reviewer edited source or granted human approval.

## Round 2 technical assessment

Read-only native parent `01a132bf-74df-79d2-928e-13c52f2902f2` assigned both configured roles independently against fixed S/P/base and report E `e703fa45223e5065dbfeabafc0e4dd321cfa1e4c`. Native rollout files exposed distinct child thread IDs even though their own messages did not expose a session UUID. Both found no demonstrated AC01–AC04 blocker; the contract child identified draft manifest traceability to correct before finalization. Neither granted readiness or owner approval.

| Role | Native child rollout/thread | Local raw rollout SHA-256 | Retained assessment |
| --- | --- | --- | --- |
| `contract-reviewer` | `01a132bf-c1b3-7f23-b4be-0166d833ebaa` | `aa6bde81504f270bc6dba81eecd29f483732cce9655e88813a3bbca7fdd61eb3` | [Round 2 contract](round2-contract.md) |
| `adversarial-reviewer` | `01a132bf-d0d6-7b01-8560-526355d5debe` | `d27087ce072a3e8d746dc0bbe7f0218286392c8ebe1945203ee227b46fe05a93` | [Round 2 adversarial](round2-adversarial.md) |

Parent JSON event log SHA-256: `bccc4fbd673d475fa55a22c4fbe809233588d9af3200534e577059b9dddcda94`; final message digest `cc81d2ab5fd1372daa38b5fd44904cdb700a66007bd69800651f92ee9bdb5086`. Raw session logs remain local-only.

## Round 3 final technical and evidence review

Read-only native parent `01a132db-775f-70e1-a7fd-4fa9a11abb5f` assigned both configured roles separately against fixed S/P/base and report E `c0d73651809ecf77a41c019dda824e38d4cfd62e`. Their first findings were kept separate until both returned. Both passed their inspected scope with zero open AC01–AC04 blockers; neither granted whole-goal readiness or human release approval. The contract reviewer identified a catalog-check wording error (four GETs total, two per product), corrected in the preflight and final manifest without changing S or P. This was the third and final permitted review round.

| Role | Native child rollout/thread | Local raw rollout SHA-256 | Retained result |
| --- | --- | --- | --- |
| `contract-reviewer` | `01a132db-c0a3-7332-bec0-04952d137383` | `d7d201aa9b0e5cdc5ded9f6331b41d3f741e347074b6c7c5e2088f5996c3aec6` | [Final contract review](contract.md) |
| `adversarial-reviewer` | `01a132db-cf11-7af0-b444-cc3f688caea7` | `0286f3c687f10f0b882f3b18bdb10113aefb31012c4b5702d5716251a55d3171` | [Final adversarial review](adversarial.md) |

Parent JSON event log SHA-256: `24e2cafe7df45df1943535a092ef351e79dd70b52b31f720e6b61273d30ea6b8`. Raw session logs remain local-only because they can contain host paths and unrelated private context.
