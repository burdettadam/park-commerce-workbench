# Roadmap audit for the first git-agent trial

Audited 12 October 2026 against the public checkout and the reviewed sibling `git-agent` source. This is a planning correction and an observation record, not acceptance of the proposed governance or any goal.

## Observed starting state

| Area | Observation | Trial consequence |
| --- | --- | --- |
| Repository | `burdettadam/park-commerce-workbench` is public with MIT; original history remains in a separate private archive. The Windows fix is on an open PR branch. | Sanitize every source, log and evidence artifact before pushing. A private-repository prerequisite is obsolete. |
| Initializer | The sibling checkout's reviewed `bin/git-agent` prints `git-agent 0.2.0`. On this Windows host it is absent from the normal Git Bash command `PATH`; temporarily adding that `bin` directory allowed the real wrapper preview. | Install the reviewed command or set session `PATH`, then recheck the exact version and preview on the trial candidate. Do not copy the executable into this public repository. |
| Guidance | The 15 project-local guidance files already exist. The real dry run reported unchanged/adopted files and a preserved initializer proposal; it wrote nothing. | G-0001 is an adoption and gap-decision exercise, not a first import or scaffold generation claim. |
| Guard | `scripts/init-agent.sh` already uses an exact `git-agent 0.2.0` comparison. The earlier red source/results survive only as historical evidence. | G-0002 now tests a disposable weakened copy against the current green source. It cannot claim to repair the same defect again. |
| Product | The imported Java suite and 64 database integration tests passed in hosted CI. The Windows six-service stack and 43 live HTTP checks passed; the guest UI served HTTP 200. Angular type/build/state checks passed with a committed lockfile. | M1 can reuse dated evidence only after checking exact source, environment and criterion scope. Vaadin host run, browser interaction, clean restore and current candidate verification remain open. |
| CI and review | Public `main` and the Windows-fix PR have passed `java` and `client-contracts`. `main` requires both checks and a PR, but the personal-account owner cannot supply an independent GitHub approval to their own PR. The proposed two independent reviewer roles and human merge gate are not yet demonstrated for an accepted goal. | Do not call a passing workflow a completed goal or claim GitHub enforces a required human reviewer. Obtain an eligible human review before a version-producing merge as `AGENTS.md` requires. |

## Corrected first trial

1. Review the proposed authority, assignments and review policy in `GOVERNANCE.md`. Record an actual human adoption decision and any remaining gap in an active G-0001 plan. Do not infer acceptance from these files or this audit.
2. From a fixed source revision, verify the reviewed `git-agent 0.2.0` command on Git Bash `PATH`; run `scripts/init-agent.sh --preview`. Compare the file plan and gaps with current guidance. Rerun `--apply` only after inspecting its proposal/conflict behavior, and keep the output as evidence.
3. Record actual instruction/skill/role discovery in a fresh Codex session. A file listing or initializer dry run alone does not establish native discovery.
4. After G-0001 is accepted, freeze G-0002's proposal as an active plan. In a disposable worktree, commit the old substring mutation on a local-only challenge branch, observe the test failure, then run the green mock suite and real initializer preview on unchanged current source. Identify historical versus fresh evidence and any actual product diff.
5. Review and checkpoint that fixed candidate, then use G-0003 for the separate worktree/handoff drill. Later M1 goals should recheck current source from clean inputs and fill the Vaadin/browser gaps; foundation test counts are provenance, not current thresholds.

## Remaining decisions and limits

- `GOVERNANCE.md` still has `UNCONFIGURED` owners and calls its baseline proposed. `DEVELOPMENT.md` still lacks accepted supported versions, isolation limits, recovery objectives and clean-restore evidence. These are explicit G-0001 decisions or gaps, not facts filled by this audit.
- The roadmap's G-0001, G-0002, G-0004, G-0005, G-0006 and G-0020 proposal text and matching catalog/template criteria were revised for this snapshot. Other unaccepted goals retain their original proposals; revisit their exact commands, dependencies and external-access assumptions when adopting them.
- The old ZIP, planning discovery and starter-code verification documents are dated historical records. Do not execute their import instructions or treat their unexecuted/blocked statuses as the current checkout's status.
- This audit made no release, cloud, paid-tool or repository-rule change. The public branch may be pushed for review; version-producing merge still follows the human gate.
