-- Additive migration: V1 event rows, tables and API contracts are untouched.
-- The order plus all issued entitlements are one immutable JSON snapshot.
-- Route-scoped keys: (admission-orders, authenticated owner, original key).
CREATE TABLE admission_orders (
  id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  quote_id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  owner VARBINARY(400) NOT NULL,
  idempotency_key VARBINARY(100) NOT NULL,
  product_id VARCHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  visit_date DATE NOT NULL,
  quantity INT NOT NULL,
  total_cents BIGINT NOT NULL,
  confirmed_epoch_ms BIGINT NOT NULL,
  payload JSON NOT NULL,
  CONSTRAINT uk_admission_quote UNIQUE (quote_id),
  CONSTRAINT uk_admission_owner_key UNIQUE (owner,idempotency_key),
  CONSTRAINT ck_admission_quantity CHECK (quantity BETWEEN 1 AND 20),
  CONSTRAINT ck_admission_total CHECK (total_cents BETWEEN 0 AND 2000000000),
  CONSTRAINT ck_admission_owner CHECK (OCTET_LENGTH(owner) BETWEEN 1 AND 400),
  CONSTRAINT ck_admission_key CHECK (OCTET_LENGTH(idempotency_key) BETWEEN 1 AND 100),
  INDEX ix_admission_owner_time (owner,confirmed_epoch_ms)
) ENGINE=InnoDB;
