INSERT IGNORE INTO `MERCHANT` (`description`, `name`, `transaction_source_type_id`, `service_bean_name`, `process_type`)
VALUES ('Enfins', 'Enfins', 2, 'enfinsMerchantServiceImpl', 'MERCHANT');

INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, merchant_input_commission,
                                      merchant_output_commission, withdraw_auto_enabled, withdraw_auto_delay_seconds)
VALUES ((SELECT id from MERCHANT WHERE name = 'Enfins'),
        (SELECT id from CURRENCY WHERE name = 'UAH'),
        10, 2.2, 1.8, 1, 60),
        ((SELECT id from MERCHANT WHERE name = 'Enfins'),
        (SELECT id from CURRENCY WHERE name = 'RUB'),
        10, 5, 2.7, 1, 60);

INSERT IGNORE INTO `MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`)
VALUES ((SELECT id from MERCHANT WHERE name = 'Enfins'), '/client/img/merchants/enfins.png',
        'Enfins(UAH)', (SELECT id from CURRENCY WHERE name = 'UAH')),
        ((SELECT id from MERCHANT WHERE name = 'Enfins'), '/client/img/merchants/enfins.png',
        'Enfins(RUB)', (SELECT id from CURRENCY WHERE name = 'RUB'));
