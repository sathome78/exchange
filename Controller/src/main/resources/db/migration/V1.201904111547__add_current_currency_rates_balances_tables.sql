DROP TABLE IF EXISTS CURRENT_CURRENCY_RATES;

CREATE TABLE CURRENT_CURRENCY_RATES
(
  id                          INT(40) UNSIGNED PRIMARY KEY   NOT NULL AUTO_INCREMENT,
  currency_id                 INT(40)                        NOT NULL UNIQUE,
  currency_name               VARCHAR (45)                   NOT NULL UNIQUE,
  usd_rate                    NUMERIC(19, 8)                          DEFAULT 0,
  btc_rate                    NUMERIC(19, 8)                          DEFAULT 0,
  schedule_last_updated_at    TIMESTAMP                      NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT current_currency_rates_currency_id_fk FOREIGN KEY (currency_id) REFERENCES CURRENCY (id)
);

INSERT IGNORE INTO CURRENT_CURRENCY_RATES (currency_id, currency_name)
SELECT cur.id, cur.name
FROM CURRENCY cur;


DROP TABLE IF EXISTS CURRENT_CURRENCY_BALANCES;

CREATE TABLE CURRENT_CURRENCY_BALANCES
(
  id                          INT(40) UNSIGNED PRIMARY KEY   NOT NULL AUTO_INCREMENT,
  currency_id                 INT(40)                        NOT NULL UNIQUE,
  currency_name               VARCHAR (45)                   NOT NULL UNIQUE,
  balance                     NUMERIC(30, 8)                          DEFAULT 0,
  last_updated_at             TIMESTAMP                      NULL     DEFAULT CURRENT_TIMESTAMP,
  schedule_last_updated_at    TIMESTAMP                      NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT current_currency_rates_balances_currency_id_fk FOREIGN KEY (currency_id) REFERENCES CURRENCY (id)
);

INSERT IGNORE INTO CURRENT_CURRENCY_BALANCES (currency_id, currency_name)
SELECT cur.id, cur.name
FROM CURRENCY cur;