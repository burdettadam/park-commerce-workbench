# Review evidence policy

This project keeps generated development evidence and artifacts out of Git. The
accepted goal defines the outcome; source and tests belong in Git. Local logs,
screenshots, traces, test reports, filled evidence manifests and reviewer
transcripts are temporary review material. The repository ignores `**/evidence/`
and `**/artifacts/`. Do not use `git add -f` to bypass those rules.

## While a feature PR is open

- Fix the source commit and relevant base before acceptance checks. Record the
  exact commit, command, environment, selected tests, result and limits in the PR
  or linked hosted CI run. A failed or skipped required check remains visible.
- Give independent reviewers the source, plan, PR and raw local or hosted output
  they need. Put their concise findings and blocker dispositions in PR review
  comments. Do not create an evidence-only commit to publish transcripts.
- Keep raw local outputs in ignored paths only as long as review needs them.
  Sanitize material before sharing it. Hosted CI logs and PR discussion may
  expire or be edited; do not describe them as permanent artifact storage.
- Source comments explain only non-obvious behavior or a durable contract.
  Put change rationale, investigation and rejected alternatives in the PR so
  Git history and blame can lead a reader to them.

The goal cards retain evidence IDs and example filenames as a checklist of
observations. Their manifest templates and `scripts/validate-evidence.mjs` are
legacy tools for earlier goals; they are not a requirement to commit filled
manifests or generated outputs for a new goal. Review still requires real
checks, two independent configured reviewers under `GOVERNANCE.md`, intended-use
validation and a recorded decision. Missing evidence remains a gap.

## After merge

Record the PR, exact merged commit and concise outcome in the goal record. Then
remove the feature's local generated evidence and artifacts, including obsolete
worktree copies, after confirming ownership and merge identity. Keep source,
reconstructable tests, necessary design or operating documentation, and PR/CI
links. Do not delete another owner's work or a separately required release,
security, legal or operational record; decide those retention needs explicitly.

Earlier goals committed evidence under the old policy. Removing those files from
the current tree does not remove them from existing Git history. Historical goal
records may name old evidence commits; treat those as historical references,
not a pattern for future work. Rewriting public history is a separate decision.
