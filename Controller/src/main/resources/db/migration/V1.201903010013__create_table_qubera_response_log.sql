CREATE TABLE IF NOT EXISTS QUBERA_RESPONSE_LOG (
  paymentId       INTEGER      PRIMARY KEY NOT NULL,
  messageId       INTEGER      NOT NULL,
  accountIBAN     VARCHAR(150) NOT NULL,
  accountNumber   VARCHAR(150) NOT NULL,
  processingTime  TIMESTAMP    NOT NULL,
  received        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  state           VARCHAR(150) NOT NULL,
  currency        VARCHAR(10)  NOT NULL,
  paymentAmount   FLOAT,
  transferType    VARCHAR(150),
  rejectionReason TEXT
)
  ENGINE InnoDB;
