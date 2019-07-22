ALTER TABLE USER ADD COLUMN pub_id CHAR(21) UNIQUE AFTER id;

create trigger TRANSACTION_BEFORE_CREATE_USER
  before INSERT
  on USER
  for each row
  BEGIN
    IF (NEW.pub_id  IS NULL) THEN
      SET new.pub_id = substring(md5(NEW.email), 1, 20);
    END IF;
  END;

UPDATE USER SET pub_id = (substring(md5(USER.email), 1, 20));



