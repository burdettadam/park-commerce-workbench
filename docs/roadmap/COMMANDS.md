# Maintained commands and verification boundaries

This is a catalog, not a launcher. Commands execute only when a person or authorized agent invokes them. New goal-specific harnesses must be implemented and reviewed in their goals.

| ID | Command | State / prerequisite |
| --- | --- | --- |
| `INIT_VERSION` | `git agent --version` | **existing after installation/PATH setup**. Reviewed local initializer must be discoverable in Git Bash; manually verify exact 0.2.0. |
| `INIT_PREVIEW` | `bash scripts/init-agent.sh --preview` | **existing**. Requires the intended Git worktree and exact reviewed initializer; current guard already rejects look-alike versions. |
| `INIT_APPLY` | `bash scripts/init-agent.sh --apply` | **existing**. Reruns the initializer after preview review; inspect any proposal/conflict before adoption. |
| `CORE` | `bash scripts/test-core.sh --demo` | **existing**. JDK 21 and Bash; in-memory domain adapters only. |
| `STATE_TESTS` | `node --test storefront/src/app/checkout-state.test.mjs` | **existing**. Existing dependency-free client-state tests, not Angular browser tests. |
| `TOOLING` | `bash scripts/test-tooling.sh` | **existing**. Disposable fixtures/mock initializer only; not real upstream adoption. |
| `JAVA` | `bash scripts/verify.sh --java` | **existing**. Maven/JDK/repository access required; does NOT execute *IT suites. |
| `INTEGRATION` | `bash scripts/verify.sh --integration` | **existing**. Maven/JDK/Docker required; real database tests must not silently skip. |
| `CONFIGURE` | `bash scripts/configure-local.sh` | **existing**. Generates local secrets; refuses existing .env; do not log or commit secrets. |
| `COMPOSE_CHECK` | `docker compose config --quiet` | **existing**. Validate without printing rendered secrets; runtime still unverified. |
| `COMPOSE_UP` | `docker compose up --build -d --wait` | **existing**. Local containers/downloads; own project/ports/volumes required. |
| `COMPOSE_DOWN` | `docker compose down` | **existing**. Stops project without intentionally deleting named volumes; never add -v casually. |
| `HTTP` | `node scripts/demo-http.mjs` | **existing**. Requires running local services and .env; creates synthetic data. |
| `UI_BUILD` | `mvn -B -ntp -Pui -pl admin-console -am verify` | **proposed_validation**. Uses existing Maven profile; must be executed and repaired in G-0004. Do not skip required tests. |
| `UI_RUN` | `mvn -Pui -pl admin-console spring-boot:run` | **existing**. Requires installed sibling modules/running APIs as documented in START-HERE. |
| `ANGULAR_FIRST_BUILD` | `(cd storefront && npm install && npm run build)` | **historical/bootstrap**. The committed lockfile now exists; use `npm ci` for clean reproduction. |
| `ANGULAR_LOCKED_BUILD` | `(cd storefront && npm ci && npm run build)` | **existing**. Node 24 baseline; committed lockfile and hosted CI have exercised this command. |
| `ANGULAR_RUN` | `(cd storefront && npm run start)` | **existing**. Real services and reviewed dependencies required; loopback development server. |
| `ROADMAP_LINT` | `node scripts/validate-roadmap.mjs` | **added_by_this_delivery**. Read-only structural checks; no execution or adoption of goals. |
| `ROADMAP_TESTS` | `node --test scripts/validate-roadmap.test.mjs scripts/validate-evidence.test.mjs` | **added_by_this_delivery**. Offline validator fixtures only; no product/framework tests or actual reviewers. |
| `EVIDENCE_CHECK` | `node scripts/validate-evidence.mjs PATH_TO_CANDIDATE_MANIFEST` | **added_by_this_delivery**. Replace path with actual collected manifest; rejects unfilled templates; does not grant approval. |

No unimplemented `git agent work`, `git work-goal`, test script or deployment command is invented here. `$work-goal` is the existing native skill invocation, not a Bash command. Use the actual initializer/native discovery.
