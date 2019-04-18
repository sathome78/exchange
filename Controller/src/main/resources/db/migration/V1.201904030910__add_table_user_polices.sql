CREATE TABLE IF NOT EXISTS USER_POLICES
(
  id        INT(11) PRIMARY KEY         NOT NULL AUTO_INCREMENT,
  policy_id INT(11)                     NOT NULL,
  user_id   INT(11)                     NOT NULL,
  FOREIGN KEY (user_id) REFERENCES USER (id),
  FOREIGN KEY (policy_id) REFERENCES POLICY (id)
)
  ENGINE = INNODB;