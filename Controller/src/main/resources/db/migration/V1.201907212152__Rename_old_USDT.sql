UPDATE `MERCHANT_CURRENCY` SET refill_block = 1, withdraw_block = 1, transfer_block = 1 WHERE currency_id = (SELECT id FROM `CURRENCY` WHERE name = 'USDT');

UPDATE `MERCHANT` SET name ='USDT-old' WHERE name = 'USDT';

UPDATE `CURRENCY` SET name ='USDT-old', hidden = 1 WHERE name = 'USDT';

UPDATE `CURRENCY_PAIR` SET name = 'BTC/USDT-old', hidden = 1, ticker_name = 'BTC/USDT-old' WHERE name = 'BTC/USDT';

UPDATE `CURRENCY_PAIR` SET name = 'ETH/USDT-old', hidden = 1, ticker_name = 'ETH/USDT-old' WHERE name = 'ETH/USDT';

UPDATE `CURRENCY_PAIR` SET name = 'LTC/USDT-old', hidden = 1, ticker_name = 'LTC/USDT-old' WHERE name = 'LTC/USDT';

UPDATE `CURRENCY_PAIR` SET name = 'ETC/USDT-old', hidden = 1, ticker_name = 'ETC/USDT-old' WHERE name = 'ETC/USDT';

UPDATE `CURRENCY_PAIR` SET name = 'DASH/USDT-old', hidden = 1, ticker_name = 'DASH/USDT-old' WHERE name = 'DASH/USDT';

UPDATE `CURRENCY_PAIR` SET name = 'XRP/USDT-old', hidden = 1, ticker_name = 'XRP/USDT-old' WHERE name = 'XRP/USDT';

UPDATE `CURRENCY_PAIR` SET name = 'XLM/USDT-old', hidden = 1, ticker_name = 'XLM/USDT-old' WHERE name = 'XLM/USDT';

UPDATE `CURRENCY_PAIR` SET name = 'XEM/USDT-old', hidden = 1, ticker_name = 'XEM/USDT-old' WHERE name = 'XEM/USDT';

UPDATE `CURRENCY_PAIR` SET name = 'ATB/USDT-old', hidden = 1, ticker_name = 'ATB/USDT-old' WHERE name = 'ATB/USDT';

UPDATE `CURRENCY_PAIR` SET name = 'EDC/USDT-old', hidden = 1, ticker_name = 'EDC/USDT-old' WHERE name = 'EDC/USDT';

UPDATE `CURRENCY_PAIR` SET name = 'IOTA/USDT-old', hidden = 1, ticker_name = 'IOTA/USDT-old' WHERE name = 'IOTA/USDT';

UPDATE `CURRENCY_PAIR` SET name = 'DOGE/USDT-old', hidden = 1, ticker_name = 'DOGE/USDT-old' WHERE name = 'DOGE/USDT';

UPDATE `CURRENCY_PAIR` SET name = 'LSK/USDT-old', hidden = 1, ticker_name = 'LSK/USDT-old' WHERE name = 'LSK/USDT';

UPDATE `CURRENCY_PAIR` SET name = 'XMR/USDT-old', hidden = 1, ticker_name = 'XMR/USDT-old' WHERE name = 'XMR/USDT';

UPDATE `CURRENCY_PAIR` SET name = 'NEO/USDT-old', hidden = 1, ticker_name = 'NEO/USDT-old' WHERE name = 'NEO/USDT';

UPDATE `CURRENCY_PAIR` SET name = 'GAS/USDT-old', hidden = 1, ticker_name = 'GAS/USDT-old' WHERE name = 'GAS/USDT';

UPDATE `CURRENCY_PAIR` SET name = 'ZEC/USDT-old', hidden = 1, ticker_name = 'ZEC/USDT-old' WHERE name = 'ZEC/USDT';

UPDATE `CURRENCY_PAIR` SET name = 'WAVES/USDT-old', hidden = 1, ticker_name = 'WAVES/USDT-old' WHERE name = 'WAVES/USDT';

UPDATE `CURRENCY_PAIR` SET name = 'BNB/USDT-old', hidden = 1, ticker_name = 'BNB/USDT-old' WHERE name = 'BNB/USDT';

UPDATE `CURRENCY_PAIR` SET name = 'VRBS/USDT-old', hidden = 1, ticker_name = 'VRBS/USDT-old' WHERE name = 'VRBS/USDT';

UPDATE `CURRENCY_PAIR` SET name = 'CRON/USDT-old', hidden = 1, ticker_name = 'CRON/USDT-old' WHERE name = 'CRON/USDT';