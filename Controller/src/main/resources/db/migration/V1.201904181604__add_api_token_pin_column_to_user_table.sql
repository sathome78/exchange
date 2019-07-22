ALTER TABLE USER ADD COLUMN api_token_setting_pin VARCHAR(100) AFTER change_2fa_setting_pin;

INSERT IGNORE INTO 2FA_NOTIFICATION_MESSAGES (event, type, message) VALUES ('API_TOKEN_SETTING', 'EMAIL', 'response.token.pin.email');