CREATE TABLE IF NOT EXISTS REFERRAL
(
    id         INT(11) PRIMARY KEY NOT NULL,
    name       VARCHAR(255)        NOT NULL,
    link       VARCHAR(255)        NOT NULL,
    created_at TIMESTAMP           NOT NULL default NOW()
);

DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
BEGIN
    IF NOT EXISTS(SELECT NULL
                  FROM INFORMATION_SCHEMA.COLUMNS
                  WHERE table_name = 'USER'
                    AND column_name = 'ref_id') THEN
        ALTER TABLE USER
            ADD COLUMN ref_id INT(11) NULL;
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
            ADD CONSTRAINT `fk_ref_id_on_referral_table` FOREIGN KEY (ref_id) REFERENCES REFERRAL (id);
    END IF;

END $$
DELIMITER ;

CALL Add_Constraint();

DROP PROCEDURE `Add_Constraint`;

CREATE TABLE IF NOT EXISTS REFERRAL_CURRENCY_LIMIT
(
    id          INT(11) PRIMARY KEY NOT NULL,
    currency_id INT(11)             NOT NULL,
    threshold   double(40, 9)       NOT NULL,
    FOREIGN KEY (currency_id) REFERENCES CURRENCY (id)
);

CREATE TABLE IF NOT EXISTS REFERRAL_TRANSACTION
(
    id            INT(11) PRIMARY KEY NOT NULL,
    currency_id   INT(11)             NOT NULL,
    currency_name VARCHAR(45)         NOT NULL,
    user_id       INT(11)             NOT NULL,
    amount        double(40, 9)       NOT NULL,
    FOREIGN KEY (currency_id) REFERENCES CURRENCY (id),
    FOREIGN KEY (user_id) REFERENCES USER (id)
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



