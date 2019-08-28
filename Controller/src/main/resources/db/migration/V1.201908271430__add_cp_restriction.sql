DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
BEGIN


    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'CURRENCY_PAIR' AND column_name = 'trade_restriction')  THEN

        ALTER TABLE CURRENCY_PAIR ADD COLUMN trade_restriction tinyint(1) not null default false;
    END IF;

END;;

DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;

INSERT IGNORE INTO USER_OPERATION (id, name)
values (5 ,'TRADING_RESTRICTION');

INSERT IGNORE INTO USER_OPERATION_AUTHORITY (user_id, user_operation_id, enabled)
    SELECT id, (SELECT id from USER_OPERATION where name = 'TRADING_RESTRICTION'), false from USER;



