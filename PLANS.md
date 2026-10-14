# Accepted plans and living goal records

The [10-milestone roadmap](docs/roadmap/README.md) contains goal proposals. [G-0001](goals/G-0001/README.md), [G-0002](goals/G-0002/README.md), and [G-0003](goals/G-0003/README.md) are ready and their source contents were promoted to `main` at `d7ed805158bd6a239480ce7ae03312c139f2acc7`. [G-0004](goals/G-0004/README.md) is ready for the accepted clean Java, Vaadin and Angular build outcome and was promoted by [PR #5](https://github.com/burdettadam/park-commerce-workbench/pull/5) to `main` at `c873cfe1b8dd93732ef0d63f1b890b9774174934`. [G-0005](goals/G-0005/README.md) is ready for real MongoDB/MySQL persistence verification at `f95ca02f1f54b4010c0e9b4c00cba6fa685d689e` and was promoted by [PR #7](https://github.com/burdettadam/park-commerce-workbench/pull/7) to `main` at `a16f17a22cbec11d5734d31bf1fbeca8dd70058e`. [G-0006](goals/G-0006/README.md) is ready for the accepted local event-purchase demonstration and was promoted by [PR #8](https://github.com/burdettadam/park-commerce-workbench/pull/8) to `main` at `c734a95836388ec8316a06bac8c7361d9ef8b0de`. [G-0007](goals/G-0007/README.md) is ready for reviewable catalog drafts and publication history at evidence checkpoint `57a5bda317281e53f01ee474ca35dce9afb2fe47`; [PR #9](https://github.com/burdettadam/park-commerce-workbench/pull/9) was squash-merged to `main` at `1488fe0ba5aee271097bdcb0f03b2d930423e5b3`. No version tag, release artifact or deployment has been approved.

[G-0008](goals/G-0008/README.md) is ready for unpublished pricing previews at fixed source `56141ae40f05704ff0ae30b232ef878e2943b6c6`; the completed [evidence manifest](goals/G-0008/evidence/56141ae/manifest.json) passed in a fresh clone. [PR #12](https://github.com/burdettadam/park-commerce-workbench/pull/12) was squash-merged to `main` at `50bbbdedb4928d2127cc6a950d8eaa9f70d77b48`. The original `goal/G-0008` branch remains published for source and evidence identity.

[G-0009](goals/G-0009/README.md) is ready for the versioned price and quote contract at fixed source `6578753c94674d95793f3ffd82ad7e67d9e98c4e`. Its completed [evidence manifest](goals/G-0009/evidence/6578753/manifest.json) passed with six checks and 31 artifacts in a fresh public clone; both native reviewers passed the scoped fourth round. [PR #14](https://github.com/burdettadam/park-commerce-workbench/pull/14) was squash-merged to `main` at `15010a258ac6618045d5afb35fb971127ce3ae7c`. The original `goal/G-0009` branch remains published for source/evidence identity. G-0010 consumed this accepted input. No version tag, release artifact or deployment was created.

[G-0010](goals/G-0010/README.md) is ready for booking contention and interrupted-checkout recovery at fixed source `c6f432bf25f3ee71b4e58b7dd17c70272d2e9ff5`. Its [evidence manifest](goals/G-0010/evidence/c6f432b/manifest.json) records the exact hosted checkout tree, two passing configured native round-2 reviews, delegated intended-use acceptance, and verified retrieval of all 20 artifacts. [PR #16](https://github.com/burdettadam/park-commerce-workbench/pull/16) passed all four free final-head CI jobs and was squash-merged to main at `354890960685b7e527b9a08f04ea694129f0463f`. The original goal branch remains published for S/E identity. No version tag, release artifact or deployment was created.

This is a project convention informed by OpenAI's ExecPlan pattern. It is not a
native Codex database or parsed configuration schema. Reuse existing issue/plan
files with the responsibilities below. Keep enough context in Git to resume on
a replacement computer without the previous conversation.

## Ready work

Before executing a slice, record accepted intent and authority, intended user and
observable outcome, operating/data/failure conditions, applicable measurable
quality criteria, assumptions/exclusions, validation owner/method, and selected
risk/review policy with rationale. Required unresolved criteria are planning gaps.
Refine steps within accepted scope; adopt material scope changes through the
governing baseline rather than editing success criteria to match the code.

Features, defects, incidents, dependency updates, backports and retirement use
the same contract. An urgent plan may be brief and still identifies affected
versions, scope, owner, authority, reproduction and required evidence. Every
version-producing merge retains the human review gate.

## One authority for each fact

- Accepted scope lives in its plan. A small change may combine feature and goal.
- Canonical goal-wide state lives on its declared integration ref, maintained by
  the integration owner. A single work branch can also be that ref.
- Each work branch owns its progress record, based on a recorded goal revision.
- Native reports/artifacts hold observed results; link them instead of copying
  large logs. Human-friendly status is derived from these records.
- Essential distributable guidance stays with the product. Pin required private
  organization guidance and document access; sibling repositories are explicit
  dependencies and are not automatically discovered instructions.

Suggested paths are `features/F-0001.md`, `goals/G-0001/README.md`, and
`goals/G-0001/work/<slice>.md`; create records only when actual work exists.

## Goal record outline

```markdown
# <goal> - <observable outcome>

Accepted plan/authority/revision:
Intended user/outcome:
Operating conditions, quality limits, assumptions/exclusions:
Acceptance criteria and independent expected results:
Intended-use validation owner/method:
Governing baseline, risk/review profile, pending control changes:
Canonical repository/ref/path and integration owner:
Status and next action:

| Slice | Branch/ref | Writer/role | Plan revision | Dependencies | Included revision | Work/evidence links |
| --- | --- | --- | --- | --- | --- | --- |

Progress, discoveries, consequential decisions, blockers:
Candidate S and relevant base/inputs:
Verification and validation:
Reviews N / rounds R, findings and dispositions:
Exceptions/debt with owner and expiry/reconsideration:
Release candidate and actual human approval:
Actual merged M, artifact D, version/tag, demo, deployed health/owner:

| Recovery component | Remote ref or retained artifact | Confirmed revision/digest | Time | Retention/retrieval |
| --- | --- | --- | --- | --- |

Whole-goal coverage, confirmation age and recovery targets:
Local-only remainder and actual next action:
```

Include parallel, dependent, or alternative branches. Record which exact revisions
enter the combined candidate. Retain a disposition for abandoned alternatives;
they need not be integrated. Branch writers propose scope changes to the
integration owner rather than independently rewriting canonical goal status.

## Progress, integration and handoff

Use `planned`, `active`, `blocked`, `paused`, `ready`, `needs-decision`, `cancelled`
or `superseded` as project states. Blocked/paused work identifies the reason,
responsible owner and next action. Cancelled/superseded work retains the reason,
checkpoint, and replacement where applicable. Ready applies only to the candidate
meeting governance's verification/review gate; release and deployment remain
separate observations.

Integrate small accepted increments early while the goal remains open, respecting
the target's gates. Refresh dependent branches when contracts/base inputs change.
Changed relevant inputs reopen verification/review. At start, resume and handoff,
review stale assignments, dependencies, exceptions, and active-work limits.

Handoff records the writing owner/session, checkpoint, owned resources and next
action. Reconcile an uncertain old writer before takeover. Publish branch and
artifact checkpoints first, then the coordinating goal record. Report recovery
for every included component, not just the most recently pushed branch.
