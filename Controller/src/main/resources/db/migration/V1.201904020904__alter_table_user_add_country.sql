DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
  BEGIN

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'USER' AND column_name = 'country')  THEN

      ALTER TABLE USER ADD COLUMN country VARCHAR(255);
    END IF;

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'USER' AND column_name = 'firstName')  THEN

      ALTER TABLE USER ADD COLUMN firstName VARCHAR(255);
    END IF;

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'USER' AND column_name = 'lastName')  THEN

      ALTER TABLE USER ADD COLUMN lastName VARCHAR(255);
    END IF;

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'USER' AND column_name = 'birthDay')  THEN

      ALTER TABLE USER ADD COLUMN birthDay VARCHAR(255);
    END IF;


  END;;

DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;