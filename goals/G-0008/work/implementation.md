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

Integrate the coordination-only `goal/G-0008` update, commit and push this slice, then hand the exact source checkpoint and remaining AC01–AC04 verification gap to the integration owner.
