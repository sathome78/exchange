INSERT IGNORE INTO `MERCHANT` (`description`, `name`, `transaction_source_type_id`, `service_bean_name`, `process_type`)
VALUES ('QIWI', 'QIWI', 2, 'qiwiServiceImpl', 'CRYPTO');

INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name='QIWI'),
          (SELECT id from CURRENCY WHERE name='RUB'),
          1.00);

INSERT IGNORE INTO `MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='QIWI')
, '/client/img/merchants/qiwi.png', 'QIWI', (SELECT id from CURRENCY WHERE name='RUB'));