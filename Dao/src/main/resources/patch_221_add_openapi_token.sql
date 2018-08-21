-- auto-generated definition
CREATE TABLE OPEN_API_USER_TOKEN
(
  id              BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  user_id         INT                                 NOT NULL,
  alias           VARCHAR(40)                         NOT NULL,
  public_key      VARCHAR(60)                         NOT NULL,
  private_key     VARCHAR(60)                         NOT NULL,
  date_generation TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  is_active       TINYINT(1) DEFAULT '1'              NOT NULL,
  allow_trade     TINYINT(1) DEFAULT '0'              NOT NULL,
  allow_withdraw  TINYINT(1) DEFAULT '0'              NOT NULL,
  CONSTRAINT open_api_user_token_public_key_uindex
  UNIQUE (public_key),
  CONSTRAINT open_api_user_token___fk_user_id
  FOREIGN KEY (user_id) REFERENCES user (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
)
  ENGINE = InnoDB;

CREATE INDEX open_api_user_token___fk_user_id
  ON open_api_user_token (user_id);

CREATE INDEX open_api_user_token__idx_is_active_user_id
  ON open_api_user_token (is_active, user_id);

