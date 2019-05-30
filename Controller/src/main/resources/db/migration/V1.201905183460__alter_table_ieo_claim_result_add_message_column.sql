DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
  BEGIN
    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'IEO_RESULT' AND column_name = 'message')  THEN

      ALTER TABLE IEO_RESULT ADD COLUMN message VARCHAR(255) NULL;
    END IF;

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'IEO_CLAIM' AND column_name = 'uuid')  THEN

      ALTER TABLE IEO_CLAIM ADD COLUMN uuid VARCHAR(64) NOT NULL;
    END IF;

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'IEO_CLAIM' AND column_name = 'test')  THEN

      ALTER TABLE IEO_CLAIM ADD COLUMN test TINYINT(1) NOT NULL;
    END IF;

  END;;

DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;
