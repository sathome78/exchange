DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE `Alter_Table`()
BEGIN

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'USER'
                     AND column_name = 'GA')  THEN

        ALTER TABLE USER ADD COLUMN GA varchar(100) DEFAULT '' NULL;

    END IF;

END ;;
DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE `Alter_Table`;
