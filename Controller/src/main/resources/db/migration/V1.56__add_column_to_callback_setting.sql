ALTER TABLE CALLBACK_SETTINGS ADD COLUMN `PAIR_ID` INT(20);

ALTER TABLE CALLBACK_SETTINGS ADD FOREIGN KEY (`PAIR_ID`) REFERENCES CURRENCY_PAIR(id);

ALTER TABLE CALLBACK_SETTINGS DROP INDEX id_UNIQUE;
