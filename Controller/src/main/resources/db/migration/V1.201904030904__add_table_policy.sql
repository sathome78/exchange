CREATE TABLE IF NOT EXISTS POLICY
(
  id          INT(11) PRIMARY KEY         NOT NULL AUTO_INCREMENT,
  name        VARCHAR(255)                NOT NULL,
  title       VARCHAR(255)                NOT NULL,
  description TEXT                        NOT NULL,
  created     timestamp default NOW()     NOT NULL
)
  ENGINE = INNODB;