# G-0008 implementation slice

Writer: G-0008 pricing subagent. Branch: `goal/G-0008-implementation`.

## Scope and source of truth

The existing `Pricing.Service.calculate` serves both unpublished preview and issued quotes. This slice preserves that shared calculation and the existing candidate fingerprint. It changes only the preview HTTP error presentation and its contract test: Jackson wraps `Failure` thrown by validated request records, and the generic malformed-request response hid the actionable overlap and party messages required by AC03. `ApiErrors` now unwraps only `INVALID_INPUT` failures on the preview route; it never echoes an arbitrary Jackson exception or payload. Other routes retain their existing generic malformed-request response.

## Exploratory checks and retained failed attempts

- From this worktree, `bash scripts/test-core.sh --demo` first exited 1 before tests: the default shell selected JDK 17, so `javac --release 21` was unavailable. `mvn -B -ntp -pl pricing-service -am -DskipTests package` also exited 1 because Maven was absent from the default PowerShell `PATH`.
- Selecting the documented process-local Microsoft JDK 21.0.12.1 and Apache Maven 3.9.16 under `%LOCALAPPDATA%/park-commerce-workbench/toolchains` fixed the setup. Git Bash `scripts/test-core.sh --demo` exited 0 with 280 passed, 0 failed; the demo is in-memory only.
- With the same toolchain, `mvn -B -ntp -pl pricing-service -am -DskipTests package` exited 0 and compiled the changed production and test sources. Tests were explicitly skipped by this compile check.
- `mvn -B -ntp -Pintegration -pl pricing-service -am verify` began at approximately 2026-10-13 16:02:20 America/Denver and reached Testcontainers on Docker Desktop 29.7.2. It created an owned Ryuk container `eedbdaeacb3f` and owned `mysql:8.4.11` container `8a91a2d1596e`, then stalled waiting for MySQL at `jdbc:mysql://localhost:49511/test`. The writer interrupted only that Maven process before 16:05:27; exit 1. A subsequent read-only `docker ps` also stalled and was interrupted. No database result is claimed. Container cleanup status is unknown while the Docker CLI is unresponsive; do not delete unrelated containers or assume the engine cleaned these up.

The coordination-only canonical update `4628f1c4b7fda83dd1acf6e13a158457618cc6f9` was merged into this branch at `06d91b55d3d92165cab7107dd78d9c28c6c640ff`. The clean implementation source checkpoint is `b749533c14da1d99cb652cfd5bf5dcebaf158fa1` (base plan `363fa8a4b1bc0c1487b9379fc110e465ebe77d62`). On that clean source, process-local JDK 21/Maven 3.9.16 `mvn -B -ntp -pl pricing-service -am test` exited 0 at 2026-10-13 16:06:40 America/Denver: core 280/280 and `PreviewErrorContractTest` 3/3, no failures/errors/skips. `bash scripts/test-core.sh --demo` exited 0 on the same clean source, 280 passed and 0 failed. The direct adapter tests cover safe domain details, a fixed repair hint for unknown conversion errors, and preservation of generic errors on other routes; the real HTTP test remains unexecuted locally because of the Docker hang.

The integration owner should rerun acceptance checks on a fixed combined candidate and retain complete raw output. The Docker attempt is a gap, not a pass.

## Next action

The integration owner fast-forwarded this implementation into canonical `goal/G-0008` and opened draft PR #12. At PR head `ba2988172ccf6d216641847e639dc095d1214382`, free public CI run `38475759454` passed all three jobs. The Java job (1m57s) reported in-memory core 280/280, Node pricing contract 29/29, Maven core 280/280, `PreviewErrorContractTest` 3/3, and real Testcontainers integration 67/67 with zero failures, errors or skips. The integration suites were `CatalogEditorialIT` 8, `MongoCatalogIT` 9, `JpaPricingIT` 8, `QuoteContractIT` 12, `AdmissionCheckoutIT` 15, `CheckoutRecoveryIT` 7, and `JdbcBookingIT` 8. `CheckIntegrationReports` confirmed selected/executed counts and zero skips. CI uploaded `java-test-reports` artifact `11854430521`.

This hosted run resolves the local Docker gap for these automated checks. It does not exercise the six-service HTTP demo, a browser UI, or live cross-service catalog during `JpaPricingIT`; that test uses a fixed catalog client double. Retain the interrupted local run above as failed exploratory evidence. Acceptance fixtures, side-effect evidence, independent reviews and intended-use validation remain open.

## Hosted live-stack check follow-up

After the local Docker hang, the writer added one `live-stack` job to the existing public candidate workflow. It uses the free standard `ubuntu-latest` runner, the repository's existing `configure-local.sh` and `compose.yml`, and `node scripts/demo-http.mjs`. The Compose project name includes GitHub's run ID and attempt. The job emits only fixed `PASS` labels and the final count from the live demo, withholding raw HTTP response details and removing its runner-local report. An always-run step targets only that Compose project with `down --volumes --remove-orphans` and removes `.env`; per-step timeouts leave time for cleanup before the job timeout. A runner or Docker failure may still prevent cleanup, so no cleanup success is claimed before execution.

Local static checks: Python YAML parsing found `live-stack` alongside the existing three jobs; `node scripts/validate-roadmap.mjs` exited 0 with 10 milestones, 30 goals, 120 criteria and 90 artifact requirements; `git diff --check` was clean. The live-stack job itself has not run yet. The integration owner must include this new source in a combined candidate and inspect hosted output before crediting cross-service evidence.

## Round-one UI error finding and correction

The adversarial reviewer flagged AC03 because `CommerceView.action` hid preview HTTP 400 details. The suggested combined 21-ticket case is locally rejected by `Pricing.Request` before HTTP with `A party must have between 1 and 20 tickets`; the UI also constructs only one rate per variant, so overlap cannot arise from that form. Those examples do not reproduce the remote error path. A past `DatePicker` visit is allowed by the form and `Pricing.Request`, then `Pricing.Service.checkDate` returns HTTP 400 `INVALID_INPUT` with `Visit date is in the past in the park time zone`; the old generic catch hid it. A catalog revision that lacks one of the form's variants can similarly return the fixed invalid-variant detail.

The assigned implementation now catches only preview `RestClientResponseException` at the preview action, parses a bounded JSON problem body, displays one of those two known harmless `INVALID_INPUT` details, and gives a generic fallback for malformed, unexpected, or other-status responses. It never prints arbitrary server text. Local 21-ticket validation keeps its existing `Failure` display. `CommerceViewPreviewErrorTest` directly exercises both known remote details, local 21-ticket rejection, untrusted/malformed body suppression and non-400 fallback. No browser or live Vaadin instance was used for this correction.

The first attempted `mvn -pl admin-console -am test` exited 1 during project selection because `admin-console` is part of the `ui` profile; no tests ran. With process-local JDK 21/Maven 3.9.16, `mvn -B -ntp -Pui -pl admin-console -am test` exited 0 at 2026-10-13 16:26:52 America/Denver: core 280/280 and admin helper 3/3, no failures/errors/skips. This was exploratory before committing the correction. The canonical evidence checkpoint `512ea881cbd42fbb3f227a8e575b261b0867212f` was fast-forwarded into this branch before editing.
