DROP TABLE IF EXISTS TRADES;

CREATE TABLE IF NOT EXISTS TRADES
(
  id                        INT PRIMARY KEY   NOT NULL AUTO_INCREMENT,
  order_id                  INT NOT NULL,
  user_maker_id             INT NOT NULL,
  user_taker_id             INT NOT NULL,
  currency_pair_id          INT NOT NULL,
  order_type_id             INT NOT NULL,
  amount_base               DECIMAL (30, 8) NOT NULL,
  amount_convert            DECIMAL (30, 8) NOT NULL,
  exrate                    DECIMAL (30, 8) NOT NULL,
  commission_maker_amount   DECIMAL (30, 8) NOT NULL,
  comission_maker_id        INT NOT NULL,
  commission_taker_amount   DECIMAL (30, 8) NOT NULL,
  commission_taker_id       INT NOT NULL,
  date_of_tarde             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT trades_order_id_orders_id_fk FOREIGN KEY (order_id) REFERENCES ORDERS (id),
  CONSTRAINT trades_user_maker_id_user_id_fk FOREIGN KEY (user_maker_id) REFERENCES USER (id),
  CONSTRAINT trades_user_taker_id_user_id_fk FOREIGN KEY (user_taker_id) REFERENCES USER (id),
  CONSTRAINT fk_trades_currency_pair_id_currency_pair_id_fk FOREIGN KEY (currency_pair_id) REFERENCES CURRENCY_PAIR (id),
  CONSTRAINT fk_trades_order_type_id_order_type_id_fk FOREIGN KEY (order_type_id) REFERENCES ORDER_TYPE (id),
  CONSTRAINT fk_trades_commission_maker_id_commission_id_fk FOREIGN KEY (comission_maker_id) REFERENCES COMMISSION (id),
  CONSTRAINT trades_commission_taker_id_commission_id_fk FOREIGN KEY (commission_taker_id) REFERENCES COMMISSION (id)
);