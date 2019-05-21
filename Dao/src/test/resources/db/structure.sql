SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS `2FA_GOOGLE_AUTHENTICATOR`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `2FA_GOOGLE_AUTHENTICATOR` (
  `user_id` int(11) NOT NULL,
  `enable` tinyint(1) DEFAULT NULL,
  `secret_code` varchar(45) DEFAULT NULL,
  UNIQUE KEY `2Fa_google_authenticator_user_id_uq` (`user_id`),
  KEY `user_id_idx` (`user_id`),
  CONSTRAINT `2FA_GOOGLE_AUTHENTICATOR_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `2FA_NOTIFICATION_MESSAGES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `2FA_NOTIFICATION_MESSAGES` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `event` varchar(64) NOT NULL,
  `type` varchar(64) NOT NULL,
  `message` varchar(512) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `2FA_NOTIFICATION_PRICE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `2FA_NOTIFICATION_PRICE` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `notificator_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  `message_price` decimal(40,9) DEFAULT NULL,
  `subscribe_price` decimal(40,9) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `notificator_id` (`notificator_id`,`role_id`),
  KEY `2FA_NOTIFICATION_PRICE_notificator_id` (`notificator_id`),
  KEY `2FA_NOTIFICATION_PRICE_role_id` (`role_id`),
  CONSTRAINT `fk_2FA_NOTIFICATION_PRICE_notificator_id` FOREIGN KEY (`notificator_id`) REFERENCES `2FA_NOTIFICATOR` (`id`),
  CONSTRAINT `fk_2FA_NOTIFICATION_PRICE_role_id` FOREIGN KEY (`role_id`) REFERENCES `USER_ROLE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;


--
-- Table structure for table `2FA_NOTIFICATOR`
--

DROP TABLE IF EXISTS `2FA_NOTIFICATOR`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `2FA_NOTIFICATOR` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bean_name` varchar(256) NOT NULL,
  `pay_type` enum('FREE','PREPAID_LIFETIME','PAY_FOR_EACH') NOT NULL,
  `enable` tinyint(1) NOT NULL DEFAULT '1',
  `name` varchar(256) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

--
-- Table structure for table `2FA_USER_NOTIFICATION_MESSAGE_SETTINGS`
--

DROP TABLE IF EXISTS `2FA_USER_NOTIFICATION_MESSAGE_SETTINGS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `2FA_USER_NOTIFICATION_MESSAGE_SETTINGS` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `notificator_id` int(11) DEFAULT NULL,
  `event_name` enum('LOGIN','WITHDRAW','TRANSFER') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`,`event_name`),
  KEY `2FA_USER_NOTIFICATION_MESSAGE_SETTINGS_user_id` (`user_id`),
  KEY `2FA_USER_NOTIFICATION_MESSAGE_SETTINGS_notificator_id` (`notificator_id`),
  CONSTRAINT `fk_2FA_USER_NOTIFICATION_MESSAGE_SETTINGS_notificator_id` FOREIGN KEY (`notificator_id`) REFERENCES `2FA_NOTIFICATOR` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_2FA_USER_NOTIFICATION_MESSAGE_SETTINGS_user_id` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=167869 DEFAULT CHARSET=utf8;


--
-- Table structure for table `ADMIN_AUTHORITY`
--

DROP TABLE IF EXISTS `ADMIN_AUTHORITY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `ADMIN_AUTHORITY` (
  `id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  `hidden` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `ADMIN_AUTHORITY_ROLE_DEFAULTS`
--

DROP TABLE IF EXISTS `ADMIN_AUTHORITY_ROLE_DEFAULTS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `ADMIN_AUTHORITY_ROLE_DEFAULTS` (
  `role_id` int(11) NOT NULL,
  `admin_authority_id` int(11) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`role_id`,`admin_authority_id`),
  KEY `fk_auth_admin_auth_id_idx` (`admin_authority_id`),
  CONSTRAINT `fk_auth_admin_auth_id` FOREIGN KEY (`admin_authority_id`) REFERENCES `ADMIN_AUTHORITY` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_auth_user_role_id` FOREIGN KEY (`role_id`) REFERENCES `USER_ROLE` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `API_AUTH_TOKEN`
--

DROP TABLE IF EXISTS `API_AUTH_TOKEN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `API_AUTH_TOKEN` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `value` varchar(64) NOT NULL,
  `last_request` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=251 DEFAULT CHARSET=latin1;


--
-- Table structure for table `API_PARAMS`
--

DROP TABLE IF EXISTS `API_PARAMS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `API_PARAMS` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `param_name` varchar(40) NOT NULL,
  `param_value` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `PARAM_NAME` (`param_name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;


--
-- Table structure for table `API_TEMP_PASSWORD`
--

DROP TABLE IF EXISTS `API_TEMP_PASSWORD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `API_TEMP_PASSWORD` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `password` varchar(100) NOT NULL,
  `date_creation` timestamp NULL DEFAULT NULL,
  `temporal_token_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `temporal_token_id` (`temporal_token_id`),
  CONSTRAINT `API_TEMP_PASSWORD_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `API_TEMP_PASSWORD_ibfk_2` FOREIGN KEY (`temporal_token_id`) REFERENCES `TEMPORAL_TOKEN` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2508 DEFAULT CHARSET=latin1;


--
-- Table structure for table `BALANCES_REPORT`
--

DROP TABLE IF EXISTS `BALANCES_REPORT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `BALANCES_REPORT` (
  `id` int(40) unsigned NOT NULL AUTO_INCREMENT,
  `file_name` varchar(64) NOT NULL,
  `content` longblob NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4199 DEFAULT CHARSET=utf8;


--
-- Table structure for table `BCH_HARD_FORK_BALANCE_SNAPSHOT`
--

DROP TABLE IF EXISTS `BCH_HARD_FORK_BALANCE_SNAPSHOT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `BCH_HARD_FORK_BALANCE_SNAPSHOT` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `user_id` int(40) NOT NULL,
  `active_balance` double(40,9) NOT NULL DEFAULT '0.000000000',
  `reserved_balance` double(40,9) NOT NULL DEFAULT '0.000000000',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `user_id_UNIQUE` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=27340003 DEFAULT CHARSET=utf8;


--
-- Table structure for table `BOT_LAUNCH_SETTINGS`
--

DROP TABLE IF EXISTS `BOT_LAUNCH_SETTINGS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `BOT_LAUNCH_SETTINGS` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bot_trader_id` int(11) NOT NULL,
  `currency_pair_id` int(11) NOT NULL,
  `is_enabled` tinyint(1) NOT NULL DEFAULT '0',
  `consider_user_orders` tinyint(1) NOT NULL DEFAULT '0',
  `launch_interval_minutes` int(11) NOT NULL DEFAULT '60',
  `create_timeout_seconds` int(11) NOT NULL DEFAULT '3',
  `quantity_per_sequence` int(11) NOT NULL DEFAULT '15',
  PRIMARY KEY (`id`),
  UNIQUE KEY `bot_launch_settings__uindex_bot_cp_ot` (`bot_trader_id`,`currency_pair_id`),
  KEY `bot_launch_settings___fk_currency_pair` (`currency_pair_id`),
  CONSTRAINT `bot_launch_settings___fk_bot_id` FOREIGN KEY (`bot_trader_id`) REFERENCES `BOT_TRADER` (`id`),
  CONSTRAINT `bot_launch_settings___fk_currency_pair` FOREIGN KEY (`currency_pair_id`) REFERENCES `CURRENCY_PAIR` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=943 DEFAULT CHARSET=utf8;


--
-- Table structure for table `BOT_TRADER`
--

DROP TABLE IF EXISTS `BOT_TRADER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `BOT_TRADER` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `is_enabled` tinyint(1) NOT NULL DEFAULT '0',
  `order_accept_timeout` int(11) NOT NULL DEFAULT '5',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


--
-- Table structure for table `BOT_TRADING_SETTINGS`
--

DROP TABLE IF EXISTS `BOT_TRADING_SETTINGS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `BOT_TRADING_SETTINGS` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bot_launch_settings_id` int(11) NOT NULL,
  `order_type_id` int(11) NOT NULL,
  `min_amount` decimal(40,9) NOT NULL DEFAULT '0.000000000',
  `max_amount` decimal(40,9) NOT NULL DEFAULT '99999999.000000000',
  `min_price` decimal(40,9) DEFAULT '0.000000000',
  `max_price` decimal(40,9) NOT NULL DEFAULT '99999999.000000000',
  `price_step` decimal(40,9) NOT NULL DEFAULT '5.000000000',
  `price_growth_direction` enum('UP','DOWN') NOT NULL DEFAULT 'UP',
  `min_price_deviation` int(11) NOT NULL DEFAULT '0',
  `max_price_deviation` int(11) NOT NULL DEFAULT '0',
  `randomize_price_step` tinyint(1) NOT NULL DEFAULT '0',
  `price_step_deviation` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `bot_trading_settings__uindex_launch_ot` (`bot_launch_settings_id`,`order_type_id`),
  KEY `bot_trading_settings___fk_order_type` (`order_type_id`),
  CONSTRAINT `bot_trading_settings___fk_launch` FOREIGN KEY (`bot_launch_settings_id`) REFERENCES `BOT_LAUNCH_SETTINGS` (`id`),
  CONSTRAINT `bot_trading_settings___fk_order_type` FOREIGN KEY (`order_type_id`) REFERENCES `ORDER_TYPE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2092 DEFAULT CHARSET=utf8;


--
-- Table structure for table `CALLBACK_LOGS`
--

DROP TABLE IF EXISTS `CALLBACK_LOGS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `CALLBACK_LOGS` (
  `REQUEST_ID` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(40) DEFAULT NULL,
  `request_date` datetime DEFAULT NULL,
  `response_date` datetime DEFAULT NULL,
  `request_json` text,
  `response_json` text,
  `response_code` int(11) DEFAULT NULL,
  PRIMARY KEY (`REQUEST_ID`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `CALLBACK_LOGS_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5386598 DEFAULT CHARSET=utf8;


--
-- Table structure for table `CALLBACK_SETTINGS`
--

DROP TABLE IF EXISTS `CALLBACK_SETTINGS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `CALLBACK_SETTINGS` (
  `USER_ID` int(11) NOT NULL,
  `CALLBACK_URL` varchar(100) NOT NULL,
  `PAIR_ID` int(20) NOT NULL,
  PRIMARY KEY (`USER_ID`,`PAIR_ID`),
  KEY `PAIR_ID` (`PAIR_ID`),
  CONSTRAINT `CALLBACK_SETTINGS_ibfk_1` FOREIGN KEY (`USER_ID`) REFERENCES `USER` (`id`),
  CONSTRAINT `CALLBACK_SETTINGS_ibfk_2` FOREIGN KEY (`PAIR_ID`) REFERENCES `CURRENCY_PAIR` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `CHAT_AR`
--

DROP TABLE IF EXISTS `CHAT_AR`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `CHAT_AR` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `body` varchar(256) NOT NULL,
  `message_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `CHAT_RU` (`user_id`),
  CONSTRAINT `CHAT_AR_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `CHAT_CN`
--

DROP TABLE IF EXISTS `CHAT_CN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `CHAT_CN` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `body` varchar(256) NOT NULL,
  `message_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `CHAT_RU` (`user_id`),
  CONSTRAINT `CHAT_CN_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `CHAT_EN`
--

DROP TABLE IF EXISTS `CHAT_EN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `CHAT_EN` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `body` varchar(256) NOT NULL,
  `message_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `CHAT_RU` (`user_id`),
  CONSTRAINT `CHAT_EN_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `CHAT_IN`
--

DROP TABLE IF EXISTS `CHAT_IN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `CHAT_IN` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `body` varchar(256) NOT NULL,
  `message_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `CHAT_RU` (`user_id`),
  CONSTRAINT `CHAT_IN_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `CHAT_KO`
--

DROP TABLE IF EXISTS `CHAT_KO`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `CHAT_KO` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `body` varchar(256) NOT NULL,
  `message_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `CHAT_KO_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `CHAT_RU`
--

DROP TABLE IF EXISTS `CHAT_RU`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `CHAT_RU` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `body` varchar(256) NOT NULL,
  `message_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `CHAT_RU` (`user_id`),
  CONSTRAINT `CHAT_RU_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `CLIENT_BANK`
--

DROP TABLE IF EXISTS `CLIENT_BANK`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `CLIENT_BANK` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `currency_id` int(11) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `code` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `currency_input_bank___fk_cur_id` (`currency_id`),
  CONSTRAINT `currency_input_bank___fk_cur_id` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=136 DEFAULT CHARSET=utf8;


--
-- Table structure for table `COMMISSION`
--

DROP TABLE IF EXISTS `COMMISSION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `COMMISSION` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `operation_type` int(40) NOT NULL,
  `value` double(40,9) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_role` int(40) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `unique_optype_user_role_idx` (`operation_type`,`user_role`),
  KEY `COMMISSION` (`operation_type`),
  KEY `commission_fk_usr_role_idx` (`user_role`),
  CONSTRAINT `commission_fk_usr_role` FOREIGN KEY (`user_role`) REFERENCES `USER_ROLE` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `commission_ibfk_1` FOREIGN KEY (`operation_type`) REFERENCES `OPERATION_TYPE` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=latin1;


--
-- Table structure for table `COMMON_REFERRAL_ROOT`
--

DROP TABLE IF EXISTS `COMMON_REFERRAL_ROOT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `COMMON_REFERRAL_ROOT` (
  `user_id` int(11) DEFAULT NULL,
  KEY `COMMON_REFERRAL_ROOT` (`user_id`),
  CONSTRAINT `COMMON_REFERRAL_ROOT_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `COMPANY_EXTERNAL_WALLET_BALANCES`
--

DROP TABLE IF EXISTS `COMPANY_EXTERNAL_WALLET_BALANCES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `COMPANY_EXTERNAL_WALLET_BALANCES` (
  `id` int(40) unsigned NOT NULL AUTO_INCREMENT,
  `currency_id` int(40) NOT NULL,
  `usd_rate` decimal(19,12) DEFAULT '0.000000000000',
  `btc_rate` decimal(19,12) DEFAULT '0.000000000000',
  `main_balance` decimal(30,8) DEFAULT '0.00000000',
  `reserved_balance` decimal(30,8) DEFAULT '0.00000000',
  `total_balance` decimal(30,8) DEFAULT '0.00000000',
  `total_balance_usd` decimal(30,8) DEFAULT '0.00000000',
  `total_balance_btc` decimal(30,8) DEFAULT '0.00000000',
  `last_updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `sign_of_certainty` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `currency_id` (`currency_id`),
  CONSTRAINT `company_external_wallet_balances_currency_id_fk` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=348 DEFAULT CHARSET=utf8;


--
-- Table structure for table `COMPANY_WALLET`
--

DROP TABLE IF EXISTS `COMPANY_WALLET`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `COMPANY_WALLET` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `currency_id` int(11) DEFAULT NULL,
  `balance` double(40,9) NOT NULL DEFAULT '0.000000000',
  `commission_balance` double(40,9) NOT NULL DEFAULT '0.000000000',
  PRIMARY KEY (`id`),
  UNIQUE KEY `currency_id` (`currency_id`),
  CONSTRAINT `company_wallet_ibfk_1` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=293 DEFAULT CHARSET=latin1;


--
-- Table structure for table `COMPANY_WALLET_EXTERNAL`
--

DROP TABLE IF EXISTS `COMPANY_WALLET_EXTERNAL`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `COMPANY_WALLET_EXTERNAL` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `currency_id` int(40) NOT NULL,
  `main_wallet_balance` double(40,9) DEFAULT '0.000000000',
  `rate_usd_additional` double(10,5) DEFAULT '0.00000',
  `reserve_wallet_balance` double(40,9) DEFAULT '0.000000000',
  `cold_wallet_balance` double(40,9) DEFAULT '0.000000000',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_WALLET_EX_CURRENCIES_idx` (`currency_id`),
  CONSTRAINT `fk_WALLET_EX_CURRENCIES` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=302 DEFAULT CHARSET=latin1;


--
-- Table structure for table `COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS`
--

DROP TABLE IF EXISTS `COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `currency_id` int(11) NOT NULL,
  `name` varchar(200) DEFAULT NULL,
  `wallet_address` varchar(128) DEFAULT NULL,
  `balance` decimal(30,8) DEFAULT '0.00000000',
  PRIMARY KEY (`id`),
  KEY `company_wallet_external_reserved_address_currency_id_fk` (`currency_id`),
  CONSTRAINT `company_wallet_external_reserved_address_currency_id_fk` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=853 DEFAULT CHARSET=utf8;


--
-- Table structure for table `CRYPTO_CORE_WALLET`
--

DROP TABLE IF EXISTS `CRYPTO_CORE_WALLET`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `CRYPTO_CORE_WALLET` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `merchant_id` int(11) DEFAULT NULL,
  `currency_id` int(11) DEFAULT NULL,
  `title_code` varchar(20) DEFAULT NULL,
  `subtract_fee_from_amount` tinyint(1) NOT NULL DEFAULT '0',
  `passphrase` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `crypto_core_wallet__index_uq` (`merchant_id`),
  KEY `crypto_core_wallet___fk_curr_id` (`currency_id`),
  CONSTRAINT `crypto_core_wallet___fk_curr_id` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`),
  CONSTRAINT `crypto_core_wallet___fk_merch_id` FOREIGN KEY (`merchant_id`) REFERENCES `MERCHANT` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=latin1;


--
-- Table structure for table `CURRENCY`
--

DROP TABLE IF EXISTS `CURRENCY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `CURRENCY` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `description` varchar(45) DEFAULT NULL,
  `hidden` tinyint(1) DEFAULT '0',
  `max_scale_for_refill` int(11) DEFAULT NULL,
  `max_scale_for_withdraw` int(11) DEFAULT NULL,
  `max_scale_for_transfer` int(11) DEFAULT NULL,
  `process_type` enum('FIAT','CRYPTO') NOT NULL DEFAULT 'CRYPTO',
  `scale` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ID_cur_UNIQUE` (`id`),
  UNIQUE KEY `currency_name_index` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=297 DEFAULT CHARSET=latin1;


--
-- Table structure for table `CURRENCY_LIMIT`
--

DROP TABLE IF EXISTS `CURRENCY_LIMIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `CURRENCY_LIMIT` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `currency_id` int(40) NOT NULL,
  `operation_type_id` int(40) NOT NULL,
  `user_role_id` int(40) NOT NULL,
  `min_sum` double(40,9) DEFAULT NULL,
  `min_sum_usd` double(40,9) NOT NULL DEFAULT '0.000000000',
  `usd_rate` decimal(19,8) NOT NULL DEFAULT '0.00000000',
  `max_sum` double(40,9) DEFAULT NULL,
  `max_daily_request` int(3) DEFAULT '10',
  `recalculate_to_usd` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `currency_limit__unique_idx` (`currency_id`,`operation_type_id`,`user_role_id`),
  KEY `currency_limit___fk_currency` (`currency_id`),
  KEY `currency_limit___fk_optype` (`operation_type_id`),
  KEY `currency_limit___fk_usr_role` (`user_role_id`),
  CONSTRAINT `currency_limit___fk_currency` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`),
  CONSTRAINT `currency_limit___fk_optype` FOREIGN KEY (`operation_type_id`) REFERENCES `OPERATION_TYPE` (`id`),
  CONSTRAINT `currency_limit___fk_usr_role` FOREIGN KEY (`user_role_id`) REFERENCES `USER_ROLE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10182 DEFAULT CHARSET=utf8;


--
-- Table structure for table `CURRENCY_PAIR`
--

DROP TABLE IF EXISTS `CURRENCY_PAIR`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `CURRENCY_PAIR` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `currency1_id` int(11) NOT NULL,
  `currency2_id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `pair_order` int(2) DEFAULT NULL,
  `hidden` tinyint(1) DEFAULT NULL,
  `market` enum('USD','BTC','FIAT','ETH','ICO','USDT') DEFAULT 'USD',
  `ticker_name` varchar(45) NOT NULL,
  `permitted_link` tinyint(1) NOT NULL DEFAULT '0',
  `type` enum('MAIN','ICO') NOT NULL DEFAULT 'MAIN',
  `scale` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `hidden` (`hidden`)
) ENGINE=InnoDB AUTO_INCREMENT=830 DEFAULT CHARSET=latin1;


--
-- Table structure for table `CURRENCY_PAIR_LIMIT`
--

DROP TABLE IF EXISTS `CURRENCY_PAIR_LIMIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `CURRENCY_PAIR_LIMIT` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `currency_pair_id` int(11) NOT NULL,
  `user_role_id` int(11) NOT NULL,
  `order_type_id` int(11) NOT NULL,
  `min_rate` decimal(40,9) NOT NULL DEFAULT '0.000000000',
  `max_rate` decimal(40,9) NOT NULL DEFAULT '99999999999.000000000',
  `min_amount` decimal(40,9) NOT NULL DEFAULT '0.000000100',
  `max_amount` decimal(40,9) NOT NULL DEFAULT '9999999999.000000000',
  PRIMARY KEY (`id`),
  UNIQUE KEY `currency_pair_limit__uq_index` (`currency_pair_id`,`user_role_id`,`order_type_id`),
  KEY `currency_pair_limit___fk_role` (`user_role_id`),
  KEY `currency_pair_limit___fk_ord_type` (`order_type_id`),
  KEY `currency_pair_limit__index_user_role_order_type` (`user_role_id`,`order_type_id`),
  CONSTRAINT `currency_pair_limit___fk_cur_pair` FOREIGN KEY (`currency_pair_id`) REFERENCES `CURRENCY_PAIR` (`id`),
  CONSTRAINT `currency_pair_limit___fk_ord_type` FOREIGN KEY (`order_type_id`) REFERENCES `ORDER_TYPE` (`id`),
  CONSTRAINT `currency_pair_limit___fk_role` FOREIGN KEY (`user_role_id`) REFERENCES `USER_ROLE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28587 DEFAULT CHARSET=latin1;


--
-- Table structure for table `CURRENT_CURRENCY_BALANCES`
--

DROP TABLE IF EXISTS `CURRENT_CURRENCY_BALANCES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `CURRENT_CURRENCY_BALANCES` (
  `id` int(40) unsigned NOT NULL AUTO_INCREMENT,
  `currency_id` int(40) NOT NULL,
  `currency_name` varchar(45) NOT NULL,
  `balance` decimal(30,8) DEFAULT '0.00000000',
  `last_updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `schedule_last_updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `currency_id` (`currency_id`),
  UNIQUE KEY `currency_name` (`currency_name`),
  CONSTRAINT `current_currency_rates_balances_currency_id_fk` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=516 DEFAULT CHARSET=latin1;


--
-- Table structure for table `CURRENT_CURRENCY_RATES`
--

DROP TABLE IF EXISTS `CURRENT_CURRENCY_RATES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `CURRENT_CURRENCY_RATES` (
  `id` int(40) unsigned NOT NULL AUTO_INCREMENT,
  `currency_id` int(40) NOT NULL,
  `currency_name` varchar(45) NOT NULL,
  `usd_rate` decimal(19,8) DEFAULT '0.00000000',
  `btc_rate` decimal(19,8) DEFAULT '0.00000000',
  `schedule_last_updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `currency_id` (`currency_id`),
  UNIQUE KEY `currency_name` (`currency_name`),
  CONSTRAINT `current_currency_rates_currency_id_fk` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=516 DEFAULT CHARSET=latin1;


--
-- Table structure for table `EXORDERS`
--

DROP TABLE IF EXISTS `EXORDERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `EXORDERS` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `user_id` int(40) NOT NULL,
  `currency_pair_id` int(11) NOT NULL,
  `operation_type_id` int(40) NOT NULL,
  `exrate` double(40,9) NOT NULL,
  `amount_base` double(40,9) NOT NULL,
  `amount_convert` double(40,9) NOT NULL,
  `commission_id` int(40) DEFAULT NULL,
  `commission_fixed_amount` double(40,9) NOT NULL,
  `user_acceptor_id` int(40) DEFAULT NULL,
  `date_creation` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_acception` timestamp NULL DEFAULT NULL,
  `status_id` int(40) NOT NULL DEFAULT '1',
  `status_modification_date` timestamp NULL DEFAULT NULL,
  `order_source_id` int(11) DEFAULT NULL,
  `counter_order_id` int(11) DEFAULT NULL,
  `base_type` enum('LIMIT','ICO') NOT NULL DEFAULT 'LIMIT',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `currency_pair` (`currency_pair_id`),
  KEY `fk_USER_ACCEPTOR` (`user_acceptor_id`),
  KEY `fk_OPERATION_TYPE` (`operation_type_id`),
  KEY `status` (`status_id`),
  KEY `commission_id` (`commission_id`),
  KEY `EXORDERS_DATE_ACCEPTION` (`date_acception`),
  KEY `EXORDERS_PAIR_STATUS_DATE_ACCEPTION` (`currency_pair_id`,`status_id`,`date_acception`),
  KEY `exorders___fk_source_id` (`order_source_id`),
  KEY `exorders__status_date_accept` (`status_id`,`date_acception`),
  KEY `composite_index_exorder_1` (`status_id`,`operation_type_id`,`currency_pair_id`,`user_id`,`user_acceptor_id`),
  CONSTRAINT `exorders___fk_source_id` FOREIGN KEY (`order_source_id`) REFERENCES `EXORDERS` (`id`),
  CONSTRAINT `fk_COMMISSION` FOREIGN KEY (`commission_id`) REFERENCES `COMMISSION` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_CURRENCY_PAIR` FOREIGN KEY (`currency_pair_id`) REFERENCES `CURRENCY_PAIR` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_OPERATION_TYPE` FOREIGN KEY (`operation_type_id`) REFERENCES `OPERATION_TYPE` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_ORDER_STATUS` FOREIGN KEY (`status_id`) REFERENCES `ORDER_STATUS` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_USER_ACCEPTOR` FOREIGN KEY (`user_acceptor_id`) REFERENCES `USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_USER_CREATOR` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=46692024 DEFAULT CHARSET=utf8;

--
-- Table structure for table `GTAG_REFILL_REQUESTS`
--

DROP TABLE IF EXISTS `GTAG_REFILL_REQUESTS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `GTAG_REFILL_REQUESTS` (
  `USER_ID` int(11) NOT NULL,
  `COUNT` int(11) DEFAULT NULL,
  PRIMARY KEY (`USER_ID`),
  CONSTRAINT `GTAG_REFILL_REQUESTS_ibfk_1` FOREIGN KEY (`USER_ID`) REFERENCES `USER` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `IEO_CLAIM`
--

DROP TABLE IF EXISTS `IEO_CLAIM`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
create table IEO_CLAIM
(
    id            int auto_increment
        primary key,
    ieo_id        int                                                                     not null,
    currency_name varchar(64)                                                             not null,
    maker_id      int                                                                     not null,
    user_id       int                                                                     not null,
    amount        double(40, 9)                                                           not null,
    rate          double(40, 9)                                                           not null,
    price_in_btc  double(40, 9)                                                           not null,
    created       timestamp                                     default CURRENT_TIMESTAMP not null,
    status        enum ('SUCCESS', 'FAILED', 'NONE', 'REVOKED') default 'NONE'            null,
    constraint IEO_CLAIM_ibfk_1 foreign key (user_id) references USER (id),
    constraint IEO_CLAIM_ibfk_2 foreign key (maker_id) references USER (id),
    constraint IEO_CLAIM_ibfk_3 foreign key (ieo_id) references IEO_DETAILS (id)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=latin1;


--
-- Table structure for table `IEO_DETAILS`
--

DROP TABLE IF EXISTS `IEO_DETAILS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
create table IEO_DETAILS
(
    id                   int auto_increment
        primary key,
    currency_name        varchar(10)                                                                                                   not null,
    currency_description varchar(255)                                                                                                  null,
    maker_id             int                                                                                                           not null,
    rate                 double                                                                                                        not null,
    amount               double                                                                                                        not null,
    available_amount     double                                                                              default 0                 null,
    contributors         int                                                                                 default 0                 null,
    status               enum ('PENDING', 'RUNNING', 'SUCCEEDED', 'FAILED', 'PROCESSING_FAIL', 'TERMINATED') default 'PENDING'         null,
    min_amount           double                                                                              default 0                 null,
    max_amount_per_claim double                                                                              default 0                 null,
    max_amount_per_user  double                                                                              default 0                 null,
    starts_at            datetime                                                                                                      not null,
    terminates_at        datetime                                                                                                      not null,
    created_at           timestamp                                                                           default CURRENT_TIMESTAMP not null,
    created_by           int                                                                                                           not null
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;


--
-- Table structure for table `IEO_RESTRICTED_COUNTRY`
--

DROP TABLE IF EXISTS `IEO_RESTRICTED_COUNTRY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
create table IEO_RESTRICTED_COUNTRY
(
    ieo_id       int         not null,
    country_code varchar(64) not null,
    constraint ieo_id
        unique (ieo_id, country_code),
    constraint IEO_RESTRICTED_COUNTRY_ibfk_1
        foreign key (ieo_id) references IEO_DETAILS (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `IEO_RESULT`
--

DROP TABLE IF EXISTS `IEO_RESULT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
create table IEO_RESULT
(
    claim_id         int                                                    null,
    ieo_id           int                                                    not null,
    available_amount double(40, 9)                      default 0.000000000 null,
    status           enum ('SUCCESS', 'FAILED', 'NONE') default 'NONE'      not null,
    constraint IEO_RESULT_ibfk_1
        foreign key (ieo_id) references IEO_DETAILS (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `INPUT_OUTPUT_REPORT`
--

DROP TABLE IF EXISTS `INPUT_OUTPUT_REPORT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `INPUT_OUTPUT_REPORT` (
  `id` int(40) unsigned NOT NULL AUTO_INCREMENT,
  `file_name` varchar(64) NOT NULL,
  `content` longblob NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3845 DEFAULT CHARSET=utf8;


--
-- Table structure for table `INTERNAL_WALLET_BALANCES`
--

DROP TABLE IF EXISTS `INTERNAL_WALLET_BALANCES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `INTERNAL_WALLET_BALANCES` (
  `id` int(40) unsigned NOT NULL AUTO_INCREMENT,
  `currency_id` int(40) NOT NULL,
  `role_id` int(40) NOT NULL,
  `usd_rate` decimal(19,12) DEFAULT '0.000000000000',
  `btc_rate` decimal(19,12) DEFAULT '0.000000000000',
  `total_balance` decimal(30,8) DEFAULT '0.00000000',
  `total_balance_usd` decimal(30,8) DEFAULT '0.00000000',
  `total_balance_btc` decimal(30,8) DEFAULT '0.00000000',
  `last_updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `internal_wallet_balances_currency_id_fk` (`currency_id`),
  KEY `internal_wallet_balances_role_id_fk` (`role_id`),
  CONSTRAINT `internal_wallet_balances_currency_id_fk` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`),
  CONSTRAINT `internal_wallet_balances_role_id_fk` FOREIGN KEY (`role_id`) REFERENCES `USER_ROLE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5497 DEFAULT CHARSET=latin1;


--
-- Table structure for table `INVOICE_BANK`
--

DROP TABLE IF EXISTS `INVOICE_BANK`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `INVOICE_BANK` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `currency_id` int(11) DEFAULT NULL,
  `name` varchar(60) DEFAULT NULL,
  `account_number` varchar(60) DEFAULT NULL,
  `recipient` varchar(150) DEFAULT NULL,
  `bank_details` varchar(300) DEFAULT NULL,
  `hidden` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `invoice_bank___fk_currency` (`currency_id`),
  CONSTRAINT `invoice_bank___fk_currency` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;


--
-- Table structure for table `INVOICE_OPERATION_DIRECTION`
--

DROP TABLE IF EXISTS `INVOICE_OPERATION_DIRECTION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `INVOICE_OPERATION_DIRECTION` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` enum('REFILL','WITHDRAW','TRANSFER_VOUCHER') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `INVOICE_OPERATION_DIRECTION_name_uindex` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;


--
-- Table structure for table `INVOICE_OPERATION_PERMISSION`
--

DROP TABLE IF EXISTS `INVOICE_OPERATION_PERMISSION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `INVOICE_OPERATION_PERMISSION` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;


--
-- Table structure for table `IP_Log`
--

DROP TABLE IF EXISTS `IP_Log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `IP_Log` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `ip` varchar(100) NOT NULL,
  `user_id` int(40) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_IP_Logs_USERS1_idx` (`user_id`),
  CONSTRAINT `fk_IP_Logs_USERS1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=168 DEFAULT CHARSET=latin1;


--
-- Table structure for table `KYC_COUNTRY_CODES`
--

DROP TABLE IF EXISTS `KYC_COUNTRY_CODES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `KYC_COUNTRY_CODES` (
  `id` int(40) unsigned NOT NULL AUTO_INCREMENT,
  `country_name` varchar(64) NOT NULL,
  `country_code` varchar(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=244 DEFAULT CHARSET=latin1;


--
-- Table structure for table `KYC_LANGUAGE_CODES`
--

DROP TABLE IF EXISTS `KYC_LANGUAGE_CODES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `KYC_LANGUAGE_CODES` (
  `id` int(40) unsigned NOT NULL AUTO_INCREMENT,
  `language_name` varchar(64) NOT NULL,
  `language_code` varchar(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=latin1;


--
-- Table structure for table `MERCHANT`
--

DROP TABLE IF EXISTS `MERCHANT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `MERCHANT` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(50) DEFAULT NULL,
  `name` varchar(42) NOT NULL,
  `merchant_order` int(2) DEFAULT NULL,
  `transaction_source_type_id` int(2) NOT NULL DEFAULT '2',
  `service_bean_name` varchar(100) DEFAULT NULL,
  `process_type` enum('INVOICE','MERCHANT','CRYPTO','TRANSFER') NOT NULL DEFAULT 'MERCHANT',
  `tokens_parrent_id` int(11) DEFAULT NULL,
  `needVerification` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `FK_merchant_transaction_source_type` (`transaction_source_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=309 DEFAULT CHARSET=latin1;


--
-- Table structure for table `MERCHANT_CURRENCY`
--

DROP TABLE IF EXISTS `MERCHANT_CURRENCY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `MERCHANT_CURRENCY` (
  `merchant_id` int(11) NOT NULL,
  `currency_id` int(11) NOT NULL,
  `min_sum` double(40,9) DEFAULT '0.000000000',
  `merchant_input_commission` double(40,9) DEFAULT '0.000000000',
  `merchant_output_commission` double(40,9) DEFAULT '0.000000000',
  `merchant_transfer_commission` double(40,9) DEFAULT '0.000000000',
  `withdraw_block` tinyint(1) DEFAULT '0',
  `refill_block` tinyint(1) NOT NULL DEFAULT '0',
  `transfer_block` tinyint(1) DEFAULT '1',
  `merchant_fixed_commission` double(40,9) DEFAULT '0.000000000',
  `merchant_fixed_commission_usd` double(40,9) NOT NULL DEFAULT '0.000000000',
  `usd_rate` decimal(19,8) NOT NULL DEFAULT '0.00000000',
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `withdraw_auto_enabled` tinyint(4) NOT NULL DEFAULT '0',
  `withdraw_auto_delay_seconds` int(11) NOT NULL DEFAULT '0',
  `withdraw_auto_threshold_amount` double(40,9) NOT NULL DEFAULT '0.000000000',
  `withdraw_lifetime_hours` int(11) NOT NULL DEFAULT '0',
  `refill_lifetime_hours` int(11) NOT NULL DEFAULT '0',
  `max_scale_for_refill` int(11) DEFAULT NULL,
  `max_scale_for_withdraw` int(11) DEFAULT NULL,
  `max_scale_for_transfer` int(11) DEFAULT NULL,
  `subtract_merchant_commission_for_withdraw` tinyint(1) NOT NULL DEFAULT '0',
  `withdraw_block_backup` tinyint(1) DEFAULT '0',
  `refill_block_backup` tinyint(1) DEFAULT '0',
  `recalculate_to_usd` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`merchant_id`,`currency_id`),
  KEY `MERCHANTS` (`currency_id`),
  CONSTRAINT `merchant_currency_ibfk_1` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `merchant_currency_ibfk_2` FOREIGN KEY (`merchant_id`) REFERENCES `MERCHANT` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `MERCHANT_IMAGE`
--

DROP TABLE IF EXISTS `MERCHANT_IMAGE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `MERCHANT_IMAGE` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `merchant_id` int(11) NOT NULL,
  `image_path` varchar(300) NOT NULL,
  `image_name` varchar(45) NOT NULL,
  `currency_id` int(11) NOT NULL,
  `child_merchant` varchar(40) DEFAULT '',
  `input_commission` double(5,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`),
  KEY `fk_MERCHANT_id_idx` (`merchant_id`),
  CONSTRAINT `fk3_MERCHANT_id` FOREIGN KEY (`merchant_id`) REFERENCES `MERCHANT` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1254 DEFAULT CHARSET=latin1;


--
-- Table structure for table `MERCHANT_SPEC_PARAMETERS`
--

DROP TABLE IF EXISTS `MERCHANT_SPEC_PARAMETERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `MERCHANT_SPEC_PARAMETERS` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `merchant_id` int(40) NOT NULL,
  `param_name` varchar(200) NOT NULL,
  `param_value` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MERCHANT_SPEC_PARAMETERS_merchant_id` (`merchant_id`),
  CONSTRAINT `fk_MERCHANT_SPEC_PARAMETERS_merchant_id` FOREIGN KEY (`merchant_id`) REFERENCES `MERCHANT` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8;


--
-- Table structure for table `NEWS`
--

DROP TABLE IF EXISTS `NEWS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `NEWS` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Ð”Ð°Ñ‚Ð°, ÐºÐ¾Ñ‚Ð¾Ñ€Ð¾Ð¹ Ð´Ð°Ñ‚Ð¸Ñ€ÑƒÐµÑ‚ÑÑ Ð½Ð¾Ð²Ð¾ÑÑ‚ÑŒ',
  `resource` varchar(100) DEFAULT NULL COMMENT 'ÐžÑ‚Ð½Ð¾ÑÐ¸Ñ‚ÐµÐ»ÑŒÐ½Ñ‹Ð¹ Ð¿ÑƒÑ‚ÑŒ Ñ€Ð°Ð·Ð¼ÐµÑ‰ÐµÐ½Ð¸Ñ Ñ„Ð°Ð¹Ð»Ð¾Ð² (html, css, img, Ð¸ Ñ‚.Ð¿.) ÐºÐ¾Ð½Ñ‚ÐµÐ½Ñ‚Ð° Ð½Ð¾Ð²Ð¾ÑÑ‚Ð¸. ',
  `description` varchar(200) DEFAULT NULL COMMENT 'ÐžÐ¿Ð¸ÑÐ°Ð½Ð¸Ðµ, Ð¸ÑÐ¿Ð¾Ð»ÑŒÐ·ÑƒÐµÐ¼Ð¾Ðµ Ð´Ð»Ñ Ð²Ð½ÑƒÑ‚Ñ€ÐµÐ½Ð½Ð¸Ñ… Ñ†ÐµÐ»ÐµÐ¹ (Ð½Ðµ Ð´Ð»Ñ Ð¿ÑƒÐ±Ð»Ð¸ÐºÐ°Ñ†Ð¸Ð¸)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=257 DEFAULT CHARSET=utf8;


--
-- Table structure for table `NEWS_EXT`
--

DROP TABLE IF EXISTS `NEWS_EXT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `NEWS_EXT` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `resource` varchar(100) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `news_type_id` int(11) DEFAULT NULL,
  `calendar_date` date DEFAULT NULL,
  `no_title_img` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `FK_news_news_type` (`news_type_id`),
  CONSTRAINT `FK_news_news_type` FOREIGN KEY (`news_type_id`) REFERENCES `NEWS_TYPE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


--
-- Table structure for table `NEWS_TYPE`
--

DROP TABLE IF EXISTS `NEWS_TYPE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `NEWS_TYPE` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;


--
-- Table structure for table `NEWS_VARIANTS`
--

DROP TABLE IF EXISTS `NEWS_VARIANTS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `NEWS_VARIANTS` (
  `news_id` int(11) NOT NULL,
  `title` varchar(100) DEFAULT NULL COMMENT 'Ð—Ð°Ð³Ð¾Ð»Ð¾Ð²Ð¾Ðº Ð½Ð¾Ð²Ð¾ÑÑ‚Ð¸ Ð½Ð° ÑÐ·Ñ‹ÐºÐµ, ÑÐ¾Ð¾Ñ‚Ð²ÐµÑ‚ÑÑ‚Ð²ÑƒÑŽÑ‰ÐµÐ¼ ÑÐ·Ñ‹ÐºÐ¾Ð²Ð¾Ð¼Ñƒ Ð²Ð°Ñ€Ð¸Ð°Ð½Ñ‚Ñƒ Ð½Ð¾Ð²Ð¾ÑÑ‚Ð¸',
  `news_variant` enum('ru','en','cn','in','ar') NOT NULL DEFAULT 'ru',
  `brief` varchar(250) DEFAULT NULL COMMENT 'ÐšÑ€Ð°Ñ‚ÐºÐ¾Ðµ ÑÐ¾Ð´ÐµÑ€Ð¶Ð°Ð½Ð¸Ðµ Ð½Ð¾Ð²Ð¾ÑÑ‚Ð¸ Ð½Ð° ÑÐ·Ñ‹ÐºÐµ, ÑÐ¾Ð¾Ñ‚Ð²ÐµÑ‚ÑÑ‚Ð²ÑƒÑŽÑ‰ÐµÐ¼ ÑÐ·Ñ‹ÐºÐ¾Ð²Ð¾Ð¼Ñƒ Ð²Ð°Ñ€Ð¸Ð°Ð½Ñ‚Ñƒ Ð½Ð¾Ð²Ð¾ÑÑ‚Ð¸',
  `added_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Ð”Ð°Ñ‚Ð° Ð´Ð¾Ð±Ð°Ð²Ð»ÐµÐ½Ð¸Ñ ÑÐ·Ñ‹ÐºÐ¾Ð²Ð¾Ð³Ð¾ Ð²Ð°Ñ€Ð¸Ð°Ð½Ñ‚Ð°. ÐœÐ¾Ð¶ÐµÑ‚ Ð¾Ñ‚Ð»Ð¸Ñ‡Ð°Ñ‚ÑŒÑÑ Ð¾Ñ‚ Ð´Ð°Ñ‚Ñ‹ ÑÐ°Ð¼Ð¾Ð¹ Ð½Ð¾Ð²Ð¾ÑÑ‚Ð¸',
  `active` tinyint(4) DEFAULT '1' COMMENT 'Ð¿Ñ€Ð¸Ð·Ð½Ð°Ðº Ð°ÐºÑ‚Ð¸Ð²Ð½Ð¾ÑÑ‚Ð¸ Ð²Ð°Ñ€Ð¸Ð°Ð½Ñ‚Ð° Ð½Ð¾Ð²Ð¾ÑÑ‚Ð¸. ÐÐµÐ°ÐºÑ‚Ð¸Ð²Ð½Ñ‹Ð¹ Ð²Ð°Ñ€Ð¸Ð°Ð½Ñ‚ Ð½Ðµ Ð±ÑƒÐ´ÐµÑ‚ Ð¾Ñ‚Ð¾Ð±Ñ€Ð°Ð¶Ð°Ñ‚ÑŒÑÑ',
  PRIMARY KEY (`news_id`,`news_variant`),
  KEY `news_id` (`news_id`),
  CONSTRAINT `news_variants_fk` FOREIGN KEY (`news_id`) REFERENCES `NEWS` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `NEWS_VARIANTS_EXT`
--

DROP TABLE IF EXISTS `NEWS_VARIANTS_EXT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `NEWS_VARIANTS_EXT` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `news_id` int(11) NOT NULL,
  `title` varchar(1024) DEFAULT NULL,
  `language` varchar(10) DEFAULT NULL,
  `brief` varchar(1024) DEFAULT NULL,
  `content` varchar(8192) DEFAULT NULL,
  `added_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `active` bit(1) DEFAULT b'1',
  `visit_count` int(11) DEFAULT '0',
  `tags` varchar(1024) DEFAULT NULL,
  `sync_to_wallet_date` timestamp NULL DEFAULT NULL,
  `updated_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `news_id` (`news_id`,`language`),
  CONSTRAINT `news_variants_ext_fk` FOREIGN KEY (`news_id`) REFERENCES `NEWS_EXT` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;


--
-- Table structure for table `NOTIFICATION`
--

DROP TABLE IF EXISTS `NOTIFICATION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `NOTIFICATION` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `title` varchar(70) NOT NULL,
  `message` varchar(400) NOT NULL,
  `creation_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `notification_event_id` int(11) NOT NULL,
  `is_read` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `user_id_idx` (`user_id`),
  KEY `notification_event_id_idx` (`notification_event_id`),
  CONSTRAINT `notification_event_id` FOREIGN KEY (`notification_event_id`) REFERENCES `NOTIFICATION_EVENT` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12227359 DEFAULT CHARSET=utf8;


--
-- Table structure for table `NOTIFICATION_EVENT`
--

DROP TABLE IF EXISTS `NOTIFICATION_EVENT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `NOTIFICATION_EVENT` (
  `id` int(11) NOT NULL,
  `name` varchar(20) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  `default_send_notification` tinyint(1) DEFAULT NULL,
  `default_send_email` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `NOTIFICATION_OPTIONS`
--

DROP TABLE IF EXISTS `NOTIFICATION_OPTIONS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `NOTIFICATION_OPTIONS` (
  `notification_event_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `send_notification` tinyint(1) NOT NULL DEFAULT '1',
  `send_email` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`notification_event_id`,`user_id`),
  KEY `user_id_idx` (`user_id`),
  CONSTRAINT `fk_notification_event_id1` FOREIGN KEY (`notification_event_id`) REFERENCES `NOTIFICATION_EVENT` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_id1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_id_nop` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `OPEN_API_USER_TOKEN`
--

DROP TABLE IF EXISTS `OPEN_API_USER_TOKEN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `OPEN_API_USER_TOKEN` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `alias` varchar(40) NOT NULL,
  `public_key` varchar(60) NOT NULL,
  `private_key` varchar(60) NOT NULL,
  `date_generation` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `allow_trade` tinyint(1) NOT NULL DEFAULT '0',
  `allow_withdraw` tinyint(1) NOT NULL DEFAULT '0',
  `allow_accept_by_id` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `open_api_user_token_public_key_uindex` (`public_key`),
  KEY `open_api_user_token___fk_user_id` (`user_id`),
  KEY `open_api_user_token__idx_is_active_user_id` (`is_active`,`user_id`),
  CONSTRAINT `open_api_user_token___fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1732 DEFAULT CHARSET=latin1;


--
-- Table structure for table `OPERATION_TYPE`
--

DROP TABLE IF EXISTS `OPERATION_TYPE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `OPERATION_TYPE` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ID_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;


--
-- Table structure for table `OPERATION_TYPE_DIRECTION`
--

DROP TABLE IF EXISTS `OPERATION_TYPE_DIRECTION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `OPERATION_TYPE_DIRECTION` (
  `operation_type_id` int(11) NOT NULL,
  `operation_direction_id` int(11) NOT NULL,
  PRIMARY KEY (`operation_type_id`,`operation_direction_id`),
  KEY `op_type_dir__fk_op_dir_id` (`operation_direction_id`),
  CONSTRAINT `op_type_dir__fk_op_dir_id` FOREIGN KEY (`operation_direction_id`) REFERENCES `INVOICE_OPERATION_DIRECTION` (`id`),
  CONSTRAINT `op_type_dir__fk_ot_id` FOREIGN KEY (`operation_type_id`) REFERENCES `OPERATION_TYPE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `ORDER_STATUS`
--

DROP TABLE IF EXISTS `ORDER_STATUS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `ORDER_STATUS` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;


--
-- Table structure for table `ORDER_TYPE`
--

DROP TABLE IF EXISTS `ORDER_TYPE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `ORDER_TYPE` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(10) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_type_name_uindex` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;


--
-- Table structure for table `PHRASE_TEMPLATE`
--

DROP TABLE IF EXISTS `PHRASE_TEMPLATE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `PHRASE_TEMPLATE` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `template` varchar(100) NOT NULL,
  `topic_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_phrase_template_user_comment_topic` (`topic_id`),
  CONSTRAINT `FK_phrase_template_user_comment_topic` FOREIGN KEY (`topic_id`) REFERENCES `USER_COMMENT_TOPIC` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;


--
-- Table structure for table `POLICY`
--

DROP TABLE IF EXISTS `POLICY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `POLICY` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;


--
-- Table structure for table `QUBERA_RESPONSE_LOG`
--

DROP TABLE IF EXISTS `QUBERA_RESPONSE_LOG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `QUBERA_RESPONSE_LOG` (
  `paymentId` int(11) NOT NULL,
  `messageId` int(11) NOT NULL,
  `accountIBAN` varchar(150) NOT NULL,
  `accountNumber` varchar(150) NOT NULL,
  `processingTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `received` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `state` varchar(150) NOT NULL,
  `currency` varchar(10) NOT NULL,
  `paymentAmount` float DEFAULT NULL,
  `transferType` varchar(150) DEFAULT NULL,
  `rejectionReason` text,
  PRIMARY KEY (`paymentId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `QUBERA_USER_DETAILS`
--

DROP TABLE IF EXISTS `QUBERA_USER_DETAILS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `QUBERA_USER_DETAILS` (
  `user_id` int(11) NOT NULL,
  `currency_id` int(11) NOT NULL,
  `account_number` varchar(50) NOT NULL,
  `iban` varchar(100) NOT NULL,
  UNIQUE KEY `uq_user_id_and_currency_id_qubera_user_details` (`user_id`,`currency_id`),
  KEY `fk_qubera_user_details_on_currencies` (`currency_id`),
  CONSTRAINT `fk_qubera_user_details_on_currencies` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_qubera_user_details_on_users` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `REFERRAL_LEVEL`
--

DROP TABLE IF EXISTS `REFERRAL_LEVEL`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `REFERRAL_LEVEL` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `level` int(11) NOT NULL,
  `percent` double DEFAULT NULL,
  `datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=latin1;


--
-- Table structure for table `REFERRAL_TRANSACTION`
--

DROP TABLE IF EXISTS `REFERRAL_TRANSACTION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `REFERRAL_TRANSACTION` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) DEFAULT NULL,
  `referral_level_id` int(11) DEFAULT NULL,
  `initiator_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `status` enum('PAYED','DELETED') NOT NULL DEFAULT 'PAYED',
  PRIMARY KEY (`id`),
  KEY `o_fk` (`order_id`),
  KEY `rl_fk` (`referral_level_id`),
  KEY `inituser_fk` (`initiator_id`),
  KEY `user_id_fk` (`user_id`),
  KEY `order_id_status` (`order_id`,`status`),
  CONSTRAINT `REFERRAL_TRANSACTION_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `EXORDERS` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `REFERRAL_TRANSACTION_ibfk_2` FOREIGN KEY (`referral_level_id`) REFERENCES `REFERRAL_LEVEL` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `REFERRAL_TRANSACTION_ibfk_3` FOREIGN KEY (`initiator_id`) REFERENCES `USER` (`id`),
  CONSTRAINT `REFERRAL_TRANSACTION_ibfk_4` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31649207 DEFAULT CHARSET=latin1;


--
-- Table structure for table `REFERRAL_USER_GRAPH`
--

DROP TABLE IF EXISTS `REFERRAL_USER_GRAPH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `REFERRAL_USER_GRAPH` (
  `child` int(11) NOT NULL DEFAULT '0',
  `parent` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`child`,`parent`),
  KEY `REFERRAL_USER_GRAPH` (`parent`),
  CONSTRAINT `REFERRAL_USER_GRAPH_ibfk_2` FOREIGN KEY (`parent`) REFERENCES `USER` (`id`),
  CONSTRAINT `referral_user_graph_ibfk_1` FOREIGN KEY (`child`) REFERENCES `USER` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `REFILL_REQUEST`
--

DROP TABLE IF EXISTS `REFILL_REQUEST`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `REFILL_REQUEST` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `amount` decimal(40,9) DEFAULT NULL,
  `date_creation` timestamp NULL DEFAULT NULL,
  `status_id` int(11) DEFAULT NULL,
  `status_modification_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `currency_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `commission_id` int(11) DEFAULT NULL,
  `merchant_id` int(11) DEFAULT NULL,
  `merchant_transaction_id` varchar(256) DEFAULT NULL,
  `refill_request_param_id` int(11) DEFAULT NULL,
  `refill_request_address_id` int(11) DEFAULT NULL,
  `admin_holder_id` int(11) DEFAULT NULL,
  `import_note` varchar(50) DEFAULT NULL,
  `remark` varchar(2048) DEFAULT NULL,
  `inner_transfer_hash` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_refill_request_refill_request_new_status` (`status_id`),
  KEY `FK_refill_request_currency` (`currency_id`),
  KEY `FK_refill_request_merchant` (`merchant_id`),
  KEY `FK_refill_request_user` (`user_id`),
  KEY `FK_refill_request_commission` (`commission_id`),
  KEY `FK_refill_request_admin_holder` (`admin_holder_id`),
  KEY `merchant_id_currency_id_status_id_status_modification_date` (`merchant_id`,`currency_id`,`status_id`,`status_modification_date`),
  KEY `status_id_status_modification_date` (`status_modification_date`,`status_id`),
  KEY `user_id_currency_id_merchant_id` (`user_id`,`currency_id`,`merchant_id`),
  KEY `FK_refill_request_refill_request_address` (`refill_request_address_id`),
  KEY `FK_refill_request_refill_request_param` (`refill_request_param_id`),
  KEY `FK_refill_request_refill_request_address_2` (`currency_id`,`merchant_id`,`user_id`,`refill_request_address_id`),
  KEY `merchant_id_merchant_transaction_id` (`merchant_id`,`merchant_transaction_id`),
  CONSTRAINT `FK_refill_request_admin_holder` FOREIGN KEY (`admin_holder_id`) REFERENCES `USER` (`id`),
  CONSTRAINT `FK_refill_request_commission` FOREIGN KEY (`commission_id`) REFERENCES `COMMISSION` (`id`),
  CONSTRAINT `FK_refill_request_currency` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`),
  CONSTRAINT `FK_refill_request_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `MERCHANT` (`id`),
  CONSTRAINT `FK_refill_request_refill_request_address` FOREIGN KEY (`refill_request_address_id`) REFERENCES `REFILL_REQUEST_ADDRESS` (`id`),
  CONSTRAINT `FK_refill_request_refill_request_address_2` FOREIGN KEY (`currency_id`, `merchant_id`, `user_id`, `refill_request_address_id`) REFERENCES `REFILL_REQUEST_ADDRESS` (`currency_id`, `merchant_id`, `user_id`, `id`),
  CONSTRAINT `FK_refill_request_refill_request_new_status` FOREIGN KEY (`status_id`) REFERENCES `REFILL_REQUEST_STATUS` (`id`),
  CONSTRAINT `FK_refill_request_refill_request_param` FOREIGN KEY (`refill_request_param_id`) REFERENCES `REFILL_REQUEST_PARAM` (`id`),
  CONSTRAINT `FK_refill_request_user` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=842292 DEFAULT CHARSET=utf8;


--
-- Table structure for table `REFILL_REQUEST_ADDRESS`
--

DROP TABLE IF EXISTS `REFILL_REQUEST_ADDRESS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `REFILL_REQUEST_ADDRESS` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `currency_id` int(11) NOT NULL,
  `merchant_id` int(11) NOT NULL,
  `address` varchar(128) NOT NULL,
  `user_id` int(11) NOT NULL,
  `priv_key` varchar(256) DEFAULT NULL,
  `pub_key` varchar(256) DEFAULT NULL,
  `brain_priv_key` varchar(256) DEFAULT NULL,
  `date_generation` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `confirmed_tx_offset` int(11) DEFAULT '0',
  `need_transfer` tinyint(1) NOT NULL DEFAULT '0',
  `is_valid` tinyint(1) NOT NULL DEFAULT '1',
  `blocked` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `currency_id_address_user_id` (`currency_id`,`address`,`user_id`),
  UNIQUE KEY `address_currency_id_merchant_id_user_id` (`address`,`currency_id`,`merchant_id`,`user_id`),
  UNIQUE KEY `REFILL_REQUEST_ADDRESS_address_currency_id_pk` (`address`,`currency_id`),
  KEY `FK_refill_request_address_user` (`user_id`),
  KEY `currency_id_merchant_id_user_id_id` (`currency_id`,`merchant_id`,`user_id`,`id`),
  CONSTRAINT `FK_refill_request_address_currency` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`),
  CONSTRAINT `FK_refill_request_address_user` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1120844 DEFAULT CHARSET=utf8;


--
-- Table structure for table `REFILL_REQUEST_CONFIRMATION`
--

DROP TABLE IF EXISTS `REFILL_REQUEST_CONFIRMATION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `REFILL_REQUEST_CONFIRMATION` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `refill_request_id` int(11) NOT NULL,
  `datetime` timestamp NULL DEFAULT NULL,
  `confirmation_number` int(11) NOT NULL,
  `amount` decimal(40,9) NOT NULL,
  `blockhash` varchar(96) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_refill_request_confirmation_refill_request` (`refill_request_id`),
  CONSTRAINT `FK_refill_request_confirmation_refill_request` FOREIGN KEY (`refill_request_id`) REFERENCES `REFILL_REQUEST` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13384907 DEFAULT CHARSET=utf8;


--
-- Table structure for table `REFILL_REQUEST_PARAM`
--

DROP TABLE IF EXISTS `REFILL_REQUEST_PARAM`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `REFILL_REQUEST_PARAM` (
  `id` int(11) NOT NULL,
  `recipient_bank_id` int(11) DEFAULT NULL,
  `user_full_name` varchar(250) DEFAULT NULL,
  `payer_bank_name` varchar(200) DEFAULT NULL,
  `payer_bank_code` varchar(10) DEFAULT NULL,
  `payer_account` varchar(100) DEFAULT NULL,
  `receipt_scan` varchar(100) DEFAULT NULL,
  `receipt_scan_name` varchar(50) DEFAULT NULL,
  `merchant_request_sign` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_refill_request_param_invoice_bank` (`recipient_bank_id`),
  CONSTRAINT `FK_refill_request_param_invoice_bank` FOREIGN KEY (`recipient_bank_id`) REFERENCES `INVOICE_BANK` (`id`),
  CONSTRAINT `FK_refill_request_param_refill_request` FOREIGN KEY (`id`) REFERENCES `REFILL_REQUEST` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `REFILL_REQUEST_STATUS`
--

DROP TABLE IF EXISTS `REFILL_REQUEST_STATUS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `REFILL_REQUEST_STATUS` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;


--
-- Table structure for table `REPORT_MAILING_PARAMS`
--

DROP TABLE IF EXISTS `REPORT_MAILING_PARAMS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `REPORT_MAILING_PARAMS` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `param_name` varchar(50) NOT NULL,
  `param_value` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `report_mailing_params_param_name_uindex` (`param_name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;


--
-- Table structure for table `REPORT_SUBSCRIBERS`
--

DROP TABLE IF EXISTS `REPORT_SUBSCRIBERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `REPORT_SUBSCRIBERS` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `REPORT_SUBSCRIBERS_email_uindex` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=latin1;


--
-- Table structure for table `SERVICE_ALERTS`
--

DROP TABLE IF EXISTS `SERVICE_ALERTS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `SERVICE_ALERTS` (
  `alert_type` enum('UPDATE','TECHNICAL_WORKS') NOT NULL,
  `enable` tinyint(1) NOT NULL DEFAULT '0',
  `time_of_start` timestamp NULL DEFAULT NULL,
  `length` int(11) DEFAULT NULL,
  `launch_date` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `SESSION_LIFE_TIME_TYPE`
--

DROP TABLE IF EXISTS `SESSION_LIFE_TIME_TYPE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `SESSION_LIFE_TIME_TYPE` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;


--
-- Table structure for table `SESSION_PARAMS`
--

DROP TABLE IF EXISTS `SESSION_PARAMS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `SESSION_PARAMS` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `session_time_minutes` int(11) NOT NULL,
  `session_life_type_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `FK_session_params_user_id` (`user_id`),
  KEY `FK_session_params_session_life_type_id` (`session_life_type_id`),
  CONSTRAINT `FK_session_params_session_life_type_id` FOREIGN KEY (`session_life_type_id`) REFERENCES `SESSION_LIFE_TIME_TYPE` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_session_params_user_id` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=7063 DEFAULT CHARSET=utf8;


--
-- Table structure for table `SMS_SUBSCRIPTION`
--

DROP TABLE IF EXISTS `SMS_SUBSCRIPTION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `SMS_SUBSCRIPTION` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `contact` varchar(64) DEFAULT NULL,
  `delivery_price` decimal(40,9) DEFAULT NULL,
  `subscribe_code` varchar(64) DEFAULT NULL,
  `subscription_state` enum('SUBSCRIBED','WAIT_FOR_SUBSCRIBE') NOT NULL,
  `new_price` decimal(40,9) DEFAULT NULL,
  `new_contact` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `2FA_SMS_SUBSCRIPTION_user_id` (`user_id`),
  CONSTRAINT `fk_2FA_SMS_SUBSCRIPTION_user_id` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10242 DEFAULT CHARSET=utf8;


--
-- Table structure for table `STOCK_CURRENCY_PAIR`
--

DROP TABLE IF EXISTS `STOCK_CURRENCY_PAIR`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `STOCK_CURRENCY_PAIR` (
  `stock_exchange_id` int(11) NOT NULL,
  `currency_pair_id` int(11) NOT NULL,
  PRIMARY KEY (`stock_exchange_id`,`currency_pair_id`),
  KEY `fk_stock_corrency_pair_id_idx` (`currency_pair_id`),
  CONSTRAINT `fk_stock_corrency_pair_id` FOREIGN KEY (`currency_pair_id`) REFERENCES `CURRENCY_PAIR` (`id`),
  CONSTRAINT `fk_stock_curr_pair_stock_ex_id` FOREIGN KEY (`stock_exchange_id`) REFERENCES `STOCK_EXCHANGE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `STOCK_EXCHANGE`
--

DROP TABLE IF EXISTS `STOCK_EXCHANGE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `STOCK_EXCHANGE` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '0',
  `last_field_name` varchar(25) DEFAULT NULL,
  `buy_field_name` varchar(25) DEFAULT NULL,
  `sell_field_name` varchar(25) DEFAULT NULL,
  `low_field_name` varchar(25) DEFAULT NULL,
  `high_field_name` varchar(25) DEFAULT NULL,
  `volume_field_name` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=latin1;


--
-- Table structure for table `STOCK_EXCHANGE_CURRENCY_ALIAS`
--

DROP TABLE IF EXISTS `STOCK_EXCHANGE_CURRENCY_ALIAS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `STOCK_EXCHANGE_CURRENCY_ALIAS` (
  `stock_exchange_id` int(11) NOT NULL,
  `currency_id` int(11) NOT NULL,
  `alias` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`stock_exchange_id`,`currency_id`),
  KEY `stock_exchange_currency_alias___fk_cur_id` (`currency_id`),
  CONSTRAINT `stock_exchange_currency_alias___fk_cur_id` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`),
  CONSTRAINT `stock_exchange_currency_alias___fk_st_ex_id` FOREIGN KEY (`stock_exchange_id`) REFERENCES `STOCK_EXCHANGE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `STOCK_EXRATE`
--

DROP TABLE IF EXISTS `STOCK_EXRATE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `STOCK_EXRATE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `currency_pair_id` int(11) NOT NULL,
  `stock_exchange_id` int(11) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `price_last` double(40,9) DEFAULT NULL,
  `price_buy` double(40,9) DEFAULT NULL,
  `price_sell` double(40,9) DEFAULT NULL,
  `price_low` double(40,9) DEFAULT NULL,
  `price_high` double(40,9) DEFAULT NULL,
  `volume` double(40,9) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_STOCK_CURRENCY_PAIR_id_idx` (`currency_pair_id`),
  KEY `fk_stock_exchange_id_idx` (`stock_exchange_id`),
  KEY `stock_exrate__idx_cur_id_ex_id_date` (`currency_pair_id`,`stock_exchange_id`,`date`),
  CONSTRAINT `fk_STOCK_CURRENCY_PAIR_id` FOREIGN KEY (`currency_pair_id`) REFERENCES `CURRENCY_PAIR` (`id`),
  CONSTRAINT `fk_stock_exchange_id` FOREIGN KEY (`stock_exchange_id`) REFERENCES `STOCK_EXCHANGE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3341159 DEFAULT CHARSET=latin1;


--
-- Table structure for table `STOP_ORDERS`
--

DROP TABLE IF EXISTS `STOP_ORDERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `STOP_ORDERS` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `user_id` int(40) NOT NULL,
  `child_order_id` int(40) DEFAULT NULL,
  `currency_pair_id` int(11) NOT NULL,
  `operation_type_id` int(40) NOT NULL,
  `stop_rate` double(40,9) NOT NULL,
  `limit_rate` double(40,9) NOT NULL,
  `amount_base` double(40,9) NOT NULL,
  `amount_convert` double(40,9) NOT NULL,
  `commission_id` int(40) DEFAULT NULL,
  `commission_fixed_amount` double(40,9) NOT NULL,
  `date_creation` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_modification` timestamp NULL DEFAULT NULL,
  `status_id` int(40) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `child_order_id` (`child_order_id`),
  KEY `stop_orders_user_id` (`user_id`),
  KEY `stop_orders_currency_pair` (`currency_pair_id`),
  KEY `stop_orders_OPERATION_TYPE` (`operation_type_id`),
  KEY `stop_orders_status` (`status_id`),
  KEY `stop_orders_commission_id` (`commission_id`),
  KEY `stop_orders_child_order_id` (`child_order_id`),
  CONSTRAINT `fk_stop_orders_CHILD_ORDER` FOREIGN KEY (`child_order_id`) REFERENCES `EXORDERS` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_stop_orders_COMMISSION` FOREIGN KEY (`commission_id`) REFERENCES `COMMISSION` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_stop_orders_CURRENCY_PAIR` FOREIGN KEY (`currency_pair_id`) REFERENCES `CURRENCY_PAIR` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_stop_orders_OPERATION_TYPE` FOREIGN KEY (`operation_type_id`) REFERENCES `OPERATION_TYPE` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_stop_orders_ORDER_STATUS` FOREIGN KEY (`status_id`) REFERENCES `ORDER_STATUS` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_stop_orders_USER_CREATOR` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=7581 DEFAULT CHARSET=utf8;

--
-- Table structure for table `SURVEY`
--

DROP TABLE IF EXISTS `SURVEY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `SURVEY` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `token` varchar(100) NOT NULL,
  `active` tinyint(4) NOT NULL DEFAULT '1',
  `json` varchar(21000) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


--
-- Table structure for table `SURVEY_ITEM`
--

DROP TABLE IF EXISTS `SURVEY_ITEM`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `SURVEY_ITEM` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `survey_id` int(11) NOT NULL,
  `lang` varchar(5) NOT NULL,
  `name` varchar(50) NOT NULL,
  `title` varchar(300) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `survey_id_lang_name` (`survey_id`,`lang`,`name`),
  CONSTRAINT `FK_survey_item_survey` FOREIGN KEY (`survey_id`) REFERENCES `SURVEY` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;


--
-- Table structure for table `SURVEY_LANG_PARAM`
--

DROP TABLE IF EXISTS `SURVEY_LANG_PARAM`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `SURVEY_LANG_PARAM` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `survey_id` int(11) NOT NULL,
  `lang` varchar(5) NOT NULL,
  `description` varchar(1024) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `survey_lang_param_id_lang_name` (`survey_id`,`lang`),
  CONSTRAINT `FK_survey_item_survey_lang_param` FOREIGN KEY (`survey_id`) REFERENCES `SURVEY` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;


--
-- Table structure for table `TELEGRAM_CHAT_EN`
--

DROP TABLE IF EXISTS `TELEGRAM_CHAT_EN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `TELEGRAM_CHAT_EN` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `message_id` int(11) NOT NULL,
  `chat_id` bigint(19) NOT NULL,
  `telegram_user_id` int(11) NOT NULL,
  `username` varchar(256) NOT NULL,
  `text` text NOT NULL,
  `message_time` datetime NOT NULL,
  `telegram_user_reply_id` int(11) DEFAULT NULL,
  `message_reply_id` int(11) DEFAULT NULL,
  `username_reply` varchar(256) DEFAULT NULL,
  `text_reply` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6422 DEFAULT CHARSET=utf8;


--
-- Table structure for table `TELEGRAM_SUBSCRIPTION`
--

DROP TABLE IF EXISTS `TELEGRAM_SUBSCRIPTION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `TELEGRAM_SUBSCRIPTION` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `code` varchar(64) DEFAULT NULL,
  `subscription_state` enum('SUBSCRIBED','WAIT_FOR_SUBSCRIBE') NOT NULL,
  `user_account` varchar(64) DEFAULT NULL,
  `chat_id` mediumtext,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `TELEGRAM_SUBSCRIPTION_user_id` (`user_id`),
  CONSTRAINT `fk_TELEGRAM_SUBSCRIPTION_user_id` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1793 DEFAULT CHARSET=utf8;


--
-- Table structure for table `TEMPORAL_TOKEN`
--

DROP TABLE IF EXISTS `TEMPORAL_TOKEN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `TEMPORAL_TOKEN` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `value` varchar(45) NOT NULL,
  `user_id` int(40) NOT NULL,
  `expired` tinyint(1) NOT NULL DEFAULT '0',
  `date_creation` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `token_type_id` int(40) DEFAULT NULL,
  `check_ip` varchar(100) DEFAULT NULL,
  `already_used` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_REGISTRATION_TOKEN_USER1_idx` (`user_id`),
  KEY `fk_TEMPORAL_TOKEN_TYPE_idx` (`token_type_id`),
  CONSTRAINT `fk_REGISTRATION_TOKEN_USER1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fk_TEMPORAL_TOKEN_TYPE` FOREIGN KEY (`token_type_id`) REFERENCES `TOKEN_TYPE` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=165866 DEFAULT CHARSET=latin1;


--
-- Table structure for table `TOKEN_TYPE`
--

DROP TABLE IF EXISTS `TOKEN_TYPE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `TOKEN_TYPE` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;


--
-- Table structure for table `TRANSACTION`
--

DROP TABLE IF EXISTS `TRANSACTION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `TRANSACTION` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `user_wallet_id` int(11) NOT NULL,
  `company_wallet_id` int(11) DEFAULT NULL,
  `amount` double(40,9) NOT NULL,
  `commission_amount` double(40,9) NOT NULL,
  `commission_id` int(11) DEFAULT NULL,
  `operation_type_id` int(11) NOT NULL,
  `currency_id` int(11) NOT NULL,
  `merchant_id` int(11) DEFAULT NULL,
  `datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `provided` tinyint(1) NOT NULL DEFAULT '0',
  `confirmation` int(11) DEFAULT '-1',
  `order_id` int(11) DEFAULT NULL,
  `status_id` int(11) NOT NULL DEFAULT '1',
  `status_modification_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `active_balance_before` double(40,9) DEFAULT NULL,
  `reserved_balance_before` double(40,9) DEFAULT NULL,
  `company_balance_before` double(40,9) DEFAULT NULL,
  `company_commission_balance_before` double(40,9) DEFAULT NULL,
  `source_type` enum('ORDER','REFERRAL','ACCRUAL','MANUAL','USER_TRANSFER','WITHDRAW','REFILL','STOP_ORDER','NOTIFICATIONS','IEO') DEFAULT NULL,
  `source_id` int(40) DEFAULT NULL,
  `provided_modification_date` timestamp NULL DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_company_UNIQUE` (`id`),
  KEY `fk_COMPANY_ACCOUNT_WALLET1_idx` (`user_wallet_id`),
  KEY `COMPANY_ACCOUNT` (`commission_id`),
  KEY `TRANSACTION_CURRENCY_ID` (`currency_id`),
  KEY `Merchant_id` (`merchant_id`),
  KEY `TRANSACTION` (`operation_type_id`),
  KEY `TRANSACTION_COMPANY_WALLET` (`company_wallet_id`),
  KEY `fk_EXORDERS` (`order_id`),
  KEY `status_id` (`status_id`),
  KEY `operation_type_id_currency_id_merchant_id_provided` (`currency_id`,`operation_type_id`,`provided`,`merchant_id`),
  KEY `id_source_type` (`id`,`source_type`),
  KEY `user_wallet_id_operation_type_id_status_id_provided_datetime` (`user_wallet_id`,`operation_type_id`,`status_id`,`provided`,`datetime`),
  KEY `operation_type_id_status_id_provided_source_type` (`operation_type_id`,`status_id`,`provided`,`source_type`),
  KEY `source_type_source_id_operation_type_id` (`source_type`,`source_id`,`operation_type_id`),
  KEY `tx__idx_wlt_optype_source_type` (`user_wallet_id`,`operation_type_id`,`source_type`),
  KEY `transaction__datetime` (`datetime`),
  CONSTRAINT `fk_COMPANY_ACCOUNT_WALLET1` FOREIGN KEY (`user_wallet_id`) REFERENCES `WALLET` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_EXORDERS` FOREIGN KEY (`order_id`) REFERENCES `EXORDERS` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `transaction_ibfk_1` FOREIGN KEY (`commission_id`) REFERENCES `COMMISSION` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `transaction_ibfk_3` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `transaction_ibfk_4` FOREIGN KEY (`merchant_id`) REFERENCES `MERCHANT` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `transaction_ibfk_5` FOREIGN KEY (`operation_type_id`) REFERENCES `OPERATION_TYPE` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `transaction_ibfk_6` FOREIGN KEY (`company_wallet_id`) REFERENCES `COMPANY_WALLET` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `transaction_status_fk` FOREIGN KEY (`status_id`) REFERENCES `TRANSACTION_STATUS` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=125558121 DEFAULT CHARSET=latin1;

--
-- Table structure for table `TRANSACTION_SOURCE_TYPE`
--

DROP TABLE IF EXISTS `TRANSACTION_SOURCE_TYPE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `TRANSACTION_SOURCE_TYPE` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=latin1;


--
-- Table structure for table `TRANSACTION_STATUS`
--

DROP TABLE IF EXISTS `TRANSACTION_STATUS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `TRANSACTION_STATUS` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;


--
-- Table structure for table `TRANSFER_REQUEST`
--

DROP TABLE IF EXISTS `TRANSFER_REQUEST`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `TRANSFER_REQUEST` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `amount` decimal(40,9) DEFAULT NULL,
  `commission` decimal(40,9) DEFAULT NULL,
  `date_creation` timestamp NULL DEFAULT NULL,
  `status_id` int(11) DEFAULT NULL,
  `status_modification_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `merchant_id` int(11) DEFAULT NULL,
  `currency_id` int(11) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  `commission_id` int(11) DEFAULT NULL,
  `recipient_user_id` int(11) DEFAULT NULL,
  `hash` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`),
  KEY `FK_transfer_request_user` (`user_id`),
  KEY `FK_transfer_request_user_2` (`recipient_user_id`),
  KEY `FK_transfer_request_commission` (`commission_id`),
  KEY `FK_transfer_request_currency` (`currency_id`),
  KEY `FK_transfer_request_merchant` (`merchant_id`),
  KEY `FK_transfer_request_transfer_request_status` (`status_id`),
  CONSTRAINT `FK_transfer_request_commission` FOREIGN KEY (`commission_id`) REFERENCES `COMMISSION` (`id`),
  CONSTRAINT `FK_transfer_request_currency` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`),
  CONSTRAINT `FK_transfer_request_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `MERCHANT` (`id`),
  CONSTRAINT `FK_transfer_request_transfer_request_status` FOREIGN KEY (`status_id`) REFERENCES `TRANSFER_REQUEST_STATUS` (`id`),
  CONSTRAINT `FK_transfer_request_user` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`),
  CONSTRAINT `FK_transfer_request_user_2` FOREIGN KEY (`recipient_user_id`) REFERENCES `USER` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1919 DEFAULT CHARSET=latin1;


--
-- Table structure for table `TRANSFER_REQUEST_STATUS`
--

DROP TABLE IF EXISTS `TRANSFER_REQUEST_STATUS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `TRANSFER_REQUEST_STATUS` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;


--
-- Table structure for table `USER`
--

DROP TABLE IF EXISTS `USER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `USER` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `pub_id` char(21) DEFAULT NULL,
  `nickname` varchar(45) DEFAULT NULL,
  `email` varchar(45) NOT NULL,
  `password` varchar(100) DEFAULT NULL,
  `regdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `phone` varchar(45) DEFAULT NULL,
  `finpassword` varchar(100) DEFAULT NULL,
  `status` int(40) NOT NULL DEFAULT '1',
  `ipaddress` varchar(100) DEFAULT NULL,
  `roleid` int(11) NOT NULL DEFAULT '4',
  `preferred_lang` enum('en','ru','cn','in','ar','ko') DEFAULT 'en',
  `avatar_path` varchar(64) DEFAULT NULL,
  `tmp_poll_passed` tinyint(4) NOT NULL DEFAULT '0',
  `login_pin` varchar(100) DEFAULT NULL,
  `use2fa` tinyint(1) NOT NULL DEFAULT '0',
  `2fa_last_notify_date` timestamp NULL DEFAULT NULL,
  `withdraw_pin` varchar(100) DEFAULT NULL,
  `transfer_pin` varchar(100) DEFAULT NULL,
  `temporary_banned` tinyint(1) DEFAULT '0',
  `change_2fa_setting_pin` varchar(100) DEFAULT NULL,
  `api_token_setting_pin` varchar(100) DEFAULT NULL,
  `GA` varchar(100) DEFAULT '',
  `kyc_verification_step` smallint(1) NOT NULL DEFAULT '0',
  `kyc_status` varchar(55) NOT NULL DEFAULT 'none',
  `kyc_reference` varchar(55) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `firstName` varchar(255) DEFAULT NULL,
  `lastName` varchar(255) DEFAULT NULL,
  `birthDay` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idusers_UNIQUE` (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  UNIQUE KEY `nickname_UNIQUE` (`nickname`),
  UNIQUE KEY `avatar_path_UNIQUE` (`avatar_path`),
  UNIQUE KEY `pub_id` (`pub_id`),
  KEY `fk_USER_USER_STATUS1_idx` (`status`),
  KEY `fk_USER_ROLE_id_idx` (`roleid`),
  KEY `id_roleid` (`id`,`roleid`),
  CONSTRAINT `fk_USER_ROLE_id` FOREIGN KEY (`roleid`) REFERENCES `USER_ROLE` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_USER_USER_STATUS1` FOREIGN KEY (`status`) REFERENCES `USER_STATUS` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=110577 DEFAULT CHARSET=latin1;

--
-- Table structure for table `USER_ADMIN_AUTHORITY`
--

DROP TABLE IF EXISTS `USER_ADMIN_AUTHORITY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `USER_ADMIN_AUTHORITY` (
  `user_id` int(11) NOT NULL,
  `admin_authority_id` int(11) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`user_id`,`admin_authority_id`),
  KEY `fk_admin_authority_id_idx` (`admin_authority_id`),
  CONSTRAINT `fk_admin_authority_id` FOREIGN KEY (`admin_authority_id`) REFERENCES `ADMIN_AUTHORITY` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_id_auth` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `USER_ADMIN_AUTHORITY_ROLE_APPLICATION`
--

DROP TABLE IF EXISTS `USER_ADMIN_AUTHORITY_ROLE_APPLICATION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `USER_ADMIN_AUTHORITY_ROLE_APPLICATION` (
  `user_id` int(11) NOT NULL,
  `admin_authority_id` int(11) NOT NULL,
  `applied_to_role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`admin_authority_id`,`applied_to_role_id`),
  KEY `user_admin_authority_role_application___fk_aaid` (`admin_authority_id`),
  KEY `user_admin_authority_role_application___fk_role` (`applied_to_role_id`),
  KEY `user_admin_authority_role_application___fk_u_id` (`user_id`),
  CONSTRAINT `user_admin_authority_role_application___fk_aaid` FOREIGN KEY (`admin_authority_id`) REFERENCES `ADMIN_AUTHORITY` (`id`),
  CONSTRAINT `user_admin_authority_role_application___fk_role` FOREIGN KEY (`applied_to_role_id`) REFERENCES `USER_ROLE` (`id`),
  CONSTRAINT `user_admin_authority_role_application___fk_u_id` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `USER_COMMENT`
--

DROP TABLE IF EXISTS `USER_COMMENT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `USER_COMMENT` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `user_id` int(40) NOT NULL,
  `users_comment` varchar(400) NOT NULL,
  `user_creator_id` int(40) NOT NULL,
  `creation_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `edit_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `message_sent` tinyint(1) DEFAULT '0',
  `topic_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_COMMENT_USER_idx` (`user_id`),
  KEY `fk_COMMENT_USER_CREATOR_idx` (`user_creator_id`),
  KEY `FK_user_comment_user_comment_topic` (`topic_id`),
  CONSTRAINT `FK_user_comment_user_comment_topic` FOREIGN KEY (`topic_id`) REFERENCES `USER_COMMENT_TOPIC` (`id`),
  CONSTRAINT `fk_COMMENT_USER` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_COMMENT_USER_CREATOR` FOREIGN KEY (`user_creator_id`) REFERENCES `USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1562098 DEFAULT CHARSET=utf8;

--
-- Table structure for table `USER_COMMENT_TOPIC`
--

DROP TABLE IF EXISTS `USER_COMMENT_TOPIC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `USER_COMMENT_TOPIC` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `topic` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;


--
-- Table structure for table `USER_CURRENCY_INVOICE_OPERATION_PERMISSION`
--

DROP TABLE IF EXISTS `USER_CURRENCY_INVOICE_OPERATION_PERMISSION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `USER_CURRENCY_INVOICE_OPERATION_PERMISSION` (
  `user_id` int(11) NOT NULL,
  `currency_id` int(11) NOT NULL,
  `operation_direction` enum('REFILL','WITHDRAW','TRANSFER_VOUCHER') NOT NULL,
  `invoice_operation_permission_id` int(11) NOT NULL,
  `operation_direction_id` int(11) DEFAULT NULL,
  UNIQUE KEY `user_id_currency_id_operation_direction` (`user_id`,`currency_id`,`operation_direction`),
  KEY `FK_user_currency_invoice_permission_invoice_permission` (`invoice_operation_permission_id`),
  KEY `u_op_perm_op_direction_id_fk` (`operation_direction_id`),
  CONSTRAINT `FK_user_currency_invoice_permission_invoice_permission` FOREIGN KEY (`invoice_operation_permission_id`) REFERENCES `INVOICE_OPERATION_PERMISSION` (`id`),
  CONSTRAINT `u_op_perm_op_direction_id_fk` FOREIGN KEY (`operation_direction_id`) REFERENCES `INVOICE_OPERATION_DIRECTION` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `USER_DOC`
--

DROP TABLE IF EXISTS `USER_DOC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `USER_DOC` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `path` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `path` (`path`),
  KEY `USER_DOCS` (`user_id`),
  CONSTRAINT `USER_DOC_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5105 DEFAULT CHARSET=latin1;


--
-- Table structure for table `USER_FAVORITE_CURRENCY_PAIRS`
--

DROP TABLE IF EXISTS `USER_FAVORITE_CURRENCY_PAIRS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `USER_FAVORITE_CURRENCY_PAIRS` (
  `user_id` int(11) NOT NULL,
  `currency_pair_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`currency_pair_id`),
  KEY `user_cp_idx` (`user_id`),
  KEY `user_cp_cp_fk` (`currency_pair_id`),
  CONSTRAINT `USER_FAVORITE_CURRENCY_PAIRS_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `USER_FAVORITE_CURRENCY_PAIRS_ibfk_2` FOREIGN KEY (`currency_pair_id`) REFERENCES `CURRENCY_PAIR` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `USER_INITIAL_EXCHANGE_OFFERINGS`
--

DROP TABLE IF EXISTS `USER_INITIAL_EXCHANGE_OFFERINGS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `USER_INITIAL_EXCHANGE_OFFERINGS` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `USER_IP`
--

DROP TABLE IF EXISTS `USER_IP`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `USER_IP` (
  `user_id` int(40) NOT NULL,
  `ip` varchar(100) NOT NULL,
  `confirmed` tinyint(1) DEFAULT NULL COMMENT '1 if ip is confirmed',
  `registration_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'date when user has registered from this ip',
  `confirm_date` timestamp NULL DEFAULT NULL COMMENT 'date when user has cofirmed this ip',
  `last_registration_date` timestamp NULL DEFAULT NULL COMMENT 'дата последней регистрации',
  PRIMARY KEY (`user_id`,`ip`),
  KEY `user_id` (`user_id`),
  KEY `user_ip__index_userid_regdate` (`user_id`,`registration_date`),
  KEY `user_ip__index_userid_last_regdate` (`user_id`,`last_registration_date`),
  KEY `user_ip__index_uid_regdate_lastregdate` (`user_id`,`registration_date`,`last_registration_date`),
  CONSTRAINT `user_ip_fk` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `USER_OPERATION`
--

DROP TABLE IF EXISTS `USER_OPERATION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `USER_OPERATION` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;


--
-- Table structure for table `USER_OPERATION_AUTHORITY`
--

DROP TABLE IF EXISTS `USER_OPERATION_AUTHORITY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `USER_OPERATION_AUTHORITY` (
  `user_id` int(11) NOT NULL,
  `user_operation_id` int(11) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`,`user_operation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `USER_POLICES`
--

DROP TABLE IF EXISTS `USER_POLICES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `USER_POLICES` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `policy_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `policy_id` (`policy_id`),
  CONSTRAINT `USER_POLICES_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`),
  CONSTRAINT `USER_POLICES_ibfk_2` FOREIGN KEY (`policy_id`) REFERENCES `POLICY` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;


--
-- Table structure for table `USER_ROLE`
--

DROP TABLE IF EXISTS `USER_ROLE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `USER_ROLE` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `user_role_business_feature_id` int(11) DEFAULT NULL,
  `user_role_group_feature_id` int(11) DEFAULT NULL,
  `user_role_report_group_feature_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_user_role_user_role_business_feature` (`user_role_business_feature_id`),
  KEY `FK_user_role_user_role_group_feature` (`user_role_group_feature_id`),
  KEY `user_role___fk_report_group_feature` (`user_role_report_group_feature_id`),
  CONSTRAINT `FK_user_role_user_role_business_feature` FOREIGN KEY (`user_role_business_feature_id`) REFERENCES `USER_ROLE_BUSINESS_FEATURE` (`id`),
  CONSTRAINT `FK_user_role_user_role_group_feature` FOREIGN KEY (`user_role_group_feature_id`) REFERENCES `USER_ROLE_GROUP_FEATURE` (`id`),
  CONSTRAINT `user_role___fk_report_group_feature` FOREIGN KEY (`user_role_report_group_feature_id`) REFERENCES `USER_ROLE_REPORT_GROUP_FEATURE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;


--
-- Table structure for table `USER_ROLE_BUSINESS_FEATURE`
--

DROP TABLE IF EXISTS `USER_ROLE_BUSINESS_FEATURE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `USER_ROLE_BUSINESS_FEATURE` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;


--
-- Table structure for table `USER_ROLE_GROUP_FEATURE`
--

DROP TABLE IF EXISTS `USER_ROLE_GROUP_FEATURE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `USER_ROLE_GROUP_FEATURE` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;


--
-- Table structure for table `USER_ROLE_REPORT_GROUP_FEATURE`
--

DROP TABLE IF EXISTS `USER_ROLE_REPORT_GROUP_FEATURE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `USER_ROLE_REPORT_GROUP_FEATURE` (
  `id` int(11) NOT NULL,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_role_report_group_feature_name_uindex` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `USER_ROLE_SETTINGS`
--

DROP TABLE IF EXISTS `USER_ROLE_SETTINGS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `USER_ROLE_SETTINGS` (
  `user_role_id` int(11) NOT NULL,
  `order_acception_same_role_only` tinyint(1) NOT NULL DEFAULT '0',
  `manual_change_allowed` tinyint(1) NOT NULL DEFAULT '1',
  `bot_acception_allowed` tinyint(1) NOT NULL DEFAULT '0',
  `considered_for_price_range` tinyint(1) NOT NULL DEFAULT '0',
  `use_real_money` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`user_role_id`),
  CONSTRAINT `user_role_settings_user_role_id_fk` FOREIGN KEY (`user_role_id`) REFERENCES `USER_ROLE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `USER_STATUS`
--

DROP TABLE IF EXISTS `USER_STATUS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `USER_STATUS` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;


--
-- Table structure for table `USER_TRANSFER`
--

DROP TABLE IF EXISTS `USER_TRANSFER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `USER_TRANSFER` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from_user_id` int(11) NOT NULL,
  `to_user_id` int(11) NOT NULL,
  `currency_id` int(11) NOT NULL,
  `amount` double(40,9) NOT NULL,
  `commission_amount` double(40,9) NOT NULL,
  `creation_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_user_trnasfer_from_user_id` (`from_user_id`),
  KEY `FK_user_trnasfer_to_user_id` (`to_user_id`),
  KEY `FK_user_trnasfer_currency_id` (`currency_id`),
  CONSTRAINT `FK_user_trnasfer_currency_id` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_user_trnasfer_from_user_id` FOREIGN KEY (`from_user_id`) REFERENCES `USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_user_trnasfer_to_user_id` FOREIGN KEY (`to_user_id`) REFERENCES `USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=122 DEFAULT CHARSET=utf8;


--
-- Table structure for table `USER_VERIFICATION_INFO`
--

DROP TABLE IF EXISTS `USER_VERIFICATION_INFO`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `USER_VERIFICATION_INFO` (
  `user_id` int(11) NOT NULL,
  `doc_type` enum('ID','P') DEFAULT 'P',
  `doc_id` varchar(55) DEFAULT NULL,
  UNIQUE KEY `user_id` (`user_id`,`doc_type`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `WALLET`
--

DROP TABLE IF EXISTS `WALLET`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `WALLET` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `currency_id` int(40) NOT NULL,
  `user_id` int(40) NOT NULL,
  `active_balance` double(40,9) DEFAULT '0.000000000',
  `reserved_balance` double(40,9) DEFAULT '0.000000000',
  `ieo_reserve` double(40,9) DEFAULT '0.000000000',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_purse_UNIQUE` (`id`),
  UNIQUE KEY `user_currency` (`user_id`,`currency_id`),
  KEY `fk_WALLET_CURRENCIES1_idx` (`currency_id`),
  KEY `fk_WALLET_USERS1_idx` (`user_id`),
  CONSTRAINT `fk_WALLET_CURRENCIES1` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_WALLET_USERS1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=42275008 DEFAULT CHARSET=latin1;


--
-- Table structure for table `WITHDRAW_REQUEST`
--

DROP TABLE IF EXISTS `WITHDRAW_REQUEST`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `WITHDRAW_REQUEST` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `acceptance` timestamp NULL DEFAULT NULL,
  `wallet` varchar(128) NOT NULL,
  `processed_by` int(11) DEFAULT NULL,
  `status` int(40) DEFAULT '1',
  `recipient_bank_name` varchar(50) CHARACTER SET latin1 DEFAULT NULL,
  `recipient_bank_code` varchar(10) CHARACTER SET latin1 DEFAULT NULL,
  `user_full_name` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `remark` varchar(300) DEFAULT NULL,
  `amount` decimal(40,9) DEFAULT NULL,
  `commission` decimal(40,9) DEFAULT NULL,
  `merchant_commission` decimal(40,9) DEFAULT NULL,
  `date_creation` timestamp NULL DEFAULT NULL,
  `status_id` int(11) DEFAULT NULL,
  `status_modification_date` timestamp NULL DEFAULT NULL,
  `currency_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `commission_id` int(11) DEFAULT NULL,
  `merchant_id` int(11) DEFAULT NULL,
  `merchant_image_id` int(11) DEFAULT NULL,
  `admin_holder_id` int(11) DEFAULT NULL,
  `destination_tag` varchar(100) DEFAULT NULL,
  `transaction_hash` varchar(400) DEFAULT NULL,
  `additional_params` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `processed_by` (`processed_by`),
  KEY `FK_withdraw_request_withdraw_request_status` (`status_id`),
  KEY `FK_withdraw_request_currency` (`currency_id`),
  KEY `FK_withdraw_request_merchant` (`merchant_id`),
  KEY `FK_withdraw_request_user` (`user_id`),
  KEY `FK_withdraw_request_commission` (`commission_id`),
  KEY `FK_withdraw_request_admin_holder` (`admin_holder_id`),
  KEY `fk_merchant_image_id` (`merchant_image_id`),
  CONSTRAINT `FK_withdraw_request_admin_holder` FOREIGN KEY (`admin_holder_id`) REFERENCES `USER` (`id`),
  CONSTRAINT `FK_withdraw_request_commission` FOREIGN KEY (`commission_id`) REFERENCES `COMMISSION` (`id`),
  CONSTRAINT `FK_withdraw_request_currency` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`),
  CONSTRAINT `FK_withdraw_request_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `MERCHANT` (`id`),
  CONSTRAINT `FK_withdraw_request_user` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`),
  CONSTRAINT `FK_withdraw_request_withdraw_request_status` FOREIGN KEY (`status_id`) REFERENCES `WITHDRAW_REQUEST_STATUS` (`id`),
  CONSTRAINT `WITHDRAW_REQUEST_ibfk_2` FOREIGN KEY (`processed_by`) REFERENCES `USER` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_merchant_image_id` FOREIGN KEY (`merchant_image_id`) REFERENCES `MERCHANT_IMAGE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=69845 DEFAULT CHARSET=utf8;


--
-- Table structure for table `WITHDRAW_REQUEST_STATUS`
--

DROP TABLE IF EXISTS `WITHDRAW_REQUEST_STATUS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `WITHDRAW_REQUEST_STATUS` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;


--
-- Table structure for table `authority`
--

DROP TABLE IF EXISTS `authority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `authority` (
  `id` int(11) NOT NULL,
  `authority` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `credentials`
--

DROP TABLE IF EXISTS `credentials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `credentials` (
  `id` int(11) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `credentials_authorities`
--

DROP TABLE IF EXISTS `credentials_authorities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `credentials_authorities` (
  `credentials_id` bigint(20) NOT NULL,
  `authorities_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `oauth_access_token`
--

DROP TABLE IF EXISTS `oauth_access_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `oauth_access_token` (
  `token_id` varchar(255) DEFAULT NULL,
  `token` longblob,
  `authentication_id` varchar(255) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) DEFAULT NULL,
  `authentication` longblob,
  `refresh_token` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `oauth_approvals`
--

DROP TABLE IF EXISTS `oauth_approvals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `oauth_approvals` (
  `userId` varchar(255) DEFAULT NULL,
  `clientId` varchar(255) DEFAULT NULL,
  `scope` varchar(255) DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  `expiresAt` datetime DEFAULT NULL,
  `lastModifiedAt` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `oauth_client_details`
--

DROP TABLE IF EXISTS `oauth_client_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `oauth_client_details` (
  `client_id` varchar(255) NOT NULL,
  `resource_ids` varchar(255) DEFAULT NULL,
  `client_secret` varchar(255) DEFAULT NULL,
  `scope` varchar(255) DEFAULT NULL,
  `authorized_grant_types` varchar(255) DEFAULT NULL,
  `web_server_redirect_uri` varchar(255) DEFAULT NULL,
  `authorities` varchar(255) DEFAULT NULL,
  `access_token_validity` int(11) DEFAULT NULL,
  `refresh_token_validity` int(11) DEFAULT NULL,
  `additional_information` varchar(255) DEFAULT NULL,
  `autoapprove` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `oauth_client_token`
--

DROP TABLE IF EXISTS `oauth_client_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `oauth_client_token` (
  `token_id` varchar(255) DEFAULT NULL,
  `token` longblob,
  `authentication_id` varchar(255) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `oauth_code`
--

DROP TABLE IF EXISTS `oauth_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `oauth_code` (
  `code` varchar(255) DEFAULT NULL,
  `authentication` varbinary(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `oauth_refresh_token`
--

DROP TABLE IF EXISTS `oauth_refresh_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `oauth_refresh_token` (
  `token_id` varchar(255) DEFAULT NULL,
  `token` longblob,
  `authentication` longblob
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `schema_version`
--

DROP TABLE IF EXISTS `schema_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `schema_version` (
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `schema_version_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

SET FOREIGN_KEY_CHECKS=1;
