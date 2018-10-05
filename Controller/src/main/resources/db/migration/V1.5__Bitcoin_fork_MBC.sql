INSERT IGNORE INTO `MERCHANT` (`description`, `name`, `transaction_source_type_id`, `service_bean_name`, `process_type`)
VALUES ('MicroBitcoin', 'MBC', 2, 'mbcServiceImpl', 'CRYPTO');
INSERT IGNORE INTO `CURRENCY` (`name`, `description`, `hidden`, `max_scale_for_refill`, `max_scale_for_withdraw`, `max_scale_for_transfer`)
VALUES ('MBC', 'MicroBitcoin', 0, 8, 8, 8);

INSERT IGNORE INTO COMPANY_WALLET_EXTERNAL(currency_id) VALUES ((SELECT id from CURRENCY WHERE name='MBC'));


INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name='MBC'),
          (SELECT id from CURRENCY WHERE name='MBC'),
          0.0001);

INSERT IGNORE INTO `MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='MBC')
, '/client/img/merchants/MBC.png', 'MBC', (SELECT id from CURRENCY WHERE name='MBC'));

INSERT IGNORE INTO WALLET (user_id, currency_id) select id, (select id from CURRENCY where name='MBC') from USER;

INSERT IGNORE INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum)
  SELECT (select id from CURRENCY where name = 'MBC'), operation_type_id, user_role_id, min_sum, max_sum
  FROM CURRENCY_LIMIT WHERE currency_id = (select id from CURRENCY where name = 'EDR');

INSERT IGNORE INTO `COMPANY_WALLET` (`currency_id`) VALUES ((select id from CURRENCY where name = 'MBC'));

INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, ticker_name)
VALUES((select id from CURRENCY where name = 'MBC'), (select id from CURRENCY where name = 'USD'), 'MBC/USD', 170, 0, 'MBC/USD');

INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
  JOIN USER_ROLE UR
  JOIN ORDER_TYPE OT where CP.name='MBC/USD';

INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'MBC'), (select id from CURRENCY where name = 'BTC'), 'MBC/BTC', 160, 0, 'BTC', 'MBC/BTC');

INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='MBC/BTC';

INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'MBC'), (select id from CURRENCY where name = 'ETH'), 'MBC/ETH', 160, 0, 'ETH', 'MBC/ETH');

INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='MBC/ETH';

INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, withdraw_block, refill_block, transfer_block)
VALUES ((SELECT id FROM MERCHANT WHERE name = 'SimpleTransfer'), (select id from CURRENCY where name = 'MBC'), 0.0001, 1, 1, 0);

INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, withdraw_block, refill_block, transfer_block)
VALUES ((SELECT id FROM MERCHANT WHERE name = 'VoucherTransfer'), (select id from CURRENCY where name = 'MBC'), 0.0001, 1, 1, 0);

INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, withdraw_block, refill_block, transfer_block)
VALUES ((SELECT id FROM MERCHANT WHERE name = 'VoucherFreeTransfer'), (select id from CURRENCY where name = 'MBC'), 0.0001, 1, 1, 0);

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'SimpleTransfer'), '/client/img/merchants/transfer.png', 'Transfer', (select id from CURRENCY where name = 'MBC'));

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'VoucherTransfer'), '/client/img/merchants/voucher.png', 'Voucher', (select id from CURRENCY where name = 'MBC'));

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'VoucherFreeTransfer'), '/client/img/merchants/voucher_free.png', 'Free voucher', (select id from CURRENCY where name = 'MBC'));

INSERT IGNORE INTO BOT_LAUNCH_SETTINGS(bot_trader_id, currency_pair_id)
  SELECT BT.id, CP.id FROM BOT_TRADER BT
    JOIN CURRENCY_PAIR CP WHERE CP.name IN ('MBC/USD', 'MBC/BTC', 'MBC/ETH');

INSERT IGNORE INTO BOT_TRADING_SETTINGS(bot_launch_settings_id, order_type_id)
  SELECT BLCH.id, OT.id FROM BOT_LAUNCH_SETTINGS BLCH
    JOIN ORDER_TYPE OT
  WHERE BLCH.currency_pair_id IN (SELECT id FROM CURRENCY_PAIR WHERE name IN ('MBC/USD', 'MBC/BTC', 'MBC/ETH'));

INSERT IGNORE INTO CRYPTO_CORE_WALLET(merchant_id, currency_id, CRYPTO_CORE_WALLET.title_code, passphrase)
VALUES ((SELECT id from MERCHANT WHERE name='MBC'), (select id from CURRENCY where name='MBC'), 'mbcWallet.title', 'pass123');