ALTER TABLE TRANSACTION
CHANGE merchant_id merchant_id INT(11);

ALTER TABLE TRANSACTION
ADD COLUMN order_id INT(11) DEFAULT NULL;

ALTER TABLE TRANSACTION
ADD CONSTRAINT `fk_EXORDERS` FOREIGN KEY (`order_id`) REFERENCES `exorders` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

INSERT INTO DATABASE_PATCH VALUES ('patch_33_update_table_transactions', DEFAULT, 1);

COMMIT;
