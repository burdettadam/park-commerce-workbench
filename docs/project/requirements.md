# Functional requirements and current boundary

| ID | Requirement | Candidate implementation / evidence |
| --- | --- | --- |
| CAT-01 | Publish consecutive immutable product revisions | Core scenarios; Mongo store and integration tests written |
| PRICE-01 | Resolve effective-dated variant prices without ambiguous overlap | Core tests for date boundaries, variants, missing rates |
| PRICE-02 | Preserve an unexpired issued quote when a new rate is published | Core walkthrough; JPA round-trip test and HTTP script written |
| PRICE-03 | Explain subtotal, offer, discount, final amount and versions | Core records, REST/GraphQL schema, UI source |
| MONEY-01 | Exact cents, deterministic line rounding, no negative results | Core tests including 230 valid party compositions |
| INV-01 | Adult and child tickets share one event occurrence's capacity | Core tests; MySQL event-lock design |
| INV-02 | Concurrent reservations cannot oversell | Executed in-memory concurrency tests; SQL/HTTP tests pending |
| HOLD-01 | Hold expires at the earlier of five minutes or quote expiry | Core tests for exact boundary and expiry |
| ORDER-01 | Same owner/key/hold returns the same order | Core repeated/concurrent tests; SQL unique keys and test source |
| ORDER-02 | Same key cannot be reused for a different hold | Core tests including cross-event conflict; SQL rollback test source |
| AUTH-01 | Only employees may publish | Spring Security source; HTTP integration test pending |
| AUTH-02 | Guests cannot read another subject's quotes/orders | Core tests; API/GraphQL/live exercise pending |
| DEP-01 | Reject invalid pricing responses before touching inventory | Executed core tests |
| OBS-01 | Trace requests and expose service metrics | OTel/Prometheus source; no end-to-end telemetry verification |
| TOOL-01 | Adopt existing `git-agent` | Local handoff only; user initialization pending |

## Product policy

Products are ADMISSION or EVENT and expose ADULT and/or CHILD variants. Counts are 0-20 per variant with total 1-20. The application does not infer or verify a customer's age. A request's adult/child choices are inputs, not proof of eligibility.

A quote represents a single product on one date in USD. A party of four or more may receive the configured family offer. No tax engine is included; demonstration amounts are synthetic, and totals must not be described as compliant real-world tax calculations.

The first purchase path supports **EVENT** products. Admission checkout and combining multiple product families are not yet supported. An event occurrence ID must exist and match the quote's product and visit date.

## Explicitly not done

Production OIDC/OAuth integration, real identities, payments, payment reconciliation, admission entitlement issuance, QR admission scanning, real age/residency eligibility, refunds, cancellations, multi-product carts, tax, currency conversion, accessibility audit, hosted UI, cloud infrastructure, reliable backup/restore, load testing, rate limiting, signed supply-chain evidence, Splunk/AppDynamics runtime demonstrations, and Neo4j are separate goals. This list does not mean those are irrelevant; it prevents incomplete scaffolding from being represented as finished engineering work.
