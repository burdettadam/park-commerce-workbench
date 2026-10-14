# Development and recovery

Adopt existing maintained setup/run/test/build instructions. Fill applicable
entries below using real product commands and evidence. `UNCONFIGURED` is a gap,
not a command or success result. No product runtime or GitHub workflow is
generated until its stack and required checks are known.

For G-0001, Adam Burdett [adopted the first-local-goal choices](goals/G-0001/work/owner-decisions.md)
on 12 October 2026. They govern this Windows/Git Bash pilot only. Wider product
support and two-instance isolation need their own measured decisions.

## Reconstructable product recipe

| Required information | Actual definition or maintained link |
| --- | --- |
| Supported OS, Git, Codex/extension versions | G-0001 pilot: Windows with Git Bash, Git for Windows 2.53.0.windows.2 and Codex CLI 0.162.0-alpha.17.2. Source handoff also documents Linux checks in [START HERE](docs/START-HERE.md); broader support and IDE-extension version certification are future decisions. |
| Toolchain/runtime/service/browser versions and native lockfiles | G-0001 pilot uses JDK 21, Maven 3.9+, Node 24 and Docker Compose v2 from root `README.md` and `pom.xml`. `storefront/package-lock.json` pins the npm tree for `npm ci`. Browser acceptance is outside this initializer/documentation goal; a UI goal must select and verify browser versions. |
| Setup, fixture preparation, existing live-reload command | [START HERE](docs/START-HERE.md), `scripts/configure-local.sh`, `docker compose up --build -d --wait`, `node scripts/demo-http.mjs`; UI start commands are in START HERE. Create private `.env` locally; never commit it. |
| Focused, contract, integration, browser and build commands | `scripts/test-core.sh --demo`, `node --test storefront/src/app/*.test.mjs`, `node --test scripts/pricing-contract.test.mjs`, `scripts/verify.sh --java`, `scripts/verify.sh --integration`, `node scripts/validate-roadmap.mjs`; see START HERE for UI builds. G-0001 runs initializer/version/discovery/tooling checks; browser acceptance belongs to a later UI goal. |
| Working directory and materially relevant configuration | Run root scripts from the repository root; `storefront` npm commands from `storefront/`. `compose.yml`, `.env.example`, and `docs/project/security.md` describe local ports and synthetic credentials. |
| Local/CI differences, production-build checks | `.github/workflows/verify.yml` runs Linux candidate checks; it is not a version release gate. The [import verification](docs/import-verification-2026-10-12.md) records the earlier Windows `.env` ACL failure; the later Git Bash tooling suite and six-service Windows Compose run passed on the fix branch. Browser acceptance belongs to a later UI goal. |
| Submodules, LFS, hooks, generated sources, private setup | No tracked LFS objects were observed at bootstrap. G-0001 has no submodule or hook operation. The private initializer source is not vendored; provenance is in [development bootstrap](docs/development-bootstrap.md). A later goal using submodules or hooks must define and test its policy. |
| Development credential names and secure recovery procedure | `.env.example` names development values; `scripts/configure-local.sh` creates private local `.env`. Adam Burdett owns account recovery; on a replacement host, restore GitHub/Codex access through their secure account flows and recreate local `.env` from the example. Do not commit credentials or assume Git clone restores machine settings. |

Record material host/build-mode/locale/timezone assumptions. Demonstrate setup
from clean inputs without correctness depending on an existing cache. Keep
versions/dependencies in native manifests, not a second dependency database.
Never commit secrets. Do not assume a clone restores machine settings or hooks.

## Worktree execution

Use the worktree-flow skill and its isolation/recovery reference. Record the
actual existing flags, environment settings, task, or configuration that scope:

- endpoints and callback/base URLs;
- database/schema/fixtures, queues, consumers, caches and generated output;
- browser contexts/test users and mutable temporary state;
- owned processes or containers;
- applicable email, webhook, object-store, identity and payment effects.

Use synthetic or appropriately sanitized fixtures. Different ports do not isolate
browser cookies or external effects. Prove two real instances cannot contaminate
each other and reconcile a partial startup/cleanup before calling the recipe
isolated. A missing control is a product work item.

| Execution bound | Demonstrated value and mechanism |
| --- | --- |
| Concurrent live instances / active-work limit | G-0001: one owned local Compose project and one active mutable product instance. Two-instance independence remains G-0003 work. |
| Test workers and shared-state serialization | G-0001: serialize tests sharing its local database or ports; use synthetic fixtures. No general product worker count is certified. |
| Readiness and test time bounds | G-0001: retain actual exit status and duration for local checks; stop a hung run by its owned process/session handle. CI uses the existing workflow timeouts. No general readiness bound is certified. |
| Endpoint identity, process ownership, interrupted cleanup | G-0001: use maintained local commands, record the Compose project/container IDs if started, and stop only owned resources after interruption. No two-instance endpoint isolation is claimed. |

Start instances on demand. Share read-only dependencies/safe caches where valid.
Stop only owned resources. Record ownership using existing session/process
handles and compact local notes; no runtime registry or global semaphore is
required. Git worktree lock protects administration, not agent ownership.

