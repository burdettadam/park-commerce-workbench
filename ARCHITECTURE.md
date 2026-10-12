# Architecture and product boundaries

This map points to the imported source and the detailed [product architecture](docs/project/architecture.md), [API contracts](docs/project/api-contracts.md), and [decision record](docs/project/decisions.md). The service boundaries remain candidates until framework and deployment checks establish them.

## Maintained map

| Module/product | Responsibility and owner | Public contract | Dependencies and consumers | Required checks/lane |
| --- | --- | --- | --- | --- |
| `core` | Shared pure-Java domain rules | Java types and scenario fixtures | Used by service modules | `scripts/test-core.sh`; Maven reactor |
| `service-support` | Local auth, HTTP error and telemetry support | Java module API | Used by backend services | Maven reactor |
| `catalog-service` | Product revisions and publication in MongoDB | REST/GraphQL; `docs/project/catalog-publication.md` | Pricing and clients consume published catalog | Core, Mongo integration, contract checks |
| `pricing-service` | Dated rates, previews and versioned quotes in MySQL | REST/GraphQL; `docs/project/pricing-contract.md` | Booking and clients consume quotes | Core, pricing contract, MySQL integration |
| `booking-service` | Holds, orders and admission entitlements in MySQL | REST; `docs/project/admission-checkout.md` | Guest client | Core, booking integration, HTTP exercise |
| `admin-console` | Employee UI candidate | Vaadin views | Backend service APIs | Optional `-Pui` build and browser exercise |
| `storefront` | Guest UI candidate | Angular application and state contract | Backend service APIs | Node state tests, Angular build and browser exercise |

Include standalone modules/interfaces even when they are not deployable products.
Keep native dependency declarations authoritative. Explain intended dependency
direction and cycles that need resolution. Link generated code to its source and
generator. Changes to the impact map itself warrant broader verification.

## Decision guidance

Prefer cohesive modules with explicit public interfaces and tests at meaningful
boundaries. A new service is justified by evidence such as independent release
needs, incompatible runtimes, or a demonstrated operational constraint. Avoid
splitting deployables merely to fit agent assignments or CI lanes.

Use the delegated architecture authority in `GOVERNANCE.md`. Record
consequential choices with context, alternatives, consequences, owner, and review
trigger in existing decision records. Routine internal changes need no separate
ADR. Temporary cross-boundary dependencies/debt need bounded dispositions.

## Compatibility and release impact

For actual public interfaces, persisted formats, or independently deployed
consumers, record supported versions and compatibility promises. Today's source
dependency graph does not enumerate older supported clients. Identify deployment
order and required coexistence behavior. Breaking changes follow accepted scope
and the product's migration/support procedure.

Use changes and transitive consumers to select affected required checks, including
contracts, fixtures, schemas, generators, configuration, and toolchain inputs.
Document uncertain impact rather than treating it as empty success. Share product
commands between local work and GitHub workflows; compose lanes from the actual
boundaries above.

## Operations and support links

| Responsibility | Existing guide or actual product decision |
| --- | --- |
| Supported upgrade/data paths and rollback/roll-forward | UNCONFIGURED |
| Deployment health, observation window, owner, recovery | UNCONFIGURED |
| Production-data backup/restore where applicable | UNCONFIGURED |
| Supported versions, report intake, triage, patches/backports | UNCONFIGURED |
| End-of-support, retirement, retained data/evidence | UNCONFIGURED |

Use the work-goal release reference for the required lifecycle sequence. Create
only records applicable to the product; give an explicit rationale for absent
product capabilities instead of leaving their applicability ambiguous.
