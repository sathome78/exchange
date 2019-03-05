CREATE TABLE IF NOT EXISTS FIAT_PAIR
(
  id             INT UNSIGNED PRIMARY KEY   NOT NULL AUTO_INCREMENT,
  currency1_id   INT(11)                    NOT NULL,
  currency2_id   INT(11)                    NOT NULL,
  ticker_name    VARCHAR(16)                NOT NULL,
  market         VARCHAR(8)                 DEFAULT 'FIAT',
  hidden         TINYINT(1)                 DEFAULT 0,
  INDEX ticker_name_idx (ticker_name),
  CONSTRAINT fiat_par_currency1_id_fk FOREIGN KEY (currency1_id) REFERENCES CURRENCY (id),
  CONSTRAINT fiat_par_currency2_id_fk FOREIGN KEY (currency2_id) REFERENCES CURRENCY (id)
) AUTO_INCREMENT=3000;

INSERT IGNORE INTO FIAT_PAIR(currency1_id, currency2_id, ticker_name) VALUES (20, 2, 'AED/USD');
INSERT IGNORE INTO FIAT_PAIR(currency1_id, currency2_id, ticker_name) VALUES (8, 2, 'CNY/USD');
INSERT IGNORE INTO FIAT_PAIR(currency1_id, currency2_id, ticker_name) VALUES (3, 2, 'EUR/USD');
INSERT IGNORE INTO FIAT_PAIR(currency1_id, currency2_id, ticker_name) VALUES (10, 2, 'IDR/USD');
INSERT IGNORE INTO FIAT_PAIR(currency1_id, currency2_id, ticker_name) VALUES (12, 2, 'INR/USD');
INSERT IGNORE INTO FIAT_PAIR(currency1_id, currency2_id, ticker_name) VALUES (13, 2, 'NGN/USD');
INSERT IGNORE INTO FIAT_PAIR(currency1_id, currency2_id, ticker_name) VALUES (1, 2, 'RUB/USD');
INSERT IGNORE INTO FIAT_PAIR(currency1_id, currency2_id, ticker_name) VALUES (11, 2, 'THB/USD');
INSERT IGNORE INTO FIAT_PAIR(currency1_id, currency2_id, ticker_name) VALUES (18, 2, 'TRY/USD');
INSERT IGNORE INTO FIAT_PAIR(currency1_id, currency2_id, ticker_name) VALUES (7, 2, 'UAH/USD');
INSERT IGNORE INTO FIAT_PAIR(currency1_id, currency2_id, ticker_name) VALUES (17, 2, 'VND/USD');