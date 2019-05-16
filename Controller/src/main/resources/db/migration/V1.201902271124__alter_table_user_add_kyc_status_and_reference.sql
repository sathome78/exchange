DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
  BEGIN
    /* delete columns if they exist */
    IF EXISTS (SELECT *
               FROM INFORMATION_SCHEMA.COLUMNS
               WHERE table_name = 'USER' AND column_name = 'kyc_reference') THEN

      ALTER TABLE USER DROP COLUMN `kyc_reference`;
    END IF;

    /* add columns */
    ALTER TABLE USER ADD COLUMN kyc_reference VARCHAR(55) NOT NULL DEFAULT 'none';


    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'USER' AND column_name = 'kyc_status')  THEN

      ALTER TABLE USER ADD COLUMN kyc_status VARCHAR(55) NOT NULL DEFAULT 'none';
    END IF;

  END ;;

DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;
