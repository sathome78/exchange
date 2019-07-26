ALTER TABLE USER ADD COLUMN qubera_account_pin VARCHAR(100) AFTER api_token_setting_pin;

INSERT IGNORE INTO 2FA_NOTIFICATION_MESSAGES (event, type, message) VALUES ('QUBERA_ACCOUNT', 'EMAIL', 'response.token.pin.email');
