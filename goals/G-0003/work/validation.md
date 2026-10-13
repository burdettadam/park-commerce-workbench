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

Selected follow-up commands: `docker compose config --quiet`, sanitized
`docker compose config --format json`, `docker compose up --build -d --wait`,
`docker compose ps --all`, `docker compose port`, `docker inspect`, scoped
`docker volume ls`, `node scripts/demo-http.mjs`, cross-project catalog GETs,
`git rev-parse`, `git merge-base --is-ancestor`, and `Get-FileHash`. The runtime
owner must retain actual exits/times and inspect process ownership before any
cleanup. Goal-wide selected product and evidence checks are the integration
owner's responsibility after candidate S is assembled.

Next action: hand this committed procedure and its exact revision to the
integration writer. After both runtime owners establish their projects, execute
the live identity and cross-data checks against fixed inputs; then participate
in fresh-session retrieval using retained records. No G-0003 acceptance,
runtime isolation, independent review, or intended-use validation is claimed
by this initial slice.
