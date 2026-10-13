# G-0003 independent validation/documentation slice

Writer: independent Codex validation writer. Assigned branch:
`goal/G-0003-validation`; assigned worktree: `.tmp/g0003-validation`. The
integration writer owns `goal/G-0003`; a separate implementation writer owns
`goal/G-0003-implementation`. This writer edits only this branch/worktree and
does not administer shared Git refs or start/stop another project's containers.

Accepted plan P: `4a0d6a66a4d51eca57059a88875fb6efe7ab687e`.
Integration base: `14656cf658c22df5560a37b26f2d6f1ea5f3be30`.
The four G-0003 criteria in the active goal record remain unchanged. The
[verification procedure](isolation-and-restore-check.md) uses the maintained
Git, Docker Compose, HTTP, and digest tools; it adds no runner or command
protocol. Existing `scripts/demo-http.mjs` is the canonical synthetic HTTP
fixture, reused for the cross-project catalog read rather than duplicated.

## Initial read-only observation

From this clean branch at the integration base, the locally parsed
`docker compose config --format json` resolved project
`pcw-g0003-validation`, services `booking`,
`catalog`, `mongo`, `mysql-booking`, `mysql-pricing`, `pricing`, host bindings
`127.0.0.1:8281->8081`, `8282->8082`, `8283->8083`, `3327->3306`,
`3328->3306`, and `27217->27017`. Named volumes resolved to
`pcw-g0003-validation_booking-data`,
`pcw-g0003-validation_mongo-data`, and
`pcw-g0003-validation_pricing-data`. The private `.env` was generated in this
worktree; its contents are not retained in Git. This is a configuration check,
not evidence of running containers, project separation, data independence,
capacity, or recovery. Implementation-project configuration and live identities
still need comparison.

`docker compose config --quiet` exited 0, and
`node scripts/validate-roadmap.mjs` exited 0 with 10 milestones, 30 goals,
120 criteria and 90 goal-specific artifact requirements. Neither result
establishes live isolation or acceptance.

## Assigned validation runtime, 13 October 2026 UTC

The validation writer ran
`docker compose --env-file .env up --build -d --wait --wait-timeout 240`
from this worktree, from `00:33:06.9337883Z` to `00:34:26.0770068Z`.
Exit was 0. The private raw output is
`.tmp/g0003-validation-compose-up.raw.txt` (35,858 bytes, SHA-256
`8a6af055af06ea5d0edf8c48267dc64c9d496450caec65ae730f2c925cfb3375`).
That file is ignored and remains local-only; it is not a remotely retained
artifact.

`docker compose --env-file .env ps -q` returned six IDs. Docker inspection
showed each running, healthy and labeled `com.docker.compose.project` =
`pcw-g0003-validation`: `catalog` `5ea7c0ad5aed`, `pricing`
`4fe997e1ae3b`, `booking` `b3aac265d9fc`, `mongo` `425fa05de956`,
`mysql-pricing` `dd2c0675b34a`, and `mysql-booking` `0b502bc04a93`.
The three named database mounts were
`pcw-g0003-validation_mongo-data`,
`pcw-g0003-validation_pricing-data`, and
`pcw-g0003-validation_booking-data`. Mongo also has an image-declared
anonymous mount; it is not treated as one of the three named project volumes.
Native `docker compose port` mapped catalog, pricing, booking, Mongo,
pricing MySQL and booking MySQL to `127.0.0.1:8281`, `8282`, `8283`,
`27217`, `3327`, and `3328`, respectively. HTTP GET to each application
`/actuator/health` returned 200 and application status `UP`.

The writer ran `node scripts/demo-http.mjs` here from
`00:35:00.9579862Z` to `00:35:03.5137313Z`; exit 0 and **43** maintained
live HTTP acceptance checks passed. It created synthetic catalog product
`lantern-fb95be11`. Private raw output is
`.tmp/g0003-validation-demo-http.raw.txt` (2,698 bytes, SHA-256
`e8244229c6bb30ebc4149778b6a807f68b9d1292dacd6704e5f73a37e4f6dc42`).
This is this project's own live behavior. The implementation project's
endpoint identity and cross-project absence of this ID remain coordinated
follow-up checks; no two-project data-isolation result is claimed here.

Selected follow-up commands: `docker compose config --quiet`, sanitized
`docker compose config --format json`, `docker compose up --build -d --wait`,
`docker compose ps --all`, `docker compose port`, `docker inspect`, scoped
`docker volume ls`, `node scripts/demo-http.mjs`, cross-project catalog GETs,
`git rev-parse`, `git merge-base --is-ancestor`, and `Get-FileHash`. The runtime
owner must retain actual exits/times and inspect process ownership before any
cleanup. Goal-wide selected product and evidence checks are the integration
owner's responsibility after candidate S is assembled.

Next action: hand this committed runtime observation and exact revision to
the integration writer. Under the integration writer's coordination, compare
both projects' identities and perform own/other catalog reads for each
synthetic product ID; then participate in fresh-session retrieval using
retained records. No G-0003 acceptance, cross-project data isolation,
independent review, or intended-use validation is claimed by this slice.
