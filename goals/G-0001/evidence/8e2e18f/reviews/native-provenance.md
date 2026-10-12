# Native role and independent review provenance

Fixed S: `8e2e18f04906cd9bdbd8344c74d3fd222ebd4e2e`; base P: `c8c46a2824f558e33c9e640fe6fc82dfdf705399`. Client: `codex-cli 0.162.0-alpha.17.2` on Windows. Parent review session `01a1323e-d0bd-7d72-9da5-1e97e45d4652` ran read-only from 22:32:20–22:33:45 UTC on 12 October 2026. Its local rollout SHA-256 is `c660e3a334082f13e85c81fb1b20838159160aeec6ed3f590910ff4545c437cd`. The recorded native `spawn_agent` tool calls used **different** `agent_type` values, not just task labels:

| Task label | Native `agent_type` | Child session | Child window UTC | Local raw rollout SHA-256 | Retained first report |
| --- | --- | --- | --- | --- | --- |
| `contract_review` | `contract-reviewer` | `01a1323f-01d1-7aa3-91f9-6da04a6fdafe` | 22:32:32–22:33:15 | `bc2fccad304306388d1f94d0e25651133a5dff0d9b57195b3bf3486113a53964` | [contract](round1-contract.md) |
| `adversarial_review` | `adversarial-reviewer` | `01a1323f-10b4-7aa2-93bb-8bcd3ca59e28` | 22:32:36–22:33:30 | `66f525d0c068ba5176a189e1d08611ca3065230c1a2bfe67dafa9c3417a6717e` | [adversarial](round1-adversarial.md) |

The independent first reports were extracted from each child session's final assistant message before reconciliation. Their only publication change was to replace absolute local repository links with relative links and normalize line endings. The raw extracted first reports remain in ignored `.tmp/`, with SHA-256 `5afde94837ac8fcf5097b68858b283dc6944bf5a2265bf314c6d986a31058d3e` (contract) and `93359d282436d1759e0032352ec4b24396b365fb9b4b0e91489f3a94f5ccb328` (adversarial). Neither reviewer edited S or saw the other's findings before filing its first report. Both found evidence/readiness blockers. Round 1 is **not a pass**; retain the blockers until the missing evidence is gathered and independently rechecked.

A separate fresh discovery session `01a13240-efb6-79e2-92a6-4d80ac900ef3` ran 22:34:39–22:34:57 UTC and again made native `spawn_agent` calls with `agent_type` `contract-reviewer` and `adversarial-reviewer`. Its sanitized final result is [native-discovery.txt](../native-discovery.txt). Its local rollout SHA-256 is `96a6d4af4fb07649b97e117c4d16cdda876f8ff40235b7e9322732e25e825835`; the redacted final-result text SHA-256 was `095f0e48f6ffacd86a4694eb625fe02e525f648cf2d1c262aece97542975d3fb`. The local rollouts may contain unrelated private prompt/context data and are not published wholesale.

Round 2 used read-only native parent session `01a13247-50bf-73e1-a4c2-a76937d3940e` on 12 October 2026. Its separate `spawn_agent` calls selected actual `agent_type` values `contract-reviewer` and `adversarial-reviewer`. The child session identities below were recovered from native parent `SubAgentActivity.agent_thread_id` records, which the parent's final text did not expose. The parent kept each first result from the other reviewer until both were complete.

| Role | Child session | Local raw rollout SHA-256 | Retained independent report |
| --- | --- | --- | --- |
| `contract-reviewer` | `01a13247-8598-7730-ab43-5f1de5795b5a` | `75d86d39d3c4eea76122884367a9ac809ee8705221c9671aab322891b70779db` | [round 2 contract](round2-contract.md) |
| `adversarial-reviewer` | `01a13247-9369-7090-aec7-e4ad3c46305c` | `ab28cf3b74fa8461a5e6233e601fdd13d51d6525f050f7071d69e84ea3a5f807` | [round 2 adversarial](round2-adversarial.md) |

Both found the prior native-discovery and delegated-validation blockers resolved. They found no substantive defect in S or AC01-AC04, but correctly withheld a present readiness pass while the manifest, final report/decision and final Git retrieval were pending. The reports remain unedited apart from UTF-8 file extraction from each child final message.

Round 3 used read-only native parent session `01a1324a-5f27-7dd1-8a4a-96ff0d85b329`. Native `SubAgentActivity` recorded distinct child sessions and `spawn_agent` selected `agent_type` `contract-reviewer` and `adversarial-reviewer`. Each result was retained before coordinator synthesis. The reviewers passed fixed S and the presently available AC01-AC04 evidence with zero substantive reviewer blockers, while leaving integration finalization to the writer.

| Role | Child session | Local raw rollout SHA-256 | Final independent report |
| --- | --- | --- | --- |
| `contract-reviewer` | `01a1324a-b0b9-7fe1-b019-bc72b877fcd9` | `bdb8036252855e619d2ac4c4eec6a1336915499b794ecd233a884f07e1c86206` | [contract](contract.md) |
| `adversarial-reviewer` | `01a1324a-bf13-7f01-81e2-0c24df802057` | `847057258494ab5bdfe4c9fd1ffcd052c18b1d2cf519ae605ac72862c4e7bb8e` | [adversarial](adversarial.md) |

The UTF-8 final reports preserve each child's final text, adding only a trailing line feed. Raw rollouts remain local; no human merge approval is inferred from these reviews.
