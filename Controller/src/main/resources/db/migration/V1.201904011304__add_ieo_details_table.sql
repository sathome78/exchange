CREATE TABLE IF NOT EXISTS IEO_DETAILS
(
    id                   INT         NOT NULL AUTO_INCREMENT,
    currency_name        VARCHAR(10) NOT NULL,
    maker_id             INT         NOT NULL,
    rate                 DOUBLE      NOT NULL,
    amount               DOUBLE      NOT NULL,
    available_amount     DOUBLE      DEFAULT 0.0,
    contributors         INT                                                DEFAULT 0,
    status               enum ('PENDING', 'RUNNING', 'SUCCEEDED', 'FAILED') DEFAULT 'PENDING',
    min_amount           DOUBLE                                             DEFAULT 0.0,
    max_amount_per_claim DOUBLE                                             DEFAULT 0.0,
    max_amount_per_user  DOUBLE                                             DEFAULT 0.0,
    starts_at            TIMESTAMP   NOT NULL,
    terminates_at        TIMESTAMP,
    created_at           TIMESTAMP                                          DEFAULT CURRENT_TIMESTAMP,
    created_by           INT         NOT NULL
) engine InnoDB;