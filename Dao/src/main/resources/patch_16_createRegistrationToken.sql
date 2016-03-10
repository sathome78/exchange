CREATE TABLE IF NOT EXISTS `Birzha`.`REGISTRATION_TOKEN` (
  `id` INT(40) NOT NULL AUTO_INCREMENT,
  `value` VARCHAR(45) NOT NULL,
  `user_id` INT(40) NOT NULL,
  `expired` TINYINT(1) NOT NULL DEFAULT 0,
  `date_creation` TIMESTAMP NULL DEFAULT now(),
  PRIMARY KEY (`id`),
  INDEX `fk_REGISTRATION_TOKEN_USER1_idx` (`user_id` ASC),
  CONSTRAINT `fk_REGISTRATION_TOKEN_USER1`
    FOREIGN KEY (`user_id`)
    REFERENCES `Birzha`.`USER` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB



INSERT DATABASE_PATCH VALUES('patch_16_createRegistrationToken',default,1);