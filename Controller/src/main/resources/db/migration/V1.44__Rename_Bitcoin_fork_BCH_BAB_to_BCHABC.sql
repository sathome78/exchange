UPDATE IGNORE MERCHANT SET name='BCHABC', description='Bitcoin Cash ABC' WHERE name='BAB';

UPDATE IGNORE CURRENCY SET name='BCHABC', description='Bitcoin Cash ABC' WHERE name='BAB';

UPDATE IGNORE MERCHANT_IMAGE SET image_path='/client/img/merchants/BCHABC.png', image_name='Bitcoin Cash ABC' WHERE image_name='BAB';

UPDATE IGNORE CURRENCY_PAIR SET name='BCHABC/USD', ticker_name='BCHABC/USD' WHERE ticker_name='BAB/USD';

UPDATE IGNORE CURRENCY_PAIR SET name='BCHABC/BTC', ticker_name='BCHABC/BTC' WHERE ticker_name='BAB/BTC';

UPDATE IGNORE CURRENCY_PAIR SET name='BCHABC/ETH', ticker_name='BCHABC/ETH' WHERE ticker_name='BAB/ETH';