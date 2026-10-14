# Independent adversarial review, round 1 — blocked at prior source

Source reviewed: `8ff55d01ed8fff68e29065de5323ca12fae9badb`. Native configured `adversarial-reviewer`, parent session `01a1377d-2f3b-79b0-bb28-ba7f223f43e8`, child session `01a1377d-590a-74e1-b33b-a04b31d003ae`. The reviewer did not edit implementation. Decision: **BLOCKED** for G-0009 readiness.

Finding A1, AC03: cross-role assertions for ordinary REST `GET /api/quotes/{id}` and GraphQL `quote(id:)` were missing. Status routes were covered, and both ordinary read paths delegated to `Pricing.Service.get`; this was a coverage blocker, not a demonstrated authorization defect.

Finding A2, evidence/recovery: G-0009 A01–A03, raw checks and manifest were absent. The original bootstrap still described G-0008 as pending/no implementation after its acceptance, making handoff stale. The reviewer found no concrete DRY or coupling defect; diff check passed. No additional tests were executed by this reviewer.

Disposition at second source `af04615957d172e6ad145baa7ba7858584b9a4f2`: `QuoteContractIT.ordinaryQuoteReadsUseTheSameOwnerAndRoleBoundaryAcrossTransports` verifies owner, other guest, employee, service role, anonymous and unknown IDs on both direct read transports, with no write. Local pricing IT passed 16/16, zero skips; hosted full CI passed. Bootstrap now has a dated resumption section preserving the original blocked checkpoint and accepted G-0008 identities. A01–A03 and check artifacts are in this directory. Both source and evidence need independent final review; this first-round report remains blocked.
