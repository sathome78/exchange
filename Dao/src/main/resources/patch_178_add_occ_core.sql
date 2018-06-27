

UPDATE MERCHANT SET name = 'OCC', service_bean_name = 'occServiceImpl' WHERE name = 'Octoin';

INSERT INTO CRYPTO_CORE_WALLET(merchant_id, currency_id, CRYPTO_CORE_WALLET.title_code)
VALUES ((SELECT id from MERCHANT WHERE name='OCC'), (select id from CURRENCY where name='OCC'), 'occWallet.title');