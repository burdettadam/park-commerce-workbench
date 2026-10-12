# Starter code — local implementation candidate

Authorization: the user's request to land starter code in this chat. Delivery remains a local ZIP, not a push or a release.

## Bounded scope fixed before implementation

1. Repair the `git-agent` exact-version guard and extend its offline regression suite. The reviewed upstream command prints `git-agent 0.2.0`. Reject look-alike, empty, malformed and unsuccessful version output before calling init. Preserve preview/apply arguments, root checks, existing files and initializer exit status.
2. Add a side-effect-free employee pricing preview for a caller-supplied `PriceBook` and date/party. Preview and real quote issuance must share the same monetary calculation. Return the complete candidate plus deterministic content identity, mark its input as uncommitted, and omit quote ID/owner/expiry. Preview must not consult or mutate the pricing store. Expose it through employee-only REST and the existing Vaadin console.

Preview expected example, independently calculated: 2 adults at $100 plus 2 children at $70 = $340 subtotal. A 10% family discount yields $34 discount and $306 total. Rounding is half-up per variant subtotal, matching the existing policy. Publishing the same candidate must produce the same lines and total in a subsequent quote. Quotes already issued must retain their prior prices.

## Verification plan

Preserve baseline tests; add regression tests and capture a failing run before each implementation where possible. Run all dependency-free suites. Add database/HTTP checks for workstation execution, but do not report them as passed unless run. Use fixed source commits and retain the raw commands, outputs, exit codes and content digests.

## Roadmap relationship and limits

This prepares implementation candidates for G-0002 and G-0008; it does not accept, complete or bypass their milestone dependencies. G-0001 native initialization, G-0007 catalog draft/publication work, full framework builds, independent reviewers and human intended-use validation remain outstanding. The user authorized local code, not release approval. No simulated reviewer or successful-goal manifest will be produced. Goal status is not changed to `ready`.

No private initializer implementation, cloud resources, production authentication, fake lockfiles, invented benchmarks, or remote writes are in scope. Source was imported from the supplied roadmap ZIP into a separate local Git history, not cloned from or merged into GitHub.
