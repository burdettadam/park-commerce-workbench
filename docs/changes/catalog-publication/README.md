# Catalog-publication development handoff

This candidate adds immutable drafts, exact-draft publication, a pinned history
cursor, and embedded publication audit to the existing catalog. Guest reads,
pricing and checkout contracts remain unchanged. No remote is configured or updated.

See [contract](../../project/catalog-publication.md), [bounded plan](PLAN.md),
[publication scenarios](publication-scenarios.md), [history and conflicts](history-and-conflicts.md),
[observed verification](VERIFICATION.md), and [goal mapping](goal-coverage.json).

## Run the available local checks

```bash
bash scripts/test-core.sh --demo
node --test storefront/src/app/*.test.mjs
bash scripts/test-tooling.sh
node scripts/validate-roadmap.mjs
node --test scripts/validate-roadmap.test.mjs scripts/validate-evidence.test.mjs
```

The Java runner uses test-only in-memory stores. Its forced insert races and
reconstructed stores do not prove MongoDB isolation or process restart behavior.
Node tests retain the previous client's storage/transport fixture coverage.

## Full-stack validation remains required

With JDK 21, Maven, Docker and dependency/registry access, run:

```bash
bash scripts/verify.sh --java
bash scripts/verify.sh --integration
mvn -B -ntp -Pui -pl admin-console -am verify
```

Then start the real services and run `node scripts/demo-http.mjs`. It now includes
30 named live checks: the previous 23 plus seven editorial scenarios. It creates a
separate synthetic editorial product so pricing/checkout examples keep their original
pinned catalog data. It is not a browser test or an off-device backup demonstration.

The integration suite includes six new Mongo adapter tests and eight new real
Spring/Mongo HTTP tests (37 total across services). They deliberately do not
silently skip absent Docker. Actual runs must verify legacy documents, indexes,
role filtering, actor derivation, missing preconditions, and round-trip serialization.

Test the Vaadin console in a real browser: save/load a draft, change a field and
attempt publication, restore/re-save and publish, race another editor, inspect
history, choose adult-only variants, and repeat after an unknown write response.
The API rejects stale writes; the UI must not encourage an automatic retry/rebase.

## Goal and review status

G-0007 remains a proposal awaiting its prerequisite decisions and real validation.
This slice supplies implementation candidates and limited domain observations for
AC01..AC04. No native agent initialization, two independent reviews, intended-use
human validation, release approval, deployment or remote recovery is implied.
Use the actual local `git-agent` adoption process before treating goals as accepted.

The ZIP is the easiest continuation artifact. The patch targets the immediately
previous checkout-recovery ZIP. The optional bundle is a separate local history;
never force-push it over the user's GitHub repository. Overlay source into the
existing local clone and inspect the differences. Verify private visibility before
any future remote push. No private initializer is included.
