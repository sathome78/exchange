CREATE TABLE IF NOT EXISTS CALLBACK_LOGS(
  user_id int(40),
  request_date DATETIME,
  response_date DATETIME,
  request_json TEXT,
  response_json TEXT,
  response_code int,
  FOREIGN KEY (user_id) REFERENCES USER (id)
)CHARACTER SET utf8 COLLATE utf8_general_ci;