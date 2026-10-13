# G-0007 independent adversarial review, round 2

Native configured role `adversarial-reviewer`; child thread `01a1367d-0329-78f2-891b-5798fcf439c7`; task `/root/g0007_round2_adversarial`; parent CLI round-two session. Raw local rollout SHA-256 `9fa82859cb87489d5b7582977ac6000053c58d3e4d0464be3c986b9a8221ee36`. Completed task, read-only. Source S `15c70f15154997f6eda7b72ad70aa3eba76e5fdd`; evidence E1 `2fe4b935afee25232f710d550f6f5961068eb863`. Local absolute paths in the original report are replaced with repository-root placeholders.

## G-0007 round-two adversarial review

**Child thread:** `01a1367d-0329-78f2-891b-5798fcf439c7`
**Role/task:** `adversarial-reviewer`, `/root/g0007_round2_adversarial`
**Candidate:** S `15c70f15154997f6eda7b72ad70aa3eba76e5fdd`; base `c734a95836388ec8316a06bac8c7361d9ef8b0de`; plan P `e5add22f8777c358e0a27981b1bb74e03f52bb47`; evidence E1 `2fe4b935afee25232f710d550f6f5961068eb863`.

I read the accepted plan, governing files, artifact contract, publication contract, and adversarial review guide. I inspected the fixed Git trees, catalog domain/API/Mongo implementation, employee console, relevant tests, and every committed file in `goals/G-0007/evidence/15c70f1/`. This was read-only; I ran no mutable tests.

### Findings and blockers

1. **Required evidence manifest and recovery proof are absent — readiness blocker.** E1 has no `goals/G-0007/evidence/15c70f1/manifest.json`, although the accepted plan and `docs/roadmap/ARTIFACT-CONTRACT.md` require a filled manifest linking criteria, exact checks, artifact digests, and both reviews. The [handoff](goals/G-0007/evidence/15c70f1/handoff.md) explicitly says final evidence checkpoint retrieval remains pending. A later evidence commit cannot be treated as remotely recoverable merely because it exists locally. Disposition: complete the manifest with actual observations, validate it, retain both final reports, then prove retrieval of the final committed evidence from a fresh path. This does not require changing S.

2. **Failed browser attempts have limited durable diagnostic evidence — evidence limitation.** [browser-attempts.txt](goals/G-0007/evidence/15c70f1/checks/browser-attempts.txt) records two failed attempts, timeouts, and a Docker engine outage, but omits per-attempt timestamps, exit status, and raw output. The raw transcripts are described as ignored local files. The later [browser-green.txt](goals/G-0007/evidence/15c70f1/checks/browser-green.txt) and committed [browser-flow.cjs](goals/G-0007/evidence/15c70f1/checks/browser-flow.cjs) give a concrete successful flow and exit 0; they do not erase the failures. Disposition: record the failed attempts’ exact available provenance and identify any indispensable raw artifact with digest, owner, retention, and retrieval, or state clearly that the raw failure output is local-only and unavailable to a replacement reviewer. I did not find evidence that the failure was a product defect after Docker recovered.

3. **Audit sample provenance is weaker than its description — limitation, not a demonstrated AC04 defect.** [audit-example.json](goals/G-0007/evidence/15c70f1/audit-example.json) identifies product `g7-ui-8166d7dd`, whereas the retained final browser output identifies `g7-ui-fec28026`. The browser report explains an earlier successful shorter script used another product, but its raw output is local-only. The sample may be genuine, and `CatalogEditorialIT` checks actor derivation and Mongo storage; the committed evidence alone cannot independently correlate the sample to the final browser flow. Disposition: identify the earlier run as the sample’s provenance or retain a redacted exact response and digest.

### Source and criteria assessment

No demonstrated AC01–AC04 source defect or duplicate canonical implementation emerged from this review. S changes only G-0007 coordination records relative to the accepted base; it reuses the base’s `CatalogEditorial.Service`, `MongoCatalogStore`, API, and console. Draft and publication streams have separate unique compound indexes; audit travels inside each inserted document. Guest reads use published snapshots. Stale predecessors map to conflict, and history uses descending revision range cursors pinned to a snapshot. The retained real Mongo/Spring tests and final browser script exercise those paths, including the legacy immediate-publication route and old stored documents. The console renders history as text and masks its token field. These observations support the local synthetic scope, subject to the evidence blockers above.

I did not verify the reported test execution independently, authenticate local-only raw logs, inspect the live Compose resources, prove remote checkpoint retrieval, or assess production identity, backup restoration, deployment health, or release approval. No readiness or release approval is granted.
