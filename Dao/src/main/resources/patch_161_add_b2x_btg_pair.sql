
INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, ticker_name, market, hidden)
VALUES((select id from CURRENCY where name = 'B2X'), (select id from CURRENCY where name = 'BTG'), 'B2X/BTG', 20, 'B2X/BTG', 'BTC', 0);

INSERT INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='B2X/BTG';



INSERT INTO BOT_LAUNCH_SETTINGS(bot_trader_id, currency_pair_id)
  SELECT BT.id, CP.id FROM BOT_TRADER BT
    JOIN CURRENCY_PAIR CP WHERE CP.name IN ('B2X/BTG');

INSERT INTO BOT_TRADING_SETTINGS(bot_launch_settings_id, order_type_id)
  SELECT BLCH.id, OT.id FROM BOT_LAUNCH_SETTINGS BLCH
    JOIN ORDER_TYPE OT
  WHERE BLCH.currency_pair_id IN (SELECT id FROM CURRENCY_PAIR WHERE name IN ('B2X/BTG'));

