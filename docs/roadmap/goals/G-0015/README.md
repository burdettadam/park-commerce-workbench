# G-0015 — Replace demo tokens with a deployable identity boundary

Milestone: M4
Depends on: G-0013, G-0014
Proposal revision: proposal-1
Plan authority: NOT ACCEPTED
Risk profile: high

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0015/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

The deployed configuration authenticates users/services and denies unauthorized actions without relying on local demo bearer tokens.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Select a reviewed test OIDC issuer; implement Spring resource-server validation, employee/guest roles, scoped service access and browser/session security. Test both interfaces.

## Exclusions

Do not choose an identity vendor solely for prior familiarity, claim production certification, or expose demo-auth profiles to a network.

## Preconditions and authority boundaries

Local test issuer first. External identity services, domains, accounts or paid plans need explicit authorization; no network exposure is implied.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Spring and UI authentication/authorization configuration plus tests.
- Local-only and deployable profiles clearly separated; approved identity choice recorded.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0015-AC01

Validate issuer, audience, signature, expiry and role/subject claims against a real test issuer. Wrong issuer/audience, expired credentials and forged role/owner inputs are denied.

Required evidence: `G-0015-A01` plus the relevant raw check, source S and independent review coverage.

### G-0015-AC02

Employee, guest and service-to-service permissions pass a negative access matrix over REST, GraphQL and both UIs, including cross-owner order/quote reads.

Required evidence: `G-0015-A01` plus the relevant raw check, source S and independent review coverage.

### G-0015-AC03

Review TLS boundaries, cookies/CSRF/CORS/logout and credential lifetime. Nonlocal deployment refuses demo credentials/profile and prevents management endpoints from public exposure.

Required evidence: `G-0015-A02` plus the relevant raw check, source S and independent review coverage.

### G-0015-AC04

Retain a threat model, dependency/security findings and secret scan; no open high-impact blocker is hidden by a successful happy-path login.

Required evidence: `G-0015-A03` plus the relevant raw check, source S and independent review coverage.

## Review evidence outputs (temporary and ignored)

The following IDs name expected review outputs, not files to commit. If generated, keep them under ignored `goals/G-0015/evidence/<candidate>/` only while the PR is open. Record concise findings and hosted check links in the PR; remove local outputs after feature code merges.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0015-A01` | `identity-test-matrix.md` | Actual issuer/user/service positive and negative checks. |
| `G-0015-A02` | `deployment-security-profile.md` | TLS/session/CSRF/CORS and fail-closed configuration. |
| `G-0015-A03` | `threat-model-and-findings.md` | Trust boundaries, scans, findings and residual risk. |

The accepted outcome still requires actual checks, independent reviews, intended-use validation and a decision. [The legacy manifest template](manifest.template.json) documents the older file format; do not fill or commit an evidence manifest. Follow the current [review evidence policy](../../ARTIFACT-CONTRACT.md) and link PR/CI records.

## Validation commands and new tests

- `bash scripts/verify.sh --integration` — existing. Maven/JDK/Docker required; real database tests must not silently skip.
- `node scripts/demo-http.mjs` — existing. Requires running local services and .env; creates synthetic data.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0015/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0015/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0015` | Unassigned integration owner | Not created |

Use one branch when that is sufficient; multiple branches are allowed, not mandatory overhead. Each writer updates only their branch record. Record exact included revisions and disposition for abandoned alternatives. A proposed branch name is not an existing branch or permission to push.

## Review and stop rules

The proposed baseline is two independent reviewers (contract and adversarial) and at most three rounds, subject to the actually accepted governance. Collect first findings independently. A relevant code/test/configuration/input change creates a new candidate and reopens affected verification and review. Retain valid blockers even when another reviewer disagrees. Missing reviews are not passes; unresolved blockers after the review limit require `needs-decision`.

Changes to these rules are reviewed under the previous accepted rules, never self-approved by the candidate. Version-producing merges require an actual human decision. Merely reaching `ready` does not create a release, deploy anything or change visibility.

## Handoff and recovery

Push source and branch checkpoints before the canonical coordination record, identify every required source component, and verify retrieval. Keep generated evidence ignored and local while the PR is open, with concise findings and CI links in the PR. After feature code merges, remove local generated outputs. Record exact source S and merged M separately; do not create an evidence-only commit E.

## Activation record — intentionally unfilled

- Accepted authority, plan commit and governing baseline: not recorded.
- Integration owner, actual ref and active writers: unassigned.
- Source candidate S / base / configuration / accepted plan identity: not recorded.
- Runtime state: not created; after adoption use the upstream planned/active/blocked/paused/ready/needs-decision/cancelled/superseded vocabulary.
- Verification, independent reviews, human validation and release: not performed by this proposal.
- Next action: accept the plan and satisfy dependencies, then select a bounded authorized slice.

[Milestone M4](../../milestones/M4.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
