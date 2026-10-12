# Primary references consulted for the adapter

Project decisions, not standards-compliance claims. Reference access: 2026-10-12.

- MySQL 8.4 InnoDB error handling: https://dev.mysql.com/doc/refman/8.4/en/innodb-error-handling.html
  A duplicate-key error may roll back only its statement. This adapter does not
  treat that as proof its surrounding transaction rolled back; exceptions leave
  TransactionTemplate before reconciliation, and real tests remain required.
- MySQL 8.4 transactions: https://dev.mysql.com/doc/refman/8.4/en/innodb-autocommit-commit-rollback.html
  Distinguishes transaction commit, rollback and statement behavior.
- Spring TransactionTemplate API: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/support/TransactionTemplate.html
  Callback execution and unchecked exception rollback. Source verification does
  not substitute for the project's dependency-compiled integration tests.

No dependency or version was changed in this slice. The previous selected
compatibility baseline still requires real dependency resolution and review.
