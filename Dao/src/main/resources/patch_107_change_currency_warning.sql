
DROP TABLE CURRENCY_WARNING;

CREATE TABLE birzha.currency_warning
(
  id INT PRIMARY KEY,
  currency_id INT NOT NULL,
  phrase_template_id INT NOT NULL,
  warning_type ENUM('SINGLE_ADDRESS', 'TIMEOUT') NOT NULL,
  CONSTRAINT currency_warning___fk_cur_id FOREIGN KEY (currency_id) REFERENCES currency (id),
  CONSTRAINT currency_warning___fk_phrase FOREIGN KEY (phrase_template_id) REFERENCES phrase_template (id)
);
CREATE UNIQUE INDEX currency_warning__uindex_uq ON birzha.currency_warning (currency_id, warning_type);

INSERT INTO CURRENCY_WARNING VALUES (1, 4, 5, 'SINGLE_ADDRESS');
INSERT INTO CURRENCY_WARNING VALUES (2, 9, 4, 'SINGLE_ADDRESS');

INSERT INTO PHRASE_TEMPLATE (template, topic_id) VALUES
  ('timeout.warning.EDR', 3);

INSERT INTO CURRENCY_WARNING VALUES (3, 9, 6, 'TIMEOUT');