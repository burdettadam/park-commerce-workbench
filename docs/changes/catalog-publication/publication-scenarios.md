# Publication scenarios — G-0007-A01 candidate material

Observed domain checks use a controlled clock and `MemoryStores.CatalogStore`.
The frozen-source test log is `evidence/final-core.txt`. Mongo/HTTP assertions are
written but not executed here. These are not independent-review conclusions.

| Case | Independent expected behavior | Implemented check |
| --- | --- | --- |
| Save new product draft | No guest product, revision or list entry yet | Draft invisibility scenario; HTTP REST/GraphQL test |
| Save draft 2 after draft 1 | Draft 1 remains readable and immutable | Historical draft and store-uniqueness scenarios |
| Publish draft 1 after draft 2 exists | Guest gets draft 1; draft 2 remains retained | Exact-content scenario and forced edit/publication interleaving |
| Save a new draft after publication | Guest keeps current published snapshot | Guest-visible previous-publication scenario |
| Another employee publishes first | Selected stale base fails; no auto-rebase | Pinned-base and competing-publication scenarios |
| Rebase explicitly | Save a new version based on the new publication | Explicit rebase scenario |
| Lost response followed by repeated command | Old expected version conflicts; inspect immutable state | Duplicate/stale-write tests; console guidance, not browser evidence |
| Old schema with no audit fields | Original product readable; provenance remains unknown | Domain legacy scenario; literal legacy Mongo fixture test |
| Caller attempts actor injection | Server accepts only authenticated actor, not payload actor | HTTP test using actual security/JSON handling; unexecuted |
| Existing immediate-publication caller | Original Product request/response and new embedded audit | Core direct-publication and compatibility HTTP test |

New source lives in `CatalogEditorial.java`, `MongoCatalogStore.java`,
`CatalogEditorialApi.java`, and the updated `CatalogApi`/Vaadin `CommerceView`.
Guest DTO/GraphQL schema, pricing and booking runtime source are unchanged.

The audit JSON example is extracted from an executed controlled Java fixture, not
from MongoDB, an HTTP access log, a real staff identity, or a production operation.
