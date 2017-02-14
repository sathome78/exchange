CREATE TABLE `INVOICE_BANK` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `currency_id` int(11) DEFAULT NULL,
  `name` varchar(60) DEFAULT NULL,
  `account_number` varchar(60) DEFAULT NULL,
  `recipient` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `invoice_bank___fk_currency` (`currency_id`),
  CONSTRAINT `invoice_bank___fk_currency` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO INVOICE_BANK (currency_id, name, account_number, recipient) VALUES 
  ((SELECT id FROM CURRENCY WHERE name = 'IDR'), 'BCA', '3150963141', 'Nanda Rizal Pahlewi');
INSERT INTO INVOICE_BANK (currency_id, name, account_number, recipient) VALUES
  ((SELECT id FROM CURRENCY WHERE name = 'IDR'), 'MANDIRI', '1440099965557', 'Nanda Rizal Pahlewi');
INSERT INTO INVOICE_BANK (currency_id, name, account_number, recipient) VALUES
  ((SELECT id FROM CURRENCY WHERE name = 'IDR'), 'BNI', '0483087786', 'Nanda Rizal Pahlewi');
INSERT INTO INVOICE_BANK (currency_id, name, account_number, recipient) VALUES
  ((SELECT id FROM CURRENCY WHERE name = 'IDR'), 'BRI', '057901000435567', 'Nanda Rizal Pahlewi');

ALTER TABLE INVOICE_REQUEST ADD remark VARCHAR(300) NULL;
ALTER TABLE INVOICE_REQUEST ADD user_full_name VARCHAR(250) NULL;
ALTER TABLE INVOICE_REQUEST ADD bank_id INT NULL;
ALTER TABLE INVOICE_REQUEST
  MODIFY COLUMN acceptance_time DATETIME AFTER user_full_name,
  MODIFY COLUMN acceptance_user_id INT(11) AFTER user_full_name;
ALTER TABLE INVOICE_REQUEST ADD payer_bank_name VARCHAR(50) NULL;
ALTER TABLE INVOICE_REQUEST ADD payer_account VARCHAR(100) NULL;
ALTER TABLE INVOICE_REQUEST CHARACTER SET utf8;
ALTER TABLE INVOICE_REQUEST MODIFY remark VARCHAR(300) CHARACTER SET utf8;
ALTER TABLE INVOICE_REQUEST MODIFY user_full_name VARCHAR(250) CHARACTER SET utf8;
ALTER TABLE INVOICE_REQUEST MODIFY payer_bank_name VARCHAR(200) CHARACTER SET utf8;
ALTER TABLE INVOICE_REQUEST MODIFY payer_account VARCHAR(100) CHARACTER SET utf8;

