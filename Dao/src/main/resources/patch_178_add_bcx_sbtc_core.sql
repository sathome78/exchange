INSERT INTO CRYPTO_CORE_WALLET(merchant_id, currency_id, CRYPTO_CORE_WALLET.title_code)
VALUES ((SELECT id from MERCHANT WHERE name='BCX'), (select id from CURRENCY where name='BCX'), 'bcxWallet.title');

INSERT INTO CRYPTO_CORE_WALLET(merchant_id, currency_id, CRYPTO_CORE_WALLET.title_code)
VALUES ((SELECT id from MERCHANT WHERE name='SBTC'), (select id from CURRENCY where name='SBTC'), 'sbtcWallet.title');