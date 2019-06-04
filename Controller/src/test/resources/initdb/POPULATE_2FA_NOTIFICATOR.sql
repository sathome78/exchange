INSERT INTO `2FA_NOTIFICATOR` (id, bean_name, pay_type, enable, name) VALUES (1, 'emailNotificatorServiceImpl', 'FREE', 1, 'E-MAIL');
INSERT INTO `2FA_NOTIFICATOR` (id, bean_name, pay_type, enable, name) VALUES (2, 'smsNotificatorServiceImpl', 'PAY_FOR_EACH', 0, 'SMS');
INSERT INTO `2FA_NOTIFICATOR` (id, bean_name, pay_type, enable, name) VALUES (3, 'telegramNotificatorServiceImpl', 'PREPAID_LIFETIME', 1, 'TELEGRAM');
INSERT INTO `2FA_NOTIFICATOR` (id, bean_name, pay_type, enable, name) VALUES (4, 'google2faNotificatorServiceImpl', 'FREE', 1, 'GOOGLE_AUTHENTICATOR');