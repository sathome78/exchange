DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
  BEGIN
    /* delete columns if they exist */
    IF EXISTS (SELECT *
               FROM INFORMATION_SCHEMA.COLUMNS
               WHERE table_name = 'API_AUTH_TOKEN' AND column_name = 'last_request') THEN

      ALTER TABLE API_AUTH_TOKEN DROP COLUMN last_request;
    END IF;
  END;;

DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;