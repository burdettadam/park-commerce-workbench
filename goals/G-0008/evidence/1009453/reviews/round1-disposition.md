# G-0008 round-one finding disposition

Both independent first reports were collected before reconciliation. Source S `100945358c88e4c72a7c444326c618e19b054180` and E1 `512ea881cbd42fbb3f227a8e575b261b0867212f` remain the reviewed identities. This document does not revise either reviewer's first finding.

The adversarial review identified a genuine generic HTTP 400 display path, but its proposed overlapping-rate and 21-ticket UI examples do not reach that handler. [CommerceView.java](../../../../../admin-console/src/main/java/dev/parkcommerce/admin/CommerceView.java) constructs one rate per variant and a `Pricing.Request` before `post`; [Pricing.java](../../../../../core/src/main/java/dev/parkcommerce/core/Pricing.java) rejects overlapping rates and combined parties over 20 in constructors, and `action` displays a local `Failure` message. We retract the integration owner's initial claim that 21 tickets reaches HTTP.

An employee can select a past visit date in the UI. `Pricing.Request` accepts a well-formed past date, while `Pricing.Service.preview` rejects it against current park time with `INVALID_INPUT` and an actionable detail. The old UI replaces that HTTP 400 with generic text. The G-0008 implementation writer is preparing a narrow safe display fix and an exercise of this reachable path. This is a source change, so the next candidate must rerun affected checks and receive a new independent review pair under the three-round limit.

The contract reviewer's intended-use validation blocker remains open. No intended-use delegation or validation has been recorded yet. E1 also lacks a filled readiness manifest and browser check. No readiness or main merge is claimed.
