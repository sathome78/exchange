INSERT IGNORE INTO `MERCHANT` (`description`, `name`, `transaction_source_type_id`, `service_bean_name`, `process_type`)
VALUES ('Syndex', 'Syndex', 2, 'syndexServiceImpl', 'MERCHANT');


INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, merchant_input_commission,
                                      merchant_output_commission)
VALUES ((SELECT id from MERCHANT WHERE name = 'Syndex'),
        (SELECT id from CURRENCY WHERE name = 'USD'), 10, 0, 0);

INSERT IGNORE INTO `MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`)
VALUES ((SELECT id from MERCHANT WHERE name = 'Syndex'), '/client/img/merchants/yandexmoney.png',
        'Syndex', (SELECT id from CURRENCY WHERE name = 'USD'));




CREATE TABLE IF NOT EXISTS SYNDEX_ORDER (
    refill_request_id          INT(11) PRIMARY KEY          NOT NULL,
    syndex_id                  INT UNSIGNED,
    modification_date          TIMESTAMP DEFAULT NOW()      NOT NULL,
    user_id                    INT(40)                      NOT NULL,
    confirmed                  TINYINT(1) DEFAULT FALSE     NOT NULL,
    amount                     DOUBLE(40, 8)                NOT NULL,
    status_id                  INT UNSIGNED                 NOT NULL,
    commission                 DOUBLE(40, 8)                NOT NULL,
    payment_system_id          VARCHAR(200)                 NOT NULL,
    currency                   VARCHAR(20)                  NOT NULL,
    country_id                 VARCHAR(20)                  NOT NULL,
    payment_details            VARCHAR(2000)                CHARACTER SET utf8 COLLATE utf8_unicode_ci,
    FOREIGN KEY syndex_order_ref_req_fk(refill_request_id) REFERENCES REFILL_REQUEST(id),
    FOREIGN KEY syndex_order_user_id_fk(user_id) REFERENCES USER(id),
    index (syndex_id)
)
