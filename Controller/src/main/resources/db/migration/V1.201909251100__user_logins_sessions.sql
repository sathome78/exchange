create table if not exists USER_SESSIONS
(
    user_id int(40) not null,
    device varchar(100) not null,
    user_agent varchar(100) not null,
    os varchar(100) not null,
    ip varchar(45) not null,
    country varchar(100) not null,
    region varchar(100) not null,
    city varchar(100) not null,
    token varchar(500) not null,
    started timestamp default CURRENT_TIMESTAMP,
    modified timestamp default CURRENT_TIMESTAMP,
    PRIMARY KEY (token, user_agent),
    unique index user_sessions_token_user_agent_uindex (token, user_agent),
    constraint user_session_user_fk
        foreign key (user_id) references USER (id)
            on delete cascade
);
