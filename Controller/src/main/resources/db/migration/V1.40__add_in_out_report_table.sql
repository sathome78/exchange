DROP TABLE IF EXISTS INPUT_OUTPUT_REPORT;

CREATE TABLE INPUT_OUTPUT_REPORT
(
  id                INT(40) UNSIGNED PRIMARY KEY   NOT NULL AUTO_INCREMENT,
  file_name         VARCHAR(64)                    NOT NULL,
  content           LONGBLOB                       NOT NULL,
  created_at        TIMESTAMP                      NOT NULL
) CHARACTER SET utf8 COLLATE utf8_general_ci;