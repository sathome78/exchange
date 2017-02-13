CREATE TABLE INVOICE_REQUEST_STATUS (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(100) NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

INSERT INTO INVOICE_REQUEST_STATUS (id, name)
VALUES 
(1, "CREATED_USER"),
(2, "CONFIRMED_USER"),
(3, "REVOKED_USER"),
(4, "ACCEPTED_ADMIN"),
(5, "DECLINED_ADMIN"),
(6, "EXPIRED");