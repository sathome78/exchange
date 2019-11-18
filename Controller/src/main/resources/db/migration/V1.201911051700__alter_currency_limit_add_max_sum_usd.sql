DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
BEGIN
    IF NOT EXISTS(SELECT NULL
                  FROM INFORMATION_SCHEMA.COLUMNS
                  WHERE table_name = 'CURRENCY_LIMIT' AND column_name = 'max_sum_usd')
    THEN

        ALTER TABLE CURRENCY_LIMIT
            ADD COLUMN max_sum_usd double(40,9) default 0.00000 after max_sum;
    END IF;

END;;
DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;
