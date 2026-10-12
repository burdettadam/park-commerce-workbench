# G-0011 coverage — candidate only

| Criterion/artifact | Implemented candidate evidence | Remaining acceptance gate |
| --- | --- | --- |
| AC01 / A01 admission-examples.json | Exact quote, grouped entitlement set, owner/key replay; literal fixtures at contracts/admission/admission-examples.json; named core cases | Actual Spring/Jackson/SQL results and intended-use validation |
| AC02 / A02 admission-isolation-tests.md | Core no-inventory dependency, event before/after checks, invalid snapshot/owner/date cases, contention cases | Real connections, filter/HTTP and uniqueness/rollback tests |
| AC03 / A03 entitlement-contract.md | docs/project/admission-checkout.md defines ACTIVE, reserved VOID, immutable audit and separate route | Independent contract review and acceptance |
| AC04 / A02 upgrade | Additive V2, unchanged V1, V1-event-to-V2 mixed-read MySQL test source | Run the disposable upgrade fixture; cannot be passed by source inspection |

Implementation work was authorized by the user's request to continue local work.
Neither G-0010, G-0011 nor native initialization is marked ready or complete.
No approved goal-wide integration ref, independent reviewer results, version tag,
human acceptance or cloud checkpoint is invented. Artifacts remain local_only.
