-- MYSQL EXAMPLE
-- CREATE TABLE if not exists exrates.API_AUTH_TOKEN (
--     id bigint(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
--     username varchar(45) NOT NULL,
--     value varchar(64) NOT NULL,
--     last_request datetime DEFAULT CURRENT_TIMESTAMP
-- );
-- HSQLDB example
create table API_AUTH_TOKEN
(
	id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	username varchar(45) not null,
	value varchar(64) not null,
	last_request datetime default CURRENT_TIMESTAMP null
);
