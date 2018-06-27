

INSERT INTO CRYPTO_CORE_WALLET(merchant_id, currency_id, CRYPTO_CORE_WALLET.title_code)
VALUES ((SELECT id from MERCHANT WHERE name='BitcoinAtom'), (select id from CURRENCY where name='BCA'), 'bcaWallet.title');