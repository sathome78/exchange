CREATE TABLE IF NOT EXISTS CURRENCY_PAIR_RESTRICTION (
    currency_pair_id INT NOT NULL,
    restriction_name ENUM ('ESCAPE_USA'),
    FOREIGN KEY currency_pair_id_idx (currency_pair_id) REFERENCES CURRENCY_PAIR (id),
    UNIQUE (currency_pair_id, restriction_name)
);


DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
BEGIN


    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'USER' AND column_name = 'verification_required')  THEN

        ALTER TABLE USER ADD COLUMN verification_required tinyint(1) not null default false;
    END IF;

END;;

DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;



