ALTER TABLE CURRENCY_PAIR
    ADD COLUMN trade_restriction tinyint(1) not null default false;

INSERT INTO USER_OPERATION (name)
    value ('TRADING_RESTRICTION');

INSERT INTO USER_OPERATION_AUTHORITY
    (SELECT id, (SELECT id from USER_OPERATION where name = 'TRADING_RESTRICTION'), false from USER);



