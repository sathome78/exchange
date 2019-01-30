UPDATE CURRENCY_PAIR SET hidden = 1
WHERE currency1_id = (SELECT id FROM CURRENCY WHERE name='BCH')
OR currency2_id = (SELECT id FROM CURRENCY WHERE name='BCH');

UPDATE MERCHANT_CURRENCY SET withdraw_block = 1, refill_block = 1, transfer_block = 1
WHERE currency_id = (SELECT id FROM CURRENCY WHERE name='BCH');
