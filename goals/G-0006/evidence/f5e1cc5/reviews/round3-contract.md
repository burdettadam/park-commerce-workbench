# G-0006 independent contract review, round 3

Native child `01a135b3-cc71-7423-868d-2ad2a1a9f4b6`; parent `01a135b3-78f8-7583-b391-b7eb8e6431bb`; local raw rollout SHA-256 `64c7078dc050ab2cacefc6aa4aa5bda3979b979f8446365316baa9e4ba776019`. Raw rollout remains local because it contains host paths. Native role metadata authenticates the child role.

## Contract reviewer

— contract reviewer

**Task path:** `/root/g0006_r3_contract`
**Role:** `contract-reviewer`
**Result:** AC01–AC04 have coherent retained evidence at S; final readiness remains blocked.

The reviewer read the governing instructions, accepted goal, architecture and review guide; compared base with S; and inspected the round-two reviews, browser script and output, HTTP and restart records, artifact contract, current uncommitted reports, and named manifest draft. Inspection and hash checks were read-only.

1. **AC01–AC04:** The retained records report six healthy Compose services and 43 passing live HTTP assertions covering roles, ownership, earlier-price preservation, retry, and competing holds. The [browser script](../../../../goals/G-0006/evidence/f5e1cc5/checks/browser-flow.cjs) publishes the $125 rate, checks the old quote at 10000 cents and a fresh quote at 12500 cents, *then* asserts that the original guest page displays `$100.00 USD`. Its [output](../../../../goals/G-0006/evidence/f5e1cc5/checks/browser-flow.txt) reports exit 0 and linked quote and order IDs. The [restart record](../../../../goals/G-0006/evidence/f5e1cc5/checks/restart-round2.txt) retains three volumes and retrieves that order with one capacity unit consumed. **Disposition:** No source fix indicated. The earlier UI timing gap is closed within the retained run’s limits. Identical quote and reprice screenshot bytes do not independently prove timing; the ordered script and output supply that evidence.

2. **Final records — blocking readiness gate:** The draft manifest says `not_ready`, with validation `not_run`, readiness `not_requested`, and round-two reviews `pending_final`. Those fields have not caught up with the current uncommitted [validation](../../../../goals/G-0006/evidence/f5e1cc5/validation.md), [decision](../../../../goals/G-0006/evidence/f5e1cc5/decision.md), and [handoff](../../../../goals/G-0006/evidence/f5e1cc5/handoff.md). E4 retrieval covered its then-current files, not this final package. **Required disposition:** Reconcile the final states, make the actual readiness decision, publish the completed manifest and reports, then prove fresh retrieval.

3. **Intended-use validation:** The validation record attributes delegation to Adam’s documented conversation instruction and records Codex’s scoped acceptance from retained evidence. It does not claim Adam personally ran the flow or grant tagged-release approval. The reviewer could not independently authenticate that conversation from repository files.

All **28** draft artifact SHA-256 values match current local files. Its counts agree with the inspected raw records: six services, 43 HTTP assertions, 280 core tests in the Vaadin build, and one Angular build, browser flow, and restart. S changes setup documentation and a storefront footer; the reviewer found no duplicated business rule, DRY divergence, boundary defect, or compatibility break. The Vaadin build did not independently prove a fresh `npm ci` install.

**Unverified by this reviewer:** Rerunning mutable checks, remote retrieval of the final checkpoint, deployment health, and human release approval.
