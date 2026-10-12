# Checkout-recovery development handoff

## Candidate delivered

The Java booking service now distinguishes active, confirmed and expired checkout
state for an authenticated owner and original retry key. Recovery does not submit
an order, reserve more inventory, or call pricing. Its transaction can reconcile
expired holds. The original purchase still requires an explicit confirmation.

The Angular guest screen now uses a sessionStorage-backed coordinator. The account
identifier comes from a new guest-only session endpoint. Only schema version,
owner, hold ID and retry key are stored; credentials and receipts are not. Unknown
outcomes keep the record, and a receipt is trusted only after backend validation.
Storage failure blocks order dispatch rather than silently using memory alone.

New source is concentrated in:

- `core/.../Booking.java`: Recovery record, locked ledger observation and service API.
- `booking-service/.../BookingApi.java`: `/api/session` and `/api/orders/recovery`.
- `storefront/src/app/checkout-recovery.mjs`: persistence, response checks and shared dispatch.
- `storefront/src/app/app.component.*`: authentication, recovery and explicit cleanup controls.
- `CheckoutRecoveryScenarios.java`, `CheckoutRecoveryIT.java`, and Node coordinator tests.

See [the public contract](../../project/checkout-recovery.md), [slice plan](PLAN.md),
[reload/retry matrix](reload-and-retry-matrix.md), and [observed verification](VERIFICATION.md).

## Run locally without downloaded Java dependencies

```bash
bash scripts/test-core.sh --demo
node --test storefront/src/app/*.test.mjs
bash scripts/test-tooling.sh
node scripts/validate-roadmap.mjs
node --test scripts/validate-roadmap.test.mjs scripts/validate-evidence.test.mjs
```

JDK 21 and Bash are needed for the Java runner; the client tests need Node. The
optional declaration-only check is `npm run test:types` inside `storefront` once
its reviewed TypeScript toolchain is installed. It is not an Angular build.

## Framework and browser gate — still open

1. Resolve/build the Maven backend and optional Vaadin module; build Angular with
   real, reviewed lockfiles (G-0004). Existing dependency pins were not substituted.
2. Execute `bash scripts/verify.sh --integration` with Docker. Seven new tests
   exercise real Spring/MySQL HTTP paths, owner separation, expiry and concurrency.
3. Start the actual services and run `node scripts/demo-http.mjs`. Its 23 assertions
   include discard-and-recover through the real APIs, using a storage fixture.
4. Exercise a real Angular browser: authenticate, reserve, lose a confirmation
   response, reload, reauthenticate and recover the same order. Repeat across
   logout/account switch, server failure, storage denial, expiry, keyboard-only use
   and a narrow viewport. Do not replace these observations with Node unit tests.
5. Perform the actual independent contract/adversarial reviews and human validation
   under the adopted native `git-agent` governance. No independent reviewers ran here.

## Goal state and retained history

This is a bounded candidate toward G-0014-AC02/AC03, not the complete G-0014 goal.
Combined admission/event carts, real event selection UI, completed accessibility
validation, production identity and unmet prerequisite milestones remain open.
The 30 original roadmap proposals are unchanged. No goal is marked ready, accepted,
released, deployed or remotely recoverable by this delivery.

The source ZIP is the easiest continuation artifact. The incremental patch targets
**the immediately previous starter-code ZIP**, not the earlier roadmap archive.
The accompanying Git bundle retains separate local development history, including
an initial missing-feature regression commit. It is not GitHub history; do not
force-push it over the user's existing repository. Overlay source into a local
clone, review changes, then initialize the real `git-agent` locally as planned.

No remote was configured in the local development checkout. No push, repository
visibility change, public release, cloud provisioning, licensed-service setup or
private initializer copying occurred. Verify repository privacy before any future
remote publication.
