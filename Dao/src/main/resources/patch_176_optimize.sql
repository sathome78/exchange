CREATE INDEX currency_pair__cur1_idx ON CURRENCY_PAIR (currency1_id);
CREATE INDEX currency_pair__cur2_idx ON CURRENCY_PAIR (currency2_id);

DROP INDEX currency_pair ON EXORDERS;
DROP INDEX status ON EXORDERS;


CREATE INDEX exorders_cp_status_user_counter_eo_idx ON EXORDERS (currency_pair_id, status_id, user_id, counter_order_id);