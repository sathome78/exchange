CREATE TABLE IF NOT EXISTS USER_FAVORITE_CURRENCY_PAIRS (
  user_id INT NOT NULL,
  currency_pair_id INT NOT NULL,
  PRIMARY KEY (user_id, currency_pair_id),
  INDEX user_cp_idx (user_id),
  FOREIGN KEY user_cp_user_fk(user_id)
  REFERENCES USER(id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY user_cp_cp_fk(currency_pair_id)
  REFERENCES CURRENCY_PAIR(id) ON DELETE CASCADE ON UPDATE CASCADE
);