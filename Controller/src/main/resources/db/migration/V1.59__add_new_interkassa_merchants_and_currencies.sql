DELETE FROM MERCHANT_IMAGE WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = 'Interkassa') AND currency_id = (SELECT id FROM CURRENCY WHERE name = 'USD');

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, child_merchant, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'Interkassa'), '/client/img/merchants/master-card.png', 'Master Card', 'mastercard', (SELECT id FROM CURRENCY WHERE name = 'USD'));

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, child_merchant, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'Interkassa'), '/client/img/merchants/visa-card.png', 'Visa', 'visa', (SELECT id FROM CURRENCY WHERE name = 'USD'));


DELETE FROM MERCHANT_IMAGE WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = 'Interkassa') AND currency_id = (SELECT id FROM CURRENCY WHERE name = 'EUR');

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, child_merchant, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'Interkassa'), '/client/img/merchants/master-card.png', 'Master Card', 'mastercard', (SELECT id FROM CURRENCY WHERE name = 'EUR'));

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, child_merchant, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'Interkassa'), '/client/img/merchants/visa-card.png', 'Visa', 'visa', (SELECT id FROM CURRENCY WHERE name = 'EUR'));


DELETE FROM MERCHANT_IMAGE WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = 'Interkassa') AND currency_id = (SELECT id FROM CURRENCY WHERE name = 'UAH');

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, child_merchant, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'Interkassa'), '/client/img/merchants/master-card.png', 'Master Card', 'mastercard', (SELECT id FROM CURRENCY WHERE name = 'UAH'));

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, child_merchant, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'Interkassa'), '/client/img/merchants/visa-card.png', 'Visa', 'visa', (SELECT id FROM CURRENCY WHERE name = 'UAH'));

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, child_merchant, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'Interkassa'), '/client/img/merchants/privat24.png', 'Privat24 Payment', 'privat24', (SELECT id FROM CURRENCY WHERE name = 'UAH'));


DELETE FROM MERCHANT_IMAGE WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = 'Interkassa') AND currency_id = (SELECT id FROM CURRENCY WHERE name = 'RUB');

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, child_merchant, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'Interkassa'), '/client/img/merchants/mir.png', 'Mir Payment', 'mir', (SELECT id FROM CURRENCY WHERE name = 'RUB'));

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, child_merchant, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'Interkassa'), '/client/img/merchants/master-card.png', 'Master Card', 'mastercard', (SELECT id FROM CURRENCY WHERE name = 'RUB'));

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, child_merchant, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'Interkassa'), '/client/img/merchants/visa-card.png', 'Visa', 'visa', (SELECT id FROM CURRENCY WHERE name = 'RUB'));

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, child_merchant, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'Interkassa'), '/client/img/merchants/yandexmoney.png', 'Yandex Money', 'yandexmoney', (SELECT id FROM CURRENCY WHERE name = 'RUB'));

INSERT IGNORE INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, child_merchant, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'Interkassa'), '/client/img/merchants/alfaclick.png', 'Alfaclick Payment', 'alfaclick', (SELECT id FROM CURRENCY WHERE name = 'RUB'));


INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, withdraw_block, refill_block, transfer_block)
VALUES ((SELECT id FROM MERCHANT WHERE name = 'Interkassa'), (select id from CURRENCY where name = 'UAH'), 100.00, 1, 1, 1);

INSERT IGNORE INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, withdraw_block, refill_block, transfer_block)
VALUES ((SELECT id FROM MERCHANT WHERE name = 'Interkassa'), (select id from CURRENCY where name = 'RUB'), 200.00, 1, 1, 1);

UPDATE CURRENCY SET hidden = 0 WHERE id = 7;