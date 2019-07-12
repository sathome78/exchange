ALTER TABLE QUBERA_USER_DETAILS
  MODIFY account_number VARCHAR(50) NULL;
ALTER TABLE QUBERA_USER_DETAILS
  MODIFY iban VARCHAR(100) NULL;

DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
  BEGIN
    IF NOT EXISTS(SELECT NULL
                  FROM INFORMATION_SCHEMA.COLUMNS
                  WHERE table_name = 'QUBERA_USER_DETAILS' AND column_name = 'bank_verification_status')
    THEN

      ALTER TABLE QUBERA_USER_DETAILS
        ADD COLUMN bank_verification_status VARCHAR(64) NOT NULL DEFAULT 'none';
    END IF;

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'QUBERA_USER_DETAILS' AND column_name = 'reference')  THEN

      ALTER TABLE QUBERA_USER_DETAILS ADD COLUMN reference VARCHAR(255) NULL;
    END IF;

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'QUBERA_USER_DETAILS' AND column_name = 'birth_day')  THEN

      ALTER TABLE QUBERA_USER_DETAILS ADD COLUMN birth_day timestamp NULL;
    END IF;

  END;;
DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;
