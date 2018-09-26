INSERT INTO COMPANY_WALLET_EXTERNAL(currency_id) VALUES ((SELECT id from CURRENCY WHERE name='SBTC'));

INSERT INTO CRYPTO_CORE_WALLET(merchant_id, currency_id, CRYPTO_CORE_WALLET.title_code, passphrase)
VALUES ((SELECT id from MERCHANT WHERE name='SBTC'), (select id from CURRENCY where name='SBTC'), 'sbtcWallet.title', 'pass123');


INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'SBTC'), (select id from CURRENCY where name = 'ETH'), 'SBTC/ETH', 160, 0, 'ETH', 'SBTC/ETH');

INSERT INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
                                                  JOIN USER_ROLE UR
                                                  JOIN ORDER_TYPE OT where CP.name='SBTC/ETH';

INSERT INTO BOT_LAUNCH_SETTINGS(bot_trader_id, currency_pair_id)
SELECT BT.id, CP.id FROM BOT_TRADER BT
                           JOIN CURRENCY_PAIR CP WHERE CP.name IN ('SBTC/ETH');

INSERT INTO BOT_TRADING_SETTINGS(bot_launch_settings_id, order_type_id)
SELECT BLCH.id, OT.id FROM BOT_LAUNCH_SETTINGS BLCH
                             JOIN ORDER_TYPE OT
WHERE BLCH.currency_pair_id IN (SELECT id FROM CURRENCY_PAIR WHERE name IN ('SBTC/ETH'));