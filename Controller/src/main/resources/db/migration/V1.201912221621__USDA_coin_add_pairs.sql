INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market, ticker_name, scale)
VALUES((select id from CURRENCY where name = 'USDA'), (select id from CURRENCY where name = 'USD'), 'USDA/USD', 170, 0, 'USD', 'USDA/USD', 2);

INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='USDA/USD';

INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market, ticker_name, scale)
VALUES((select id from CURRENCY where name = 'USDA'), (select id from CURRENCY where name = 'EUR'), 'USDA/EUR', 170, 0, 'FIAT', 'USDA/EUR', 2);

INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='USDA/EUR';

INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market, ticker_name, scale)
VALUES((select id from CURRENCY where name = 'USDA'), (select id from CURRENCY where name = 'RUB'), 'USDA/RUB', 170, 0, 'FIAT', 'USDA/RUB', 2);

INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='USDA/RUB';

INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market, ticker_name, scale)
VALUES((select id from CURRENCY where name = 'USDA'), (select id from CURRENCY where name = 'UAH'), 'USDA/UAH', 170, 0, 'FIAT', 'USDA/UAH', 2);

INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='USDA/UAH';

INSERT IGNORE INTO BOT_LAUNCH_SETTINGS(bot_trader_id, currency_pair_id)
  SELECT BT.id, CP.id FROM BOT_TRADER BT
    JOIN CURRENCY_PAIR CP WHERE CP.name IN ('USDA/USD', 'USDA/EUR', 'USDA/RUB', 'USDA/UAH');

INSERT IGNORE INTO BOT_TRADING_SETTINGS(bot_launch_settings_id, order_type_id)
  SELECT BLCH.id, OT.id FROM BOT_LAUNCH_SETTINGS BLCH
    JOIN ORDER_TYPE OT
  WHERE BLCH.currency_pair_id IN (SELECT id FROM CURRENCY_PAIR WHERE name IN ('USDA/USD', 'USDA/EUR', 'USDA/RUB', 'USDA/UAH'));