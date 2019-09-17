CREATE TABLE IF NOT EXISTS FREE_COINS_PROCESS
(
  id             INT UNSIGNED PRIMARY KEY                                 NOT NULL  AUTO_INCREMENT,
  giveaway_id    INT(11)  UNSIGNED                                        NOT NULL,
  receiver_email VARCHAR(128)                                             NOT NULL,
  received       TINYINT(1),
  last_received  TIMESTAMP,
  CONSTRAINT free_coins_process_giveaway_id_fk FOREIGN KEY (giveaway_id) REFERENCES FREE_COINS_CLAIM (id)
);