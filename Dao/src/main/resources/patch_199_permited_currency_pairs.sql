ALTER TABLE CURRENCY_PAIR
ADD COLUMN `permitted_link` TINYINT(1) NOT NULL DEFAULT 0 AFTER `ticker_name`;

UPDATE CURRENCY_PAIR SET `permitted_link`='1' WHERE `name`='SLT/BTC';
UPDATE CURRENCY_PAIR SET `permitted_link`='1' WHERE `name`='UQC/USD';
UPDATE CURRENCY_PAIR SET `permitted_link`='1' WHERE `name`='UQC/BTC';
UPDATE CURRENCY_PAIR SET `permitted_link`='1' WHERE `name`='UQC/ETH';

