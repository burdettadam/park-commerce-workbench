# G-0003 two-worktree verification procedure

Use this from two assigned, clean worktrees at the same accepted plan P. The
implementation writer owns project `pcw-g0003-implementation`; the independent
validation writer owns `pcw-g0003-validation`. Keep each worktree's private
`.env` in that worktree. Record the exact branch/HEAD, `git status --porcelain`,
writer, start/end time, commands, exit codes, and sanitized output for each
observation. Run the commands from each repository root; never print `.env` or
unfiltered `docker compose config`, which contains local tokens and passwords.

## Configuration and live resource identity

1. In each worktree, run `git rev-parse HEAD`, `git branch --show-current`,
   `git status --porcelain`, `docker compose config --quiet`, and
   `docker compose config --format json`. Parse the JSON locally and retain
   only project name, service names, published host IP/port and container
   target port, and volume names. Compare all six published host ports and
   three volume names across projects; no pair may overlap. The service
   targets are expected to be shared internal ports. A Compose configuration
   comparison alone is not a live isolation pass.
2. The assigned runtime owner starts only its project with
   `docker compose up --build -d --wait` and records the exit code and elapsed
   time. In each worktree, use `docker compose ps --all`,
   `docker compose ps -q`, `docker compose port catalog 8081`,
   `docker compose port pricing 8082`, and `docker compose port booking 8083`.
   Match container IDs to
   `docker inspect <id> --format '{{json .Config.Labels}} {{json .Mounts}}'`
   and compare the Compose project/service labels, actual mounts, published
   ports, and health states. Redact environment values and credentials from
   retained output. `docker volume ls --filter label=com.docker.compose.project=<project>`
   should list each project's three own data volumes. A healthy response on a
   port is insufficient unless the responding container is identified.
3. Read `http://127.0.0.1:<catalog-port>/actuator/health`, and the analogous
   pricing and booking URLs, for both projects. Confirm successful health and
   that the port-to-container mapping from step 2 points to the expected
   project. Record unavailable endpoints as failures. Inspect for other
   applicable shared effects: this Compose file has no queue, external email,
   payment, webhook, or object-store service. Confirm in the live application
   configuration that no external effect is enabled; its three database
   volumes and local HTTP endpoints are the known mutable resources in this
   drill. Separate
   browser profiles would be required if browser sessions were exercised.

## Mutable data independence and interrupted startup

4. Run the maintained `node scripts/demo-http.mjs` once in each worktree while
   both stacks are up. Retain each run's exit code, check count, and printed
   synthetic `productId` (no tokens). For each generated ID, read
   `/api/products/<productId>` on both catalog ports. The owning project must
   return HTTP 200 with that exact ID; the other project must return HTTP 404.
   Repeat the own/other reads after both demo runs to catch cross-project
   effects. The demo creates data and does not erase it; preserve the volumes
   until recovery evidence is retained. If either demo fails, record the raw
   failure and do not call the independence check passed.
5. The integration owner chooses one owned project for a bounded interruption
   test. Record its container IDs/health before interruption; interrupt one
   scoped startup or stop operation using its actual process/session handle,
   then use `docker compose ps --all` and Docker labels to reconcile partial
   state. Restore only that project's services with the maintained Compose
   command and recheck its own and the other project's endpoint/data identity.
   Do not stop, reset volumes, or remove containers owned by the other writer.

## Fresh handoff and recovery

6. Before ending the authoring session, checkpoint both included branch commits,
   the assembled source candidate S, the explicit excluded alternative, the
   exact accepted P, and indispensable evidence digests. The integration owner
   publishes the authorized sanitized branch/artifact refs first, or records a
   local bundle/ZIP with SHA-256, owner and location. A local archive is
   `local_only`, not an off-device backup. Record the handoff start time.
7. In a genuinely fresh directory/session, read the retained goal and branch
   records first. Clone/fetch or restore the recorded refs; retrieve **every**
   indispensable artifact, recompute its SHA-256 with
   `Get-FileHash -Algorithm SHA256` (or `sha256sum` in Git Bash), and compare
   with the checkpoint. Use
   `git rev-parse` and `git merge-base --is-ancestor` to check exact included
   commits and ensure the excluded alternative was not silently integrated.
   Recreate the private `.env` and scoped worktrees through the maintained
   setup; never restore credentials from a public report. Run the selected
   checks on the fixed candidate. Record the time at which useful work resumes,
   elapsed handoff, loss or unknown loss, unavailable inputs, and local-only
   limits. A clean clone and matching Git SHA alone do not prove recovery of
   local bundles, credentials or runtime data.

Do not mark G-0003 ready on this procedure alone. Retain the actual outputs,
candidate-specific manifest, stale-review and missing-artifact gate challenges,
two independent native reviews, and delegated or human intended-use validation
under the accepted goal record.
