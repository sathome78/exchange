ALTER TABLE MERCHANT_CURRENCY
	ADD COLUMN withdraw_auto_enabled TINYINT NOT NULL DEFAULT '0',
	ADD COLUMN withdraw_auto_delay INT NOT NULL DEFAULT '0',
	ADD COLUMN withdraw_auto_threshold DOUBLE(40,9) NOT NULL DEFAULT '0';