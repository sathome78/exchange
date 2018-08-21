/*
  Скрипт для начисления средств юзерам при хардфорке биткоина (на примере Bitcoin Interest)


*/



CREATE TABLE BTC_BALANCE_FOR_BCI SELECT U.id, U.email, W.id AS wallet_id, IFNULL(DRV.active_balance_before, W.active_balance) AS btc_balance  FROM USER U
  JOIN WALLET W ON U.id = W.user_id AND W.currency_id = 4
  LEFT JOIN (SELECT user_wallet_id, active_balance_before FROM TRANSACTION
  WHERE id IN (SELECT MIN(id) FROM TRANSACTION WHERE currency_id = 4
                                                     -- TODO проверить id кошельков бот-аккаунтов
                                                     AND user_wallet_id != 482201 AND datetime > STR_TO_DATE('2018-01-20 02:04:45', '%Y-%m-%d %h:%i:%s') GROUP BY user_wallet_id)) DRV
    ON DRV.user_wallet_id = W.id
WHERE U.id NOT IN (SELECT user_id FROM BOT_TRADER) AND (W.active_balance > 0 OR (DRV.active_balance_before IS NOT NULL AND DRV.active_balance_before > 0)) ORDER BY U.id ASC;

UPDATE WALLET WLT
  JOIN BTC_BALANCE_FOR_BCI BB ON WLT.user_id = BB.id AND WLT.currency_id = 75
SET WLT.active_balance = WLT.active_balance + BB.btc_balance;