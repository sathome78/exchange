CREATE TABLE IF NOT EXISTS USER_VERIFICATION_INFO (
  user_id INT NOT NULL,
  document_type ENUM ('PASSPORT', 'IDENTITY_CARD', 'DRIVER_LICENSE') DEFAULT 'PASSPORT',
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  born DATE NOT NULL,
  residential_address VARCHAR(255),
  postal_code VARCHAR(10),
  country VARCHAR(50),
  city VARCHAR(50),
  path    varchar(64) not null,
  PRIMARY KEY (user_id, document_type),
  FOREIGN KEY (user_id) REFERENCES USER(id) ON DELETE NO ACTION ON UPDATE NO ACTION
)DEFAULT CHARACTER SET = utf8;