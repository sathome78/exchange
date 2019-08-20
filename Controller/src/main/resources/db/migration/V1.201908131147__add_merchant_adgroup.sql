INSERT IGNORE INTO `MERCHANT` (`description`, `name`, `transaction_source_type_id`, `service_bean_name`, `process_type`)
VALUES ('Adgroup.Yandex.Money(Wallet)', 'Adgroup_Wallet', 2, 'adgroupServiceImpl', 'MERCHANT');

INSERT IGNORE INTO `MERCHANT` (`description`, `name`, `transaction_source_type_id`, `service_bean_name`, `process_type`)
VALUES ('Adgroup.Yandex.Money(PaymentCard)', 'Adgroup_PaymentCard', 2, 'adgroupServiceImpl', 'MERCHANT');

INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, merchant_input_commission,
                                      merchant_output_commission)
VALUES ((SELECT id from MERCHANT WHERE name = 'Adgroup_Wallet'),
        (SELECT id from CURRENCY WHERE name = 'RUB'), 10, 1, 3),
      ((SELECT id from MERCHANT WHERE name = 'Adgroup_PaymentCard'),
        (SELECT id from CURRENCY WHERE name = 'RUB'), 10, 1, 3);

INSERT IGNORE INTO `MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`)
VALUES ((SELECT id from MERCHANT WHERE name = 'Adgroup_Wallet'), '/client/img/merchants/yandexmoney.png',
        'Yandex.Money(Wallet)', (SELECT id from CURRENCY WHERE name = 'RUB')),
       ((SELECT id from MERCHANT WHERE name = 'Adgroup_PaymentCard'), '/client/img/merchants/yandexmoney.png',
        'Yandex.Money(PaymentCard)', (SELECT id from CURRENCY WHERE name = 'RUB'));
