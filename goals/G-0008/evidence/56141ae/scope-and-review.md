# G-0008 fixed candidate and review scope

- Source S: `56141ae40f05704ff0ae30b232ef878e2943b6c6`; base `bd26cfbd062228c228223518d3800cd125c4e9b1`.
- Accepted plan P: `717c31b04eca3f6478efa50c25dd666d3de73057`; exact bytes are in `accepted-plan.md`, SHA-256 `cb89fb489c5b53bbb65469d441ebdea820f73c5f15e5843154b890779147722a`.
- G-0008 product change: `service-support/ApiErrors.java` surfaces safe preview-only invalid-input details; `CommerceView.java` shows only two known harmless remote details in the employee preview action. `Pricing.Service.calculate` is shared by preview and quote issuance. Other code changes are focused tests and the isolated standard-runner `live-stack` job.
- The first independent review was at prior source `1009453` and is retained under that candidate's `reviews/`. It blocked readiness on evidence and a UI error-display concern. The suggested 21-ticket reproduction was inaccurate for the UI because local validation rejects it before HTTP; `round1-disposition.md` records the correction and the reachable past-date case.
- Current source S adds the `JpaPricingIT` past-date real HTTP assertion after the successful S2 `98b8283` product/UI pass. The admin JAR exercised in the browser was built at product commit `3ededd1`, whose product sources match S; only a pricing integration test and goal records changed afterward.
- This directory separates the controlled browser 400 fixture from real service checks. A fixture click alone cannot establish the real API response; the hosted `JpaPricingIT` and six-service checks are required.
- Local Docker Desktop and the default Windows Linux `bash.exe` failed during attempted additional checks. Those attempts remain disclosed. The exact `--demo` check later passed at S through Git Bash; free hosted checks at S are the acceptance source for real databases and cross-service HTTP.
- No paid runner, cloud deployment, version tag or release is included. Readiness, main merge and release are separate decisions.

The independent second review round, intended-use validation, manifest, readiness decision and remote retrieval record must use this exact S and the completed current-run evidence. This file is a scope record, not a passing review.
