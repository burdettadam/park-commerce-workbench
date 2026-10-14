# Starter-code import verification

Date: 2026-10-12

Branch: `codex/git-agent-bootstrap-20261012` (draft PR #1)

The handoff was found in a local handoff directory. Its 393-entry `SOURCE-MANIFEST.sha256` passed before import, and 394 files were copied into the existing Git clone. The original manifest is retained as [archive source provenance](https://github.com/burdettadam/park-commerce-workbench/blob/90ae8eff6485c950c8d02c8d3a33893059f5d82e/docs/archive-source-manifest-2026-10-12.sha256). The root manifest is regenerated for the integrated checkout because merged documentation, source fixes, the frontend lockfile, and project-local guidance change the file set.

GitHub reported `PRIVATE` before the starter code was pushed. The source handoff contained no `.git` directory or `.env`; the latter remains ignored. The private `git-agent` implementation was not copied into this repository.

## Executed checks

| Check | Observation |
| --- | --- |
| `git-agent` wrapper preview | The real `scripts/init-agent.sh --preview` found the 15 existing guidance files; 12 were unchanged and three product documents were adopted as existing content. The initializer made no writes. |
| Java domain walkthrough | JDK 21 container: 280 passed, 0 failed; in-memory test stores only. |
| Maven `scripts/verify.sh --java` | JDK 21 / Maven 3.9.11 container: full six-module reactor built; 280 JUnit core scenarios passed. An initial `JdbcBookingStore` method-reference ambiguity was fixed before this result. |
| Client state tests | Node 24.14.1: 66 passed, 0 failed. |
| Frontend dependency and build | `storefront/package-lock.json` generated; `npm ci`, `npm run test:types`, and `npm run build` passed. Browser interaction was not tested. |
| Roadmap structure | 10 milestones, 30 proposed goals, 120 criteria, and 90 goal-specific artifact requirements validated. |
| Roadmap/evidence tests on Windows | 40 passed, one test could not create a symlink (`EPERM`); this is an environment limitation, not a passing test. |
| Tooling on Windows | Stopped at its Unix permission assertion for `.env`; no full pass claimed. |
| Tooling in Linux container | 29 passed; initializer calls in that suite used a mock. |
| Compose and live API | `docker compose --env-file .env.example config --quiet` passed. Private `.env` generation, `docker compose up --build -d --wait`, and all six service/dependency health checks passed. `node scripts/demo-http.mjs` passed 43 live HTTP assertions using synthetic data; `docker compose down` stopped the containers without deleting the named volumes. |
| Database integration | Catalog: 17 passed after correcting the test database name and legacy document mapping; a focused rerun verified both literal `id` and mapped `_id` legacy records. Pricing contract: 12 passed. An isolated JPA pricing rerun passed seven tests after an initial Docker Desktop MySQL connection timeout. Booking: 28 passed. The hosted combined run subsequently passed all 64 integration tests. |
| Hosted PR checks | A private candidate CI run passed both `java` and `client-contracts` on the imported source commit `f8c5471`. The downloaded Java artifact contains eight `TEST-*.xml` reports: 344 tests, including all 64 integration tests, with zero failures, errors, or skips. The Java job also passed the 29 pricing-contract Node tests; the client job passed install, type, build, state, and roadmap/evidence checks. |

The Windows host has JDK 17 and no host Maven. JDK 21/Maven tests ran in `maven:3.9.11-eclipse-temurin-21` with a project bind mount. Docker Desktop supplied Testcontainers. The local `.env` is ignored and contains generated development credentials; the stopped Compose volumes retain only synthetic test data. The hosted run verifies the combined candidate, while clean-host restore, browser acceptance, and deployment remain unverified.

## Hosted control limit

The original private repository returned HTTP 403 when checking rulesets and `main` branch protection on the available account plan. The historical draft PR and passing Actions jobs were observable there, but no merge or release was performed. The public repository needs its own hosted branch controls verified separately.

## Remaining adoption work

The generated `GOVERNANCE.md` is still proposed, and the imported roadmap contains proposed rather than accepted goals. The private archive retains its historical draft PR. The public source snapshot is a separate, clean history. Independent reviewer execution, a human adoption decision, hosted branch rules, full browser/UI exercise, and a recovery drill remain open. A passing core suite or source manifest does not close those gaps.
