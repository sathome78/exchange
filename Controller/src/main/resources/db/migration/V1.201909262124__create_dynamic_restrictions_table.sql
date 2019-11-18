CREATE TABLE IF NOT EXISTS `DYNAMIC_RESTRICTION`
(
    `id`                 int(11)      NOT NULL AUTO_INCREMENT,
    `currency_pair_name` varchar(64)  NOT NULL,
    `res_name`           varchar(64)  NOT NULL,
    `res_description`    varchar(255),
    `res_condition`      varchar(255) NOT NULL,
    `error_code`         varchar(128) NOT NULL,
    `error_message`      varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
