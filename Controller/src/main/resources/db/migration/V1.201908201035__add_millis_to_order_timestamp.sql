alter table EXORDERS modify date_acception timestamp(6) null;
alter table EXORDERS modify status_modification_date timestamp(6) null;

LOCK TABLES EXORDERS WRITE;
DROP TRIGGER EXORDERS_BEFORE_UPD_TR;
DELIMITER $$;
create trigger EXORDERS_BEFORE_UPD_TR
    before UPDATE
    on exorders
    for each row
BEGIN
    IF (NEW.status_id <> OLD.status_id) THEN
        SET new.status_modification_date = CURRENT_TIMESTAMP(6);
    END IF;
END $$;
DELIMITER ;
UNLOCK TABLES;

