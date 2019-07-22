DELETE FROM MERCHANT_IMAGE WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = 'Interkassa') AND currency_id = (SELECT id FROM CURRENCY WHERE name = 'USD');

DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER $$
CREATE PROCEDURE `Alter_Table`()
BEGIN

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'MERCHANT_IMAGE'
                     AND column_name = 'child_merchant')  THEN

        ALTER TABLE MERCHANT_IMAGE ADD child_merchant varchar(40) DEFAULT '' NULL;

    END IF;

END $$
DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE `Alter_Table`;

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, child_merchant, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'Interkassa'), '/client/img/merchants/master-card.png', 'Master Card', 'mastercard', (SELECT id FROM CURRENCY WHERE name = 'USD'));

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, child_merchant, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'Interkassa'), '/client/img/merchants/visa-card.png', 'Visa', 'visa', (SELECT id FROM CURRENCY WHERE name = 'USD'));

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, child_merchant, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'Interkassa'), '/client/img/merchants/qiwi.png', 'Qiwi Payment', 'qiwi', (SELECT id FROM CURRENCY WHERE name = 'USD'));
