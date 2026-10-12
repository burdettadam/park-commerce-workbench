# Requirement-to-goal traceability

The first section preserves requirement IDs already present in the source candidate. The second adds **proposed roadmap coverage IDs**, not claims of new implemented functionality. Every ID is covered by at least one goal; acceptance still needs actual criterion evidence.

## Existing source requirements

| Requirement | Meaning | Goal coverage |
| --- | --- | --- |
| CAT-01 | Publish consecutive immutable product revisions | G-0005, G-0006, G-0007 |
| PRICE-01 | Resolve effective-dated variant prices without ambiguous overlap | G-0008, G-0009 |
| PRICE-02 | Preserve an unexpired issued quote when a new rate is published | G-0005, G-0006, G-0008, G-0009, G-0012 |
| PRICE-03 | Explain subtotal, offer, discount, final amount and versions | G-0006, G-0008, G-0009 |
| MONEY-01 | Exact cents, deterministic line rounding, no negative results | G-0005, G-0008, G-0009, G-0012 |
| INV-01 | Adult and child tickets share one event occurrence's capacity | G-0010 |
| INV-02 | Concurrent reservations cannot oversell | G-0005, G-0010, G-0012, G-0018, G-0023, G-0028 |
| HOLD-01 | Hold expires at the earlier of five minutes or quote expiry | G-0006, G-0009, G-0010 |
| ORDER-01 | Same owner/key/hold returns the same order | G-0005, G-0006, G-0010, G-0011, G-0012, G-0014, G-0018, G-0023, G-0028 |
| ORDER-02 | Same key cannot be reused for a different hold | G-0005, G-0010 |
| AUTH-01 | Only employees may publish | G-0006, G-0007, G-0013, G-0015 |
| AUTH-02 | Guests cannot read another subject's quotes/orders | G-0006, G-0009, G-0011, G-0014, G-0015 |
| DEP-01 | Reject invalid pricing responses before touching inventory | G-0010, G-0016 |
| OBS-01 | Trace requests and expose service metrics | G-0017 |
| TOOL-01 | Adopt existing `git-agent` | G-0001, G-0002 |

## Proposed additional coverage

| Requirement | Meaning | Goal coverage |
| --- | --- | --- |
| WORKFLOW-01 | Actual adoption and canonical plan/goal/branch authority. | G-0001, G-0003, G-0029 |
| WORKFLOW-02 | Independent review and fail-closed acceptance controls. | G-0002, G-0020, G-0029 |
| WORKFLOW-03 | Worktree isolation, retained checkpoints and recoverable handoff. | G-0003, G-0029 |
| BUILD-01 | Real full-stack compilation, locks and required database suites. | G-0004, G-0005 |
| CAT-02 | Draft/publication history, stable pagination and audit. | G-0007, G-0013 |
| PRICE-04 | Side-effect-free unpublished price preview. | G-0008, G-0013 |
| ADMISSION-01 | Fictional dated admission entitlement checkout. | G-0011 |
| CART-01 | Bounded admission/event cart consistency. | G-0012, G-0014 |
| CANCEL-01 | Authorized idempotent cancellation and inventory/entitlement reconciliation. | G-0012 |
| UI-01 | Usable Vaadin employee journey. | G-0013 |
| UI-02 | Usable Angular journey and reload-safe uncertain checkout. | G-0014 |
| SEC-01 | Deployable identity/trust boundaries and secret/access controls. | G-0015, G-0016, G-0019, G-0020, G-0022, G-0023 |
| CONTRACT-01 | HTTP/schema compatibility, bounded failures and safe error behavior. | G-0016 |
| OBS-02 | Executed observability and investigation, not merely dependencies. | G-0017, G-0025 |
| SQL-01 | Real SQL plans, indexing and contention evidence. | G-0018 |
| PERF-01 | Measured, reproducible workload with correctness invariants. | G-0018, G-0028 |
| RECOVERY-01 | Artifact/data/environment recovery actually demonstrated. | G-0018, G-0019, G-0021, G-0022, G-0024 |
| DELIVERY-01 | Identifiable, retained distributable artifacts from fixed source. | G-0019, G-0021, G-0030 |
| DELIVERY-02 | Actual pipelines with enforceable failure/authority boundaries. | G-0020 |
| K8S-01 | Actual replicas, probes, rollout and rollback. | G-0021, G-0023 |
| AWS-01 | Authorized EKS/Aurora/private environment and lifecycle. | G-0022, G-0023, G-0024 |
| SPLUNK-01 | Actual ingestion and SPL investigations. | G-0025 |
| APPDYNAMICS-01 | Actual AppDynamics Java transaction diagnosis. | G-0026 |
| GRAPH-01 | Real bounded Neo4j query exercise, separate from GraphQL. | G-0027 |
| WORKFLOW-04 | Evidence-backed workflow assessment and truthful claims. | G-0029, G-0030 |
| RELEASE-01 | Private reproducible release candidate, truthful evidence and human/publication gates. | G-0030 |

## Stack coverage remains explicit

Java/Spring/GraphQL: M1–M5. MongoDB/MySQL/SQL: M1–M3/M5. Vaadin/Angular: M1/M4. Docker/Jenkins/GitHub/Kubernetes: M6. Actual Aurora/EKS: M7. OpenTelemetry/Prometheus/Tempo/Grafana: M5. Splunk/AppDynamics/Neo4j: M8. A local MySQL run is not Aurora evidence; GraphQL is not Neo4j evidence; Grafana is not Splunk/AppDynamics evidence.

## Original backlog crosswalk

The earlier G0–G6 headings were broad planning themes, not accepted goal identities. This roadmap replaces their active navigation while retaining their intent.

| Earlier heading | New bounded coverage |
| --- | --- |
| G0 adoption | M0 / G-0001–G-0003 |
| G1 compile/verify | M1 / G-0004–G-0006 |
| G2 contracts/operations | G-0010 and M5 / G-0016–G-0018 |
| G3 product capabilities | M2–M4 / G-0007–G-0015 |
| G4 AWS/Kubernetes | M6–M7 / G-0019–G-0024 |
| G5 operations tools | G-0017, G-0025 and G-0026 |
| G6 graph/portfolio | G-0027 and M9 / G-0028–G-0030 |
