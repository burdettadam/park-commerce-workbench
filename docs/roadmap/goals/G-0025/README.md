# G-0025 — Investigate commerce failures in Splunk

Milestone: M8
Depends on: G-0017
Proposal revision: proposal-1
Plan authority: NOT ACCEPTED
Risk profile: medium

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0025/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

An operator uses retained SPL searches to explain actual request failures and retry patterns.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Use authorized Splunk access to ingest safe structured application logs and reproduce three bounded investigations.

## Exclusions

No subscription purchase, customer data export, empty search screenshot or Grafana substitute for the named tool.

## Preconditions and authority boundaries

Use only an authorized environment and approved data. License/cloud expense requires a separate human decision.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- SPL query files, safe log routing and investigation runbook.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0025-AC01

Ingest real sanitized logs for a fixed purchase/failure workload and record time window, source revision, index/source configuration and actual searchable events.

Required evidence: `G-0025-A01` plus the relevant raw check, source S and independent review coverage.

### G-0025-AC02

Execute saved SPL searches for failed checkout, repeated idempotency keys and a service-error trend; compare returned counts/events with the known workload oracle.

Required evidence: `G-0025-A02` plus the relevant raw check, source S and independent review coverage.

### G-0025-AC03

An independent operator follows the search runbook and identifies the induced failure using only retained queries/logs, with uncertainty and false positives documented.

Required evidence: `G-0025-A03` plus the relevant raw check, source S and independent review coverage.

### G-0025-AC04

Record license/access/retention constraints and verify tokens, sensitive headers and real personal data are absent. Missing access keeps this goal blocked.

Required evidence: `G-0025-A01` plus the relevant raw check, source S and independent review coverage.

## Required goal-specific artifacts

Relative to the eventual candidate evidence directory, for example `goals/G-0025/evidence/<candidate>/`. These are output requirements, not generated evidence.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0025-A01` | `splunk-ingestion.md` | Actual ingestion/configuration and access/retention boundary. |
| `G-0025-A02` | `saved-search-results.md` | Versioned .spl source, real results and workload reconciliation. |
| `G-0025-A03` | `splunk-investigation.md` | Independent investigation steps and observations. |

The common manifest, environment, raw checks, independent contract/adversarial review reports, intended-use validation, decision and handoff are also mandatory. Start from [the unfilled manifest template](manifest.template.json) and [artifact contract](../../ARTIFACT-CONTRACT.md). The unfilled template deliberately fails the readiness checker.

## Validation commands and new tests

- `node scripts/demo-http.mjs` — existing. Requires running local services and .env; creates synthetic data.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0025/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0025/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0025` | Unassigned integration owner | Not created |

Use one branch when that is sufficient; multiple branches are allowed, not mandatory overhead. Each writer updates only their branch record. Record exact included revisions and disposition for abandoned alternatives. A proposed branch name is not an existing branch or permission to push.

## Review and stop rules

The proposed baseline is two independent reviewers (contract and adversarial) and at most three rounds, subject to the actually accepted governance. Collect first findings independently. A relevant code/test/configuration/input change creates a new candidate and reopens affected verification and review. Retain valid blockers even when another reviewer disagrees. Missing reviews are not passes; unresolved blockers after the review limit require `needs-decision`.

Changes to these rules are reviewed under the previous accepted rules, never self-approved by the candidate. Version-producing merges require an actual human decision. Merely reaching `ready` does not create a release, deploy anything or change visibility.

## Handoff and recovery

Retain source/branch/artifact checkpoints before the canonical coordination record, identify every indispensable component, then prove retrieval. Until privacy is verified, use local artifacts and label coverage `local_only`; do not claim off-device recovery. Record source S separately from later evidence/report commit E so the report does not need its own future commit hash.

## Activation record — intentionally unfilled

- Accepted authority, plan commit and governing baseline: not recorded.
- Integration owner, actual ref and active writers: unassigned.
- Source candidate S / base / configuration / accepted plan identity: not recorded.
- Runtime state: not created; after adoption use the upstream planned/active/blocked/paused/ready/needs-decision/cancelled/superseded vocabulary.
- Verification, independent reviews, human validation and release: not performed by this proposal.
- Next action: accept the plan and satisfy dependencies, then select a bounded authorized slice.

[Milestone M8](../../milestones/M8.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
