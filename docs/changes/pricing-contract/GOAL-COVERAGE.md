# G-0009 contribution and remaining gates

This document maps one local candidate to the existing proposal; it does not
activate or mark the goal ready. The accepted plan and prerequisites are still
unresolved. The user's request authorizes only this bounded implementation work.

| Criterion | Candidate artifact/evidence | Remaining work |
| --- | --- | --- |
| G-0009-AC01 — independently specified monetary cases | `contracts/pricing/pricing-contract-fixtures.json`; actual Java comparison and BigInt oracle; final-pricing-contract.txt | Independent fixture review and acceptance are not recorded |
| G-0009-AC02 — calendar and exact expiry through public APIs | Core boundary tests and strict parsers; `QuoteContractIT`; extended live HTTP script | Execute real HTTP/GraphQL tests, runtime date/error binding and booking expiry paths |
| G-0009-AC03 — immutable revisions and consistent ownership | Core snapshot/status and original-order recovery tests; unchanged Quote record; HTTP/security tests source | Run persistence and role-filter tests with real service framework; independent review |
| G-0009-AC04 — examples, exclusions and compatibility checks | `docs/project/pricing-contract.md`; `contracts/pricing/quote-api-v1.json`; source-SDL checks; two detected deliberate regressions | Execute engine/consumer tests; approve intentional input tightening; human validation |

The original required G-0009 artifacts can link the literal fixture file and
contract above. This candidate's VERIFICATION.md is the boundary-results record.
Do not fill a ready evidence manifest with the still-missing reviews or framework
checks, and do not replace the original proposal to make it match only executed work.

## First workstation continuation

1. Retain/import the source in the existing local clone, without replacing remote history.
2. Initialize and adopt the actual reviewed `git-agent` configuration. Reconcile
   prerequisite goals and exact candidate/source identity under that baseline.
3. Run `bash scripts/verify.sh --integration`, then the live service demo and UI builds.
4. Obtain the two real independent reviews and intended-use validation. Record
   defects and a new candidate when fixes change relevant inputs; never reuse stale results.
5. Keep development private; this package does not authorize deployment or release.
