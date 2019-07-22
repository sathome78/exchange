DELIMITER $$
DROP PROCEDURE IF EXISTS add_currency $$
CREATE PROCEDURE add_currency(currencyName VARCHAR(255), curDescription VARCHAR(255), beanName VARCHAR(255), imgPath VARCHAR(255), hidde boolean, lockInOut boolean)
  BEGIN
    INSERT INTO `MERCHANT` (`description`, `name`, `transaction_source_type_id`, `service_bean_name`, `process_type`)
    VALUES (curDescription, currencyName, 2, beanName, 'CRYPTO');

    INSERT INTO `CURRENCY` (`name`, `description`, `hidden`, `max_scale_for_refill`, `max_scale_for_withdraw`, `max_scale_for_transfer`)
    VALUES (currencyName, curDescription, hidde, 8, 8, 8);

    INSERT INTO COMPANY_WALLET_EXTERNAL(currency_id) VALUES ((SELECT id from CURRENCY WHERE name = currencyName));

    INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, refill_block, withdraw_block)
    VALUES ((SELECT id from MERCHANT WHERE name = currencyName),
    (SELECT id from CURRENCY WHERE name = currencyName), 0.00000001, lockInOut, lockInOut);

     INSERT INTO `MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name = currencyName),
     imgPath, currencyName, (SELECT id from CURRENCY WHERE name = currencyName));

    INSERT INTO WALLET (user_id, currency_id) select id, (select id from CURRENCY where name = currencyName) from USER;

    INSERT INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum)
      SELECT (select id from CURRENCY where name = currencyName), operation_type_id, user_role_id, min_sum, max_sum
      FROM CURRENCY_LIMIT WHERE currency_id = (select id from CURRENCY where name = 'EDC');

    INSERT INTO `COMPANY_WALLET` (`currency_id`) VALUES ((select id from CURRENCY where name = currencyName));

    INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, withdraw_block, refill_block, transfer_block)
    VALUES ((SELECT id FROM MERCHANT WHERE name = 'SimpleTransfer'), (select id from CURRENCY where name = currencyName), 0.000001, 1, 1, lockInOut);

    INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, withdraw_block, refill_block, transfer_block)
    VALUES ((SELECT id FROM MERCHANT WHERE name = 'VoucherTransfer'), (select id from CURRENCY where name = currencyName), 0.000001, 1, 1, lockInOut);

    INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, withdraw_block, refill_block, transfer_block)
    VALUES ((SELECT id FROM MERCHANT WHERE name = 'VoucherFreeTransfer'), (select id from CURRENCY where name = currencyName), 0.000001, 1, 1, lockInOut);

    INSERT INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES
    ((SELECT id FROM MERCHANT WHERE name = 'SimpleTransfer'), '/client/img/merchants/transfer.png', 'Transfer', (select id from CURRENCY where name = currencyName));

    INSERT INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES
    ((SELECT id FROM MERCHANT WHERE name = 'VoucherTransfer'), '/client/img/merchants/voucher.png', 'Voucher', (select id from CURRENCY where name = currencyName));

    INSERT INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES
    ((SELECT id FROM MERCHANT WHERE name = 'VoucherFreeTransfer'), '/client/img/merchants/voucher_free.png', 'Free voucher', (select id from CURRENCY where name = currencyName));

    INSERT INTO INTERNAL_WALLET_BALANCES (currency_id, role_id)
    SELECT cur.id AS currency_id, ur.id AS role_id
    FROM CURRENCY cur CROSS JOIN USER_ROLE ur
    WHERE cur.name IN (currencyName)
    ORDER BY cur.id, ur.id;

    INSERT INTO COMPANY_EXTERNAL_WALLET_BALANCES (currency_id) SELECT cur.id
    FROM CURRENCY cur
    WHERE cur.name IN (currencyName);
  END $$
DELIMITER ;