CREATE TABLE NOTIFICATOR
(
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `bean_name` VARCHAR(256) NOT NULL,
  `pay_type` ENUM('FREE', 'PREPAID_LIFETIME', 'PAY_FOR_EACH') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO NOTIFICATOR VALUES (1, 'emailNotificatorServiceImpl', 'FREE');
INSERT INTO NOTIFICATOR VALUES (2, 'smsNotificatorServiceImpl', 'PAY_FOR_EACH');

CREATE TABLE NOTIFICATION_MESSAGES
(
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `event` VARCHAR(64) NOT NULL,
  `type` VARCHAR(64) NOT NULL,
  `message` VARCHAR(512) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE NOTIFICATION_PRICE
(
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `notificator_id` INT NOT NULL,
  `role_id` INT NOT NULL,
  `pay_event` ENUM('SUBSCRIBE', 'LOOKUP', 'BUY_ONE') NOT NULL,
  `price` DECIMAL(40,9) NOT NULL,
  UNIQUE(`notificator_id`),
  INDEX `NOTIFICATION_PRICE_notificator_id` (`notificator_id`),
  CONSTRAINT `fk_NOTIFICATION_PRICE_notificator_id` FOREIGN KEY (`notificator_id`) REFERENCES `NOTIFICATOR` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE TELEGRAM_SUBSCRIPTION
(
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `code` VARCHAR(64),
  `subscription_state` ENUM('SUBSCRIBED', 'WAIT_FOR_SUBSCRIBE') NOT NULL ,
  `user_account`  VARCHAR(64),
  `chat_id` LONG,
  UNIQUE(`user_id`),
  INDEX `TELEGRAM_SUBSCRIPTION_user_id` (`user_id`),
  CONSTRAINT `fk_TELEGRAM_SUBSCRIPTION_user_id` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE SMS_SUBSCRIPTION
(
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `contact` VARCHAR(64),
  UNIQUE(`user_id`),
  INDEX `SMS_SUBSCRIPTION_user_id` (`user_id`),
  CONSTRAINT `fk_SMS_SUBSCRIPTION_user_id` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE USER_NOTIFICATION_MESSAGE_SETTINGS
(
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `user_id`  INT NOT NULL,
  `notificator_id`  INT,
  `event_name` ENUM('LOGIN', 'WITHDRAW', 'TRANSFER') NOT NULL,
  UNIQUE (`user_id`, `event_name`),
  INDEX `USER_NOTIFICATION_MESSAGE_SETTINGS_user_id` (`user_id`),
  INDEX `USER_NOTIFICATION_MESSAGE_SETTINGS_notificator_id` (`notificator_id`),
  CONSTRAINT `fk_USER_NOTIFICATION_MESSAGE_SETTINGS_user_id` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`),
  CONSTRAINT `fk_USER_NOTIFICATION_MESSAGE_SETTINGS_notificator_id` FOREIGN KEY (`notificator_id`) REFERENCES `NOTIFICATOR` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE USER ADD COLUMN `withdraw_pin` VARCHAR(100) NULL;
ALTER TABLE USER ADD COLUMN `transfer_pin` VARCHAR(100) NULL;
ALTER TABLE USER CHANGE `pin` `login_pin` VARCHAR(100) NULL;

INSERT INTO USER_NOTIFICATION_MESSAGE_SETTINGS (user_id, notificator_id, event_name)
  SELECT id, IF(use2fa = 0, NULL, 1) AS new_val, 'LOGIN' FROM USER;