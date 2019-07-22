CREATE TABLE IF NOT EXISTS COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS
(
  id             INT UNSIGNED PRIMARY KEY   NOT NULL AUTO_INCREMENT,
  currency_id    INT                        NOT NULL,
  wallet_address VARCHAR(128),
  balance        NUMERIC(30, 8)                      DEFAULT 0,
  CONSTRAINT company_wallet_external_reserved_address_currency_id_fk FOREIGN KEY (currency_id) REFERENCES CURRENCY (id)
);