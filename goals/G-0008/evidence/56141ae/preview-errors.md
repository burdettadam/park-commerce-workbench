# G-0008 preview validation evidence

Source S: `56141ae40f05704ff0ae30b232ef878e2943b6c6`. The hosted Java job at clean source S reported 280/280 core cases and 68/68 real database integration cases with zero skips; see `checks/hosted-ci.txt` and `checks/hosted-junit.txt`. A prior-source local Bash rerun failed before tests because its service did not respond; see `checks/windows-bash-interrupted.md`. Hosted `JpaPricingIT` at S passed 9/9 with zero failures, errors or skips; see `checks/hosted-ci.txt` and `checks/hosted-junit.txt`.

| Invalid input | Expected response | Exercised assertion |
| --- | --- | --- |
| Two ADULT rate intervals share an inclusive endpoint | HTTP 400 `INVALID_INPUT`, detail contains `Overlapping date ranges for ADULT` | `JpaPricingIT.httpPreviewRejectsMalformedAndOverlappingInputsWithoutWrites` |
| 21 tickets in one party | HTTP 400 `INVALID_INPUT`, detail contains `A party must have between 1 and 20 tickets` | Same HTTP test; core out-of-range and empty-party cases also passed |
| Fractional adult count | HTTP 400 `INVALID_INPUT`, detail contains `whole-number ticket counts` | Same HTTP test |
| Empty or null request envelope | HTTP 400 `INVALID_INPUT`; no price or quote write | Same HTTP test |
| Candidate lacks a rate for the requested date or variant | Failure rather than a fallback price | `PricingPreviewScenarios` no-uncovered-date case passed in `checks/hosted-ci.txt` |
| Past visit date accepted by the form's local request record | HTTP 400 `INVALID_INPUT` with exact park-time-zone repair message; zero price-book and quote writes | `JpaPricingIT.httpPreviewPastVisitReturnsTheEmployeeRepairMessageWithoutWrites` passed in hosted real-database suite (9/9); employee browser showed the same detail from a controlled 400 fixture |

`ApiErrors` unwraps only a validated `Failure.Code.INVALID_INPUT` from Jackson for the preview route. `PreviewErrorContractTest` passed 3/3 checks that an unrelated conversion failure produces a fixed repair hint and that other routes keep their generic malformed-request response. The hosted HTTP assertions establish these API responses at S. The browser form sent a past-date preview to a controlled loopback HTTP 400 fixture and displayed its actionable detail; see checks/browser-preview-error.md. The hosted service checks separately verify the real API. This browser exercise does not claim an end-to-end real-server click.
