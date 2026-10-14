# G-0008 — Preview unpublished prices with the production calculation path

Status: **ready and merged** by [PR #12](https://github.com/burdettadam/park-commerce-workbench/pull/12) to `main` at `50bbbdedb4928d2127cc6a950d8eaa9f70d77b48`. Milestone: M2. Dependency: [G-0007 ready and merged](../G-0007/README.md) on `main` at `bd26cfbd062228c228223518d3800cd125c4e9b1`.

## Accepted work contract

- **Authority:** Adam Burdett's 2026-10-13 instruction to make an active goal for subagents to work on the next three roadmap goals adopts the unchanged [G-0008 proposal-1](../../docs/roadmap/goals/G-0008/README.md), SHA-256 `918d63d9389aa0455099672998ba291353d1b2111dd79b948ad12f86b235d797`. This record is `G-0008-active-1`. Material scope or criterion changes return to Adam.
- **Governing baseline:** `AGENTS.md`, `GOVERNANCE.md`, `DEVELOPMENT.md`, `ARCHITECTURE.md`, `PLANS.md`, the two repository skills, and native reviewer roles at base `bd26cfbd062228c228223518d3800cd125c4e9b1`. Review control changes under that baseline.
- **Integration:** public `burdettadam/park-commerce-workbench`, canonical `goal/G-0008`, this file. The primary Codex session is integration owner and coordinates assigned Git operations. The G-0008 subagent is the sole writer of its assigned implementation branch. Adam retains scope, architecture, repository administration, and human release authority.
- **Risk and review:** medium risk. Two independent contract/adversarial reviewers, at most three rounds, on a fixed source candidate. Changes to relevant source or inputs reopen affected checks and reviews.
- **Intended-use validation:** Adam Burdett or an explicitly delegated validator will compare unpublished preview, exact-candidate publication, monetary parity, error behavior, and side effects. No delegation for this goal is inferred from earlier goals.
- **Cost and scope:** local synthetic data and standard free public GitHub runners only. No paid service, cloud deployment, version tag, or release is authorized.

## Outcome and boundaries

An employee can explain the price a family would receive before publishing a rule. Add a side-effect-free what-if preview for a single product/date/party using the same calculation semantics as published quote issuance. A preview must not issue a purchasable quote, reserve inventory, or silently publish configuration. Inspect and reuse the existing preview candidate before adding an implementation.

## Acceptance criteria

These outcomes are adopted verbatim from proposal-1. Candidate results are recorded against fixed source S in the evidence directory below; readiness remains pending.

### G-0008-AC01

A known fixture produces the independently calculated subtotal, discount and final amount in preview; preview explicitly labels unpublished input/version state.

### G-0008-AC02

Publishing exactly the previewed candidate makes a subsequent real quote agree on all monetary lines under identical date/party inputs; quote issuance additionally assigns actual identity/expiry.

### G-0008-AC03

Invalid or overlapping effective-date rules and out-of-range party inputs are rejected with actionable messages, not a best-guess price.

### G-0008-AC04

No preview mutates price publication, issued quotes, orders or inventory. Concurrent preview/publication keeps candidate identity explicit; previous unexpired quotes remain unchanged.

## Evidence and execution

Retain `preview-fixtures.json` for AC01/AC02, `preview-errors.md` for AC03, and `preview-side-effects.md` for AC04 under `goals/G-0008/evidence/<candidate>/`, alongside the common manifest, raw checks, independent reports, intended-use validation, decision, and handoff. Use the [goal manifest template](../../docs/roadmap/goals/G-0008/manifest.template.json) and [artifact contract](../../docs/roadmap/ARTIFACT-CONTRACT.md). A template is not passing evidence.

Candidate checks include `bash scripts/test-core.sh --demo`, `bash scripts/verify.sh --integration` with real databases and no silent skips, and `node scripts/demo-http.mjs` against an assigned local stack. Select focused additional checks from the actual code. Record commands, inputs, runtime ownership, and independent expected values. The existing code and earlier runs are exploratory until checked at fixed source S.

| Slice | Ref | Writer | Dependency and state |
| --- | --- | --- | --- |
| Canonical integration and accepted record | `goal/G-0008` | Primary Codex session | Fixed source S `56141ae40f05704ff0ae30b232ef878e2943b6c6`; manifest validates, remote retrieval pending |
| Pricing preview implementation and focused tests | `goal/G-0008-implementation` | G-0008 subagent | Implementation and UI repair merged; real HTTP past-date assertion selected at S |
| Independent contract/adversarial review | Separate native reviewer assignments | Primary Codex session coordinates | Both native roles passed round two at S with zero open blockers; original findings and follow-ups retained |

Publish implementation and required artifacts before updating this canonical record. Preserve old failed attempts. Confirm exact remote revisions and artifact retrieval before claiming recovery. A version-producing merge requires distinct eligible-human review; goal readiness, merge, release, and deployment remain separate observations.

At S, free public CI run `38478861553` passed all four jobs: 280 core cases, 68 real database integration cases with zero skips, three admin helper cases, and 43 six-service HTTP checks. The separately named Git Bash `scripts/test-core.sh --demo` check passed 280/280 at S. The employee browser displayed the past-date repair message from a controlled loopback 400 response; a separate real HTTP integration case passed at S. The fixture, side-effect and error records are under [evidence/56141ae](https://github.com/burdettadam/park-commerce-workbench/tree/90ae8eff6485c950c8d02c8d3a33893059f5d82e/goals/G-0008/evidence/56141ae). The earlier Windows Linux `bash` and local Docker attempts failed and are retained as gaps. Codex performed the intended-use check under Adam Burdett's explicit G-0008 delegation. Both native reviewers passed at S, and `scripts/validate-evidence.mjs` passed the completed manifest with seven declared checks and 26 local artifacts. At final branch head `cc3835464469979b3940627abf7a358696779229`, all four PR-head CI jobs passed in run `38480447611`, and a fresh clone matched all 27 committed evidence blobs and passed the manifest validator. The original source/evidence branch remains published after the squash merge.

Next action: G-0009 refreshes against the accepted source and merged `main` revision. G-0010 waits for G-0009 acceptance. No version tag, release artifact or deployment was created by the G-0008 merge.
