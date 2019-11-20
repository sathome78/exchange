DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
BEGIN
    IF NOT EXISTS(SELECT NULL
                         FROM INFORMATION_SCHEMA.COLUMNS
                         WHERE table_name = 'MERCHANT_CURRENCY' AND column_name = 'withdraw_merchant_commission_type')
    THEN
        ALTER TABLE MERCHANT_CURRENCY
            ADD COLUMN merchant_commission_type ENUM('MAIN_CURRENCY', 'SECONDARY_CURRENCY') NOT NULL DEFAULT 'MAIN_CURRENCY';
    END IF;

    IF NOT EXISTS(SELECT NULL
                  FROM INFORMATION_SCHEMA.COLUMNS
                  WHERE table_name = 'MERCHANT_CURRENCY' AND column_name = 'merchant_secondary_commission_currency')
    THEN
        ALTER TABLE MERCHANT_CURRENCY
            ADD COLUMN merchant_secondary_commission_currency int(40) null;
    END IF;

    IF NOT EXISTS(SELECT NULL
                  FROM INFORMATION_SCHEMA.COLUMNS
                  WHERE table_name = 'MERCHANT_CURRENCY' AND column_name = 'merchant_secondary_commission_amount')
    THEN
        ALTER TABLE MERCHANT_CURRENCY
            ADD COLUMN merchant_secondary_commission_amount DECIMAL(40,9) DEFAULT 0;
    END IF;

    IF NOT EXISTS(SELECT NULL
                  FROM INFORMATION_SCHEMA.COLUMNS
                  WHERE table_name = 'CURRENCY' AND column_name = 'use_for_commission')
    THEN
        ALTER TABLE CURRENCY
            ADD COLUMN use_for_commission TINYINT DEFAULT 0;
    END IF;

    IF NOT EXISTS(SELECT NULL
                  FROM INFORMATION_SCHEMA.COLUMNS
                  WHERE table_name = 'WITHDRAW_REQUEST' AND column_name = 'merchant_commission_currency')
    THEN
        ALTER TABLE WITHDRAW_REQUEST
            ADD COLUMN merchant_commission_currency int(40) DEFAULT null;
    END IF;

    IF NOT EXISTS (
            SELECT NULL
            FROM information_schema.TABLE_CONSTRAINTS
            WHERE
                    CONSTRAINT_SCHEMA = DATABASE() AND
                    CONSTRAINT_NAME   = 'fk_withdraw_request_merchant_commission_currency' AND
                    CONSTRAINT_TYPE   = 'FOREIGN KEY')
    THEN
        ALTER TABLE WITHDRAW_REQUEST
            ADD CONSTRAINT fk_withdraw_request_merchant_commission_currency FOREIGN KEY (merchant_commission_currency)
                REFERENCES CURRENCY(id);
    END IF;

    IF NOT EXISTS (
                   SELECT NULL
                   FROM information_schema.TABLE_CONSTRAINTS
                   WHERE
                           CONSTRAINT_SCHEMA = DATABASE() AND
                           CONSTRAINT_NAME   = 'fk_merchant_commission_currency' AND
                           CONSTRAINT_TYPE   = 'FOREIGN KEY')
    THEN
        ALTER TABLE MERCHANT_CURRENCY
            ADD CONSTRAINT fk_merchant_commission_currency FOREIGN KEY (merchant_secondary_commission_currency)
                REFERENCES CURRENCY(id);
    END IF;

END;;
DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;



