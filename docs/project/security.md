# Security, privacy, and isolation

## Workstation-only baseline

The backend `local` profile accepts four configured opaque bearer tokens. This is an explicit local development arrangement, not JWT validation, OpenID Connect, SSO, or a production identity solution. The regular backend profile denies all requests. Compose's application and database host mappings bind to `127.0.0.1`; the Vaadin server and Angular development server also bind loopback.

Bearer tokens are sent in headers, not query strings. No HTTP Basic or cookie authentication is enabled for the backend APIs. CSRF is disabled there because requests do not use ambient browser credentials; this must be reconsidered when introducing production cookies or sessions. Vaadin's own UI session behavior is distinct from the API's stateless bearer authentication.

The Vaadin console is intentionally a localhost-only tool without a login gateway. API writes still require the employee token. Do not expose it via a tunnel or change its bind address to a public interface. Angular keeps the guest token in memory; Vaadin keeps an entered employee token in the local view/session memory. No localStorage/database/file token persistence is implemented.

## Secrets and data

The repository contains no usable credentials. Clearly labeled fixed credentials in integration-test source are only for disposable Testcontainers applications. `configure-local.sh` creates fresh random credentials in a restrictive `.env`, refuses existing files/symlinks, and does not print secrets. It uses mode 600 on Unix and a current-account-only ACL on Windows. Never commit `.env`, private keys, account exports, real customer data, or external initializer source. `.gitignore` is a convenience, not a security scanner.

Only synthetic people/product/price data are permitted. Do not add actual payment cards or payment-provider keys. No paid monitoring subscription or cloud resource is provisioned.

## Trust boundaries

Authorization is checked at service endpoints and private GraphQL methods, not only in buttons. Ownership uses the authenticated subject, never a request-supplied owner. Internal pricing access has a service-only role. The booking core validates critical quote fields from the dependency before allocating inventory. JDBC values are bound as parameters. GraphQL depth/complexity are bounded, but comprehensive request rate limiting and request-size budgeting remain unimplemented.

Inbound failures must not disclose SQL, stack traces, tokens, or sensitive payloads. The initial error handler logs only exception class for unknown errors; this is deliberately sparse and should be expanded with safe trace-context fields during operations work, not by logging request secrets.

## Worktree isolation

Each local worktree should have its own `.env`, Compose project name, volumes, and nonconflicting published ports. Do not point tests at shared cloud data. Testcontainers test suites create their own disposable services; the ordinary Compose stack retains local named volumes. `down -v` is destructive and never called by default.

## Before production or public release

Implement production OIDC/TLS and service identities; review CORS/CSRF/session handling; add security and dependency/image scans, reviewed lockfiles, digest-pinned images/actions, supply-chain provenance, least-privilege cloud roles, validated secrets management, rate limits, backup recovery, retention policy, audit events, and independent review. These are unresolved acceptance gates, not protections already supplied by this ZIP.

GitHub visibility and branch/release settings are external controls. Verify the actual hosted controls before treating a passing workflow as a release gate.
