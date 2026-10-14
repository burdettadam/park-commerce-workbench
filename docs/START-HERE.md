> **Admission-checkout update:** dated entitlement sets, owner-bound confirmation/recovery, and additive SQL migration are included. See [contract](project/admission-checkout.md), [candidate verification](changes/admission-checkout/VERIFICATION.md), and [G-0005 database evidence](https://github.com/burdettadam/park-commerce-workbench/blob/90ae8eff6485c950c8d02c8d3a33893059f5d82e/goals/G-0005/evidence/1dd1ef2/database-tests.md). The guest browser flow below covers an event, not admission checkout.

> **Pricing-contract update:** single-instant quote evaluation, read-only expiry status, strict shared parsers and independent cents fixtures. See [contract](project/pricing-contract.md) and [evidence](changes/pricing-contract/README.md). Framework execution and independent acceptance remain pending.

> **Catalog-publication update:** immutable drafts, exact-version publishing, and stable audit history are now implemented as a local candidate. See [the contract](project/catalog-publication.md) and [candidate evidence](changes/catalog-publication/README.md).

> **Checkout-recovery update:** owner-bound same-tab retry metadata and server checkout reconciliation are now implemented. See [the recovery contract](project/checkout-recovery.md) and [candidate evidence](changes/checkout-recovery/README.md). Local validation still requires your own services and browser; the G-0006 goal records an executed event purchase on Windows.

# Start here

## 1. Extract and review

This repository contains source and local development guidance. A clone does not include `.env`, Maven caches, application binaries, `node_modules`, or an external `git-agent` executable.

Clone the repository and work from its root. Create a development branch for changes. The source, guidance, and checks are already present in this checkout.

## 2. Run the checks that need no downloaded Java libraries

Requirements: JDK 21 (both `java` and `javac`) and Bash. On Windows use Git Bash with JDK 21 on PATH.

```bash
bash scripts/test-core.sh --demo
```

Expected candidate result: **280 passed, 0 failed** plus a walkthrough where a $100 quote stays $100 after publication of a $125 rate, checkout retries return the same order, and capacity is consumed once. The stores in this walkthrough are deliberately **test-only in-memory adapters**.

With Node installed:

```bash
node --test storefront/src/app/*.test.mjs
```

Expected: **66 passed** (10 original state tests and 56 recovery tests). These exercise the coordinator, storage fixtures and transport contracts, not Angular rendering or an actual browser reload.

For the independent Java/pricing fixture suite (JDK 21 and Node, no libraries/network):

```bash
node --test scripts/pricing-contract.test.mjs
```

Expected: **29 passed**. This includes 22 literal examples, 384 deterministic
input comparisons across both Java pricing paths, and source-only schema checks.
Do not confuse these observations with Spring/GraphQL engine or database execution.

## 3. Initialize your agent development environment

Use [the 10-milestone roadmap](roadmap/README.md) and [first-goal instructions](roadmap/START-FIRST-GOAL.md). Verify your reviewed initializer version before use. The wrapper's substring-match defect has already been repaired in the starter-code candidate; G-0002 remains a historical proposal whose original failing test commit is retained in the bundle, not an instruction to reintroduce that defect. The roadmap does not itself initialize native guidance or approve goals.


Follow [agent-handoff.md](agent-handoff.md). The short sequence, from the root of a local Git checkout with your reviewed `git-agent` installed, is:

```bash
bash scripts/init-agent.sh --preview
bash scripts/init-agent.sh --apply
git status --short
```

Review the generated adoption record and governance before treating it as accepted. The helper deliberately refuses a non-repository or a project nested inside some other worktree. It does not install or copy the private tool, commit, push, or run models.

## 4. Generate local secrets and validate Compose

Requirements: Docker with Compose v2, registry access, and sufficient disk/memory for three services, two MySQL instances, and MongoDB. All host ports bind to loopback.

On Windows, use Git for Windows Bash explicitly from PowerShell (`& "$env:ProgramFiles\Git\bin\bash.exe" scripts/configure-local.sh`). Plain `bash` may launch WSL instead. The generated `.env` receives an explicit Windows ACL for the current account; on Unix it uses mode 600. Keep `.env` local and ignored.

```bash
bash scripts/configure-local.sh
docker compose config --quiet
docker compose up --build -d --wait
```

`.env` contains random development credentials and a unique Compose project name. The script refuses to overwrite it. Keep it private. Compose uses it automatically. When running two worktrees, edit each `.env` to give it unused service/database ports; unique volume names alone do not prevent port collisions.

Docker image builds require network access to Maven repositories. A build or readiness failure is a **failed/unverified setup**, not something to skip. This imported checkout built and started the local stack on 2026-10-12; see [import verification](import-verification-2026-10-12.md).

Inspect:

```bash
docker compose ps
docker compose logs --tail=100 catalog pricing booking
```

Avoid posting logs containing credentials or personal information. Database initialization scripts apply to new volumes. Changing a password in `.env` does not change the credentials inside an already initialized database volume.

## 5. Run the actual three-service exercise

Requirements: running Compose stack plus Node 24 recommended. The script reads `.env`; it never prints token values and only connects to localhost ports.

```bash
node scripts/demo-http.mjs
```

This creates uniquely named synthetic products/events, checks role access and quote ownership, exercises GraphQL pricing, publishes a price change, confirms a protected-price order, repeats checkout, and races ten HTTP holds for the two remaining tickets. It prints IDs you can inspect. It does **not** delete your existing data.

The extra holds expire after five minutes; the confirmed order remains sold. The imported checkout passed 43 HTTP assertions on 2026-10-12; see [import verification](import-verification-2026-10-12.md). The script reports the current actual count, including admission API checks added later. The original archive delivery did not execute this runtime exercise. Report any future failure rather than treating core test results as a substitute.

## 6. Compile and run database integration tests

On a host with JDK 21, Maven 3.9+, Docker, and registry/repository access:

```bash
bash scripts/verify.sh --java
bash scripts/verify.sh --integration
```

`--java` compiles the backend reactor and runs the core JUnit suite. It also compiles integration-test source, but **does not execute `*IT` classes**.

`--integration` starts from a clean Maven build, enables Failsafe, and requires every selected MongoDB/MySQL `*IT` suite to report executed tests with no skips, failures or errors. The current seven suites contain 67 tests; the command prints the actual per-suite counts. Each Testcontainers suite owns disposable test databases; it does not require the Compose stack. Docker absence is an error, not an automatic skip. Results go to each module's `target/surefire-reports` and `target/failsafe-reports`.

On Windows, select JDK 21 and Maven 3.9+ in the current PowerShell terminal before Maven or Node tests that call `javac`. This leaves other projects' toolchains alone:

```powershell
$env:JAVA_HOME = 'C:\path\to\jdk-21'
$env:Path = "$env:JAVA_HOME\bin;C:\path\to\apache-maven-3.9.x\bin;$env:Path"
java -version
javac -version
mvn -version
mvn -B -ntp clean verify
```

Install the JDK and Maven from their publishers and verify their archive checksums. G-0004's [Windows investigation](../goals/G-0004/work/build-investigation.md) records a working Microsoft OpenJDK 21.0.12.1 and Maven 3.9.16 pair. Run `clean` once after switching from an older JDK or build checkout so stale `target` classes cannot affect the result. Use Git for Windows Bash for the repository's shell scripts; `bash` in a default Windows PowerShell may point to WSL.

## 7. Start the Angular guest client

With Node **24** and npm on the workstation, while the three APIs are running:

```bash
cd storefront
npm ci
npm run build
npm run start
```

Visit `http://127.0.0.1:4200`. Paste **DEMO_GUEST_TOKEN** from `.env` into the password field and click **Authenticate and recover saved checkout**. Load products, select a visit date inside the published price window, create a quote, and reserve the matching event occurrence ID. The ID is separate from the product ID; obtain it from the employee console or HTTP demo output.

The application sends quotes through GraphQL and booking commands through REST. Tokens stay in memory. Only the schema version, authenticated subject, hold ID and retry key are retained in **sessionStorage**. After a reload, reenter the token and authenticate; the server reports whether the original hold is active, confirmed or expired. Checking status never confirms a purchase. A timeout preserves the original key. Form edits and new purchases stay blocked until the checkout is resolved and explicitly cleared. Closing the tab or clearing site data is not a supported recovery guarantee. See [the lifecycle and limits](project/checkout-recovery.md).

The checkout includes `storefront/package-lock.json` for repeatable installs. The Angular build and type check have passed locally and in hosted CI. The development proxy uses ports 8081/8082/8083 unless you set `CATALOG_PORT`, `PRICING_PORT`, and `BOOKING_PORT` in its process. For a second isolated worktree, assign unused ports in its `.env`, then set those three variables in the Angular process. For example, from `storefront` in PowerShell after assigning API ports 8381/8382/8383:

```powershell
$env:CATALOG_PORT = '8381'
$env:PRICING_PORT = '8382'
$env:BOOKING_PORT = '8383'
node node_modules/@angular/cli/bin/ng.js serve --host 127.0.0.1 --port 4300 --proxy-config proxy.conf.cjs
```

Verify that both the Angular port and API ports are unused first. This example is local only; stop only the process you started.

## 8. Start the Vaadin employee client

With JDK 21, Maven 3.9+, Node 24, and npm:

```bash
mvn -B -ntp -Pui,production -pl admin-console -am clean install
mvn -Pui -pl admin-console spring-boot:run
```

Visit `http://127.0.0.1:8080` and paste **DEMO_EMPLOYEE_TOKEN** from `.env`. Publish product revision 1, price version 1, then an event occurrence. Subsequent publication requires the next revision/version.

The console can preview **unpublished** candidate prices without issuing a quote or publishing the rates. Vaadin Java compilation and a forced production frontend build passed locally in G-0004. G-0006 exercises the real browser path. For the isolated API ports above, the built console can run on another free loopback port with process arguments:

```powershell
java -jar admin-console/target/admin.jar --server.port=8480 --park.catalog-url=http://127.0.0.1:8381 --park.pricing-url=http://127.0.0.1:8382 --park.booking-url=http://127.0.0.1:8383
```

This local-only console has no production login flow. It holds the entered token in its Vaadin view/session memory and forwards it to the API; the API enforces the employee role. The session expires after fifteen minutes of inactivity. It does not persist tokens to a database or file.

The optional UI profile keeps Vaadin's frontend toolchain out of the three backend builds. The committed `admin-console/package.json` and `package-lock.json` came from Vaadin's generated inputs. The `production` profile forces a fresh frontend bundle and uses `npm ci`; a package/lock mismatch fails the build. Do not substitute a precompiled-bundle success for this clean build check. The generated bundle is ignored; browser behavior remains a separate verification task from compilation.

## 9. Stop without destroying data

```bash
docker compose down
```

Named database volumes remain. **`docker compose down -v` destroys this Compose project's database volumes**; use it only for an intentional disposable-data reset, not as a routine troubleshooting command. No reset is performed automatically.

## Before any remote push

Review `git diff`, ignored files, and the agent adoption proposals. Keep generated `.env` files and credentials out of Git. Verify the target remote and its visibility before pushing.
