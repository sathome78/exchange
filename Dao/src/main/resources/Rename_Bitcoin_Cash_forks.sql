#Part 1 | Rename Bitcoin Cash (BCH) - Bitcoin Cash Old (BCH-old)

UPDATE IGNORE CURRENCY SET hidden=1 WHERE name='BCH';

UPDATE IGNORE CURRENCY_PAIR SET hidden=1 WHERE currency1_id = (SELECT id FROM CURRENCY WHERE name='BCH')
OR currency2_id = (SELECT id FROM CURRENCY WHERE name='BCH');

UPDATE IGNORE MERCHANT_CURRENCY SET withdraw_block=1, refill_block=1, transfer_block=1 WHERE currency_id = (SELECT id FROM CURRENCY WHERE name='BCH');

UPDATE IGNORE MERCHANT SET name='BCH-old', description='Bitcoin Cash Old' WHERE name='BCH';

UPDATE IGNORE CURRENCY SET name='BCH-old', description='Bitcoin Cash Old' WHERE name='BCH';

UPDATE IGNORE CURRENCY_PAIR SET name='BCH-old/USD', ticker_name='BCH-old/USD' WHERE ticker_name='BCH/USD';

UPDATE IGNORE CURRENCY_PAIR SET name='BCH-old/BTC', ticker_name='BCH-old/BTC' WHERE ticker_name='BCH/BTC';

UPDATE IGNORE CURRENCY_PAIR SET name='BCH-old/ETH', ticker_name='BCH-old/ETH' WHERE ticker_name='BCH/ETH';

UPDATE IGNORE CURRENCY_PAIR SET name='EDR/BCH-old', ticker_name='EDR/BCH-old' WHERE ticker_name='EDR/BCH';

#Part 2 | Rename Bitcoin Cash ABC (BCHABC) - Bitcoin Cash (BCH)

UPDATE IGNORE MERCHANT SET name='BCH', description='Bitcoin Cash', service_bean_name='bchServiceImpl' WHERE name='BCHABC';

UPDATE IGNORE CURRENCY SET name='BCH', description='Bitcoin Cash' WHERE name='BCHABC';

UPDATE IGNORE MERCHANT_IMAGE SET image_path='/client/img/merchants/BCH.png', image_name='Bitcoin Cash' WHERE image_name='Bitcoin Cash ABC';

UPDATE IGNORE CURRENCY_PAIR SET name='BCH/USD', ticker_name='BCH/USD' WHERE ticker_name='BCHABC/USD';

UPDATE IGNORE CURRENCY_PAIR SET name='BCH/BTC', ticker_name='BCH/BTC' WHERE ticker_name='BCHABC/BTC';

UPDATE IGNORE CURRENCY_PAIR SET name='BCH/ETH', ticker_name='BCH/ETH' WHERE ticker_name='BCHABC/ETH';

#Part 3 | Rename Bitcoin Cash SV (BCHSV) - Bitcoin SV (BSV)

UPDATE IGNORE MERCHANT SET name='BSV', description='Bitcoin SV' WHERE name='BCHSV';

UPDATE IGNORE CURRENCY SET name='BSV', description='Bitcoin SV' WHERE name='BCHSV';

UPDATE IGNORE MERCHANT_IMAGE SET image_path='/client/img/merchants/BSV.png', image_name='Bitcoin SV' WHERE image_name='Bitcoin Cash SV';

UPDATE IGNORE CURRENCY_PAIR SET name='BSV/USD', ticker_name='BSV/USD' WHERE ticker_name='BCHSV/USD';

UPDATE IGNORE CURRENCY_PAIR SET name='BSV/BTC', ticker_name='BSV/BTC' WHERE ticker_name='BCHSV/BTC';

UPDATE IGNORE CURRENCY_PAIR SET name='BSV/ETH', ticker_name='BSV/ETH' WHERE ticker_name='BCHSV/ETH';