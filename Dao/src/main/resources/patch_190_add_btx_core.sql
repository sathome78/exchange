
UPDATE MERCHANT SET name = 'BTX', service_bean_name = 'btxServiceImpl' WHERE name = 'BitCore';

INSERT INTO CRYPTO_CORE_WALLET(merchant_id, currency_id, CRYPTO_CORE_WALLET.title_code)
VALUES ((SELECT id from MERCHANT WHERE name='BTX'), (select id from CURRENCY where name='BTX'), 'btxWallet.title');