# Roadmap delivery verification

This is a **planning and artifact-contract delivery**, not acceptance of a product goal. No initializer, native model/reviewer session, human approval, GitHub write, cloud operation or application release occurred.

## Observed checks

| Check | Actual result | Meaning |
| --- | --- | --- |
| Read-only roadmap validation | 10 milestones, 30 goal proposals, 120 criteria, 90 goal-specific artifact requirements; no structural errors | IDs, dependencies, acyclic graph, card/index agreement, command references, templates and existing requirement coverage |
| Offline validator tests | 41 passed, 0 failed, 0 skipped | Positive/negative fixtures for structure, stale/missing evidence, digests, dropped criteria, empty/skipped suites, review scope and decision/recovery fields |
| Unfilled manifests | All 30 rejected as not ready by a validator test | Templates cannot count as completed evidence without being filled; not proof that filled claims are authentic |
| Existing Java core rerun | 77 passed, 0 failed | JDK-only core with test-only in-memory stores, not actual Spring/database behavior |
| Existing client-state rerun | 10 passed, 0 failed | Node retry-state tests, not Angular rendering/build |
| Existing tooling rerun | 11 passed | Mock-based wrapper tests, not private initializer execution |
| Source preservation | 83 preexisting non-document code/configuration files unchanged byte-for-byte | Planning did not silently fix/rewrite application code, wrappers or pipelines |

The validator suite uses explicitly synthetic identities, reports and decisions. A synthetic complete fixture passes the structural contract; it does not constitute a real goal run or independent review. The scripts do not grant approval, run commands from metadata, authenticate a human, verify a remote Git object or perform any goal-state transition.

## Concrete planning discovery

The current wrapper accepts version strings such as `0.2.01`, `10.2.0`, and `0.2.0-rc.1` because its guard tests a substring. This was reproduced in disposable worktrees with a mock command and retained in [planning-discovery.txt](planning-discovery.txt). No private initializer was run.

The defect remains in the unchanged candidate and is explicitly assigned to **G-0002**, the bounded first post-adoption red/green goal. During G-0001 bootstrap, manually verify the exact reviewed initializer version before invoking the wrapper. Do not interpret the preexisting 11 passing tooling checks as coverage for the newly identified cases.

## Retained local observations

- [Roadmap check](validation/roadmap-check.txt)
- [Validator test output](validation/validator-tests.txt)
- [Core rerun](validation/core-rerun.txt)
- [Client-state rerun](validation/client-rerun.txt)
- [Tooling rerun](validation/tooling-rerun.txt)
- [Environment](validation/environment.txt)
- [Source preservation](validation/source-preservation.txt)
- [Original source/ZIP provenance](baseline/provenance.json)

The updated archive contains no `.git`, usable `.env`, private initializer executable, generated native agent guidance, installed dependencies or fabricated source commits. Its root `SOURCE-MANIFEST.sha256` identifies this delivered file set. Original evidence remains bound to the foundation archive recorded under `baseline/`; later evidence/report changes are not retroactively original test inputs.

## Remaining validation boundary

Maven and Docker remain unavailable in this execution environment. No Spring/UI build, database suite, HTTP/browser application exercise, container/pipeline/Kubernetes/AWS deployment, vendor-tool instrumentation or native goal workflow was run. The first application milestone still has to establish those relevant baseline capabilities on the user's workstation.

The final packaging procedure extracts the archive into a fresh directory with spaces, checks every file against the source manifest and reruns the roadmap/validator/core/client/tooling checks. Its observed result is recorded in the accompanying final response. File hashes establish bytes, not the truth of human/model claims.
