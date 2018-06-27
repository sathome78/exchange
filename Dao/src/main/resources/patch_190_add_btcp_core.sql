

INSERT INTO CRYPTO_CORE_WALLET(merchant_id, currency_id, CRYPTO_CORE_WALLET.title_code)
VALUES ((SELECT id from MERCHANT WHERE name='BTCP'), (select id from CURRENCY where name='BTCP'), 'btcpWallet.title');