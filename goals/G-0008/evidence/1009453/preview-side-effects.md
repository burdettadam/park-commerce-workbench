# G-0008 preview side effects and candidate identity

Source S: `100945358c88e4c72a7c444326c618e19b054180`. `checks/core-demo.txt` exited 0 with 280 passed, 0 failed. These core checks use in-memory test stores. Hosted database checks passed 67/67 with zero skips, and 43 live HTTP assertions passed against an isolated six-service stack; see `checks/hosted-ci.txt`, `checks/hosted-junit.txt` and `checks/live-stack.txt`.

| Invariant | Selected observation at S |
| --- | --- |
| Preview does not read or write the pricing store | `PricingPreviewScenarios` passed against a `ForbiddenStore` whose methods throw on access. |
| Preview does not create a purchasable quote | The preview record lacks ID, owner and expiry fields; the core case passed. |
| Existing price, quote, order and capacity remain unchanged | Five preview calls were followed by equality assertions on published price, prior quote, confirmed order and event availability; the core case passed. |
| Exact candidate stays explicit during concurrent publication | The core test blocks preview at catalog lookup, publishes a different version 2, resumes preview, and asserts the original candidate, $340 subtotal, $34 discount and $306 total. The old quote is unchanged, a newly issued quote uses the $125 rate, and republishing the stale candidate conflicts; the core case passed. |
| Candidate fingerprint changes with exact rate, version, offer, date, catalog revision or input order | Deterministic and changed-input fingerprint core cases passed. |

`JpaPricingIT` includes database assertions that preview leaves price-book and quote row counts unchanged and that publication of the previewed candidate yields the same lines. The live-stack job passed checks that an employee preview does not publish its candidate and that prior issued quotes remain immutable after a later publication. Neither proves an inventory/order invariant through the live stack; the core check covers that path. The browser UI has not been exercised at S.
