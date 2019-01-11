UPDATE IGNORE MERCHANT SET name = 'ECTE' WHERE name = 'ECT';

UPDATE IGNORE CURRENCY SET name = 'ECTE' WHERE name = 'ECT';

UPDATE IGNORE MERCHANT_IMAGE SET image_path = '/client/img/merchants/ECTE.png' WHERE image_path = '/client/img/merchants/ECT.png';

UPDATE IGNORE CURRENCY_PAIR SET name = 'ECTE/USD', ticker_name = 'ECTE/USD' WHERE name = 'ECT/USD';

UPDATE IGNORE CURRENCY_PAIR SET name = 'ECTE/BTC', ticker_name = 'ECTE/BTC' WHERE name = 'ECT/BTC';

UPDATE IGNORE CURRENCY_PAIR SET name = 'ECTE/ETH', ticker_name = 'ECTE/ETH' WHERE name = 'ECT/ETH';