CREATE TABLE IF NOT EXISTS FREE_COINS_SETTINGS
(
    id                 INT UNSIGNED PRIMARY KEY                                 NOT NULL  AUTO_INCREMENT,
    currency_id        INT(11)                                                  NOT NULL,
    min_amount         DOUBLE(40, 8)                                            NOT NULL  DEFAULT 0.00000001,
    min_partial_amount DOUBLE(40, 8)                                            NOT NULL  DEFAULT 0.00000001,
    CONSTRAINT free_coins_settings_currency_id_fk FOREIGN KEY (currency_id) REFERENCES CURRENCY (id)
);

INSERT IGNORE INTO FREE_COINS_SETTINGS (currency_id)
SELECT cur.id FROM CURRENCY cur;