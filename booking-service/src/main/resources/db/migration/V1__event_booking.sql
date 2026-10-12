CREATE TABLE events (
  id VARCHAR(64) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  product_id VARCHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  visit_date DATE NOT NULL,
  capacity INT NOT NULL,
  CONSTRAINT ck_event_capacity CHECK (capacity BETWEEN 1 AND 1000000)
) ENGINE=InnoDB;
CREATE TABLE holds (
  id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  event_id VARCHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  owner VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  quote_id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  quantity INT NOT NULL,
  total_cents BIGINT NOT NULL,
  created_epoch_ms BIGINT NOT NULL,
  expires_epoch_ms BIGINT NOT NULL,
  status VARCHAR(12) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  CONSTRAINT fk_hold_event FOREIGN KEY (event_id) REFERENCES events(id),
  CONSTRAINT uk_hold_quote UNIQUE (quote_id),
  CONSTRAINT ck_hold_quantity CHECK (quantity BETWEEN 1 AND 20),
  CONSTRAINT ck_hold_total CHECK (total_cents BETWEEN 0 AND 2000000000),
  CONSTRAINT ck_hold_time CHECK (expires_epoch_ms > created_epoch_ms),
  CONSTRAINT ck_hold_status CHECK (status IN ('ACTIVE','CONFIRMED','EXPIRED')),
  INDEX ix_holds_event_status_expiry (event_id,status,expires_epoch_ms)
) ENGINE=InnoDB;
CREATE TABLE orders (
  id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  hold_id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  event_id VARCHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  owner VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  idempotency_key VARCHAR(100) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  quote_id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  quantity INT NOT NULL,
  total_cents BIGINT NOT NULL,
  confirmed_epoch_ms BIGINT NOT NULL,
  CONSTRAINT fk_order_hold FOREIGN KEY (hold_id) REFERENCES holds(id),
  CONSTRAINT fk_order_event FOREIGN KEY (event_id) REFERENCES events(id),
  CONSTRAINT uk_order_hold UNIQUE (hold_id),
  CONSTRAINT uk_order_owner_key UNIQUE (owner,idempotency_key),
  CONSTRAINT ck_order_quantity CHECK (quantity BETWEEN 1 AND 20),
  CONSTRAINT ck_order_total CHECK (total_cents BETWEEN 0 AND 2000000000),
  INDEX ix_orders_event (event_id),
  INDEX ix_orders_owner_time (owner,confirmed_epoch_ms)
) ENGINE=InnoDB;
