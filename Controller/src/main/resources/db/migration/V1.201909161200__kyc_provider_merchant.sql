ALTER TABLE MERCHANT
    MODIFY type_verification ENUM('none', 'shuftipro', 'ariadnext', 'shuftipro_and_ariadnext') not null default 'none';

DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
BEGIN

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'MERCHANT' AND column_name = 'kyc_refill')  THEN

        ALTER TABLE MERCHANT
            ADD COLUMN kyc_refill tinyint(1) default false;

    END IF;

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'MERCHANT' AND column_name = 'kyc_withdraw')  THEN

        ALTER TABLE MERCHANT
            ADD COLUMN kyc_withdraw tinyint(1) default false;

    END IF;

END;;

DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;
