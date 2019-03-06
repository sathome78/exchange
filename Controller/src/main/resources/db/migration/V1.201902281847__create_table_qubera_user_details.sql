CREATE TABLE IF NOT EXISTS QUBERA_USER_DETAILS (
  user_id        INTEGER      NOT NULL,
  currency_id    INTEGER      NOT NULL,
  account_number VARCHAR(50)  NOT NULL,
  iban           VARCHAR(100) NOT NULL
)
  ENGINE InnoDB;


ALTER TABLE QUBERA_USER_DETAILS
  ADD CONSTRAINT uq_user_id_and_currency_id_qubera_user_details
      UNIQUE (user_id, currency_id),
  ADD CONSTRAINT fk_qubera_user_details_on_users
      FOREIGN KEY (user_id) REFERENCES USER (id) ON DELETE CASCADE,
  ADD CONSTRAINT fk_qubera_user_details_on_currencies
      FOREIGN KEY (currency_id) REFERENCES CURRENCY (id) ON DELETE CASCADE;
