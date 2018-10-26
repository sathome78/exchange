DROP TABLE IF EXISTS COMPANY_WALLET_EXTERNAL;

CREATE TABLE COMPANY_WALLET_EXTERNAL
(
  id                INT(40) UNSIGNED PRIMARY KEY   NOT NULL AUTO_INCREMENT,
  currency_id       INT(40)                        NOT NULL UNIQUE,
  usd_rate          NUMERIC(19, 8)                          DEFAULT 0,
  btc_rate          NUMERIC(19, 8)                          DEFAULT 0,
  main_balance      NUMERIC(30, 8)                          DEFAULT 0,
  reserved_balance  NUMERIC(30, 8)                          DEFAULT 0,
  total_balance     NUMERIC(30, 8)                          DEFAULT 0,
  total_balance_usd NUMERIC(30, 8)                          DEFAULT 0,
  total_balance_btc NUMERIC(30, 8)                          DEFAULT 0,
  last_updated_at   TIMESTAMP                      NULL     DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT company_wallet_external_currency_id_fk FOREIGN KEY (currency_id) REFERENCES CURRENCY (id)
);

INSERT IGNORE INTO COMPANY_WALLET_EXTERNAL (currency_id)
SELECT cur.id
FROM CURRENCY cur;

