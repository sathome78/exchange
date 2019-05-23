DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE `Alter_Table`()
BEGIN

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'MERCHANT_IMAGE'
                     AND column_name = 'input_commission')  THEN

        ALTER TABLE MERCHANT_IMAGE ADD input_commission DOUBLE(5,2) DEFAULT 0.00 NOT NULL;

    END IF;

END ;;
DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE `Alter_Table`;

UPDATE MERCHANT_IMAGE SET input_commission = 7.00
 WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = 'Interkassa')
 AND currency_id = (SELECT id FROM CURRENCY WHERE name = 'USD')
 AND child_merchant = 'mastercard';

UPDATE MERCHANT_IMAGE SET input_commission = 7.00
 WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = 'Interkassa')
 AND currency_id = (SELECT id FROM CURRENCY WHERE name = 'EUR')
 AND child_merchant = 'mastercard';

UPDATE MERCHANT_IMAGE SET input_commission = 5.00
 WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = 'Interkassa')
 AND currency_id = (SELECT id FROM CURRENCY WHERE name = 'UAH')
 AND child_merchant = 'mastercard';

UPDATE MERCHANT_IMAGE SET input_commission = 6.50
 WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = 'Interkassa')
 AND currency_id = (SELECT id FROM CURRENCY WHERE name = 'RUB')
 AND child_merchant = 'mastercard';


UPDATE MERCHANT_IMAGE SET input_commission = 7.00
 WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = 'Interkassa')
 AND currency_id = (SELECT id FROM CURRENCY WHERE name = 'USD')
 AND child_merchant = 'visa';

UPDATE MERCHANT_IMAGE SET input_commission = 7.00
 WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = 'Interkassa')
 AND currency_id = (SELECT id FROM CURRENCY WHERE name = 'EUR')
 AND child_merchant = 'visa';

UPDATE MERCHANT_IMAGE SET input_commission = 5.00
 WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = 'Interkassa')
 AND currency_id = (SELECT id FROM CURRENCY WHERE name = 'UAH')
 AND child_merchant = 'visa';

UPDATE MERCHANT_IMAGE SET input_commission = 6.50
 WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = 'Interkassa')
 AND currency_id = (SELECT id FROM CURRENCY WHERE name = 'RUB')
 AND child_merchant = 'visa';


UPDATE MERCHANT_IMAGE SET input_commission = 6.50
 WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = 'Interkassa')
 AND currency_id = (SELECT id FROM CURRENCY WHERE name = 'RUB')
 AND child_merchant = 'mir';


UPDATE MERCHANT_IMAGE SET input_commission = 10.50
 WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = 'Interkassa')
 AND currency_id = (SELECT id FROM CURRENCY WHERE name = 'RUB')
 AND child_merchant = 'yandexmoney';


UPDATE MERCHANT_IMAGE SET input_commission = 8.00
 WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = 'Interkassa')
 AND currency_id = (SELECT id FROM CURRENCY WHERE name = 'RUB')
 AND child_merchant = 'alfaclick';


UPDATE MERCHANT_IMAGE SET input_commission = 5.00
 WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = 'Interkassa')
 AND currency_id = (SELECT id FROM CURRENCY WHERE name = 'UAH')
 AND child_merchant = 'privat24';
