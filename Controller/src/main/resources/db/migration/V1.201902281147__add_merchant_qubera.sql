INSERT IGNORE INTO `MERCHANT` (`description`, `name`, `transaction_source_type_id`, `service_bean_name`, `process_type`)
VALUES ('Qubera', 'Qubera', 2, 'quberaServiceImpl', 'MERCHANT');

INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
 VALUES ((SELECT id from MERCHANT WHERE name='Qubera'),
         (SELECT id from CURRENCY WHERE name='EUR'),
         10);

INSERT IGNORE INTO `MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Qubera')
, '/client/img/merchants/qiwi.png', 'Qubera', (SELECT id from CURRENCY WHERE name='EUR'));
