ALTER TABLE COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS ADD COLUMN name VARCHAR(200) NULL AFTER currency_id;

-- ALTER TABLE COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS
-- DROP COLUMN name;