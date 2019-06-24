DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
  BEGIN
    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'IEO_DETAILS' AND column_name = 'sold_out_at')  THEN

      ALTER TABLE IEO_DETAILS ADD COLUMN sold_out_at timestamp NULL;
    END IF;

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'IEO_DETAILS' AND column_name = 'test_ieo')  THEN

      ALTER TABLE IEO_DETAILS ADD COLUMN test_ieo TINYINT(1) NULL;
    END IF;

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'IEO_DETAILS' AND column_name = 'count_test_transaction')  THEN

      ALTER TABLE IEO_DETAILS ADD COLUMN count_test_transaction int(11) DEFAULT 20 NULL;
    END IF;

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'IEO_DETAILS' AND column_name = 'content')  THEN

      ALTER TABLE IEO_DETAILS ADD COLUMN content TEXT NULL;
    END IF;

  END;;

DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;


INSERT IGNORE INTO USER (id, email) VALUES (-2, 'test_ieo@gmail.com');

