# Observed checkout-recovery verification

## Source identities

- Input archive: `park-commerce-starter-code.zip`, SHA-256 `8e25a5e47d1dc22848fc3254f7c7bbaf8e520ed46e78cf78234ce9985eaf3105`.
- Local base: `28764055cf18071aecc640f3b6c864290808df67` from the supplied `local/starter-code` bundle.
- Initial missing-feature regression commit: `665b046fafef0cdf1c40e953164eed2bd30189be`.
- Final tested source **S**: `d991a2ebe564ac340b46d7b54ea785d2512cd0ca` on `local/checkout-recovery`.
- A later report-only commit **E** adds this report, raw evidence, coverage mapping
  and updated source-manifest hashes. Its identity is in the delivered Git bundle.
  No runtime code, framework configuration or test assertion changes in E.

These are local Git identities, **not GitHub publication or release identifiers**.
The checkout has no remotes. Source S was clean before and after the final checks.
The full [check index](evidence/checks.json) retains command, directory, source,
clean/dirty classification, timestamps, exit status, duration and output digest.
Recorded timestamps are UTC from the execution environment.

## Executed checks against frozen source S

| Check | Actual result | Raw evidence |
| --- | --- | --- |
| JDK-only Java core | 123 passed / 0 failed; 107 previous + 16 recovery | [final-core.txt](evidence/final-core.txt) |
| Client state and recovery coordinator | 66 passed / 0 failed; 10 previous + 56 recovery | [final-client.txt](evidence/final-client.txt) |
| Tooling and initializer wrapper | 29 passed; mock initializer only | [final-tooling.txt](evidence/final-tooling.txt) |
| Roadmap/evidence validators | 41 passed / 0 failed | [final-validators.txt](evidence/final-validators.txt) |
| Roadmap structure | 10 milestones / 30 proposals / 120 criteria retained | [final-roadmap.txt](evidence/final-roadmap.txt) |
| Public TypeScript declaration contract | Exit 0 with installed TypeScript 5.8.3 | [final-types.txt](evidence/final-types.txt) |
| JavaScript and Bash syntax | Exit 0 | `final-js-syntax.txt`, `final-shell-syntax.txt` |
| JSON, POM XML, workflow YAML parsing | Exit 0; syntax/structure only | [final-static-config.txt](evidence/final-static-config.txt) |
| Source-S file manifest | All listed hashes matched | [final-source-manifest.txt](evidence/final-source-manifest.txt) |

Source S was also archived, extracted into a directory containing spaces, and its
manifest/core/client/tooling/validator checks rerun successfully. Corresponding
`archive-source-*.txt` logs identify that archive and extraction, not a live Git
worktree or a hosted deployment.

## Toolchain and unavailable validation

Observed: OpenJDK/Javac 21.0.12.1, Node 22.16.0, TypeScript 5.8.3, Git 2.47.3 and
Bash 5.2.37. Node 24 / TypeScript 5.9.3 remain the planned Angular workstation
baseline. The declaration-only test is not proof of the pinned Angular toolchain.

Maven and Docker are absent. Maven Central lookup failed DNS resolution; npm
registry lookup failed/timed out. No frontend dependencies are installed. The
capability check is retained in [capabilities.txt](evidence/capabilities.txt).

`bash scripts/verify.sh --java` and `--integration` each returned **2** at the
missing-Maven prerequisite. They are **blocked, not passed or intentionally skipped**.
See [Java attempt](evidence/final-java-framework.txt) and
[integration attempt](evidence/final-database-framework.txt).

**Not compiled/executed here:** Spring application modules and authentication
filters, real MongoDB/MySQL adapters, all 23 Testcontainers tests (including the
seven new recovery tests), the 23-check live HTTP exercise, Angular compilation
and real browser storage/reload behavior, Vaadin compilation, Docker, Jenkins,
GitHub Actions, deployment, or production identity. No actual financial transaction
is implemented or tested.

The Java checks use test-only in-memory stores. The Node loss/reload test uses
storage and transport test doubles. The new HTTP/database suite uses real
Spring/MySQL when run, but a test-double pricing client and controlled clock.
The live HTTP script uses actual services when run, but a storage fixture and
coordinator reconstruction—not an actual browser reload. These distinctions
must survive any later portfolio claim or goal acceptance.

## Red-to-green evidence and author's self-checks

At `665b046`, the Java tests fail because the recovery API does not yet exist;
the Node suite fails because the coordinator module does not yet exist. Those
are reproducible **missing-feature compile/import failures**, not simulated
passing HTTP or database tests. See `recovery-core-red.txt` and
`recovery-client-red.txt`. Their source commit is retained in the bundle.

During exploratory, explicitly dirty-source self-checking, two extra client
regressions exposed acceptance of a changed hold amount and a changed order ID
under the same retry identity. Both failed before correlation checks were added
and passed afterward. Logs `recovery-receipt-correlation-red.txt` and
`recovery-client-correlation-green.txt` preserve those attempts. They are not
claimed as clean-source review evidence or separately retained exact commits.
Final frozen-source checks establish the delivered candidate's current coverage.

Late-response checks also verify that logout and a later purchase cannot be
silently overwritten by an old response, and a direct confirmation updates the
known hold's displayed status. Form reset and storage cleanup never authorize a
server cancellation or a replacement order.

## Review, goal authority and recovery

One author implemented and self-checked this slice. **Independent contract review:
not performed. Independent adversarial review: not performed. Intended-use human
validation: not performed. Release approval: not requested or granted.**

The slice contributes to G-0014-AC02/AC03. The actual browser/cart/accessibility
criteria, native agent adoption and prerequisite goal decisions remain outstanding.
No goal is marked ready or complete. See [coverage map](goal-coverage.json),
[slice plan](PLAN.md) and [reload/retry matrix](reload-and-retry-matrix.md).

The source ZIP, incremental patch against the starter-code archive and local Git
bundle are handoff artifacts. They do not establish off-device recovery or remote
repository privacy. Overlay source into the user's existing local clone to retain
its history; do not force-push this separate local history. Verify private visibility
before any future push. No private initializer, actual credentials, cloud resources,
paid service, deployment, remote branch or release tag was created by this slice.
