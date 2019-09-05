INSERT IGNORE INTO `MERCHANT` (`description`, `name`, `transaction_source_type_id`, `service_bean_name`, `process_type`)
VALUES ('CoinPay', 'CoinPay', 2, 'coinPayMerchantServiceImpl', 'MERCHANT');

INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, merchant_input_commission,
                                      merchant_output_commission)
VALUES ((SELECT id from MERCHANT WHERE name = 'CoinPay'),
        (SELECT id from CURRENCY WHERE name = 'UAH'),
        10, 1, 3);

INSERT IGNORE INTO `MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`)
VALUES ((SELECT id from MERCHANT WHERE name = 'CoinPay'), '/client/img/merchants/yandexmoney.png',
        'CoinPay(Privat24)', (SELECT id from CURRENCY WHERE name = 'UAH'));
