

UPDATE MERCHANT SET name = 'LCC', service_bean_name = 'lccServiceImpl' WHERE name = 'LitecoinCash';

INSERT INTO CRYPTO_CORE_WALLET(merchant_id, currency_id, CRYPTO_CORE_WALLET.title_code)
VALUES ((SELECT id from MERCHANT WHERE name='LCC'), (select id from CURRENCY where name='LCC'), 'lccWallet.title');