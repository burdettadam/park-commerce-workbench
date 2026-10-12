# Catalog-publication verification

## Source and scope

- Input archive: `park-commerce-checkout-recovery.zip`.
- Input archive SHA-256: `ad1a31499f825705800e65cf89931037228b884330953d0d19f3a212b7b37d15`.
- Local base: `9686f2ad656415b819db220fe599a7ae325c35ff`. All 286 input files matched this bundle checkout before editing.
- Initial missing-feature test/plan commit: `fd8aea26e08b9885bcadb477c448c691518e53eb`.
- Fixed tested source **S**: `7a306aa579e2559853c8a3a37b03f4c3ff6dfbbb`.
- Branch: `local/catalog-publication`; no remote is configured.
- A later documentation/evidence/manifest-only commit **E** contains this report and
  raw observations. Its actual identity is retained in the bundle, not fabricated
  inside its own content. E does not change product code, tests, or framework config.

This is local development toward G-0007, not a GitHub push, accepted goal, released
version, deployed application, or verified private repository.

## Executed against clean source S

The [command index](evidence/checks.json) records exact command, working directory,
source, clean state before/after, UTC execution timestamp, duration, exit code and
raw artifact SHA-256. Every final command left source S clean.

| Check | Actual result | Evidence |
| --- | --- | --- |
| JDK-only Java scenarios and demo | **180 passed / 0 failed**; 123 previous + 57 editorial | [core](evidence/final-core.txt) |
| Client checkout/recovery tests | **66 passed / 0 failed**, unchanged suite | [client](evidence/final-client.txt) |
| Initializer/setup tooling | **29 passed**, mock initializer only | [tooling](evidence/final-tooling.txt) |
| Roadmap/evidence-validator tests | **41 passed / 0 failed** | [validators](evidence/final-validators.txt) |
| Roadmap structure | 10 milestones, 30 proposals, 120 criteria retained | [roadmap](evidence/final-roadmap.txt) |
| Public client TypeScript declarations | Exit 0 with installed TypeScript 5.8.3 | [types](evidence/final-types.txt) |
| Bash and JavaScript syntax | Exit 0 | `final-shell-syntax.txt`, `final-js-syntax.txt` |
| JSON/POM XML/YAML parsing | Exit 0, syntax/structure only | [config](evidence/final-config.txt) |
| Source-S manifest | 294 listed hashes matched | [hashes](evidence/final-source-manifest.txt) |

The code source S was also archived from Git and extracted into a directory
containing spaces. Core, client, tooling and validator checks passed again there.
The [source-archive record](evidence/source-archive.json) identifies the exact archive,
296 files, raw portability logs and digests. Source S's manifest excluded a retained
nested historical manifest by filename; the final delivery manifest corrects that
metadata-only exclusion and hashes every delivered file except itself.

## What the tests prove—and do not prove

The 57 new core cases exercise immutable drafts, guest invisibility, exact selected
content, stale bases, direct-publication compatibility, embedded audit, legacy
unknown provenance, bounded cursor parsing/pagination, clock reversal, immutable
collections, invalid inputs and exhausted version counters.

Two tests force **24 writers** to finish the service's read/check phase before any
insert is allowed. Exactly one save wins in one test and one publication wins in
the other; all losing calls must report CONFLICT. Other tests inject edits or rival
publications between checks and inserts. These observations use **test-only
synchronized in-memory stores**. They do not establish MongoDB index correctness,
multi-process isolation, persistence after a crash, or a real browser interaction.

The [audit example](audit-example.json) is extracted from the actually executed
controlled Java demo. Actor `employee-b` and its clock are synthetic fixtures, not
claims about a real authenticated employee or production timestamp.

## Unavailable framework/toolchain validation

Observed environment: OpenJDK/Javac 21.0.12.1, Node 22.16.0, TypeScript 5.8.3,
Git 2.47.3 and Bash 5.2.37. The Angular project still targets its original Node 24 /
TypeScript 5.9.3 baseline. No dependency versions or stack technologies were replaced.
The declaration-only check is **not an Angular compilation**.

Maven and Docker are absent; Maven Central DNS lookup failed. See
[capabilities](evidence/capabilities.txt). Actual attempts were retained:

| Attempt | Outcome |
| --- | --- |
| `bash scripts/verify.sh --java` | Exit 2, blocked by missing Maven |
| `bash scripts/verify.sh --integration` | Exit 2, blocked by missing Maven; Docker also absent |
| `mvn -B -ntp -Pui -pl admin-console -am verify` | Exit 127, Maven unavailable |
| `cd storefront && npm run build` | Exit 127, Angular CLI dependencies unavailable |

These are **blocked, not passing or intentionally skipped tests**. The new Spring
controllers, Principal-derived audit, MongoDB record conversion/indexes/query bounds,
legacy schema compatibility, and Vaadin handlers have not been dependency-built or
executed. Six new Mongo adapter tests and eight new Spring/Mongo HTTP tests are
included (**37 total IT tests** across services), but not reported as passing.
The live HTTP script now has **30 named assertions**, seven new editorial checks;
only its JavaScript syntax was checked, not a service run. Existing core, Node and
parser results are not substitutes for those acceptance gates.

## Red-to-green history and author's self-checks

The retained initial test commit fails because `CatalogEditorial` does not exist.
[Initial red output](evidence/initial-missing-feature-red.txt) is a genuine
missing-feature compile failure, not a failed running database or HTTP request.

An early exploratory compile also caught an unqualified static helper call shadowed
by a record accessor; [that compiler output](evidence/exploratory-compile-error.txt)
is retained. It came from a dirty worktree and is not attributed to a frozen commit.
The helper call was qualified before the final candidate; all final core tests pass.
The larger suite and injected races are author's self-checks, **not independent reviews**.

## Authority, remaining work and recovery

Independent contract review: **not performed**. Independent adversarial review:
**not performed**. Human intended-use validation: **not performed**. Native
`git-agent` adoption: **still local workstation work**. Release approval: **not granted**.
No roadmap goal is promoted to ready/complete and no acceptance criterion is waived.
See [goal coverage](goal-coverage.json), [plan](PLAN.md), and [contract](../../project/catalog-publication.md).

The next integration gate is the real Java/Mongo/Vaadin build and HTTP/browser
workflow, including role rejection, audit round trips, explicit preconditions,
legacy-document reads and index contention. Then obtain the required independent
reviews and human validation under the adopted governing baseline.

The full ZIP, patch against the checkout-recovery ZIP and local Git bundle are
handoff artifacts. They are not proof of off-device restoration or GitHub privacy.
Use the source archive within the existing local clone; do not force-push this
separate local history. No initializer executable, real credentials, cloud
resources, paid tooling, deployment, remote branch or release tag was created.
