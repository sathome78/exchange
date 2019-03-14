UPDATE CURRENCY SET name = 'EDC' WHERE name = 'EDR';

UPDATE CURRENCY_PAIR SET name = REPLACE(name, 'EDR', 'EDC'), ticker_name = REPLACE(ticker_name, 'EDR', 'EDC') WHERE currency1_id = 9;