# Final native reviewer provenance

Read-only parent thread `01a1336c-eab6-7312-8abd-af2f9f8fe25f` spawned two distinct configured native reviewer roles concurrently. Each child session metadata identifies that parent, depth 1, the role and the path. The children returned independent findings before reconciliation. Their own responses did not expose UUIDs; the integration writer verified the UUIDs in local native session metadata after the review.

| Native role | Child UUID | Path | Local-only raw rollout SHA-256 | Report |
| --- | --- | --- | --- | --- |
| `contract-reviewer` | `01a1336d-199f-7971-bf74-10f7ce5a27e6` | `/root/contract_round3` | `7fa2817423ea0531c277d59ce75ceca8a79edb5a30a1efc97f535973175c8bd0` | [Contract](round3-contract.md) |
| `adversarial-reviewer` | `01a1336d-2e85-7f53-bbf8-7d65bff98ac3` | `/root/adversarial_round3` | `31443b4cf898299ef688623c7673bf835207c03c9d3aa6c9ea58d93d8273a5c1` | [Adversarial](round3-adversarial.md) |

The local-only parent JSONL capture SHA-256 is `656cf57be677e60c7ea90c7c25ebaa48260ab021a6f648fd24ea81e94eea3ad1`. The wrapper exit was 1 because PowerShell classified a Codex CLI warning on stderr as `NativeCommandError`; the native turn completed and produced both child responses. The warning was unrelated to G-0005 acceptance checks.
