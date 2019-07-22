CREATE TABLE IF NOT EXISTS IEO_RESULT
(
    claim_id         INT(11)                                                NULL,
    ieo_id           INT(11)                                                NOT NULL,
    available_amount DOUBLE(40, 9)                      DEFAULT 0.000000000 NULL,
    status           ENUM ('SUCCESS', 'FAILED', 'NONE') DEFAULT 'NONE'      NOT NULL,
    FOREIGN KEY (ieo_id) REFERENCES IEO_DETAILS (id)
)
    ENGINE = INNODB;