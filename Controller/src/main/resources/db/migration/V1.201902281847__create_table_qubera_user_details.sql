CREATE TABLE IF NOT EXISTS QUBERA_USER_DETAILS (
  user_id        INTEGER      NOT NULL,
  currency_id    INTEGER      NOT NULL,
  account_number VARCHAR(50)  NOT NULL,
  iban           VARCHAR(100) NOT NULL
)
  ENGINE InnoDB;

DROP PROCEDURE IF EXISTS `Add_Constraint`;

DELIMITER $$
CREATE PROCEDURE `Add_Constraint`()
  BEGIN
    IF NOT EXISTS( SELECT `TABLE_SCHEMA`, `TABLE_NAME`
                   FROM `information_schema`.`KEY_COLUMN_USAGE`
                   WHERE `CONSTRAINT_NAME` IN ('uq_user_id_and_currency_id_qubera_user_details'))  THEN

      ALTER TABLE `QUBERA_USER_DETAILS`
        ADD CONSTRAINT `uq_user_id_and_currency_id_qubera_user_details` UNIQUE (user_id, currency_id);
    END IF;

    IF NOT EXISTS( SELECT `TABLE_SCHEMA`, `TABLE_NAME`
                   FROM `information_schema`.`KEY_COLUMN_USAGE`
                   WHERE `CONSTRAINT_NAME` IN ('fk_qubera_user_details_on_users'))  THEN

      ALTER TABLE `QUBERA_USER_DETAILS`
        ADD CONSTRAINT `fk_qubera_user_details_on_users` FOREIGN KEY (user_id) REFERENCES USER (id) ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS( SELECT `TABLE_SCHEMA`, `TABLE_NAME`
                   FROM `information_schema`.`KEY_COLUMN_USAGE`
                   WHERE `CONSTRAINT_NAME` IN ('fk_qubera_user_details_on_currencies'))  THEN

      ALTER TABLE `QUBERA_USER_DETAILS`
        ADD CONSTRAINT `fk_qubera_user_details_on_currencies` FOREIGN KEY (currency_id) REFERENCES CURRENCY (id) ON DELETE CASCADE;
    END IF;

  END $$
DELIMITER ;


CALL Add_Constraint();

DROP PROCEDURE `Add_Constraint`;
