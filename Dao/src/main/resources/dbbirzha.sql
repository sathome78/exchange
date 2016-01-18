SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `Birzha` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `Birzha` ;

-- -----------------------------------------------------
-- Table `Birzha`.`USER`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Birzha`.`USER` (
  `id` INT(40) NOT NULL AUTO_INCREMENT,
  `nickname` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `regdate` TIMESTAMP NOT NULL DEFAULT now(),
  `phone` INT(40) NULL,
  `finpassword` VARCHAR(100) NULL,
  `status` VARCHAR(45) NOT NULL DEFAULT 'active',
  `ipaddress` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `idusers_UNIQUE` (`id` ASC),
  UNIQUE INDEX `nickname_UNIQUE` (`nickname` ASC),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Birzha`.`CURRENCY`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Birzha`.`CURRENCY` (
  `id` INT(40) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL,
  `commission` DOUBLE(40,5) NULL,
  `description` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `ID_cur_UNIQUE` (`id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Birzha`.`WALLET`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Birzha`.`WALLET` (
  `id` INT(40) NOT NULL AUTO_INCREMENT,
  `currency_id` INT(40) NOT NULL,
  `user_id` INT(40) NOT NULL,
  `active_balance` DOUBLE(40,9) NULL,
  `reserved_balance` DOUBLE(40,9) NULL,
  `WALLETScol` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_purse_UNIQUE` (`id` ASC),
  INDEX `fk_WALLET_CURRENCIES1_idx` (`currency_id` ASC),
  INDEX `fk_WALLET_USERS1_idx` (`user_id` ASC),
  CONSTRAINT `fk_WALLET_CURRENCIES1`
    FOREIGN KEY (`currency_id`)
    REFERENCES `Birzha`.`CURRENCY` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_WALLET_USERS1`
    FOREIGN KEY (`user_id`)
    REFERENCES `Birzha`.`USER` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Birzha`.`ORDER`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Birzha`.`ORDER` (
  `id` INT(40) NOT NULL AUTO_INCREMENT,
  `wallet_id_from` INT(40) NOT NULL,
  `amountFrom` DOUBLE(40,9) NOT NULL,
  `wallet_id_to` INT(40) NULL,
  `currency_to` INT(40) NOT NULL,
  `exchange_rate` DOUBLE(40,9) NOT NULL,
  `date_creation` TIMESTAMP NOT NULL DEFAULT now(),
  `date_final` DATETIME NULL,
  `status` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_order_UNIQUE` (`id` ASC),
  INDEX `fk_ORDERS_WALLET1_idx` (`wallet_id_from` ASC),
  INDEX `fk_ORDER_CURRENCY1_idx` (`currency_to` ASC),
  CONSTRAINT `fk_ORDERS_WALLET1`
    FOREIGN KEY (`wallet_id_from`)
    REFERENCES `Birzha`.`WALLET` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ORDER_CURRENCY1`
    FOREIGN KEY (`currency_to`)
    REFERENCES `Birzha`.`CURRENCY` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Birzha`.`COMPANY_ACCOUNT`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Birzha`.`COMPANY_ACCOUNT` (
  `id` INT(40) NOT NULL AUTO_INCREMENT,
  `wallet_id` INT(40) NOT NULL,
  `amount` DOUBLE(40,5) NULL,
  `transaction_type` INT(1) NULL DEFAULT 1,
  `date` TIMESTAMP NOT NULL DEFAULT now(),
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_company_UNIQUE` (`id` ASC),
  INDEX `fk_COMPANY_ACCOUNT_WALLET1_idx` (`wallet_id` ASC),
  CONSTRAINT `fk_COMPANY_ACCOUNT_WALLET1`
    FOREIGN KEY (`wallet_id`)
    REFERENCES `Birzha`.`WALLET` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Birzha`.`IP_Log`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Birzha`.`IP_Log` (
  `id` INT(40) NOT NULL AUTO_INCREMENT,
  `ip` VARCHAR(45) NOT NULL,
  `user_id` INT(40) NOT NULL,
  `date` TIMESTAMP NOT NULL DEFAULT now(),
  PRIMARY KEY (`id`),
  INDEX `fk_IP_Logs_USERS1_idx` (`user_id` ASC),
  CONSTRAINT `fk_IP_Logs_USERS1`
    FOREIGN KEY (`user_id`)
    REFERENCES `Birzha`.`USER` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Birzha`.`USER_ROLE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Birzha`.`USER_ROLE` (
  `id` INT(40) NOT NULL AUTO_INCREMENT,
  `user_id` INT(40) NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_USER_ROLE_USER1_idx` (`user_id` ASC),
  CONSTRAINT `fk_USER_ROLE_USER1`
    FOREIGN KEY (`user_id`)
    REFERENCES `Birzha`.`USER` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
