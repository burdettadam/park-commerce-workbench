# G-0001 owner decision packet

Status: **adopted for G-0001** by Adam Burdett on 12 October 2026. Prepared from the accepted G-0001 contract and observed first-local-goal controls. The owner answered “Adopt all proposed decisions” to the seven-group decision request in the active goal conversation. This adopts the choices below; it does not prove implementation, native-role availability, GitHub enforcement, or release approval.

## First local goal choices

| Group | Proposed decision | Reason and limit |
| --- | --- | --- |
| Product commands and support | Adopt the commands linked from `DEVELOPMENT.md` and `docs/START-HERE.md` for G-0001. Scope this goal's supported host to Windows with Git Bash, Git for Windows 2.53, Codex CLI 0.162.0-alpha.17.2, JDK 21, Maven 3.9+, Node 24 and Docker Compose v2. Treat these as an observed pilot configuration, not a general support promise. G-0001 has no browser acceptance, UI build, submodule, LFS or hook operation; any later goal using one must define and verify it. | Uses real product commands and observed host versions without inventing browser or cross-platform certification. |
| Decision owners | Adam Burdett owns scope, acceptance, architecture/compatibility, repository administration and project-policy adoption. The accepted goal record names the branch writer and integration owner for each goal. | One named owner can hold several assignments; branch writers do not inherit policy or repository-admin authority. |
| Independent reviews | Adopt N=2 distinct reviewers and R=3 maximum rounds, using contract and adversarial assignments and the current review guide. A review counts only when the actual fixed candidate, prior governing baseline and evidence were inspected. | Carries this goal's accepted conservative gate into project policy. Native custom-role invocation remains unverified in this runtime; generic task names are not role evidence. |
| First-goal local isolation | Permit one owned local Compose project and one active mutable product instance at a time for G-0001. Serialize tests that share its database or local ports; use synthetic fixtures. Start only through maintained commands, record the Compose project/container IDs and stop only owned resources after interruption. Do not claim two-instance isolation until G-0003 proves it. | Sufficient for the documentation and initializer goal; this is a limit, not a concurrency capability claim. |
| First-goal execution bounds | Run CI jobs with their existing workflow timeouts; for local checks record actual durations and stop a hung run through its own process/session handle. No automatic time bound or parallel worker count is certified for general product goals. | Avoids filling performance or readiness values without measurement. G-0001's initializer and discovery checks have actual exit codes. |
| Development recovery | Target at most **one hour of active work** lost when the workstation fails while GitHub and account access survive; push each meaningful increment, at handoff, and at least hourly during active work. Target resuming useful development within **one working day** on a replacement host. Adam owns retained work refs and access recovery. Keep indispensable evidence and exact digests through goal closure and for 30 days afterward; retrieve a sample from a clean clone before closure. Drill after material setup/access/recovery changes and before the first version release. | These are proposed service targets, not achieved RPO/RTO evidence. The procedure has no unattended backup while Codex is inactive. Private `.env`, credentials and machine-specific settings must be recreated through secured account access and local setup, never committed. |
| Release control | No version-producing merge in G-0001. Adam owns release authority. A distinct eligible human reviewer must inspect the final candidate and approve before any future version-producing merge; if no eligible reviewer exists, that merge remains blocked. Do not call the rule GitHub-enforced while the protected branch requires zero approving reviews. | Preserves the human gate without pretending that a PR author can self-approve. |
| Initializer README proposal | Keep the current product-specific `docs/agent-init/README.md`; add the proposed template digest line only if it improves provenance without replacing product guidance. Record the current initializer's exit 3 as a preserved proposal until this is resolved. | The generic generated README would lose project-specific adoption history. |

## Hosted controls and blockers

The public repository and MIT license are observed. `main` currently requires `java` and `client-contracts` checks and disallows force push/deletion; required approving reviews are zero. Human review and reviewer identity are **remote-work blockers for a version-producing merge** until a real eligible review path is chosen and configured where feasible. This goal may prepare and push its branch without merging a version. A later goal must determine whether branch protection can enforce a distinct human approval on this account and must verify stale-review dismissal, bypass and admin behavior before claiming the hosted gate.

The CLI role-selector gap is an **AC02 blocker**. A supported native role invocation in Codex CLI or the IDE must be observed, or the project owner must approve a revised acceptance route under the preceding G-0001 contract. File presence and renamed generic subagents are insufficient evidence.

## Decision record

| Group | Owner decision/date | Approved revision or reason for rejection |
| --- | --- | --- |
| Product commands and support | Adopted, 12 October 2026 | Scoped pilot configuration and maintained commands; broader support remains future work |
| Decision owners | Adopted, 12 October 2026 | Adam Burdett holds the named project authority; branch writers remain per-goal |
| Independent reviews | Adopted, 12 October 2026 | N=2, R=3 and assigned guides; actual native-role gap remains open |
| Local isolation and execution bounds | Adopted, 12 October 2026 | One owned instance and serialized shared-state tests for G-0001; no two-instance claim |
| Development recovery | Adopted, 12 October 2026 | One-hour active-work RPO target, one-working-day RTO target, retained evidence and restore triggers; targets still need measurement |
| Release control | Adopted, 12 October 2026 | Distinct eligible human review before a later version-producing merge; current GitHub review rule does not enforce this |
| Initializer README proposal | Adopted, 12 October 2026 | Keep product README and add the exact proposed digest line |

The adoption decision authorizes these project choices only. It does not grant an agent human-approval authority. The maintained policy documents and current evidence must be checked against this decision and independently reviewed under the preceding accepted baseline.
