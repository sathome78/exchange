CREATE TABLE IF NOT EXISTS FREE_COINS_CLAIM
(
    id             INT UNSIGNED PRIMARY KEY                                 NOT NULL  AUTO_INCREMENT,
    currency_name  VARCHAR (45)                                             NOT NULL,
    amount         DOUBLE(40, 8)                                            NOT NULL,
    partial_amount DOUBLE(40, 8)                                            NOT NULL,
    total_quantity INT(40)                                                  NOT NULL  DEFAULT 0,
    single         TINYINT(1)                                               NOT NULL  DEFAULT 0,
    time_range     INT(5),
    creator_email  VARCHAR(128)                                             NOT NULL,
    status         ENUM ('CREATED', 'FAILED', 'REVOKED', 'CLOSED', 'NONE') NOT NULL  DEFAULT 'NONE'
);