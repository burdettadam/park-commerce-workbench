# G-0003-A03: observed workflow gate challenge

This is one controlled development drill for fixed source S `4a3d26ea99be5deeac1c0afd94b9cc4443efd9c6` and accepted plan P `4a0d6a66a4d51eca57059a88875fb6efe7ab687e`. It is not a statistical model evaluation.

## Deliberate negative case and actual review outcome

At report commit E1 `9cf8b4ac676a033f9ae7d0ae55febd570a7d3a82`, the candidate package deliberately lacked G-0003-A02 `restore-drill.md`. The ignored local fixture `.tmp/g0003-negative-review-fixture` offered a G-0002 contract report (source `72ad591`) as though it could review G-0003 S. The fixture identified both conditions before reviewer assignment. It contains no private credentials; its raw copy and CLI event log remain local-only.

The read-only native parent `01a132b5-a164-71d0-b061-eff758191b04` assigned the configured `contract-reviewer` and `adversarial-reviewer` roles to distinct child threads. Each received fixed S/P/base/E and the fixture without the other's first findings. Their first reports, retained separately under `reviews/`, both **blocked readiness**: the earlier-goal review was stale for S, and the required recovery artifact was missing from E. Both additionally identified the absent G-0003-A03 report and other pending gate material. Neither waived a blocker or declared final pass. This is the observed negative gate result, not an expectation inferred from a template.

| Negative condition | Actual first contract result | Actual first adversarial result | Disposition |
| --- | --- | --- | --- |
| G-0002 review offered for G-0003 S/P | Rejected as stale | Rejected as stale | Do not count it; obtain current reviews of S |
| G-0003-A02 absent from E1 | AC03 unproven, readiness blocked | AC03 unproven, readiness blocked | Retain the measured fresh recovery and digest matches, then review again |

The fresh recovery used a local complete Git bundle and inventory checkpoint made at 2026-10-13 00:40:18.641397 UTC. The new session resumed useful work at 00:42:45.2254394 UTC: **146.584 seconds** checkpoint-to-resumption, with 60.157 seconds of recovery execution. The authoring writer had been interrupted while running; its unretained draft was not used. G-0003-A02 records the retrieval results and limits. Two actual commerce stacks ran concurrently beforehand, each passed 43 maintained HTTP checks, and cross-project product IDs did not leak between them; `branch-map.md` and `checks/` retain those observations.

At this report stage, the negative case is resolved only as a challenge result. The first reviews remain blockers for E1. Current final native reviews, a filled validated manifest, intended-use validation and readiness decision remain open until separately observed. Private `.env` values and database volumes were not restored; the bundle is local-only rather than an off-device backup. Host JDK 17 failed the Java 21 core command, while the same suite passed in a scoped Java 21 container; G-0004 must resolve the intended local build toolchain. No production release or version-producing merge is implied.

## Scoped cleanup correction

After the evidence run, an integration cleanup command inspected the two drill worktree paths but ran `docker compose down` from the root checkout by mistake. It stopped the root development project `park-wb-81aff63b` twice; the second call found no containers. The root project was immediately restarted with `docker compose --env-file .env up -d --wait --wait-timeout 240`, exit 0. Docker then reported six healthy root containers and HTTP 200 from the catalog, pricing and booking health endpoints on ports 8081, 8082 and 8083. No `-v` was used and its data volumes were retained. The two drill projects were subsequently stopped from their own worktree directories using explicit `--project-name pcw-g0003-impl` and `--project-name pcw-g0003-validation`; their six named volumes remain. This mistake did not affect the earlier concurrent isolation observation, but it shows why cleanup commands must bind both the worktree and Compose project explicitly. It is recorded here rather than silently omitted.
