CREATE INDEX composite_index_exorder_1
  ON EXORDERS(status_id, operation_type_id, currency_pair_id, user_id, user_acceptor_id);