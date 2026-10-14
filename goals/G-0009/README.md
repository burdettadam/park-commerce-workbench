# G-0009 — Freeze the price and quote contract with independent examples

Status: **active** (scoped fourth independent review authorized by delegated decision; acceptance pending). Milestone: M2.

## Accepted work contract

- **Authority and revision:** The user's 2026-10-13 instruction to make an active goal for subagents on the next three goals adopts the unchanged [G-0009 proposal-1](../../docs/roadmap/goals/G-0009/README.md), SHA-256 `f506c61802cc81355f76ba7132ef57e1042022506df2b2f84699bd8b4fcc4a67`. This record is `G-0009-active-1`. Its activation commit P and exact file digest belong in [bootstrap](work/bootstrap.md) after P exists. Material scope or criterion changes require the owner's decision.
- **Governing baseline:** Root `AGENTS.md`, `GOVERNANCE.md`, `DEVELOPMENT.md`, `ARCHITECTURE.md`, `PLANS.md`, `.agents/skills/work-goal/`, `.agents/skills/worktree-flow/`, and native contract/adversarial reviewer roles at base `bd26cfbd062228c228223518d3800cd125c4e9b1`. Check applicable instructions and effective permissions again at execution. Proposed control changes do not weaken this baseline.
- **Dependency gate:** G-0008 is accepted and squash-merged to public `main` as PR #12 at `50bbbdedb4928d2127cc6a950d8eaa9f70d77b48`. Its fixed product source S is `56141ae40f05704ff0ae30b232ef878e2943b6c6`, accepted plan P is `717c31b04eca3f6478efa50c25dd666d3de73057`, and final evidence E is `cc3835464469979b3940627abf7a358696779229`. The G-0009 branch merged that exact main revision at `82934e30bc0ed33019745d2ed8f5f197c2ae36a0`. The retained original G-0008 ref permits retrieval of S/P/E; main contains the squash result and evidence. Relevant changes added preview error API/UI tests and HTTP error sanitization while preserving the canonical core calculation. Changed relevant inputs reopen G-0009 checks.
- **Canonical repository/ref/path:** Public `burdettadam/park-commerce-workbench`, `goal/G-0009`, this file. The assigned G-0009 subagent is integration owner and sole writer of this branch/worktree. Adam Burdett retains scope, architecture and repository administration authority. No additional writer is assigned.
- **Risk and review:** High risk for monetary totals, calendar/expiry rules, persisted quote identity and access. Two independent native reviewers, `contract-reviewer` and `adversarial-reviewer`, at most three rounds. Freeze source candidate S before acceptance checks and review; relevant changes reopen coverage.
- **Intended-use validation:** Adam Burdett is the owner proposed by the adopted roadmap. Record the actual validation method and outcome or explicit delegation when execution reaches that gate. Reviewer reports and release approval are distinct.
- **Cost boundary:** Use local tools and only standard free public GitHub runners. No paid runner, cloud service, deployment or external write is authorized by this activation.

## Intended user and observable outcome

Guest-facing prices have explicit, reproducible rules for amounts, discounts, dates, revisions and expiry.

## Scope

Document and test price-calendar boundaries, park-local dates, rounding, offer combination policy, immutable quote identity and expiry.

## Exclusions

USD synthetic amounts only; no tax-compliance claim, real residency/age proof, multicurrency or dynamic revenue-management algorithm.

## Preconditions and authority boundaries

Local synthetic-data work only; no deployment or external write authorization.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Versioned pricing/quote contracts, fixture pack, boundary and consumer tests.

## Acceptance criteria

The following outcomes and required-evidence statements are accepted verbatim from proposal-1. They are not yet observed G-0009 results. Do not weaken or delete a criterion because the current implementation fails it.

### G-0009-AC01

A reviewed fixture pack covers no offer, exact threshold, one-below threshold, mixed adult/child quantities and every applicable rounding boundary using expected cents calculated independently of implementation.

Required evidence: `G-0009-A01` plus the relevant raw check, source S and independent review coverage.

