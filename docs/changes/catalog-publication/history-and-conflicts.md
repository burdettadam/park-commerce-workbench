# History and conflicts — G-0007-A02 candidate material

## Publication traversal

For published revisions [1, 2, 3], requesting two rows returns [3, 2], snapshot 3,
and a continuation boundary of 2. Publishing revision 4 before the next request
does not change that traversal: its last page is [1], snapshot 3. A fresh first
page sees revision 4. These expected sequences are asserted explicitly in the
JDK-only tests and separately specified in real Mongo/HTTP tests.

First, singleton, multi-page, empty, replayed and changed-size pages are covered.
The tests also move the clock backward without changing revision ordering, reject
malformed/product-mismatched/oversized/noncanonical cursors and invalid page sizes,
check exhausted long counters, and reconstruct a fixture store before resuming.
Cursor parsing is not cryptographic verification; an authorized caller can create
another valid range. The server rechecks authorization and retained boundaries.

## Forced races, not just sequential duplicate calls

Two tests force **24 operations** past the service's version reads before any
insert occurs. A CyclicBarrier releases them together. Exactly one save and,
in the separate test, exactly one publication succeeds. The other operations
must fail with CONFLICT; another exception, cancelled future, or missing writer
fails the test. One published record contains both the winner's snapshot and audit.

These races use synchronized *test-memory* insert methods as the Store contract.
They are useful domain observations but not proof of Mongo's unique indexes.
The new real-Mongo suite separately declares 16-writer concurrent save/publication
checks; it remains unexecuted without Docker and dependencies.

Three deterministic interleavings additionally assert:

1. A direct publication wins after a draft save's base check. The saved draft pins
   the old base and cannot publish until explicitly rebased.
2. A later draft is saved after the publisher chose its draft. Publication retains
   the originally selected content; the new draft remains unchanged.
3. A rival publication wins after the losing publisher checked the base. The
   loser cannot insert its snapshot or a separate audit event.

No global cross-stream atomicity or automatic idempotent publication replay is
claimed. Draft and publication inserts are separate operations with documented
interleavings. Real Mongo indexes, legacy-record mapping, query plans, role filters,
serialization and browser interactions still need workstation execution.
