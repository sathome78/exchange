DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
BEGIN
    IF NOT EXISTS(SELECT NULL
                  FROM INFORMATION_SCHEMA.COLUMNS
                  WHERE table_name = 'EXORDERS' AND column_name = 'counter_order_type')
    THEN

        ALTER TABLE EXORDERS
            ADD COLUMN counter_order_type enum ('LIMIT', 'ICO', 'MARKET') DEFAULT 'LIMIT' not null AFTER counter_order_id;
    END IF;

END;;
DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;
