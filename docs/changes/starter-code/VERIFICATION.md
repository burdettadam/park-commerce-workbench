# Observed starter-code verification

## Identity and limits

- Imported roadmap archive SHA-256: `d8802442e30faf33a89a4cd083d147b5d9738826a02b10cf5356db99c7285056`.
- Local imported baseline: `dd7acc604e64d39279b0a6e129aeeac5c072a2bf`. This is a locally constructed checkpoint, **not** a GitHub commit.
- Final tested source **S**: `210ae99f7b9b7294180990f10584d1e58f12971d` on `local/starter-code`.
- Later report commit **E** records S and adds raw evidence, documentation and source-manifest hashes only. E is identified by the separately delivered bundle and patch; this report does not pretend to know its own future hash.
- Final selected checks ran with a clean source worktree before and after execution. Detailed command, timestamps, working directory, exit codes and output digests are in [checks.json](evidence/checks.json).
- The test environment has JDK 21, Bash, Git and Node. Maven/Docker executables were absent; Maven Central DNS resolution failed during the initial capability check. No dependencies were downloaded.

## Actual executed checks

| Check | Observed result | Evidence |
| --- | --- | --- |
| Existing + new Java core scenarios | 107 passed, 0 failed; 77 previous + 30 preview | [core.txt](evidence/core.txt) |
| Local tooling / initializer wrapper | 29 checks passed using a mock initializer | [tooling.txt](evidence/tooling.txt) |
| Existing client checkout-state suite | 10 passed, 0 failed | [client.txt](evidence/client.txt) |
| Existing roadmap/evidence validator suite | 41 passed, 0 failed | [validators.txt](evidence/validators.txt) |
| Roadmap structure | 10 milestones / 30 proposals / 120 criteria retained | [roadmap.txt](evidence/roadmap.txt) |
| Bash and live-demo JavaScript syntax | Exit 0 | `shell-syntax.txt`, `http-script-syntax.txt` |
| Source S file manifest | All listed files matched | [source-manifest.txt](evidence/source-manifest.txt) |

The two build-wrapper attempts are explicitly **blocked**, not green: `--java` and `--integration` returned 2 because Maven was unavailable. Their raw messages are retained. All 16 Testcontainers tests, the new REST endpoint, actual authorization/serialization, Vaadin compilation/browser use, and the 17-check live HTTP exercise remain unexecuted. The new integration tests use a real Spring server/MySQL when run, but a test-double catalog client. The live exercise is the separate real cross-service check.

## Preserved red-to-green evidence

**Exact-version defect:** `3e1becad3c5e42323a72eb8dc2fa22cb268740f8` contains the new tests against the old guard. The observed red run failed 11 rejection cases, including `0.2.01`, `10.2.0` and `0.2.0-rc.1`, and confirmed that init was wrongly called. `c8ca64ee4d9bc18f482ee6b327a0374169618602` repairs the guard; its complete tooling run passed. See [red](evidence/version-red.txt) and [green](evidence/version-green.txt). The exact expected version output was checked against the pinned private upstream source before implementation; its implementation was not copied into this project.

**Preview feature:** `0695d26c442b3cdab0c06c9083c3cebd639180fd` contains the new domain tests before the preview types and method existed. The red run is explicitly a **compile-time missing-feature failure**, not a claim that HTTP/database behavior was reproduced. See [preview-red.txt](evidence/preview-red.txt). Subsequent exploratory outputs are retained separately; final evidence is the clean-S run in `core.txt`.

## What the new tests establish

The core checks assert independently calculated money, inclusive date bounds, park-timezone midnight, invalid party/rate inputs, candidate fingerprint stability, pinned catalog handling, exact parity after publication, and preservation of old quotes/orders/capacity. A store implementation whose every method throws proves preview does not consult or mutate the pricing store. A bounded latch-based test publishes a different book while preview is suspended at its catalog lookup, confirming the original submitted candidate remains explicit.

These in-memory tests do not establish MySQL isolation, a full process crash/recovery result, API serialization, actual employee authorization or UI rendering. The service cannot access a booking store through its interfaces; that is code/test evidence, not a production monitoring observation.

## Review and decisions

One author implemented and self-checked the changes. **No independent contract review, independent adversarial review, intended-use validation or human release approval has occurred.** Neither G-0002 nor G-0008 is marked ready or complete. Their prerequisite milestones and accepted-plan records are still outstanding; this delivery is a code candidate authorized by the request in this chat.

## Recovery and next execution

The full source ZIP, cumulative patch against the original roadmap archive, and local Git bundle retain the work. Original roadmap proposal files remain intact. The old exact-version defect can be reproduced from the red commit in the bundle rather than reintroduced into the current source.

Run the framework build and database/HTTP/browser checks on the workstation, resolve failures without weakening criteria, then arrange the separate reviewers under the adopted `git-agent` governance. Keep remote pushes blocked until privacy is verified. No cloud resources, fees, production credentials, remote branches or release tags were created.
