DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
  BEGIN
    IF NOT EXISTS(SELECT NULL
                  FROM INFORMATION_SCHEMA.COLUMNS
                  WHERE table_name = 'CURRENCY_PAIR' AND column_name = 'top_market')
    THEN

      ALTER TABLE CURRENCY_PAIR
        ADD COLUMN top_market TINYINT(1) NOT NULL DEFAULT '1';
    END IF;

    IF NOT EXISTS(SELECT NULL
                  FROM INFORMATION_SCHEMA.COLUMNS
                  WHERE table_name = 'CURRENCY_PAIR' AND column_name = 'top_market_volume')
    THEN

      ALTER TABLE CURRENCY_PAIR
        ADD COLUMN top_market_volume DOUBLE (40, 9) NULL;
    END IF;

  END;;
DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;
