DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER $$
CREATE PROCEDURE `Alter_Table`()
  BEGIN

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'MERCHANT'
                         AND column_name = 'needVerification')  THEN

      ALTER TABLE MERCHANT ADD COLUMN needVerification tinyint(1) NOT NULL DEFAULT 0;

    END IF;

  END $$
DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE `Alter_Table`;
