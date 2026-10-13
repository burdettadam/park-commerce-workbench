# G-0008 round-one contract review — independent first finding

Native configured role `contract-reviewer`; child thread `01a1375c-3585-7d11-a179-a4b3469a9432`, task `/root/contract_review`. Local raw rollout SHA-256 `931c98a7db0fc935d672266a985cf4f1e979155be1d140c52f68424f2bbcb43c`. The raw local rollout is not published; this report normalizes absolute workstation paths.

Reviewed fixed source S `100945358c88e4c72a7c444326c618e19b054180` against base `bd26cfbd062228c228223518d3800cd125c4e9b1`, accepted plan P `717c31b04eca3f6478efa50c25dd666d3de73057` (`G-0008-active-1`), and evidence checkpoint E1 `512ea881cbd42fbb3f227a8e575b261b0867212f`. E1 is report-only relative to S. The review inspected governance, architecture, both repository skills, the contract review guide, accepted criteria, source diff, shared calculation, HTTP adapter, tests, live demo assertions, employee UI error handling, and E1 evidence.

**No demonstrated API or calculation defect** in the inspected scope. Preview and quote use `Pricing.Service.calculate`; the HTTP tests assert overlap and party errors, monetary parity, quote identity and expiry, and price/quote row counts. The live demo checks publication of the exact submitted candidate and the resulting quote lines. Retained checks report core 280/280, safe error tests 3/3, database integration 67/67, and 43 live HTTP checks.

**Acceptance blocker:** Intended-use comparison of preview, publication, monetary parity, errors and side effects by Adam Burdett or an explicitly delegated validator is absent. The [accepted plan](../accepted-plan.md) reserves that gate and [scope report](../scope-and-review.md) marks it open for AC01–AC04. Obtain and retain validation before readiness; CI does not replace it.

**Uncertainty needing disposition:** The [employee UI error handler](../../../../../admin-console/src/main/java/dev/parkcommerce/admin/CommerceView.java) replaces every HTTP 400 detail with a generic instruction. This hides the API's specific repair detail. The reviewer could not establish from static evidence whether an AC03 invalid input reaches that handler, because some requests receive local validation. Exercise the employee path with accepted invalid cases and resolve any lost actionable message.

No tests were run in this read-only review. Browser behavior, a completed Windows Compose run, and live-stack order/inventory invariants beyond the core checks remain unverified. This review grants no intended-use validation, readiness, approval or merge.
