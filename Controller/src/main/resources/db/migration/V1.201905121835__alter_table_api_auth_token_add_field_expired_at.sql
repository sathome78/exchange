DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
  BEGIN
    /* add columns if they not exist */
    IF NOT EXISTS (SELECT *
               FROM INFORMATION_SCHEMA.COLUMNS
               WHERE table_name = 'API_AUTH_TOKEN' AND column_name = 'expired_at') THEN

      ALTER TABLE API_AUTH_TOKEN ADD COLUMN expired_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
    END IF;
  END;;

DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;