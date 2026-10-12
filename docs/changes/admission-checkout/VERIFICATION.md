# Admission checkout — verification and handoff

Date: 2026-10-12. This report records a local implementation candidate, not an
accepted milestone, working cloud service, or production-readiness declaration.

## Identity

- Base / preceding ZIP: `48bcaa5529085cc8640ea602a4f125aac681776d`.
- Initial plan and unimplemented-feature tests: `f498ac8bda60f09ce70097831bf9800c7ed77bfa`.
- Fixed source S tested: **`8e646771f3617f6dea974ddf9271203175d48099`**.
- Branch: `local/admission-checkout`; no remote configured, tag or release created.
- This report and logs are retained in a later evidence-only commit E. No report
  must contain its own future commit ID. The bundle retains both S and E.
- The previous pricing-contract ZIP matched every file of the base commit exactly.

## Implemented slice

A separate admission-only confirmation operation creates one immutable receipt
containing the original server quote and grouped ADULT/CHILD entitlements. No
event inventory is part of the admission domain or JDBC adapter. Owner/quote/key
binding, replay after expiry, missing-result recovery, strict snapshot validation,
clock boundary checks, JDBC persistence, an additive Flyway migration and three
HTTP routes are included. Event routes, records, adapter and V1 SQL are unchanged.

One entitlement represents all purchased tickets on its variant line. Two adults
plus one child produce two entitlement IDs representing three admissions. This
is a fictional dated entitlement, not an identity credential or live entry system.
The Angular screen and its reload coordinator remain event-only. No admission
browser UI, cart, cancellation/VOID mutation, refunds, live payment or deployment
was implemented. The new contract explicitly documents the route-specific key
namespace and the future cancellation/combined-cart boundaries.

## Actually executed against S

[checks.json](evidence/checks.json) records the fixed source, commands, environment,
exit codes and raw-file digests. Source was clean when execution began. Writing
these later evidence files did not change the implementation under test.

| Check | Observed result | Boundary |
| --- | --- | --- |
| `bash scripts/test-core.sh --demo` | **280 passed, 0 failed** | 231 prior + **49 new admission** cases; actual Java domain, test-only memory stores |
| Independent pricing fixture suite | **29 passed** | Existing 22 fixtures + 384 deterministic sample inputs, not HTTP or databases |
| Client checkout/recovery tests | **66 passed** | Existing event client, mocked transport/storage, not Angular rendering |
| Setup/initializer wrapper suite | **29 passed** | Private initializer is mocked; not native git-agent adoption |
| Roadmap/evidence validator tests | **41 passed** | Structure/integrity only, no review or acceptance |
| Roadmap structural validation | Passed | 10 milestone / 30 goal proposals preserved |
| TypeScript declaration check | Passed with installed **5.8.3** | Existing isolated declarations, not the pinned 5.9.3/Angular build |
| Java syntax parse | **52 files, 0 syntax errors** | Parsing only, NOT symbol resolution or framework compilation |
| Bash / live JS syntax checks | Passed | Not a Compose, Jenkins, GitHub or live HTTP run |

Host: Linux, OpenJDK **21.0.12.1**, Node **22.16.0**, TypeScript **5.8.3**, Bash
5.2.37. These are observed local check tools, not changes to the selected stack.
The workstation Angular baseline remains Node 24 and its pinned TypeScript version.
See [environment.txt](evidence/environment.txt).

The core demonstration bought three dated admissions for **$210.00**, issued two
variant groups, preserved event availability at **2** after the prior event sale,
and recovered the identical admission receipt after quote expiry without pricing.
These are in-memory demonstration observations, not database durability evidence.

## First failure and negative checks

The initial red command did not compile because Admission and its store/API did
not exist. It demonstrates missing feature symbols, **not an existing behavioral
bug or executed failing assertions**. A checked-exception declaration in the new
test helper was corrected as part of source implementation. The first captured
output is retained unchanged in [red-core.txt](evidence/red-core.txt).

Two deliberate mutations were tested in isolated copies of S, not the delivery:

1. Remove the explicit new-order quote-expiry branch. The exact-boundary and
   delayed-attempt tests reject the changed behavior/error contract. The receipt
   constructor also retains its independent issuance-time invariant.
