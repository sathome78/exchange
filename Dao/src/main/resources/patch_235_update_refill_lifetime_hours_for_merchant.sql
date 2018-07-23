UPDATE MERCHANT_CURRENCY merch_curr
JOIN MERCHANT merch ON merch_curr.merchant_id=merch.id
SET merch_curr.refill_lifetime_hours = 48
WHERE merch.process_type='MERCHANT'