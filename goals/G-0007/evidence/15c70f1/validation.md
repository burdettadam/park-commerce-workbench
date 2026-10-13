# G-0007 intended-use validation

**Decision: accept AC01–AC04 for the accepted synthetic local catalog scope at S `15c70f15154997f6eda7b72ad70aa3eba76e5fdd`.** Adam Burdett explicitly delegated this goal's intended-use check to Codex in the 2026-10-13 conversation after being asked specifically about G-0007 drafts, conflicts and history. The repository cannot independently authenticate that conversation; this record identifies the delegated observer. It is separate from any tagged-release human review.

I inspected the source contract, executed the fixed-source integration and live HTTP scripts, drove the production Vaadin JAR with local Playwright, read the authenticated audit/history response, and compared outcomes to the accepted plan. The source worktree was clean. The first two browser attempts failed during a temporary Docker Desktop engine outage and are disclosed in [browser attempts](checks/browser-attempts.txt). A fresh run passed after the engine recovered; I did not reinterpret the failed attempts as passes.

| Criterion | Observed intended use | Judgment |
| --- | --- | --- |
| AC01 | Saved draft returned guest 404; changed unsaved form was rejected; exact loaded draft published revision 1; another draft stayed guest-invisible until publication; revisions 1 and 2 remained in history. | Accept. |
| AC02 | Two employee views used the same expected draft predecessor. First saved version 3; stale second view displayed `CONFLICT`, latest draft remained the first view's content, guest revision stayed 2. The 9 real Mongo tests also include competing saves/publishers and unique index checks. | Accept. |
| AC03 | Employee UI showed descending revisions 2, 1. Authenticated one-item API pages returned 2 then 1 and null final cursor. Real Mongo and HTTP tests covered new writes during a pinned traversal, empty history and invalid/product-mismatched cursors. | Accept. |
| AC04 | Authenticated API returned server-derived `employee` actor, operation, product, previous/new stream versions and timestamp. The representative [audit sample](audit-example.json) excludes token and content; `CatalogEditorialIT` checks Mongo audit storage and rejects a forged actor. | Accept. |

Supporting output: [integration](checks/integration-summary.txt), [live HTTP](checks/live-http-summary.txt), [browser](checks/browser-green.txt), [publication scenarios](publication-scenarios.md), and [history/conflicts](history-and-conflicts.md). This is a local synthetic check. The demo uses a shared employee identity, so it proves attribution to the authenticated subject, not to a named individual staff member. It does not validate production identity, remote deployment, backup restoration, or a version-producing release.
