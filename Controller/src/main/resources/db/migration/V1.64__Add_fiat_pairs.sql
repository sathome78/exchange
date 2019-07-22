use birzha;

INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, ticker_name, market)
VALUES((select id from CURRENCY where name = 'BTC'), (select id from CURRENCY where name = 'UAH'), 'BTC/UAH', 170, 0, 'BTC/UAH', 'FIAT');

INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
                                                  JOIN USER_ROLE UR
                                                  JOIN ORDER_TYPE OT where CP.name='BTC/UAH';


INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, ticker_name, market)
VALUES((select id from CURRENCY where name = 'BTC'), (select id from CURRENCY where name = 'RUB'), 'BTC/RUB', 170, 0, 'BTC/RUB', 'FIAT');

INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
                                                  JOIN USER_ROLE UR
                                                  JOIN ORDER_TYPE OT where CP.name='BTC/RUB';




INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, ticker_name, market)
VALUES((select id from CURRENCY where name = 'ETH'), (select id from CURRENCY where name = 'UAH'), 'ETH/UAH', 170, 0, 'ETH/UAH', 'FIAT');

INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
                                                  JOIN USER_ROLE UR
                                                  JOIN ORDER_TYPE OT where CP.name='ETH/UAH';


INSERT IGNORE INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, ticker_name, market)
VALUES((select id from CURRENCY where name = 'ETH'), (select id from CURRENCY where name = 'RUB'), 'ETH/RUB', 170, 0, 'ETH/RUB', 'FIAT');

INSERT IGNORE INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
                                                  JOIN USER_ROLE UR
                                                  JOIN ORDER_TYPE OT where CP.name='ETH/RUB';


