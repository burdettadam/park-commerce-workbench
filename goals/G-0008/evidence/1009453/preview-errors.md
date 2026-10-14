# G-0008 preview validation evidence

Source S: `100945358c88e4c72a7c444326c618e19b054180`. The clean-source `checks/core-demo.txt` run exited 0 with 280 passed, 0 failed. Hosted `JpaPricingIT` at S passed 8/8 with zero failures, errors or skips; see `checks/hosted-ci.txt` and `checks/hosted-junit.txt`.

| Invalid input | Expected response | Exercised assertion |
| --- | --- | --- |
| Two ADULT rate intervals share an inclusive endpoint | HTTP 400 `INVALID_INPUT`, detail contains `Overlapping date ranges for ADULT` | `JpaPricingIT.httpPreviewRejectsMalformedAndOverlappingInputsWithoutWrites` |
| 21 tickets in one party | HTTP 400 `INVALID_INPUT`, detail contains `A party must have between 1 and 20 tickets` | Same HTTP test; core out-of-range and empty-party cases also passed |
| Fractional adult count | HTTP 400 `INVALID_INPUT`, detail contains `whole-number ticket counts` | Same HTTP test |
| Empty or null request envelope | HTTP 400 `INVALID_INPUT`; no price or quote write | Same HTTP test |
| Candidate lacks a rate for the requested date or variant | Failure rather than a fallback price | `PricingPreviewScenarios` no-uncovered-date case passed in `checks/core-demo.txt` |

`ApiErrors` unwraps only a validated `Failure.Code.INVALID_INPUT` from Jackson for the preview route. `PreviewErrorContractTest` passed 3/3 checks that an unrelated conversion failure produces a fixed repair hint and that other routes keep their generic malformed-request response. The hosted HTTP assertions establish these API responses at S. No browser rendering of the employee-facing message is claimed yet.
