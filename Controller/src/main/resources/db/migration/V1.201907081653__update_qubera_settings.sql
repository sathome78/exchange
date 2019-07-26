UPDATE MERCHANT_CURRENCY
SET withdraw_auto_enabled = 1, withdraw_auto_threshold_amount = 1
WHERE merchant_id = (
  SELECT id
  FROM MERCHANT
  WHERE name = 'Qubera'
);