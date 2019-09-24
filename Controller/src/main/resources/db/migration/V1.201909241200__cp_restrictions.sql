DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
BEGIN


    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'USER' AND column_name = 'trades_restriction')  THEN

        ALTER TABLE USER ADD COLUMN trades_restriction tinyint(1) not null default false;
    END IF;

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'USER' AND column_name = 'trades_manually_allowed')  THEN

        ALTER TABLE USER ADD COLUMN trades_manually_allowed tinyint(1) not null default false;
    END IF;

END;;

DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;

