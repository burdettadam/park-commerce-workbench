# Starter-code candidate handoff

This checkpoint adds real implementation and regression tests to the supplied roadmap. It remains local and not ready for release. Nothing has been pushed to GitHub; repository visibility was not changed or rechecked in this turn.

## Changes

**Version guard:** `scripts/init-agent.sh` now accepts only the exact reviewed `git-agent 0.2.0` output, checking from the same worktree context as initialization. Its expanded test suite rejects look-alike, empty, malformed and unsuccessful version responses, and preserves initializer exit codes and existing files.

**Employee what-if pricing:** `Pricing.Service.preview` uses the same calculation as quote issuance but never reads or writes the pricing store. It returns full candidate/request identity and an itemized calculation, without a quote ID or expiry. The employee-only REST endpoint and Vaadin form use this operation rather than creating a real quote. No database migration is required.

**Tests and contracts:** 30 new Java scenarios, four unexecuted MySQL/HTTP integration tests, four additional live cross-service exercise checks, and an explicit [preview contract](../../project/pricing-preview.md). Existing quote and booking tests remain selected.

## Run locally

```bash
bash scripts/test-core.sh --demo
bash scripts/test-tooling.sh
node --test storefront/src/app/checkout-state.test.mjs
node scripts/validate-roadmap.mjs
node --test scripts/validate-roadmap.test.mjs scripts/validate-evidence.test.mjs
```

The core demo shows an unpublished two-adult/two-child candidate at $125/$85: $420 subtotal, $42 family discount and $378 total. Published version remains 1 immediately afterward. The subsequent original-price checkout demonstration still runs.

On a workstation with the configured toolchains/dependencies:

```bash
bash scripts/verify.sh --java
mvn -B -ntp -Pui -pl admin-console -am verify
bash scripts/verify.sh --integration
# After configuring local credentials and starting the real Compose services:
node scripts/demo-http.mjs
```

None of these workstation-only checks was reported as passing here. The HTTP database tests substitute the catalog client; the live cross-service exercise does not. Vaadin compilation/browser behavior and serialization/security execution remain specific open checks.

## Roadmap adoption

Original proposal files remain intact. G-0002's regression defect is now fixed in this candidate; the red source and outputs remain in local history/evidence for testing the workflow. G-0008 has a preparatory implementation, not an accepted milestone. Do not mark either goal ready until prerequisite/adoption decisions, independent reviewers and human validation have actually occurred.

A single implementation author produced this code and self-checked it. No independent contract/adversarial reviews or human approval are fabricated. Source-level checks, tests and a runnable core demo do not replace those gates.

Read [scope recorded before coding](SCOPE.md) and [observed verification](VERIFICATION.md). The verification record distinguishes clean source S from later report/archive commit E. Local-only retention is not a verified off-device backup.

## Import without replacing repository history

The source ZIP is complete: use it for a fresh local directory, or carefully overlay it onto your clone after saving your changes. It deliberately omits `.git` and `.env`. Do not overlay another developer's conflicting changes without review.

A separate cumulative patch is relative to the supplied roadmap archive (not necessarily your GitHub HEAD). Test applicability with `git apply --check /path/to/park-commerce-starter-code.patch` before applying. A separate Git bundle retains the actual local baseline and candidate commits and can be inspected in a separate directory. Neither operation pushes or changes repository visibility.

Before the first remote push, explicitly verify that the repository is private. Initialize your reviewed private `git-agent` on the local checkout using the existing guarded helper; it is not included in this package.
