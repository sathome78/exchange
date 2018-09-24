DROP TABLE IF EXISTS user_api;

CREATE TABLE user_api
(
  user_id INT NOT NULL,
  attempts  INT NOT NULL,
  CONSTRAINT user_api_user_id_fk FOREIGN KEY (user_id) REFERENCES user (id)
);