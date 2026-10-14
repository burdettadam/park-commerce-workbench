# Foundation archive verification — historical record

Observed on **11 October 2026 UTC** for the original `park-commerce-workbench-foundation-2026-10-11.zip`. Its original source manifest is available in the [historical snapshot](https://github.com/burdettadam/park-commerce-workbench/blob/90ae8eff6485c950c8d02c8d3a33893059f5d82e/docs/roadmap/baseline/SOURCE-MANIFEST.sha256); see `docs/roadmap/DELIVERY-VERIFICATION.md` for the later roadmap delivery.

The candidate ZIP was extracted into a directory whose path contains spaces. Its `SOURCE-MANIFEST.sha256` entries were checked against the extracted files. The included scripts were then run from the extracted project root:

| Extracted-project check | Result |
| --- | --- |
| `sha256sum --quiet -c SOURCE-MANIFEST.sha256` | All listed file hashes matched |
| `bash scripts/test-core.sh --demo` | 77 passed, 0 failed; protected-price/idempotency walkthrough completed |
| `node --test storefront/src/app/checkout-state.test.mjs` | 10 passed, 0 failed |
| `bash scripts/test-tooling.sh` | 11 passed; initializer forwarding used a mock |

The final archive was regenerated to include this record and its checksums, then extracted and checked again. No generated `.env`, Git administration, test build output, or private initializer was copied back into the delivered source.

This is evidence for the extracted core, retry-state, and local wrapper checks. It is **not** a Maven/Spring, database, Docker, Angular, Vaadin, cloud, or real `git-agent` execution result. Those boundaries remain as described in [verification.md](verification.md).
