# Bounded development candidate — reload-safe event checkout

This local implementation slice follows the user's instruction to continue development.
It contributes to G-0014 (especially AC02/AC03), but does not activate or accept
that goal, satisfy its unmet prerequisites, or change the roadmap's acceptance rules.
No independent review, human validation, release, remote publication, or cloud work
is authorized or represented by this record.

## Intended outcome

After an event checkout response is lost, a guest can authenticate again in the
same browser tab, recover the original owner/hold/key identity and ask the booking
service for the authoritative result without creating another order. A read-only
recovery request never confirms a purchase; confirmation is an explicit action.
Expiry reconciliation may update an expired hold and release its allocation.

## Scope

- A Java booking recovery operation and authenticated HTTP endpoint.
- A guest session-subject endpoint using the authenticated Principal, not user input.
- A sessionStorage-backed Angular checkout coordinator with no stored credentials.
- Regression tests, live HTTP checks for later execution, and retained evidence.

## Fixed acceptance checks for this slice

1. Restoring an unchanged owner/hold/key record never generates a replacement key.
2. Persist and read back retry identity before allowing an order POST. Storage
   failure, corruption, or an unexpected record change blocks dispatch.
3. Owner comes from the authenticated backend. Logging out or editing credentials
   disconnects in-memory state; it does not delete unresolved retry metadata.
4. A server recovery response is ACTIVE, CONFIRMED with the matching order, or
   EXPIRED. Recovery cannot create orders or reserve additional inventory.
5. Confirmed results can be retrieved after hold/quote expiration and reconstructed
   service state. Recovery never depends on pricing availability.
6. Network/auth failures, invalid replies and conflicts retain the retry identity.
   Only a validated confirmed/expired result permits explicit local cleanup.
7. The existing core, client-state, tooling and roadmap checks still pass.
8. Real Spring/MySQL/Angular/browser execution remains a separate required gate;
   test doubles and source inspection are never recorded as passing that gate.

## Boundaries

The current one-event flow only; no combined cart, real payment, new identity
provider, tab-close persistence guarantee, or native agent-runner implementation.
Browser retry metadata is not a credential or an authorization boundary. It is
readable by same-origin JavaScript; protect the application from XSS separately.
A new tab is not a cross-tab coordination system. Retained copies of the same
identity are harmless only because the server enforces checkout idempotency.

A local snapshot is never treated as proof of an order or a price. Reauthentication
and backend reconciliation are required after a reload. No timeout is proof of
failure and no client clock expires or deletes an uncertain checkout record.

## Evidence and ownership

Writer: Development Assistant in this chat. Source base: the supplied starter-code
bundle's `local/starter-code` branch. Candidate and report commits will be separate.
Proposed owner/review authority remains Adam Burdett under the future adopted
`git-agent` baseline; naming that owner does not imply an approval.

Independent reviewers: not assigned. Review gate: outstanding. Status: development
candidate, not goal-ready. Remote recovery coverage: none; delivery is local ZIP,
patch and Git bundle only. Preserve the failing-before-feature test commits.
