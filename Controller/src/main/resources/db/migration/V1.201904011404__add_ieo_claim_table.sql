CREATE TABLE IF NOT EXISTS IEO_CLAIM
(
    id            INT(11) PRIMARY KEY                               NOT NULL AUTO_INCREMENT,
    ieo_id        INT(11)                                           NOT NULL,
    currency_name VARCHAR(64)                                       NOT NULL,
    maker_id      INT(11)                                           not null,
    user_id       INT(11)                                           not null,
    amount        double(40, 9)                                     not null,
    rate          double(40, 9)                                     not null,
    price_in_btc  double(40, 9)                                     not null,
    created       timestamp                          default NOW()  NOT NULL,
    status        ENUM ('SUCCESS', 'FAILED', 'NONE') DEFAULT 'NONE' NOT NULL,
    FOREIGN KEY (user_id) REFERENCES USER (id),
    FOREIGN KEY (maker_id) REFERENCES USER (id),
    FOREIGN KEY (ieo_id) REFERENCES IEO_DETAILS (id)
)
    ENGINE = INNODB;