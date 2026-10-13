# G-0003-A02: fresh-session recovery drill

Fixed source S: `4a3d26ea99be5deeac1c0afd94b9cc4443efd9c6`. Accepted plan P: `4a0d6a66a4d51eca57059a88875fb6efe7ab687e`. Checkpoint report commit: `9cf8b4ac676a033f9ae7d0ae55febd570a7d3a82`. This report is later evidence about S, not a change to S.

The independent validation writer was running a read-only authoring audit when the integration session interrupted it. The interruption tool returned `previous_status: running`; its unretained draft was not used for recovery. Before the fresh session began, the integration writer retained a complete local Git bundle and a separate JSON digest inventory in ignored `.tmp`. The fresh recovery agent received only those paths, their expected SHA-256 values, and the recovery task. It received no previous conversation turns.

| Event | UTC |
| --- | --- |
| Bundle/inventory checkpoint | 2026-10-13 00:40:18.641397 |
| Fresh recovery began | 2026-10-13 00:41:45.0688493 |
| Useful work resumed | 2026-10-13 00:42:45.2254394 |

Checkpoint-to-resumption handoff: **146.584 seconds**. Fresh recovery execution: **60.157 seconds**. The interruption itself occurred before the recovery session; the coordination tool did not supply a separate timestamp, so no precise interruption-to-resumption duration is claimed.

The bundle (`.tmp/g0003-checkpoint.bundle`, 750,958 bytes) matched SHA-256 `90cf021a85c6899fcfcf5de434a829f4c6642efe443d14e86064950ccb373d4a`; the inventory (`.tmp/g0003-recovery-inventory.json`) matched `4b090bcfb7a16161aea055fea28026252b693713489caaee5b18035993e651ef`. `git bundle verify` reported a complete history. The fresh session initialized a new repository in ignored `.tmp/g0003-fresh-recovery-e6dd5d7c`, fetched the bundle's three refs, and configured no remote. The three heads matched the inventory: integration `9cf8b4a`, implementation `ecc1439`, validation `7eed2b0`.

The fresh session checked that P and both included slice heads are ancestors of S. It found the shared-default-Compose alternative documented as nonincluded in `goals/G-0003/work/integration.md`; no corresponding implementation/ref was in the bundle. All **23 indispensable files** matched the inventory digests. At S, `SOURCE-MANIFEST.sha256` verified **487 of 487** entries with zero missing or mismatched. Product source at S was unchanged from the G-0002 ready base; G-0003 adds the controlled workflow records and runtime evidence.

Fresh read-only checks exited zero: `node scripts/validate-roadmap.mjs`; `node --test storefront/src/app/*.test.mjs` (**66 passed**); and `node --test scripts/validate-roadmap.test.mjs scripts/validate-evidence.test.mjs` (**46 passed, one host symlink test skipped** because Windows denied symlink creation with EPERM). The source-checkout and runtime checks recorded elsewhere remain distinct; these fresh checks test the recovered checkout and validators.

The recovery agent's local raw JSON and Markdown reports remain in that ignored fresh directory under `.tmp`; their SHA-256 values are `7498c470e7eb77de9ba83f6060bd02c386c54a6731557ff709a332c7a3c1bc4f` and `23d1d5525f4e763ceeafeb0c05ebc18309ef733b30286448e6b3a8e579f1b63`, respectively. They are local-only because they contain a host path. The bundle/inventory and the fresh clone are local-only recovery artifacts under the integration writer's custody. The public branches and sanitized reports are independently retrievable from origin; this local bundle is **not** an off-device backup.

Private `.env` values and database volumes were outside this checkpoint and were not restored. This result proves recovery of the retained source and indispensable work artifacts for this goal, not recovery of the live runtime or its data. The resumed next action, identified from retained records, was to exercise the stale-review and missing-artifact gates, then obtain current native reviews and intended-use validation.
