# Initial decision record

These are candidate design decisions prepared for human/agent governance adoption, not a claim of independent review or production approval.

**D01 · Match the position's stack.** Java/Spring owns business behavior. MongoDB owns catalog documents. MySQL is the local relational implementation and Aurora MySQL the cloud target. Familiarity with a different language/database is not a reason to replace those targets.

**D02 · Core logic can be tested separately, but not substituted for integration.** Ports permit dependency-free invariant checks. Runtime adapters use the actual MongoDB/MySQL technologies. In-memory implementations live only in tests and are never selected as a silent runtime fallback.

**D03 · Three service boundaries.** Catalog, pricing, and booking are independent Spring applications. Inventory/holds/orders stay within booking's one transactional boundary. There is no distributed transaction spanning pricing or catalog.

**D04 · Immutable snapshots.** Product and price history remain retained. Quotes snapshot their pricing and expire after fifteen minutes. Booking does not reprice an accepted quote. No mutable “current price” is read during confirmation.

**D05 · Visible, bounded concurrency.** An event row lock serializes one event ledger. Database unique constraints protect cross-event quote/key reuse. This is chosen for inspectable correctness, not claimed performance. A future optimization must preserve the same adversarial tests and add real database/load evidence.

**D06 · No implicit credential choices.** Workstation tokens are generated, distinct, and explicit. No production fallback identities, public service tokens, hard-coded cloud credentials, or localhost-to-internet deployment is accepted.

**D07 · Optional UI build.** Vaadin's Node/Maven toolchain is behind the `ui` profile; Angular has its own workspace. This keeps backend verification independent without abandoning the role-aligned UI choices.

**D08 · No invented agent configuration.** The user runs the real agent initializer. The ZIP supplies product decisions/commands, not imitation native model keys or a private tool copy. Release review and hosted controls remain separate acceptance work.

**D09 · No empty cloud placeholders.** EKS, Aurora, Grafana/Tempo dashboards, Splunk, AppDynamics, and Neo4j are tracked as future goals. Their absence is preferable to unused manifests presented as finished experience. The local code does contain the intended instrumentation dependencies and opt-in telemetry export configuration.

**D10 · Stay local until privacy is verified.** No repository mutation or cloud action is part of this delivery. The ZIP can be used in a local clone without publishing anything.
