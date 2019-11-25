DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
BEGIN
    IF EXISTS(SELECT NULL
                         FROM INFORMATION_SCHEMA.COLUMNS
                         WHERE table_name = 'MERCHANT_CURRENCY' AND column_name = 'merchant_commission_type')
    THEN
        ALTER TABLE MERCHANT_CURRENCY
        CHANGE merchant_commission_type withdraw_merchant_commission_type enum('MAIN_CURRENCY', 'SECONDARY_CURRENCY') NOT NULL DEFAULT 'MAIN_CURRENCY';
    END IF;
END;;
DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;