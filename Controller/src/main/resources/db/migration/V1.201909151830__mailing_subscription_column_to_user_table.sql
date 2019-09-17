DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
  BEGIN
    IF NOT EXISTS(SELECT NULL
                  FROM INFORMATION_SCHEMA.COLUMNS
                  WHERE table_name = 'USER' AND column_name = 'mailing_subscription')
    THEN
      ALTER TABLE USER ADD COLUMN mailing_subscription TINYINT(1) DEFAULT TRUE AFTER pub_id;
    END IF;

  END;;
DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;