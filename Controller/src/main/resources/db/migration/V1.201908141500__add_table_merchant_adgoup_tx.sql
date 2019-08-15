CREATE TABLE IF NOT EXISTS MERCHANT_ADGROUP_TX
(
    id                INT(11) PRIMARY KEY                 NOT NULL AUTO_INCREMENT,
    refill_request_id int                                 NOT null,
    tx                VARCHAR(255)                        NOT NULL,
    status            VARCHAR(255)                        NOT NULL,
    date              timestamp default CURRENT_TIMESTAMP NOT NULL,
    constraint FK_refill_request_id
        foreign key (refill_request_id) references REFILL_REQUEST (id)
)
    ENGINE = INNODB;