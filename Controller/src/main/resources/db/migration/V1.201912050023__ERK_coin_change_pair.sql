UPDATE IGNORE CURRENCY_PAIR SET currency2_id = (SELECT id FROM CURRENCY WHERE name = 'USDT'), name = 'ERK/USDT', ticker_name = 'ERK/USDT', market = 'USDT'
WHERE currency1_id = (SELECT id FROM CURRENCY WHERE name = 'ERK') AND currency2_id = (SELECT id FROM CURRENCY WHERE name = 'USD');