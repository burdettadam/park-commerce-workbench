# G-0008 fixed candidate and review scope

- Source S: `100945358c88e4c72a7c444326c618e19b054180`; base `bd26cfbd062228c228223518d3800cd125c4e9b1`. The worktree was clean before the source-S checks. Later files in this evidence directory describe S and are not part of S.
- Accepted plan P: `717c31b04eca3f6478efa50c25dd666d3de73057`; exact bytes in `accepted-plan.md`, SHA-256 `cb89fb489c5b53bbb65469d441ebdea820f73c5f15e5843154b890779147722a`.
- Changed product path: `service-support/ApiErrors.java` exposes validated preview-only input details without echoing arbitrary Jackson failures. `pricing-service/JpaPricingIT.java` and `PreviewErrorContractTest.java` cover actual HTTP and safe error behavior. The canonical `Pricing.Service.calculate` remains shared by preview and issued quote. The new workflow job exercises the existing six-service HTTP demo on a unique free public runner.
- Relevant configuration identities are in `configuration.json`; synthetic fixture inputs are in `preview-fixtures.json`. No private `.env` values are retained.

| Criterion | Current evidence | Still open |
| --- | --- | --- |
| AC01 | Hand-calculated 34000−3400=30600 cents; core, real database and live HTTP preview checks passed. | Independent review and intended-use validation. |
| AC02 | Core monetary-line parity; real HTTP integration and six-service exact publication parity passed; quote ID/expiry asserted in `JpaPricingIT`. | Independent review and intended-use validation. |
| AC03 | Core negative cases, HTTP 400 details in `JpaPricingIT`, 3/3 safe error contract tests passed. | Employee browser rendering remains unexercised; review and validation. |
| AC04 | Core side-effect and concurrent publication cases passed; database count assertions and live-stack nonpublication/prior quote checks passed. | Browser check, review and validation. |

`checks/core-demo.txt` is the complete local raw output. `checks/hosted-junit.txt` contains the downloaded Surefire/Failsafe text reports. `checks/hosted-ci.txt` and `checks/live-stack.txt` retain sanitized exact lines and link the full public hosted logs with SHA-256. `checks/windows-compose-interrupted.md` records the failed local attempt without claiming its missing full raw transcript. Hosted run `38476661000` passed all four jobs at S; the `live-stack` job reported 43 checks and scoped cleanup. No readiness, human release approval, main merge, deployment or browser behavior is claimed here.
