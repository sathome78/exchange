INSERT IGNORE INTO `MERCHANT` (`description`, `name`, `transaction_source_type_id`, `service_bean_name`, `process_type`)
VALUES ('BinanceBlockchain', 'BinanceBlockchain', 2, 'BinanceServiceImpl', 'CRYPTO');

INSERT INTO MERCHANT_SPEC_PARAMETERS (merchant_id, param_name, param_value) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'BinanceBlockchain'), 'LastScannedBlock', '27004949');