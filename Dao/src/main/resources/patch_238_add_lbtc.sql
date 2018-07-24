
INSERT INTO CRYPTO_CORE_WALLET(merchant_id, currency_id, CRYPTO_CORE_WALLET.title_code, passphrase)
VALUES ((SELECT id from MERCHANT WHERE name='LBTC'), (select id from CURRENCY where name='LBTC'), 'lbtcWallet.title',
        'pass123');