# G-0003 integration and recovery record

Integration writer: current Codex session on `goal/G-0003`. Accepted plan P `4a0d6a66a4d51eca57059a88875fb6efe7ab687e`, based on G-0002 ready `a0cc50650a34c1f131eef824d9a074bd0e945605`. This record coordinates branch facts; the separate writers' work records retain their own observations. No criterion is declared ready here.

## Included and excluded work

| Slice | Actual ref and owner | Included commit | Scope |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0003-implementation`, Codex integration writer | `ecc1439e9879dccafa7faea5375194c19ecb9310` | Private isolated Compose configuration and first stack's live observations in [implementation.md](implementation.md) |
| Independent test/documentation | `goal/G-0003-validation`, independent Codex writer | `7eed2b0142b6c4e5097963aee935c53da0a1857f` (includes correction `831b943` and first slice `e0332ad`) | Native validation procedure and second stack's live observations in [validation.md](validation.md) |
| Canonical integration | `goal/G-0003`, current Codex integration writer | merge commits `f8098e4` and `12a67c6`; fixed source S `4a3d26ea99be5deeac1c0afd94b9cc4443efd9c6` | Both branch heads are ancestors, with no product code change |

**Explicit nonincluded alternative:** use one shared Compose project and default ports/volumes for both worktrees to reduce the number of containers. This would make a second instance collide on ports or share mutable databases, defeating AC01 and the accepted isolation boundary. No branch or code for this alternative was integrated. A separate global orchestration daemon or lock database would exceed this goal's exclusions and was not proposed for implementation. The accepted four criteria are unchanged.

The proposal's `goal/G-0003/implementation` style could not be created because Git already has `goal/G-0003`; Git refs cannot be both a leaf and a directory. The two sibling slice refs above are the actual names. Both were pushed before integration; their exact remote heads were confirmed. These internal merges are not a version-producing merge into `main`.

## Live commerce-stack observation

Two clean worktrees had private ignored `.env` files with distinct random local credentials. Parsed Compose configuration gave projects `pcw-g0003-impl` and `pcw-g0003-validation`, disjoint sets of six loopback host ports and three project-prefixed named data volumes. The original default-port stack and other projects were left untouched.

Both writers started their own six-service stacks with native `docker compose --env-file .env up --build -d --wait --wait-timeout 240`, exit 0. Docker labels showed six healthy containers per project. Live inspection found all twelve published ports distinct, all mounted volume names distinct, and the three application health endpoints in each project returning HTTP 200. Mongo also created one unique anonymous mount per project; these were included in the live nonoverlap check, not mislabeled as declared named volumes.

The maintained `node scripts/demo-http.mjs` passed 43 live checks in each project. The implementation catalog created `lantern-a7a047a6`; the validation catalog created `lantern-fb95be11`. With both stacks running, four explicit GETs returned 200 with the exact ID on the owning catalog and 404 on the other catalog for each ID. The sanitized combined identity and cross-request outputs are retained in the [candidate evidence](../evidence/4a3d26e/branch-map.md). No real customer or payment data was used.

The integration writer stopped and restarted only the implementation project's booking service on 13 October 2026 UTC. Its stop and `up -d --wait` both exited 0; the validation booking service stayed healthy. Both synthetic catalog IDs remained available from their owning project afterward. This was a scoped service interruption, separate from the later authoring-session handoff.

## Recovery and pending gate

The authoring validation writer was interrupted while running. A fresh session received only local bundle/inventory paths and their expected digests, then recovered all 23 indispensable files, all three refs and 487 source-manifest entries from a new directory; [G-0003-A02](../evidence/4a3d26e/restore-drill.md) records the 146.584-second checkpoint-to-useful-work handoff and limits. Two independent native reviewers rejected the deliberately stale G-0002 review and absent A02 in E1; [G-0003-A03](../evidence/4a3d26e/workflow-observations.md) preserves the negative gate result and first findings. The root development stack was accidentally stopped during cleanup, immediately restarted and verified healthy; the two drill stacks were then stopped by explicit project name without deleting their named volumes.

Final native reviews, the filled manifest, actual intended-use validation and readiness decision remain pending. Keep the local bundle/fresh checkout, both drill projects' named volumes and G-0002 red worktree until retention dispositions are explicit.
