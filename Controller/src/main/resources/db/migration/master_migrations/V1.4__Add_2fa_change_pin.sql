#ALTER TABLE USER ADD COLUMN change_2fa_setting_pin varchar(100);

INSERT IGNORE INTO 2FA_NOTIFICATION_MESSAGES (event, type, message) VALUES ('CHANGE_2FA_SETTING', 'EMAIL', 'response.change2fa.pin.email');