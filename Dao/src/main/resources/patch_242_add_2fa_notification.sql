INSERT INTO 2FA_NOTIFICATOR VALUES (4, 'google2faNotificatorServiceImpl', 'FREE', true, 'GOOGLE AUTHENTICATOR');

INSERT INTO `2FA_NOTIFICATION_MESSAGES` (event, type, message) VALUES ('LOGIN', 'GOOGLE2FA', 'response.login.code.google2fa');
INSERT INTO `2FA_NOTIFICATION_MESSAGES` (event, type, message) VALUES ('WITHDRAW', 'GOOGLE2FA', 'response.withdraw.code.google2fa');
INSERT INTO `2FA_NOTIFICATION_MESSAGES` (event, type, message) VALUES ('TRANSFER', 'GOOGLE2FA', 'response.transfer.code.google2fa');

ALTER TABLE `USER` ADD COLUMN `2FA_SECRET` VARCHAR(255) NULL AFTER `2FA_LAST_NOTIFY_DATE`;
