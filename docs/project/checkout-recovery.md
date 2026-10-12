# Same-tab checkout recovery contract

This is an implementation candidate for the current **one-evening-event** journey,
not completion of G-0014, combined-cart checkout, or production authentication.

## User-visible flow

1. Enter the local guest token and choose **Authenticate and recover saved checkout**.
   The booking service supplies the authenticated subject. The client never asks
   the user to supply an account ID or uses the bearer token as a storage key.
2. If metadata exists for that subject, load its original hold and retry key, then
   call the recovery endpoint. This checks state; it does not confirm a purchase.
3. For an ACTIVE hold, choose **Confirm saved simulated purchase** explicitly. For
   a CONFIRMED result, show the original receipt. For EXPIRED, permit cleanup.
4. A timeout, unavailable service, conflict, invalid response or authentication
   failure does not clear retry metadata or generate a new key.
5. **Clear resolved checkout and start another purchase** is available only after
   a validated confirmed/expired server result. This clears local metadata, not a
   server order. It does not cancel anything or issue a refund.

The booking service serializes recovery with confirmation using its existing
exclusive event-ledger transaction. Looking up an ACTIVE hold does not reserve
more capacity, extend its expiry, or create an order. Recovery may persist
expiry and release an unconfirmed allocation. A confirmed order stays confirmed
when its old quote or hold deadline has passed. The recovery path never calls
pricing, so a pricing outage does not prevent retrieving a booked result.

## API

### GET /api/session

Requires the GUEST role. Returns `200` and `Cache-Control: no-store`:

```json
{"subject":"guest"}
```

The value comes from the authenticated `Principal`. Anonymous credentials return
401; the local employee credential is not a guest-session credential and returns
403. This endpoint is a bridge for the existing local demo identity, not a new
identity provider. Production issuer/subject semantics remain G-0015 work.

### POST /api/orders/recovery

Authenticated bearer credential and the **original** `Idempotency-Key` header:

```json
{"holdId":"00000000-0000-4000-8000-000000000001"}
```

Returns `200` and `Cache-Control: no-store` with a `hold` and an explicit `order`
property. `hold.status` is ACTIVE, CONFIRMED, or EXPIRED. `order` is `null` for
ACTIVE/EXPIRED and the complete original order for CONFIRMED. It must match the
hold, authenticated owner, retry key, quote, event, quantity and total.

A POST keeps the retry key out of URLs and acknowledges expiry reconciliation;
it does not authorize confirmation. Purchases still use the existing
`POST /api/orders` with the same body and key. No changed order schema or database
migration is required by this addition.

| Outcome | HTTP behavior | Client rule |
| --- | --- | --- |
| Owned active hold | 200 / ACTIVE / null order | Keep identity; require explicit confirmation |
| Matching committed order | 200 / CONFIRMED / original order | Show validated receipt; allow explicit cleanup |
| Expired unconfirmed hold | 200 / EXPIRED / null order | Do not confirm; allow explicit cleanup |
| Missing or another owner's hold | 404 | Retain metadata; do not start a guessed replacement |
| Key belongs to another hold or confirmed under another key | 409 / CONFLICT | Retain metadata; investigate/reconcile |
| Invalid ID or key | 400 | Fail closed; do not regenerate identity |
| Unauthenticated / unauthorized | 401 / 403 | Reauthenticate; retain original metadata |
| Unavailable or invalid response | Failure / timeout | Unknown outcome, not proof of failed purchase |

An ACTIVE observation can become stale immediately after its transaction ends.
It is not permission to delete the retry record: an earlier order request may be
in flight. An explicit confirmation still uses the same identity and is protected
by the existing server idempotency rules.

## Stored metadata and lifecycle

One sessionStorage slot per authenticated subject:

```text
park-commerce:checkout:v1:<encoded-subject>
```

```json
{
  "version": 1,
  "owner": "guest",
  "holdId": "00000000-0000-4000-8000-000000000001",
  "key": "original-retry-key"
}
```

Only those four fields are accepted. No token, Authorization header, credentials,
quote price, order receipt, customer name or card information is stored. The record
is not proof of authorization, a price, a confirmation or a refund. It is validated
again when loaded and before dispatch. Receipt state exists only in memory and
must be recovered from the server after a reload.

The coordinator writes and reads back this record before allowing any order POST.
Unavailable storage, malformed JSON, unsupported versions, foreign-owner records,
unknown fields, oversized payloads or changed record identity stop dispatch. The
UI does not silently fall back to memory-only checkout. A failed save after a
successful reserve leaves an unconfirmed hold to expire; no order is submitted.

Logout and token editing disconnect in-memory receipt/identity state but preserve
saved retry metadata. Reauthentication with a different subject does not load the
first account's pending checkout. Logging back into the first account restores it.
Form edits and new-quote actions cannot erase a pending purchase. Only a validated
terminal outcome enables explicit removal, with storage deletion checked before
allowing another purchase.

There is no browser-clock-based timeout that discards uncertainty. An old record
can still identify a successful order. Changed/corrupt storage or a reset backend
may need manual developer reconciliation; the UI deliberately does not pretend
those conditions prove no order exists.

## Limits and threat boundary

- This is **same-tab reload** recovery using sessionStorage. Closing the tab,
  clearing site data or losing the browser session is not a supported recovery
  guarantee. This is not cross-device order history.
- A new tab may receive a copy of an opener's sessionStorage. Copies of the same
  checkout identity must still yield the same server order. The application is
  not a multi-tab coordination or locking system.
- Metadata is noncredential data, but same-origin JavaScript can read/alter it.
  It is not encrypted or protected from XSS. Validation prevents accidental use of
  malformed identities; the server's owner checks are the authorization boundary.
- No real payment is accepted. Claims about duplicate financial charges would be
  inappropriate; the implemented invariant concerns simulated orders/capacity.
- No assumption is made that offline unit checks prove Spring filter behavior,
  database transactions, Angular templates, actual storage in a browser, or
  keyboard/narrow-viewport usability. Those remain separately required checks.

## Executable checks

```bash
bash scripts/test-core.sh --demo
node --test storefront/src/app/*.test.mjs
```

On a reviewed dependency-enabled workstation:

```bash
bash scripts/verify.sh --integration
node scripts/demo-http.mjs
(cd storefront && npm run test:types && npm run build)
```

The HTTP exercise uses actual running Spring services but a Map-backed storage
fixture and a fresh coordinator, not a real browser reload. It deliberately discards
a confirmation receipt and asks the server for it again. The database HTTP suite
uses real Spring/MySQL with a controlled clock and test-double pricing client.
Actual browser observations belong in the G-0014 validation record.

## References and implementation choices

- [MDN sessionStorage](https://developer.mozilla.org/en-US/docs/Web/API/Window/sessionStorage):
  tab-scoped lifetime, reload persistence, opener copying and storage exceptions.
- [Spring MVC security integration](https://docs.spring.io/spring-security/reference/7.0/servlet/integrations/mvc.html):
  accessing authenticated identity through Spring's MVC/security integration.
- Exact storage namespace, payload, terminal-state rules, API shape and cleanup
  policy above are **project decisions**, not claims imposed by those platforms.
