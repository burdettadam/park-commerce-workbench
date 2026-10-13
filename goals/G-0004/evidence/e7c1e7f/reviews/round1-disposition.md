# G-0004 first-review reconciliation

Fixed product/build source S remains `e7c1e7f309bd976b6bb92e575374c1bb1add316d`. The two first reports were collected independently before this disposition: [contract](round1-contract.md) and [adversarial](round1-adversarial.md). Neither first review is relabeled as a pass. No product code, build input, test, or accepted criterion changed while addressing these report and provenance findings.

| Finding | Disposition and evidence |
| --- | --- |
| README linked run `38335871253` while evidence used `38336113859` | [CI provenance](../checks/ci-provenance.json) records the former as `f411a69` and the latter as exact S `e7c1e7f`. README now links the S run. This is a report-only change after S; no affected build input changed. |
| Second Vaadin checkout/volume identity not fully captured | Repeated the clean build in a new detached S checkout. [Provenance transcript](../checks/clean-provenance.txt) captures worktree creation, HEAD, clean status before/after, absent-before Maven volume, creation time, command, exit, diff exit, and lock digest. [Build output](../checks/clean-provenance-build.txt) shows `npm ci`, Vite, 280 core tests and Maven success. The earlier separate [clean rebuild](../checks/clean-vaadin.txt) remains retained, not overwritten. |
| Evidence not recoverable from remote at first review | Published initial report E1 `5b220aec3f7a41c3de5d39e7b3e28a92ba91932d`; a fresh public HTTPS clone matched 14/14 committed evidence file blobs and could retrieve source S. [Preflight retrieval](../checks/retrieval-preflight.txt) records the result. Final E retrieval still must be repeated after the manifest and reviews are published. |
| Report said clean S rebuild was still required | Corrected [A02](../dependency-locks.md); the build has passed. |
| Earlier second Angular checkout lacked retained raw output | Removed that unsupported extra assertion from A02. The hosted S run's fresh `npm ci`, build, client tests and unchanged lock remain the acceptance evidence. |

The second native review round should reassess the concrete proof and verify no remaining AC01-AC04 blocker. Adam delegated G-0004 intended-use validation to Codex after the clean builds and hosted checks were shown; actual validation is recorded separately, after technical review.
