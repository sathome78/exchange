ALTER TABLE QUBERA_RESPONSE_LOG MODIFY messageId int(11);
ALTER TABLE QUBERA_RESPONSE_LOG MODIFY currency VARCHAR (10);

ALTER TABLE QUBERA_RESPONSE_LOG DROP PRIMARY KEY;
ALTER TABLE QUBERA_RESPONSE_LOG MODIFY paymentId BIGINT (20);

ALTER TABLE QUBERA_RESPONSE_LOG ADD id BIGINT(20) NOT NULL auto_increment PRIMARY KEY;
