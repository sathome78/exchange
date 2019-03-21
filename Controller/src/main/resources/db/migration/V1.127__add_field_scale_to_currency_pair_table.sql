DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ';;'
CREATE PROCEDURE Alter_Table()
  BEGIN
    /* delete columns if they exist */
    IF EXISTS (SELECT *
                FROM information_schema.columns
                WHERE table_name = 'CURRENCY_PAIR' AND column_name = 'scale') THEN

      ALTER TABLE CURRENCY_PAIR DROP COLUMN `scale`;
    END IF;

    /* add columns */
    ALTER TABLE CURRENCY_PAIR ADD COLUMN scale INT(11) NULL;

  END;;

DELIMITER ';'

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;

UPDATE CURRENCY_PAIR SET scale = 2 WHERE market = 'FIAT';
UPDATE CURRENCY_PAIR SET scale = 2 WHERE market = 'USD';
UPDATE CURRENCY_PAIR SET scale = 8 WHERE market = 'BTC';
UPDATE CURRENCY_PAIR SET scale = 8 WHERE market = 'ETH';