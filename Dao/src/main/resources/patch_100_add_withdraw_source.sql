INSERT INTO TRANSACTION_SOURCE_TYPE(id, name) VALUES (9, 'WITHDRAW');
ALTER TABLE TRANSACTION MODIFY source_type
  ENUM('ORDER', 'MERCHANT', 'REFERRAL', 'ACCRUAL', 'MANUAL', 'USER_TRANSFER', 'INVOICE', 'BTC_INVOICE', 'WITHDRAW');


SELECT COUNT(*) FROM TRANSACTION JOIN WITHDRAW_REQUEST ON WITHDRAW_REQUEST.transaction_id = TRANSACTION.id;

UPDATE TRANSACTION
  JOIN WITHDRAW_REQUEST ON WITHDRAW_REQUEST.transaction_id = TRANSACTION.id
SET TRANSACTION.source_type = 'WITHDRAW', TRANSACTION.source_id = WITHDRAW_REQUEST.transaction_id;

ALTER TABLE USER_COMMENT MODIFY users_comment varchar(400) CHARACTER SET utf8 NOT NULL;
