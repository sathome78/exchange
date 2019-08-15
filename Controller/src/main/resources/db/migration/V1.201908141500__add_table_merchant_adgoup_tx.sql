CREATE TABLE IF NOT EXISTS MERCHANT_ADGROUP_TX
(
    id                INT(11) PRIMARY KEY                 NOT NULL AUTO_INCREMENT,
    refill_request_id INT(11)                             NOT null,
    user_id           INT(11)                             NOT null,
    tx                VARCHAR(255)                        NOT NULL,
    status            VARCHAR(255)                        NOT NULL,
    date              timestamp default CURRENT_TIMESTAMP NOT NULL,
    constraint FK_refill_request_id
        foreign key (refill_request_id) references REFILL_REQUEST (id),
    constraint FK_user_id
        foreign key (user_id) references USER (id)
)
    ENGINE = INNODB;