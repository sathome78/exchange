-- USER STATUS
INSERT INTO USER_STATUS (id, name, description) VALUES (1, 'registered', 'without email confirmation');
INSERT INTO USER_STATUS (id, name, description) VALUES (2, 'activated', 'with email confirmation');
INSERT INTO USER_STATUS (id, name, description) VALUES (3, 'blocked', 'blocked by admin');
INSERT INTO USER_STATUS (id, name, description) VALUES (4, 'banned_in_chat', 'banned in chat by admin');

-- USER ROLE BUSINESS FEATURE
INSERT INTO USER_ROLE_BUSINESS_FEATURE (id, name) VALUES (1, 'ADMIN');
INSERT INTO USER_ROLE_BUSINESS_FEATURE (id, name) VALUES (2, 'USER');
INSERT INTO USER_ROLE_BUSINESS_FEATURE (id, name) VALUES (3, 'EXCHANGE');
INSERT INTO USER_ROLE_BUSINESS_FEATURE (id, name) VALUES (4, 'VIP_USER');
INSERT INTO USER_ROLE_BUSINESS_FEATURE (id, name) VALUES (5, 'TRADER');
INSERT INTO USER_ROLE_BUSINESS_FEATURE (id, name) VALUES (6, 'BOT');
INSERT INTO USER_ROLE_BUSINESS_FEATURE (id, name) VALUES (7, 'MARKET_MAKER');
INSERT INTO USER_ROLE_BUSINESS_FEATURE (id, name) VALUES (9, 'OUTER_MARKET_BOT');

-- USER ROLE GROUP FEATURE
INSERT INTO USER_ROLE_GROUP_FEATURE (id, name) VALUES (1, 'ADMINS');
INSERT INTO USER_ROLE_GROUP_FEATURE (id, name) VALUES (2, 'USERS');
INSERT INTO USER_ROLE_GROUP_FEATURE (id, name) VALUES (3, 'BOT');

-- USER ROLE REPORT GROUP FEATURE
INSERT INTO USER_ROLE_REPORT_GROUP_FEATURE (id, name) VALUES (1, 'ADMIN');
INSERT INTO USER_ROLE_REPORT_GROUP_FEATURE (id, name) VALUES (4, 'BOT');
INSERT INTO USER_ROLE_REPORT_GROUP_FEATURE (id, name) VALUES (3, 'TRADER');
INSERT INTO USER_ROLE_REPORT_GROUP_FEATURE (id, name) VALUES (2, 'USER');

-- USER ROLE
INSERT INTO USER_ROLE (id, name, user_role_business_feature_id, user_role_group_feature_id, user_role_report_group_feature_id) VALUES (1, 'ADMINISTRATOR', 1, 1, 1);
INSERT INTO USER_ROLE (id, name, user_role_business_feature_id, user_role_group_feature_id, user_role_report_group_feature_id) VALUES (2, 'ACCOUNTANT', 1, 1, 1);
INSERT INTO USER_ROLE (id, name, user_role_business_feature_id, user_role_group_feature_id, user_role_report_group_feature_id) VALUES (3, 'ADMIN_USER', 1, 1, 1);
INSERT INTO USER_ROLE (id, name, user_role_business_feature_id, user_role_group_feature_id, user_role_report_group_feature_id) VALUES (4, 'USER', 2, 2, 2);
INSERT INTO USER_ROLE (id, name, user_role_business_feature_id, user_role_group_feature_id, user_role_report_group_feature_id) VALUES (5, 'ROLE_CHANGE_PASSWORD', null, 2, null);
INSERT INTO USER_ROLE (id, name, user_role_business_feature_id, user_role_group_feature_id, user_role_report_group_feature_id) VALUES (6, 'EXCHANGE', 3, 2, 2);
INSERT INTO USER_ROLE (id, name, user_role_business_feature_id, user_role_group_feature_id, user_role_report_group_feature_id) VALUES (7, 'VIP_USER', 4, 2, 2);
INSERT INTO USER_ROLE (id, name, user_role_business_feature_id, user_role_group_feature_id, user_role_report_group_feature_id) VALUES (8, 'TRADER', 5, 2, 3);
INSERT INTO USER_ROLE (id, name, user_role_business_feature_id, user_role_group_feature_id, user_role_report_group_feature_id) VALUES (9, 'FIN_OPERATOR', 1, 1, 1);
INSERT INTO USER_ROLE (id, name, user_role_business_feature_id, user_role_group_feature_id, user_role_report_group_feature_id) VALUES (10, 'BOT_TRADER', 6, 3, 4);
INSERT INTO USER_ROLE (id, name, user_role_business_feature_id, user_role_group_feature_id, user_role_report_group_feature_id) VALUES (11, 'ICO_MARKET_MAKER', 7, 2, 2);
INSERT INTO USER_ROLE (id, name, user_role_business_feature_id, user_role_group_feature_id, user_role_report_group_feature_id) VALUES (12, 'OUTER_MARKET_BOT', 9, 2, 4);