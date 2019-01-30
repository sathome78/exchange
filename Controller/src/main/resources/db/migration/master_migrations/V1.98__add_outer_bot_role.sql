
INSERT INTO USER_ROLE_BUSINESS_FEATURE (name) VALUE ('OUTER_MARKET_BOT');

INSERT INTO USER_ROLE (name, user_role_business_feature_id, user_role_group_feature_id, user_role_report_group_feature_id)
VALUES ('OUTER_MARKET_BOT', (SELECT id FROM USER_ROLE_BUSINESS_FEATURE WHERE name = 'OUTER_MARKET_BOT'), 3, 4);

SET @new_role_id = (SELECT id FROM USER_ROLE WHERE name = 'OUTER_MARKET_BOT');

INSERT INTO USER_ROLE_SETTINGS (user_role_id, order_acception_same_role_only, manual_change_allowed, bot_acception_allowed, considered_for_price_range, use_real_money)
VALUES (@new_role_id, 1, 0, 0, 1, 0);


INSERT INTO COMMISSION (operation_type, value, user_role) VALUES
 (1, 0, @new_role_id),
 (2, 0.2, @new_role_id),
 (3, 0.2, @new_role_id),
 (4, 0.2, @new_role_id),
 (5, 0, @new_role_id),
 (6, 0, @new_role_id),
 (7, 0, @new_role_id),
 (8, 0, @new_role_id),
 (9, 1, @new_role_id);

INSERT INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
SELECT CP.id, @new_role_id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
  JOIN ORDER_TYPE OT;

INSERT INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum, max_daily_request)
  SELECT id , 1, @new_role_id, 1, null, @new_role_id from CURRENCY;

  INSERT INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum, max_daily_request)
  SELECT id , 2, @new_role_id, 1, null, @new_role_id from CURRENCY;

INSERT INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum, max_daily_request)
  SELECT id , 9, @new_role_id, 1, null, @new_role_id from CURRENCY;