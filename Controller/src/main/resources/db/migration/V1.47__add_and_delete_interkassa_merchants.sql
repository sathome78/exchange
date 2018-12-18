DELETE FROM MERCHANT_IMAGE WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = 'Interkassa') AND currency_id = (SELECT id FROM CURRENCY WHERE name = 'USD');

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'Interkassa'), '/client/img/merchants/master-card.png', 'Master Card', (SELECT id FROM CURRENCY WHERE name = 'USD'));

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'Interkassa'), '/client/img/merchants/visa-card.png', 'Visa', (SELECT id FROM CURRENCY WHERE name = 'USD'));

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'Interkassa'), '/client/img/merchants/mir.png', 'Mir Payment', (SELECT id FROM CURRENCY WHERE name = 'USD'));