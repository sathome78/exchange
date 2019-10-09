create table if not EXISTS EMAIL_SETTING
(
    host         varchar(255) primary key,
    email_sender varchar(255) not null
);


INSERT IGNORE INTO EMAIL_SETTING (host, email_sender) VALUE ('icloud.com', 'sendGridMailSender');
