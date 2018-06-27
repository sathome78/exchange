UPDATE MERCHANT SET name = 'BCI', service_bean_name = 'bciServiceImpl' WHERE name = 'BitcoinInterest';

INSERT INTO CRYPTO_CORE_WALLET(merchant_id, currency_id, CRYPTO_CORE_WALLET.title_code)
VALUES ((SELECT id from MERCHANT WHERE name='BCI'), (select id from CURRENCY where name='BCI'), 'bciWallet.title');