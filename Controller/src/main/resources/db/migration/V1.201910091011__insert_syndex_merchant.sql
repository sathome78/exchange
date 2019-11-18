INSERT IGNORE INTO `MERCHANT` (`description`, `name`, `transaction_source_type_id`, `service_bean_name`, `process_type`)
VALUES ('Syndex', 'Syndex', 2, 'syndexServiceImpl', 'MERCHANT');


INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, merchant_input_commission,
                                      merchant_output_commission)
VALUES ((SELECT id from MERCHANT WHERE name = 'Syndex'),
        (SELECT id from CURRENCY WHERE name = 'USD'), 10, 0, 0);

INSERT IGNORE INTO `MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`)
VALUES ((SELECT id from MERCHANT WHERE name = 'Syndex'), '/client/img/merchants/syndex.png',
        'Syndex', (SELECT id from CURRENCY WHERE name = 'USD'));
