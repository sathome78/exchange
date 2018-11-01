DROP TABLE IF EXISTS BALANCES_REPORT;

CREATE TABLE BALANCES_REPORT
(
  id                INT(40) UNSIGNED PRIMARY KEY   NOT NULL AUTO_INCREMENT,
  file_name         VARCHAR(64)                    NOT NULL,
  content           BLOB                           NOT NULL,
  created_at        DATE                           NOT NULL
);