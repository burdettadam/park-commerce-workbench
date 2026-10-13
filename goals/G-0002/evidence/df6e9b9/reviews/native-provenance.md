# G-0002 first native reviewer assignment provenance

Read-only parent Codex CLI session `01a13289-d35e-7ac1-85c8-c3e1653396e0` began 12 October 2026 at 23:54 UTC from the public project root. Its native `spawn_agent` calls selected actual `agent_type` values `contract-reviewer` and `adversarial-reviewer` in distinct child sessions. Both received fixed green S `df6e9b9819cda0170bd9233387f72e16aa73573f`, accepted P `56ea84e4e0d0429e3c7069c7a48efd1d903b9bec`, local-only red R `79560f4fc6692584f414e6851122bbc2058609dc`, and the same review guide/evidence. They did not edit files or see one another's first findings before reporting.

| Role | Child session | Local raw rollout SHA-256 | Retained first report |
| --- | --- | --- | --- |
| `contract-reviewer` | `01a1328a-035e-7aa0-8e63-3a1201a9d5ca` | `24c76702bc99f468a8b4748f827d889f79b0119e0308063b0b139ff2f1ad3031` | [contract](round1-contract.md) |
| `adversarial-reviewer` | `01a1328a-10c1-7430-a599-95730689e666` | `366ead339aaa76afbba0e9953a0fab94b15f7f7a841a52cbf92c55f563c8b714` after resume | [adversarial](round1-adversarial.md) |

The contract child's first final message was retained directly. The adversarial child sent its independent findings to the parent at 23:56:21 UTC, but its thread was aborted when the parent ended before the child emitted a final message. The same child was resumed through the parent and restated its original first findings as its final output; no new check or source edit occurred. Its raw final text SHA-256 is `584833c51312048721cf193841a9f1ca850cdf5a512b5faab67dafa76e5322b4`. The public report changes only absolute local links to repository-relative links and adds a terminal newline. Raw rollouts may include unrelated private prompt context and remain in the local Codex session store. Both first reports have open evidence/readiness findings; neither is counted as a final pass.
