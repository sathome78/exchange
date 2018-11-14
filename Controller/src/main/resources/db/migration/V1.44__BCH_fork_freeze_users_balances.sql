CREATE TABLE IF NOT EXISTS BCH_HARD_FORK_BALANCE_SNAPSHOT (
  id INT(40) NOT NULL AUTO_INCREMENT,
  user_id INT(40) NOT NULL,
  active_balance DOUBLE(40,9) NOT NULL DEFAULT '0.000000000',
  reserved_balance DOUBLE(40,9) NOT NULL DEFAULT '0.000000000',
  PRIMARY KEY (id),
  UNIQUE INDEX id_UNIQUE (id ASC),
  UNIQUE INDEX user_id_UNIQUE (user_id ASC)
  ) DEFAULT CHARACTER SET = utf8;

INSERT INTO BCH_HARD_FORK_BALANCE_SNAPSHOT SELECT id, user_id, active_balance, reserved_balance
FROM WALLET WHERE currency_id = (SELECT id FROM CURRENCY WHERE name = 'BCH');