DROP TABLE IF EXISTS IEO_SUBSCRIBE;


CREATE TABLE IEO_SUBSCRIBE
(
  email              VARCHAR(255) PRIMARY KEY               NOT NULL,
  email_subscribe    TINYINT(1) DEFAULT 0                   NOT NULL,
  telegram_subscribe TINYINT(1) DEFAULT 0                   NOT NULL
);
