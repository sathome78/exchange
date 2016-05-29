CREATE TABLE IF NOT EXISTS REFERRAL_LEVEL(
    id INT PRIMARY KEY AUTO_INCREMENT,
    level INT NOT NULL,
    percent DOUBLE,
    datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP()
);


INSERT INTO REFERRAL_LEVEL (level, percent) VALUES (1, 0);
INSERT INTO REFERRAL_LEVEL (level, percent) VALUES (2, 0);
INSERT INTO REFERRAL_LEVEL (level, percent) VALUES (3, 0);
INSERT INTO REFERRAL_LEVEL (level, percent) VALUES (4, 0);
INSERT INTO REFERRAL_LEVEL (level, percent) VALUES (5, 0);
INSERT INTO REFERRAL_LEVEL (level, percent) VALUES (6, 0);
INSERT INTO REFERRAL_LEVEL (level, percent) VALUES (7, 0);


CREATE TABLE IF NOT EXISTS COMMON_REFERRAL_ROOT (
  user_id INT,
  FOREIGN KEY COMMON_REFERRAL_ROOT(user_id) REFERENCES USER(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

INSERT INTO OPERATION_TYPE (name) VALUE ('referral');

CREATE TABLE IF NOT EXISTS REFERRAL_USER_GRAPH (
  child  INT,
  parent INT,
  FOREIGN KEY REFERRAL_USER_GRAPH(child) REFERENCES USER (id),
  FOREIGN KEY REFERRAL_USER_GRAPH(parent) REFERENCES USER (id),
  PRIMARY KEY (child, parent)
);

CREATE TABLE IF NOT EXISTS REFERRAL_TRANSACTION (
  transaction_id INT PRIMARY KEY ,
  order_id INT,
  referral_level_id INT,
  FOREIGN KEY t_fk(transaction_id) REFERENCES TRANSACTION(id) ON UPDATE CASCADE ON DELETE RESTRICT,
  FOREIGN KEY o_fk(order_id) REFERENCES EXORDERS(id) ON UPDATE CASCADE ON DELETE RESTRICT ,
  FOREIGN KEY rl_fk(referral_level_id) REFERENCES REFERRAL_LEVEL(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

SELECT * FROM REFERRAL_LEVEL;


INSERT INTO REFERRAL_TRANSACTION (transaction_id, order_id, referral_level_id) VALUES (39, 3,1);


SELECT REFERRAL_LEVEL.id, REFERRAL_LEVEL.level, REFERRAL_LEVEL.percent
FROM REFERRAL_LEVEL;


SELECT * FROM REFERRAL_LEVEL;

