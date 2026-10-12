# Pricing-contract verification — 12 October 2026 UTC

## Source, authority and recovery boundary

- Input archive: `park-commerce-catalog-publication.zip`.
- Input SHA-256: `25ca0a229d300f68e8e615cde317f258af1f475db84271819ccfc66477c2b75e`.
- Base: `a9119af6db19e141d569bb97075243616436935a`. All **323** input files matched the restored bundle before editing.
- Initial regression tests and bounded plan: `790e116f94e85bae015afd6fbdc57bd339ef4068`.
- Fixed tested code candidate **S**: `67817658b211c5ae56b7ed4f3a4f688723c1d6b5`.
- Local branch: `local/pricing-contract`. No remote is configured.
- Later evidence/report commit **E** retains this report, raw results and an updated
  delivery manifest. Its real identity is in the delivered bundle; this file does
  not attempt to contain its own future commit hash.

The user's continuation request authorizes this local implementation slice, not
release, remote publication, cloud activity, approval of prerequisite goals,
independent reviewer acceptance or completion of G-0009.

## Executed against clean source S

[checks.json](evidence/checks.json) records command, directory, fixed source, clean
state before/after, UTC timestamp, duration, exit code and raw output digest.
Raw logs retain their original bytes, including trailing spaces from deliberately
malformed test inputs and Node assertion formatting. Source whitespace checks
exclude only those raw evidence files; their exact bytes are instead digest-checked.

| Check | Observed result | What it establishes |
| --- | --- | --- |
| Java core scenarios and walkthrough | **231 passed, 0 failed** | JDK 21 domain execution with test-only in-memory stores; 180 previous + 51 new cases |
| Independent pricing contract suite | **29 passed, 0 failed** | 22 literal fixtures and 384 deterministic samples compared against both Java pricing paths; source-only SDL checks |
| Existing client checkout/recovery tests | **66 passed, 0 failed** | Dependency-free Node logic with storage/transport test doubles |
| Setup/initializer-wrapper checks | **29 passed** | Mock initializer only; no actual git-agent adoption |
| Roadmap/evidence-validator tests | **41 passed, 0 failed** | Existing validation contract, not goal completion |
| Roadmap structure | **Passed** | Original 10 milestones / 30 proposals / 120 criteria retained |
| Existing TypeScript declaration contract | **Passed** | Standalone client declaration contract, not Angular framework compilation |
| Java syntax parse | **46 files, 0 parser errors** | JDK compiler parsing only, without library symbol/type resolution |
| Bash / JavaScript syntax | **Passed** | Syntax only |
| Source-S manifest | **332 listed hashes matched** | Source snapshot integrity, not semantic correctness |

The [portable source check](evidence/source-archive.json) records core and pricing
contract tests rerun successfully from a Git archive of S in a path containing
spaces. The source archive contains 333 files including its manifest. The full
later delivery also contains these new verification artifacts.

## Failing-before-fix evidence

[red-core.txt](evidence/red-core.txt) records **181 passed, 3 failed** against the
base implementation plus the initial tests preserved in `790e116`. The tests were
run before their commit, then committed unchanged to preserve reproduction.

1. A ticking clock showed createdAt two seconds later than the date check.
2. Advancing through park midnight caused an initially eligible request to fail
   its second date check inside the same quote operation.
3. A delayed catalog response extended the quote deadline by two minutes.

The fix captures one evaluation instant before dependency reads. These three
regressions now pass. Existing preview calculation behavior is retained.

## Independent pricing evidence and deliberate regressions

The 22 **hand-entered** fixtures are in
[pricing-contract-fixtures.json](../../../contracts/pricing/pricing-contract-fixtures.json).
A separate Node BigInt rational oracle checks those expected cents. A test-only
Java bridge then executes the actual production preview and quote calculations
and compares all line quantities, prices, subtotals, discounts and totals against
that oracle. It also checks 384 deterministic bounded sample parties. Thus **406
monetary inputs** traverse each of the two Java paths. This is not exhaustive
coverage, a performance benchmark, or independent human review.

In isolated copies of S, two deliberate changes were introduced:

- `HALF_UP` changed to `DOWN` in the Java money calculation: the fixture suite
  failed on actual Java preview results.
- GraphQL `Money.cents` changed from `String!` to `Int!`: the pinned SDL-source
  representation check failed.

[mutation-checks.json](evidence/mutation-checks.json) and its raw logs retain the
changes and intended assertion failures. The original source S remained unchanged.
These are test-sensitivity demonstrations, not independent reviewer reports.

## New behavior and tests

The 51 new core scenarios cover one-instant issuance, delayed dependencies,
park-midnight and DST transitions, strict date/UUID/party input, inclusive/gapped
rates, exact quote-expiry equality, historical snapshots, read-only owner-scoped
status, new catalog/price revisions and successful recovery of already confirmed
orders after quote expiry. Code uses no new product dependencies.

Status is an **expiration observation only**. It does not claim available capacity,
that a quote is unused, or that a purchase can succeed. A status read never renews
the quote or confirms an order. Historical reads and internal booking reads retain
the existing snapshot shape. Intentional input tightening now requires both
quantities, a strict date, and a full UUID; see the compatibility notes in the
[contract](../../project/pricing-contract.md).

## Framework and integration checks NOT executed

The actual environment contains OpenJDK/Javac 21.0.12.1, Node 22.16.0, TypeScript
5.8.3, Bash 5.2.37 and Git 2.47.3. Node 24 / TypeScript 5.9.3 remain the separate
workstation UI baseline; the dependency-free Node/declaration tests here do not
establish that UI toolchain's build.

The integration preflight exits **2** because Maven is missing. Docker and UI
node_modules are also absent. The Maven repository request fails DNS resolution
(exit **6**). These failures are retained, not converted into passing/skipped tests.

- **12 new** `QuoteContractIT` methods are included, bringing the Testcontainers
  integration suite to **49** methods. The new class specifies real MySQL + Spring
  HTTP/GraphQL checks with an explicitly stubbed catalog and controlled clock.
  No method is claimed executed here.
- The live three-service exercise has **35 named checks** (30 prior + 5 new).
  This run requires actual services; it was not executed.
- REST binding, Jackson serialization, Spring Security filters, GraphQL execution,
  real JPA persistence and MySQL round-trips are unverified by this delivery.
- No Angular/Vaadin framework build, browser run, Compose deployment, CI/Jenkins
  execution, AWS operation, image build, dependency scan or release occurred.
- Java syntax parsing is NOT dependency compilation; SDL-source checks are NOT a
  GraphQL engine or consumer integration test. Current framework compatibility
  remains a required workstation gate.

## Goal and review state

See [coverage](GOAL-COVERAGE.md). The roadmap proposals and native agent setup are
not altered. Actual contract and adversarial reviewers, prerequisite acceptance,
human intended-use validation and release decisions are **pending**. Writer
self-checks and mutation tests do not replace those independent reviews.

Recovery coverage is **local_only**: source archive, cumulative patch and a separate
local Git bundle. No remote/off-device retention or private visibility has been
verified. Check repository privacy before any push.
