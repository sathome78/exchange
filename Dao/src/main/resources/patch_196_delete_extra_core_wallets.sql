
select m.name as merchant_name, c.name as currency_name, ccw.title_code
from CRYPTO_CORE_WALLET ccw
  join MERCHANT m ON ccw.merchant_id = m.id
  join CURRENCY c ON ccw.currency_id = c.id

where ccw.title_code = 'bchWallet.title';


UPDATE CRYPTO_CORE_WALLET SET title_code = 'zecWallet.title' where merchant_id = (SELECT id from MERCHANT where name = 'Zcash');


DELETE FROM CRYPTO_CORE_WALLET where title_code = 'bchWallet.title' AND merchant_id != (SELECT id from MERCHANT where name = 'Bitcoin Cash');
DELETE FROM CRYPTO_CORE_WALLET where merchant_id = (SELECT id from MERCHANT where name = 'PBTC');

INSERT INTO CRYPTO_CORE_WALLET(merchant_id, currency_id, CRYPTO_CORE_WALLET.title_code)
VALUES ((SELECT id from MERCHANT WHERE name='BCD'), (select id from CURRENCY where name='BCD'), 'bcdWallet.title');

select m.name as merchant_name, c.name as currency_name, ccw.title_code
from CRYPTO_CORE_WALLET ccw
  join MERCHANT m ON ccw.merchant_id = m.id
  join CURRENCY c ON ccw.currency_id = c.id
ORDER BY currency_name ASC;



