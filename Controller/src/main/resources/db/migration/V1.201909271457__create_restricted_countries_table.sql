CREATE TABLE IF NOT EXISTS `RESTRICTED_COUNTRY`
(
    `id`                   int(11)      NOT NULL AUTO_INCREMENT,
    `restricted_operation` enum ('TRADE', 'WITHDRAW', 'REFILL', 'IEO', 'UNDEFINED'),
    `country_name`         varchar(255) not null,
    `country_code`         varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
