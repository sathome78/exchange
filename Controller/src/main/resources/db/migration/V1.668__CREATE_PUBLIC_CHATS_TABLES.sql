CREATE TABLE IF NOT EXISTS PUBLIC_CHAT_EN (
  id           INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
  nickname      VARCHAR(100)      DEFAULT 'anonymous',
  body         varchar(256) not null,
  message_time datetime     DEFAULT current_timestamp
) charset = utf8;

# INSERT PUBLIC_CHAT_EN
# (nickname, body, message_time)
#   SELECT u.email, c.body, c.message_time FROM CHAT_EN as c
#     INNER JOIN USER as u on c.user_id = u.id;

CREATE TABLE IF NOT EXISTS PUBLIC_CHAT_AR (
  id           INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
  nickname      VARCHAR(100)      DEFAULT 'anonymous',
  body         varchar(256) not null,
  message_time datetime     DEFAULT current_timestamp
) charset = utf8;

# INSERT PUBLIC_CHAT_AR
# (nickname, body, message_time)
#   SELECT u.email, c.body, c.message_time FROM CHAT_AR as c
#     INNER JOIN USER as u on c.user_id = u.id;


CREATE TABLE IF NOT EXISTS PUBLIC_CHAT_CN (
  id           INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
  nickname      VARCHAR(100)      DEFAULT 'anonymous',
  body         varchar(256) not null,
  message_time datetime     DEFAULT current_timestamp
) charset = utf8;

# INSERT PUBLIC_CHAT_CN
# (nickname, body, message_time)
#   SELECT u.email, c.body, c.message_time FROM CHAT_CN as c
#     INNER JOIN USER as u on c.user_id = u.id;


CREATE TABLE IF NOT EXISTS PUBLIC_CHAT_IN (
  id           INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
  nickname      VARCHAR(100)      DEFAULT 'anonymous',
  body         varchar(256) not null,
  message_time datetime     DEFAULT current_timestamp
) charset = utf8;

# INSERT PUBLIC_CHAT_IN
# (nickname, body, message_time)
#   SELECT u.email, c.body, c.message_time FROM CHAT_IN as c
#     INNER JOIN USER as u on c.user_id = u.id;

CREATE TABLE IF NOT EXISTS PUBLIC_CHAT_RU (
  id           INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
  nickname      VARCHAR(100)      DEFAULT 'anonymous',
  body         varchar(256) not null,
  message_time datetime     DEFAULT current_timestamp
) charset = utf8;

# INSERT PUBLIC_CHAT_RU
# (nickname, body, message_time)
#   SELECT u.email, c.body, c.message_time FROM CHAT_RU as c
#     INNER JOIN USER as u on c.user_id = u.id;