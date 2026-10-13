# G-0004-A01: fixed-source build and suite results

Source S: `e7c1e7f309bd976b6bb92e575374c1bb1add316d`, clean at the start of verification. Accepted plan P: `5ec839681f563b4267a912e66f5969c1f1d9ac24`. Base: `d7ed805158bd6a239480ce7ae03312c139f2acc7`.

| Environment and command | Observed result | Retained output |
| --- | --- | --- |
| Hosted Ubuntu, Java 21/Node 24, `bash scripts/test-core.sh`; `node --test scripts/pricing-contract.test.mjs`; `bash scripts/verify.sh --integration` | All steps exited 0. Core 280/280, pricing 29/29, integration 64/64 (17 catalog, 19 pricing, 28 booking); no failed or skipped tests. Six-module Maven reactor `BUILD SUCCESS`. | [Java CI execution](checks/ci-java.txt), [hosted run](https://github.com/burdettadam/park-commerce-workbench/actions/runs/38336113859) |
| Hosted Ubuntu, `mvn -B -ntp -Pui,production -pl admin-console -am verify`; tracked frontend diff; `npm audit --audit-level=high` | All steps exited 0. Core 280/280, optional Java module compiled, Vaadin ran `npm ci` and Vite, Maven `BUILD SUCCESS`, manifest/lock unchanged, npm audit found 0 vulnerabilities. The admin module has no application tests; the 280 tests belong to core. | [UI CI execution](checks/ci-admin-ui.txt) |
| Hosted Ubuntu, `cd storefront && npm ci && npm audit --audit-level=high && npm run test:types && npm run build`; client tests | All steps exited 0. Fresh npm install added 406 packages; Angular production bundle completed; strict type check passed; client state 66/66; 0 failures or skips; audit found 0 vulnerabilities. Roadmap validator tests 47/47 also passed. | [Client CI execution](checks/ci-client.txt) |
| Windows 11, process-scoped Microsoft OpenJDK 21.0.12.1/Apache Maven 3.9.16, Git Bash `scripts/test-core.sh --demo` and `scripts/test-tooling.sh` | Core demo 280/280, tooling 29/29, each exit 0. Tooling initializer exercises use a mock. | [Core demo](checks/core-demo.txt), [tooling](checks/tooling.txt) |

The initial JDK 17 attempt and stale Windows `target` artifacts were setup failures; a clean JDK 21 Windows Maven reactor and forced Vaadin production build passed during investigation. [Investigation](../../work/build-investigation.md) retains those observations. CI on S is the fixed-source acceptance result. The UI and client jobs completed 2026-10-13 02:17 UTC; Java completed 02:19 UTC. Hosted logs in this evidence directory contain only the command execution steps, with ephemeral generated Spring passwords redacted; the complete original logs remain in the linked run and local ignored cache.

Build success does not establish browser behavior, production security, deployment, or a version release. Those are outside this goal.
