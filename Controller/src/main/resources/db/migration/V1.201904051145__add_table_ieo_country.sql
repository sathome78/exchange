CREATE TABLE IF NOT EXISTS IEO_RESTRICTED_COUNTRY
(
  ieo_id       INT(11)     NOT NULL,
  country_code VARCHAR(64) NOT NULL,
  FOREIGN KEY (ieo_id) REFERENCES IEO_DETAILS (id),
  UNIQUE (ieo_id, country_code)
);
