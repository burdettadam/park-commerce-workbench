# Admission-only checkout slice

Local candidate toward G-0011, preserving previous code and roadmap proposals.

- [Plan and authority boundaries](PLAN.md)
- [API, persistence, lifecycle and key scope](../../project/admission-checkout.md)
- [Expected synthetic examples](../../../contracts/admission/admission-examples.json)
- [Goal coverage and open gates](GOAL-COVERAGE.md)
- [Executed verification and handoff](VERIFICATION.md)

Run `bash scripts/test-core.sh --demo` with JDK 21 and Bash. Admission UI, real
payments, cancellation and cart integration are excluded. Run the actual database
and HTTP gates on a workstation before claiming adapter behavior. Then obtain
independent contract and adversarial reviews of the fixed candidate and human
validation under the adopted project rules.

Full ZIP is source-only. The patch targets the preceding pricing-contract ZIP.
The local bundle is separate development history, not the existing GitHub history;
never force-push it over the remote. Verify private visibility before any publish.
