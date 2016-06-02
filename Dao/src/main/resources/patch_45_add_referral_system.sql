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

#IMPORTANT! To be able to work with this table (COMMON_REFERRAL_ROOT) -  you have to add explicitly one and only one user to this table
CREATE TABLE IF NOT EXISTS COMMON_REFERRAL_ROOT (
  user_id INT,
  FOREIGN KEY COMMON_REFERRAL_ROOT(user_id) REFERENCES USER(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

INSERT INTO OPERATION_TYPE (id, name) VALUE (6,'referral');

CREATE TABLE IF NOT EXISTS REFERRAL_USER_GRAPH (
  child  INT,
  parent INT,
  FOREIGN KEY REFERRAL_USER_GRAPH(child) REFERENCES USER (id),
  FOREIGN KEY REFERRAL_USER_GRAPH(parent) REFERENCES USER (id),
  PRIMARY KEY (child, parent)
);

CREATE TABLE IF NOT EXISTS REFERRAL_TRANSACTION (
  id INT AUTO_INCREMENT PRIMARY KEY ,
  order_id INT,
  referral_level_id INT,
  initiator_id INT NOT NULL,
  user_id INT NOT NULL ,
  FOREIGN KEY t_fk(id) REFERENCES TRANSACTION(id) ON UPDATE CASCADE ON DELETE RESTRICT,
  FOREIGN KEY o_fk(order_id) REFERENCES EXORDERS(id) ON UPDATE CASCADE ON DELETE RESTRICT ,
  FOREIGN KEY rl_fk(referral_level_id) REFERENCES REFERRAL_LEVEL(id) ON UPDATE CASCADE ON DELETE RESTRICT,
  FOREIGN KEY inituser_fk(initiator_id) REFERENCES USER (id),
  FOREIGN KEY user_id_fk(user_id) REFERENCES USER(id)
);

INSERT INTO COMMISSION (operation_type, value) VALUES (6, 0);

ALTER TABLE TRANSACTION CHANGE COLUMN source_type source_type enum('ORDER','MERCHANT','REFERRAL','ACCRUAL') DEFAULT NULL;

INSERT INTO DATABASE_PATCH VALUES('patch_45_added_referral_system',default,1);