UPDATE MERCHANT
SET tokens_parrent_id = 16, service_bean_name = 'ethereumServiceImpl'
WHERE name = 'GAPI';

UPDATE MERCHANT_CURRENCY
SET min_sum = 0.00000001, refill_block = TRUE, withdraw_block = TRUE
WHERE merchant_id = (SELECT id from MERCHANT WHERE name='GAPI');

UPDATE MERCHANT_CURRENCY
SET min_sum = 0.000001
WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = 'SimpleTransfer')
AND currency_id = (select id from CURRENCY where name = 'GAPI');

UPDATE MERCHANT_CURRENCY
SET min_sum = 0.000001
WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = 'VoucherTransfer')
AND currency_id = (select id from CURRENCY where name = 'GAPI');

UPDATE MERCHANT_CURRENCY
SET min_sum = 0.000001
WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = 'VoucherFreeTransfer')
AND currency_id = (select id from CURRENCY where name = 'GAPI');