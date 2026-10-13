# Local fixed-source checks and retained attempts

Repository root: `park-commerce-workbench/.tmp/g0009-planning`. All listed final checks began with a clean `goal/G-0009` worktree at S `af04615957d172e6ad145baa7ba7858584b9a4f2`. JDK 21, Maven 3.9.16, Node 24.14.1, Git Bash and Docker Desktop Testcontainers were process-local tools. No paid runner or deployment was used.

| Check | UTC interval on 2026-10-13 | Exit | Observed |
| --- | --- | --- | --- |
| `node --test scripts/pricing-contract.test.mjs` | 23:02:10–23:02:11 | 0 | 29 pass, 0 fail/skip; raw output `node-fixtures-1.txt` |
| `C:/Program Files/Git/bin/bash.exe scripts/test-core.sh --demo` | 23:02:11–23:02:14 | 0 | 280 pass, 0 fail; raw output `core-demo-1.txt` |
| `mvn -B -ntp -Pintegration -pl pricing-service -am '-Dit.test=QuoteContractIT' '-Dfailsafe.failIfNoSpecifiedTests=false' verify` | 23:02:19–23:02:44 | 0 | `QuoteContractIT` 16/16 and selected adjacent suites passed; `pricing-junit-summary.txt` |
| `mvn -B -ntp -Pintegration -pl booking-service -am '-Dit.test=CheckoutRecoveryIT' '-Dfailsafe.failIfNoSpecifiedTests=false' verify` | 23:02:50–23:03:11 | 0 | `CheckoutRecoveryIT` 8/8 and selected adjacent suites passed; `booking-junit-summary.txt` |

The full local Maven stdout/stderr logs are retained only on the workstation under `%TEMP%/g0009-fixed-af04615/`: `pricing-it-1.log` SHA-256 `b3fd7a0cad5273d71f1f13f8ff9387b1126ec5432242b42fd5692cf29ccc4532`; `booking-it-1.log` SHA-256 `ce0891339c8121fac03ab1f3e578d9019c2a56591d02cca44a637b357ad4d08f`. They include verbose framework/debug output and are classified local-only rather than copied to the public repo. The Maven-generated test-case names/counts and full-log digests are retained in the checked-in summaries. The public hosted run supplies off-device full integration evidence.

Prior S `8ff55d01ed8fff68e29065de5323ca12fae9badb` had an exploratory full local `scripts/verify.sh --integration` attempt. It passed pricing and proceeded into booking, then stopped progressing while Testcontainers waited for a booking MySQL connection. The owned process was interrupted; raw output remains at `%TEMP%/g0009-fixed-8ff55d0/integration-1.log` with no fabricated exit/pass count. This result is neither a fixed-S check nor a full local pass. Its hosted PR-head run `38481228771` passed but is also prior-source evidence.
