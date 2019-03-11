DROP TABLE IF EXISTS ORDERS;

CREATE TABLE IF NOT EXISTS ORDERS
(
  id                              INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  user_id                         INT NOT NULL,
  currency_pair_id                INT NOT NULL,
  order_type_id                   INT NOT NULL,
  amount_base                     DECIMAL (20, 8) NOT NULL,
  amount_convert                  DECIMAL (20, 8) NOT NULL,
  amount_accepted                 DECIMAL (20, 8) DEFAULT NULL,
  amount_available                DECIMAL (20, 8) NOT NULL,
  exrate                          DECIMAL (20, 8) NOT NULL,
  commission_maker_fixed_amount   DECIMAL (20, 8) NOT NULL,
  commission_id                    INT NOT NULL,
  order_status_id                 INT NOT NULL,
  date_of_creation                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  date_of_last_update             TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  base_type                       enum('LIMIT', 'ICO'),


  CONSTRAINT trades_user_id_user_id_fk FOREIGN KEY (user_id) REFERENCES USER (id),
  CONSTRAINT trades_currency_pair_id_currency_pair_id_fk FOREIGN KEY (currency_pair_id) REFERENCES CURRENCY_PAIR (id),
  CONSTRAINT trades_order_type_id_order_type_id_fk FOREIGN KEY (order_type_id) REFERENCES ORDER_TYPE (id),
  CONSTRAINT trades_commission_maker_id_commission_id_fk FOREIGN KEY (commission_id) REFERENCES COMMISSION (id),
  CONSTRAINT trades_order_status_id_order_status_id_fk FOREIGN KEY (order_status_id) REFERENCES ORDER_STATUS (id)
);


INSERT IGNORE INTO ORDER_STATUS (id, name, description) VALUES (8, 'partially_accepted', 'order is partially accepted')