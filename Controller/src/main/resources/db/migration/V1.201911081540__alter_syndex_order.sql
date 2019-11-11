DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
BEGIN
    IF NOT EXISTS(SELECT NULL
                  FROM INFORMATION_SCHEMA.COLUMNS
                  WHERE table_name = 'SYNDEX_ORDER' AND column_name = 'amount_to_refill')
    THEN
        ALTER TABLE SYNDEX_ORDER
            ADD COLUMN amount_to_refill double(40,8) null;
    END IF;

END;;
DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;



