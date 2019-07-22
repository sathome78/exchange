DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE `Alter_Table`()
BEGIN

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'OPEN_API_USER_TOKEN'
                     AND column_name = 'allow_accept_by_id')  THEN

        ALTER TABLE OPEN_API_USER_TOKEN ADD allow_accept_by_id tinyint(1) not null default false;

    END IF;

END ;;
DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE `Alter_Table`;
