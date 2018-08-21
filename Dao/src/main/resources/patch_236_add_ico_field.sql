ALTER TABLE CURRENCY_PAIR ADD type ENUM('MAIN', 'ICO') DEFAULT 'MAIN'  NOT NULL;
ALTER TABLE CURRENCY_PAIR MODIFY market enum('USD', 'BTC', 'ETH', 'FIAT', 'ICO') NOT NULL DEFAULT 'USD';

INSERT INTO USER_ROLE_BUSINESS_FEATURE (name) values ('MARKET_MAKER');

INSERT INTO USER_ROLE (name, user_role_business_feature_id, user_role_group_feature_id)
VALUES ('ICO_MARKET_MAKER', (SELECT id FROM USER_ROLE_BUSINESS_FEATURE UR WHERE UR.name = 'MARKET_MAKER'), 2);

INSERT INTO USER_ROLE_SETTINGS (user_role_id, order_acception_same_role_only, manual_change_allowed, bot_acception_allowed, considered_for_price_range, use_real_money) 
VALUES ((SELECT id FROM USER_ROLE WHERE name = 'ICO_MARKET_MAKER'), 0, 1, 0, 0, 1);

INSERT INTO COMMISSION (operation_type, value, user_role) VALUES
 (1, 0, 11),
 (2, 0.2, 11),
 (3, 0.2,11),
 (4, 0.2,11),
 (5,0,11),
 (6,0,11),
 (7,0,11),
 (8,0,11),
 (9,1,11);

INSERT INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
SELECT CP.id, 11, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
  JOIN ORDER_TYPE OT;

INSERT INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum, max_daily_request)
  SELECT id , 1, 11, 1, null, 10 from CURRENCY

  INSERT INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum, max_daily_request)
  SELECT id , 2, 11, 1, null, 10 from CURRENCY

INSERT INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum, max_daily_request)
  SELECT id , 9, 11, 1, null, 10 from CURRENCY


ALTER TABLE EXORDERS ADD base_type ENUM('LIMIT', 'ICO') DEFAULT 'LIMIT'  NOT NULL;


INSERT INTO USER_ADMIN_AUTHORITY_ROLE_APPLICATION  (user_id, admin_authority_id, applied_to_role_id)
  SELECT distinct user_id, 8, 11 FROM USER_ADMIN_AUTHORITY_ROLE_APPLICATION