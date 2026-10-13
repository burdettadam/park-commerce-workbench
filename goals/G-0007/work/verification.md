# G-0007 verification checkpoint — 2026-10-13

Source S is `15c70f15154997f6eda7b72ad70aa3eba76e5fdd`. It was checked out clean in the ignored detached worktree `.tmp/g0007-verify`; the branch writer has made no source change. Accepted plan P and its exact digest are in `bootstrap.md`. The imported editorial implementation is the current candidate rather than a second implementation.

## Completed at S

- `bash scripts/verify.sh --integration` exited 0 at 17:58:56 UTC. Core: 280/280. Real database suites: 67/67, including 8 `CatalogEditorialIT` and 9 `MongoCatalogIT`. No skipped, failed, or errored tests. The Testcontainers checks used real MongoDB and MySQL, not the earlier in-memory candidate observations.
- `docker compose up --build -d --wait` exited 0 at 18:01:05 UTC for six isolated local services on loopback ports 8581–8583, 27517, 3357–3358.
- `node scripts/demo-http.mjs` exited 0 after 43 live HTTP assertions against that stack. The seven named editorial assertions cover guest visibility, employee-only editorial routes, stale write conflict, exact reviewed draft publication, surviving later draft, stale base rejection, and pinned history traversal. This run created synthetic data only.
- `mvn -B -ntp -Pui,production -pl admin-console -am clean install` exited 0. Vaadin production package and 280 core tests passed.
- After the Docker engine recovered, a focused Playwright run exited 0 against Vaadin and catalog. The final retained [script](../evidence/15c70f1/checks/browser-flow.cjs) saved a draft, observed guest 404 before publication, rejected an unsaved form change, loaded the exact saved draft, published two immutable revisions, and displayed history. Authenticated API history returned revisions 2 then 1 via a one-item page cursor. A second employee view received a visible conflict on a stale save; the winning draft remained version 3 and the guest still saw revision 2. The employee token control was a password input. Final synthetic product `g7-ui-fec28026`.
- Public PR #9 CI run 38442316069 completed successfully in `java`, `admin-ui`, and `client-contracts` on standard GitHub hosted runners.

Local raw command transcripts remain ignored under `.tmp/g0007-*-raw.txt`; they have not been published because application output may expose local development credentials. The compact retained check summaries are in `evidence/15c70f1/checks/`.

## Open browser and runtime issue

The first two isolated Playwright employee-screen attempts failed at `Save new draft version`: the UI reported `Response unavailable; a write may already have succeeded`. The Java client's five-second read timeout elapsed. A subsequent direct Node POST to catalog also timed out at 20 seconds, and a guest product GET timed out at five seconds, while `/actuator/health` still returned 200 in 45 ms. Docker Desktop's Linux engine then stopped responding to `docker version`/Compose calls. These are **failed attempted checks**, not a browser acceptance pass. Once the engine recovered, an authenticated probe POST returned 201 in 77 ms and the fresh synthetic browser run passed. Earlier timed-out writes used unique synthetic IDs; no blind retry was made. The browser script and raw output remain local under `.tmp/g0006-browser/g7-flow.cjs`, `.tmp/g0007-browser-raw.txt`, and `.tmp/g0007-browser-green-raw.txt`.

Adam explicitly delegated this G-0007 intended-use check to Codex on 2026-10-13. That delegation does not authorize a tagged release.

## Review/evidence continuation

Preliminary evidence E1 `2fe4b935afee25232f710d550f6f5961068eb863` was pushed and retrieved from a fresh public clone. Both configured native round-two reviewers completed independently, found no demonstrated AC01–AC04 source or DRY defect, and blocked on missing manifest, selected rather than complete raw output, audit sample linkage, stale contract-document status and final retrieval. Their exact reports and disposition are in `evidence/15c70f1/reviews/`. Complete gzip command transcripts are now retained; the first failed browser attempt's original raw file was overwritten and that limitation is disclosed. The catalog-publication status document was corrected. A draft manifest maps source, checks and artifact digests. The final round found escaped local username paths in the published integration archive; the replacement has been rescanned and the affected post-E1 history is being withdrawn. This exhausts the accepted review rounds with a blocker, so the goal is `needs-decision` pending Adam's review-rule decision. No ready claim is made.
