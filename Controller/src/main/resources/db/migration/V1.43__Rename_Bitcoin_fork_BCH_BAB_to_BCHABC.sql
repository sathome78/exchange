UPDATE IGNORE MERCHANT SET name='BCHSV', description='Bitcoin Cash SV' WHERE name='BSV';

UPDATE IGNORE CURRENCY SET name='BCHSV', description='Bitcoin Cash SV' WHERE name='BSV';

UPDATE IGNORE MERCHANT_IMAGE SET image_path='/client/img/merchants/BCHSV.png', image_name='Bitcoin Cash SV' WHERE image_name='BSV';

UPDATE IGNORE CURRENCY_PAIR SET name='BCHSV/USD', ticker_name='BCHSV/USD' WHERE ticker_name='BSV/USD';

UPDATE IGNORE CURRENCY_PAIR SET name='BCHSV/BTC', ticker_name='BCHSV/BTC' WHERE ticker_name='BSV/BTC';

UPDATE IGNORE CURRENCY_PAIR SET name='BCHSV/ETH', ticker_name='BCHSV/ETH' WHERE ticker_name='BSV/ETH';