> **Admission-checkout update:** dated entitlement sets, owner-bound confirmation/recovery, and additive SQL migration are included as a local candidate. See [contract](project/admission-checkout.md) and [verification](changes/admission-checkout/VERIFICATION.md). No admission browser UI or database runtime acceptance is claimed.

> **Pricing-contract update:** single-instant quote evaluation, read-only expiry status, strict shared parsers and independent cents fixtures. See [contract](project/pricing-contract.md) and [evidence](changes/pricing-contract/README.md). Framework execution and independent acceptance remain pending.

> **Catalog-publication update:** immutable drafts, exact-version publishing, and stable audit history are now implemented as a local candidate. See [the contract](project/catalog-publication.md) and [candidate evidence](changes/catalog-publication/README.md).

> **Checkout-recovery update:** owner-bound same-tab retry metadata and server checkout reconciliation are now implemented. See [the recovery contract](project/checkout-recovery.md) and [candidate evidence](changes/checkout-recovery/README.md). Full Spring, database and browser validation still need your workstation.

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

The extra holds expire after five minutes; the confirmed order remains sold. The imported checkout passed 43 HTTP assertions on 2026-10-12; see [import verification](import-verification-2026-10-12.md). The original archive delivery did not execute this runtime exercise. Report any future failure rather than treating core test results as a substitute.

## 6. Compile and run database integration tests

On a host with JDK 21, Maven 3.9+, Docker, and registry/repository access:

```bash
bash scripts/verify.sh --java
bash scripts/verify.sh --integration
```

`--java` compiles the backend reactor and runs the JUnit wrapper over the 180 core scenarios. It also compiles integration-test source, but **does not execute `*IT` classes**.

`--integration` enables Maven Failsafe and runs the 64 MongoDB/MySQL integration tests as well. Each Testcontainers suite owns disposable test databases; it does not require the Compose stack. Docker absence is an error, not an automatic skip. Results go to each module's `target/surefire-reports` and `target/failsafe-reports`.

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

The checkout includes `storefront/package-lock.json` for repeatable installs. The Angular build and type check have passed locally and in hosted CI; browser behavior remains unverified. The development proxy uses ports 8081/8082/8083 unless you set `CATALOG_PORT`, `PRICING_PORT`, and `BOOKING_PORT` in its process.

## 8. Start the Vaadin employee client

With JDK 21, Maven 3.9+, Node 24, and npm 11.3+:

```bash
mvn -B -ntp -Pui -pl admin-console -am install -DskipTests
mvn -Pui -pl admin-console spring-boot:run
```

Visit `http://127.0.0.1:8080` and paste **DEMO_EMPLOYEE_TOKEN** from `.env`. Publish product revision 1, price version 1, then an event occurrence. Subsequent publication requires the next revision/version.

The console can preview **unpublished** candidate prices without issuing a quote or publishing the rates. Full Vaadin execution remains unverified. This local-only console has no production login flow. It holds the entered token in its Vaadin view/session memory and forwards it to the API; the API enforces the employee role. The session expires after fifteen minutes of inactivity. It does not persist tokens to a database or file.

The optional UI profile avoids making Vaadin's frontend toolchain a prerequisite for the three backend builds. `-DskipTests` above is solely for the optional console build, not release acceptance. The Vaadin source has not been compiled or browser-tested here.

## 9. Stop without destroying data

```bash
docker compose down
```

Named database volumes remain. **`docker compose down -v` destroys this Compose project's database volumes**; use it only for an intentional disposable-data reset, not as a routine troubleshooting command. No reset is performed automatically.

## Before any remote push

Review `git diff`, ignored files, and the agent adoption proposals. Keep generated `.env` files and credentials out of Git. Verify the target remote and its visibility before pushing.
