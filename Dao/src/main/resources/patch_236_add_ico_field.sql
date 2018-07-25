ALTER TABLE CURRENCY_PAIR ADD type ENUM('MAIN', 'ICO') DEFAULT 'MAIN'  NOT NULL;

INSERT INTO USER_ROLE (name, user_role_business_feature_id, user_role_group_feature_id) VALUES ('ICO_MARKET_MAKER', 2, 2);

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

ALTER TABLE EXORDERS ADD base_type ENUM('LIMIT', 'ICO') DEFAULT 'LIMIT'  NOT NULL;