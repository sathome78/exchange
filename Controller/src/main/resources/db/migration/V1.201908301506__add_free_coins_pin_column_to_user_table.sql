DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
  BEGIN
    IF NOT EXISTS(SELECT NULL
                  FROM INFORMATION_SCHEMA.COLUMNS
                  WHERE table_name = 'USER' AND column_name = 'free_coins_pin')
    THEN
      ALTER TABLE USER ADD COLUMN free_coins_pin VARCHAR(100) AFTER qubera_account_pin;
    END IF;

  END;;
DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;

INSERT IGNORE INTO 2FA_NOTIFICATION_MESSAGES (event, type, message) VALUES ('FREE_COINS', 'EMAIL', 'response.freecoins.pin.email');
