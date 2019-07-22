INSERT IGNORE INTO `MERCHANT` (`description`, `name`, `transaction_source_type_id`, `service_bean_name`, `process_type`, `tokens_parrent_id`)
VALUES ('Tether USD ERC-20', 'USDT', 2, 'ethereumServiceImpl', 'CRYPTO', 16);
INSERT IGNORE INTO `CURRENCY` (`name`, `description`, `hidden`, `max_scale_for_refill`, `max_scale_for_withdraw`, `max_scale_for_transfer`)
VALUES ('USDT', 'Tether USD ERC-20', 0, 8, 8, 8);

INSERT IGNORE INTO COMPANY_WALLET_EXTERNAL(currency_id) VALUES ((SELECT id from CURRENCY WHERE name='USDT'));


INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, refill_block, withdraw_block)
  VALUES ((SELECT id from MERCHANT WHERE name='USDT'),
          (SELECT id from CURRENCY WHERE name='USDT'),
          0.00000001, TRUE, TRUE);

INSERT IGNORE INTO `MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='USDT')
, '/client/img/merchants/USDT.png', 'USDT', (SELECT id from CURRENCY WHERE name='USDT'));

INSERT IGNORE INTO WALLET (user_id, currency_id) select id, (select id from CURRENCY where name='USDT') from USER;

INSERT IGNORE INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum)
  SELECT (select id from CURRENCY where name = 'USDT'), operation_type_id, user_role_id, min_sum, max_sum
  FROM CURRENCY_LIMIT WHERE currency_id = (select id from CURRENCY where name = 'EDC');

INSERT IGNORE INTO `COMPANY_WALLET` (`currency_id`) VALUES ((select id from CURRENCY where name = 'USDT'));

#BTC currency pair
INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'BTC'), (select id from CURRENCY where name = 'USDT'), 'BTC/USDT', 160, 0, 'BTC', 'BTC/USDT');
INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='BTC/USDT';

#ETH currency pair
INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'ETH'), (select id from CURRENCY where name = 'USDT'), 'ETH/USDT', 160, 0, 'ETH', 'ETH/USDT');
INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='ETH/USDT';

#LTC currency pair
INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'LTC'), (select id from CURRENCY where name = 'USDT'), 'LTC/USDT', 160, 0, 'USDT', 'LTC/USDT');
INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='LTC/USDT';

#ETC currency pair
INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'ETC'), (select id from CURRENCY where name = 'USDT'), 'ETC/USDT', 160, 0, 'USDT', 'ETC/USDT');
INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='ETC/USDT';

#DASH currency pair
INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'DASH'), (select id from CURRENCY where name = 'USDT'), 'DASH/USDT', 160, 0, 'USDT', 'DASH/USDT');
INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='DASH/USDT';

#XRP currency pair
INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'XRP'), (select id from CURRENCY where name = 'USDT'), 'XRP/USDT', 160, 0, 'USDT', 'XRP/USDT');
INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='XRP/USDT';

#XLM currency pair
INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'XLM'), (select id from CURRENCY where name = 'XLM'), 'XLM/USDT', 160, 0, 'USDT', 'XLM/USDT');
INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='XLM/USDT';

#XEM currency pair
INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'XEM'), (select id from CURRENCY where name = 'USDT'), 'XEM/USDT', 160, 0, 'USDT', 'XEM/USDT');
INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='XEM/USDT';

#ATB currency pair
INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'ATB'), (select id from CURRENCY where name = 'USDT'), 'ATB/USDT', 160, 0, 'USDT', 'ATB/USDT');
INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='ATB/USDT';

#EDC currency pair
INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'EDC'), (select id from CURRENCY where name = 'USDT'), 'EDC/USDT', 160, 0, 'USDT', 'EDC/USDT');
INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='EDC/USDT';

#IOTA currency pair
INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'IOTA'), (select id from CURRENCY where name = 'USDT'), 'IOTA/USDT', 160, 0, 'USDT', 'IOTA/USDT');
INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='IOTA/USDT';

#DOGE currency pair
INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'DOGE'), (select id from CURRENCY where name = 'USDT'), 'DOGE/USDT', 160, 0, 'USDT', 'DOGE/USDT');
INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='DOGE/USDT';

#LSK currency pair
INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'LSK'), (select id from CURRENCY where name = 'USDT'), 'LSK/USDT', 160, 0, 'USDT', 'LSK/USDT');
INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='LSK/USDT';

#XMR currency pair
INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'XMR'), (select id from CURRENCY where name = 'USDT'), 'XMR/USDT', 160, 0, 'USDT', 'XMR/USDT');
INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='XMR/USDT';

#NEO currency pair
INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'NEO'), (select id from CURRENCY where name = 'USDT'), 'NEO/USDT', 160, 0, 'USDT', 'NEO/USDT');
INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='NEO/USDT';

#GAS currency pair
INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'GAS'), (select id from CURRENCY where name = 'USDT'), 'GAS/USDT', 160, 0, 'USDT', 'GAS/USDT');
INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='GAS/USDT';

#ZEC currency pair
INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'ZEC'), (select id from CURRENCY where name = 'USDT'), 'ZEC/USDT', 160, 0, 'USDT', 'ZEC/USDT');
INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='ZEC/USDT';

#WAVES currency pair
INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'WAVES'), (select id from CURRENCY where name = 'USDT'), 'WAVES/USDT', 160, 0, 'USDT', 'WAVES/USDT');
INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='WAVES/USDT';

#BNB currency pair
INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'BNB'), (select id from CURRENCY where name = 'USDT'), 'BNB/USDT', 160, 0, 'USDT', 'BNB/USDT');
INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='BNB/USDT';

#CRON currency pair
INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'CRON'), (select id from CURRENCY where name = 'USDT'), 'CRON/USDT', 160, 0, 'USDT', 'CRON/USDT');
INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='CRON/USDT';

#VRBS currency pair
INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'VRBS'), (select id from CURRENCY where name = 'USDT'), 'VRBS/USDT', 160, 0, 'USDT', 'VRBS/USDT');
INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='VRBS/USDT';

INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, withdraw_block, refill_block, transfer_block)
VALUES ((SELECT id FROM MERCHANT WHERE name = 'SimpleTransfer'), (select id from CURRENCY where name = 'USDT'), 0.000001, 1, 1, 0);

INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, withdraw_block, refill_block, transfer_block)
VALUES ((SELECT id FROM MERCHANT WHERE name = 'VoucherTransfer'), (select id from CURRENCY where name = 'USDT'), 0.000001, 1, 1, 0);

INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, withdraw_block, refill_block, transfer_block)
VALUES ((SELECT id FROM MERCHANT WHERE name = 'VoucherFreeTransfer'), (select id from CURRENCY where name = 'USDT'), 0.000001, 1, 1, 0);

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'SimpleTransfer'), '/client/img/merchants/transfer.png', 'Transfer', (select id from CURRENCY where name = 'USDT'));

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'VoucherTransfer'), '/client/img/merchants/voucher.png', 'Voucher', (select id from CURRENCY where name = 'USDT'));

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'VoucherFreeTransfer'), '/client/img/merchants/voucher_free.png', 'Free voucher', (select id from CURRENCY where name = 'USDT'));

INSERT IGNORE INTO BOT_LAUNCH_SETTINGS(bot_trader_id, currency_pair_id)
  SELECT BT.id, CP.id FROM BOT_TRADER BT
    JOIN CURRENCY_PAIR CP WHERE CP.name IN ('USDT/USD', 'USDT/BTC', 'USDT/ETH');

INSERT IGNORE INTO BOT_TRADING_SETTINGS(bot_launch_settings_id, order_type_id)
  SELECT BLCH.id, OT.id FROM BOT_LAUNCH_SETTINGS BLCH
    JOIN ORDER_TYPE OT
  WHERE BLCH.currency_pair_id IN (SELECT id FROM CURRENCY_PAIR WHERE name IN ('USDT/USD', 'USDT/BTC', 'USDT/ETH'));

INSERT IGNORE INTO INTERNAL_WALLET_BALANCES (currency_id, role_id)
SELECT cur.id AS currency_id, ur.id AS role_id
FROM CURRENCY cur CROSS JOIN USER_ROLE ur
WHERE cur.name IN ('USDT')
ORDER BY cur.id, ur.id;

INSERT IGNORE INTO COMPANY_EXTERNAL_WALLET_BALANCES (currency_id)
SELECT cur.id
FROM CURRENCY cur
WHERE cur.name IN ('USDT');

INSERT IGNORE INTO CURRENT_CURRENCY_RATES (currency_id, currency_name)
SELECT cur.id, cur.name
FROM CURRENCY cur
WHERE cur.name = 'USDT';

INSERT IGNORE INTO CURRENT_CURRENCY_BALANCES (currency_id, currency_name)
SELECT cur.id, cur.name
FROM CURRENCY cur
WHERE cur.name = 'USDT';