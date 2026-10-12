# Reload/retry validation matrix

This is candidate evidence for a bounded part of G-0014-AC02/AC03, not a claim that
the goal's real-browser/cart/accessibility requirements have passed. No criterion
or prerequisite in the original roadmap is changed by this slice.

| Scenario | Expected behavior | Executed coverage | Still required |
| --- | --- | --- | --- |
| Reload with saved owner/hold/key | Reuse original key after backend authentication | Node coordinator recreation + contract server double | Actual Angular/browser/sessionStorage run |
| Lost confirmation response | Recover original order without another allocation | Java order recovery; Node loss/recovery with test server | Spring/MySQL HTTP and browser packet-loss run |
| ACTIVE recovery | Observe only; no confirmation or added capacity | Java + Node | HTTP/database run |
| Confirmed after hold/quote expiry | Return original order; do not call pricing | Java with reconstructed memory store + throwing pricing client | Database/server restart run |
| Exact expiry boundary | Expire and release once; no order | Java controlled clock | MySQL HTTP controlled-clock suite |
| Different owner | Do not restore or return another account's hold/order | Java owner checks + Node owner-scoped storage | Spring auth filters and browser identity switch |
| Key used for a different hold | Conflict, no rebinding | Java + client metadata checks | Real concurrent cross-event database case |
| Logout / token editing | Clear view, retain retry tuple | Node coordinator disconnect | Angular input/logout interactions |
| Network failure / timeout / conflict | Keep tuple; never regenerate key | Node dispatch tests | Real server/network failure |
| Missing/unavailable/full browser storage | Do not send an order without retained identity | Node throwing/discarding storage fixtures | Actual browser privacy/quota conditions |
| Invalid or changed saved record | Fail closed, preserve record for investigation | Node malformed/foreign/changed state tests | Browser inspection |
| Corrupt/mismatched receipt | Reject; retain original identity/receipt | Node response correlation tests | HTTP contract error injection |
| Late reply after logout/new purchase | Cannot replace current owner or checkout | Node deferred-promise tests | Browser request/control interaction |
| Explicit cleanup after confirmed/expired result | Remove only matching tuple; preserve server order | Node state/storage tests | Browser journey |
| Two-stage known receipt corruption | Reject changed amount/changed order ID | Two regressions first failed, then passed during author self-check | Independent contract/adversarial review |

## Artifact classification

- `evidence/final-core.txt`: real JDK-compiled core checks with in-memory test stores.
- `evidence/final-client.txt`: real Node execution; browser storage and transport
  test doubles are explicitly identified in the tests.
- `CheckoutRecoveryIT`: seven **unexecuted** real Spring/MySQL HTTP tests, with
  controlled clock and test-double pricing. They are not marked skipped/passed.
- `scripts/demo-http.mjs`: extended to 23 actual-service assertions; unexecuted here.
- Angular production build, browser recordings, keyboard/narrow-viewport findings,
  independent reviews, human validation and actual goal acceptance: outstanding.
