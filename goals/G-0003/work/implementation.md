# G-0003 implementation/configuration slice

Writer: Codex integration writer, assigned to `goal/G-0003-implementation` in worktree `.tmp/g0003-implementation`. Accepted plan revision `G-0003-active-1`, activation P `4a0d6a66a4d51eca57059a88875fb6efe7ab687e`; starting integration checkpoint `14656cf658c22df5560a37b26f2d6f1ea5f3be30`. This branch owns the first live Compose instance and the local isolation configuration. The independent validation worktree and its documents have a separate writer.

The proposal's suggested `goal/G-0003/implementation` ref could not be created because `goal/G-0003` already occupies that Git ref path. This sibling ref retains the same slice role without changing any accepted criterion.

## Scoped resources

`scripts/configure-local.sh` generated a private ignored `.env` in this worktree with random development credentials and a Windows account ACL. Only these nonsecret values were changed:

| Resource | Implementation worktree value |
| --- | --- |
| Compose project | `pcw-g0003-impl` |
| Catalog, pricing, booking host ports | `8181`, `8182`, `8183` |
| Pricing MySQL, booking MySQL, Mongo host ports | `3317`, `3318`, `27117` |
| Named volumes from Compose config | `pcw-g0003-impl_mongo-data`, `pcw-g0003-impl_pricing-data`, `pcw-g0003-impl_booking-data` |

Read-only `docker compose --env-file .env config --format json` showed those six ports and three named volumes. Its full JSON contains secrets and is not retained publicly. The validation worktree's proposed project/ports/volumes are distinct in that same configuration check. This is a configuration preflight, not runtime proof.

The existing `compose.yml` and `scripts/configure-local.sh` are the canonical implementation of host-port and project scoping. No second configuration generator or wrapper is needed. Use native Compose from this worktree with its private `.env`, record actual container IDs/health and endpoint identity, and keep synthetic mutations separate from the validation instance. Do not use `down -v` or stop any other project's containers.

## Progress

- Worktree and branch were created clean from integration checkpoint `14656cf`.
- Private `.env` exists and is ignored. No secret value was printed or committed.
- The twelve planned host ports were free at preflight; static Compose configuration for both worktrees had disjoint project names, ports and volume names.
- Live startup `docker compose --env-file .env up --build -d --wait --wait-timeout 240` ran from this worktree from `2026-10-13T00:31:32.222667Z` to `00:32:51.672840Z`, exit 0. Its ignored local raw output SHA-256 is `a54b853fa74659c5d4b3608507fbb3c56a99d942824893886f2b44681d8a5c7c`.
- Docker labels identified six `pcw-g0003-impl` containers, all healthy. The three declared project-prefixed data volumes were present. Mongo also mounted a unique anonymous volume, so the final cross-project inspection must compare that name as well. The private sanitized identity record under ignored `.tmp/` has SHA-256 `e4876148cdd2aa7dd8b7297d2b9bfd21ac6a22a1a0f0ccc7703ddb41d7c707e9`.
- Catalog, pricing and booking health endpoints each returned HTTP 200 on this project's ports. Maintained `node scripts/demo-http.mjs` ran from `2026-10-13T00:33:05.737658Z` to `00:33:07.763756Z`, exit 0 with 43 PASS lines and synthetic catalog ID `lantern-a7a047a6`; ignored local raw output SHA-256 `7d2571de109fefeec1515d7485c12073e0609e0f47dd74285489fd6a4e0dbb77`. This project's catalog returned HTTP 200 with that exact ID.
- The second instance, cross-instance data-isolation exercise, interrupted-session handoff and final checks are pending. No scope or success criterion changed.

Next: compare this live project's containers, ports and volumes with the validation instance; prove each synthetic catalog ID is visible only in its owner instance. Keep both projects' data volumes until recovery evidence is retained. Then reconcile an owned interruption and record exact branch/handoff identities before integration.