2. Make admission recovery call pricing. The no-pricing historical replay/recovery
   test fails, as do the offline absent-result/recovery checks as applicable.

Both returned exit 1 and the intended named failure. See
[mutation-checks.json](evidence/mutation-checks.json) and its raw outputs. This is
writer-run regression checking, **not independent adversarial review**.

## Concurrency evidence

Three new core tests place 24 contenders at a pricing-response barrier before
letting them enter the test store. Same quote/key yields 24 identical successful
receipts with one entitlement set. Different keys for one quote, and different
quotes for one owner/key, each yield one winner with the rest conflicting.

The memory store uses synchronization. These results do NOT establish InnoDB
isolation, lock timeouts, deadlock handling, process recovery or Aurora behavior.
The JDBC adapter relies on unique indexes and explicit transaction boundaries.
After a failed SQL attempt it either reconciles a committed identical winner or
preserves conflict/uncertainty; it does not run an unbounded retry loop.

## Framework and database work NOT executed

**Maven and Docker are absent.** `bash scripts/verify.sh --integration` exited **2**
at its Maven prerequisite; it did not start tests or reach its Docker check.
A Maven Central probe exited **6** because the hostname could not be resolved.
Those are blocked checks, never successful skips. Logs are included.

Added **15** MySQL/Spring HTTP integration tests; total candidate `*IT` source is
**64 tests**. Added **8** named live HTTP checks; the demo source now has **43**.
None of these 64 database/HTTP tests or 43 live checks ran in this environment.
The new suite covers JSON round trips, authentication/ownership, old-quote replay,
key binding, zero-event operation, source clock/input boundaries, binary key/owner
identity, concurrent confirmation, complete aggregate persistence and corruption
rejection. It includes a separate disposable MySQL instance migrated to V1,
seeded with a real event order, upgraded to V2 and checked for both order types.
This is test source, not observed upgrade success.

The concurrent SQL test retains counts of uncertain first attempts and uses an
explicit bounded same-key reconciliation after contenders finish. It does not
pretend that all first attempts succeeded. Live-demo event comparisons distinguish
unchanged confirmed/capacity values from natural expiry of preexisting holds.

Spring, Jackson, Flyway and JDBC code is syntax-checked only. No framework dependency
build, security filter execution, SQL migration, SQL durability, real browser,
container, pipeline, cloud deployment, benchmark or production identity is claimed.
No new dependency/version has been introduced to bypass these gates.

## Artifact and review state

- [Bounded plan](PLAN.md), [API/lifecycle/key contract](../../project/admission-checkout.md),
  [literal expected examples](../../../contracts/admission/admission-examples.json),
  [goal coverage](GOAL-COVERAGE.md), raw checks and [review state](review-status.json).
- The four JSON examples are human-readable expected fixtures corresponding to named
  Java scenarios, not captured runtime JSON responses. The tests use Java assertions;
  they do not deserialize that documentation file.
- G-0011 and its prerequisites remain unaccepted; no goal is marked ready/complete.
  Two independent reviews and human intended-use validation are pending.
- Scope/release/private-development requirements remain unchanged. Nothing was
  committed to the connected GitHub repository, deployed or exposed publicly.

## Workstation next action

Preserve your existing GitHub history: overlay the source ZIP on a local branch,
inspect it, initialize/adopt the actual git-agent guidance, and accept the specific
candidate/prerequisite decisions. Do not push until private visibility is verified.

Run `bash scripts/verify.sh --integration` with the required JDK/Maven/Docker/network,
then run `node scripts/demo-http.mjs` against actual local services. Validate the
V1-to-V2 fixture, complete JSON snapshot round trips, role filters and collision
handling before accepting this slice. Build the existing UIs separately; admission
browser work is not included. Obtain current independent contract/adversarial
reviews, record dispositions, and obtain human validation under the adopted rules.

The full ZIP, incremental patch and local Git bundle preserve source and evidence.
They are **local_only** checkpoints, not a verified remote backup. A separate package
verification JSON records final archive hashes, fresh-extraction checks and patch/
bundle restoration; it does not amend any goal acceptance state.
