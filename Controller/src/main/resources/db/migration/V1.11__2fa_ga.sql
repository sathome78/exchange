INSERT IGNORE INTO 2FA_NOTIFICATOR VALUES (4, 'google2faNotificatorServiceImpl', 'FREE', true, 'GOOGLE_AUTHENTICATOR');

INSERT IGNORE INTO `2FA_NOTIFICATION_MESSAGES` (event, type, message) VALUES ('LOGIN', 'GOOGLE2FA', 'response.login.code.google2fa');
INSERT IGNORE INTO `2FA_NOTIFICATION_MESSAGES` (event, type, message) VALUES ('WITHDRAW', 'GOOGLE2FA', 'response.withdraw.code.google2fa');
INSERT IGNORE INTO `2FA_NOTIFICATION_MESSAGES` (event, type, message) VALUES ('TRANSFER', 'GOOGLE2FA', 'response.transfer.code.google2fa');


CREATE TABLE IF NOT EXISTS 2FA_GOOGLE_AUTHENTICATOR (
  `user_id` INT NOT NULL,
  `enable` BOOLEAN,
  `secret_code` VARCHAR(45) NULL,
  INDEX `user_id_idx` (`user_id` ASC),
    FOREIGN KEY (`user_id`)
    REFERENCES USER (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
DEFAULT CHARACTER SET = utf8;

UPDATE USER SET tmp_poll_passed = false;