UPDATE IGNORE MERCHANT SET name = 'POA' WHERE name = 'POA20';

UPDATE IGNORE CURRENCY SET name = 'POA' WHERE name = 'POA20';

UPDATE IGNORE MERCHANT_IMAGE SET image_path = '/client/img/merchants/POA.png' WHERE image_path = '/client/img/merchants/POA20.png';

UPDATE IGNORE CURRENCY_PAIR SET ticker_name = 'POA/USD', name = 'POA/USD' WHERE name = 'POA20/USD';

UPDATE IGNORE CURRENCY_PAIR SET ticker_name = 'POA/BTC', name = 'POA/BTC' WHERE name = 'POA20/BTC';

UPDATE IGNORE CURRENCY_PAIR SET ticker_name = 'POA/ETH', name = 'POA/ETH' WHERE name = 'POA20/ETH';