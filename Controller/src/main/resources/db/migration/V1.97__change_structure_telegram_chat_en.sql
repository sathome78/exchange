DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE `Alter_Table`()
BEGIN

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'TELEGRAM_CHAT_EN'
                     AND column_name = 'message_id')  THEN

        ALTER TABLE TELEGRAM_CHAT_EN ADD COLUMN message_id INT(11) NOT NULL AFTER id;

    END IF;

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'TELEGRAM_CHAT_EN'
                     AND column_name = 'message_reply_id')  THEN

        ALTER TABLE TELEGRAM_CHAT_EN ADD COLUMN message_reply_id INT(11) NOT NULL AFTER message_time;

    END IF;

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'TELEGRAM_CHAT_EN'
                     AND column_name = 'telegram_user_id')  THEN

        ALTER TABLE TELEGRAM_CHAT_EN ADD COLUMN telegram_user_id INT(11) NOT NULL AFTER chat_id;

    END IF;

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'TELEGRAM_CHAT_EN'
                     AND column_name = 'telegram_user_reply_id')  THEN

        ALTER TABLE TELEGRAM_CHAT_EN ADD COLUMN telegram_user_reply_id INT(11) NOT NULL AFTER message_time;

    END IF;

END ;;
DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE `Alter_Table`;