Repository-wide ref/configuration/stash effects require coordination with the
administration owner in `GOVERNANCE.md`. Prefer intentional checkpoint
commits. If using a stash, retain its exact object identity and owner; never use
an unqualified latest stash. Preserve Git's same-branch safeguards and retain
required work before cleanup or rewriting refs.

## Evidence and CI economy

Live-reload checks may use changing files; label them exploratory and identify
dirty inputs. Acceptance evidence uses stable source/build inputs held fixed
during execution. HEAD alone does not identify code served from a dirty worktree.
Read `.agents/skills/work-goal/references/review-guide.md` for evidence fields and
flaky-check handling.

Generated evidence and artifact directories are ignored. Do not stage logs,
screenshots, test output, reviewer transcripts or evidence manifests. Keep them
locally during review; use PR discussion and hosted CI checks for concise,
source-linked findings. Remove local copies after work merges or is cancelled or superseded, once
the merge and required decisions are recorded. Keep code comments minimal and
put change rationale in the PR.

| Event | Required work using maintained commands |
| --- | --- |
| Active edits | Live feedback and focused local checks |
| Stable branch increment | Applicable local integration/contract checks |
| Combined candidate | Fixed inputs and the selected required verification set |
| Reviewer | Independent inspection and warranted targeted checks |
| Candidate change | Renew applicable verification/review coverage |
| Version release | Actual merged-source, artifact, demo and promotion checks |

Use architecture's impact map, including transitive consumers and changed
schemas/fixtures/generators/config/toolchains. Unmapped or uncertain impact needs
broader checks or a visible gap. Empty selection cannot establish success.
Maintain a stable aggregate required integration check that validates selection
and success of every selected required check, including module/interface checks
and product lanes. Missing, skipped, cancelled and failed selected checks do not
pass. Verify merge-queue candidates where applicable.

Use distinct checkpoint/candidate/release triggers. An open PR may run CI on
every push; use explicit retained checkpoint refs when needed and advance the
candidate deliberately. Cancel only superseded disposable checks, with GitHub
concurrency scoped by workflow/product/stream. Stateful deployment or migration
cancellation follows its interruption/recovery procedure.

Measure baseline and pilot feedback delay, expensive CI runs/minutes per goal,
review/rework effort, and escaped failures. State the observation window and
workload differences. Preserve required coverage when reducing duplicate work.

## Dependencies and maintenance

Record adopted origins/revisions and an update owner/trigger for templates,
initializer, executable dependencies, workflows/actions, and client/toolchain.
Pin executable CI dependencies to immutable revisions where supported and review
applicable distribution/license requirements. Updates are accepted planned work
with product checks and applicable behavioral calibration. Retain the prior usable
baseline where available; withdrawn hosted models/components need a verified
replacement, not an unsupported restoration promise.

## Recovery commitments

Baseline scenario: total workstation loss with GitHub material and independently
recoverable account access surviving. Provider/repository/account loss is a
separate scope decision. Production-data recovery is separate from development.

| Commitment | Actual value, owner, and retained evidence |
| --- | --- |
| Maximum tolerable active-work loss / RPO | G-0001 target: one hour while actively working. Push each meaningful increment, at handoff and at least hourly during active work; no unattended backup while inactive. Owner: Adam Burdett. |
| Target time to resume useful development / RTO | G-0001 target: one working day with GitHub material and independently recoverable account access surviving. Owner: Adam Burdett. |
| Retained work/checkpoint refs and required private guidance | Per-goal record; push source refs before a coordinating goal checkpoint. The initializer executable is not required after adoption. |
| Development evidence retention | Keep generated local evidence only while the PR is open; link relevant hosted CI runs and review findings. Remove local copies after merge or terminal cancellation/supersession. Separate release/operational retention decisions remain goal-specific. |
| Access, machine setup and native GitHub/Codex settings recovery | Adam Burdett owns secure GitHub/Codex account recovery. Reinstall documented toolchain, restore account settings through native facilities, and recreate `.env` locally from `.env.example`; never commit tokens. |
| Restore-drill triggers and measured loss/resumption time | Drill after material setup, access or recovery changes and before the first version release. Record measured elapsed loss/resumption time in the applicable goal evidence; the G-0001 clean-clone retrieval is partial, not a measured full restore. |

Checkpoint completed increments, pause/handoff, and before destructive cleanup.
Publish each component/work branch before its coordinating goal record. Confirm
remote revisions and relevant PR/CI links; report incomplete coverage and
local-only work. Generated development evidence is transient and ignored.
A local commit is not cloud backup.
Report confirmation time/age against targets while active. These instructions do
not provide an unattended timed backup while Codex is inactive.

Exercise a clean restore after material setup/access/dependency/recovery changes
and at the product's chosen reconsideration events. Follow the worktree-flow
recovery reference. Retrieve separately required release/operational artifacts
rather than trusting URLs. Retain source refs before cleanup. Do not invent the amount lost after a real
failure when it is unknown.
