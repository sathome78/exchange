ALTER TABLE TRANSACTION ADD COLUMN provided BOOL DEFAULT 0 NOT NULL;
UPDATE TRANSACTION SET provided = 1;