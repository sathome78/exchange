UPDATE MERCHANT M SET M.service_bean_name = 'zilServiceImpl', M.tokens_parrent_id = null WHERE M.name = 'ZIL';

UPDATE REFILL_REQUEST_ADDRESS RRA SET RRA.is_valid = false WHERE RRA.currency_id = (SELECT id FROM CURRENCY WHERE name = 'ZIL');

INSERT INTO MERCHANT_SPEC_PARAMETERS (merchant_id, param_name, param_value) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'ZIL'), 'LastScannedBlock', '172800');