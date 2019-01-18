CREATE TABLE IF NOT EXISTS METHOD_METRICS
(
  id                      INT UNSIGNED PRIMARY KEY   NOT NULL AUTO_INCREMENT,
  method_key              VARCHAR(128)               NOT NULL,
  invocation_counter      INT,
  error_counter           INT,
  average_execution_time  NUMERIC(10, 2),
  created_at              DATE                       NOT NULL
);