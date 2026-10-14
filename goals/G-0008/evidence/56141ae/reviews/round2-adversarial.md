# G-0008 round-two adversarial review: independent first findings

Product source S: `56141ae40f05704ff0ae30b232ef878e2943b6c6`.
Evidence checkpoint E2: `31725c5f6fa295e774233c8430ea73d1e595714a`.
Native role session ID: `01a1376d-4820-7781-a1ca-ed7183d14b32`; raw local rollout SHA-256 `e7faf3c814888116920f27fea42eaeb15908d1ca0d561cc693832c4576f1a1b4`.
The child submitted these findings before parent reconciliation. Subsequent disposition is separate; this report is not overwritten.

**Independent round-two adversarial findings** — native `adversarial-reviewer`, session `/root/adversarial_round2`. Reviewed source S `56141ae40f05704ff0ae30b232ef878e2943b6c6` against base `bd26cfbd062228c228223518d3800cd125c4e9b1`, accepted plan P `717c31b04eca3f6478efa50c25dd666d3de73057` (verified SHA-256 `cb89fb489c5b53bbb65469d441ebdea820f73c5f15e5843154b890779147722a`), and evidence checkpoint E2 `31725c5f6fa295e774233c8430ea73d1e595714a`.

1. **Open verification selection gate:** The [accepted goal record](C:/Users/maree/OneDrive/Glthub/marty-workspace/park-commerce-workbench/.tmp/g0008-integration/goals/G-0008/README.md:42) names `bash scripts/test-core.sh --demo` among candidate checks. [CI](C:/Users/maree/OneDrive/Glthub/marty-workspace/park-commerce-workbench/.tmp/g0008-integration/.github/workflows/verify.yml:28) runs the script without `--demo`; [the script](C:/Users/maree/OneDrive/Glthub/marty-workspace/park-commerce-workbench/.tmp/g0008-integration/scripts/test-core.sh:14) executes `CoreDemo` only when that flag is present. The retained local attempt failed before tests at preceding product source, and no exact-S demo execution is recorded. Run the named check at S or record an authorized, evidence-based selection disposition before readiness. This is a check coverage gap, **not an observed product defect**.

I inspected the source diff, shared `Pricing.Service.calculate`, pricing API and error advice, Vaadin preview consumer, affected tests, CI selection, AC01–AC04 evidence, browser fixture disclosure, and downloaded JUnit XML. The XML digest matches the E2 report and contains the passing exact past-date HTTP case. I found no demonstrated duplicate calculation, sensitive-data disclosure, preview write, candidate substitution during the tested concurrency path, or incompatible transitive consumer change. The controlled browser 400 establishes UI rendering and wiring; it does not establish a real service click. Local Docker cleanup remains unverified and the local Bash attempt failed as disclosed. I ran no tests, Docker commands, workflows, or Git mutations under the read-only instruction.

The final manifest, retention confirmation, and readiness decision remain **administrative gates**. This review grants neither readiness nor release approval.
