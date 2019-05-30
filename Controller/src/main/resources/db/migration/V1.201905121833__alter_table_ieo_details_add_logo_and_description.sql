DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
  BEGIN
    /* delete columns if they exist */
    IF EXISTS (SELECT *
               FROM INFORMATION_SCHEMA.COLUMNS
               WHERE table_name = 'IEO_DETAILS' AND column_name = 'description') THEN

      ALTER TABLE IEO_DETAILS DROP COLUMN `description`;
    END IF;

    /* add columns */
    ALTER TABLE IEO_DETAILS ADD COLUMN description TEXT;


    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'IEO_DETAILS' AND column_name = 'description')  THEN

      ALTER TABLE IEO_DETAILS ADD COLUMN description TEXT;
    END IF;

  END;;

DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;

DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
  BEGIN
    /* delete columns if they exist */
    IF EXISTS (SELECT *
               FROM INFORMATION_SCHEMA.COLUMNS
               WHERE table_name = 'IEO_DETAILS' AND column_name = 'logo') THEN

      ALTER TABLE IEO_DETAILS DROP COLUMN `logo`;
    END IF;

    /* add columns */
    ALTER TABLE IEO_DETAILS ADD COLUMN logo VARCHAR(255);


    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'IEO_DETAILS' AND column_name = 'logo')  THEN

      ALTER TABLE IEO_DETAILS ADD COLUMN logo VARCHAR(255);
    END IF;

  END;;

DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;