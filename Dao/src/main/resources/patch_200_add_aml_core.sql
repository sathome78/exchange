

UPDATE MERCHANT SET name = 'AML', service_bean_name = 'amlServiceImpl' WHERE name = 'AMLtoken';

INSERT INTO CRYPTO_CORE_WALLET(merchant_id, currency_id, CRYPTO_CORE_WALLET.title_code)
VALUES ((SELECT id from MERCHANT WHERE name='AML'), (select id from CURRENCY where name='AML'), 'amlWallet.title');