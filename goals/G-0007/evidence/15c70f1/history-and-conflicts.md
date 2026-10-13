# Conflicts and stable history at S

Fixed source `15c70f15154997f6eda7b72ad70aa3eba76e5fdd`. The [API contract](../../../../docs/project/catalog-publication.md) defines two separate monotonic counters: draft version and published revision. Saving and publishing require explicit predecessor values; a stale value returns HTTP 409. No command silently rebases or overwrites an earlier document.

## Concurrent writes

`MongoCatalogIT.simultaneousEditorialSavesAndPublicationsHaveOneWinner` starts competing real MongoDB operations and checks one saved draft and one publication survives. `MongoCatalogIT.indexRejectsDuplicateRevision` and `MongoCatalogIT.competingPublishersCannotOverwrite` exercise the unique publication revision key. The 9/9 Mongo suite passed with no skip; see [integration summary](checks/integration-summary.txt). `CatalogEditorialIT.staleWritesAre409AndNeverSilentlyRebase` exercises the actual HTTP 409 mapping. The live [HTTP run](checks/live-http-summary.txt) also reports a stale catalog draft write conflict and stale published-base rejection.

The draft index is unique on `(productId, version)` and the publication index on `(productId, revision)`. The service checks the expected predecessor, then inserts an immutable next record. A race between check and insert is resolved by the unique index; the failed contender gets a conflict. Audit is embedded in the inserted document, so an insert failure creates no separate audit event. The implementation intentionally permits an already saved draft to pin an older published base, which must be explicitly reviewed and rebased before publication.

## History traversal

The [API contract](../../../../docs/project/catalog-publication.md) defines descending published revision order, `limit` 1–50, first-page `snapshotRevision`, and an opaque versioned URL-safe Base64 continuation containing product ID, snapshot upper bound and exclusive last returned revision. It is a range cursor, not an offset; a new publication does not shift an existing traversal. It is neither a credential nor a signed tamper-proof token. Every request remains employee-authorized.

`CatalogEditorialIT.historyHttpCursorIsPinnedAndBadInputsAre400` verifies first and continuation HTTP pages and invalid/product-mismatched cursors against Spring and MongoDB. `MongoCatalogIT.publicationHistoryIsPinnedAcrossNewInserts` verifies retained snapshot order after a newer insert. Core `CoreScenariosTest` covers first, middle, last, empty and invalid cursor cases. The 280/280 core and 17/17 catalog database tests passed in [integration summary](checks/integration-summary.txt). The live HTTP script reports pinned traversal after a new publication. The browser run obtained revisions 2 then 1 via a one-item API page cursor; `nextCursor` was null on the last page. Empty and invalid cases are evidenced by the selected automated tests, not by the browser script.

The behavior assumes append-only API writes and retained history. Out-of-band deletion or direct database modification is outside the accepted cursor contract.
