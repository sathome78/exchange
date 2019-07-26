DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
  BEGIN
    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'QUBERA_USER_DETAILS' AND column_name = 'address')  THEN

      ALTER TABLE QUBERA_USER_DETAILS ADD COLUMN address VARCHAR(255) NULL;
    END IF;

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'QUBERA_USER_DETAILS' AND column_name = 'city')  THEN

      ALTER TABLE QUBERA_USER_DETAILS ADD COLUMN city VARCHAR(255) NULL;
    END IF;

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'QUBERA_USER_DETAILS' AND column_name = 'first_name')  THEN

      ALTER TABLE QUBERA_USER_DETAILS ADD COLUMN first_name VARCHAR(255) NULL;
    END IF;

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'QUBERA_USER_DETAILS' AND column_name = 'last_name')  THEN

      ALTER TABLE QUBERA_USER_DETAILS ADD COLUMN last_name VARCHAR(255) NULL;
    END IF;

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'QUBERA_USER_DETAILS' AND column_name = 'country_code')  THEN

      ALTER TABLE QUBERA_USER_DETAILS ADD COLUMN country_code VARCHAR(8) NULL;
    END IF;

  END;;

DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;


INSERT IGNORE INTO USER (id, email) VALUES (-2, 'test_ieo@gmail.com');