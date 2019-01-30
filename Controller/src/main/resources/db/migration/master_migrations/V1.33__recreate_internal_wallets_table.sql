DROP TABLE IF EXISTS INTERNAL_WALLET_BALANCES;

CREATE TABLE INTERNAL_WALLET_BALANCES
(
  id                INT(40) UNSIGNED PRIMARY KEY   NOT NULL AUTO_INCREMENT,
  currency_id       INT(40)                        NOT NULL,
  role_id           INT(40)                        NOT NULL,
  usd_rate          NUMERIC(19, 12)                           DEFAULT 0,
  btc_rate          NUMERIC(19, 12)                           DEFAULT 0,
  total_balance     NUMERIC(30, 8)                            DEFAULT 0,
  total_balance_usd NUMERIC(30, 8)                            DEFAULT 0,
  total_balance_btc NUMERIC(30, 8)                            DEFAULT 0,
  last_updated_at   TIMESTAMP                      NULL       DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT internal_wallet_balances_currency_id_fk FOREIGN KEY (currency_id) REFERENCES CURRENCY (id),
  CONSTRAINT internal_wallet_balances_role_id_fk FOREIGN KEY (role_id) REFERENCES USER_ROLE (id)
);

INSERT IGNORE INTO INTERNAL_WALLET_BALANCES (currency_id, role_id)
SELECT cur.id AS currency_id, ur.id AS role_id
FROM CURRENCY cur CROSS JOIN USER_ROLE ur
ORDER BY cur.id, ur.id