CREATE TABLE `currency_input_bank` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `currency_id` int(11) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `code` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `currency_input_bank___fk_cur_id` (`currency_id`),
  CONSTRAINT `currency_input_bank___fk_cur_id` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK BCA', '014');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK MANDIRI', '008');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK BNI', '009');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK BRI', '002');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'AMERICAN EXPRESS BANK LTD', '030');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'ANGLOMAS INTERNASIONAL BANK', '531');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'ANZ PANIN BANK', '061');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK ABN AMRO', '052');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK AGRO NIAGA', '494');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK AKITA', '525');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK ALFINDO', '503');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK ANTARDAERAH', '088');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK ARTA NIAGA KENCANA', '020');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK ARTHA GRAHA', '037');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK ARTOS IND', '542');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK BENGKULU', '133');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK BII', '016');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK BINTANG MANUNGGAL', '484');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK BISNIS INTERNASIONAL', '459');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK BNP PARIBAS INDONESIA', '057');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK BUANA IND', '023');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK BUKOPIN', '441');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK BUMI ARTA', '076');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK BUMIPUTERA', '485');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK CAPITAL INDONESIA, TBK.', '054');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK CENTURY, TBK.', '095');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK CHINA TRUST INDONESIA', '949');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK COMMONWEALTH', '950');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK CREDIT AGRICOLE INDOSUEZ', '039');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK DANAMON', '011');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK DBS INDONESIA', '046');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK DIPO INTERNATIONAL', '523');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK DKI', '111');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK EKONOMI', '087');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK EKSEKUTIF', '558');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK EKSPOR INDONESIA', '003');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK FAMA INTERNASIONAL', '562');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK FINCONESIA', '945');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK GANESHA', '161');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK HAGA', '089');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK HAGAKITA', '159');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK HARDA', '567');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK HARFA', '517');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK HARMONI INTERNATIONAL', '166');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK HIMPUNAN SAUDARA 1906, TBK.', '212');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK IFI', '093');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK INA PERDANA', '513');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK INDEX SELINDO', '555');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK INDOMONEX', '498');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK JABAR', '110');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK BRI SYARIAH', '422');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK JASA JAKARTA', '427');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK JASA JAKARTA', '472');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK JATENG', '113');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK JATIM', '114');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK KEPPEL TATLEE BUANA', '053');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK KESAWAN', '167');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK KESEJAHTERAAN EKONOMI', '535');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK LAMPUNG', '121');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK LIPPO', '026');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK MALUKU', '131');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK MASPION', '157');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK MAYAPADA', '097');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK MAYBANK INDOCORP', '947');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK MAYORA', '553');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK MEGA', '426');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK MERINCORP', '946');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK MESTIKA', '151');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK METRO EXPRESS', '152');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK MITRANIAGA', '491');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK MIZUHO INDONESIA', '048');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK MUAMALAT', '147');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK MULTI ARTA SENTOSA', '548');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK MULTICOR TBK.', '036');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK NAGARI', '118');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK NIAGA', '022');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK NISP', '028');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK NTT', '130');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK NUSANTARA PARAHYANGAN', '145');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK OCBC – INDONESIA', '948');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK OF AMERICA, N.A', '033');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK OF CHINA LIMITED', '069');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK PANIN', '019');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK PERSYARIKATAN INDONESIA', '521');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK PURBA DANARTA', '547');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK RESONA PERDANIA', '047');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK RIAU', '119');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK ROYAL INDONESIA', '501');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK SHINTA INDONESIA', '153');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK SINAR HARAPAN BALI', '564');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK SRI PARTHA', '466');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK SULTRA', '135');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK SULUT', '127');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK SUMITOMO MITSUI INDONESIA', '045');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK SUMSEL', '120');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK SUMUT', '117');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK SWADESI', '146');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK SWAGUNA', '405');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK SYARIAH MANDIRI', '451');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK SYARIAH MEGA', '506');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK TABUNGAN NEGARA (PERSERO) (BTN)', '200');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK TABUNGAN PENSIUNAN NASIONAL', '213');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK UIB', '536');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK UOB INDONESIA', '058');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK VICTORIA INTERNATIONAL', '566');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK WINDU KENTJANA', '162');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK WOORI INDONESIA', '068');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BANK YUDHA BHAKTI', '490');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BPD ACEH', '116');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BPD BALI', '129');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BPD DIY', '112');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BPD JAMBI', '115');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BPD KALIMANTAN BARAT', '123');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BPD KALSEL', '122');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BPD KALTENG', '125');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BPD KALTIM', '124');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BPD NTB', '128');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BPD PAPUA', '132');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BPD SULAWESI TENGAH', '134');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'BPD SULSEL', '126');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'CENTRATAMA NASIONAL BANK', '559');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'CITIBANK N.A.', '031');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'DEUTSCHE BANK AG.', '067');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'HALIM INDONESIA BANK', '164');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'ING INDONESIA BANK', '034');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'JP. MORGAN CHASE BANK, N.A.', '032');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'KOREA EXCHANGE BANK DANAMON', '059');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'LIMAN INTERNATIONAL BANK', '526');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'PERMATA BANK', '013');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'PRIMA MASTER BANK', '520');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'RABOBANK INTERNASIONAL INDONESIA', '060');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'STANDARD CHARTERED BANK', '050');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'THE BANGKOK BANK COMP. LTD', '040');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'THE BANK OF TOKYO MITSUBISHI UFJ LTD', '042');
INSERT INTO CURRENCY_INPUT_BANK (currency_id, name, code) VALUES (10, 'THE HONGKONG & SHANGHAI B.C.', '041');

ALTER TABLE INVOICE_REQUEST ADD receipt_scan VARCHAR(100) NULL;