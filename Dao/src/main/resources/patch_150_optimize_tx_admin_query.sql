CREATE TABLE INVOICE_OPERATION_DIRECTION
(
  id INT PRIMARY KEY AUTO_INCREMENT,
  name ENUM('REFILL', 'WITHDRAW') NOT NULL
);
CREATE UNIQUE INDEX INVOICE_OPERATION_DIRECTION_name_uindex ON INVOICE_OPERATION_DIRECTION (name);

INSERT INTO INVOICE_OPERATION_DIRECTION (name) VALUES ('REFILL'), ('WITHDRAW');

ALTER TABLE USER_CURRENCY_INVOICE_OPERATION_PERMISSION ADD operation_direction_id INT NULL;
ALTER TABLE USER_CURRENCY_INVOICE_OPERATION_PERMISSION
  ADD CONSTRAINT u_op_perm_op_direction_id_fk
FOREIGN KEY (operation_direction_id) REFERENCES INVOICE_OPERATION_DIRECTION (id);

UPDATE USER_CURRENCY_INVOICE_OPERATION_PERMISSION SET operation_direction_id = 1 WHERE operation_direction = 'REFILL';
UPDATE USER_CURRENCY_INVOICE_OPERATION_PERMISSION SET operation_direction_id = 2 WHERE operation_direction = 'WITHDRAW';

CREATE TABLE OPERATION_TYPE_DIRECTION
(
  operation_type_id INT,
  operation_direction_id INT,
  PRIMARY KEY (operation_type_id, operation_direction_id),
  CONSTRAINT op_type_dir__fk_ot_id FOREIGN KEY (operation_type_id) REFERENCES OPERATION_TYPE (id),
  CONSTRAINT op_type_dir__fk_op_dir_id FOREIGN KEY (operation_direction_id) REFERENCES INVOICE_OPERATION_DIRECTION (id)
);

INSERT INTO OPERATION_TYPE_DIRECTION VALUES
  (1, 1),
  (2, 2),
  (5, 1),
  (5, 2),
  (8, 1);

CREATE INDEX tx__idx_wlt_optype_source_type
  ON transaction (user_wallet_id, operation_type_id, source_type);

CREATE INDEX transaction__datetime
  ON transaction (datetime);