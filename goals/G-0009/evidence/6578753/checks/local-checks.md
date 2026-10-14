# S6 local checks and retained prior failed attempt

Fixed source S `6578753c94674d95793f3ffd82ad7e67d9e98c4e`, clean at check start. Process-local `JAVA_HOME`/PATH selected Microsoft JDK 21. Prior S5 first-attempt Java 17 setup failure remains at `%TEMP%/g0009-fixed-00fe6be/` with hashes in its S5 evidence; it is not counted as a pass here. S6 checks passed:

| Check | UTC interval, 2026-10-14 | Exit | Selected result |
| --- | --- | --- | --- |
| `node --test scripts/pricing-contract.test.mjs` | 03:18:08–03:18:10 | 0 | 29 pass, 0 fail/skip; checked-in raw text `node-fixtures-1.txt` |
| `C:/Program Files/Git/bin/bash.exe scripts/test-core.sh --demo` | 03:18:10–03:18:12 | 0 | 280 pass, 0 fail; checked-in raw text `core-demo-1.txt` |
| `mvn -B -ntp -Pintegration -pl pricing-service -am '-Dit.test=QuoteContractIT' '-Dfailsafe.failIfNoSpecifiedTests=false' verify` | 03:18:17–03:18:48 | 0 | QuoteContractIT 16/16, zero skips; `pricing-junit-summary.txt` |
| `mvn -B -ntp -Pintegration -pl booking-service -am '-Dit.test=CheckoutRecoveryIT' '-Dfailsafe.failIfNoSpecifiedTests=false' verify` | 03:19:00–03:19:26 | 0 | CheckoutRecoveryIT 8/8, zero skips; `booking-junit-summary.txt` |
| `node --test scripts/validate-roadmap.test.mjs scripts/validate-evidence.test.mjs` | before source freeze | 0 | 66 selected, 65 pass, 1 Windows symlink skip, 0 fail |
| `node scripts/validate-roadmap.mjs` | before source freeze | 0 | 10 milestones, 30 goals, 120 criteria, 90 artifact requirements |

Full local pricing/booking Maven logs remain only at the same workstation TEMP path, SHA-256 `c3feae878eb2bfacf4e7046c98880a1fddfd83727c40afd7bab94f459562aa4d` and `5a70faae82b8cc382f2ec11f32422059f2a0a77693baa5306ee718778a6b4b80` respectively. They are verbose framework logs, so the public bundle retains generated test-case summaries and digests instead. PowerShell Node/core UTF-16LE logs were transcoded to UTF-8 with LF line endings for checked-in raw text; original bytes SHA-256 `be10a8162178dc72354ad02fcd5d8f27b95a30996290b3797fa077979667b3df` and `7a5ef1a5617bb8b49fe2e061ccc5205e8ffb2dc7fee617d9109f6ca542170caa`. Prior S1 interrupted Docker check remains under `%TEMP%/g0009-fixed-8ff55d0/`; prior S2/S3/S5 checks remain in their own evidence directories. Only standard free public CI is used.
