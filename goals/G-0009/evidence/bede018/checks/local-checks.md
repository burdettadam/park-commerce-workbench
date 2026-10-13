# Local fixed-source checks and retained attempts

Repository root: `park-commerce-workbench/.tmp/g0009-planning`. The source was clean at the start of each check: S `bede01801a1c489982edbe688ac87f68c7ba7396`. JDK 21, Maven 3.9.16, Node 24.14.1, Git Bash, and Docker Desktop Testcontainers were local tools. No paid runner or deployment was used.

| Check | UTC interval on 2026-10-13 | Exit | Observed |
| --- | --- | --- | --- |
| `node --test scripts/pricing-contract.test.mjs` | 23:10:59–23:11:01 | 0 | 29 pass, 0 fail/skip; raw output `node-fixtures-1.txt` |
| `C:/Program Files/Git/bin/bash.exe scripts/test-core.sh --demo` | 23:11:01–23:11:03 | 0 | 280 pass, 0 fail; raw output `core-demo-1.txt` |
| `mvn -B -ntp -Pintegration -pl pricing-service -am '-Dit.test=QuoteContractIT' '-Dfailsafe.failIfNoSpecifiedTests=false' verify` | 23:11:59–23:12:28 | 0 | `QuoteContractIT` 16/16 and adjacent suites passed; `pricing-junit-summary.txt` |
| `mvn -B -ntp -Pintegration -pl booking-service -am '-Dit.test=CheckoutRecoveryIT' '-Dfailsafe.failIfNoSpecifiedTests=false' verify` | 23:12:32–23:12:55 | 0 | `CheckoutRecoveryIT` 8/8 and adjacent suites passed; `booking-junit-summary.txt` |

Full local Maven stdout/stderr remain only on the workstation under `%TEMP%/g0009-fixed-bede018/`: pricing log SHA-256 `db5a8afdd5d5bcfbedfc8d1b81856d099f1b11a344a30154c0f373309a6a67f1`; booking log SHA-256 `51990840ce52fbd88cf0f42a967339d2807d2535248a5c74ed5340c8da4009d6`. Verbose framework/debug output is local-only. The checked-in summaries retain Maven-generated test names/counts and full-log digests; the public hosted run supplies off-device full integration evidence. PowerShell captured Node and core transcripts as UTF-16LE; the checked-in versions are exact text transcoded to UTF-8 with LF line endings. Original local byte digests are respectively `4f6e2b451321267c835fdfe290fc9eb08997a1a888400a4eb65e1a373e88ac70` and `7a5ef1a5617bb8b49fe2e061ccc5205e8ffb2dc7fee617d9109f6ca542170caa`.

Prior S1 `8ff55d01ed8fff68e29065de5323ca12fae9badb` had an exploratory full local `scripts/verify.sh --integration` attempt. It passed pricing and proceeded into booking, then stopped progressing while Testcontainers waited for a booking MySQL connection. The owned process was interrupted; raw output remains at `%TEMP%/g0009-fixed-8ff55d0/integration-1.log` with no fabricated exit/pass count. Its hosted PR-head run `38481228771` passed but is prior-source evidence. S2 `af04615957d172e6ad145baa7ba7858584b9a4f2` passed focused local and hosted checks; its blocked review record and raw outputs remain under `../af04615/`.
