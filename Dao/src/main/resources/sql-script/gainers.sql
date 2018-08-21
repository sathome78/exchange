/*
    Скрипт для сбора информации по заработкам трейдеров на бирже.

    НА ПРОДАКШЕНЕ ПРИМЕНЯТЬ КРАЙНЕ НЕЖЕЛАТЕЛЬНО! Некоторые запросы очень тяжёлые и могут повлиять на производительность БД.

    Поэтому лучше применять скрипт на локально развёрнутом дампе.
*/

CREATE TABLE USER_BALANCES_PREV
    SELECT user_wallet_id, active_balance_before, reserved_balance_before
    from TRANSACTION
    where id IN (SELECT MIN(id)FROM TRANSACTION
    where datetime > STR_TO_DATE('2018-04-01', '%Y-%m-%d')
          AND user_wallet_id NOT IN (SELECT id FROM WALLET WHERE id IN (SELECT user_id FROM BOT_TRADER))
    GROUP BY user_wallet_id);

CREATE INDEX withdraw_request_status_modification_date_index ON withdraw_request (status_modification_date);

CREATE TABLE WALLET_NON_ZERO
    SELECT * from WALLET where active_balance > 0 OR reserved_balance > 0;

CREATE TABLE WALLET_PREV_CURRENT

    SELECT W.id, W.user_id, W.currency_id, W.active_balance, W.reserved_balance, IFNULL(UBP.active_balance_before, 0) AS active_balance_before, IFNULL(UBP.reserved_balance_before, 0) AS reserved_balance_before
    from WALLET_NON_ZERO W
      LEFT JOIN USER_BALANCES_PREV UBP ON UBP.user_wallet_id = W.id

    UNION
    SELECT IFNULL(W.id, UBP.user_wallet_id) AS id,
           IFNULL(W.user_id, (SELECT user_id FROM WALLET WHERE id = UBP.user_wallet_id)) AS user_id,
           IFNULL(W.currency_id, (SELECT currency_id FROM WALLET WHERE id = UBP.user_wallet_id)) AS currency_id,
           IFNULL(W.active_balance, 0) AS active_balance,
           IFNULL(W.reserved_balance, 0) AS reserved_balance,
      UBP.active_balance_before, UBP.reserved_balance_before
    from WALLET_NON_ZERO W
      RIGHT JOIN USER_BALANCES_PREV UBP ON UBP.user_wallet_id = W.id;




CREATE TABLE WALLET_IN_OUT
    SELECT user_id, currency_id, active_balance, reserved_balance, SUM(active_balance_before) AS active_balance_before,
                                                                   SUM(reserved_balance_before) AS reserved_balance_before,
                                                                   SUM(total_input) AS total_input, SUM(total_output) AS total_output

    FROM (
           SELECT W.user_id AS user_id, W.currency_id AS currency_id,
                  W.active_balance AS active_balance, W.reserved_balance AS reserved_balance,
                  W.active_balance_before AS active_balance_before, W.reserved_balance_before AS reserved_balance_before,
                  0 AS total_input, 0 AS total_output
           FROM WALLET_PREV_CURRENT AS W

           UNION ALL

           SELECT WALLET.user_id, WALLET.currency_id,
             WALLET.active_balance, WALLET.reserved_balance,
             0, 0,
             IFNULL(RR.amount, 0), 0
           FROM WALLET
             JOIN REFILL_REQUEST RR ON RR.user_id = WALLET.user_id AND RR.currency_id = WALLET.currency_id
                                       AND RR.status_id IN (9, 10)
           WHERE RR.status_modification_date >= STR_TO_DATE('2018-04-01', '%Y-%m-%d')

           UNION ALL

           SELECT WALLET.user_id, WALLET.currency_id,  WALLET.active_balance, WALLET.reserved_balance,
             0, 0, 0, IFNULL(WITHDRAW_REQUEST.amount, 0)
           FROM WALLET
             JOIN WITHDRAW_REQUEST ON WITHDRAW_REQUEST.user_id = WALLET.user_id AND WITHDRAW_REQUEST.currency_id = WALLET.currency_id
                                      AND WITHDRAW_REQUEST.status_id IN (9, 10)
           WHERE WITHDRAW_REQUEST.status_modification_date >= STR_TO_DATE('2018-04-01', '%Y-%m-%d')

         ) AGGR
    GROUP BY user_id, currency_id, active_balance, reserved_balance, active_balance_before, reserved_balance_before;


CREATE TABLE RATES_TO_USD
    SELECT CURRENCY_PAIR.currency1_id,
      (SELECT LASTORDER.exrate
       FROM EXORDERS LASTORDER
       WHERE
         (LASTORDER.currency_pair_id = AGRIGATE.currency_pair_id)  AND
         (LASTORDER.status_id = AGRIGATE.status_id)
       ORDER BY LASTORDER.date_acception DESC, LASTORDER.id DESC
       LIMIT 1) AS rate_to_usd

    FROM (
           SELECT DISTINCT
             EXORDERS.status_id AS status_id,
             EXORDERS.currency_pair_id AS currency_pair_id
           FROM EXORDERS
           WHERE EXORDERS.status_id = 3
         )
         AGRIGATE
      JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = AGRIGATE.currency_pair_id)
                            AND CURRENCY_PAIR.currency1_id IN (SELECT DISTINCT currency_id from GAINERS where rate_to_usd IS NULL)
                            AND CURRENCY_PAIR.currency2_id = 4
                            AND (CURRENCY_PAIR.hidden != 1)


    ORDER BY -CURRENCY_PAIR.pair_order DESC;


CREATE TABLE GAINERS
    SELECT U.id AS user_id, U.email, CUR.id AS currency_id, CUR.name AS currency_name, ((WIO.active_balance + WIO.reserved_balance) +
                                                                                        WIO.total_output - WIO.total_input -
                                                                                        (WIO.active_balance_before + WIO.reserved_balance_before)) AS surplus,
      RTU.rate_to_usd
    from WALLET_IN_OUT WIO
      JOIN USER U ON WIO.user_id = U.id
      JOIN CURRENCY CUR ON WIO.currency_id = CUR.id
      LEFT JOIN RATES_TO_USD RTU ON RTU.currency1_id = WIO.currency_id
    ORDER BY email;

UPDATE GAINERS SET rate_to_usd = 0.0028 where currency_id = 13;

select count(*) from GAINERS where rate_to_usd IS NULL;
SET @btc_usd_rate = (SELECT exrate from exorders where currency_pair_id = 1 AND status_id = 3 ORDER BY date_acception DESC LIMIT 1);
SELECT @btc_usd_rate;

UPDATE GAINERS G
  JOIN currency_pair CP on CP.currency1_id = G.currency_id AND CP.currency2_id = 4
set G.rate_to_usd = ((SELECT exrate from exorders
where currency_pair_id = CP.id
      AND status_id = 3 ORDER BY date_acception DESC LIMIT 1) * @btc_usd_rate)
WHERE G.rate_to_usd IS NULL;


SELECT DISTINCT ((SELECT exrate from exorders
where currency_pair_id = CP.id
      AND status_id = 3 ORDER BY date_acception DESC LIMIT 1) * @btc_usd_rate) AS rate_to_btc FROM GAINERS G
  JOIN currency_pair CP on CP.currency1_id = G.currency_id AND CP.currency2_id = 4
WHERE G.rate_to_usd IS NULL;



SELECT * FROM GAINERS
INTO OUTFILE 'C:/ProgramData/MySQL/MySQL Server 5.7/Uploads/result1.csv'
FIELDS TERMINATED BY ';'
LINES TERMINATED BY '\n';