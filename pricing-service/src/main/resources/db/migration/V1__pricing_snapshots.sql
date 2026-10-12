CREATE TABLE price_books (
  id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL PRIMARY KEY,
  product_id VARCHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  pricing_version BIGINT NOT NULL,
  payload LONGTEXT NOT NULL,
  CONSTRAINT ck_price_book_version CHECK (pricing_version > 0),
  CONSTRAINT ck_price_book_json CHECK (JSON_VALID(payload)),
  CONSTRAINT uk_price_book_version UNIQUE (product_id, pricing_version)
) ENGINE=InnoDB;
CREATE TABLE quotes (
  id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL PRIMARY KEY,
  owner VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  expires_epoch_ms BIGINT NOT NULL,
  payload LONGTEXT NOT NULL,
  CONSTRAINT ck_quote_json CHECK (JSON_VALID(payload)),
  INDEX ix_quotes_owner_expiry (owner, expires_epoch_ms)
) ENGINE=InnoDB;
