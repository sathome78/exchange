
UPDATE CURRENCY_PAIR SET hidden = 0 WHERE name = 'B2X/USD';

INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'B2X'), (select id from CURRENCY where name = 'ETH'), 'B2X/ETH', 50, 0, 'FIAT', 'B2X/ETH');

INSERT INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='B2X/ETH';

INSERT INTO BOT_LAUNCH_SETTINGS(bot_trader_id, currency_pair_id)
  SELECT BT.id, CP.id FROM BOT_TRADER BT
    JOIN CURRENCY_PAIR CP WHERE CP.name IN ('B2X/ETH');

INSERT INTO BOT_TRADING_SETTINGS(bot_launch_settings_id, order_type_id)
  SELECT BLCH.id, OT.id FROM BOT_LAUNCH_SETTINGS BLCH
    JOIN ORDER_TYPE OT
  WHERE BLCH.currency_pair_id IN (SELECT id FROM CURRENCY_PAIR WHERE name IN ('B2X/ETH'));

ALTER TABLE CURRENCY_PAIR MODIFY market ENUM('USD', 'BTC', 'ETH', 'FIAT') NOT NULL DEFAULT 'USD';
UPDATE CURRENCY_PAIR set market = 'ETH' where currency2_id = 14;