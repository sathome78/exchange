SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS USER_VERIFICATION_INFO;
SET FOREIGN_KEY_CHECKS=1;

CREATE TABLE IF NOT EXISTS USER_VERIFICATION_INFO (
  user_id INTEGER NOT NULL,
  doc_type ENUM ('ID', 'P') DEFAULT 'P',
  doc_id VARCHAR(55),
  UNIQUE (user_id, doc_type))
  ENGINE = InnoDB;

DROP PROCEDURE IF EXISTS `Add_Constraint`;

DELIMITER $$
CREATE PROCEDURE `Add_Constraint`()
  BEGIN
    IF NOT EXISTS( SELECT `TABLE_SCHEMA`, `TABLE_NAME`
               FROM `information_schema`.`KEY_COLUMN_USAGE`
               WHERE `CONSTRAINT_NAME` IN ('fk_user_id_on_user_verification_info'))  THEN

      ALTER TABLE `USER_VERIFICATION_INFO`
                   ADD CONSTRAINT `fk_user_id_on_user_verification_info` FOREIGN KEY (user_id) REFERENCES USER(id);
    END IF;

  END $$
DELIMITER ;

CALL Add_Constraint();

DROP PROCEDURE `Add_Constraint`;