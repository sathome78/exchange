CREATE TABLE IF NOT EXISTS REFERRAL_LINK
(
    user_id    INT(11)             NOT NULL,
    name       VARCHAR(255)        NOT NULL,
    link       VARCHAR(64) UNIQUE NOT NULL,
    created_at TIMESTAMP           NOT NULL default NOW(),
    main       tinyint(1)                   DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES USER (id),
    PRIMARY KEY (user_id, link)
);

DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
BEGIN
    IF NOT EXISTS(SELECT NULL
                  FROM INFORMATION_SCHEMA.COLUMNS
                  WHERE table_name = 'USER'
                    AND column_name = 'invite_referral_link') THEN
        ALTER TABLE USER
            ADD COLUMN invite_referral_link VARCHAR(64) NULL;
    END IF;
END ;;

DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;

DROP PROCEDURE IF EXISTS `Add_Constraint`;

DELIMITER $$
CREATE PROCEDURE `Add_Constraint`()
BEGIN
    IF NOT EXISTS(SELECT `TABLE_SCHEMA`, `TABLE_NAME`
                  FROM `information_schema`.`KEY_COLUMN_USAGE`
                  WHERE `CONSTRAINT_NAME` IN ('fk_ref_id_on_referral_table')) THEN
        ALTER TABLE `USER`
            ADD CONSTRAINT `fk_ref_id_on_referral_table` FOREIGN KEY (invite_referral_link) REFERENCES REFERRAL_LINK (link);
    END IF;

END $$
DELIMITER ;

CALL Add_Constraint();

DROP PROCEDURE `Add_Constraint`;

DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
BEGIN
    IF NOT EXISTS(SELECT NULL
                  FROM INFORMATION_SCHEMA.COLUMNS
                  WHERE table_name = 'CURRENCY'
                    AND column_name = 'cup_income') THEN
        ALTER TABLE CURRENCY
            ADD COLUMN cup_income double(40, 9) NULL ;
    END IF;

    IF NOT EXISTS(SELECT NULL
                  FROM INFORMATION_SCHEMA.COLUMNS
                  WHERE table_name = 'CURRENCY'
                    AND column_name = 'manual_confirm_above_sum') THEN
        ALTER TABLE CURRENCY
            ADD COLUMN manual_confirm_above_sum double(40, 9) NULL ;
    END IF;

END ;;

DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;

CREATE TABLE IF NOT EXISTS REFERRAL_TRANSACTION
(
    id            INT(11) PRIMARY KEY AUTO_INCREMENT NOT NULL,
    currency_id   INT(11)                            NOT NULL,
    currency_name VARCHAR(45)                        NULL,
    user_from     INT(11)                            NOT NULL,
    user_to       INT(11)                            NOT NULL,
    amount        VARCHAR(255)                       NOT NULL,
    created_at    TIMESTAMP                          NOT NULL default NOW(),
    FOREIGN KEY (currency_id) REFERENCES CURRENCY (id),
    FOREIGN KEY (user_from) REFERENCES USER (id),
    FOREIGN KEY (user_to) REFERENCES USER (id)
);

CREATE TABLE IF NOT EXISTS REFERRAL_REQUESTS
(
    id             INT(11) PRIMARY KEY AUTO_INCREMENT NOT NULL,
    currency_id    INT(11)                            NOT NULL,
    user_id        INT(11)                            NOT NULL,
    amount         double(40, 9)                      NOT NULL,
    order_id       INT(11)                            NOT NULL,
    process_status enum ('CREATED', 'PROCESSED', 'ERROR')      DEFAULT 'CREATED',
    created_at     TIMESTAMP                          NOT NULL default NOW(),
    FOREIGN KEY (currency_id) REFERENCES CURRENCY (id),
    FOREIGN KEY (user_id) REFERENCES USER (id)
);

CREATE TABLE IF NOT EXISTS REFERRAL_REQUEST_TRANSFER
(
    id                       INT(11) PRIMARY KEY AUTO_INCREMENT NOT NULL,
    currency_id              INT(11)                            NOT NULL,
    currency_name            VARCHAR(45)                        NOT NULL,
    user_id                  INT(11)                            NOT NULL,
    amount                   double(40, 9)                      NOT NULL,
    status                   enum ('CREATED_USER', 'WAITING_MANUAL_POSTING', 'WAITING_AUTO_POSTING', 'POSTED_MANUAL', 'POSTED_AUTO', 'IN_POSTING', 'DECLINED_ANALYTICS_MANUAL', 'DECLINED_ERROR'),
    transaction_id           INT(11)                            NULL,
    created_at               timestamp                          NOT NULL default NOW(),
    remark                   VARCHAR(2048)                      NULL,
    status_modification_date timestamp                          NULL,
    FOREIGN KEY (currency_id) REFERENCES CURRENCY (id),
    FOREIGN KEY (user_id) REFERENCES USER (id),
    FOREIGN KEY (transaction_id) REFERENCES TRANSACTION (id)
);

DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
BEGIN
    IF NOT EXISTS(SELECT NULL
                  FROM INFORMATION_SCHEMA.COLUMNS
                  WHERE table_name = 'WALLET'
                    AND column_name = 'referral_balance') THEN
        ALTER TABLE WALLET
            ADD COLUMN referral_balance double(40, 9) default 0.000000000 null;
    END IF;
END ;;

DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;



