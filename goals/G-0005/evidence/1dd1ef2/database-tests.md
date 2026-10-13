# G-0005-A01 — database suite selection and execution

Fixed source S `1dd1ef28d3d5db6bcd4e47255c074ed6205c1627`, accepted plan P `30ffac8304f0ddf40a4845bb19e32393b1f0a516`. A fresh clean public clone ran `bash scripts/verify.sh --integration` in the Docker-hosted JDK 21/Maven 3.9.11 runner. Exit 0, completed 2026-10-13T03:34:30Z. The command performs Maven `clean verify`, then reads each selected Failsafe XML report; missing, empty, skipped, failed or errored suites fail the command. Its retained [command excerpt](checks/integration-summary.txt) and the seven unmodified Failsafe text reports below show actual counts.

| Module / selected suite | Executed | Skipped | Raw Failsafe report |
| --- | ---: | ---: | --- |
| catalog / CatalogEditorialIT | 8 | 0 | [report](checks/dev.parkcommerce.catalog.CatalogEditorialIT.txt) |
| catalog / MongoCatalogIT | 9 | 0 | [report](checks/dev.parkcommerce.catalog.MongoCatalogIT.txt) |
| pricing / JpaPricingIT | 8 | 0 | [report](checks/dev.parkcommerce.pricing.JpaPricingIT.txt) |
| pricing / QuoteContractIT | 12 | 0 | [report](checks/dev.parkcommerce.pricing.QuoteContractIT.txt) |
| booking / AdmissionCheckoutIT | 15 | 0 | [report](checks/dev.parkcommerce.booking.AdmissionCheckoutIT.txt) |
| booking / CheckoutRecoveryIT | 7 | 0 | [report](checks/dev.parkcommerce.booking.CheckoutRecoveryIT.txt) |
| booking / JdbcBookingIT | 8 | 0 | [report](checks/dev.parkcommerce.booking.JdbcBookingIT.txt) |
| **Total** | **67** | **0** | 0 failures and 0 errors |

The seven baseline classes were present at P and are mandatory in `scripts/CheckIntegrationReports.java`; the checker also discovers newly added `*IT.java` classes in these modules. [Hosted CI run 38342100003](https://github.com/burdettadam/park-commerce-workbench/actions/runs/38342100003) independently passed 67/67 in its Java job, with the exact per-suite lines retained in [CI excerpt](checks/ci-java-summary.txt). The current count is measured, not inherited from the old 64-test foundation.

The deliberate `MAVEN_ARGS=-DskipITs` run built successfully but the required command exited 1 when the first selected report was missing. A separate synthetic partial-skip challenge changed one disposable report to `skipped=1`; the checker exited 1. These are gate challenges, not candidate acceptance passes; [fail-closed evidence](checks/fail-closed.txt) records them.
