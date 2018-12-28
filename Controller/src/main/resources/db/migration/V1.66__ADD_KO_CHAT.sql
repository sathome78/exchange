create table if not exists CHAT_KO
(
  id           int          not null
    primary key,
  user_id      int          not null,
  body         varchar(256) not null,
  message_time datetime     not null,
  constraint CHAT_KO_ibfk_1
    foreign key (user_id) references USER (id)
      on update cascade
);


ALTER TABLE CHAT_KO ADD INDEX (user_id);
