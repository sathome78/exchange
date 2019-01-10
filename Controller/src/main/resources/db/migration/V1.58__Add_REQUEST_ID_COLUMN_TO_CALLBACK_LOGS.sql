
SET @dbname = DATABASE();
SET @tablename = 'CALLBACK_LOGS';
SET @columnname = 'REQUEST_ID';
SET @preparedStatement = (SELECT IF(
                                       (
                                         SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                                         WHERE
                                           (table_name = @tablename)
                                           AND (table_schema = @dbname)
                                           AND (column_name = @columnname)
                                       ) > 0,
                                       'SELECT 1',
                                       CONCAT('ALTER TABLE ', @tablename, ' ADD ', @columnname, ' INT PRIMARY KEY AUTO_INCREMENT;')
                                   ));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;


alter table CALLBACK_LOGS modify REQUEST_ID int auto_increment first;
