SET @dbname = DATABASE();
SET @tablename = 'USER';
SET @columnname = 'change_2fa_setting_pin';
SET @preparedStatement = (SELECT IF(
    (
      SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
      WHERE
        (table_name = @tablename)
        AND (table_schema = @dbname)
        AND (column_name = @columnname)
    ) > 0,
    "SELECT 1",
    CONCAT('ALTER TABLE ', @tablename, ' ADD ', @columnname, ' varchar(100);')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

INSERT IGNORE INTO 2FA_NOTIFICATION_MESSAGES (event, type, message) VALUES ('CHANGE_2FA_SETTING', 'EMAIL', 'response.change2fa.pin.email');