# G-0007 independent contract review, round 2

Native configured role `contract-reviewer`; child thread `01a1367c-f344-7220-8355-7c9c5c75ac68`; task `/root/g0007_round2_contract`; parent CLI round-two session. Raw local rollout SHA-256 `46b2a8e77159cf0184091a1ae094aded51c39afa4a53f1a4fe417a017c418f45`. Completed task, read-only. Source S `15c70f15154997f6eda7b72ad70aa3eba76e5fdd`; evidence E1 `2fe4b935afee25232f710d550f6f5961068eb863`. Local absolute paths in the original report are replaced with repository-root placeholders.

## G-0007 round-two contract review — final report

**Candidate:** S `15c70f15154997f6eda7b72ad70aa3eba76e5fdd`; base `c734a95836388ec8316a06bac8c7361d9ef8b0de`. **Plan:** P `e5add22f8777c358e0a27981b1bb74e03f52bb47`, G-0007-active-1. **Evidence:** E1 `2fe4b935afee25232f710d550f6f5961068eb863`. **Guide:** `.agents/skills/work-goal/references/review-guide.md`, contract reviewer section, under the accepted two-reviewer, three-round policy. My task path is `/root/g0007_round2_contract`; my thread UUID is not observable to me.

I inspected the fixed catalog domain, Mongo adapter, employee and guest APIs, Vaadin workflow, relevant tests and public contract at S; the accepted plan, governance and architecture; and every committed file under `goals/G-0007/evidence/15c70f1` at E1. The package includes two failed browser attempts, the successful final browser script and summary, selected integration and HTTP output, the audit sample, and the incomplete first-review record. I ran no mutable checks. S contains goal-record changes over the base, with no product-code change.

### Findings

1. **Blocker — required evidence manifest is absent.** E1 contains no `goals/G-0007/evidence/15c70f1/manifest.json`. The accepted plan and `docs/roadmap/ARTIFACT-CONTRACT.md` require a filled manifest tying S, P, checks, criterion coverage, artifact digests, reviews, validation and retention together. The unfilled roadmap template does not serve that purpose. Add and validate a manifest from the actual results; independently inspect its claims.

2. **Blocker — committed check evidence lacks the required raw output.** `checks/integration-selected.txt` contains test totals and build success, while `checks/live-http-assertions.txt` contains pass labels. `checks/browser-attempts.txt` summarizes the failures; `checks/browser-green.txt` summarizes the final success. The package identifies the original transcripts as ignored, local files. Those selected extracts support that the reported checks were selected and meaningful when compared with source, but do not preserve the raw command results and failed attempts required by the artifact contract. Retain sanitized raw output with command, result and artifact identity, or make a documented retained artifact retrievable and verify it.

3. **Evidence provenance needs correction.** `audit-example.json` identifies product `g7-ui-8166d7dd` and says its events followed a passing browser run. The retained final `browser-green.txt` and `browser-flow.cjs` use `g7-ui-fec28026`; the green summary mentions an earlier, shorter successful script with another product, but that run and its output are local-only. The sample may be valid, yet E1 does not independently connect it to a retained run. State which run produced it and retain enough sanitized output to verify that link. This does not negate the separate Mongo audit test.

4. **Recovery remains unproved.** `handoff.md` explicitly says final evidence checkpoint retrieval is pending. It cites source retrieval at the earlier activation checkpoint, rather than a fresh retrieval of S plus E1. Complete the branch and evidence retrieval check before claiming remote recovery.

5. **Documentation is stale.** At S, `docs/project/catalog-publication.md` still says Spring/MongoDB/Vaadin integration is unexecuted and that there is no executed browser walkthrough. E1 reports both. Update the maintained contract/status text or link a later status record so readers can distinguish the original imported candidate note from current observations.

### Contract assessment and limits

I found **no demonstrated AC01–AC04 product-code defect** in the inspected paths. The tests contain substantive assertions for guest draft invisibility, retained revisions, competing real Mongo writes, HTTP 409 responses, pinned first/last/empty/invalid history pages, server-derived actors and legacy audit compatibility. The final browser script makes direct guest/API assertions in addition to UI status checks. Its successful summary does not turn the two earlier failures into passes; both remain disclosed. The first native reviewer attempt ended without final child reports and cannot count as a completed review.

This is source and committed-evidence inspection only. I did not rerun MongoDB, live HTTP, browser, hosted CI or retrieval checks, and could not inspect the ignored raw transcripts. The shared demo `employee` subject establishes authenticated-subject attribution within the local setup, not individual staff identity. I observed no token value in the committed package or in a path-only search for obvious credential patterns. I grant no readiness or release approval.