### G-0009-AC02

Test date-range endpoints, invalid dates, park-local timezone transitions, exact quote-expiry instant and expired/unknown quote errors through the public API.

Required evidence: `G-0009-A02` plus the relevant raw check, source S and independent review coverage.

### G-0009-AC03

A quote identifies catalog and pricing versions and cannot be rewritten by new publications. Own/other-user/service-role access behaves consistently across REST and GraphQL.

Required evidence: `G-0009-A02` plus the relevant raw check, source S and independent review coverage.

### G-0009-AC04

Publish contract examples and explicit exclusions; retain schema/consumer tests that fail for an incompatible quote or error-shape change.

Required evidence: `G-0009-A03` plus the relevant raw check, source S and independent review coverage.

## Required goal-specific artifacts

Relative to the eventual candidate evidence directory, for example `goals/G-0009/evidence/<candidate>/`. These are output requirements, not generated evidence.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0009-A01` | `pricing-contract-fixtures.json` | Independent price inputs and expected outputs. |
| `G-0009-A02` | `quote-boundary-tests.md` | Actual time/ownership/version and API boundary results. |
| `G-0009-A03` | `contract-examples.md` | Documented public request/response/error examples and compatibility tests. |

The common manifest, environment, raw checks, independent contract/adversarial review reports, intended-use validation, decision and handoff are also mandatory. Start from the [unfilled manifest template](../../docs/roadmap/goals/G-0009/manifest.template.json) and [artifact contract](../../docs/roadmap/ARTIFACT-CONTRACT.md). The unfilled template deliberately fails the readiness checker.

## Validation commands and new tests

- `bash scripts/test-core.sh --demo` — existing; in-memory domain adapters only.
- `node --test scripts/pricing-contract.test.mjs` — existing fixture and SDL-source checks; current G-0009 source and evidence identities are in the [review checkpoint](work/review-checkpoint.md).
- `bash scripts/verify.sh --integration` — existing; Maven/JDK/Docker required and real database tests must not silently skip.
- `node scripts/demo-http.mjs` — existing; requires an owned running local stack and `.env`; creates synthetic data.

These baseline commands prove only their actual selections. Add focused public API date-range and consumer/error-shape checks for the criteria. Preserve command, working directory, source, configuration, raw output and skips for every acceptance run.

## Execution and ownership

| Slice | Ref | Writer | Included revision |
| --- | --- | --- | --- |
| Canonical planning and later integration | `goal/G-0009` | Assigned G-0009 subagent | Base `bd26cfbd062228c228223518d3800cd125c4e9b1`; P in [bootstrap](work/bootstrap.md) |
| Product implementation and verification | `goal/G-0009` | Assigned G-0009 subagent | G-0008 main input `50bbbdedb4928d2127cc6a950d8eaa9f70d77b48`; current checked source and evidence in the [review checkpoint](work/review-checkpoint.md) |

One writer owns each future worktree. The integration owner records included revisions and dispositions before claiming a combined candidate.

## State, review and recovery

G-0009 is **active** after both third-round native reviewers blocked source S3 on evidence citation drift and Codex authorized exactly one further review pair under Adam Burdett's decision delegation. The preparatory [audit](work/bootstrap.md) is not acceptance evidence. The [review checkpoint](work/review-checkpoint.md) identifies the current source and evidence, while the S3 [historical evidence](evidence/bede018/) retains blocked attempts. The scoped exception and validator control change are in [round-three disposition](work/round3-disposition.md). G-0009 intended-use validation was explicitly delegated to Codex but remains pending, as does readiness. No merge, tag, release or deployment is claimed.

At execution, publish source and indispensable artifacts before updating this canonical record. Verify retrieval and report local-only remainder. Source S, later evidence E, merge M and release/deployment observations are separate facts. A version-producing merge still requires distinct eligible human review.

[Roadmap proposal](../../docs/roadmap/goals/G-0009/README.md) · [Milestone M2](../../docs/roadmap/milestones/M2.md)
