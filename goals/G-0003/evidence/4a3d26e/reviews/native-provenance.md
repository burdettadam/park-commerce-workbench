# G-0003 native review provenance

Read-only Codex CLI parent `01a132b5-a164-71d0-b061-eff758191b04` invoked the configured native `contract-reviewer` and `adversarial-reviewer` agent types in distinct child threads for round 1. Both were assigned the same fixed S, P, base, E and negative fixture independently. The parent collected each first report before reconciling; neither child's findings were given to the other before its report. Both rejected the stale review and missing required artifact as blockers. They share the parent `CODEX_SESSION_ID` in their output, so independence is supported by the distinct native child thread IDs and assignment sequence, not by a claim of distinct session environment variables.

| Role | Native child thread | Local raw rollout SHA-256 | Retained result |
| --- | --- | --- | --- |
| `contract-reviewer` | `01a132b5-eda3-7912-afde-92937931ea34` | `acfb9c9fca884ec8b2e525eeaf837539b10882d37f371132fb4277e58d0280bf` | [First contract findings](round1-contract.md) |
| `adversarial-reviewer` | `01a132b5-ff82-7942-a7a9-f9407c634c53` | `550e51115bf572e23d9c7e3f3cb65e9f0de6e7be6bd9ae4da01f9ec94146b7a6` | [First adversarial findings](round1-adversarial.md) |

Parent JSON event log SHA-256: `7b5d47a47a81604a42bf984ae9fc76745d461f87d519c850937f41b12e083d08`; last message digest: `1de62a4715ca0b9715c642cdf71c9dcc331213bff9c10c8d96e00051a84ce06b`. The raw logs remain local-only in the native Codex session store and ignored `.tmp` because they can contain host paths or unrelated private context. No native reviewer edited source or granted human approval.
