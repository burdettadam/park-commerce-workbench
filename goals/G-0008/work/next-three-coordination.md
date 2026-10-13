# G-0008 to G-0010 subagent coordination

The active orchestration objective covers roadmap G-0008, G-0009 and G-0010 in their accepted dependency order. Three distinct subagents inspected the current public `main` base `bd26cfbd062228c228223518d3800cd125c4e9b1` without editing it. Their initial audits are preparatory findings, not product validation, independent acceptance reviews or adopted G-0009/G-0010 plans.

| Goal | Assignment and present state | Dependency gate |
| --- | --- | --- |
| G-0008 | Pricing subagent owns `goal/G-0008-implementation` at plan base `363fa8a4b1bc0c1487b9379fc110e465ebe77d62`; canonical integration remains on `goal/G-0008`. | G-0007 is ready and merged. |
| G-0009 | Quote-contract subagent completed a read-only gap audit; no writer branch, active record, or acceptance claim. | Accept fixed G-0008 source and exact relevant input revision before execution. |
| G-0010 | Contention subagent completed a read-only gap audit; no writer branch, active record, or acceptance claim. | Accept fixed G-0009 quote contract and exact relevant input revision before execution. |

## Preparatory findings

- **G-0008:** `core/Pricing.java` already shares `calculate()` between preview and quote issuance; `PricingApi.java` and the Vaadin workbench have preview paths. Reuse that canonical calculation. Focus on employee-facing validation details, exact candidate identity, read-only side effects and fixed-candidate core, real database, HTTP and applicable UI checks. Existing HTTP negatives assert status without the actionable message required by AC03.
- **G-0009:** Existing quote code, `contracts/pricing` fixture oracle and REST/GraphQL tests are candidate inputs only. Add public-API multi-day date endpoint/gap checks and executable consumer checks for serialized quote/error compatibility; a source-only GraphQL SDL check does not satisfy AC04. Retain the single Java calculation and the independent test oracle.
- **G-0010:** Existing MySQL contention checks race 24 holds for three seats via service calls, and HTTP recovery checks cover controlled failures. The proposal requires at least 100 real HTTP holds for 10 seats, ledger reconciliation, concurrent idempotency replay, actual process interruption/restart and lock-scope SQL evidence. Reuse the booking transaction model and distinguish pool/transaction timeouts from sold-out results.

All three proposed goals allow only local synthetic data and standard free public GitHub runners. No paid workflow, cloud run, release or deployment is assigned. G-0009 and G-0010 proposed criteria remain unchanged and unaccepted as active execution contracts until their predecessor gates are met. The primary integration owner will assign separate refs/worktrees and update each canonical record at that point.
