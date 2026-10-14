# Local check attempts before fixed-source hosted CI

These are preserved attempts, not acceptance checks on fixed source S.

| Attempt | Environment/command | Actual result | Raw local artifact |
| --- | --- | --- | --- |
| Focused booking, first | Windows, Microsoft JDK 21, Maven 3.9.16, Docker Desktop; `mvn -B -ntp -Pintegration -pl booking-service -am -Dit.test=CheckoutRecoveryIT -Dfailsafe.failIfNoSpecifiedTests=false verify` | Exit 0, 12/12, zero skips; 100 HTTP holds produced ten accepted/90 SOLD_OUT | `%TEMP%/g0010-contention-first-run.txt` |
| Process interruption, first | Same toolchain, `-Dit.test=CheckoutRecoveryIT#killedApplicationAfterCommitBeforeClientResponseRecoversAfterRestart` | Exit 0, 1/1, zero skips; process kill/restart and SQL reconciliation passed | `%TEMP%/g0010-interruption-first-run.txt` |
| Focused booking after deterministic clock gate | Same focused class command | Exit 0, 13/13, zero skips; SHA-256 `a3462e5b8b10ae66143cdf9d347b36b0b056c1a6ec974be65926dc003a5e0e63` | `%TEMP%/g0010-contention-second-run.txt` |
| First broad integration | `C:/Program Files/Git/bin/bash.exe scripts/verify.sh --integration` | Exit 1; core 280, catalog 17, pricing 25 completed with zero skips, then Docker Desktop HTTP 500/MySQL connection timeout during booking; owned hung Failsafe fork stopped after identity check; SHA-256 `06ae644dfe89f2012b78ce2b391b8151b75829e1edc4650350b221f96a2cd143` | `%TEMP%/g0010-full-integration-first-run.txt` |
| Compilation after final marker changes | `mvn -B -ntp -pl booking-service -am -DskipTests package` | Exit 0; execution explicitly skipped, so no behavioral pass | `%TEMP%/g0010-compile-after-docker-failure.txt` |

The complete local logs remain on the writer workstation and are local-only, not a remote recovery claim. The later fixed-source public run `38508335182` passed the selected suite; its retained job output and JUnit summaries are in this evidence directory. A passing hosted run does not prove Docker Desktop recovered locally.
