> **Current candidate:** see [pricing-contract handoff](changes/pricing-contract/README.md) and [verification](changes/pricing-contract/VERIFICATION.md). The foundation observations below are retained as historical evidence.

# Verification record · 11 October 2026

**This is a locally tested source candidate, not a deployed or framework-verified application.** No remote repository change, release, cloud deployment, real payment, or actual `git-agent` initialization occurred during this delivery.

## Executed checks

| Check | Observed result | What it establishes |
| --- | --- | --- |
| `bash scripts/test-core.sh --demo` | **77 passed, 0 failed** | JDK 21 compilation and execution of the commerce core with test-only in-memory stores |
| Three additional complete core runs | **77 passed in each run** | Repeat execution, including concurrent reservations and checkout retries; not three new sets of distinct tests |
| `node --test storefront/src/app/checkout-state.test.mjs` | **10 passed, 0 failed** | Hold-bound retry key and client state behavior, independent of Angular |
| `bash scripts/test-tooling.sh` | **11 passed** | Local secret generation/preservation, Git-root guards, and invocation forwarding with a **mock initializer** |
| XML/JSON/YAML parsing, Bash/JavaScript syntax and selected path/privacy checks | **35 structural checks passed** | File syntax, Maven module paths, Docker artifact path consistency, loopback mappings, and absence of secrets/native agent scaffolding |

The final packaging check also extracts the ZIP into a path containing spaces and reruns the core, client-state, and tooling checks. See [archive verification](archive-verification.md) for its observed result. This verifies the extracted candidate's tested paths, not dependency resolution or production readiness.

## Retained raw evidence

- [Core results and walkthrough](evidence/core-tests.txt)
- [Client retry-state results](evidence/frontend-state-tests.txt)
- [Tooling results](evidence/tooling-tests.txt)
- [Structural checks](evidence/static-checks.txt)
- [Environment](evidence/environment.txt)
- [Repeat 1](evidence/core-repeat-1.txt), [repeat 2](evidence/core-repeat-2.txt), [repeat 3](evidence/core-repeat-3.txt)

These original observations describe the foundation archive; its source manifest is now also retained at `docs/roadmap/baseline/SOURCE-MANIFEST.sha256`. The new archive root manifest includes the roadmap additions and is not the identity of the original test run. The ZIP deliberately contains no Git administration/history. When importing it into your clone, record the actual resulting source commit alongside your own test results.

## Environment and verification limit

The executable checks used Linux, OpenJDK **21.0.12.1**, Bash **5.2.37**, Git **2.47.3**, and Node **22.16.0**. The UI build baseline remains Node **24**; the Node 22 results apply only to the dependency-free retry-state tests and JavaScript syntax checks.

Maven and Docker were not installed, and container dependency downloads were blocked. Consequently, the following were **not run**:

- Maven dependency resolution, Spring source compilation, JUnit/Failsafe execution, or application startup.
- The **12** Testcontainers integration tests (3 MongoDB, 3 JPA/MySQL, 6 transactional JDBC/MySQL).
- Dockerfile builds, Compose semantic/runtime validation, readiness checks, real network/service integration, or the **13** live HTTP assertions.
- Angular/TypeScript compilation, npm dependency resolution, Vaadin build, browser interaction, or accessibility testing.
- Jenkins or GitHub Actions execution; image/action digest pinning, dependency scans, or release enforcement.
- OTLP export, collector ingestion, metrics scraping, dashboards, Splunk or AppDynamics investigation.
- Aurora, EKS, production identity, backup/restore, load testing, or a real process/database failure drill.
- Private initializer execution, native Codex discovery, independent adversarial/contract reviewers, or human acceptance.

No unresolved runtime check is treated as passed, and a missing Docker environment does not cause the integration profile to silently skip its suites.

## What the core evidence covers

The named cases cover decimal-safe prices, discount rounding, immutable catalog revisions, price version publication, overlapping ranges, offer thresholds, ownership, park-local dates, quote expiry, 230 valid party compositions, capacity sharing, hold expiry, quote reuse, order replay, owner/key scope, transaction rollback, state reconstruction, malformed upstream quote rejection, and concurrent allocation/confirmation.

The concurrency tests use the synchronized transactional **test adapter**. They support the domain design; they do not validate InnoDB locking, JPA/Mongo conversion, HTTP authentication, database durability, throughput, or deployment behavior. Those tests are provided as source for workstation execution.

## Local implementation review findings addressed

This was an implementation/self-review, not an independent reviewer pass. Corrections made before packaging include:

1. Enforcing globally unique quote use in the test booking adapter and SQL migration, not just within one event's ledger.
2. Rejecting null, mismatched, or invalid-quantity pricing responses before allocating inventory.
3. Keeping checkout keys stable across client retries and testing the key/hold relationship.
4. Aligning the HTTP event payload with the actual `visitDate` contract rather than an incorrect `date` field.
5. Using millisecond clocks for booking persistence identity and explicit case-sensitive SQL key collations.
6. Matching the intended Boot 4 starter/configuration names and the current Testcontainers module package names in source.
7. Retaining source-level and operational verification boundaries instead of labeling unexecuted builds as successful.

## Required first acceptance work

After local `git-agent` adoption, the first goal is to resolve/build and test this exact candidate on a Maven/Docker/Node 24 workstation. Repair failures before increasing scope. Generate and review frontend locks, execute both UIs against the real services, retain database/HTTP evidence, and obtain the actual independent/human reviews under the adopted governance.

## Roadmap follow-up

A later planning-only addition decomposes work into milestones/goals. See [roadmap delivery verification](roadmap/DELIVERY-VERIFICATION.md) for the new validator checks and any explicitly rerun baseline checks. It does not turn these original framework/database/runtime gaps into passes.
