CREATE TABLE 2FA_GOOGLE_AUTHENTICATOR (
  `user_id` INT NOT NULL,
  `enable` BOOLEAN,
  `secret_code` VARCHAR(45) NULL,
  INDEX `user_id_idx` (`user_id` ASC),
    FOREIGN KEY (`user_id`)
    REFERENCES `birzha`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
DEFAULT CHARACTER SET = utf8;