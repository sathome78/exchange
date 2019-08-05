UPDATE MERCHANT M SET M.service_bean_name = 'arnServiceImpl', M.tokens_parrent_id = null WHERE M.name = 'ARN';

UPDATE REFILL_REQUEST_ADDRESS RRA SET RRA.is_valid = false WHERE RRA.currency_id = (SELECT id FROM CURRENCY WHERE name = 'ARN');