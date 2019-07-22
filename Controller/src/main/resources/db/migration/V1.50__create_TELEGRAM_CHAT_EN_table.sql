CREATE TABLE IF NOT EXISTS `TELEGRAM_CHAT_EN` (
  `id`              int(11)       NOT NULL AUTO_INCREMENT,
  `chat_id`         bigint(19)    NOT NULL,
  `username`        varchar(256)  NOT NULL,
  `text`            text          NOT NULL,
  `message_time`    datetime      NOT NULL,
  `username_reply`  varchar(256)  DEFAULT NULL,
  `text_reply`      text          DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) CHARSET=utf8;
