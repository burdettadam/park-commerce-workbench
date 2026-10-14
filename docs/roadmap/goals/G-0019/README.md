# G-0019 — Build traceable distributable artifacts from clean source

Milestone: M6
Depends on: G-0018
Proposal revision: proposal-2
Plan authority: NOT ACCEPTED
Risk profile: high

This is a **proposal**, not an active or completed goal. Once accepted, copy/adapt this contract into `goals/G-0019/README.md` on its declared integration ref and record the actual accepted plan commit. That active record becomes the authority for scope and state; this proposal remains a historical input. Do not create 30 active work records in advance.

## Intended user and observable outcome

Each distributable can be traced to fixed reviewed source and configuration and retrieved by digest.

Intended-use validation owner: Adam Burdett, proposed; record the actual responsible person and method at adoption. Reviewer roles are separate from the implementation writer. No approval is implied by naming the owner.

## Scope

Make clean builds, lock dependencies, pin verified image/action inputs, create SBOMs and image digests, and retain provenance/scan results.

## Exclusions

No invented signatures, latest-tag identity claims or publication to registries without authorization.

## Preconditions and authority boundaries

Local synthetic-data work only; no deployment or external write authorization.

Accept all prerequisite candidates and the exact relevant input revisions before execution. Unresolved access, budget, toolchain, source ownership or required success criteria are planning gaps, not successful checks. Keep the role-aligned technology stack; a material replacement needs a recorded decision.

## Deliverable code, configuration or documents

- Hardened Dockerfiles/build configuration, lockfiles and distributable evidence.

## Acceptance criteria

Expected outcomes below are proposed contractual tests, not observations. Accept them **before** coding. Do not weaken or delete a criterion because the current implementation fails it.

### G-0019-AC01

Build all selected deployable artifacts from clean source S with recorded toolchain/configuration inputs; record image digest D and source relationship, not only a mutable tag.

Required evidence: `G-0019-A01` plus the relevant raw check, source S and independent review coverage.

### G-0019-AC02

Generate real dependency/SBOM and vulnerability reports for the produced binaries/images; retain findings and required dispositions under accepted policy.

Required evidence: `G-0019-A02` plus the relevant raw check, source S and independent review coverage.

### G-0019-AC03

Rebuild/retrieve in a clean environment and compare the defined reproducibility properties. Explain nondeterministic metadata rather than asserting bit-identical output without measuring it.

Required evidence: `G-0019-A01` plus the relevant raw check, source S and independent review coverage.

### G-0019-AC04

Retained artifacts have digest, owner, retrieval steps and expiry/retention commitment; independently retrieve at least one image and one nonimage artifact and verify their identities.

Required evidence: `G-0019-A03` plus the relevant raw check, source S and independent review coverage.

## Review evidence outputs (temporary and ignored)

The following IDs name expected review outputs, not files to commit. If generated, keep them under ignored `goals/G-0019/evidence/<candidate>/` only while the PR is open. Record concise findings and hosted check links in the PR; remove local outputs after merge or terminal cancellation/supersession.

| ID | File | Purpose |
| --- | --- | --- |
| `G-0019-A01` | `build-provenance.json` | Source S, build inputs, artifact D and reproducibility observations. |
| `G-0019-A02` | `supply-chain-review.md` | Real SBOM/scan paths, verified pins and risk dispositions. |
| `G-0019-A03` | `artifact-retrieval.md` | Storage, retention and tested retrieval for all required outputs. |

The accepted outcome still requires actual checks, independent reviews, intended-use validation and a decision. [The legacy manifest template](manifest.template.json) documents the older file format; do not fill or commit an evidence manifest. Follow the current [review evidence policy](../../ARTIFACT-CONTRACT.md) and link PR/CI records.

## Validation commands and new tests

- `bash scripts/verify.sh --java` — existing. Maven/JDK/repository access required; does NOT execute *IT suites.
- `mvn -B -ntp -Pui -pl admin-console -am verify` — proposed_validation. Uses existing Maven profile; must be executed and repaired in G-0004. Do not skip required tests.
- `(cd storefront && npm ci && npm run build)` — existing. Uses the committed lockfile; repeat on the accepted candidate rather than relying on earlier CI.
- `bash scripts/verify.sh --integration` — existing. Maven/JDK/Docker required; real database tests must not silently skip.

Existing commands establish only their stated coverage. Extend tests for the new criteria; a successful baseline command does not automatically prove a new goal. Resolve unspecified goal-specific commands during plan acceptance and retain the actual executed commands, working directory and inputs.

## Suggested slices and ownership

| Slice | Proposed ref | Writer | Included commit |
| --- | --- | --- | --- |
| Implementation/configuration | `goal/G-0019/implementation` | Unassigned | Not created |
| Tests/evidence or independent alternative | `goal/G-0019/validation` | Unassigned | Not created |
| Canonical integration | `goal/G-0019` | Unassigned integration owner | Not created |

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

[Milestone M6](../../milestones/M6.md) · [Roadmap](../../README.md) · [Commands](../../COMMANDS.md)
