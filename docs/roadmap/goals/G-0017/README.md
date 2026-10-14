# G-0017 — Make requests traceable through the observability stack

Milestone: M5
Depends on: G-0016
Proposal revision: proposal-1
Plan authority: NOT ACCEPTED
Risk profile: medium

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0017/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

An operator follows a slow or failed purchase across services using real traces, metrics and safe logs.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Run OpenTelemetry collector, Tempo, Prometheus and Grafana; instrument actual service/SQL spans and define a small operational dashboard.

## Exclusions

Instrumentation dependencies or empty dashboards are not operational evidence; no customer or token data in telemetry.

## Preconditions and authority boundaries

Local synthetic-data work only; no deployment or external write authorization.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Collector/Tempo/Prometheus/Grafana configuration and application instrumentation.
- Reproducible failure scenario and operational runbook.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0017-AC01

Execute one successful purchase and one injected dependency failure; retain real trace IDs spanning the intended service boundaries and matching backend operations.

Required evidence: `G-0017-A01` plus the relevant raw check, source S and independent review coverage.

### G-0017-AC02

Dashboard queries display actual request counts, latency distributions, error rates and booking conflicts for a declared time window; exported configuration recreates the view.

Required evidence: `G-0017-A02` plus the relevant raw check, source S and independent review coverage.

### G-0017-AC03

An operator identifies the induced bottleneck/failure from retained traces and queries without reading the test script's hidden answer first; record the investigation and uncertainty.

Required evidence: `G-0017-A03` plus the relevant raw check, source S and independent review coverage.

### G-0017-AC04

Check telemetry for secrets/high-cardinality owner identifiers and exercise collector unavailability; application correctness does not depend on successful telemetry export.

Required evidence: `G-0017-A03` plus the relevant raw check, source S and independent review coverage.

## Review evidence outputs (temporary and ignored)

The following IDs name expected review outputs, not files to commit. If generated, keep them under ignored `goals/G-0017/evidence/<candidate>/` only while review or a live handoff needs them. Record concise findings and hosted check links in the PR; remove local outputs after merge or terminal cancellation/supersession.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0017-A01` | `trace-examples.md` | Real trace IDs, export references and candidate/input identity. |
| `G-0017-A02` | `dashboard-evidence.md` | Dashboard JSON/query references and captured workload window. |
| `G-0017-A03` | `investigation-and-safety.md` | Operator diagnosis, redaction and collector-failure observations. |

The accepted outcome still requires actual checks, independent reviews, intended-use validation and a decision. [The legacy manifest template](manifest.template.json) documents the older file format; do not fill or commit an evidence manifest. Follow the current [review evidence policy](../../ARTIFACT-CONTRACT.md) and link PR/CI records.

## Validation commands and new tests

- `node scripts/demo-http.mjs` — existing. Requires running local services and .env; creates synthetic data.
- `docker compose config --quiet` — existing. Validate without printing rendered secrets; runtime still unverified.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0017/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0017/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0017` | Unassigned integration owner | Not created |

Use one branch when that is sufficient; multiple branches are allowed, not mandatory overhead. Each writer updates only their branch record. Record exact included revisions and disposition for abandoned alternatives. A proposed branch name is not an existing branch or permission to push.

## Review and stop rules

The proposed baseline is two independent reviewers (contract and adversarial) and at most three rounds, subject to the actually accepted governance. Collect first findings independently. A relevant code/test/configuration/input change creates a new candidate and reopens affected verification and review. Retain valid blockers even when another reviewer disagrees. Missing reviews are not passes; unresolved blockers after the review limit require `needs-decision`.

Changes to these rules are reviewed under the previous accepted rules, never self-approved by the candidate. Version-producing merges require an actual human decision. Merely reaching `ready` does not create a release, deploy anything or change visibility.

## Handoff and recovery

Push source and branch checkpoints before the canonical coordination record, identify every required source component, and verify retrieval. Keep generated evidence ignored and local while review or a live handoff needs it, with concise findings and CI links in the PR. After merge or terminal cancellation/supersession, remove local generated outputs. Record exact source S and merged M separately; do not create an evidence-only commit E.

## Activation record — intentionally unfilled

- Accepted authority, plan commit and governing baseline: not recorded.
- Integration owner, actual ref and active writers: unassigned.
- Source candidate S / base / configuration / accepted plan identity: not recorded.
- Runtime state: not created; after adoption use the upstream planned/active/blocked/paused/ready/needs-decision/cancelled/superseded vocabulary.
- Verification, independent reviews, human validation and release: not performed by this proposal.
- Next action: accept the plan and satisfy dependencies, then select a bounded authorized slice.

[Milestone M5](../../milestones/M5.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
