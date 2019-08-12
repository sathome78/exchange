DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
  BEGIN
    IF NOT EXISTS(SELECT NULL
                  FROM INFORMATION_SCHEMA.COLUMNS
                  WHERE table_name = 'MERCHANT' AND column_name = 'type_verification')
    THEN

      ALTER TABLE MERCHANT
        ADD COLUMN type_verification enum ('none', 'shuftipro', 'ariadnext') NOT NULL DEFAULT 'none';
    END IF;

  END;;
DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;
