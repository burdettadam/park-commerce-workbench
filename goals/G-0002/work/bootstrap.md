# G-0002 activation and writer handoff

Writer/integration record maintainer: current Codex session on `goal/G-0002`. Owner for scope, repository administration and intended-use validation: Adam Burdett. One writer; no independent reviewer assignment has started.

Accepted proposal: unchanged `docs/roadmap/goals/G-0002/README.md` at `0a68d87d2312a1dd40f47a6ef2cd0bdaa938b80c`. Accepted active-plan commit P: `56ea84e4e0d0429e3c7069c7a48efd1d903b9bec`. Starting dependency: G-0001 ready checkpoint `f4ca6dc02d96a309763b1725b2eb3782cadd8f76`, with its native role discovery, owner decisions and evidence retained on `goal/G-0001`. G-0002 is stacked on that branch because `main` does not contain G-0001.

The current `scripts/init-agent.sh` compares the complete observed version line to `git-agent 0.2.0`; `scripts/test-tooling.sh` contains the existing negative-version matrix. These are source observations, not G-0002 test results. The prior G-0001 checks are dependency evidence only.

Next: use the worktree-flow instructions to create an owned disposable worktree/ref from this activation commit. Mutate only the disposable copy to the old substring behavior, commit it, run the existing mock test suite and retain the three required red assertions and actual exit. Keep its ref local-only and away from the public remote. Then run the unchanged guard suite and reviewed real preview on a fixed green candidate, keeping source identities and outputs separate.

No product service or container was started for activation. Do not run two mutable product instances. Keep raw paths, credentials and the private initializer out of the public branch. P was pushed to `origin/goal/G-0002` and independently retrieved in ignored `.tmp/g0002-activation-restore` on 12 October 2026; clone HEAD was P, all 455 source-manifest entries matched, `.env` was absent, and `node scripts/validate-roadmap.mjs` passed. The later report-only update must also be pushed and retrieved before handoff. No G-0002 acceptance or intended-use result is claimed by this activation check.
