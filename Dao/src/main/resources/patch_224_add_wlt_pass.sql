ALTER TABLE CRYPTO_CORE_WALLET ADD passphrase VARCHAR(64) NULL;

-- for prod - replace 'pass123' with actual passphrase!

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'MxQR8wTcEuuNJdFzxGKz9MDcjawg_Y5N66uIHELS5JY=' where currency_id = (SELECT id FROM CURRENCY where name = 'BTC')
                                                    AND merchant_id = (SELECT id FROM MERCHANT where name = 'Bitcoin');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'KBhChGEuEyqu9uTXxxrPPfQrSprZUpVfXS5G' where currency_id = (SELECT id FROM CURRENCY where name = 'LTC')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'Litecoin');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'e3tzpe9PsuhpP93C7GeLrz7WmZCBptpbwdCk' where currency_id = (SELECT id FROM CURRENCY where name = 'DASH')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'Dash');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'JucEfGB3YQ7abeAj6S7AFq8mjZC68hbzkk4E' where currency_id = (SELECT id FROM CURRENCY where name = 'ATB')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'ATB'); -- NEW!!

UPDATE CRYPTO_CORE_WALLET SET passphrase = '989sX5KeV6aHxT2AQNRX8znNqvuQB3p5ztwh' where currency_id = (SELECT id FROM CURRENCY where name = 'BCH')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'Bitcoin Cash');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'UkcM3mKmhuujCZNBKqtjjyvp4Cz898AsR58E' where currency_id = (SELECT id FROM CURRENCY where name = 'DOGE')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'Dogecoin');

UPDATE CRYPTO_CORE_WALLET SET passphrase = '4xAP6d7Kw8d4jeSFm5dEjedVUD2DUCQdNCds' where currency_id = (SELECT id FROM CURRENCY where name = 'BTG')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'BTG');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'zfYEJDsP5Y9uQdssrF3wAmsgBRWEMNRnJsvB' where currency_id = (SELECT id FROM CURRENCY where name = 'ZEC')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'Zcash'); -- NEW

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'cwg59fVvMCTyPWHA5N2tjwdVcfE6CHmd6D57' where currency_id = (SELECT id FROM CURRENCY where name = 'B2X')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'B2X');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'MxQR8wTcEuuNJdFzxGKz9MDcjawg_Y5N66uIHELS5JY=' where currency_id = (SELECT id FROM CURRENCY where name = 'BCD')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'BCD');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'a9qW7VEsE89XGGR4KV4ynsKuQ8hF5AZj' where currency_id = (SELECT id FROM CURRENCY where name = 'PLC')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'PLC');

UPDATE CRYPTO_CORE_WALLET SET passphrase = '5mCGFy6GAteA7nxAJXREHzevXcXunHfFgNmN' where currency_id = (SELECT id FROM CURRENCY where name = 'OCC')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'OCC');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'PBEhsVJqVtMaxWRgTdhdjmMgU927DwhypqHY' where currency_id = (SELECT id FROM CURRENCY where name = 'BCX')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'BCX');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 's9BdTJtRDcgtwALFNBty6x2fBYkwqNZU2e8Q' where currency_id = (SELECT id FROM CURRENCY where name = 'BTCZ')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'BTCZ');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'gjQhUEc9nkJE9UM5sPSMUV44AuBtZm25G2un' where currency_id = (SELECT id FROM CURRENCY where name = 'LCC')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'LCC');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'sP5gMKpq6cNeYSHxptxhdAD742EEXh9ZXVDa' where currency_id = (SELECT id FROM CURRENCY where name = 'BTX')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'BTX'); -- NEW!!

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'dZP?c7-{67zGSAKD=7fQ' where currency_id = (SELECT id FROM CURRENCY where name = 'BCI')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'BCI');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'MUTNfAxYGHTQF9E2zjVAbpt46pkUsR66Jdrb' where currency_id = (SELECT id FROM CURRENCY where name = 'BCA')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'BitcoinAtom');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'BC5ZZE32c8wVYFXV9Xd5hHhFnCy6DmtfCPbY' where currency_id = (SELECT id FROM CURRENCY where name = 'SZC')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'SZC');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'p4nzTz56YS8ZkncYckfMBZUY2n32RyELQAvJ' where currency_id = (SELECT id FROM CURRENCY where name = 'XBD')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'BitDollar');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'xT3qLbEBGmsVVq6Xuzb9L45xZhK3kSaTHS2G' where currency_id = (SELECT id FROM CURRENCY where name = 'BTCP')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'BTCP');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'UhcX4rh9uHjGhmPNnH4s5QMBZdYgp34G' where currency_id = (SELECT id FROM CURRENCY where name = 'ABTC')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'AML');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'G8ASRsXCXJe5ugjgvsKQnavk64wdrLjGmUkM' where currency_id = (SELECT id FROM CURRENCY where name = 'NSR')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'NuShares');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'tMVPjT6DaumNaMYAc9sehc2p2jt6VrFzsaN2' where currency_id = (SELECT id FROM CURRENCY where name = 'BBX')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'BBX');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'CN66hNvLY2FhEuFVMCLBQ2sxPA7cbC426xGB' where currency_id = (SELECT id FROM CURRENCY where name = 'BEET')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'BEET');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'BC5ZZE32c8wVYFXV9Xd5hHhFnCy6DmtfCPbY' where currency_id = (SELECT id FROM CURRENCY where name = 'NYC')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'NYC');


