ALTER TABLE WITHDRAW_REQUEST ADD COLUMN merchant_image_id INT(11) NULL AFTER merchant_id;

ALTER TABLE WITHDRAW_REQUEST ADD CONSTRAINT fk_merchant_image_id FOREIGN KEY (merchant_image_id) REFERENCES MERCHANT_IMAGE(id);