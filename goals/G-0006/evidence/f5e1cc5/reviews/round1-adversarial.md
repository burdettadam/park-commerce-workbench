# G-0006 independent adversarial review, round 1

Native child thread: 01a134e0-deb2-7c00-80fa-a18500302246; parent: 01a134e0-527e-7812-8936-da1cc365ff3c. Local raw rollout SHA-256: 12e4ec55a58e99ed70cc02f19d2e12625d1b435c77f94ad32c1e793369859a14. The role/path were authenticated from child session metadata; the child could not see its own thread ID. Raw rollout stays local because it contains host paths. The following is the child final response with local file links rewritten as repository-relative links and native trailing whitespace normalized for public retention.

## Independent adversarial review — round 1

**Role:** `adversarial-reviewer`
**Task path:** `/root/g0006_adversarial_round1`
**Native child thread ID:** Not exposed to this reviewer.

**Fixed identities:** source S `f5e1cc5e576c45a785199374031e5853a4dc7606`; base `f95ca02f1f54b4010c0e9b4c00cba6fa685d689e`; accepted plan P `db494ebe4b5761cab9a1ecd67831dbad059f69b6`; evidence checkpoint E1 `4900d4a2c36189746f95567a71e48359c0b2bb04`. Git ancestry confirms P precedes S and E1 follows S. The retained accepted-plan copy matches the recorded SHA-256. The working tree was clean during this read-only review.

**Inspected scope:** Accepted AC01–AC04, governing baseline and review guide; the complete base-to-S diff; E1 reports, raw check logs, browser script and screenshots; Vaadin and Angular request paths; booking retry dispatch; Compose/build results and restart record. S changes setup guidance and the storefront footer. I found no new duplicate pricing, booking, or authorization implementation, misplaced business rule, dependency cycle, or public contract change in that diff. The established services remain the canonical owners of those rules.

### Finding 1 — browser evidence overstates its post-publication UI check

In [`browser-flow.cjs`](../../../../../goals/G-0006/evidence/f5e1cc5/checks/browser-flow.cjs), `guestText` is captured **before** Vaadin publishes price version 2. The later `uiKeepsOldPrice` value tests that cached text. Consequently, the statement in [`browser-walkthrough.md`](../../../../../goals/G-0006/evidence/f5e1cc5/browser-walkthrough.md) that the guest UI “still displayed $100” after publication is not established by that assertion. The quote screenshot was also taken before publication.

**Impact and disposition:** Correct that statement or obtain a post-publication UI observation. This is an evidence-integrity finding, not presently a demonstrated AC03 failure: the script separately reads the old quote after publication, checks 10000 cents against a new 12500-cent quote, and checks the resulting order total.

### Finding 2 — current checkpoint is incomplete for readiness

E1 contains the required A01–A03 reports and raw checks, but no filled `manifest.json`, independent review reports, intended-use validation, readiness decision, or recovery handoff. The accepted [goal record](../../../../../goals/G-0006/README.md) and [artifact contract](../../../../../docs/roadmap/ARTIFACT-CONTRACT.md) require those before a ready claim.

**Impact and disposition:** Readiness remains blocked pending actual completion and review of those records. E1 accurately calls itself a checkpoint; its absence of later records is not evidence that S failed the executed product checks.

**Evidence limits:** The Vaadin build log says existing `node_modules` caused `npm ci` to be skipped, so its successful `mvn clean install` does not independently demonstrate a fresh npm dependency install. The browser script and logs are retained copies of a local scratch run; their execution and clean-worktree claim were not independently reproduced here. I performed no mutable product checks, credential inspection, remote retrieval, intended-use validation, release approval, or deployment assessment. The local remote-tracking ref contains E1, but that observation alone does not prove a fresh off-device retrieval or whole-goal recovery.

The retained logs report six healthy Compose services, 43 passing HTTP assertions, a browser order at 10000 cents after price version 2, and order retrieval after a volume-preserving restart. Those are reported observations for S, with the limits above; they do not confer validation or release approval.
