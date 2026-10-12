# Goal dependency table

Goal dependencies are contractual prerequisites. Refresh dependent evidence whenever relevant accepted inputs change; prior readiness is not permanent. Independent tracks may run in parallel with one writer per branch and isolated resources.

| Goal | Milestone | Dependencies |
| --- | --- | --- |
| G-0001 | M0 | None; bootstrap authority needed |
| G-0002 | M0 | G-0001 |
| G-0003 | M0 | G-0002 |
| G-0004 | M1 | G-0003 |
| G-0005 | M1 | G-0004 |
| G-0006 | M1 | G-0005 |
| G-0007 | M2 | G-0006 |
| G-0008 | M2 | G-0007 |
| G-0009 | M2 | G-0008 |
| G-0010 | M3 | G-0009 |
| G-0011 | M3 | G-0010 |
| G-0012 | M3 | G-0011 |
| G-0013 | M4 | G-0012 |
| G-0014 | M4 | G-0012 |
| G-0015 | M4 | G-0013, G-0014 |
| G-0016 | M5 | G-0015 |
| G-0017 | M5 | G-0016 |
| G-0018 | M5 | G-0017 |
| G-0019 | M6 | G-0018 |
| G-0020 | M6 | G-0019 |
| G-0021 | M6 | G-0020 |
| G-0022 | M7 | G-0021 |
| G-0023 | M7 | G-0022 |
| G-0024 | M7 | G-0023 |
| G-0025 | M8 | G-0017 |
| G-0026 | M8 | G-0017 |
| G-0027 | M8 | G-0009 |
| G-0028 | M9 | G-0021 |
| G-0029 | M9 | G-0003, G-0006, G-0012, G-0020, G-0024 |
| G-0030 | M9 | G-0024, G-0025, G-0026, G-0027, G-0028, G-0029 |

No dates are assumed. Do not start an active implementation merely because a predecessor has source files; prerequisite acceptance and actual inputs must be known. G-0022 scopes cloud decisions; G-0023/G-0024 need their own real authorization.
