

INSERT INTO CRYPTO_CORE_WALLET(merchant_id, currency_id, CRYPTO_CORE_WALLET.title_code)
VALUES ((SELECT id from MERCHANT WHERE name='BTCZ'), (select id from CURRENCY where name='BTCZ'), 'btczWallet.title');