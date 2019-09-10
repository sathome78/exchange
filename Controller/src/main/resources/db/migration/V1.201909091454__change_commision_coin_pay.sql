UPDATE MERCHANT_CURRENCY
SET merchant_input_commission  = 2,
    merchant_output_commission = 2,
    min_sum                    = 300
WHERE currency_id = (SELECT id FROM CURRENCY WHERE name = 'UAH')
  AND merchant_id = (SELECT id FROM MERCHANT WHERE name = 'CoinPay');