INSERT INTO CRYPTO_CORE_WALLET(merchant_id, currency_id, CRYPTO_CORE_WALLET.title_code)
VALUES ((SELECT id from MERCHANT WHERE name='BCX'), (select id from CURRENCY where name='BCX'), 'bcxWallet.title');

INSERT INTO CRYPTO_CORE_WALLET(merchant_id, currency_id, CRYPTO_CORE_WALLET.title_code)
VALUES ((SELECT id from MERCHANT WHERE name='SBTC'), (select id from CURRENCY where name='SBTC'), 'sbtcWallet.title');

UPDATE MERCHANT SET service_bean_name = 'bcxServiceImpl' WHERE name = 'BCX';

UPDATE MERCHANT_IMAGE SET image_path = '/client/img/merchants/BCX.png', image_name = 'BCX'
WHERE merchant_id = (SELECT id from MERCHANT WHERE name='BCX');