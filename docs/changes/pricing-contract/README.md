# Pricing-contract slice handoff

Local development toward G-0009: coherent evaluation time, owner-scoped quote
status, strict shared input parsing, independent monetary fixtures and additive
REST/GraphQL contract tests. No remote publication or release.

- [Bounded plan and authority](PLAN.md)
- [Behavior, wire format, errors and exclusions](../../project/pricing-contract.md)
- [Literal pricing fixtures](../../../contracts/pricing/pricing-contract-fixtures.json)
- [Pinned source-level API contract](../../../contracts/pricing/quote-api-v1.json)
- [Verification and actual limitations](VERIFICATION.md)

```bash
bash scripts/test-core.sh --demo
node --test scripts/pricing-contract.test.mjs
```

The second command requires JDK 21 and Node but no Maven, external libraries,
network, services or database. It compiles a **test-only** Java bridge into a
fresh temporary directory and removes it afterward. A missing compiler is a
failure, not a skipped test. It is not an alternative production pricing engine.

Next workstation gate: initialize/adopt the actual git-agent guidance; accept the
prerequisites and exact candidate; run `bash scripts/verify.sh --integration`,
then the real service demo. Check Jackson integer/date binding, GraphQL resolver
and error envelopes, role filters, unchanged snapshot round-trips, and Vaadin/Angular
builds. Two independent reviews and human validation remain pending. This slice
must not be marked ready simply because the offline suite passes.

The plan/test/source/evidence commits in the separately delivered bundle are local
history, not the GitHub repository history. Use the full ZIP as source for your
local clone, or the patch against the preceding catalog-publication ZIP. Do not
force-push this separate commit chain over the remote repository.
