# S5 local checks and retained failed setup attempt

Fixed source S `00fe6bec25ba8b9a2480bafb2371d349ec4ca546`, clean at check start. An initial Node/core attempt at 2026-10-14 03:12 UTC failed because PATH selected system Java 17: `javac` rejected `--release 21`. No product failure or acceptance result is inferred. Raw local logs and SHA-256: `node-fixtures-1.log` 7e804ef39552bb2fc3ecde103f3413e77b18ae96464a615f5252831eacdde7f1; `core-demo-1.log` 1e74a07fed9a8a111ba17ed4c6691ee2fba3bb04943732e3193f161bc5cfad32. Both remain at `%TEMP%/g0009-fixed-00fe6be/` and are not overwritten.

After process-local `JAVA_HOME`/PATH selected Microsoft JDK 21, these checks passed:

| Check | UTC interval, 2026-10-14 | Exit | Selected result |
| --- | --- | --- | --- |
| `node --test scripts/pricing-contract.test.mjs` | 03:12:28–03:12:30 | 0 | 29 pass, 0 fail/skip; checked-in raw text `node-fixtures-1.txt` |
| `C:/Program Files/Git/bin/bash.exe scripts/test-core.sh --demo` | 03:12:30–03:12:33 | 0 | 280 pass, 0 fail; checked-in raw text `core-demo-1.txt` |
| `mvn -B -ntp -Pintegration -pl pricing-service -am '-Dit.test=QuoteContractIT' '-Dfailsafe.failIfNoSpecifiedTests=false' verify` | 03:12:38–03:13:20 | 0 | QuoteContractIT 16/16, zero skips; `pricing-junit-summary.txt` |
| `mvn -B -ntp -Pintegration -pl booking-service -am '-Dit.test=CheckoutRecoveryIT' '-Dfailsafe.failIfNoSpecifiedTests=false' verify` | 03:13:25–03:13:50 | 0 | CheckoutRecoveryIT 8/8, zero skips; `booking-junit-summary.txt` |
| `node --test scripts/validate-roadmap.test.mjs scripts/validate-evidence.test.mjs` | before source freeze | 0 | 66 selected, 65 pass, 1 Windows symlink skip, 0 fail |
| `node scripts/validate-roadmap.mjs` | before source freeze | 0 | 10 milestones, 30 goals, 120 criteria, 90 artifact requirements |

Full local pricing/booking Maven logs remain only at the same workstation TEMP path, SHA-256 `952c634d21a87001d51a0933dc99ad2799d515cf5cc970991cad4c40e8aed2fb` and `80a8bfcd7b557d887163e9122d61f1c37d61b31c4deb468066d57b8e77ed72af` respectively. They are verbose framework logs, so the public bundle retains generated test-case summaries and digests instead. PowerShell Node/core UTF-16LE logs were transcoded to UTF-8 with LF line endings for checked-in raw text; original successful bytes SHA-256 `03369bea28be6389ae00136417782ddd75184160a0a61452bb85387972551d26` and `7a5ef1a5617bb8b49fe2e061ccc5205e8ffb2dc7fee617d9109f6ca542170caa`. Prior S1 interrupted Docker check remains under `%TEMP%/g0009-fixed-8ff55d0/`; prior S2/S3 checks remain in their own evidence directories. Only standard free public CI is used.
