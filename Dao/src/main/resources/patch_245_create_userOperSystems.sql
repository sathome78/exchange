CREATE TABLE `USER_OPER_SYSTEMS` (
  `user_id` INT NOT NULL,
  `operating_system` VARCHAR(100) NULL,
  INDEX `user_id_idx` (`user_id` ASC),
  CONSTRAINT ``
  FOREIGN KEY (`user_id`)
  REFERENCES `USER` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

INSERT INTO TOKEN_TYPE (id, name) VALUES (5, 'confirmNewOS');