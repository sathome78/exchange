/*
for currency pair INO/BTC (id 205) and user id 59079
*/


SELECT
  U.email,
  AGGR.sum_btc              AS sum_trade,
  SUM(IFNULL(RR.amount, 0)) AS sum_refill
FROM (
       SELECT
         AGR_INNER.counteragent_id,
         SUM(AGR_INNER.sum_btc) AS sum_btc
       FROM (
              SELECT
                EO.user_acceptor_id    AS counteragent_id,
                SUM(EO.amount_convert) AS sum_btc
              FROM EXORDERS EO
              WHERE EO.currency_pair_id = 205 AND EO.status_id = 3 AND EO.operation_type_id = 3 AND EO.user_id = 59079
              GROUP BY counteragent_id

              UNION ALL

              SELECT
                EO.user_id             AS counteragent_id,
                SUM(EO.amount_convert) AS sum_btc
              FROM EXORDERS EO
              WHERE EO.currency_pair_id = 205 AND EO.status_id = 3 AND EO.operation_type_id = 4 AND
                    EO.user_acceptor_id = 59079
              GROUP BY counteragent_id
            ) AS AGR_INNER
       GROUP BY AGR_INNER.counteragent_id

     ) AS AGGR

  JOIN USER U ON U.id = AGGR.counteragent_id
  LEFT JOIN REFILL_REQUEST RR ON U.id = RR.user_id AND RR.currency_id = 4
GROUP BY U.email, sum_trade;