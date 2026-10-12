# Operational demonstration: implementation boundary

The backend POM includes Spring Boot's OpenTelemetry starter and Prometheus registry.
The normal local profile does not attempt to export traces or OTLP metrics.

To exercise traces, run with `SPRING_PROFILES_ACTIVE=local,telemetry` and set
`OTLP_TRACES_ENDPOINT` to your local Tempo/collector HTTP `/v1/traces` endpoint.
Micrometer metrics are available at `/actuator/prometheus` only with the INTERNAL
bearer credential. No credential is included here. Do not publish scrape credentials.

Still to implement and execute: a pinned Prometheus/Tempo/Grafana environment,
trace propagation assertions, useful business observations, dashboard provisioning,
structured-log ingestion into Splunk, saved SPL investigations, and AppDynamics
instrumentation with an authorized account. A dependency or YAML profile alone
is not completed monitoring evidence. No AppDynamics or Grafana commercial add-ons
are required or purchased by this source package.
