-- MySQL dump 10.13  Distrib 5.7.26, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: birzha_test
-- ------------------------------------------------------
-- Server version	5.7.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `2fa_google_authenticator`
--

DROP TABLE IF EXISTS `2fa_google_authenticator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `2fa_google_authenticator` (
  `user_id` int(11) NOT NULL,
  `enable` tinyint(1) DEFAULT NULL,
  `secret_code` varchar(45) DEFAULT NULL,
  UNIQUE KEY `2Fa_google_authenticator_user_id_uq` (`user_id`),
  KEY `user_id_idx` (`user_id`),
  CONSTRAINT `2FA_GOOGLE_AUTHENTICATOR_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `2fa_google_authenticator`
--

LOCK TABLES `2fa_google_authenticator` WRITE;
/*!40000 ALTER TABLE `2fa_google_authenticator` DISABLE KEYS */;
/*!40000 ALTER TABLE `2fa_google_authenticator` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `2fa_notification_messages`
--

DROP TABLE IF EXISTS `2fa_notification_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `2fa_notification_messages` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `event` varchar(64) NOT NULL,
  `type` varchar(64) NOT NULL,
  `message` varchar(512) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `2fa_notification_messages`
--

LOCK TABLES `2fa_notification_messages` WRITE;
/*!40000 ALTER TABLE `2fa_notification_messages` DISABLE KEYS */;
INSERT INTO `2fa_notification_messages` VALUES (1,'LOGIN','EMAIL','response.login.pin.email'),(2,'WITHDRAW','EMAIL','response.withdraw.pin.email'),(3,'TRANSFER','EMAIL','response.transfer.pin.email'),(4,'LOGIN','TELEGRAM','response.login.pin.telegram'),(5,'WITHDRAW','TELEGRAM','response.withdraw.pin.telegram'),(6,'TRANSFER','TELEGRAM','response.transfer.pin.telegram'),(7,'LOGIN','SMS','response.login.pin.sms'),(8,'WITHDRAW','SMS','response.withdraw.pin.sms'),(9,'TRANSFER','SMS','response.transfer.pin.sms'),(10,'CHANGE_2FA_SETTING','EMAIL','response.change2fa.pin.email'),(11,'LOGIN','GOOGLE2FA','response.login.code.google2fa'),(12,'WITHDRAW','GOOGLE2FA','response.withdraw.code.google2fa'),(13,'TRANSFER','GOOGLE2FA','response.transfer.code.google2fa'),(14,'API_TOKEN_SETTING','EMAIL','response.token.pin.email');
/*!40000 ALTER TABLE `2fa_notification_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `2fa_notification_price`
--

DROP TABLE IF EXISTS `2fa_notification_price`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `2fa_notification_price` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `notificator_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  `message_price` decimal(40,9) DEFAULT NULL,
  `subscribe_price` decimal(40,9) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `notificator_id` (`notificator_id`,`role_id`),
  KEY `2FA_NOTIFICATION_PRICE_notificator_id` (`notificator_id`),
  KEY `2FA_NOTIFICATION_PRICE_role_id` (`role_id`),
  CONSTRAINT `fk_2FA_NOTIFICATION_PRICE_notificator_id` FOREIGN KEY (`notificator_id`) REFERENCES `2fa_notificator` (`id`),
  CONSTRAINT `fk_2FA_NOTIFICATION_PRICE_role_id` FOREIGN KEY (`role_id`) REFERENCES `user_role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `2fa_notification_price`
--

LOCK TABLES `2fa_notification_price` WRITE;
/*!40000 ALTER TABLE `2fa_notification_price` DISABLE KEYS */;
INSERT INTO `2fa_notification_price` VALUES (1,2,1,0.000000000,NULL),(2,2,2,0.000000000,NULL),(3,2,3,0.000000000,NULL),(4,2,4,5.000000000,NULL),(5,2,5,0.000000000,NULL),(6,2,6,0.000000000,NULL),(7,2,7,5.000000000,NULL),(8,2,8,0.000000000,NULL),(9,2,9,0.000000000,NULL),(10,2,10,0.000000000,NULL),(11,3,1,NULL,0.000000000),(12,3,2,NULL,0.000000000),(13,3,3,NULL,0.000000000),(14,3,4,NULL,0.010000000),(15,3,5,NULL,0.000000000),(16,3,6,NULL,0.000000000),(17,3,7,NULL,0.010000000),(18,3,8,NULL,0.000000000),(19,3,9,NULL,0.000000000),(20,3,10,NULL,0.000000000);
/*!40000 ALTER TABLE `2fa_notification_price` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `2fa_notificator`
--

DROP TABLE IF EXISTS `2fa_notificator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `2fa_notificator` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bean_name` varchar(256) NOT NULL,
  `pay_type` enum('FREE','PREPAID_LIFETIME','PAY_FOR_EACH') NOT NULL,
  `enable` tinyint(1) NOT NULL DEFAULT '1',
  `name` varchar(256) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `2fa_notificator`
--

LOCK TABLES `2fa_notificator` WRITE;
/*!40000 ALTER TABLE `2fa_notificator` DISABLE KEYS */;
INSERT INTO `2fa_notificator` VALUES (1,'emailNotificatorServiceImpl','FREE',1,'E-MAIL'),(2,'smsNotificatorServiceImpl','PAY_FOR_EACH',0,'SMS'),(3,'telegramNotificatorServiceImpl','PREPAID_LIFETIME',1,'TELEGRAM'),(4,'google2faNotificatorServiceImpl','FREE',1,'GOOGLE_AUTHENTICATOR');
/*!40000 ALTER TABLE `2fa_notificator` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `2fa_user_notification_message_settings`
--

DROP TABLE IF EXISTS `2fa_user_notification_message_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `2fa_user_notification_message_settings` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `notificator_id` int(11) DEFAULT NULL,
  `event_name` enum('LOGIN','WITHDRAW','TRANSFER') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`,`event_name`),
  KEY `2FA_USER_NOTIFICATION_MESSAGE_SETTINGS_user_id` (`user_id`),
  KEY `2FA_USER_NOTIFICATION_MESSAGE_SETTINGS_notificator_id` (`notificator_id`),
  CONSTRAINT `fk_2FA_USER_NOTIFICATION_MESSAGE_SETTINGS_notificator_id` FOREIGN KEY (`notificator_id`) REFERENCES `2fa_notificator` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_2FA_USER_NOTIFICATION_MESSAGE_SETTINGS_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `2fa_user_notification_message_settings`
--

LOCK TABLES `2fa_user_notification_message_settings` WRITE;
/*!40000 ALTER TABLE `2fa_user_notification_message_settings` DISABLE KEYS */;
/*!40000 ALTER TABLE `2fa_user_notification_message_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_authority`
--

DROP TABLE IF EXISTS `admin_authority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin_authority` (
  `id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  `hidden` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_authority`
--

LOCK TABLES `admin_authority` WRITE;
/*!40000 ALTER TABLE `admin_authority` DISABLE KEYS */;
INSERT INTO `admin_authority` VALUES (1,'PROCESS_WITHDRAW',NULL,0),(2,'PROCESS_INVOICE',NULL,0),(3,'DELETE_ORDER',NULL,0),(4,'COMMENT_USER',NULL,0),(5,'MANAGE_SESSIONS',NULL,0),(6,'SET_CURRENCY_LIMIT',NULL,0),(7,'MANAGE_ACCESS',NULL,0),(8,'MANUAL_BALANCE_CHANGE',NULL,1),(9,'EDIT_USER',NULL,0),(10,'MANAGE_BTC_CORE_WALLET',NULL,0),(11,'SEE_REPORTS',NULL,0);
/*!40000 ALTER TABLE `admin_authority` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_authority_role_defaults`
--

DROP TABLE IF EXISTS `admin_authority_role_defaults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin_authority_role_defaults` (
  `role_id` int(11) NOT NULL,
  `admin_authority_id` int(11) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`role_id`,`admin_authority_id`),
  KEY `fk_auth_admin_auth_id_idx` (`admin_authority_id`),
  CONSTRAINT `fk_auth_admin_auth_id` FOREIGN KEY (`admin_authority_id`) REFERENCES `admin_authority` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_auth_user_role_id` FOREIGN KEY (`role_id`) REFERENCES `user_role` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_authority_role_defaults`
--

LOCK TABLES `admin_authority_role_defaults` WRITE;
/*!40000 ALTER TABLE `admin_authority_role_defaults` DISABLE KEYS */;
INSERT INTO `admin_authority_role_defaults` VALUES (1,1,0),(1,2,0),(1,3,0),(1,4,0),(1,5,0),(1,6,0),(1,7,0),(1,8,0),(1,9,0),(1,10,0),(1,11,0),(2,1,0),(2,2,0),(2,3,0),(2,4,0),(2,5,0),(2,6,0),(2,7,0),(2,8,0),(2,9,0),(2,10,0),(2,11,0),(3,1,0),(3,2,0),(3,3,0),(3,4,0),(3,5,0),(3,6,0),(3,7,0),(3,8,0),(3,9,0),(3,10,0),(9,1,0),(9,2,0),(9,3,0),(9,4,0),(9,5,0),(9,6,0),(9,7,0),(9,8,0),(9,9,0),(9,10,0);
/*!40000 ALTER TABLE `admin_authority_role_defaults` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `api_auth_token`
--

DROP TABLE IF EXISTS `api_auth_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `api_auth_token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `value` varchar(64) NOT NULL,
  `last_request` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `api_auth_token`
--

LOCK TABLES `api_auth_token` WRITE;
/*!40000 ALTER TABLE `api_auth_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `api_auth_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `api_params`
--

DROP TABLE IF EXISTS `api_params`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `api_params` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `param_name` varchar(40) NOT NULL,
  `param_value` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `PARAM_NAME` (`param_name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `api_params`
--

LOCK TABLES `api_params` WRITE;
/*!40000 ALTER TABLE `api_params` DISABLE KEYS */;
INSERT INTO `api_params` VALUES (1,'COINMARKETCAP_STATISTICS','ON'),(2,'ANDROID_APP_VERSION_KEY','A1.0.5'),(3,'IOS_APP_VERSION_KEY','I1.0.2'),(4,'KEY_CHECK_ENABLED','OFF'),(5,'NICKNAME_SEARCH_LIMIT','2');
/*!40000 ALTER TABLE `api_params` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `api_temp_password`
--

DROP TABLE IF EXISTS `api_temp_password`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `api_temp_password` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `password` varchar(100) NOT NULL,
  `date_creation` timestamp NULL DEFAULT NULL,
  `temporal_token_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `temporal_token_id` (`temporal_token_id`),
  CONSTRAINT `API_TEMP_PASSWORD_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `API_TEMP_PASSWORD_ibfk_2` FOREIGN KEY (`temporal_token_id`) REFERENCES `temporal_token` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `api_temp_password`
--

LOCK TABLES `api_temp_password` WRITE;
/*!40000 ALTER TABLE `api_temp_password` DISABLE KEYS */;
/*!40000 ALTER TABLE `api_temp_password` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `authority`
--

DROP TABLE IF EXISTS `authority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authority` (
  `id` int(11) NOT NULL,
  `authority` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authority`
--

LOCK TABLES `authority` WRITE;
/*!40000 ALTER TABLE `authority` DISABLE KEYS */;
/*!40000 ALTER TABLE `authority` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `balances_report`
--

DROP TABLE IF EXISTS `balances_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `balances_report` (
  `id` int(40) unsigned NOT NULL AUTO_INCREMENT,
  `file_name` varchar(64) NOT NULL,
  `content` longblob NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `balances_report`
--

LOCK TABLES `balances_report` WRITE;
/*!40000 ALTER TABLE `balances_report` DISABLE KEYS */;
/*!40000 ALTER TABLE `balances_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bch_hard_fork_balance_snapshot`
--

DROP TABLE IF EXISTS `bch_hard_fork_balance_snapshot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bch_hard_fork_balance_snapshot` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `user_id` int(40) NOT NULL,
  `active_balance` double(40,9) NOT NULL DEFAULT '0.000000000',
  `reserved_balance` double(40,9) NOT NULL DEFAULT '0.000000000',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `user_id_UNIQUE` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bch_hard_fork_balance_snapshot`
--

LOCK TABLES `bch_hard_fork_balance_snapshot` WRITE;
/*!40000 ALTER TABLE `bch_hard_fork_balance_snapshot` DISABLE KEYS */;
/*!40000 ALTER TABLE `bch_hard_fork_balance_snapshot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bot_launch_settings`
--

DROP TABLE IF EXISTS `bot_launch_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bot_launch_settings` (
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
  CONSTRAINT `bot_launch_settings___fk_bot_id` FOREIGN KEY (`bot_trader_id`) REFERENCES `bot_trader` (`id`),
  CONSTRAINT `bot_launch_settings___fk_currency_pair` FOREIGN KEY (`currency_pair_id`) REFERENCES `currency_pair` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bot_launch_settings`
--

LOCK TABLES `bot_launch_settings` WRITE;
/*!40000 ALTER TABLE `bot_launch_settings` DISABLE KEYS */;
/*!40000 ALTER TABLE `bot_launch_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bot_trader`
--

DROP TABLE IF EXISTS `bot_trader`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bot_trader` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `is_enabled` tinyint(1) NOT NULL DEFAULT '0',
  `order_accept_timeout` int(11) NOT NULL DEFAULT '5',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bot_trader`
--

LOCK TABLES `bot_trader` WRITE;
/*!40000 ALTER TABLE `bot_trader` DISABLE KEYS */;
/*!40000 ALTER TABLE `bot_trader` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bot_trading_settings`
--

DROP TABLE IF EXISTS `bot_trading_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bot_trading_settings` (
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
  CONSTRAINT `bot_trading_settings___fk_launch` FOREIGN KEY (`bot_launch_settings_id`) REFERENCES `bot_launch_settings` (`id`),
  CONSTRAINT `bot_trading_settings___fk_order_type` FOREIGN KEY (`order_type_id`) REFERENCES `order_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bot_trading_settings`
--

LOCK TABLES `bot_trading_settings` WRITE;
/*!40000 ALTER TABLE `bot_trading_settings` DISABLE KEYS */;
/*!40000 ALTER TABLE `bot_trading_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `callback_logs`
--

DROP TABLE IF EXISTS `callback_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `callback_logs` (
  `REQUEST_ID` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(40) DEFAULT NULL,
  `request_date` datetime DEFAULT NULL,
  `response_date` datetime DEFAULT NULL,
  `request_json` text,
  `response_json` text,
  `response_code` int(11) DEFAULT NULL,
  PRIMARY KEY (`REQUEST_ID`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `CALLBACK_LOGS_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `callback_logs`
--

LOCK TABLES `callback_logs` WRITE;
/*!40000 ALTER TABLE `callback_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `callback_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `callback_settings`
--

DROP TABLE IF EXISTS `callback_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `callback_settings` (
  `USER_ID` int(11) NOT NULL,
  `CALLBACK_URL` varchar(100) NOT NULL,
  `PAIR_ID` int(20) NOT NULL,
  PRIMARY KEY (`USER_ID`,`PAIR_ID`),
  KEY `PAIR_ID` (`PAIR_ID`),
  CONSTRAINT `CALLBACK_SETTINGS_ibfk_1` FOREIGN KEY (`USER_ID`) REFERENCES `user` (`id`),
  CONSTRAINT `CALLBACK_SETTINGS_ibfk_2` FOREIGN KEY (`PAIR_ID`) REFERENCES `currency_pair` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `callback_settings`
--

LOCK TABLES `callback_settings` WRITE;
/*!40000 ALTER TABLE `callback_settings` DISABLE KEYS */;
/*!40000 ALTER TABLE `callback_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_ar`
--

DROP TABLE IF EXISTS `chat_ar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chat_ar` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `body` varchar(256) NOT NULL,
  `message_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `CHAT_RU` (`user_id`),
  CONSTRAINT `CHAT_AR_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_ar`
--

LOCK TABLES `chat_ar` WRITE;
/*!40000 ALTER TABLE `chat_ar` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_ar` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_cn`
--

DROP TABLE IF EXISTS `chat_cn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chat_cn` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `body` varchar(256) NOT NULL,
  `message_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `CHAT_RU` (`user_id`),
  CONSTRAINT `CHAT_CN_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_cn`
--

LOCK TABLES `chat_cn` WRITE;
/*!40000 ALTER TABLE `chat_cn` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_cn` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_en`
--

DROP TABLE IF EXISTS `chat_en`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chat_en` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `body` varchar(256) NOT NULL,
  `message_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `CHAT_RU` (`user_id`),
  CONSTRAINT `CHAT_EN_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_en`
--

LOCK TABLES `chat_en` WRITE;
/*!40000 ALTER TABLE `chat_en` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_en` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_in`
--

DROP TABLE IF EXISTS `chat_in`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chat_in` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `body` varchar(256) NOT NULL,
  `message_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `CHAT_RU` (`user_id`),
  CONSTRAINT `CHAT_IN_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_in`
--

LOCK TABLES `chat_in` WRITE;
/*!40000 ALTER TABLE `chat_in` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_in` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_ko`
--

DROP TABLE IF EXISTS `chat_ko`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chat_ko` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `body` varchar(256) NOT NULL,
  `message_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `CHAT_KO_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_ko`
--

LOCK TABLES `chat_ko` WRITE;
/*!40000 ALTER TABLE `chat_ko` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_ko` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_ru`
--

DROP TABLE IF EXISTS `chat_ru`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chat_ru` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `body` varchar(256) NOT NULL,
  `message_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `CHAT_RU` (`user_id`),
  CONSTRAINT `CHAT_RU_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_ru`
--

LOCK TABLES `chat_ru` WRITE;
/*!40000 ALTER TABLE `chat_ru` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_ru` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_bank`
--

DROP TABLE IF EXISTS `client_bank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_bank` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `currency_id` int(11) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `code` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `currency_input_bank___fk_cur_id` (`currency_id`),
  CONSTRAINT `currency_input_bank___fk_cur_id` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=136 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client_bank`
--

LOCK TABLES `client_bank` WRITE;
/*!40000 ALTER TABLE `client_bank` DISABLE KEYS */;
INSERT INTO `client_bank` VALUES (1,10,'BANK BCA','014'),(2,10,'BANK MANDIRI','008'),(3,10,'BANK BNI','009'),(4,10,'BANK BRI','002'),(5,10,'AMERICAN EXPRESS BANK LTD','030'),(6,10,'ANGLOMAS INTERNASIONAL BANK','531'),(7,10,'ANZ PANIN BANK','061'),(8,10,'BANK ABN AMRO','052'),(9,10,'BANK AGRO NIAGA','494'),(10,10,'BANK AKITA','525'),(11,10,'BANK ALFINDO','503'),(12,10,'BANK ANTARDAERAH','088'),(13,10,'BANK ARTA NIAGA KENCANA','020'),(14,10,'BANK ARTHA GRAHA','037'),(15,10,'BANK ARTOS IND','542'),(16,10,'BANK BENGKULU','133'),(17,10,'BANK BII','016'),(18,10,'BANK BINTANG MANUNGGAL','484'),(19,10,'BANK BISNIS INTERNASIONAL','459'),(20,10,'BANK BNP PARIBAS INDONESIA','057'),(21,10,'BANK BUANA IND','023'),(22,10,'BANK BUKOPIN','441'),(23,10,'BANK BUMI ARTA','076'),(24,10,'BANK BUMIPUTERA','485'),(25,10,'BANK CAPITAL INDONESIA, TBK.','054'),(26,10,'BANK CENTURY, TBK.','095'),(27,10,'BANK CHINA TRUST INDONESIA','949'),(28,10,'BANK COMMONWEALTH','950'),(29,10,'BANK CREDIT AGRICOLE INDOSUEZ','039'),(30,10,'BANK DANAMON','011'),(31,10,'BANK DBS INDONESIA','046'),(32,10,'BANK DIPO INTERNATIONAL','523'),(33,10,'BANK DKI','111'),(34,10,'BANK EKONOMI','087'),(35,10,'BANK EKSEKUTIF','558'),(36,10,'BANK EKSPOR INDONESIA','003'),(37,10,'BANK FAMA INTERNASIONAL','562'),(38,10,'BANK FINCONESIA','945'),(39,10,'BANK GANESHA','161'),(40,10,'BANK HAGA','089'),(41,10,'BANK HAGAKITA','159'),(42,10,'BANK HARDA','567'),(43,10,'BANK HARFA','517'),(44,10,'BANK HARMONI INTERNATIONAL','166'),(45,10,'BANK HIMPUNAN SAUDARA 1906, TBK.','212'),(46,10,'BANK IFI','093'),(47,10,'BANK INA PERDANA','513'),(48,10,'BANK INDEX SELINDO','555'),(49,10,'BANK INDOMONEX','498'),(50,10,'BANK JABAR','110'),(51,10,'BANK BRI SYARIAH','422'),(52,10,'BANK JASA JAKARTA-427','427'),(53,10,'BANK JASA JAKARTA-472','472'),(54,10,'BANK JATENG','113'),(55,10,'BANK JATIM','114'),(56,10,'BANK KEPPEL TATLEE BUANA','053'),(57,10,'BANK KESAWAN','167'),(58,10,'BANK KESEJAHTERAAN EKONOMI','535'),(59,10,'BANK LAMPUNG','121'),(60,10,'BANK LIPPO','026'),(61,10,'BANK MALUKU','131'),(62,10,'BANK MASPION','157'),(63,10,'BANK MAYAPADA','097'),(64,10,'BANK MAYBANK INDOCORP','947'),(65,10,'BANK MAYORA','553'),(66,10,'BANK MEGA','426'),(67,10,'BANK MERINCORP','946'),(68,10,'BANK MESTIKA','151'),(69,10,'BANK METRO EXPRESS','152'),(70,10,'BANK MITRANIAGA','491'),(71,10,'BANK MIZUHO INDONESIA','048'),(72,10,'BANK MUAMALAT','147'),(73,10,'BANK MULTI ARTA SENTOSA','548'),(74,10,'BANK MULTICOR TBK.','036'),(75,10,'BANK NAGARI','118'),(76,10,'BANK NIAGA','022'),(77,10,'BANK NISP','028'),(78,10,'BANK NTT','130'),(79,10,'BANK NUSANTARA PARAHYANGAN','145'),(80,10,'BANK OCBC – INDONESIA','948'),(81,10,'BANK OF AMERICA, N.A','033'),(82,10,'BANK OF CHINA LIMITED','069'),(83,10,'BANK PANIN','019'),(84,10,'BANK PERSYARIKATAN INDONESIA','521'),(85,10,'BANK PURBA DANARTA','547'),(86,10,'BANK RESONA PERDANIA','047'),(87,10,'BANK RIAU','119'),(88,10,'BANK ROYAL INDONESIA','501'),(89,10,'BANK SHINTA INDONESIA','153'),(90,10,'BANK SINAR HARAPAN BALI','564'),(91,10,'BANK SRI PARTHA','466'),(92,10,'BANK SULTRA','135'),(93,10,'BANK SULUT','127'),(94,10,'BANK SUMITOMO MITSUI INDONESIA','045'),(95,10,'BANK SUMSEL','120'),(96,10,'BANK SUMUT','117'),(97,10,'BANK SWADESI','146'),(98,10,'BANK SWAGUNA','405'),(99,10,'BANK SYARIAH MANDIRI','451'),(100,10,'BANK SYARIAH MEGA','506'),(101,10,'BANK TABUNGAN NEGARA PERSERO BTN','200'),(102,10,'BANK TABUNGAN PENSIUNAN NASIONAL','213'),(103,10,'BANK UIB','536'),(104,10,'BANK UOB INDONESIA','058'),(105,10,'BANK VICTORIA INTERNATIONAL','566'),(106,10,'BANK WINDU KENTJANA','162'),(107,10,'BANK WOORI INDONESIA','068'),(108,10,'BANK YUDHA BHAKTI','490'),(109,10,'BPD ACEH','116'),(110,10,'BPD BALI','129'),(111,10,'BPD DIY','112'),(112,10,'BPD JAMBI','115'),(113,10,'BPD KALIMANTAN BARAT','123'),(114,10,'BPD KALSEL','122'),(115,10,'BPD KALTENG','125'),(116,10,'BPD KALTIM','124'),(117,10,'BPD NTB','128'),(118,10,'BPD PAPUA','132'),(119,10,'BPD SULAWESI TENGAH','134'),(120,10,'BPD SULSEL','126'),(121,10,'CENTRATAMA NASIONAL BANK','559'),(122,10,'CITIBANK N.A.','031'),(123,10,'DEUTSCHE BANK AG.','067'),(124,10,'HALIM INDONESIA BANK','164'),(125,10,'ING INDONESIA BANK','034'),(126,10,'JP. MORGAN CHASE BANK, N.A.','032'),(127,10,'KOREA EXCHANGE BANK DANAMON','059'),(128,10,'LIMAN INTERNATIONAL BANK','526'),(129,10,'PERMATA BANK','013'),(130,10,'PRIMA MASTER BANK','520'),(131,10,'RABOBANK INTERNASIONAL INDONESIA','060'),(132,10,'STANDARD CHARTERED BANK','050'),(133,10,'THE BANGKOK BANK COMP. LTD','040'),(134,10,'THE BANK OF TOKYO MITSUBISHI UFJ LTD','042'),(135,10,'THE HONGKONG & SHANGHAI B.C.','041');
/*!40000 ALTER TABLE `client_bank` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `commission`
--

DROP TABLE IF EXISTS `commission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commission` (
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
  CONSTRAINT `commission_fk_usr_role` FOREIGN KEY (`user_role`) REFERENCES `user_role` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `commission_ibfk_1` FOREIGN KEY (`operation_type`) REFERENCES `operation_type` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commission`
--

LOCK TABLES `commission` WRITE;
/*!40000 ALTER TABLE `commission` DISABLE KEYS */;
INSERT INTO `commission` VALUES (6,1,0.000000000,'2017-01-27 13:00:33',4),(7,2,0.000000000,'2018-10-23 17:06:42',4),(8,3,0.200000000,'2016-02-10 07:58:23',4),(9,4,0.200000000,'2016-02-10 07:58:34',4),(10,5,0.000000000,'2016-05-25 06:59:42',4),(11,7,0.000000000,'2016-05-25 06:59:49',4),(12,6,0.000000000,'2016-06-06 08:50:41',4),(13,8,0.000000000,'2016-12-13 13:43:55',4),(14,9,0.200000000,'2018-03-13 12:14:31',4),(15,1,0.000000000,'2017-02-01 04:47:45',1),(16,1,0.000000000,'2017-02-01 04:47:45',2),(17,1,0.000000000,'2017-02-01 04:47:45',3),(18,1,0.000000000,'2017-01-31 15:08:21',6),(19,1,0.000000000,'2017-01-31 15:08:21',7),(20,2,0.000000000,'2018-09-18 08:04:21',1),(21,2,0.000000000,'2018-09-18 08:04:21',2),(22,2,0.000000000,'2018-09-18 08:04:21',3),(23,2,0.000000000,'2018-06-12 11:19:40',6),(24,2,0.000000000,'2018-10-23 17:07:06',7),(25,3,0.000000000,'2019-01-16 06:06:57',1),(26,3,0.000000000,'2019-01-16 06:06:57',2),(27,3,0.000000000,'2019-01-16 06:06:57',3),(28,3,0.100000000,'2018-06-12 11:19:34',6),(29,3,0.000000000,'2017-02-28 09:45:20',7),(30,4,0.000000000,'2018-08-01 10:45:02',1),(31,4,0.000000000,'2018-08-01 10:45:02',2),(32,4,0.000000000,'2018-08-01 10:45:02',3),(33,4,0.100000000,'2018-06-12 11:19:29',6),(34,4,0.000000000,'2017-02-28 09:45:24',7),(35,5,0.000000000,'2017-01-31 15:08:21',1),(36,5,0.000000000,'2017-01-31 15:08:21',2),(37,5,0.000000000,'2017-01-31 15:08:21',3),(38,5,0.000000000,'2017-01-31 15:08:21',6),(39,5,0.000000000,'2017-01-31 15:08:21',7),(40,7,0.000000000,'2017-01-31 15:08:21',1),(41,7,0.000000000,'2017-01-31 15:08:21',2),(42,7,0.000000000,'2017-01-31 15:08:21',3),(43,7,0.000000000,'2017-01-31 15:08:21',6),(44,7,0.000000000,'2017-01-31 15:08:21',7),(45,6,0.000000000,'2017-01-31 15:08:21',1),(46,6,0.000000000,'2017-01-31 15:08:21',2),(47,6,0.000000000,'2017-01-31 15:08:21',3),(48,6,0.000000000,'2017-01-31 15:08:21',6),(49,6,0.000000000,'2017-01-31 15:08:21',7),(50,8,0.000000000,'2017-01-31 15:08:21',1),(51,8,0.000000000,'2017-01-31 15:08:21',2),(52,8,0.000000000,'2017-01-31 15:08:21',3),(53,8,0.000000000,'2017-01-31 15:08:21',6),(54,8,0.000000000,'2017-01-31 15:08:21',7),(55,9,0.000000000,'2018-03-19 15:01:29',1),(56,9,0.000000000,'2018-03-19 15:01:29',2),(57,9,0.000000000,'2018-03-19 15:01:29',3),(58,9,0.000000000,'2018-06-12 11:19:20',6),(59,9,0.000000000,'2017-01-31 15:08:21',7),(60,1,0.000000000,'2017-03-14 12:37:41',8),(61,2,0.000000000,'2018-10-23 17:07:21',8),(62,3,0.000000000,'2017-07-13 08:39:42',8),(63,4,0.000000000,'2017-07-13 08:39:47',8),(64,5,0.000000000,'2017-03-14 12:37:41',8),(65,7,0.000000000,'2017-03-14 12:37:41',8),(66,6,0.000000000,'2017-03-14 12:37:41',8),(67,8,0.000000000,'2017-03-14 12:37:41',8),(68,9,0.000000000,'2017-07-13 08:39:55',8),(75,1,0.000000000,'2017-03-14 12:38:36',9),(76,2,0.000000000,'2018-09-18 08:04:21',9),(77,3,0.000000000,'2019-01-16 06:06:57',9),(78,4,0.000000000,'2018-08-01 10:45:02',9),(79,5,0.000000000,'2017-03-14 12:38:36',9),(80,7,0.000000000,'2017-03-14 12:38:36',9),(81,6,0.000000000,'2017-03-14 12:38:36',9),(82,8,0.000000000,'2017-03-14 12:38:36',9),(83,9,0.000000000,'2018-03-19 15:01:29',9),(84,1,0.000000000,'2017-08-14 10:45:19',10),(85,2,0.000000000,'2017-08-14 10:45:19',10),(86,3,0.000000000,'2017-08-14 10:45:19',10),(87,4,0.000000000,'2017-08-14 10:45:19',10),(88,5,0.000000000,'2017-08-14 10:45:19',10),(89,6,0.000000000,'2017-08-14 10:45:19',10),(90,7,0.000000000,'2017-08-14 10:45:19',10),(91,8,0.000000000,'2017-08-14 10:45:19',10),(92,9,0.000000000,'2017-08-14 10:45:19',10),(93,1,0.000000000,'2018-08-14 07:51:36',11),(94,2,0.000000000,'2018-08-15 09:48:53',11),(95,3,0.000000000,'2018-08-15 09:48:38',11),(96,4,0.000000000,'2018-08-15 09:48:42',11),(97,5,0.000000000,'2018-08-14 07:51:36',11),(98,6,0.000000000,'2018-08-14 07:51:36',11),(99,7,0.000000000,'2018-08-14 07:51:36',11),(100,8,0.000000000,'2018-08-14 07:51:36',11),(101,9,0.000000000,'2018-08-15 09:48:47',11),(102,1,0.000000000,'2019-01-28 13:16:37',12),(103,2,0.000000000,'2019-01-28 13:16:37',12),(104,3,0.000000000,'2019-01-28 13:16:37',12),(105,4,0.000000000,'2019-01-28 13:16:37',12),(106,5,0.000000000,'2019-01-28 13:16:37',12),(107,6,0.000000000,'2019-01-28 13:16:37',12),(108,7,0.000000000,'2019-01-28 13:16:37',12),(109,8,0.000000000,'2019-01-28 13:16:37',12),(110,9,0.000000000,'2019-01-28 13:16:37',12);
/*!40000 ALTER TABLE `commission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `common_referral_root`
--

DROP TABLE IF EXISTS `common_referral_root`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `common_referral_root` (
  `user_id` int(11) DEFAULT NULL,
  KEY `COMMON_REFERRAL_ROOT` (`user_id`),
  CONSTRAINT `COMMON_REFERRAL_ROOT_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `common_referral_root`
--

LOCK TABLES `common_referral_root` WRITE;
/*!40000 ALTER TABLE `common_referral_root` DISABLE KEYS */;
/*!40000 ALTER TABLE `common_referral_root` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `company_external_wallet_balances`
--

DROP TABLE IF EXISTS `company_external_wallet_balances`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company_external_wallet_balances` (
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
  CONSTRAINT `company_external_wallet_balances_currency_id_fk` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company_external_wallet_balances`
--

LOCK TABLES `company_external_wallet_balances` WRITE;
/*!40000 ALTER TABLE `company_external_wallet_balances` DISABLE KEYS */;
INSERT INTO `company_external_wallet_balances` VALUES (1,300,0.000000000000,0.000000000000,0.00000000,0.00000000,0.00000000,0.00000000,0.00000000,'2019-06-11 08:59:33',0);
/*!40000 ALTER TABLE `company_external_wallet_balances` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `company_wallet`
--

DROP TABLE IF EXISTS `company_wallet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company_wallet` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `currency_id` int(11) DEFAULT NULL,
  `balance` double(40,9) NOT NULL DEFAULT '0.000000000',
  `commission_balance` double(40,9) NOT NULL DEFAULT '0.000000000',
  PRIMARY KEY (`id`),
  UNIQUE KEY `currency_id` (`currency_id`),
  CONSTRAINT `company_wallet_ibfk_1` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company_wallet`
--

LOCK TABLES `company_wallet` WRITE;
/*!40000 ALTER TABLE `company_wallet` DISABLE KEYS */;
INSERT INTO `company_wallet` VALUES (1,300,0.000000000,0.000000000);
/*!40000 ALTER TABLE `company_wallet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `company_wallet_external`
--

DROP TABLE IF EXISTS `company_wallet_external`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company_wallet_external` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `currency_id` int(40) NOT NULL,
  `main_wallet_balance` double(40,9) DEFAULT '0.000000000',
  `rate_usd_additional` double(10,5) DEFAULT '0.00000',
  `reserve_wallet_balance` double(40,9) DEFAULT '0.000000000',
  `cold_wallet_balance` double(40,9) DEFAULT '0.000000000',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_WALLET_EX_CURRENCIES_idx` (`currency_id`),
  CONSTRAINT `fk_WALLET_EX_CURRENCIES` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company_wallet_external`
--

LOCK TABLES `company_wallet_external` WRITE;
/*!40000 ALTER TABLE `company_wallet_external` DISABLE KEYS */;
INSERT INTO `company_wallet_external` VALUES (1,300,0.000000000,0.00000,0.000000000,0.000000000);
/*!40000 ALTER TABLE `company_wallet_external` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `company_wallet_external_reserved_address`
--

DROP TABLE IF EXISTS `company_wallet_external_reserved_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company_wallet_external_reserved_address` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `currency_id` int(11) NOT NULL,
  `name` varchar(200) DEFAULT NULL,
  `wallet_address` varchar(128) DEFAULT NULL,
  `balance` decimal(30,8) DEFAULT '0.00000000',
  PRIMARY KEY (`id`),
  KEY `company_wallet_external_reserved_address_currency_id_fk` (`currency_id`),
  CONSTRAINT `company_wallet_external_reserved_address_currency_id_fk` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company_wallet_external_reserved_address`
--

LOCK TABLES `company_wallet_external_reserved_address` WRITE;
/*!40000 ALTER TABLE `company_wallet_external_reserved_address` DISABLE KEYS */;
/*!40000 ALTER TABLE `company_wallet_external_reserved_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `credentials`
--

DROP TABLE IF EXISTS `credentials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `credentials` (
  `id` int(11) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `credentials`
--

LOCK TABLES `credentials` WRITE;
/*!40000 ALTER TABLE `credentials` DISABLE KEYS */;
/*!40000 ALTER TABLE `credentials` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `credentials_authorities`
--

DROP TABLE IF EXISTS `credentials_authorities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `credentials_authorities` (
  `credentials_id` bigint(20) NOT NULL,
  `authorities_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `credentials_authorities`
--

LOCK TABLES `credentials_authorities` WRITE;
/*!40000 ALTER TABLE `credentials_authorities` DISABLE KEYS */;
/*!40000 ALTER TABLE `credentials_authorities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `crypto_core_wallet`
--

DROP TABLE IF EXISTS `crypto_core_wallet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `crypto_core_wallet` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `merchant_id` int(11) DEFAULT NULL,
  `currency_id` int(11) DEFAULT NULL,
  `title_code` varchar(20) DEFAULT NULL,
  `subtract_fee_from_amount` tinyint(1) NOT NULL DEFAULT '0',
  `passphrase` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `crypto_core_wallet__index_uq` (`merchant_id`),
  KEY `crypto_core_wallet___fk_curr_id` (`currency_id`),
  CONSTRAINT `crypto_core_wallet___fk_curr_id` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
  CONSTRAINT `crypto_core_wallet___fk_merch_id` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `crypto_core_wallet`
--

LOCK TABLES `crypto_core_wallet` WRITE;
/*!40000 ALTER TABLE `crypto_core_wallet` DISABLE KEYS */;
/*!40000 ALTER TABLE `crypto_core_wallet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `currency`
--

DROP TABLE IF EXISTS `currency`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `currency` (
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
) ENGINE=InnoDB AUTO_INCREMENT=301 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `currency`
--

LOCK TABLES `currency` WRITE;
/*!40000 ALTER TABLE `currency` DISABLE KEYS */;
INSERT INTO `currency` VALUES (1,'RUB','Russian Ruble',0,2,2,2,'FIAT',2),(2,'USD','US Dollar',0,2,2,2,'FIAT',2),(3,'EUR','Euro',0,2,2,2,'FIAT',2),(4,'BTC','Bitcoin',0,8,8,8,'CRYPTO',8),(5,'LTC','Litecoin',1,8,8,8,'CRYPTO',8),(6,'EDRC','EDR-coin',1,8,8,8,'CRYPTO',8),(7,'UAH','Ukrainian Hryvnia',0,2,2,2,'FIAT',2),(8,'CNY','Chinese Yuan Renminbi',0,2,2,2,'FIAT',2),(9,'EDC','E-DinarCoin',0,8,3,3,'CRYPTO',8),(10,'IDR','Indonesian Rupiah',0,2,2,2,'FIAT',2),(11,'THB','Thai Baht',1,2,2,2,'FIAT',2),(12,'INR','Indian Rupee',1,2,2,2,'FIAT',2),(13,'NGN','Nigerian Naira',0,2,2,2,'FIAT',2),(14,'ETH','Ethereum',0,8,8,8,'CRYPTO',8),(17,'VND','Vietnamese Dong',0,2,2,2,'FIAT',2),(18,'TRY','Turkish Lira',0,2,2,2,'FIAT',2),(19,'ETC','Ethereum Classic',0,8,8,8,'CRYPTO',8),(20,'AED','Emirati Dirham',0,2,2,2,'FIAT',2),(21,'DASH','Dash',0,8,8,8,'CRYPTO',8),(22,'XRP','Ripple',0,6,6,6,'CRYPTO',8),(23,'XLM','Stellar Lumens',0,6,6,6,'CRYPTO',8),(24,'XEM','NEM',0,6,6,6,'CRYPTO',8),(25,'ATB','ATB-coin',0,6,6,6,'CRYPTO',8),(26,'BCH-old','Bitcoin Cash Old',1,8,8,8,'CRYPTO',8),(27,'IOTA','IOTA',0,6,6,6,'CRYPTO',8),(28,'DOGE','Dogecoin',0,8,8,8,'CRYPTO',8),(29,'LSK','Lisk',0,8,8,8,'CRYPTO',8),(30,'XMR','Monero',0,8,8,8,'CRYPTO',8),(31,'EOS','EOS',0,8,8,8,'CRYPTO',8),(32,'REP','Augur',0,8,8,8,'CRYPTO',8),(33,'GNT','Golem',0,8,8,8,'CRYPTO',8),(34,'BTG','Bitcoin-gold',0,8,8,8,'CRYPTO',8),(35,'B2X','Segwit2x',1,8,8,8,'CRYPTO',8),(36,'BCD','Bitcoin-diamond',0,6,6,6,'CRYPTO',8),(37,'NEO','NEO',0,0,0,0,'CRYPTO',8),(38,'GAS','Gas',0,4,4,4,'CRYPTO',8),(39,'ZEC','Zcash',0,8,8,8,'CRYPTO',8),(40,'OMG','OmiseGO',0,8,8,8,'CRYPTO',8),(41,'WAVES','Waves',0,8,8,8,'CRYPTO',8),(42,'BCX','Bitcoin-X',0,4,4,8,'CRYPTO',8),(43,'ATL','ATLANT',0,8,8,8,'CRYPTO',8),(44,'BCA','BitcoinAtom',0,8,8,8,'CRYPTO',8),(45,'BNB','BinanceCoin',0,8,8,8,'CRYPTO',8),(47,'SBTC','Super Bitcoin',1,8,8,8,'CRYPTO',8),(48,'OCC','Octoin',0,8,8,8,'CRYPTO',8),(49,'RNTB','BitRent',0,8,8,8,'CRYPTO',8),(50,'ETZ','Ether Zero',0,8,8,8,'CRYPTO',8),(51,'NIO','Autonio',0,0,0,0,'CRYPTO',8),(52,'GOS','GOSSAMER',0,6,6,6,'CRYPTO',8),(53,'BTCZ','BitcoinZ',0,8,8,8,'CRYPTO',8),(54,'BTW','BitWhite',0,8,8,8,'CRYPTO',8),(55,'BPTN','BPTN',0,8,8,8,'CRYPTO',8),(56,'QTUM','Qtum',0,8,8,8,'CRYPTO',8),(57,'BTCP','Bitcoin Private',0,8,8,8,'CRYPTO',8),(58,'TAXI','TAXI',1,8,8,8,'CRYPTO',8),(59,'LCC','LitecoinCash',0,6,6,6,'CRYPTO',8),(60,'GX','GameX',0,2,2,2,'CRYPTO',8),(61,'INK','Ink',0,8,8,8,'CRYPTO',8),(62,'DIM','DimCoin',0,6,6,6,'CRYPTO',8),(63,'NBTK','Nebeus Crypto Bank',0,8,8,8,'CRYPTO',8),(64,'NBC','Niobium',0,8,8,8,'CRYPTO',8),(65,'UCASH','U.CASH',0,8,8,8,'CRYPTO',8),(66,'PLC','Platincoin',0,8,8,8,'CRYPTO',8),(67,'NAC','Nami',0,8,8,8,'CRYPTO',8),(68,'ECHT','ECHAT TOKEN',0,8,8,8,'CRYPTO',8),(69,'DDX_old','DietBitcoin',1,8,8,8,'CRYPTO',8),(70,'DIT','Ditcoin',0,8,8,8,'CRYPTO',8),(71,'IDH','indaHash Coin',0,8,8,8,'CRYPTO',8),(72,'SZC','ShopZCoin',0,8,8,8,'CRYPTO',8),(73,'COBC','Com Bill Token',0,8,8,8,'CRYPTO',8),(74,'BTX','BitCore',0,8,8,8,'CRYPTO',8),(75,'BCI','Bitcoin Interest',0,8,8,8,'CRYPTO',8),(76,'LBTC','Lightning Bitcoin',0,8,8,8,'CRYPTO',8),(77,'BCS','BCShop.io',0,8,8,8,'CRYPTO',8),(78,'XBD','BitDollar',1,8,8,8,'CRYPTO',8),(79,'SLT','Smartlands',0,8,8,8,'CRYPTO',8),(80,'UQC','Uquid Coin',0,8,8,8,'CRYPTO',8),(81,'INO','Ino Coin',0,8,8,8,'CRYPTO',8),(82,'ORME','Ormeus Coin',0,8,8,8,'CRYPTO',8),(83,'PROFIT','PROFIT',0,8,8,8,'CRYPTO',8),(84,'BEZ','Bezop Coin',0,8,8,8,'CRYPTO',8),(85,'AMN','Amon',0,8,8,8,'CRYPTO',8),(86,'RISE','RiseVision',0,8,8,8,'CRYPTO',8),(87,'GET','Guaranteed Entrance',0,8,8,8,'CRYPTO',8),(88,'SIM','Simmitri',0,8,8,8,'CRYPTO',8),(89,'FLOT','Fire Lotto',0,8,8,8,'CRYPTO',8),(90,'ABTC','AML Bitcoin',0,8,8,8,'CRYPTO',8),(91,'VDG','Veri Doc Global',0,0,0,0,'CRYPTO',8),(92,'NPXSXEM','Pundix',0,6,6,6,'CRYPTO',8),(93,'DRONE','DEEP AERO',0,0,0,0,'CRYPTO',8),(94,'DGTX','Digitex Futures',0,8,8,8,'CRYPTO',8),(95,'NSR','NuShares',0,4,4,8,'CRYPTO',8),(96,'ARK','Ark',0,8,8,8,'CRYPTO',8),(97,'WDSC','WINDORSCOIN',0,8,8,8,'CRYPTO',8),(98,'FSBT','Forty Seven Bank Token',0,8,8,8,'CRYPTO',8),(99,'RTH','Rotharium',0,8,8,8,'CRYPTO',8),(100,'IPR','iPRONTO',0,8,8,8,'CRYPTO',8),(101,'CAS','Cashaa',0,8,8,8,'CRYPTO',8),(102,'SPC','Space Chain',0,8,8,8,'CRYPTO',8),(103,'SPD','SPINDLE',0,8,8,8,'CRYPTO',8),(104,'BBX',' BBCashCoin',0,8,8,8,'CRYPTO',8),(105,'BEET','Beetlecoin',0,8,8,8,'CRYPTO',8),(106,'MTC','Medical Token',0,8,8,8,'CRYPTO',8),(107,'HLC','Halal Chain',0,8,8,8,'CRYPTO',8),(108,'DTRC','Datarius Credit',0,8,8,8,'CRYPTO',8),(109,'CLO','CALLISTO',0,8,8,8,'CRYPTO',8),(110,'TNR','Tonestra',0,8,8,8,'CRYPTO',8),(111,'CEEK','CEEK',0,8,8,8,'CRYPTO',8),(112,'NYC','New York Coin',0,8,8,8,'CRYPTO',8),(113,'B2G','Bitcoiin2Gen',0,8,8,8,'CRYPTO',8),(114,'ENGT','Engagement Token',1,8,8,8,'CRYPTO',8),(115,'ARN','Aeron',0,8,8,8,'CRYPTO',8),(116,'ANY','AnyCoin',1,8,8,8,'CRYPTO',8),(117,'FGC','Fantasy Gold',0,8,8,8,'CRYPTO',8),(118,'TGAME','TGAME',0,8,8,8,'CRYPTO',8),(119,'HST','Decision Token',0,8,8,8,'CRYPTO',8),(120,'TAVITT','Tavittcoin',0,8,8,8,'CRYPTO',8),(121,'PTC','Perfectcoin',0,8,8,8,'CRYPTO',8),(122,'MTL','MTL',0,8,8,8,'CRYPTO',8),(123,'GOL','Goldiam',0,8,8,8,'CRYPTO',8),(124,'LEDU','Education',0,8,8,8,'CRYPTO',8),(125,'HSR',' HShare',0,8,8,8,'CRYPTO',8),(126,'CNET','ContractNet',0,8,8,8,'CRYPTO',8),(127,'CEDEX','CEDEX',0,8,8,8,'CRYPTO',8),(128,'LUNES','Lunes',0,8,8,8,'CRYPTO',8),(129,'ADB','AdBank',0,8,8,8,'CRYPTO',8),(130,'CHE','Crypto Harbor Exchange',0,8,8,8,'CRYPTO',8),(131,'GST','Gamestars',0,8,8,8,'CRYPTO',8),(132,'BCL','BitcoinClean',0,8,8,8,'CRYPTO',8),(133,'BRECO','Bitcoinreco',0,8,8,8,'CRYPTO',8),(134,'ACT','ACHAIN',0,5,5,8,'CRYPTO',8),(135,'VEX','Vexanium',0,5,5,8,'CRYPTO',8),(136,'FTO','FuturoCoin',0,8,8,8,'CRYPTO',8),(137,'UMT','Universal Mobile Token',0,8,8,8,'CRYPTO',8),(138,'SABR','SABR',0,8,8,8,'CRYPTO',8),(139,'EQL','Equalizer',0,8,8,8,'CRYPTO',8),(140,'MASP','MaspToken',0,8,8,8,'CRYPTO',8),(141,'VNT','Ventory',0,8,8,8,'CRYPTO',8),(143,'SKILL','SKILLCOIN',0,8,8,8,'CRYPTO',8),(144,'SAT','Social Activity Token',0,8,8,8,'CRYPTO',8),(145,'STOR','Self Storage Coin',0,8,8,8,'CRYPTO',8),(146,'DACC','Decentralized Accessible Content Chain',0,8,8,8,'CRYPTO',8),(147,'QUiNT','QUiNTillion Burgh',0,8,8,8,'CRYPTO',8),(148,'TERN','Ternio',0,8,8,8,'CRYPTO',8),(149,'TTC','Tabs Tracking Chain',0,8,8,8,'CRYPTO',8),(150,'NTY','Nexty',0,8,8,8,'CRYPTO',8),(151,'BFG','Blocform Global',0,8,8,8,'CRYPTO',8),(152,'SUMO','Sumokoin',0,8,8,8,'CRYPTO',8),(153,'KGS','KGS',0,8,8,8,'CRYPTO',8),(154,'BRB','Breakbits',0,8,8,8,'CRYPTO',8),(155,'JET','Jetcoin',0,8,8,8,'CRYPTO',8),(156,'RIZ','RizWave',0,8,8,8,'CRYPTO',8),(157,'PAT','PATRON',0,8,8,8,'CRYPTO',8),(158,'KWATT','KWATT',0,8,8,8,'CRYPTO',8),(159,'eMTV','Multiversum',0,8,8,8,'CRYPTO',8),(160,'FPWR','FireToken',0,8,8,8,'CRYPTO',8),(161,'TUSD','TrueUSD',0,8,8,8,'CRYPTO',8),(162,'CRBT','CRUISEBIT',0,8,8,8,'CRYPTO',8),(163,'HIVE','Hive token',0,8,8,8,'CRYPTO',8),(164,'TRX','Tron',0,6,6,8,'CRYPTO',8),(165,'HDR','Hedger',0,8,8,8,'CRYPTO',8),(166,'CMIT','CMITCOIN',0,8,8,8,'CRYPTO',8),(167,'RAC','RoboAdvisorCoin',0,8,8,8,'CRYPTO',8),(169,'IQN','IQeon',0,8,8,8,'CRYPTO',8),(170,'ETI','Etherinc',0,8,8,8,'CRYPTO',8),(171,'GEX','GREENX',0,8,8,8,'CRYPTO',8),(172,'SIC','Swisscoin',0,8,8,8,'CRYPTO',8),(173,'IXE','IXTUS Edutainment',0,8,8,8,'CRYPTO',8),(174,'CLX','CryptoLux',0,8,8,8,'CRYPTO',8),(175,'PHI','PHI Token',0,8,8,8,'CRYPTO',8),(176,'NER','Nerves',0,8,8,8,'CRYPTO',8),(177,'MFTU','Mainstream for the Underground',0,8,8,8,'CRYPTO',8),(178,'RET','RealTract',0,8,8,8,'CRYPTO',8),(179,'CMK','MarketC',0,8,8,8,'CRYPTO',8),(180,'GIGC','GIG Coin',0,8,8,8,'CRYPTO',8),(181,'DCR','Decred',0,8,8,8,'CRYPTO',8),(182,'SWM','Swarm Fund',0,8,8,8,'CRYPTO',8),(183,'BNC','Bionic',1,8,8,8,'CRYPTO',8),(184,'TIC','Thingschain',0,8,8,8,'CRYPTO',8),(185,'uDOO','uDOO',0,8,8,8,'CRYPTO',8),(186,'WTL','Welltrado token',0,8,8,8,'CRYPTO',8),(187,'ADK','Aidos Kuneen',0,8,8,8,'CRYPTO',8),(188,'QRK','Quark',0,8,8,8,'CRYPTO',8),(189,'LPC','Lightpaycoin',0,8,8,8,'CRYPTO',8),(190,'TOA','TOAcoin',0,8,8,8,'CRYPTO',8),(191,'XFC','FootballCoin',0,8,8,8,'CRYPTO',8),(192,'CRYP','Cryptic Coin',0,8,8,8,'CRYPTO',8),(193,'DDX','DietBitcoin',0,8,8,8,'CRYPTO',8),(194,'MBC','MicroBitcoin',0,8,8,8,'CRYPTO',8),(195,'APL','Apollo',0,1,0,1,'CRYPTO',8),(196,'XAU','Au Coin',0,8,8,8,'CRYPTO',8),(197,'KAZE','KAZECoin',0,8,8,8,'CRYPTO',8),(198,'STREAM','KazeSTREAM',0,8,8,8,'CRYPTO',8),(199,'TTP','Trent',0,8,8,8,'CRYPTO',8),(200,'USDC','USD Coin',0,8,8,8,'CRYPTO',8),(201,'AUNIT','Aunit Coin',0,5,5,5,'CRYPTO',8),(202,'ABBC','ABBC Coin',0,8,8,8,'CRYPTO',8),(203,'CBC','Cashbery Coin',0,8,8,8,'CRYPTO',8),(204,'BCH','Bitcoin Cash',0,8,8,8,'CRYPTO',8),(205,'BSV','Bitcoin SV',0,8,8,8,'CRYPTO',8),(207,'Q','Quick',0,8,8,8,'CRYPTO',8),(208,'VAI','VIOLET',0,8,8,8,'CRYPTO',8),(209,'UNC','Unicom',0,8,8,8,'CRYPTO',8),(210,'MODL','MODULE',0,8,8,8,'CRYPTO',8),(212,'DIME','DimeCoin',0,5,5,5,'CRYPTO',8),(213,'MGX','MEGAX',0,8,8,8,'CRYPTO',8),(214,'MNC','MainCoin',0,8,8,8,'CRYPTO',8),(215,'S4F','S4FE',0,8,8,8,'CRYPTO',8),(216,'ECTE','EurocoinToken',0,8,8,8,'CRYPTO',8),(217,'CTX','Centauri',0,5,5,5,'CRYPTO',8),(218,'EDT','ENDO Token',0,8,8,8,'CRYPTO',8),(219,'HT','Huobi token',0,8,8,8,'CRYPTO',8),(220,'RIME','RimeCoin',0,8,8,8,'CRYPTO',8),(222,'TCAT','TheCurrencyAnalytics',0,8,8,8,'CRYPTO',8),(223,'WaBi','Tael',1,8,8,8,'CRYPTO',8),(224,'POA','POA ERC20 on Foundation',1,8,8,8,'CRYPTO',8),(225,'MCO','Crypto.com',0,8,8,8,'CRYPTO',8),(226,'ZIL','Zilliqa',0,8,8,8,'CRYPTO',8),(227,'MANA','Decentraland',0,8,8,8,'CRYPTO',8),(228,'EXO','Exosis',0,8,8,8,'CRYPTO',8),(229,'GRS','GroestlCoin',0,8,8,8,'CRYPTO',8),(230,'KOD','KODCoin',0,8,8,8,'CRYPTO',8),(231,'HCXP','HCXPay',0,6,6,6,'CRYPTO',8),(232,'QKC','QuarkChain Token',0,8,8,8,'CRYPTO',8),(233,'NPXS','Pundi X Token',0,8,8,8,'CRYPTO',8),(234,'HOT','HoloToken',0,8,8,8,'CRYPTO',8),(235,'ZRX','0x',0,8,8,8,'CRYPTO',8),(236,'BAT','Basic Attention Token',0,8,8,8,'CRYPTO',8),(237,'RDN','Raiden Network Token',0,8,8,8,'CRYPTO',8),(238,'DIM.EUR','DIM.EUR',0,2,2,8,'CRYPTO',8),(239,'DIM.USD','DIM.USD',0,2,2,8,'CRYPTO',8),(240,'DIGIT','DIGIT coin',0,6,6,6,'CRYPTO',8),(241,'ELT','Ethereum Lendo Token',1,8,8,8,'CRYPTO',8),(242,'HNI','HUNI',0,8,8,8,'CRYPTO',8),(243,'WOLF','WolfCoin',0,8,8,8,'CRYPTO',8),(244,'REN','Republic Protocol',0,8,8,8,'CRYPTO',8),(245,'MET','Metronome',0,8,8,8,'CRYPTO',8),(246,'USDT','Tether US',0,8,8,8,'CRYPTO',8),(247,'PLTC','PlatonCoin',0,8,8,8,'CRYPTO',8),(248,'DIVI','Divicoin',0,8,8,8,'CRYPTO',8),(249,'BTT','BitTorrent',0,8,8,8,'CRYPTO',8),(250,'PPY','PeerPlays',0,8,8,8,'CRYPTO',8),(251,'VRBS','Viribustoken',0,8,8,8,'CRYPTO',8),(252,'ZUBE','Zuzubecoin',0,8,8,8,'CRYPTO',8),(253,'OWC','OduwaCoin',0,8,8,8,'CRYPTO',8),(254,'ELC','EconomicLeisureCoin',0,8,8,8,'CRYPTO',8),(255,'CSC','CasinoCoin',0,8,8,8,'CRYPTO',8),(256,'RBC','RobboCoach',0,8,8,8,'CRYPTO',8),(257,'TTT','TTV',0,8,8,8,'CRYPTO',8),(258,'REB','REBGLO',0,8,8,8,'CRYPTO',8),(263,'CREA','Crea',0,3,3,3,'CRYPTO',8),(265,'AISI','Aisicoin',0,8,8,8,'CRYPTO',NULL),(266,'RVC','RenvaleCoin',0,8,8,8,'CRYPTO',NULL),(267,'BIO','BioCrypt ',0,8,8,8,'CRYPTO',NULL),(268,'ETA','Etheera ',0,8,8,8,'CRYPTO',NULL),(269,'KAT','Kambria ',0,8,8,8,'CRYPTO',NULL),(270,'VRA','VERA',0,8,8,8,'CRYPTO',NULL),(271,'BRC','BaerChain',0,8,8,8,'CRYPTO',NULL),(272,'CRON','CRONFoundation',0,8,8,8,'CRYPTO',NULL),(273,'GNY','GNY token',0,8,8,8,'CRYPTO',NULL),(274,'NOVA','Novachain',0,8,8,8,'CRYPTO',NULL),(277,'IEO12','tesieo',1,8,8,8,'CRYPTO',NULL),(278,'DARC','Konstellation',0,6,6,6,'CRYPTO',NULL),(279,'EXTT','exrates token 1234567890',1,8,8,8,'CRYPTO',NULL),(291,'ICT','ICOTest',1,8,8,8,'CRYPTO',NULL),(292,'NWT','new test',1,8,8,8,'CRYPTO',NULL),(293,'TSL','TreasureSL',0,8,8,8,'CRYPTO',NULL),(294,'VOLLAR','V-Dimension',0,8,8,8,'CRYPTO',NULL),(295,'GAPI','GAPICOIN',0,8,8,8,'CRYPTO',NULL),(296,'FST','1irstcoin',0,8,8,8,'CRYPTO',NULL),(297,'RVT','Renvale Token',0,8,8,8,'CRYPTO',NULL),(298,'RWDS','Rewards4u',0,6,6,6,'CRYPTO',NULL),(299,'LHT','LightHouse',0,5,5,5,'CRYPTO',NULL),(300,'LINA','Token LINA',0,8,8,8,'CRYPTO',NULL);
/*!40000 ALTER TABLE `currency` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `currency_limit`
--

DROP TABLE IF EXISTS `currency_limit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `currency_limit` (
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
  CONSTRAINT `currency_limit___fk_currency` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
  CONSTRAINT `currency_limit___fk_optype` FOREIGN KEY (`operation_type_id`) REFERENCES `operation_type` (`id`),
  CONSTRAINT `currency_limit___fk_usr_role` FOREIGN KEY (`user_role_id`) REFERENCES `user_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `currency_limit`
--

LOCK TABLES `currency_limit` WRITE;
/*!40000 ALTER TABLE `currency_limit` DISABLE KEYS */;
/*!40000 ALTER TABLE `currency_limit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `currency_pair`
--

DROP TABLE IF EXISTS `currency_pair`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `currency_pair` (
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
  UNIQUE KEY `CURRENCY_PAIR_name_uindex` (`name`),
  KEY `hidden` (`hidden`)
) ENGINE=InnoDB AUTO_INCREMENT=849 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `currency_pair`
--

LOCK TABLES `currency_pair` WRITE;
/*!40000 ALTER TABLE `currency_pair` DISABLE KEYS */;
INSERT INTO `currency_pair` VALUES (1,4,2,'BTC/USD',13,0,'USD','BTC/USD',0,'MAIN',2),(2,4,3,'BTC/EUR',201,0,'FIAT','BTC/EUR',0,'MAIN',2),(3,4,1,'BTC/RUB',15,1,'FIAT','BTC/RUB',0,'MAIN',2),(4,5,4,'LTC/BTC',219,0,'BTC','LTC/BTC',0,'MAIN',8),(5,5,2,'LTC/USD',220,0,'USD','LTC/USD',0,'MAIN',2),(6,5,3,'LTC/EUR',NULL,1,'FIAT','LTC/EUR',0,'MAIN',2),(7,5,1,'LTC/RUB',NULL,1,'FIAT','LTC/RUB',0,'MAIN',2),(8,3,2,'EUR/USD',NULL,1,'USD','EUR/USD',0,'MAIN',2),(9,3,1,'EUR/RUB',NULL,1,'FIAT','EUR/RUB',0,'MAIN',2),(10,2,1,'USD/RUB',22,1,'FIAT','USD/RUB',0,'MAIN',2),(11,6,1,'EDRC/RUB',19,1,'FIAT','EDRC/RUB',0,'MAIN',2),(12,6,2,'EDRC/USD',18,1,'USD','EDRC/USD',0,'MAIN',2),(13,6,3,'EDRC/EUR',65,1,'FIAT','EDRC/EUR',0,'MAIN',2),(14,6,4,'EDRC/BTC',100,1,'BTC','EDRC/BTC',0,'MAIN',8),(15,6,5,'EDRC/LTC',NULL,1,'BTC','EDRC/LTC',0,'MAIN',8),(16,1,7,'RUB/UAH',NULL,1,'FIAT','RUB/UAH',0,'MAIN',2),(17,2,7,'USD/UAH',23,1,'FIAT','USD/UAH',0,'MAIN',2),(18,3,7,'EUR/UAH',21,1,'FIAT','EUR/UAH',0,'MAIN',2),(19,4,7,'BTC/UAH',16,1,'FIAT','BTC/UAH',0,'MAIN',2),(20,5,7,'LTC/UAH',NULL,1,'FIAT','LTC/UAH',0,'MAIN',2),(21,6,7,'EDRC/UAH',20,1,'FIAT','EDRC/UAH',0,'MAIN',2),(22,4,8,'BTC/CNY',202,0,'FIAT','BTC/CNY',0,'MAIN',2),(23,6,8,'EDRC/CNY',64,1,'FIAT','EDRC/CNY',0,'MAIN',2),(24,2,8,'USD/CNY',68,1,'FIAT','USD/CNY',0,'MAIN',2),(25,9,4,'EDC/BTC',300,0,'BTC','EDC/BTC',0,'MAIN',8),(26,9,2,'EDC/USD',300,0,'USD','EDC/USD',0,'MAIN',2),(27,9,8,'EDC/CNY',101,0,'FIAT','EDC/CNY',0,'MAIN',2),(28,9,1,'EDC/RUB',13,1,'FIAT','EDC/RUB',0,'MAIN',2),(29,9,7,'EDC/UAH',14,1,'FIAT','EDC/UAH',0,'MAIN',2),(30,6,10,'EDRC/IDR',62,1,'FIAT','EDRC/IDR',0,'MAIN',2),(31,9,10,'EDC/IDR',102,0,'FIAT','EDC/IDR',0,'MAIN',2),(32,4,10,'BTC/IDR',203,0,'FIAT','BTC/IDR',0,'MAIN',2),(33,4,11,'BTC/THB',200,1,'FIAT','BTC/THB',0,'MAIN',2),(34,9,11,'EDC/THB',201,1,'FIAT','EDC/THB',0,'MAIN',2),(35,6,11,'EDRC/THB',202,1,'FIAT','EDRC/THB',0,'MAIN',2),(36,2,11,'USD/THB',203,1,'FIAT','USD/THB',0,'MAIN',2),(37,9,12,'EDC/INR',210,1,'FIAT','EDC/INR',0,'MAIN',2),(38,9,13,'EDC/NGN',103,0,'FIAT','EDC/NGN',0,'MAIN',2),(39,4,13,'BTC/NGN',204,0,'FIAT','BTC/NGN',0,'MAIN',2),(40,14,2,'ETH/USD',213,0,'USD','ETH/USD',0,'MAIN',2),(41,14,4,'ETH/BTC',214,0,'BTC','ETH/BTC',0,'MAIN',8),(42,4,17,'BTC/VND',205,0,'FIAT','BTC/VND',0,'MAIN',2),(43,9,17,'EDC/VND',104,0,'FIAT','EDC/VND',0,'MAIN',2),(44,4,18,'BTC/TRY',206,0,'FIAT','BTC/TRY',0,'MAIN',2),(45,9,18,'EDC/TRY',105,0,'FIAT','EDC/TRY',0,'MAIN',2),(46,19,2,'ETC/USD',219,1,'USD','ETC/USD',0,'MAIN',2),(47,19,4,'ETC/BTC',220,1,'BTC','ETC/BTC',0,'MAIN',8),(48,4,20,'BTC/AED',207,0,'FIAT','BTC/AED',0,'MAIN',2),(49,9,20,'EDC/AED',106,0,'FIAT','EDC/AED',0,'MAIN',2),(50,21,2,'DASH/USD',223,0,'USD','DASH/USD',0,'MAIN',2),(51,21,4,'DASH/BTC',224,0,'BTC','DASH/BTC',0,'MAIN',8),(52,22,2,'XRP/USD',260,0,'USD','XRP/USD',0,'MAIN',2),(53,23,2,'XLM/USD',260,0,'USD','XLM/USD',0,'MAIN',2),(54,23,4,'XLM/BTC',250,0,'BTC','XLM/BTC',0,'MAIN',8),(55,22,4,'XRP/BTC',260,0,'BTC','XRP/BTC',0,'MAIN',8),(56,24,2,'XEM/USD',280,0,'USD','XEM/USD',0,'MAIN',2),(57,24,4,'XEM/BTC',280,0,'BTC','XEM/BTC',0,'MAIN',8),(58,25,2,'ATB/USD',10,0,'USD','ATB/USD',0,'MAIN',2),(59,25,4,'ATB/BTC',10,0,'BTC','ATB/BTC',0,'MAIN',8),(60,26,2,'BCH-old/USD',57,1,'USD','BCH-old/USD',0,'MAIN',2),(61,26,4,'BCH-old/BTC',58,1,'BTC','BCH-old/BTC',0,'MAIN',8),(62,9,26,'EDC/BCH-old',310,1,'BTC','EDC/BCH-old',0,'MAIN',8),(63,27,2,'IOTA/USD',280,0,'USD','MIOTA/USD',0,'MAIN',2),(64,27,4,'IOTA/BTC',270,0,'BTC','MIOTA/BTC',0,'MAIN',8),(65,28,2,'DOGE/USD',224,0,'USD','DOGE/USD',0,'MAIN',2),(66,28,4,'DOGE/BTC',225,0,'BTC','DOGE/BTC',0,'MAIN',8),(67,29,2,'LSK/USD',290,0,'USD','LSK/USD',0,'MAIN',2),(68,29,4,'LSK/BTC',290,0,'BTC','LSK/BTC',0,'MAIN',8),(69,30,2,'XMR/USD',282,0,'USD','XMR/USD',0,'MAIN',2),(70,30,4,'XMR/BTC',272,0,'BTC','XMR/BTC',0,'MAIN',8),(71,31,2,'EOS/USD',280,0,'USD','EOS/USD',0,'MAIN',2),(72,31,4,'EOS/BTC',270,0,'BTC','EOS/BTC',0,'MAIN',8),(73,32,2,'REP/USD',281,0,'USD','REP/USD',0,'MAIN',2),(74,32,4,'REP/BTC',270,0,'BTC','REP/BTC',0,'MAIN',8),(75,33,2,'GNT/USD',281,0,'USD','GNT/USD',0,'MAIN',2),(76,33,4,'GNT/BTC',270,0,'BTC','GNT/BTC',0,'MAIN',8),(77,34,2,'BTG/USD',20,0,'USD','BTG/USD',0,'MAIN',2),(78,34,4,'BTG/BTC',60,0,'BTC','BTG/BTC',0,'MAIN',8),(79,35,2,'B2X/USD',10,0,'USD','B2X/USD',0,'MAIN',2),(80,35,4,'B2X/BTC',30,0,'BTC','B2X/BTC',0,'MAIN',8),(81,35,34,'B2X/BTG',20,1,'BTC','B2X/BTG',0,'MAIN',8),(82,36,2,'BCD/USD',20,0,'USD','BCD/USD',0,'MAIN',2),(83,36,4,'BCD/BTC',60,0,'BTC','BCD/BTC',0,'MAIN',8),(84,37,2,'NEO/USD',20,0,'USD','NEO/USD',0,'MAIN',2),(85,37,4,'NEO/BTC',60,0,'BTC','NEO/BTC',0,'MAIN',8),(86,38,2,'GAS/USD',20,0,'USD','GAS/USD',0,'MAIN',2),(87,38,4,'GAS/BTC',60,0,'BTC','GAS/BTC',0,'MAIN',8),(88,39,2,'ZEC/USD',282,0,'USD','ZEC/USD',0,'MAIN',2),(89,39,4,'ZEC/BTC',270,0,'BTC','ZEC/BTC',0,'MAIN',8),(90,40,2,'OMG/USD',270,0,'USD','OMG/USD',0,'MAIN',2),(91,40,4,'OMG/BTC',260,0,'BTC','OMG/BTC',0,'MAIN',8),(92,41,2,'WAVES/USD',290,0,'USD','WAVES/USD',0,'MAIN',2),(93,41,4,'WAVES/BTC',290,0,'BTC','WAVES/BTC',0,'MAIN',8),(94,42,2,'BCX/USD',20,0,'USD','BTCX/USD',0,'MAIN',2),(95,42,4,'BCX/BTC',60,0,'BTC','BCX/BTC',0,'MAIN',8),(96,43,2,'ATL/USD',11,0,'USD','ATL/USD',0,'MAIN',2),(97,43,4,'ATL/BTC',20,0,'BTC','ATL/BTC',0,'MAIN',8),(98,44,2,'BCA/USD',12,0,'USD','BCA/USD',0,'MAIN',2),(99,44,4,'BCA/BTC',50,0,'BTC','BCA/BTC',0,'MAIN',8),(100,45,2,'BNB/USD',70,0,'USD','BNB/USD',0,'MAIN',2),(101,45,4,'BNB/BTC',60,0,'BTC','BNB/BTC',0,'MAIN',8),(102,47,2,'SBTC/USD',20,1,'USD','SBTC/USD',0,'MAIN',2),(103,47,4,'SBTC/BTC',60,1,'BTC','SBTC/BTC',0,'MAIN',8),(104,48,2,'OCC/USD',12,0,'USD','OCC/USD',0,'MAIN',2),(105,48,4,'OCC/BTC',50,0,'BTC','OCC/BTC',0,'MAIN',8),(106,48,14,'OCC/ETH',50,0,'ETH','OCC/ETH',0,'MAIN',8),(108,35,14,'B2X/ETH',50,0,'ETH','B2X/ETH',0,'MAIN',8),(109,49,2,'RNTB/USD',170,0,'USD','RNTB/USD',0,'MAIN',2),(110,49,4,'RNTB/BTC',160,0,'BTC','RNTB/BTC',0,'MAIN',8),(111,49,14,'RNTB/ETH',160,0,'ETH','RNTB/ETH',0,'MAIN',8),(112,50,2,'ETZ/USD',20,0,'USD','ETZ/USD',0,'MAIN',2),(113,50,4,'ETZ/BTC',60,0,'BTC','ETZ/BTC',0,'MAIN',8),(114,51,2,'NIO/USD',20,0,'USD','NIO/USD',1,'MAIN',2),(115,51,4,'NIO/BTC',60,0,'BTC','NIO/BTC',1,'MAIN',8),(116,52,2,'GOS/USD',20,0,'USD','GOS/USD',0,'MAIN',2),(117,52,4,'GOS/BTC',60,0,'BTC','GOS/BTC',0,'MAIN',8),(118,52,14,'GOS/ETH',50,0,'ETH','GOS/ETH',0,'MAIN',8),(119,53,2,'BTCZ/USD',12,0,'USD','BTCZ/USD',0,'MAIN',2),(120,53,4,'BTCZ/BTC',50,0,'BTC','BTCZ/BTC',0,'MAIN',8),(121,53,14,'BTCZ/ETH',50,0,'ETH','BTCZ/ETH',0,'MAIN',8),(122,54,2,'BTW/USD',12,0,'USD','BTW/USD',0,'MAIN',2),(123,54,4,'BTW/BTC',50,0,'BTC','BTW/BTC',0,'MAIN',8),(124,54,14,'BTW/ETH',50,0,'ETH','BTW/ETH',0,'MAIN',8),(125,55,2,'BPTN/USD',170,0,'USD','BPTN/USD',0,'MAIN',2),(126,55,4,'BPTN/BTC',160,0,'BTC','BPTN/BTC',0,'MAIN',8),(127,55,14,'BPTN/ETH',160,0,'ETH','BPTN/ETH',0,'MAIN',8),(128,56,2,'QTUM/USD',20,0,'USD','QTUM/USD',0,'MAIN',2),(129,56,4,'QTUM/BTC',60,0,'BTC','QTUM/BTC',0,'MAIN',8),(130,56,14,'QTUM/ETH',50,0,'ETH','QTUM/ETH',0,'MAIN',8),(131,57,2,'BTCP/USD',20,1,'USD','BTCP/USD',0,'MAIN',2),(132,57,4,'BTCP/BTC',60,1,'BTC','BTCP/BTC',0,'MAIN',8),(133,57,14,'BTCP/ETH',60,1,'ETH','BTCP/ETH',0,'MAIN',8),(134,58,2,'TAXI/USD',170,1,'USD','TAXI/USD',0,'MAIN',2),(135,58,4,'TAXI/BTC',160,1,'BTC','TAXI/BTC',0,'MAIN',8),(136,58,14,'TAXI/ETH',160,1,'ETH','TAXI/ETH',0,'MAIN',8),(137,59,2,'LCC/USD',12,1,'USD','LCC/USD',0,'MAIN',2),(138,59,4,'LCC/BTC',50,1,'BTC','LCC/BTC',0,'MAIN',8),(139,59,14,'LCC/ETH',50,1,'ETH','LCC/ETH',0,'MAIN',8),(140,60,2,'GX/USD',12,0,'USD','GX/USD',0,'MAIN',2),(141,60,4,'GX/BTC',50,0,'BTC','GX/BTC',0,'MAIN',8),(142,60,14,'GX/ETH',50,0,'ETH','GX/ETH',0,'MAIN',8),(143,61,2,'INK/USD',20,0,'USD','INK/USD',0,'MAIN',2),(144,61,4,'INK/BTC',60,0,'BTC','INK/BTC',0,'MAIN',8),(145,61,14,'INK/ETH',50,0,'ETH','INK/ETH',0,'MAIN',8),(146,62,2,'DIM/USD',170,0,'USD','DIM/USD',0,'MAIN',2),(147,62,4,'DIM/BTC',160,0,'BTC','DIM/BTC',0,'MAIN',8),(148,62,14,'DIM/ETH',160,0,'ETH','DIM/ETH',0,'MAIN',8),(149,63,2,'NBTK/USD',170,0,'USD','NBTK/USD',0,'MAIN',2),(150,63,4,'NBTK/BTC',160,0,'BTC','NBTK/BTC',0,'MAIN',8),(151,63,14,'NBTK/ETH',160,0,'ETH','NBTK/ETH',0,'MAIN',8),(152,64,2,'NBC/USD',170,0,'USD','NBC/USD',0,'MAIN',2),(153,64,4,'NBC/BTC',160,0,'BTC','NBC/BTC',0,'MAIN',8),(154,64,14,'NBC/ETH',160,0,'ETH','NBC/ETH',0,'MAIN',8),(155,65,2,'UCASH/USD',170,0,'USD','UCASH/USD',0,'MAIN',2),(156,65,4,'UCASH/BTC',160,0,'BTC','UCASH/BTC',0,'MAIN',8),(157,65,14,'UCASH/ETH',160,0,'ETH','UCASH/ETH',0,'MAIN',8),(158,66,2,'PLC/USD',20,0,'USD','PLC/USD',0,'MAIN',2),(159,66,4,'PLC/BTC',60,0,'BTC','PLC/BTC',0,'MAIN',8),(160,66,14,'PLC/ETH',60,0,'ETH','PLC/ETH',0,'MAIN',8),(161,67,2,'NAC/USD',170,0,'USD','NAC/USD',0,'MAIN',2),(162,67,4,'NAC/BTC',160,0,'BTC','NAC/BTC',0,'MAIN',8),(163,67,14,'NAC/ETH',160,0,'ETH','NAC/ETH',0,'MAIN',8),(164,68,2,'ECHT/USD',170,0,'USD','ECHT/USD',0,'MAIN',2),(165,68,4,'ECHT/BTC',160,0,'BTC','ECHT/BTC',0,'MAIN',8),(166,68,14,'ECHT/ETH',160,0,'ETH','ECHT/ETH',0,'MAIN',8),(167,50,14,'ETZ/ETH',60,0,'ETH','ETZ/ETH',0,'MAIN',8),(168,69,2,'DDX/USD',170,1,'USD','DDX/USD',0,'MAIN',2),(169,69,4,'DDX/BTC',160,1,'BTC','DDX/BTC',0,'MAIN',8),(170,69,14,'DDX/ETH',160,1,'ETH','DDX/ETH',0,'MAIN',8),(171,70,4,'DIT/BTC',60,0,'BTC','DIT/BTC',0,'MAIN',8),(172,71,2,'IDH/USD',160,0,'USD','IDH/USD',0,'MAIN',2),(173,71,4,'IDH/BTC',150,0,'BTC','IDH/BTC',0,'MAIN',8),(174,71,14,'IDH/ETH',150,0,'ETH','IDH/ETH',0,'MAIN',8),(175,72,2,'SZC/USD',12,0,'USD','SZC/USD',0,'MAIN',2),(176,72,4,'SZC/BTC',50,0,'BTC','SZC/BTC',0,'MAIN',8),(177,72,14,'SZC/ETH',50,0,'ETH','SZC/ETH',0,'MAIN',8),(178,73,2,'COBC/USD',170,0,'USD','COBC/USD',0,'MAIN',2),(179,73,4,'COBC/BTC',160,0,'BTC','COBC/BTC',0,'MAIN',8),(180,73,14,'COBC/ETH',160,0,'ETH','COBC/ETH',0,'MAIN',8),(181,74,2,'BTX/USD',12,0,'USD','BTX/USD',0,'MAIN',2),(182,74,4,'BTX/BTC',50,0,'BTC','BTX/BTC',0,'MAIN',8),(183,74,14,'BTX/ETH',50,0,'ETH','BTX/ETH',0,'MAIN',8),(184,74,3,'BTX/EUR',50,0,'FIAT','BTX/EUR',0,'MAIN',2),(185,75,2,'BCI/USD',20,0,'USD','BCI/USD',0,'MAIN',2),(186,75,4,'BCI/BTC',60,0,'BTC','BCI/BTC',0,'MAIN',8),(187,75,14,'BCI/ETH',160,0,'ETH','BCI/ETH',0,'MAIN',8),(188,76,2,'LBTC/USD',150,0,'USD','LBTC/USD',0,'MAIN',2),(189,76,4,'LBTC/BTC',150,0,'BTC','LBTC/BTC',0,'MAIN',8),(190,76,14,'LBTC/ETH',150,0,'ETH','LBTC/ETH',0,'MAIN',8),(191,77,2,'BCS/USD',170,0,'USD','BCS/USD',0,'MAIN',2),(192,77,4,'BCS/BTC',160,0,'BTC','BCS/BTC',0,'MAIN',8),(193,77,14,'BCS/ETH',160,0,'ETH','BCS/ETH',0,'MAIN',8),(194,78,2,'XBD/USD',12,1,'USD','XBD/USD',0,'MAIN',2),(195,78,4,'XBD/BTC',50,1,'BTC','XBD/BTC',0,'MAIN',8),(196,78,14,'XBD/ETH',50,1,'ETH','XBD/ETH',0,'MAIN',8),(197,79,2,'SLT/USD',160,0,'USD','SLT/USD',1,'MAIN',2),(198,79,4,'SLT/BTC',150,0,'BTC','SLT/BTC',1,'MAIN',8),(199,79,14,'SLT/ETH',150,0,'ETH','SLT/ETH',1,'MAIN',8),(200,80,2,'UQC/USD',160,0,'USD','UQC/USD',1,'MAIN',2),(201,80,4,'UQC/BTC',150,0,'BTC','UQC/BTC',1,'MAIN',8),(202,80,14,'UQC/ETH',150,0,'ETH','UQC/ETH',1,'MAIN',8),(203,51,14,'NIO/ETH',50,0,'ETH','NIO/ETH',1,'MAIN',8),(204,81,2,'INO/USD',170,0,'USD','INO/USD',1,'MAIN',2),(205,81,4,'INO/BTC',160,0,'BTC','INO/BTC',1,'MAIN',8),(206,81,14,'INO/ETH',160,0,'ETH','INO/ETH',1,'MAIN',8),(207,82,2,'ORME/USD',170,0,'USD','ORME/USD',1,'MAIN',2),(208,82,4,'ORME/BTC',160,0,'BTC','ORME/BTC',1,'MAIN',8),(209,82,14,'ORME/ETH',160,0,'ETH','ORME/ETH',1,'MAIN',8),(210,83,2,'PROFIT/USD',160,0,'USD','PROFIT/USD',0,'MAIN',2),(211,83,4,'PROFIT/BTC',150,0,'BTC','PROFIT/BTC',0,'MAIN',8),(212,83,14,'PROFIT/ETH',150,0,'ETH','PROFIT/ETH',0,'MAIN',8),(213,84,2,'BEZ/USD',170,0,'USD','BEZ/USD',1,'MAIN',2),(214,84,4,'BEZ/BTC',160,0,'BTC','BEZ/BTC',1,'MAIN',8),(215,84,14,'BEZ/ETH',160,0,'ETH','BEZ/ETH',1,'MAIN',8),(216,85,2,'AMN/USD',170,0,'USD','AMN/USD',0,'MAIN',2),(217,85,4,'AMN/BTC',160,0,'BTC','AMN/BTC',0,'MAIN',8),(218,85,14,'AMN/ETH',160,0,'ETH','AMN/ETH',0,'MAIN',8),(219,86,2,'RISE/USD',12,0,'USD','RISE/USD',0,'MAIN',2),(220,86,4,'RISE/BTC',50,0,'BTC','RISE/BTC',0,'MAIN',8),(221,86,14,'RISE/ETH',50,0,'ETH','RISE/ETH',0,'MAIN',8),(222,87,2,'GET/USD',170,0,'USD','GET/USD',1,'MAIN',2),(223,87,4,'GET/BTC',160,0,'BTC','GET/BTC',1,'MAIN',8),(224,87,14,'GET/ETH',160,0,'ETH','GET/ETH',1,'MAIN',8),(225,88,2,'SIM/USD',170,0,'USD','SIM/USD',1,'MAIN',2),(226,88,4,'SIM/BTC',160,0,'BTC','SIM/BTC',1,'MAIN',8),(227,88,14,'SIM/ETH',160,0,'ETH','SIM/ETH',1,'MAIN',8),(228,89,2,'FLOT/USD',170,0,'USD','FLOT/USD',1,'MAIN',2),(229,89,4,'FLOT/BTC',160,0,'BTC','FLOT/BTC',1,'MAIN',8),(230,89,14,'FLOT/ETH',160,0,'ETH','FLOT/ETH',1,'MAIN',8),(231,90,2,'ABTC/USD',20,0,'USD','ABTC/USD',0,'MAIN',2),(232,90,4,'ABTC/BTC',60,0,'BTC','ABTC/BTC',0,'MAIN',8),(233,90,14,'ABTC/ETH',160,0,'ETH','ABTC/ETH',0,'MAIN',8),(234,91,2,'VDG/USD',170,0,'USD','VDG/USD',0,'MAIN',2),(235,91,4,'VDG/BTC',160,0,'BTC','VDG/BTC',0,'MAIN',8),(236,91,14,'VDG/ETH',160,0,'ETH','VDG/ETH',0,'MAIN',8),(237,92,2,'NPXSXEM/USD',170,0,'USD','NPXSXEM/USD',1,'MAIN',2),(238,92,4,'NPXSXEM/BTC',160,0,'BTC','NPXSXEM/BTC',1,'MAIN',8),(239,92,14,'NPXSXEM/ETH',160,0,'ETH','NPXSXEM/ETH',1,'MAIN',8),(240,93,2,'DRONE/USD',170,0,'USD','DRONE/USD',0,'MAIN',2),(241,93,4,'DRONE/BTC',160,0,'BTC','DRONE/BTC',0,'MAIN',8),(242,93,14,'DRONE/ETH',160,0,'ETH','DRONE/ETH',0,'MAIN',8),(243,94,2,'DGTX/USD',170,0,'USD','DGTX/USD',0,'MAIN',2),(244,94,4,'DGTX/BTC',160,0,'BTC','DGTX/BTC',0,'MAIN',8),(245,94,14,'DGTX/ETH',160,0,'ETH','DGTX/ETH',0,'MAIN',8),(246,95,2,'NSR/USD',12,0,'USD','NSR/USD',0,'MAIN',2),(247,95,4,'NSR/BTC',50,0,'BTC','NSR/BTC',0,'MAIN',8),(248,95,14,'NSR/ETH',50,0,'ETH','NSR/ETH',0,'MAIN',8),(249,96,2,'ARK/USD',12,0,'USD','ARK/USD',0,'MAIN',2),(250,96,4,'ARK/BTC',50,0,'BTC','ARK/BTC',0,'MAIN',8),(251,96,14,'ARK/ETH',50,0,'ETH','ARK/ETH',0,'MAIN',8),(252,97,2,'WDSC/USD',170,1,'USD','WDSC/USD',1,'MAIN',2),(253,97,4,'WDSC/BTC',160,1,'BTC','WDSC/BTC',1,'MAIN',8),(254,97,14,'WDSC/ETH',160,1,'ETH','WDSC/ETH',1,'MAIN',8),(255,98,2,'FSBT/USD',170,0,'USD','FSBT/USD',1,'MAIN',2),(256,98,4,'FSBT/BTC',160,0,'BTC','FSBT/BTC',1,'MAIN',8),(257,98,14,'FSBT/ETH',160,0,'ETH','FSBT/ETH',1,'MAIN',8),(258,99,2,'RTH/USD',170,0,'USD','RTH/USD',0,'MAIN',2),(259,99,4,'RTH/BTC',160,0,'BTC','RTH/BTC',0,'MAIN',8),(260,99,14,'RTH/ETH',160,0,'ETH','RTH/ETH',0,'MAIN',8),(261,100,2,'IPR/USD',170,0,'USD','IPR/USD',0,'MAIN',2),(262,100,4,'IPR/BTC',160,0,'BTC','IPR/BTC',0,'MAIN',8),(263,100,14,'IPR/ETH',160,0,'ETH','IPR/ETH',0,'MAIN',8),(264,101,2,'CAS/USD',170,0,'USD','CAS/USD',1,'MAIN',2),(265,101,4,'CAS/BTC',160,0,'BTC','CAS/BTC',1,'MAIN',8),(266,101,14,'CAS/ETH',160,0,'ETH','CAS/ETH',1,'MAIN',8),(267,102,2,'SPC/USD',170,0,'USD','SPC/USD',0,'MAIN',2),(268,102,4,'SPC/BTC',160,0,'BTC','SPC/BTC',0,'MAIN',8),(269,102,14,'SPC/ETH',160,0,'ETH','SPC/ETH',0,'MAIN',8),(270,103,2,'SPD/USD',170,0,'USD','SPD/USD',1,'MAIN',2),(271,103,4,'SPD/BTC',160,0,'BTC','SPD/BTC',1,'MAIN',8),(272,103,14,'SPD/ETH',160,0,'ETH','SPD/ETH',1,'MAIN',8),(273,104,2,'BBX/USD',12,0,'USD','BBX/USD',0,'MAIN',2),(274,104,4,'BBX/BTC',50,0,'BTC','BBX/BTC',0,'MAIN',8),(275,104,14,'BBX/ETH',50,0,'ETH','BBX/ETH',0,'MAIN',8),(276,105,2,'BEET/USD',170,0,'USD','BEET/USD',0,'MAIN',2),(277,105,4,'BEET/BTC',160,0,'BTC','BEET/BTC',0,'MAIN',8),(278,105,14,'BEET/ETH',160,0,'ETH','BEET/ETH',0,'MAIN',8),(279,106,2,'MTC/USD',170,0,'USD','MTC/USD',1,'MAIN',2),(280,106,4,'MTC/BTC',160,0,'BTC','MTC/BTC',1,'MAIN',8),(281,106,14,'MTC/ETH',160,0,'ETH','MTC/ETH',1,'MAIN',8),(282,107,2,'HLC/USD',170,0,'USD','HLC/USD',0,'MAIN',2),(283,107,4,'HLC/BTC',160,0,'BTC','HLC/BTC',0,'MAIN',8),(284,107,14,'HLC/ETH',160,0,'ETH','HLC/ETH',0,'MAIN',8),(285,108,2,'DTRC/USD',170,0,'USD','DTRC/USD',1,'MAIN',2),(286,108,4,'DTRC/BTC',160,0,'BTC','DTRC/BTC',1,'MAIN',8),(287,108,14,'DTRC/ETH',160,0,'ETH','DTRC/ETH',1,'MAIN',8),(288,109,2,'CLO/USD',170,0,'USD','CLO/USD',0,'MAIN',2),(289,109,4,'CLO/BTC',160,0,'BTC','CLO/BTC',0,'MAIN',8),(290,109,14,'CLO/ETH',160,0,'ETH','CLO/ETH',0,'MAIN',8),(291,110,2,'TNR/USD',170,0,'USD','TNR/USD',0,'MAIN',2),(292,110,4,'TNR/BTC',160,0,'BTC','TNR/BTC',0,'MAIN',8),(293,110,14,'TNR/ETH',160,0,'ETH','TNR/ETH',0,'MAIN',8),(294,111,2,'CEEK/USD',170,0,'USD','CEEK/USD',1,'MAIN',2),(295,111,4,'CEEK/BTC',160,0,'BTC','CEEK/BTC',1,'MAIN',8),(296,111,14,'CEEK/ETH',160,0,'ETH','CEEK/ETH',1,'MAIN',8),(297,112,2,'NYC/USD',20,0,'USD','NYC/USD',0,'MAIN',2),(298,112,4,'NYC/BTC',60,0,'BTC','NYC/BTC',0,'MAIN',8),(299,112,14,'NYC/ETH',160,0,'ETH','NYC/ETH',0,'MAIN',8),(300,113,2,'B2G/USD',170,0,'USD','B2G/USD',0,'MAIN',2),(301,113,4,'B2G/BTC',160,0,'BTC','B2G/BTC',0,'MAIN',8),(302,113,14,'B2G/ETH',160,0,'ETH','B2G/ETH',0,'MAIN',8),(303,114,2,'ENGT/USD',170,1,'USD','ENGT/USD',0,'MAIN',2),(304,114,4,'ENGT/BTC',160,1,'BTC','ENGT/BTC',0,'MAIN',8),(305,114,14,'ENGT/ETH',160,1,'ETH','ENGT/ETH',0,'MAIN',8),(306,115,2,'ARN/USD',170,0,'USD','ARN/USD',1,'MAIN',2),(307,115,4,'ARN/BTC',160,0,'BTC','ARN/BTC',1,'MAIN',8),(308,115,14,'ARN/ETH',160,0,'ETH','ARN/ETH',1,'MAIN',8),(309,116,2,'ANY/USD',170,1,'USD','ANY/USD',0,'MAIN',2),(310,116,4,'ANY/BTC',160,1,'BTC','ANY/BTC',0,'MAIN',8),(311,116,14,'ANY/ETH',160,1,'ETH','ANY/ETH',0,'MAIN',8),(312,117,2,'FGC/USD',20,0,'USD','FGC/USD',0,'MAIN',2),(313,117,4,'FGC/BTC',60,0,'BTC','FGC/BTC',0,'MAIN',8),(314,117,14,'FGC/ETH',160,0,'ETH','FGC/ETH',0,'MAIN',8),(315,118,2,'TGAME/USD',170,0,'USD','TGAME/USD',1,'MAIN',2),(316,118,4,'TGAME/BTC',160,0,'BTC','TGAME/BTC',1,'MAIN',8),(317,118,14,'TGAME/ETH',160,0,'ETH','TGAME/ETH',1,'MAIN',8),(318,119,2,'HST/USD',170,0,'USD','HST/USD',0,'MAIN',2),(319,119,4,'HST/BTC',160,0,'BTC','HST/BTC',0,'MAIN',8),(320,119,14,'HST/ETH',160,0,'ETH','HST/ETH',0,'MAIN',8),(321,120,2,'TAVITT/USD',170,1,'USD','TAVITT/USD',0,'MAIN',2),(322,120,4,'TAVITT/BTC',160,0,'BTC','TAVITT/BTC',0,'MAIN',8),(323,120,14,'TAVITT/ETH',160,1,'ETH','TAVITT/ETH',0,'MAIN',8),(324,121,2,'PTC/USD',12,0,'USD','PTC/USD',0,'MAIN',2),(325,121,4,'PTC/BTC',50,0,'BTC','PTC/BTC',0,'MAIN',8),(326,121,14,'PTC/ETH',50,0,'ETH','PTC/ETH',0,'MAIN',8),(327,122,2,'MTL/USD',170,0,'USD','MTL/USD',0,'MAIN',2),(328,122,4,'MTL/BTC',160,0,'BTC','MTL/BTC',0,'MAIN',8),(329,122,14,'MTL/ETH',160,0,'ETH','MTL/ETH',0,'MAIN',8),(330,123,2,'GOL/USD',170,0,'USD','GOL/USD',0,'MAIN',2),(331,123,4,'GOL/BTC',160,0,'BTC','GOL/BTC',0,'MAIN',8),(332,123,14,'GOL/ETH',160,0,'ETH','GOL/ETH',0,'MAIN',8),(333,124,4,'LEDU/BTC',160,0,'BTC','LEDU/BTC',0,'MAIN',8),(334,124,14,'LEDU/ETH',160,0,'ETH','LEDU/ETH',0,'MAIN',8),(335,125,2,'HSR/USD',12,0,'USD','HSR/USD',0,'MAIN',2),(336,125,4,'HSR/BTC',50,0,'BTC','HSR/BTC',0,'MAIN',8),(337,125,14,'HSR/ETH',50,0,'ETH','HSR/ETH',0,'MAIN',8),(338,126,2,'CNET/USD',170,1,'USD','CNET/USD',0,'MAIN',2),(339,126,4,'CNET/BTC',160,1,'BTC','CNET/BTC',0,'MAIN',8),(340,126,14,'CNET/ETH',160,1,'ETH','CNET/ETH',0,'MAIN',8),(341,127,2,'CEDEX/USD',170,0,'USD','CEDEX/USD',1,'MAIN',2),(342,127,4,'CEDEX/BTC',160,0,'BTC','CEDEX/BTC',1,'MAIN',8),(343,127,14,'CEDEX/ETH',160,0,'ETH','CEDEX/ETH',1,'MAIN',8),(344,128,2,'LUNES/USD',170,0,'USD','LNS/USD',0,'MAIN',2),(345,128,4,'LUNES/BTC',160,0,'BTC','LUNES/BTC',0,'MAIN',8),(346,128,14,'LUNES/ETH',160,0,'ETH','LNS/ETH',0,'MAIN',8),(347,129,2,'ADB/USD',170,0,'USD','ADB/USD',0,'MAIN',2),(348,129,4,'ADB/BTC',160,0,'BTC','ADB/BTC',0,'MAIN',8),(349,129,14,'ADB/ETH',160,0,'ETH','ADB/ETH',0,'MAIN',8),(350,130,2,'CHE/USD',170,0,'USD','CHE/USD',0,'MAIN',2),(351,130,4,'CHE/BTC',160,0,'BTC','CHE/BTC',0,'MAIN',8),(352,130,14,'CHE/ETH',160,0,'ETH','CHE/ETH',0,'MAIN',8),(353,131,2,'GST/USD',170,0,'USD','GST/USD',0,'MAIN',2),(354,131,4,'GST/BTC',160,0,'BTC','GST/BTC',0,'MAIN',8),(355,131,14,'GST/ETH',160,0,'ETH','GST/ETH',0,'MAIN',8),(356,132,2,'BCL/USD',12,0,'USD','BCL/USD',0,'MAIN',2),(357,132,4,'BCL/BTC',50,0,'BTC','BCL/BTC',0,'MAIN',8),(358,132,14,'BCL/ETH',50,0,'ETH','BCL/ETH',0,'MAIN',8),(359,133,2,'BRECO/USD',170,1,'USD','BRECO/USD',0,'MAIN',2),(360,133,4,'BRECO/BTC',160,1,'BTC','BRECO/BTC',0,'MAIN',8),(361,133,14,'BRECO/ETH',160,1,'ETH','BRECO/ETH',0,'MAIN',8),(362,134,2,'ACT/USD',170,0,'USD','ACT/USD',0,'MAIN',2),(363,134,4,'ACT/BTC',160,0,'BTC','ACT/BTC',0,'MAIN',8),(364,134,14,'ACT/ETH',160,0,'ETH','ACT/ETH',0,'MAIN',8),(365,135,2,'VEX/USD',270,1,'USD','VEX/USD',0,'MAIN',2),(366,135,4,'VEX/BTC',260,0,'BTC','VEX/BTC',1,'MAIN',8),(367,135,14,'VEX/ETH',260,1,'ETH','VEX/ETH',0,'MAIN',8),(368,136,2,'FTO/USD',20,0,'USD','FTO/USD',0,'MAIN',2),(369,136,4,'FTO/BTC',60,0,'BTC','FTO/BTC',0,'MAIN',8),(370,136,14,'FTO/ETH',160,0,'ETH','FTO/ETH',0,'MAIN',8),(371,137,2,'UMT/USD',170,0,'USD','UMT/USD',1,'MAIN',2),(372,137,4,'UMT/BTC',160,0,'BTC','UMT/BTC',1,'MAIN',8),(373,137,14,'UMT/ETH',160,0,'ETH','UMT/ETH',1,'MAIN',8),(374,138,2,'SABR/USD',12,0,'USD','SABR/USD',0,'MAIN',2),(375,138,4,'SABR/BTC',50,0,'BTC','SABR/BTC',0,'MAIN',8),(376,138,14,'SABR/ETH',50,0,'ETH','SABR/ETH',0,'MAIN',8),(377,139,2,'EQL/USD',170,0,'USD','EQL/USD',1,'MAIN',2),(378,139,4,'EQL/BTC',160,0,'BTC','EQL/BTC',1,'MAIN',8),(379,139,14,'EQL/ETH',160,0,'ETH','EQL/ETH',1,'MAIN',8),(380,140,2,'MASP/USD',170,0,'USD','MASP/USD',0,'MAIN',2),(381,140,4,'MASP/BTC',160,0,'BTC','MASP/BTC',0,'MAIN',8),(382,140,14,'MASP/ETH',160,0,'ETH','MASP/ETH',0,'MAIN',8),(383,141,2,'VNT/USD',160,0,'USD','VNT/USD',0,'MAIN',2),(384,141,4,'VNT/BTC',150,0,'BTC','VNT/BTC',0,'MAIN',8),(385,141,14,'VNT/ETH',150,0,'ETH','VNT/ETH',0,'MAIN',8),(386,70,2,'DIT/USD',170,0,'USD','DIT/USD',0,'MAIN',2),(387,70,14,'DIT/ETH',160,0,'ETH','DIT/ETH',0,'MAIN',8),(388,143,2,'SKILL/USD',170,0,'USD','SKILL/USD',0,'MAIN',2),(389,143,4,'SKILL/BTC',160,0,'BTC','SKILL/BTC',0,'MAIN',8),(390,143,14,'SKILL/ETH',160,0,'ETH','SKILL/ETH',0,'MAIN',8),(391,144,2,'SAT/USD',170,0,'USD','SAT/USD',0,'MAIN',2),(392,144,4,'SAT/BTC',160,0,'BTC','SAT/BTC',0,'MAIN',8),(393,144,14,'SAT/ETH',160,0,'ETH','SAT/ETH',0,'MAIN',8),(394,145,2,'STOR/USD',170,0,'USD','STOR/USD',0,'MAIN',2),(395,145,4,'STOR/BTC',160,0,'BTC','STOR/BTC',0,'MAIN',8),(396,145,14,'STOR/ETH',160,0,'ETH','STOR/ETH',0,'MAIN',8),(397,146,2,'DACC/USD',170,0,'USD','DACC/USD',0,'MAIN',2),(398,146,4,'DACC/BTC',160,0,'BTC','DACC/BTC',0,'MAIN',8),(399,146,14,'DACC/ETH',160,0,'ETH','DACC/ETH',0,'MAIN',8),(400,147,2,'QUiNT/USD',170,0,'USD','QUiNT/USD',1,'MAIN',2),(401,147,4,'QUiNT/BTC',160,1,'BTC','QUiNT/BTC',1,'MAIN',8),(402,147,14,'QUiNT/ETH',160,1,'ETH','QUiNT/ETH',1,'MAIN',8),(403,148,2,'TERN/USD',160,0,'USD','TERN/USD',0,'MAIN',2),(404,148,4,'TERN/BTC',150,0,'BTC','TERN/BTC',0,'MAIN',8),(405,148,14,'TERN/ETH',150,0,'ETH','TERN/ETH',0,'MAIN',8),(406,149,2,'TTC/USD',170,0,'USD','TTC/USD',0,'MAIN',2),(407,149,4,'TTC/BTC',160,0,'BTC','TTC/BTC',0,'MAIN',8),(408,149,14,'TTC/ETH',160,0,'ETH','TTC/ETH',0,'MAIN',8),(409,150,2,'NTY/USD',170,0,'USD','NTY/USD',0,'MAIN',2),(410,150,4,'NTY/BTC',160,1,'BTC','NTY/BTC',0,'MAIN',8),(411,150,14,'NTY/ETH',160,1,'ETH','NTY/ETH',0,'MAIN',8),(412,151,2,'BFG/USD',170,0,'USD','BFG/USD',0,'MAIN',2),(413,151,4,'BFG/BTC',160,0,'BTC','BFG/BTC',0,'MAIN',8),(414,151,14,'BFG/ETH',160,0,'ETH','BFG/ETH',0,'MAIN',8),(415,152,2,'SUMO/USD',170,0,'USD','SUMO/USD',0,'MAIN',2),(416,152,4,'SUMO/BTC',160,0,'BTC','SUMO/BTC',0,'MAIN',8),(417,152,14,'SUMO/ETH',160,0,'ETH','SUMO/ETH',0,'MAIN',8),(418,153,4,'KGS/BTC',160,0,'ICO','KGS/BTC',0,'ICO',NULL),(419,153,14,'KGS/ETH',160,0,'ICO','KGS/ETH',0,'ICO',NULL),(420,154,2,'BRB/USD',170,0,'USD','BRB/USD',0,'MAIN',2),(421,154,4,'BRB/BTC',160,0,'BTC','BRB/BTC',0,'MAIN',8),(422,154,14,'BRB/ETH',160,0,'ETH','BRB/ETH',0,'MAIN',8),(423,155,2,'JET/USD',170,0,'USD','JET/USD',0,'MAIN',2),(424,155,4,'JET/BTC',160,0,'BTC','JET/BTC',0,'MAIN',8),(425,155,14,'JET/ETH',160,0,'ETH','JET/ETH',0,'MAIN',8),(426,156,2,'RIZ/USD',170,0,'USD','RIZ/USD',0,'MAIN',2),(427,156,4,'RIZ/BTC',160,0,'BTC','RIZ/BTC',0,'MAIN',8),(428,156,14,'RIZ/ETH',160,0,'ETH','RIZ/ETH',0,'MAIN',8),(429,157,2,'PAT/USD',170,0,'USD','PAT/USD',0,'MAIN',2),(430,157,4,'PAT/BTC',160,0,'BTC','PAT/BTC',0,'MAIN',8),(431,157,14,'PAT/ETH',160,0,'ETH','PAT/ETH',0,'MAIN',8),(432,158,2,'KWATT/USD',170,0,'USD','KWATT/USD',0,'MAIN',2),(433,158,4,'KWATT/BTC',160,0,'BTC','KWATT/BTC',0,'MAIN',8),(434,158,14,'KWATT/ETH',160,0,'ETH','KWATT/ETH',0,'MAIN',8),(435,159,2,'eMTV/USD',170,0,'USD','eMTV/USD',0,'MAIN',2),(436,159,4,'eMTV/BTC',160,0,'BTC','eMTV/BTC',0,'MAIN',8),(437,159,14,'eMTV/ETH',160,0,'ETH','eMTV/ETH',0,'MAIN',8),(438,160,2,'FPWR/USD',170,0,'USD','FPWR/USD',0,'MAIN',2),(439,160,4,'FPWR/BTC',160,0,'BTC','FPWR/BTC',0,'MAIN',8),(440,160,14,'FPWR/ETH',160,0,'ETH','FPWR/ETH',0,'MAIN',8),(441,161,2,'TUSD/USD',170,0,'USD','TUSD/USD',0,'MAIN',2),(442,161,4,'TUSD/BTC',160,0,'BTC','TUSD/BTC',0,'MAIN',8),(443,161,14,'TUSD/ETH',160,0,'ETH','TUSD/ETH',0,'MAIN',8),(444,23,161,'XLM/TUSD',160,0,'FIAT','XLM/TUSD',0,'MAIN',2),(445,37,161,'NEO/TUSD',160,0,'FIAT','NEO/TUSD',0,'MAIN',2),(446,5,161,'LTC/TUSD',160,0,'FIAT','LTC/TUSD',0,'MAIN',2),(447,21,161,'DASH/TUSD',160,0,'FIAT','DASH/TUSD',0,'MAIN',2),(448,26,161,'BCH/TUSD',160,1,'FIAT','BCH/TUSD',0,'MAIN',2),(449,22,161,'XRP/TUSD',160,0,'FIAT','XRP/TUSD',0,'MAIN',2),(450,162,2,'CRBT/USD',170,1,'USD','CRBT/USD',0,'MAIN',2),(451,162,4,'CRBT/BTC',160,1,'BTC','CRBT/BTC',0,'MAIN',8),(452,162,14,'CRBT/ETH',160,1,'ETH','CRBT/ETH',0,'MAIN',8),(453,163,2,'HIVE/USD',170,0,'USD','HIVE/USD',0,'MAIN',2),(454,163,4,'HIVE/BTC',160,0,'BTC','HIVE/BTC',0,'MAIN',8),(455,163,14,'HIVE/ETH',160,0,'ETH','HIVE/ETH',0,'MAIN',8),(456,164,2,'TRX/USD',270,0,'USD','TRX/USD',0,'MAIN',2),(457,164,4,'TRX/BTC',260,0,'BTC','TRX/BTC',0,'MAIN',8),(458,164,14,'TRX/ETH',260,0,'ETH','TRX/ETH',0,'MAIN',8),(459,165,2,'HDR/USD',170,0,'USD','HDR/USD',0,'MAIN',2),(460,165,4,'HDR/BTC',160,0,'BTC','HDR/BTC',0,'MAIN',8),(461,165,14,'HDR/ETH',160,0,'ETH','HDR/ETH',0,'MAIN',8),(462,166,2,'CMIT/USD',170,0,'USD','CMIT/USD',0,'MAIN',2),(463,166,4,'CMIT/BTC',160,0,'BTC','CMIT/BTC',0,'MAIN',8),(464,166,14,'CMIT/ETH',160,0,'ETH','CMIT/ETH',0,'MAIN',8),(465,167,2,'RAC/USD',170,0,'USD','RAC/USD',0,'MAIN',2),(466,167,4,'RAC/BTC',160,0,'BTC','RAC/BTC',0,'MAIN',8),(467,167,14,'RAC/ETH',160,0,'ETH','RAC/ETH',0,'MAIN',8),(471,169,2,'IQN/USD',170,0,'USD','IQN/USD',0,'MAIN',2),(472,169,4,'IQN/BTC',160,0,'BTC','IQN/BTC',0,'MAIN',8),(473,169,14,'IQN/ETH',160,0,'ETH','IQN/ETH',0,'MAIN',8),(474,170,2,'ETI/USD',170,1,'USD','ETI/USD',0,'MAIN',2),(475,170,4,'ETI/BTC',160,1,'BTC','ETI/BTC',0,'MAIN',8),(476,170,14,'ETI/ETH',160,1,'ETH','ETI/ETH',0,'MAIN',8),(477,171,2,'GEX/USD',170,0,'USD','GEX/USD',0,'MAIN',2),(478,171,4,'GEX/BTC',160,0,'BTC','GEX/BTC',0,'MAIN',8),(479,171,14,'GEX/ETH',160,0,'ETH','GEX/ETH',0,'MAIN',8),(480,172,2,'SIC/USD',170,0,'USD','SIC/USD',0,'MAIN',2),(481,172,4,'SIC/BTC',160,0,'BTC','SIC/BTC',0,'MAIN',8),(482,172,14,'SIC/ETH',160,0,'ETH','SIC/ETH',0,'MAIN',8),(483,173,2,'IXE/USD',170,1,'USD','IXE/USD',0,'MAIN',2),(484,173,4,'IXE/BTC',160,0,'BTC','IXE/BTC',0,'MAIN',8),(485,173,14,'IXE/ETH',160,0,'ETH','IXE/ETH',0,'MAIN',8),(486,174,2,'CLX/USD',170,0,'USD','CLX/USD',0,'MAIN',2),(487,174,4,'CLX/BTC',160,0,'BTC','CLX/BTC',0,'MAIN',8),(488,175,2,'PHI/USD',170,0,'USD','PHI/USD',0,'MAIN',2),(489,175,4,'PHI/BTC',160,0,'BTC','PHI/BTC',0,'MAIN',8),(490,175,14,'PHI/ETH',160,0,'ETH','PHI/ETH',0,'MAIN',8),(491,176,2,'NER/USD',170,0,'USD','NER/USD',0,'MAIN',2),(492,176,4,'NER/BTC',160,0,'BTC','NER/BTC',0,'MAIN',8),(493,176,14,'NER/ETH',160,0,'ETH','NER/ETH',0,'MAIN',8),(494,177,2,'MFTU/USD',170,0,'USD','MFTU/USD',0,'MAIN',2),(495,177,4,'MFTU/BTC',160,0,'BTC','MFTU/BTC',0,'MAIN',8),(496,177,14,'MFTU/ETH',160,0,'ETH','MFTU/ETH',0,'MAIN',8),(497,178,2,'RET/USD',170,0,'USD','RET/USD',0,'MAIN',2),(498,178,4,'RET/BTC',160,0,'BTC','RET/BTC',0,'MAIN',8),(499,178,14,'RET/ETH',160,0,'ETH','RET/ETH',0,'MAIN',8),(500,179,2,'CMK/USD',170,0,'USD','CMK/USD',0,'MAIN',2),(501,179,4,'CMK/BTC',160,0,'BTC','CMK/BTC',0,'MAIN',8),(502,179,14,'CMK/ETH',160,0,'ETH','CMK/ETH',0,'MAIN',8),(503,180,4,'GIGC/BTC',160,0,'BTC','GIGC/BTC',0,'MAIN',8),(504,181,2,'DCR/USD',170,0,'USD','DCR/USD',0,'MAIN',2),(505,181,4,'DCR/BTC',160,0,'BTC','DCR/BTC',0,'MAIN',8),(506,181,14,'DCR/ETH',160,0,'ETH','DCR/ETH',0,'MAIN',8),(507,182,2,'SWM/USD',170,0,'USD','SWM/USD',0,'MAIN',2),(508,182,4,'SWM/BTC',160,0,'BTC','SWM/BTC',0,'MAIN',8),(509,182,14,'SWM/ETH',160,0,'ETH','SWM/ETH',0,'MAIN',8),(510,183,2,'BNC/USD',170,1,'USD','BNC/USD',0,'MAIN',2),(511,183,4,'BNC/BTC',160,1,'BTC','BNC/BTC',0,'MAIN',8),(512,183,14,'BNC/ETH',160,1,'ETH','BNC/ETH',0,'MAIN',8),(513,184,2,'TIC/USD',170,0,'USD','TIC/USD',0,'MAIN',2),(514,184,4,'TIC/BTC',160,0,'BTC','TIC/BTC',0,'MAIN',8),(515,184,14,'TIC/ETH',160,0,'ETH','TIC/ETH',0,'MAIN',8),(516,185,2,'uDOO/USD',170,0,'USD','uDOO/USD',0,'MAIN',2),(517,185,4,'uDOO/BTC',160,0,'BTC','uDOO/BTC',0,'MAIN',8),(518,185,14,'uDOO/ETH',160,0,'ETH','uDOO/ETH',0,'MAIN',8),(519,186,2,'WTL/USD',170,0,'USD','WTL/USD',0,'MAIN',2),(520,186,4,'WTL/BTC',160,0,'BTC','WTL/BTC',0,'MAIN',8),(521,186,14,'WTL/ETH',160,0,'ETH','WTL/ETH',0,'MAIN',8),(522,187,2,'ADK/USD',170,0,'USD','ADK/USD',0,'MAIN',2),(523,187,4,'ADK/BTC',160,0,'BTC','ADK/BTC',0,'MAIN',8),(524,187,4,'ADK/ETH',160,0,'ETH','ADK/ETH',0,'MAIN',8),(525,188,2,'QRK/USD',170,0,'USD','QRK/USD',0,'MAIN',2),(526,188,4,'QRK/BTC',160,0,'BTC','QRK/BTC',0,'MAIN',8),(527,188,14,'QRK/ETH',160,0,'ETH','QRK/ETH',0,'MAIN',8),(528,189,2,'LPC/USD',170,0,'USD','LPC/USD',0,'MAIN',2),(529,189,4,'LPC/BTC',160,0,'BTC','LPC/BTC',0,'MAIN',8),(530,189,14,'LPC/ETH',160,0,'ETH','LPC/ETH',0,'MAIN',8),(531,190,2,'TOA/USD',170,0,'USD','TOA/USD',0,'MAIN',2),(532,190,4,'TOA/BTC',160,0,'BTC','TOA/BTC',0,'MAIN',8),(533,190,14,'TOA/ETH',160,0,'ETH','TOA/ETH',0,'MAIN',8),(534,191,2,'XFC/USD',170,0,'USD','XFC/USD',0,'MAIN',2),(535,191,4,'XFC/BTC',160,0,'BTC','XFC/BTC',0,'MAIN',8),(536,191,14,'XFC/ETH',160,0,'ETH','XFC/ETH',0,'MAIN',8),(537,192,2,'CRYP/USD',170,0,'USD','CRYP/USD',0,'MAIN',2),(538,192,4,'CRYP/BTC',160,0,'BTC','CRYP/BTC',0,'MAIN',8),(539,192,14,'CRYP/ETH',160,0,'ETH','CRYP/ETH',0,'MAIN',8),(543,194,2,'MBC/USD',170,0,'USD','MBC/USD',0,'MAIN',2),(544,194,4,'MBC/BTC',160,0,'BTC','MBC/BTC',0,'MAIN',8),(545,194,14,'MBC/ETH',160,0,'ETH','MBC/ETH',0,'MAIN',8),(546,195,2,'APL/USD',170,0,'USD','APL/USD',0,'MAIN',2),(547,195,4,'APL/BTC',160,0,'BTC','APL/BTC',0,'MAIN',8),(548,195,14,'APL/ETH',160,0,'ETH','APL/ETH',0,'MAIN',8),(549,196,2,'XAU/USD',170,0,'USD','XAU/USD',0,'MAIN',2),(550,196,4,'XAU/BTC',160,0,'BTC','XAU/BTC',0,'MAIN',8),(551,196,14,'XAU/ETH',160,0,'ETH','XAU/ETH',0,'MAIN',8),(552,197,2,'KAZE/USD',170,0,'USD','KAZE/USD',0,'MAIN',2),(553,197,4,'KAZE/BTC',160,0,'BTC','KAZE/BTC',0,'MAIN',8),(554,197,14,'KAZE/ETH',160,0,'ETH','KAZE/ETH',0,'MAIN',8),(555,198,2,'STREAM/USD',170,0,'USD','STREAM/USD',0,'MAIN',2),(556,198,4,'STREAM/BTC',160,0,'BTC','STREAM/BTC',0,'MAIN',8),(557,198,14,'STREAM/ETH',160,0,'ETH','STREAM/ETH',0,'MAIN',8),(558,199,2,'TTP/USD',170,0,'USD','TTP/USD',0,'MAIN',2),(559,199,4,'TTP/BTC',160,0,'BTC','TTP/BTC',0,'MAIN',8),(560,199,14,'TTP/ETH',160,0,'ETH','TTP/ETH',0,'MAIN',8),(561,200,2,'USDC/USD',170,0,'USD','USDC/USD',0,'MAIN',2),(562,200,4,'USDC/BTC',160,0,'BTC','USDC/BTC',0,'MAIN',8),(563,200,14,'USDC/ETH',160,0,'ETH','USDC/ETH',0,'MAIN',8),(564,201,2,'AUNIT/USD',170,0,'USD','AUNIT/USD',0,'MAIN',2),(565,201,4,'AUNIT/BTC',160,0,'BTC','AUNIT/BTC',0,'MAIN',8),(566,201,14,'AUNIT/ETH',160,0,'ETH','AUNIT/ETH',0,'MAIN',8),(567,202,2,'ABBC/USD',170,0,'USD','ABBC/USD',0,'MAIN',2),(568,202,4,'ABBC/BTC',160,0,'BTC','ABBC/BTC',0,'MAIN',8),(569,202,14,'ABBC/ETH',160,0,'ETH','ABBC/ETH',0,'MAIN',8),(570,203,2,'CBC/USD',170,0,'USD','CBC/USD',0,'MAIN',2),(571,203,4,'CBC/BTC',160,0,'BTC','CBC/BTC',0,'MAIN',8),(572,203,14,'CBC/ETH',160,0,'ETH','CBC/ETH',0,'MAIN',8),(573,153,2,'KGS/USD',160,1,'ICO','KGS/USD',0,'ICO',NULL),(574,153,200,'KGS/USDC',170,0,'ICO','KGS/USDC',0,'ICO',NULL),(575,204,2,'BCH/USD',170,0,'USD','BCH/USD',0,'MAIN',2),(576,204,4,'BCH/BTC',160,0,'BTC','BCH/BTC',0,'MAIN',8),(577,204,14,'BCH/ETH',160,0,'ETH','BCH/ETH',0,'MAIN',8),(578,205,2,'BSV/USD',170,0,'USD','BSV/USD',0,'MAIN',2),(579,205,4,'BSV/BTC',160,0,'BTC','BSV/BTC',0,'MAIN',8),(580,205,14,'BSV/ETH',160,0,'ETH','BSV/ETH',0,'MAIN',8),(581,207,2,'Q/USD',170,0,'USD','Q/USD',0,'MAIN',2),(582,207,4,'Q/BTC',160,0,'BTC','Q/BTC',0,'MAIN',8),(583,207,14,'Q/ETH',160,0,'ETH','Q/ETH',0,'MAIN',8),(584,208,2,'VAI/USD',170,0,'USD','VAI/USD',0,'MAIN',2),(585,208,4,'VAI/BTC',160,0,'BTC','VAI/BTC',0,'MAIN',8),(586,208,14,'VAI/ETH',160,0,'ETH','VAI/ETH',0,'MAIN',8),(587,209,2,'UNC/USD',170,0,'USD','UNC/USD',0,'MAIN',2),(588,209,4,'UNC/BTC',160,0,'BTC','UNC/BTC',0,'MAIN',8),(589,209,14,'UNC/ETH',160,0,'ETH','UNC/ETH',0,'MAIN',8),(590,210,2,'MODL/USD',170,0,'USD','MODL/USD',0,'MAIN',2),(591,210,4,'MODL/BTC',160,0,'BTC','MODL/BTC',0,'MAIN',8),(592,210,14,'MODL/ETH',160,0,'ETH','MODL/ETH',0,'MAIN',8),(593,124,2,'LEDU/USD',170,0,'USD','LEDU/USD',0,'MAIN',2),(594,212,2,'DIME/USD',170,0,'USD','DIME/USD',0,'MAIN',2),(595,212,4,'DIME/BTC',160,0,'BTC','DIME/BTC',0,'MAIN',8),(596,212,14,'DIME/ETH',160,0,'ETH','DIME/ETH',0,'MAIN',8),(597,213,2,'MGX/USD',170,0,'USD','MGX/USD',0,'MAIN',2),(598,213,4,'MGX/BTC',160,0,'BTC','MGX/BTC',0,'MAIN',8),(599,213,14,'MGX/ETH',160,0,'ETH','MGX/ETH',0,'MAIN',8),(600,214,2,'MNC/USD',170,0,'USD','MNC/USD',0,'MAIN',2),(601,214,4,'MNC/BTC',160,0,'BTC','MNC/BTC',0,'MAIN',8),(602,214,14,'MNC/ETH',160,0,'ETH','MNC/ETH',0,'MAIN',8),(605,14,7,'ETH/UAH',170,0,'FIAT','ETH/UAH',0,'MAIN',2),(606,14,1,'ETH/RUB',170,0,'FIAT','ETH/RUB',0,'MAIN',2),(607,215,2,'S4F/USD',170,0,'USD','S4F/USD',0,'MAIN',2),(608,215,4,'S4F/BTC',160,0,'BTC','S4F/BTC',0,'MAIN',8),(609,215,14,'S4F/ETH',160,0,'ETH','S4F/ETH',0,'MAIN',8),(610,216,2,'ECTE/USD',170,0,'USD','ECTE/USD',1,'MAIN',2),(611,216,4,'ECTE/BTC',160,0,'BTC','ECTE/BTC',1,'MAIN',8),(612,216,14,'ECTE/ETH',160,0,'ETH','ECTE/ETH',1,'MAIN',8),(613,217,2,'CTX/USD',170,0,'USD','CTX/USD',0,'MAIN',2),(614,217,4,'CTX/BTC',160,0,'BTC','CTX/BTC',0,'MAIN',8),(615,217,14,'CTX/ETH',160,0,'ETH','CTX/ETH',0,'MAIN',8),(616,218,2,'EDT/USD',170,0,'USD','EDT/USD',0,'MAIN',2),(617,218,4,'EDT/BTC',160,0,'BTC','EDT/BTC',0,'MAIN',8),(618,218,14,'EDT/ETH',160,0,'ETH','EDT/ETH',0,'MAIN',8),(619,219,2,'HT/USD',170,0,'USD','HT/USD',0,'MAIN',2),(620,219,4,'HT/BTC',160,0,'BTC','HT/BTC',0,'MAIN',8),(621,219,14,'HT/ETH',160,0,'ETH','HT/ETH',0,'MAIN',8),(622,220,2,'RIME/USD',170,0,'USD','RIME/USD',0,'MAIN',2),(623,220,4,'RIME/BTC',160,0,'BTC','RIME/BTC',0,'MAIN',8),(624,220,14,'RIME/ETH',160,0,'ETH','RIME/ETH',0,'MAIN',8),(628,222,2,'TCAT/USD',170,0,'USD','TCAT/USD',0,'MAIN',2),(629,222,4,'TCAT/BTC',160,0,'BTC','TCAT/BTC',0,'MAIN',8),(630,222,14,'TCAT/ETH',160,0,'ETH','TCAT/ETH',0,'MAIN',8),(631,223,2,'WaBi/USD',170,1,'USD','WaBi/USD',0,'MAIN',2),(632,223,4,'WaBi/BTC',160,1,'BTC','WaBi/BTC',0,'MAIN',8),(633,223,14,'WaBi/ETH',160,1,'ETH','WaBi/ETH',0,'MAIN',8),(634,224,2,'POA/USD',170,1,'USD','POA/USD',0,'MAIN',2),(635,224,4,'POA/BTC',160,1,'BTC','POA/BTC',0,'MAIN',8),(636,224,14,'POA/ETH',160,1,'ETH','POA/ETH',0,'MAIN',8),(637,225,2,'MCO/USD',170,0,'USD','MCO/USD',0,'MAIN',2),(638,225,4,'MCO/BTC',160,0,'BTC','MCO/BTC',0,'MAIN',8),(639,225,14,'MCO/ETH',160,0,'ETH','MCO/ETH',0,'MAIN',8),(640,226,2,'ZIL/USD',170,0,'USD','ZIL/USD',0,'MAIN',2),(641,226,4,'ZIL/BTC',160,0,'BTC','ZIL/BTC',0,'MAIN',8),(642,226,14,'ZIL/ETH',160,0,'ETH','ZIL/ETH',0,'MAIN',8),(643,227,2,'MANA/USD',170,0,'USD','MANA/USD',0,'MAIN',2),(644,227,4,'MANA/BTC',160,0,'BTC','MANA/BTC',0,'MAIN',8),(645,227,14,'MANA/ETH',160,0,'ETH','MANA/ETH',0,'MAIN',8),(646,228,2,'EXO/USD',170,0,'USD','EXO/USD',0,'MAIN',2),(647,228,4,'EXO/BTC',160,0,'BTC','EXO/BTC',0,'MAIN',8),(648,228,14,'EXO/ETH',160,0,'ETH','EXO/ETH',0,'MAIN',8),(649,229,2,'GRS/USD',170,0,'USD','GRS/USD',0,'MAIN',2),(650,229,4,'GRS/BTC',160,0,'BTC','GRS/BTC',0,'MAIN',8),(651,229,14,'GRS/ETH',160,0,'ETH','GRS/ETH',0,'MAIN',8),(652,230,2,'KOD/USD',170,0,'USD','KOD/USD',0,'MAIN',2),(653,230,4,'KOD/BTC',160,0,'BTC','KOD/BTC',0,'MAIN',8),(654,230,14,'KOD/ETH',160,0,'ETH','KOD/ETH',0,'MAIN',8),(655,231,2,'HCXP/USD',170,0,'USD','HCXP/USD',1,'MAIN',2),(656,231,4,'HCXP/BTC',160,0,'BTC','HCXP/BTC',1,'MAIN',8),(657,231,14,'HCXP/ETH',160,0,'ETH','HCXP/ETH',1,'MAIN',8),(658,232,2,'QKC/USD',170,0,'USD','QKC/USD',0,'MAIN',2),(659,232,4,'QKC/BTC',160,0,'BTC','QKC/BTC',0,'MAIN',8),(660,232,14,'QKC/ETH',160,0,'ETH','QKC/ETH',0,'MAIN',8),(661,233,2,'NPXS/USD',170,0,'USD','NPXS/USD',0,'MAIN',2),(662,233,4,'NPXS/BTC',160,0,'BTC','NPXS/BTC',0,'MAIN',8),(663,233,14,'NPXS/ETH',160,0,'ETH','NPXS/ETH',0,'MAIN',8),(664,234,2,'HOT/USD',170,0,'USD','HOT/USD',0,'MAIN',2),(665,234,4,'HOT/BTC',160,0,'BTC','HOT/BTC',0,'MAIN',8),(666,234,14,'HOT/ETH',160,0,'ETH','HOT/ETH',0,'MAIN',8),(667,235,2,'ZRX/USD',170,0,'USD','ZRX/USD',0,'MAIN',2),(668,235,4,'ZRX/BTC',160,0,'BTC','ZRX/BTC',0,'MAIN',8),(669,235,14,'ZRX/ETH',160,0,'ETH','ZRX/ETH',0,'MAIN',8),(670,236,2,'BAT/USD',170,0,'USD','BAT/USD',0,'MAIN',2),(671,236,4,'BAT/BTC',160,0,'BTC','BAT/BTC',0,'MAIN',8),(672,236,14,'BAT/ETH',160,0,'ETH','BAT/ETH',0,'MAIN',8),(673,237,2,'RDN/USD',170,0,'USD','RDN/USD',0,'MAIN',2),(674,237,4,'RDN/BTC',160,0,'BTC','RDN/BTC',0,'MAIN',8),(675,237,14,'RDN/ETH',160,0,'ETH','RDN/ETH',0,'MAIN',8),(676,238,4,'DIM.EUR/BTC',160,0,'BTC','DIM.EUR/BTC',0,'MAIN',8),(677,239,4,'DIM.USD/BTC',160,0,'BTC','DIM.USD/BTC',0,'MAIN',8),(678,240,2,'DIGIT/USD',170,0,'USD','DIGIT/USD',0,'MAIN',2),(679,240,4,'DIGIT/BTC',160,0,'BTC','DIGIT/BTC',0,'MAIN',8),(680,240,14,'DIGIT/ETH',160,0,'ETH','DIGIT/ETH',0,'MAIN',8),(681,241,3,'ELT/EUR',170,1,'USD','ELT/EUR',0,'MAIN',2),(682,241,4,'ELT/BTC',160,1,'BTC','ELT/BTC',0,'MAIN',8),(683,241,14,'ELT/ETH',160,1,'ETH','ELT/ETH',0,'MAIN',8),(684,242,2,'HNI/USD',170,0,'USD','HNI/USD',0,'MAIN',2),(685,242,4,'HNI/BTC',160,0,'BTC','HNI/BTC',0,'MAIN',8),(686,242,14,'HNI/ETH',160,0,'ETH','HNI/ETH',0,'MAIN',8),(687,243,2,'WOLF/USD',170,1,'USD','WOLF/USD',0,'MAIN',2),(688,243,4,'WOLF/BTC',160,1,'BTC','WOLF/BTC',0,'MAIN',8),(689,243,14,'WOLF/ETH',160,1,'ETH','WOLF/ETH',0,'MAIN',8),(690,244,2,'REN/USD',170,0,'USD','REN/USD',0,'MAIN',2),(691,244,4,'REN/BTC',160,0,'BTC','REN/BTC',0,'MAIN',8),(692,244,14,'REN/ETH',160,0,'ETH','REN/ETH',0,'MAIN',8),(693,245,2,'MET/USD',170,0,'USD','MET/USD',0,'MAIN',2),(694,245,4,'MET/BTC',160,0,'BTC','MET/BTC',0,'MAIN',8),(695,245,14,'MET/ETH',160,0,'ETH','MET/ETH',0,'MAIN',8),(696,4,246,'BTC/USDT',160,0,'USDT','BTC/USDT',0,'MAIN',NULL),(697,14,246,'ETH/USDT',160,0,'USDT','ETH/USDT',0,'MAIN',NULL),(698,5,246,'LTC/USDT',160,0,'USDT','LTC/USDT',0,'MAIN',NULL),(699,19,246,'ETC/USDT',160,0,'USDT','ETC/USDT',0,'MAIN',NULL),(700,21,246,'DASH/USDT',160,0,'USDT','DASH/USDT',0,'MAIN',NULL),(701,22,246,'XRP/USDT',160,0,'USDT','XRP/USDT',0,'MAIN',NULL),(702,23,246,'XLM/USDT',160,0,'USDT','XLM/USDT',0,'MAIN',NULL),(703,24,246,'XEM/USDT',160,0,'USDT','XEM/USDT',0,'MAIN',NULL),(704,25,246,'ATB/USDT',160,0,'USDT','ATB/USDT',0,'MAIN',NULL),(705,9,246,'EDC/USDT',160,0,'USDT','EDC/USDT',0,'MAIN',NULL),(706,27,246,'IOTA/USDT',160,0,'USDT','MIOTA/USDT',0,'MAIN',NULL),(707,28,246,'DOGE/USDT',160,0,'USDT','DOGE/USDT',0,'MAIN',NULL),(708,29,246,'LSK/USDT',160,0,'USDT','LSK/USDT',0,'MAIN',NULL),(709,30,246,'XMR/USDT',160,0,'USDT','XMR/USDT',0,'MAIN',NULL),(710,37,246,'NEO/USDT',160,0,'USDT','NEO/USDT',0,'MAIN',NULL),(711,38,246,'GAS/USDT',160,0,'USDT','GAS/USDT',0,'MAIN',NULL),(712,39,246,'ZEC/USDT',160,0,'USDT','ZEC/USDT',0,'MAIN',NULL),(713,41,246,'WAVES/USDT',160,0,'USDT','WAVES/USDT',0,'MAIN',NULL),(714,45,246,'BNB/USDT',160,0,'USDT','BNB/USDT',0,'MAIN',NULL),(715,247,2,'PLTC/USD',170,0,'USD','PLTC/USD',0,'MAIN',2),(716,247,4,'PLTC/BTC',160,0,'BTC','PLTC/BTC',0,'MAIN',8),(717,247,14,'PLTC/ETH',160,0,'ETH','PLTC/ETH',0,'MAIN',8),(718,248,4,'DIVI/BTC',160,0,'BTC','DIVI/BTC',0,'MAIN',8),(719,249,4,'BTT/BTC',160,0,'BTC','BTT/BTC',0,'MAIN',8),(720,249,14,'BTT/ETH',160,0,'ETH','BTT/ETH',0,'MAIN',8),(721,249,2,'BTT/USD',160,0,'USD','BTT/USD',0,'MAIN',2),(722,250,2,'PPY/USD',170,0,'USD','PPY/USD',0,'MAIN',2),(723,250,4,'PPY/BTC',160,0,'BTC','PPY/BTC',0,'MAIN',8),(724,250,14,'PPY/ETH',160,0,'ETH','PPY/ETH',0,'MAIN',8),(725,251,2,'VRBS/USD',170,0,'USD','VRBS/USD',0,'MAIN',2),(726,251,246,'VRBS/USDT',160,0,'USDT','VRBS/USDT',0,'MAIN',NULL),(727,251,14,'VRBS/ETH',160,0,'ETH','VRBS/ETH',0,'MAIN',8),(728,252,2,'ZUBE/USD',170,0,'USD','ZUBE/USD',0,'MAIN',2),(729,252,4,'ZUBE/BTC',160,0,'BTC','ZUBE/BTC',0,'MAIN',8),(730,252,14,'ZUBE/ETH',160,0,'ETH','ZUBE/ETH',0,'MAIN',8),(731,253,2,'OWC/USD',170,0,'USD','OWC/USD',0,'MAIN',2),(732,253,4,'OWC/BTC',160,0,'BTC','OWC/BTC',0,'MAIN',8),(733,253,14,'OWC/ETH',160,0,'ETH','OWC/ETH',0,'MAIN',8),(734,254,14,'ELC/ETH',160,0,'ETH','ELC/ETH',0,'MAIN',8),(735,255,2,'CSC/USD',170,0,'USD','CSC/USD',0,'MAIN',2),(736,255,4,'CSC/BTC',160,0,'BTC','CSC/BTC',0,'MAIN',8),(737,255,14,'CSC/ETH',160,0,'ETH','CSC/ETH',0,'MAIN',8),(738,256,2,'RBC/USD',170,0,'USD','RBC/USD',0,'MAIN',2),(739,256,4,'RBC/BTC',160,0,'BTC','RBC/BTC',0,'MAIN',8),(740,256,14,'RBC/ETH',160,0,'ETH','RBC/ETH',0,'MAIN',8),(741,257,2,'TTT/USD',170,0,'USD','TTT/USD',0,'MAIN',2),(742,257,4,'TTT/BTC',160,0,'BTC','TTT/BTC',0,'MAIN',8),(743,257,14,'TTT/ETH',160,0,'ETH','TTT/ETH',0,'MAIN',8),(744,258,2,'REB/USD',170,0,'USD','REB/USD',0,'MAIN',2),(745,258,4,'REB/BTC',160,0,'BTC','REB/BTC',0,'MAIN',8),(746,258,14,'REB/ETH',160,0,'ETH','REB/ETH',0,'MAIN',8),(759,263,2,'CREA/USD',170,0,'USD','CREA/USD',0,'MAIN',NULL),(760,263,4,'CREA/BTC',160,0,'BTC','CREA/BTC',0,'MAIN',NULL),(761,263,14,'CREA/ETH',160,0,'ETH','CREA/ETH',0,'MAIN',NULL),(765,265,2,'AISI/USD',170,0,'USD','AISI/USD',0,'MAIN',NULL),(766,265,4,'AISI/BTC',160,0,'BTC','AISI/BTC',0,'MAIN',NULL),(767,265,14,'AISI/ETH',160,0,'ETH','AISI/ETH',0,'MAIN',NULL),(768,266,2,'RVC/USD',170,0,'USD','RVC/USD',0,'MAIN',NULL),(769,266,4,'RVC/BTC',160,0,'BTC','RVC/BTC',0,'MAIN',NULL),(770,266,14,'RVC/ETH',160,0,'ETH','RVC/ETH',0,'MAIN',NULL),(771,267,2,'BIO/USD',170,0,'USD','BIO/USD',0,'MAIN',NULL),(772,267,4,'BIO/BTC',160,0,'BTC','BIO/BTC',0,'MAIN',NULL),(773,267,14,'BIO/ETH',160,0,'ETH','BIO/ETH',0,'MAIN',NULL),(786,268,2,'ETA/USD',170,0,'USD','ETA/USD',0,'MAIN',NULL),(787,268,4,'ETA/BTC',160,0,'BTC','ETA/BTC',0,'MAIN',NULL),(788,268,14,'ETA/ETH',160,0,'ETH','ETA/ETH',0,'MAIN',NULL),(789,269,2,'KAT/USD',170,0,'USD','KAT/USD',0,'MAIN',NULL),(790,269,4,'KAT/BTC',160,0,'BTC','KAT/BTC',0,'MAIN',NULL),(791,269,14,'KAT/ETH',160,0,'ETH','KAT/ETH',0,'MAIN',NULL),(792,270,2,'VRA/USD',170,0,'USD','VRA/USD',0,'MAIN',NULL),(793,270,4,'VRA/BTC',160,0,'BTC','VRA/BTC',0,'MAIN',NULL),(794,270,14,'VRA/ETH',160,0,'ETH','VRA/ETH',0,'MAIN',NULL),(795,271,2,'BRC/USD',170,0,'USD','BRC/USD',0,'MAIN',NULL),(796,271,4,'BRC/BTC',160,0,'BTC','BRC/BTC',0,'MAIN',NULL),(797,271,14,'BRC/ETH',160,0,'ETH','BRC/ETH',0,'MAIN',NULL),(798,272,2,'CRON/USD',170,0,'USD','CRON/USD',0,'MAIN',NULL),(799,272,4,'CRON/BTC',160,0,'BTC','CRON/BTC',0,'MAIN',NULL),(800,272,14,'CRON/ETH',160,0,'ETH','CRON/ETH',0,'MAIN',NULL),(801,273,2,'GNY/USD',170,0,'USD','GNY/USD',0,'MAIN',NULL),(802,273,4,'GNY/BTC',160,0,'BTC','GNY/BTC',0,'MAIN',NULL),(803,273,14,'GNY/ETH',160,0,'ETH','GNY/ETH',0,'MAIN',NULL),(804,272,246,'CRON/USDT',160,0,'USD','CRON/USDT',0,'MAIN',NULL),(805,274,2,'NOVA/USD',170,0,'USD','NOVA/USD',0,'MAIN',NULL),(806,274,4,'NOVA/BTC',160,0,'BTC','NOVA/BTC',0,'MAIN',NULL),(807,274,14,'NOVA/ETH',160,0,'ETH','NOVA/ETH',0,'MAIN',NULL),(810,277,4,'IEO12/BTC',160,1,'ICO','IEO12/BTC',0,'ICO',NULL),(811,278,2,'DARC/USD',170,0,'USD','DARC/USD',0,'MAIN',NULL),(812,278,4,'DARC/BTC',160,0,'BTC','DARC/BTC',0,'MAIN',NULL),(813,278,14,'DARC/ETH',160,0,'ETH','DARC/ETH',0,'MAIN',NULL),(814,279,4,'EXTT/BTC',160,1,'ICO','EXTT/BTC',0,'ICO',NULL),(815,288,4,'SCF/BTC',170,1,'BTC','SCF/BTC',0,'ICO',8),(816,291,4,'ICT/BTC',160,1,'ICO','ICT/BTC',0,'ICO',NULL),(817,292,4,'NWT/BTC',160,1,'ICO','NWT/BTC',0,'ICO',NULL),(818,293,2,'TSL/USD',170,0,'USD','TSL/USD',0,'MAIN',NULL),(819,293,4,'TSL/BTC',160,0,'BTC','TSL/BTC',0,'MAIN',NULL),(820,293,14,'TSL/ETH',160,0,'ETH','TSL/ETH',0,'MAIN',NULL),(821,294,2,'VOLLAR/USD',170,0,'USD','VOLLAR/USD',0,'MAIN',NULL),(822,294,4,'VOLLAR/BTC',160,0,'BTC','VOLLAR/BTC',0,'MAIN',NULL),(823,294,14,'VOLLAR/ETH',160,0,'ETH','VOLLAR/ETH',0,'MAIN',NULL),(824,295,2,'GAPI/USD',170,0,'USD','GAPI/USD',0,'MAIN',NULL),(825,295,4,'GAPI/BTC',160,0,'BTC','GAPI/BTC',0,'MAIN',NULL),(826,295,14,'GAPI/ETH',160,0,'ETH','GAPI/ETH',0,'MAIN',NULL),(827,296,2,'FST/USD',170,0,'USD','FST/USD',0,'MAIN',NULL),(828,296,4,'FST/BTC',160,0,'BTC','FST/BTC',0,'MAIN',NULL),(829,296,14,'FST/ETH',160,0,'ETH','FST/ETH',0,'MAIN',NULL),(830,297,2,'RVT/USD',170,0,'USD','RVT/USD',0,'MAIN',NULL),(831,297,4,'RVT/BTC',160,0,'BTC','RVT/BTC',0,'MAIN',NULL),(832,297,14,'RVT/ETH',160,0,'ETH','RVT/ETH',0,'MAIN',NULL),(833,298,2,'RWDS/USD',170,0,'USD','RWDS/USD',0,'MAIN',NULL),(834,298,4,'RWDS/BTC',160,0,'BTC','RWDS/BTC',0,'MAIN',NULL),(835,298,14,'RWDS/ETH',160,0,'ETH','RWDS/ETH',0,'MAIN',NULL),(836,299,2,'LHT/USD',170,0,'USD','LHT/USD',0,'MAIN',NULL),(837,299,4,'LHT/BTC',160,0,'BTC','LHT/BTC',0,'MAIN',NULL),(838,299,14,'LHT/ETH',160,0,'ETH','LHT/ETH',0,'MAIN',NULL),(839,300,4,'HBO/BTC',160,1,'ICO','HBO/BTC',0,'ICO',NULL),(840,301,4,'KYC/BTC',160,1,'ICO','KYC/BTC',0,'ICO',NULL),(841,302,4,'BOM 2019/BTC',160,1,'ICO','BOM 2019/BTC',0,'ICO',NULL),(842,303,4,'NWCCOI/BTC',160,1,'ICO','NWCCOI/BTC',0,'ICO',NULL),(843,304,4,'ACDC test/BTC',160,1,'ICO','ACDC test/BTC',0,'ICO',NULL),(844,164,249,'TRX/BTT',160,0,'FIAT','TRX/BTT',0,'MAIN',NULL),(845,305,4,'MMM/BTC',160,1,'ICO','MMM/BTC',0,'ICO',NULL),(846,300,2,'LINA/USD',170,0,'USD','LINA/USD',0,'MAIN',NULL),(847,300,4,'LINA/BTC',160,0,'BTC','LINA/BTC',0,'MAIN',NULL),(848,300,14,'LINA/ETH',160,0,'ETH','LINA/ETH',0,'MAIN',NULL);
/*!40000 ALTER TABLE `currency_pair` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `currency_pair_limit`
--

DROP TABLE IF EXISTS `currency_pair_limit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `currency_pair_limit` (
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
  CONSTRAINT `currency_pair_limit___fk_cur_pair` FOREIGN KEY (`currency_pair_id`) REFERENCES `currency_pair` (`id`),
  CONSTRAINT `currency_pair_limit___fk_ord_type` FOREIGN KEY (`order_type_id`) REFERENCES `order_type` (`id`),
  CONSTRAINT `currency_pair_limit___fk_role` FOREIGN KEY (`user_role_id`) REFERENCES `user_role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=94 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `currency_pair_limit`
--

LOCK TABLES `currency_pair_limit` WRITE;
/*!40000 ALTER TABLE `currency_pair_limit` DISABLE KEYS */;
INSERT INTO `currency_pair_limit` VALUES (1,846,5,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(2,846,5,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(3,846,1,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(4,846,1,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(5,846,2,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(6,846,2,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(7,846,3,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(8,846,3,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(9,846,9,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(10,846,9,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(11,846,4,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(12,846,4,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(13,846,6,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(14,846,6,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(15,846,7,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(16,846,7,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(17,846,8,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(18,846,8,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(19,846,10,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(20,846,10,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(21,846,11,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(22,846,11,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(23,846,12,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(24,846,12,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(32,847,5,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(33,847,5,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(34,847,1,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(35,847,1,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(36,847,2,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(37,847,2,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(38,847,3,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(39,847,3,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(40,847,9,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(41,847,9,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(42,847,4,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(43,847,4,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(44,847,6,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(45,847,6,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(46,847,7,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(47,847,7,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(48,847,8,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(49,847,8,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(50,847,10,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(51,847,10,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(52,847,11,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(53,847,11,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(54,847,12,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(55,847,12,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(63,848,5,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(64,848,5,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(65,848,1,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(66,848,1,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(67,848,2,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(68,848,2,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(69,848,3,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(70,848,3,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(71,848,9,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(72,848,9,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(73,848,4,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(74,848,4,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(75,848,6,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(76,848,6,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(77,848,7,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(78,848,7,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(79,848,8,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(80,848,8,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(81,848,10,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(82,848,10,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(83,848,11,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(84,848,11,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(85,848,12,2,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000),(86,848,12,1,0.000000000,99999999999.000000000,0.000000100,9999999999.000000000);
/*!40000 ALTER TABLE `currency_pair_limit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `current_currency_balances`
--

DROP TABLE IF EXISTS `current_currency_balances`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `current_currency_balances` (
  `id` int(40) unsigned NOT NULL AUTO_INCREMENT,
  `currency_id` int(40) NOT NULL,
  `currency_name` varchar(45) NOT NULL,
  `balance` decimal(30,8) DEFAULT '0.00000000',
  `last_updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `schedule_last_updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `currency_id` (`currency_id`),
  UNIQUE KEY `currency_name` (`currency_name`),
  CONSTRAINT `current_currency_rates_balances_currency_id_fk` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `current_currency_balances`
--

LOCK TABLES `current_currency_balances` WRITE;
/*!40000 ALTER TABLE `current_currency_balances` DISABLE KEYS */;
INSERT INTO `current_currency_balances` VALUES (1,300,'LINA',0.00000000,'2019-06-11 08:59:33','2019-06-11 08:59:33');
/*!40000 ALTER TABLE `current_currency_balances` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `current_currency_rates`
--

DROP TABLE IF EXISTS `current_currency_rates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `current_currency_rates` (
  `id` int(40) unsigned NOT NULL AUTO_INCREMENT,
  `currency_id` int(40) NOT NULL,
  `currency_name` varchar(45) NOT NULL,
  `usd_rate` decimal(19,8) DEFAULT '0.00000000',
  `btc_rate` decimal(19,8) DEFAULT '0.00000000',
  `schedule_last_updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `currency_id` (`currency_id`),
  UNIQUE KEY `currency_name` (`currency_name`),
  CONSTRAINT `current_currency_rates_currency_id_fk` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `current_currency_rates`
--

LOCK TABLES `current_currency_rates` WRITE;
/*!40000 ALTER TABLE `current_currency_rates` DISABLE KEYS */;
INSERT INTO `current_currency_rates` VALUES (1,300,'LINA',0.00000000,0.00000000,'2019-06-11 08:59:33');
/*!40000 ALTER TABLE `current_currency_rates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exorders`
--

DROP TABLE IF EXISTS `exorders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `exorders` (
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
  CONSTRAINT `exorders___fk_source_id` FOREIGN KEY (`order_source_id`) REFERENCES `exorders` (`id`),
  CONSTRAINT `fk_COMMISSION` FOREIGN KEY (`commission_id`) REFERENCES `commission` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_CURRENCY_PAIR` FOREIGN KEY (`currency_pair_id`) REFERENCES `currency_pair` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_OPERATION_TYPE` FOREIGN KEY (`operation_type_id`) REFERENCES `operation_type` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_ORDER_STATUS` FOREIGN KEY (`status_id`) REFERENCES `order_status` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_USER_ACCEPTOR` FOREIGN KEY (`user_acceptor_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_USER_CREATOR` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exorders`
--

LOCK TABLES `exorders` WRITE;
/*!40000 ALTER TABLE `exorders` DISABLE KEYS */;
/*!40000 ALTER TABLE `exorders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gtag_refill_requests`
--

DROP TABLE IF EXISTS `gtag_refill_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gtag_refill_requests` (
  `USER_ID` int(11) NOT NULL,
  `COUNT` int(11) DEFAULT NULL,
  PRIMARY KEY (`USER_ID`),
  CONSTRAINT `GTAG_REFILL_REQUESTS_ibfk_1` FOREIGN KEY (`USER_ID`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gtag_refill_requests`
--

LOCK TABLES `gtag_refill_requests` WRITE;
/*!40000 ALTER TABLE `gtag_refill_requests` DISABLE KEYS */;
/*!40000 ALTER TABLE `gtag_refill_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ieo_claim`
--

DROP TABLE IF EXISTS `ieo_claim`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ieo_claim` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ieo_id` int(11) NOT NULL,
  `currency_name` varchar(64) NOT NULL,
  `maker_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `amount` double(40,9) NOT NULL,
  `rate` double(40,9) NOT NULL,
  `price_in_btc` double(40,9) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('SUCCESS','FAILED','NONE','REVOKED') DEFAULT 'NONE',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `maker_id` (`maker_id`),
  KEY `ieo_id` (`ieo_id`),
  CONSTRAINT `IEO_CLAIM_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `IEO_CLAIM_ibfk_2` FOREIGN KEY (`maker_id`) REFERENCES `user` (`id`),
  CONSTRAINT `IEO_CLAIM_ibfk_3` FOREIGN KEY (`ieo_id`) REFERENCES `ieo_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ieo_claim`
--

LOCK TABLES `ieo_claim` WRITE;
/*!40000 ALTER TABLE `ieo_claim` DISABLE KEYS */;
/*!40000 ALTER TABLE `ieo_claim` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ieo_details`
--

DROP TABLE IF EXISTS `ieo_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ieo_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `currency_name` varchar(10) NOT NULL,
  `currency_description` varchar(255) DEFAULT NULL,
  `maker_id` int(11) NOT NULL,
  `rate` double NOT NULL,
  `amount` double NOT NULL,
  `available_amount` double DEFAULT '0',
  `contributors` int(11) DEFAULT '0',
  `status` enum('PENDING','RUNNING','SUCCEEDED','FAILED','PROCESSING_FAIL','TERMINATED') DEFAULT 'PENDING',
  `min_amount` double DEFAULT '0',
  `max_amount_per_claim` double DEFAULT '0',
  `max_amount_per_user` double DEFAULT '0',
  `starts_at` datetime NOT NULL,
  `terminates_at` datetime NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` int(11) NOT NULL,
  `description` text,
  `logo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ieo_details`
--

LOCK TABLES `ieo_details` WRITE;
/*!40000 ALTER TABLE `ieo_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `ieo_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ieo_restricted_country`
--

DROP TABLE IF EXISTS `ieo_restricted_country`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ieo_restricted_country` (
  `ieo_id` int(11) NOT NULL,
  `country_code` varchar(64) NOT NULL,
  UNIQUE KEY `ieo_id` (`ieo_id`,`country_code`),
  CONSTRAINT `IEO_RESTRICTED_COUNTRY_ibfk_1` FOREIGN KEY (`ieo_id`) REFERENCES `ieo_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ieo_restricted_country`
--

LOCK TABLES `ieo_restricted_country` WRITE;
/*!40000 ALTER TABLE `ieo_restricted_country` DISABLE KEYS */;
/*!40000 ALTER TABLE `ieo_restricted_country` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ieo_result`
--

DROP TABLE IF EXISTS `ieo_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ieo_result` (
  `claim_id` int(11) DEFAULT NULL,
  `ieo_id` int(11) NOT NULL,
  `available_amount` double(40,9) DEFAULT '0.000000000',
  `status` enum('SUCCESS','FAILED','NONE') NOT NULL DEFAULT 'NONE',
  KEY `ieo_id` (`ieo_id`),
  CONSTRAINT `IEO_RESULT_ibfk_1` FOREIGN KEY (`ieo_id`) REFERENCES `ieo_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ieo_result`
--

LOCK TABLES `ieo_result` WRITE;
/*!40000 ALTER TABLE `ieo_result` DISABLE KEYS */;
/*!40000 ALTER TABLE `ieo_result` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ieo_subscribe`
--

DROP TABLE IF EXISTS `ieo_subscribe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ieo_subscribe` (
  `email` varchar(255) NOT NULL,
  `email_subscribe` tinyint(1) NOT NULL DEFAULT '0',
  `telegram_subscribe` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ieo_subscribe`
--

LOCK TABLES `ieo_subscribe` WRITE;
/*!40000 ALTER TABLE `ieo_subscribe` DISABLE KEYS */;
/*!40000 ALTER TABLE `ieo_subscribe` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `input_output_report`
--

DROP TABLE IF EXISTS `input_output_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `input_output_report` (
  `id` int(40) unsigned NOT NULL AUTO_INCREMENT,
  `file_name` varchar(64) NOT NULL,
  `content` longblob NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `input_output_report`
--

LOCK TABLES `input_output_report` WRITE;
/*!40000 ALTER TABLE `input_output_report` DISABLE KEYS */;
/*!40000 ALTER TABLE `input_output_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `internal_wallet_balances`
--

DROP TABLE IF EXISTS `internal_wallet_balances`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `internal_wallet_balances` (
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
  CONSTRAINT `internal_wallet_balances_currency_id_fk` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
  CONSTRAINT `internal_wallet_balances_role_id_fk` FOREIGN KEY (`role_id`) REFERENCES `user_role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `internal_wallet_balances`
--

LOCK TABLES `internal_wallet_balances` WRITE;
/*!40000 ALTER TABLE `internal_wallet_balances` DISABLE KEYS */;
INSERT INTO `internal_wallet_balances` VALUES (1,300,1,0.000000000000,0.000000000000,0.00000000,0.00000000,0.00000000,'2019-06-11 08:59:33'),(2,300,2,0.000000000000,0.000000000000,0.00000000,0.00000000,0.00000000,'2019-06-11 08:59:33'),(3,300,3,0.000000000000,0.000000000000,0.00000000,0.00000000,0.00000000,'2019-06-11 08:59:33'),(4,300,4,0.000000000000,0.000000000000,0.00000000,0.00000000,0.00000000,'2019-06-11 08:59:33'),(5,300,5,0.000000000000,0.000000000000,0.00000000,0.00000000,0.00000000,'2019-06-11 08:59:33'),(6,300,6,0.000000000000,0.000000000000,0.00000000,0.00000000,0.00000000,'2019-06-11 08:59:33'),(7,300,7,0.000000000000,0.000000000000,0.00000000,0.00000000,0.00000000,'2019-06-11 08:59:33'),(8,300,8,0.000000000000,0.000000000000,0.00000000,0.00000000,0.00000000,'2019-06-11 08:59:33'),(9,300,9,0.000000000000,0.000000000000,0.00000000,0.00000000,0.00000000,'2019-06-11 08:59:33'),(10,300,10,0.000000000000,0.000000000000,0.00000000,0.00000000,0.00000000,'2019-06-11 08:59:33'),(11,300,11,0.000000000000,0.000000000000,0.00000000,0.00000000,0.00000000,'2019-06-11 08:59:33'),(12,300,12,0.000000000000,0.000000000000,0.00000000,0.00000000,0.00000000,'2019-06-11 08:59:33');
/*!40000 ALTER TABLE `internal_wallet_balances` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoice_bank`
--

DROP TABLE IF EXISTS `invoice_bank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoice_bank` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `currency_id` int(11) DEFAULT NULL,
  `name` varchar(60) DEFAULT NULL,
  `account_number` varchar(60) DEFAULT NULL,
  `recipient` varchar(150) DEFAULT NULL,
  `bank_details` varchar(300) DEFAULT NULL,
  `hidden` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `invoice_bank___fk_currency` (`currency_id`),
  CONSTRAINT `invoice_bank___fk_currency` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoice_bank`
--

LOCK TABLES `invoice_bank` WRITE;
/*!40000 ALTER TABLE `invoice_bank` DISABLE KEYS */;
INSERT INTO `invoice_bank` VALUES (1,10,'BCA','3150963141','Nanda Rizal Pahlewi',NULL,1),(2,10,'MANDIRI','1440099965557','Nanda Rizal Pahlewi',NULL,1),(3,10,'BNI','0483087786','Nanda Rizal Pahlewi',NULL,1),(4,10,'BRI','057901000435567','Nanda Rizal Pahlewi',NULL,1),(5,17,'Vietcombank VND','‎0251002734890','PHAM TRUONG HO','Bank name: VIETCOMBANK CHI NHÁNH BÌNH TÂY',0),(6,20,'Diamond Bank PLC','‎‎068520105558301',NULL,'Branch: Dubai Mall Branch\r\nSWIFT Code / BIC : DUIBAEAD\r\nIBAN: AE330240068520105558301',0),(7,8,'China Merchants Bank','‎‎110923954410401',NULL,'公司名稱：北京艾特天下网络科技有限公司\r\n銀行名稱: 招商银行\r\n地址：招商银行北京东直门支行',0),(8,10,'BCA','3150970261','BASUNI',NULL,1),(9,10,'BRI','124701007080505','BASUNI',NULL,1),(10,10,'BNI','0474501360','BASUNI',NULL,1),(11,10,'CIMB NIAGA','704912137600','Tab Xtra',NULL,0);
/*!40000 ALTER TABLE `invoice_bank` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoice_operation_direction`
--

DROP TABLE IF EXISTS `invoice_operation_direction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoice_operation_direction` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` enum('REFILL','WITHDRAW','TRANSFER_VOUCHER') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `INVOICE_OPERATION_DIRECTION_name_uindex` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoice_operation_direction`
--

LOCK TABLES `invoice_operation_direction` WRITE;
/*!40000 ALTER TABLE `invoice_operation_direction` DISABLE KEYS */;
INSERT INTO `invoice_operation_direction` VALUES (1,'REFILL'),(2,'WITHDRAW'),(3,'TRANSFER_VOUCHER');
/*!40000 ALTER TABLE `invoice_operation_direction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoice_operation_permission`
--

DROP TABLE IF EXISTS `invoice_operation_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoice_operation_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoice_operation_permission`
--

LOCK TABLES `invoice_operation_permission` WRITE;
/*!40000 ALTER TABLE `invoice_operation_permission` DISABLE KEYS */;
INSERT INTO `invoice_operation_permission` VALUES (1,'VIEW_ONLY'),(2,'ACCEPT_DECLINE');
/*!40000 ALTER TABLE `invoice_operation_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ip_log`
--

DROP TABLE IF EXISTS `ip_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ip_log` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `ip` varchar(100) NOT NULL,
  `user_id` int(40) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_IP_Logs_USERS1_idx` (`user_id`),
  CONSTRAINT `fk_IP_Logs_USERS1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ip_log`
--

LOCK TABLES `ip_log` WRITE;
/*!40000 ALTER TABLE `ip_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `ip_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kyc_country_codes`
--

DROP TABLE IF EXISTS `kyc_country_codes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kyc_country_codes` (
  `id` int(40) unsigned NOT NULL AUTO_INCREMENT,
  `country_name` varchar(64) NOT NULL,
  `country_code` varchar(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=244 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kyc_country_codes`
--

LOCK TABLES `kyc_country_codes` WRITE;
/*!40000 ALTER TABLE `kyc_country_codes` DISABLE KEYS */;
INSERT INTO `kyc_country_codes` VALUES (1,'Aland Islands','AX'),(2,'Albania','AL'),(3,'Algeria','DZ'),(4,'American Samoa','AS'),(5,'Andorra','AD'),(6,'Angola','AO'),(7,'Anguilla','AI'),(8,'Antarctica','AQ'),(9,'Antigua and Barbuda','AG'),(10,'Argentina','AR'),(11,'Armenia','AM'),(12,'Aruba','AW'),(13,'Australia','AU'),(14,'Austria','AT'),(15,'Azerbaijan','AZ'),(16,'Bahamas','BS'),(17,'Bahrain','BH'),(18,'Bangladesh','BD'),(19,'Barbados','BB'),(20,'Belarus','BY'),(21,'Belgium','BE'),(22,'Belize','BZ'),(23,'Benin','BJ'),(24,'Bermuda','BM'),(25,'Bhutan','BT'),(26,'Bolivia','BO'),(27,'Bosnia and Herzegovina','BA'),(28,'Botswana','BW'),(29,'Bouvet Island','BV'),(30,'Brazil','BR'),(31,'British Indian Ocean Territory','IO'),(32,'Brunei','BN'),(33,'Bulgaria','BG'),(34,'Burkina Faso','BF'),(35,'Burma (Myanmar)','MM'),(36,'Burundi','BI'),(37,'Cambodia','KH'),(38,'Cameroon','CM'),(39,'Canada','CA'),(40,'Cape Verde','CV'),(41,'Cayman Islands','KY'),(42,'Central African Republic','CF'),(43,'Chad','TD'),(44,'Chile','CL'),(45,'China','CN'),(46,'Christmas Island','CX'),(47,'Cocos (Keeling) Islands','CC'),(48,'Colombia','CO'),(49,'Comoros','KM'),(50,'Congo,  Dem. Republic','CD'),(51,'Congo,  Republic','CG'),(52,'Cook Islands','CK'),(53,'Costa Rica','CR'),(54,'Croatia','HR'),(55,'Cuba','CU'),(56,'Cyprus','CY'),(57,'Czech Republic','CZ'),(58,'Denmark','DK'),(59,'Djibouti','DJ'),(60,'Dominica','DM'),(61,'Dominican Republic','DO'),(62,'East Timor','TL'),(63,'Ecuador','EC'),(64,'Egypt','EG'),(65,'El Salvador','SV'),(66,'Equatorial Guinea','GQ'),(67,'Eritrea','ER'),(68,'Estonia','EE'),(69,'Ethiopia','ET'),(70,'Falkland Islands','FK'),(71,'Faroe Islands','FO'),(72,'Fiji','FJ'),(73,'Finland','FI'),(74,'France','FR'),(75,'French Guiana','GF'),(76,'French Polynesia','PF'),(77,'French Southern Territories','TF'),(78,'Gabon','GA'),(79,'Gambia','GM'),(80,'Georgia','GE'),(81,'Germany','DE'),(82,'Ghana','GH'),(83,'Gibraltar','GI'),(84,'Greece','GR'),(85,'Greenland','GL'),(86,'Grenada','GD'),(87,'Guadeloupe','GP'),(88,'Guam','GU'),(89,'Guatemala','GT'),(90,'Guernsey','GG'),(91,'Guinea','GN'),(92,'Guinea-Bissau','GW'),(93,'Guyana','GY'),(94,'Haiti','HT'),(95,'Heard Island and McDonald Islands','HM'),(96,'Honduras','HN'),(97,'HongKong','HK'),(98,'Hungary','HU'),(99,'Iceland','IS'),(100,'India','IN'),(101,'Indonesia','ID'),(102,'Iran','IR'),(103,'Iraq','IQ'),(104,'Ireland','IE'),(105,'Israel','IL'),(106,'Italy','IT'),(107,'Ivory Coast','CI'),(108,'Jamaica','JM'),(109,'Japan','JP'),(110,'Jersey','JE'),(111,'Jordan','JO'),(112,'Kazakhstan','KZ'),(113,'Kenya','KE'),(114,'Kiribati','KI'),(115,'Korea,  Dem. Republic of','KP'),(116,'Kuwait','KW'),(117,'Kyrgyzstan','KG'),(118,'Laos','LA'),(119,'Latvia','LV'),(120,'Lebanon','LB'),(121,'Lesotho','LS'),(122,'Liberia','LR'),(123,'Libya','LY'),(124,'Liechtenstein','LI'),(125,'Lithuania','LT'),(126,'Luxemburg','LU'),(127,'Macau','MO'),(128,'Macedonia','MK'),(129,'Madagascar','MG'),(130,'Malawi','MW'),(131,'Malaysia','MY'),(132,'Maldives','MV'),(133,'Mali','ML'),(134,'Malta','MT'),(135,'Man Island','IM'),(136,'Marshall Islands','MH'),(137,'Martinique','MQ'),(138,'Mauritania','MR'),(139,'Mauritius','MU'),(140,'Mayotte','YT'),(141,'Mexico','MX'),(142,'Micronesia','FM'),(143,'Moldova','MD'),(144,'Monaco','MC'),(145,'Mongolia','MN'),(146,'Montenegro','ME'),(147,'Montserrat','MS'),(148,'Morocco','MA'),(149,'Mozambique','MZ'),(150,'Namibia','NA'),(151,'Nauru','NR'),(152,'Nepal','NP'),(153,'Netherlands','NL'),(154,'Netherlands Antilles','AN'),(155,'New Caledonia','NC'),(156,'New Zealand','NZ'),(157,'Nicaragua','NI'),(158,'Niger','NE'),(159,'Nigeria','NG'),(160,'Niue','NU'),(161,'Norfolk Island','NF'),(162,'Northern Mariana Islands','MP'),(163,'Norway','NO'),(164,'Oman','OM'),(165,'Pakistan','PK'),(166,'Palau','PW'),(167,'Palestinian Territories','PS'),(168,'Panama','PA'),(169,'Papua New Guinea','PG'),(170,'Paraguay','PY'),(171,'Peru','PE'),(172,'Philippines','PH'),(173,'Pitcairn','PN'),(174,'Poland','PL'),(175,'Portugal','PT'),(176,'Puerto Rico','PR'),(177,'Qatar','QA'),(178,'Reunion Island','RE'),(179,'Romania','RO'),(180,'Russian Federation','RU'),(181,'Rwanda','RW'),(182,'Saint Barthelemy','BL'),(183,'Saint Kitts and Nevis','KN'),(184,'Saint Lucia','LC'),(185,'Saint Martin','MF'),(186,'Saint Pierre and Miquelon','PM'),(187,'Saint Vincent and the Grenadines','VC'),(188,'Samoa','WS'),(189,'San Marino','SM'),(190,'Saudi Arabia','SA'),(191,'Senegal','SN'),(192,'Serbia','RS'),(193,'Seychelles','SC'),(194,'Sierra Leone','SL'),(195,'Singapore','SG'),(196,'Slovakia','SK'),(197,'Slovenia','SI'),(198,'Solomon Islands','SB'),(199,'Somalia','SO'),(200,'South Africa','ZA'),(201,'South Georgia and the South Sandwich Islands','GS'),(202,'South Korea','KR'),(203,'Spain','ES'),(204,'Sri Lanka','LK'),(205,'Sudan','SD'),(206,'Suriname','SR'),(207,'Svalbard and Jan Mayen','SJ'),(208,'Swaziland','SZ'),(209,'Sweden','SE'),(210,'Switzerland','CH'),(211,'Syria','SY'),(212,'São Tomé and Príncipe','ST'),(213,'Taiwan','TW'),(214,'Tajikistan','TJ'),(215,'Tanzania','TZ'),(216,'Thailand','TH'),(217,'Togo','TG'),(218,'Tokelau','TK'),(219,'Tonga','TO'),(220,'Trinidad and Tobago','TT'),(221,'Tunisia','TN'),(222,'Turkey','TR'),(223,'Turkmenistan','TM'),(224,'Turks and Caicos Islands','TC'),(225,'Tuvalu','TV'),(226,'Uganda','UG'),(227,'Ukraine','UA'),(228,'United Arab Emirates','AE'),(229,'United Kingdom','GB'),(230,'United States','US'),(231,'Uruguay','UY'),(232,'Uzbekistan','UZ'),(233,'Vanuatu','VU'),(234,'Vatican City State','VA'),(235,'Venezuela','VE'),(236,'Vietnam','VN'),(237,'Virgin Islands (British)','VG'),(238,'Virgin Islands (U.S.)','VI'),(239,'Wallis and Futuna','WF'),(240,'Western Sahara','EH'),(241,'Yemen','YE'),(242,'Zambia','ZM'),(243,'Zimbabwe','ZW');
/*!40000 ALTER TABLE `kyc_country_codes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kyc_language_codes`
--

DROP TABLE IF EXISTS `kyc_language_codes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kyc_language_codes` (
  `id` int(40) unsigned NOT NULL AUTO_INCREMENT,
  `language_name` varchar(64) NOT NULL,
  `language_code` varchar(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kyc_language_codes`
--

LOCK TABLES `kyc_language_codes` WRITE;
/*!40000 ALTER TABLE `kyc_language_codes` DISABLE KEYS */;
INSERT INTO `kyc_language_codes` VALUES (1,'Albanian','SQ'),(2,'Amharic','AM'),(3,'Arabic','AR'),(4,'Armenian','HY'),(5,'Azerbaijani','AZ'),(6,'Basque','EU'),(7,'Belarusian','BE'),(8,'Bengali','BN'),(9,'Bosnian','BS'),(10,'Bulgarian','BG'),(11,'Burmese','MY'),(12,'Catalan','CA'),(13,'Chichewa','NY'),(14,'Chinese','ZH'),(15,'Corsican','CO'),(16,'Croatian','HR'),(17,'Czech','CS'),(18,'Danish','DA'),(19,'Dutch','NL'),(20,'English','EN'),(21,'Esperanto','EO'),(22,'Estonian','ET'),(23,'Filipino','TL'),(24,'Finnish','FI'),(25,'French','FR'),(26,'Frisian','FY'),(27,'Galician','GL'),(28,'Georgian','KA'),(29,'German','DE'),(30,'Greek (modern)','EL'),(31,'Gujarati','GU'),(32,'Haitian,  Haitian Creole','HT'),(33,'Hausa','HA'),(34,'Hebrew (modern)','HE'),(35,'Hindi','HI'),(36,'Hungarian','HU'),(37,'Icelandic','IS'),(38,'Igbo','IG'),(39,'Indonesian','ID'),(40,'Irish','GA'),(41,'Italian','IT'),(42,'Japanese','JA'),(43,'Javanese','JV'),(44,'Kannada','KN'),(45,'Kazakh','KK'),(46,'Khmer','KM'),(47,'Kirghiz,  Kyrgyz','KY'),(48,'Korean','KO'),(49,'Kurdish','KU'),(50,'Lao','LO'),(51,'Latin','LA'),(52,'Latvian','LV'),(53,'Lithuanian','LT'),(54,'Luxembourgish,  Letzeburgesch','LB'),(55,'Macedonian','MK'),(56,'Malagasy','MG'),(57,'Malay','MS'),(58,'Malayalam','ML'),(59,'Maltese','MT'),(60,'Maori','MI'),(61,'Marathi','MR'),(62,'Mongolian','MN'),(63,'Nepali','NE'),(64,'Norwegian','NO'),(65,'Pashto','PS'),(66,'Persian','FA'),(67,'Polish','PL'),(68,'Portuguese','PT'),(69,'Punjabi','PA'),(70,'Romanian','RO'),(71,'Russian','RU'),(72,'Samoan','SM'),(73,'Scottish Gaelic','GD'),(74,'Serbian','SR'),(75,'Sesotho','ST'),(76,'Shona','SN'),(77,'Sindhi','SD'),(78,'Sinhala','SI'),(79,'Slovak','SK'),(80,'Slovenian','SL'),(81,'Somali','SO'),(82,'Spanish','ES'),(83,'Sundanese','SU'),(84,'Swahili','SW'),(85,'Swedish','SV'),(86,'Tajik','TG'),(87,'Tamil','TA'),(88,'Telugu','TE'),(89,'Thai','TH'),(90,'Turkish','TR'),(91,'Ukrainian','UK'),(92,'Urdu','UR'),(93,'Uzbek','UZ'),(94,'Vietnamese','VI'),(95,'Welsh','CY'),(96,'Xhosa','XH'),(97,'Yiddish','YI'),(98,'Yoruba','YO'),(99,'Zulu','ZU');
/*!40000 ALTER TABLE `kyc_language_codes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchant`
--

DROP TABLE IF EXISTS `merchant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `merchant` (
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
) ENGINE=InnoDB AUTO_INCREMENT=313 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant`
--

LOCK TABLES `merchant` WRITE;
/*!40000 ALTER TABLE `merchant` DISABLE KEYS */;
INSERT INTO `merchant` VALUES (1,'Yandex kassa','Yandex kassa',20,2,'yandexKassaServiceImpl','MERCHANT',NULL,0),(2,'Perfect Money','Perfect Money',1,2,'perfectMoneyServiceImpl','MERCHANT',NULL,0),(3,'Bitcoin','Bitcoin',21,8,'bitcoinServiceImpl','CRYPTO',NULL,0),(4,'EDR Coin','EDR Coin',22,2,'edrcServiceImpl','CRYPTO',NULL,0),(5,'Advcash Money','Advcash Money',3,2,'advcashServiceImpl','MERCHANT',NULL,0),(6,'Yandex.Money','Yandex.Money',23,2,'yandexMoneyServiceImpl','MERCHANT',NULL,0),(7,'LiqPay','LiqPay',24,2,'liqpayServiceImpl','MERCHANT',NULL,0),(8,'Nix Money','Nix Money',4,2,'nixMoneyServiceImpl','MERCHANT',NULL,0),(9,'Privat24','Privat24',25,2,'privat24ServiceImpl','MERCHANT',NULL,0),(10,'Interkassa','Interkassa',26,2,'interkassaServiceImpl','MERCHANT',NULL,1),(12,'Invoice','Invoice',27,7,'invoiceServiceImpl','INVOICE',NULL,0),(13,'E-DinarCoin','EDC',28,2,'EDCServiceImpl','CRYPTO',NULL,0),(14,'OkPay','OkPay',2,2,'okPayServiceImpl','MERCHANT',NULL,0),(15,'Payeer','Payeer',NULL,2,'payeerServiceImpl','MERCHANT',NULL,0),(16,'Ethereum','Ethereum',NULL,2,'ethereumServiceImpl','CRYPTO',NULL,0),(17,'Litecoin','Litecoin',NULL,8,'litecoinServiceImpl','CRYPTO',NULL,0),(18,'Ethereum Classic','Ethereum Classic',NULL,2,'ethereumClassicServiceImpl','CRYPTO',NULL,0),(19,'Dash','Dash',NULL,8,'dashServiceImpl','CRYPTO',NULL,0),(20,'Ripple','Ripple',NULL,2,'rippleServiceImpl','CRYPTO',NULL,0),(21,'Stellar','Stellar',NULL,2,'stellarServiceImpl','CRYPTO',NULL,0),(30,'SimpleTransfer','SimpleTransfer',NULL,2,'transferSimpleServiceImpl','TRANSFER',NULL,0),(31,'VoucherTransfer','VoucherTransfer',NULL,2,'transferVoucherServiceImpl','TRANSFER',NULL,0),(32,'VoucherFreeTransfer','VoucherFreeTransfer',NULL,2,'transferVoucherFreeServiceImpl','TRANSFER',NULL,0),(33,'NEM','NEM',NULL,2,'nemServiceImpl','CRYPTO',NULL,0),(34,'ATB-coin','ATB',NULL,2,'atbServiceImpl','CRYPTO',NULL,0),(35,'Bitcoin Cash','Bitcoin Cash',NULL,2,'bitcoinCashServiceImpl','CRYPTO',NULL,0),(36,'IOTA','IOTA',NULL,2,'iotaServiceImpl','CRYPTO',NULL,0),(37,'Dogecoin','Dogecoin',NULL,2,'dogecoinServiceImpl','CRYPTO',NULL,0),(38,'Lisk','Lisk',NULL,2,'liskServiceImpl','CRYPTO',NULL,0),(39,'Monero','Monero',NULL,2,'moneroServiceImpl','CRYPTO',NULL,0),(40,'EOS','EOS',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(41,'REP','REP',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(42,'Golem','Golem',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(43,'BTG','BTG',NULL,2,'btgServiceImpl','CRYPTO',NULL,0),(44,'B2X','B2X',NULL,2,'b2xServiceImpl','CRYPTO',NULL,0),(45,'BCD','BCD',NULL,2,'bcdServiceImpl','CRYPTO',NULL,0),(46,'NEO','NEO',NULL,2,'neoServiceImpl','CRYPTO',NULL,0),(47,'GAS','GAS',NULL,2,'neoServiceImpl','CRYPTO',46,0),(48,'Zcash','Zcash',NULL,2,'zcashServiceImpl','CRYPTO',NULL,0),(49,'OmiseGO','OmiseGO',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(50,'Waves','Waves',NULL,2,'wavesServiceImpl','CRYPTO',NULL,0),(51,'BCX','BCX',NULL,2,'bcxServiceImpl','CRYPTO',NULL,0),(52,'ATLANT','ATLANT',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(53,'BitcoinAtom','BitcoinAtom',NULL,2,'bitcoinAtomServiceImpl','CRYPTO',NULL,0),(54,'BinanceCoin','BinanceCoin',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(56,'SBTC','SBTC',NULL,2,'sbtcServiceImpl','CRYPTO',NULL,0),(57,'Octoin','OCC',NULL,2,'occServiceImpl','CRYPTO',NULL,0),(58,'BitRent','BitRent',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(59,'ETZ','EtherZero',NULL,2,'etzServiceImpl','CRYPTO',NULL,0),(60,'NIO','NIO',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(61,'GOS','GOS',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(62,'BitcoinZ','BTCZ',NULL,2,'btczServiceImpl','CRYPTO',NULL,0),(63,'BitcoinWhite','BitcoinWhite',NULL,2,'btwServiceImpl','CRYPTO',NULL,0),(64,'BPTN','BPTN',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(65,'Qtum','Qtum',NULL,2,'qtumServiceImpl','CRYPTO',NULL,0),(66,'BTCP','BTCP',NULL,2,'btcpServiceImpl','CRYPTO',NULL,0),(67,'TAXI','TAXI',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(68,'LitecoinCash','LCC',NULL,2,'lccServiceImpl','CRYPTO',NULL,0),(69,'GameX','GameX',NULL,2,'wavesServiceImpl','CRYPTO',50,0),(70,'Ink','Ink',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(71,'DimCoin','DimCoin',NULL,2,'nemServiceImpl','CRYPTO',33,0),(72,'Nebeus Crypto Bank','NBTK',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(73,'NBC','NBC',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(74,'U.CASH','UCASH',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(75,'PLC','PLC',NULL,2,'plcServiceImpl','CRYPTO',NULL,0),(76,'Nami','NAC',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(77,'ECHAT TOKEN','ECHT',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(78,'DietBitcoin','DDX_old',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(79,'DIT','DIT',NULL,2,'ditcoinServiceImpl','CRYPTO',NULL,0),(80,'indaHash Coin','IDH',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(81,'ShopZCoin','SZC',NULL,2,'szcServiceImpl','CRYPTO',NULL,0),(82,'Com Bill Token','COBC',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(83,'BitCore','BTX',NULL,2,'btxServiceImpl','CRYPTO',NULL,0),(84,'BCI','BCI',NULL,2,'bciServiceImpl','CRYPTO',NULL,0),(85,'Lightning Bitcoin','LBTC',NULL,2,'lbtcServiceImpl','CRYPTO',NULL,0),(86,'BCShop.io','BCS',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(87,'BitDollar','BitDollar',NULL,2,'bitdollarServiceImpl','CRYPTO',NULL,0),(88,'Smartlands','SLT',NULL,2,'stellarServiceImpl','CRYPTO',23,0),(89,'Uquid Coin','UQC',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(90,'Ino Coin','INO',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(91,'Ormeus Coin','ORME',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(92,'PROFIT','PROFIT',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(93,'Bezop Coin','BEZ',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(94,'Amon ','AMN',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(95,'RiseVision','RiseVision',NULL,2,'riseServiceImpl','CRYPTO',NULL,0),(96,'Guaranteed Entrance','GET',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(97,'Simmitri','SIM',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(98,'Fire Lotto','FLOT',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(99,'AML','AML',NULL,2,'amlServiceImpl','CRYPTO',NULL,0),(100,'Veri Doc Global','VDG',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(101,'Pundix','NPXSXEM',NULL,2,'nemServiceImpl','CRYPTO',33,0),(102,'DEEP AERO','DRONE',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(103,'Digitex Futures','DGTX',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(104,'NuShares','NuShares',NULL,2,'nsrServiceImpl','CRYPTO',NULL,0),(105,'Ark','Ark',NULL,2,'arkServiceImpl','CRYPTO',NULL,0),(106,'WINDORSCOIN','WDSC',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(107,'Forty Seven Bank Token','FSBT',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(108,'Rotharium','RTH',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(109,'iPRONTO','IPR',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(110,'Cashaa','CAS',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(111,'Space Chain','SPC',NULL,2,'qtumServiceImpl','CRYPTO',65,0),(112,'SPINDLE','SPD',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(113,'BBCashCoin','BBX',NULL,2,'bbccServiceImpl','CRYPTO',NULL,0),(114,'Beetlecoin','BEET',NULL,2,'beetServiceImpl','CRYPTO',NULL,0),(115,'Medical Token','MTC',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(116,'Halal Chain','HLC',NULL,2,'qtumServiceImpl','CRYPTO',65,0),(117,'Datarius Credit','DTRC',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(118,'CALLISTO','CLO',NULL,2,'cloServiceImpl','CRYPTO',NULL,0),(119,'Tonestra','TNR',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(120,'CEEK','CEEK',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(121,'NYC','NYC',NULL,2,'nycoinServiceImpl','CRYPTO',NULL,0),(122,'Bitcoiin2Gen','B2G',NULL,2,'b2gServiceImpl','CRYPTO',NULL,0),(123,'Engagement Token','ENGT',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(124,'Aeron','ARN',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(125,'AnyCoin','ANY',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(126,'Fantasy Gold','FGC',NULL,2,'fgcServiceImpl','CRYPTO',NULL,0),(127,'TGAME','TGAME',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(128,'Decision Token','HST',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(129,'Tavittcoin','TAVITT',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(130,'Perfectcoin','Perfectcoin',NULL,2,'ptcServiceImpl','CRYPTO',NULL,0),(131,'Metal','MTL',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(132,'Goldiam','GOL',NULL,2,'golServiceImpl','CRYPTO',NULL,0),(133,'Education','LEDU',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(134,'HShare','HSR',NULL,2,'hsrServiceImpl','CRYPTO',NULL,0),(135,'ContractNet','CNET',NULL,2,'cnetServiceImpl','CRYPTO',NULL,0),(136,'CEDEX','CEDEX',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(137,'Lunes','LUNES',NULL,2,'lunesServiceImpl','CRYPTO',NULL,0),(138,'AdBank','ADB',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(139,'Crypto Harbor Exchange','CHE',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(140,'Gamestars','GST',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(141,'BitcoinClean','BitcoinClean',NULL,2,'bclServiceImpl','CRYPTO',NULL,0),(142,'Bitcoinreco','BRECO',NULL,2,'brecoServiceImpl','CRYPTO',NULL,0),(143,'ACHAIN','ACHAIN',NULL,2,'achainServiceImpl','CRYPTO',NULL,0),(144,'Vexanium','VEX',NULL,2,'achainServiceImpl','CRYPTO',NULL,0),(145,'FuturoCoin','FTO',NULL,2,'ftoServiceImpl','CRYPTO',NULL,0),(146,'Universal Mobile Token','UMT',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(147,'SABR','SABR',NULL,2,'sabrServiceImpl','CRYPTO',NULL,0),(148,'Equalizer','EQL',NULL,2,'eqlServiceImpl','CRYPTO',NULL,0),(149,'MaspToken','MASP',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(150,'Ventory','VNT',NULL,2,'stellarServiceImpl','CRYPTO',23,0),(153,'SKILLCOIN','SKILL',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(154,'Social Activity Token','SAT',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(155,'Self Storage Coin','STOR',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(156,'Decentralized Accessible Content Chain','DACC',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(157,'QUiNTillion Burgh','QUiNT',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(158,'Ternio','TERN',NULL,2,'stellarServiceImpl','CRYPTO',23,0),(159,'Tabs Tracking Chain','TTC',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(160,'Nexty','NTY',NULL,2,'ntyServiceImpl','CRYPTO',NULL,0),(161,'Blocform Global','BFG',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(162,'Sumokoin','SUMO',NULL,2,'sumoServiceImpl','CRYPTO',NULL,0),(163,'KGS','KGS',NULL,2,'icoServiceImpl','CRYPTO',NULL,0),(164,'Breakbits','BRB',NULL,2,'brbServiceImpl','CRYPTO',NULL,0),(165,'Jetcoin','JET',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(166,'RizWave','RIZ',NULL,2,'rizServiceImpl','CRYPTO',NULL,0),(167,'PATRON','PAT',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(168,'KWATT','KWATT',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(169,'Multiversum','eMTV',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(170,'FireToken','FPWR',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(171,'TrueUSD','TUSD',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(172,'CRUISEBIT','CRBT',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(173,'Hive token','HIVE',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(174,'Tron','TRX',NULL,2,'tronServiceImpl','CRYPTO',NULL,0),(175,'Hedger','HDR',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(176,'CMITCOIN','CMIT',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(177,'RoboAdvisorCoin','RAC',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(179,'IQeon','IQN',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(180,'Etherinc','ETI',NULL,2,'etherincServiceImpl','CRYPTO',NULL,0),(181,'GREENX','GEX',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(182,'Swisscoin','SIC',NULL,2,'sicServiceImpl','CRYPTO',NULL,0),(183,'IXTUS Edutainment','IXE',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(184,'CryptoLux','CLX',NULL,2,'clxServiceImpl','CRYPTO',NULL,0),(185,'PHI Token','PHI',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(186,'Nerves','NER',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(187,'Mainstream for the Underground','MFTU',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(188,'RealTract','RET',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(189,'MarketC','CMK',NULL,2,'cmkServiceImpl','CRYPTO',NULL,0),(190,'GIG Coin','GIGC',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(191,'Decred','DCR',NULL,2,'decredServiceImpl','CRYPTO',NULL,0),(192,'Swarm Fund','SWM',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(193,'Bionic','BNC',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(194,'Thingschain','TIC',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(195,'uDOO','uDOO',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(196,'Welltrado token','WTL',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(197,'Aidos Kuneen','ADK',NULL,2,'adkServiceImpl','CRYPTO',NULL,0),(198,'Quark','QRK',NULL,2,'qrkServiceImpl','CRYPTO',NULL,0),(199,'Lightpaycoin','LPC',NULL,2,'lpcServiceImpl','CRYPTO',NULL,0),(200,'TOAcoin','TOA',NULL,2,'TOAServiceImpl','CRYPTO',NULL,0),(201,'FootballCoin','XFC',NULL,2,'xfcServiceImpl','CRYPTO',NULL,0),(202,'Cryptic Coin','CRYP',NULL,2,'crypServiceImpl','CRYPTO',NULL,0),(203,'DietBitcoin','DDX',NULL,2,'ddxServiceImpl','CRYPTO',NULL,0),(204,'MicroBitcoin','MBC',NULL,2,'mbcServiceImpl','CRYPTO',NULL,0),(205,'Apollo','APL',NULL,2,'apolloServiceImpl','CRYPTO',NULL,0),(206,'Au Coin','XAU',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(207,'KAZECoin','KAZE',NULL,2,'kazeServiceImpl','CRYPTO',NULL,0),(208,'KazeSTREAM','STREAM',NULL,2,'kazeServiceImpl','CRYPTO',207,0),(209,'Trent','TTP',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(210,'USD Coin','USDC',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(211,'Aunit Coin','AUNIT',NULL,2,'aunitServiceImpl','CRYPTO',NULL,0),(212,'ABBC Coin','ABBC',NULL,2,'abbcServiceImpl','CRYPTO',NULL,0),(213,'Cashbery Coin','CBC',NULL,2,'cbcServiceImpl','CRYPTO',NULL,0),(214,'Bitcoin Cash','BCH',NULL,2,'bchServiceImpl','CRYPTO',NULL,0),(215,'Bitcoin SV','BSV',NULL,2,'bsvServiceImpl','CRYPTO',NULL,0),(217,'Quick','Q',NULL,2,'qServiceImpl','CRYPTO',NULL,0),(218,'VIOLET','VAI',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(219,'Unicom','UNC',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(220,'MODULE','MODL',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(222,'DimeCoin','DIME',NULL,2,'dimeServiceImpl','CRYPTO',NULL,0),(223,'QIWI','QIWI',NULL,2,'qiwiServiceImpl','CRYPTO',NULL,0),(224,'MEGAX','MGX',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(225,'MainCoin','MNC',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(226,'S4FE','S4F',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(227,'EurocoinToken','ECTE',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(228,'Centauri','CTX',NULL,2,'ctxServiceImpl','CRYPTO',NULL,0),(229,'ENDO Token','EDT',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(230,'Huobi token','HT',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(231,'RimeCoin','RIME',NULL,2,'rimeServiceImpl','CRYPTO',NULL,0),(233,'TheCurrencyAnalytics','TCAT',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(234,'Tael','WaBi',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(235,'POA ERC20 on Foundation','POA',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(236,'Crypto.com','MCO',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(237,'Zilliqa','ZIL',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(238,'Decentraland','MANA',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(239,'Exosis','EXO',NULL,2,'exoServiceImpl','CRYPTO',NULL,0),(240,'GroestlCoin','GRS',NULL,2,'grsServiceImpl','CRYPTO',NULL,0),(241,'KODCoin','KOD',NULL,2,'kodServiceImpl','CRYPTO',NULL,0),(242,'HCXPay','HCXP',NULL,2,'hcxpServiceImpl','CRYPTO',NULL,0),(243,'QuarkChain Token','QKC',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(244,'Pundi X Token','NPXS',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(245,'HoloToken','HOT',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(246,'0x','ZRX',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(247,'Basic Attention Token','BAT',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(248,'Raiden Network Token','RDN',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(249,'DIM.EUR','DIM.EUR',NULL,2,'nemServiceImpl','CRYPTO',33,0),(250,'DIM.USD','DIM.USD',NULL,2,'nemServiceImpl','CRYPTO',33,0),(251,'DIGIT coin','DIGIT',NULL,2,'nemServiceImpl','CRYPTO',33,0),(252,'Ethereum Lendo Token','ELT',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(253,'HUNI','HNI',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(254,'WolfCoin','WOLF',NULL,2,'wolfServiceImpl','CRYPTO',NULL,0),(255,'Republic Protocol','REN',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(256,'Metronome','MET',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(257,'Tether US','USDT',NULL,2,'omniServiceImpl','CRYPTO',NULL,0),(258,'PlatonCoin','PLTC',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(259,'Divicoin','DIVI',NULL,2,'diviServiceImpl','CRYPTO',NULL,0),(260,'BitTorrent','BTT',NULL,2,'tronServiceImpl','CRYPTO',174,0),(261,'PeerPlays','PPY',NULL,2,'ppyServiceImpl','CRYPTO',NULL,0),(262,'Viribustoken','VRBS',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(263,'Zuzubecoin','ZUBE',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(264,'OduwaCoin','OWC',NULL,2,'owcServiceImpl','CRYPTO',NULL,0),(265,'EconomicLeisureCoin','ELC',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(266,'CasinoCoin','CSC',NULL,2,'casinoCoinServiceImpl','CRYPTO',NULL,0),(267,'RobboCoach','RBC',NULL,2,'wavesServiceImpl','CRYPTO',50,0),(268,'TTV','TTT',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(269,'REBGLO','REB',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(271,'Qubera','Qubera',NULL,2,'quberaServiceImpl','MERCHANT',NULL,1),(276,'Crea','CREA',NULL,2,'creaServiceImpl','CRYPTO',NULL,0),(278,'Aisicoin','AISI',NULL,2,'aisiServiceImpl','CRYPTO',NULL,0),(279,'RenvaleCoin','RVC',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(280,'BioCrypt ','BIO',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(281,'Etheera ','ETA',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(282,'Kambria ','KAT',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(283,'VERA','VRA',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(284,'BaerChain','BRC',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(285,'CRONFoundation','CRON',NULL,2,'cronServiceImpl','CRYPTO',NULL,0),(286,'GNY token','GNY',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(287,'Novachain','NOVA',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(291,'Konstellation','DARC',NULL,2,'nemServiceImpl','CRYPTO',33,0),(305,'TreasureSL','TSL',NULL,2,'tslServiceImpl','CRYPTO',NULL,0),(306,'V-Dimension','VOLLAR',NULL,2,'vollarServiceImpl','CRYPTO',NULL,0),(307,'GAPICOIN','GAPI',NULL,2,'gapiServiceImpl','CRYPTO',NULL,0),(308,'1irstcoin','FST',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(309,'Renvale Token','RVT',NULL,2,'ethereumServiceImpl','CRYPTO',16,0),(310,'Rewards4u','RWDS',NULL,2,'nemServiceImpl','CRYPTO',33,0),(311,'LightHouse','LHT',NULL,2,'lightHouseServiceImpl','CRYPTO',NULL,0),(312,'Token LINA','LINA',NULL,2,'ethereumServiceImpl','CRYPTO',16,0);
/*!40000 ALTER TABLE `merchant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchant_currency`
--

DROP TABLE IF EXISTS `merchant_currency`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `merchant_currency` (
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
  CONSTRAINT `merchant_currency_ibfk_1` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `merchant_currency_ibfk_2` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant_currency`
--

LOCK TABLES `merchant_currency` WRITE;
/*!40000 ALTER TABLE `merchant_currency` DISABLE KEYS */;
INSERT INTO `merchant_currency` VALUES (30,300,0.000001000,0.000000000,0.000000000,0.000000000,1,1,0,0.000000000,0.000000000,0.00000000,'2019-06-11 08:59:33',0,0,0.000000000,0,0,NULL,NULL,NULL,0,0,0,0),(31,300,0.000001000,0.000000000,0.000000000,0.000000000,1,1,0,0.000000000,0.000000000,0.00000000,'2019-06-11 08:59:33',0,0,0.000000000,0,0,NULL,NULL,NULL,0,0,0,0),(32,300,0.000001000,0.000000000,0.000000000,0.000000000,1,1,0,0.000000000,0.000000000,0.00000000,'2019-06-11 08:59:33',0,0,0.000000000,0,0,NULL,NULL,NULL,0,0,0,0),(312,300,0.000000010,0.000000000,0.000000000,0.000000000,1,1,1,0.000000000,0.000000000,0.00000000,'2019-06-11 08:59:33',0,0,0.000000000,0,0,NULL,NULL,NULL,0,0,0,0);
/*!40000 ALTER TABLE `merchant_currency` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchant_image`
--

DROP TABLE IF EXISTS `merchant_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `merchant_image` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `merchant_id` int(11) NOT NULL,
  `image_path` varchar(300) NOT NULL,
  `image_name` varchar(45) NOT NULL,
  `currency_id` int(11) NOT NULL,
  `child_merchant` varchar(40) DEFAULT '',
  `input_commission` double(5,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`),
  KEY `fk_MERCHANT_id_idx` (`merchant_id`),
  CONSTRAINT `fk3_MERCHANT_id` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant_image`
--

LOCK TABLES `merchant_image` WRITE;
/*!40000 ALTER TABLE `merchant_image` DISABLE KEYS */;
INSERT INTO `merchant_image` VALUES (1,312,'/client/img/merchants/LINA.png','LINA',300,'',0.00),(2,30,'/client/img/merchants/transfer.png','Transfer',300,'',0.00),(3,31,'/client/img/merchants/voucher.png','Voucher',300,'',0.00),(4,32,'/client/img/merchants/voucher_free.png','Free voucher',300,'',0.00);
/*!40000 ALTER TABLE `merchant_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchant_spec_parameters`
--

DROP TABLE IF EXISTS `merchant_spec_parameters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `merchant_spec_parameters` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `merchant_id` int(40) NOT NULL,
  `param_name` varchar(200) NOT NULL,
  `param_value` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MERCHANT_SPEC_PARAMETERS_merchant_id` (`merchant_id`),
  CONSTRAINT `fk_MERCHANT_SPEC_PARAMETERS_merchant_id` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant_spec_parameters`
--

LOCK TABLES `merchant_spec_parameters` WRITE;
/*!40000 ALTER TABLE `merchant_spec_parameters` DISABLE KEYS */;
INSERT INTO `merchant_spec_parameters` VALUES (3,21,'LastPagingToken','95938922168774657'),(4,33,'LastRecievedTrHash','58f296d9acaadb0f19bfb83479ddd21c3daeafcfbdd5caf311d49fb6f2b0ba0d'),(5,16,'LastRecievedBlock','7877988'),(6,18,'LastRecievedBlock','7780698'),(7,46,'LastRecievedBlock','1723993'),(8,65,'LastRecievedBlock','309794'),(9,70,'LastRecievedBlock','121850'),(10,71,'exrateToNem','0.018'),(11,59,'LastRecievedBlock','9977157'),(12,101,'exrateToNem','0.0011'),(13,111,'LastRecievedBlock','262397'),(14,113,'lastBlock','20239f9a9162bee6983c347dfb2d21188599f9ec9e0e5c1051976039892b9f50'),(15,116,'LastRecievedBlock','192383'),(16,118,'LastRecievedBlock','2365030'),(17,122,'LastRecievedBlock','2551351'),(18,132,'LastRecievedBlock','1817926'),(19,135,'LastRecievedBlock','381916'),(20,143,'LastScannedBlock','4857700'),(21,79,'LastRecievedBlock','330599'),(22,160,'LastRecievedBlock','13504294'),(23,162,'LastRecievedBlock','181459'),(24,174,'LastScannedBlock','9818935'),(25,180,'LastRecievedBlock','3100054'),(26,191,'LastBlock','297271'),(27,197,'LastBundle','ZTMABWLXNVUQEZQADALNBUKGHUJPSJIQOUGUMDQLBDGBFLLEQLFLFQIZVXCZRXYTAQIWXSHCJ9FSKGLLN'),(28,205,'LastBlockTime','33413033'),(29,207,'LastRecievedBlock','1723993'),(30,211,'last_irreversible_block_num','5614635'),(31,249,'exrateToNem','22'),(32,250,'exrateToNem','19'),(33,251,'exrateToNem','0.01'),(34,257,'LastScannedBlock','561039'),(35,261,'last_irreversible_block_num','20791431'),(36,276,'last_irreversible_block_num','1272334'),(37,276,'last_irreversible_block_num','1272334'),(38,285,'LastRecievedBlock','899101'),(39,291,'exrateToNem','1.187'),(40,310,'exrateToNem','40');
/*!40000 ALTER TABLE `merchant_spec_parameters` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `news`
--

DROP TABLE IF EXISTS `news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `news` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Ð”Ð°Ñ‚Ð°, ÐºÐ¾Ñ‚Ð¾Ñ€Ð¾Ð¹ Ð´Ð°Ñ‚Ð¸Ñ€ÑƒÐµÑ‚ÑÑ Ð½Ð¾Ð²Ð¾ÑÑ‚ÑŒ',
  `resource` varchar(100) DEFAULT NULL COMMENT 'ÐžÑ‚Ð½Ð¾ÑÐ¸Ñ‚ÐµÐ»ÑŒÐ½Ñ‹Ð¹ Ð¿ÑƒÑ‚ÑŒ Ñ€Ð°Ð·Ð¼ÐµÑ‰ÐµÐ½Ð¸Ñ Ñ„Ð°Ð¹Ð»Ð¾Ð² (html, css, img, Ð¸ Ñ‚.Ð¿.) ÐºÐ¾Ð½Ñ‚ÐµÐ½Ñ‚Ð° Ð½Ð¾Ð²Ð¾ÑÑ‚Ð¸. ',
  `description` varchar(200) DEFAULT NULL COMMENT 'ÐžÐ¿Ð¸ÑÐ°Ð½Ð¸Ðµ, Ð¸ÑÐ¿Ð¾Ð»ÑŒÐ·ÑƒÐµÐ¼Ð¾Ðµ Ð´Ð»Ñ Ð²Ð½ÑƒÑ‚Ñ€ÐµÐ½Ð½Ð¸Ñ… Ñ†ÐµÐ»ÐµÐ¹ (Ð½Ðµ Ð´Ð»Ñ Ð¿ÑƒÐ±Ð»Ð¸ÐºÐ°Ñ†Ð¸Ð¸)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news`
--

LOCK TABLES `news` WRITE;
/*!40000 ALTER TABLE `news` DISABLE KEYS */;
/*!40000 ALTER TABLE `news` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `news_ext`
--

DROP TABLE IF EXISTS `news_ext`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `news_ext` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `resource` varchar(100) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `news_type_id` int(11) DEFAULT NULL,
  `calendar_date` date DEFAULT NULL,
  `no_title_img` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `FK_news_news_type` (`news_type_id`),
  CONSTRAINT `FK_news_news_type` FOREIGN KEY (`news_type_id`) REFERENCES `news_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news_ext`
--

LOCK TABLES `news_ext` WRITE;
/*!40000 ALTER TABLE `news_ext` DISABLE KEYS */;
/*!40000 ALTER TABLE `news_ext` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `news_type`
--

DROP TABLE IF EXISTS `news_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `news_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news_type`
--

LOCK TABLES `news_type` WRITE;
/*!40000 ALTER TABLE `news_type` DISABLE KEYS */;
INSERT INTO `news_type` VALUES (1,'NEWS'),(2,'MATERIALS'),(3,'WEBINAR'),(4,'VIDEO'),(5,'EVENT'),(6,'FEASTDAY'),(7,'PAGE');
/*!40000 ALTER TABLE `news_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `news_variants`
--

DROP TABLE IF EXISTS `news_variants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `news_variants` (
  `news_id` int(11) NOT NULL,
  `title` varchar(100) DEFAULT NULL COMMENT 'Ð—Ð°Ð³Ð¾Ð»Ð¾Ð²Ð¾Ðº Ð½Ð¾Ð²Ð¾ÑÑ‚Ð¸ Ð½Ð° ÑÐ·Ñ‹ÐºÐµ, ÑÐ¾Ð¾Ñ‚Ð²ÐµÑ‚ÑÑ‚Ð²ÑƒÑŽÑ‰ÐµÐ¼ ÑÐ·Ñ‹ÐºÐ¾Ð²Ð¾Ð¼Ñƒ Ð²Ð°Ñ€Ð¸Ð°Ð½Ñ‚Ñƒ Ð½Ð¾Ð²Ð¾ÑÑ‚Ð¸',
  `news_variant` enum('ru','en','cn','in','ar') NOT NULL DEFAULT 'ru',
  `brief` varchar(250) DEFAULT NULL COMMENT 'ÐšÑ€Ð°Ñ‚ÐºÐ¾Ðµ ÑÐ¾Ð´ÐµÑ€Ð¶Ð°Ð½Ð¸Ðµ Ð½Ð¾Ð²Ð¾ÑÑ‚Ð¸ Ð½Ð° ÑÐ·Ñ‹ÐºÐµ, ÑÐ¾Ð¾Ñ‚Ð²ÐµÑ‚ÑÑ‚Ð²ÑƒÑŽÑ‰ÐµÐ¼ ÑÐ·Ñ‹ÐºÐ¾Ð²Ð¾Ð¼Ñƒ Ð²Ð°Ñ€Ð¸Ð°Ð½Ñ‚Ñƒ Ð½Ð¾Ð²Ð¾ÑÑ‚Ð¸',
  `added_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Ð”Ð°Ñ‚Ð° Ð´Ð¾Ð±Ð°Ð²Ð»ÐµÐ½Ð¸Ñ ÑÐ·Ñ‹ÐºÐ¾Ð²Ð¾Ð³Ð¾ Ð²Ð°Ñ€Ð¸Ð°Ð½Ñ‚Ð°. ÐœÐ¾Ð¶ÐµÑ‚ Ð¾Ñ‚Ð»Ð¸Ñ‡Ð°Ñ‚ÑŒÑÑ Ð¾Ñ‚ Ð´Ð°Ñ‚Ñ‹ ÑÐ°Ð¼Ð¾Ð¹ Ð½Ð¾Ð²Ð¾ÑÑ‚Ð¸',
  `active` tinyint(4) DEFAULT '1' COMMENT 'Ð¿Ñ€Ð¸Ð·Ð½Ð°Ðº Ð°ÐºÑ‚Ð¸Ð²Ð½Ð¾ÑÑ‚Ð¸ Ð²Ð°Ñ€Ð¸Ð°Ð½Ñ‚Ð° Ð½Ð¾Ð²Ð¾ÑÑ‚Ð¸. ÐÐµÐ°ÐºÑ‚Ð¸Ð²Ð½Ñ‹Ð¹ Ð²Ð°Ñ€Ð¸Ð°Ð½Ñ‚ Ð½Ðµ Ð±ÑƒÐ´ÐµÑ‚ Ð¾Ñ‚Ð¾Ð±Ñ€Ð°Ð¶Ð°Ñ‚ÑŒÑÑ',
  PRIMARY KEY (`news_id`,`news_variant`),
  KEY `news_id` (`news_id`),
  CONSTRAINT `news_variants_fk` FOREIGN KEY (`news_id`) REFERENCES `news` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news_variants`
--

LOCK TABLES `news_variants` WRITE;
/*!40000 ALTER TABLE `news_variants` DISABLE KEYS */;
/*!40000 ALTER TABLE `news_variants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `news_variants_ext`
--

DROP TABLE IF EXISTS `news_variants_ext`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `news_variants_ext` (
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
  CONSTRAINT `news_variants_ext_fk` FOREIGN KEY (`news_id`) REFERENCES `news_ext` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news_variants_ext`
--

LOCK TABLES `news_variants_ext` WRITE;
/*!40000 ALTER TABLE `news_variants_ext` DISABLE KEYS */;
/*!40000 ALTER TABLE `news_variants_ext` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification` (
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
  CONSTRAINT `notification_event_id` FOREIGN KEY (`notification_event_id`) REFERENCES `notification_event` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification_event`
--

DROP TABLE IF EXISTS `notification_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification_event` (
  `id` int(11) NOT NULL,
  `name` varchar(20) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  `default_send_notification` tinyint(1) DEFAULT NULL,
  `default_send_email` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification_event`
--

LOCK TABLES `notification_event` WRITE;
/*!40000 ALTER TABLE `notification_event` DISABLE KEYS */;
INSERT INTO `notification_event` VALUES (1,'CUSTOM',NULL,1,0),(2,'ADMIN',NULL,1,1),(3,'ACCOUNT',NULL,1,1),(4,'ORDER',NULL,1,0),(5,'IN_OUT',NULL,1,1);
/*!40000 ALTER TABLE `notification_event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification_options`
--

DROP TABLE IF EXISTS `notification_options`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification_options` (
  `notification_event_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `send_notification` tinyint(1) NOT NULL DEFAULT '1',
  `send_email` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`notification_event_id`,`user_id`),
  KEY `user_id_idx` (`user_id`),
  CONSTRAINT `fk_notification_event_id1` FOREIGN KEY (`notification_event_id`) REFERENCES `notification_event` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_id1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_id_nop` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification_options`
--

LOCK TABLES `notification_options` WRITE;
/*!40000 ALTER TABLE `notification_options` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification_options` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oauth_access_token`
--

DROP TABLE IF EXISTS `oauth_access_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_access_token` (
  `token_id` varchar(255) DEFAULT NULL,
  `token` longblob,
  `authentication_id` varchar(255) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) DEFAULT NULL,
  `authentication` longblob,
  `refresh_token` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oauth_access_token`
--

LOCK TABLES `oauth_access_token` WRITE;
/*!40000 ALTER TABLE `oauth_access_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `oauth_access_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oauth_approvals`
--

DROP TABLE IF EXISTS `oauth_approvals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_approvals` (
  `userId` varchar(255) DEFAULT NULL,
  `clientId` varchar(255) DEFAULT NULL,
  `scope` varchar(255) DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  `expiresAt` datetime DEFAULT NULL,
  `lastModifiedAt` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oauth_approvals`
--

LOCK TABLES `oauth_approvals` WRITE;
/*!40000 ALTER TABLE `oauth_approvals` DISABLE KEYS */;
/*!40000 ALTER TABLE `oauth_approvals` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oauth_client_details`
--

DROP TABLE IF EXISTS `oauth_client_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oauth_client_details`
--

LOCK TABLES `oauth_client_details` WRITE;
/*!40000 ALTER TABLE `oauth_client_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `oauth_client_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oauth_client_token`
--

DROP TABLE IF EXISTS `oauth_client_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_client_token` (
  `token_id` varchar(255) DEFAULT NULL,
  `token` longblob,
  `authentication_id` varchar(255) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oauth_client_token`
--

LOCK TABLES `oauth_client_token` WRITE;
/*!40000 ALTER TABLE `oauth_client_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `oauth_client_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oauth_code`
--

DROP TABLE IF EXISTS `oauth_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_code` (
  `code` varchar(255) DEFAULT NULL,
  `authentication` varbinary(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oauth_code`
--

LOCK TABLES `oauth_code` WRITE;
/*!40000 ALTER TABLE `oauth_code` DISABLE KEYS */;
/*!40000 ALTER TABLE `oauth_code` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oauth_refresh_token`
--

DROP TABLE IF EXISTS `oauth_refresh_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_refresh_token` (
  `token_id` varchar(255) DEFAULT NULL,
  `token` longblob,
  `authentication` longblob
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oauth_refresh_token`
--

LOCK TABLES `oauth_refresh_token` WRITE;
/*!40000 ALTER TABLE `oauth_refresh_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `oauth_refresh_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `open_api_user_token`
--

DROP TABLE IF EXISTS `open_api_user_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `open_api_user_token` (
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
  CONSTRAINT `open_api_user_token___fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `open_api_user_token`
--

LOCK TABLES `open_api_user_token` WRITE;
/*!40000 ALTER TABLE `open_api_user_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `open_api_user_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operation_type`
--

DROP TABLE IF EXISTS `operation_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `operation_type` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ID_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operation_type`
--

LOCK TABLES `operation_type` WRITE;
/*!40000 ALTER TABLE `operation_type` DISABLE KEYS */;
INSERT INTO `operation_type` VALUES (1,'Input',NULL),(2,'Output',NULL),(3,'sell',NULL),(4,'buy',NULL),(5,'wallet_inner_transfer','between active and reserved balance'),(6,'referral',NULL),(7,'storno','for storno operation'),(8,'manual',NULL),(9,'user_transfer',NULL);
/*!40000 ALTER TABLE `operation_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operation_type_direction`
--

DROP TABLE IF EXISTS `operation_type_direction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `operation_type_direction` (
  `operation_type_id` int(11) NOT NULL,
  `operation_direction_id` int(11) NOT NULL,
  PRIMARY KEY (`operation_type_id`,`operation_direction_id`),
  KEY `op_type_dir__fk_op_dir_id` (`operation_direction_id`),
  CONSTRAINT `op_type_dir__fk_op_dir_id` FOREIGN KEY (`operation_direction_id`) REFERENCES `invoice_operation_direction` (`id`),
  CONSTRAINT `op_type_dir__fk_ot_id` FOREIGN KEY (`operation_type_id`) REFERENCES `operation_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operation_type_direction`
--

LOCK TABLES `operation_type_direction` WRITE;
/*!40000 ALTER TABLE `operation_type_direction` DISABLE KEYS */;
/*!40000 ALTER TABLE `operation_type_direction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_status`
--

DROP TABLE IF EXISTS `order_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_status` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_status`
--

LOCK TABLES `order_status` WRITE;
/*!40000 ALTER TABLE `order_status` DISABLE KEYS */;
INSERT INTO `order_status` VALUES (1,'in process',NULL),(2,'opened',NULL),(3,'closed',NULL),(4,'cancelled','cancelled by user'),(5,'deleted','deleted by admin'),(6,'draft','not submited by user'),(7,'split_closed','split for partial acception');
/*!40000 ALTER TABLE `order_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_type`
--

DROP TABLE IF EXISTS `order_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(10) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_type_name_uindex` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_type`
--

LOCK TABLES `order_type` WRITE;
/*!40000 ALTER TABLE `order_type` DISABLE KEYS */;
INSERT INTO `order_type` VALUES (2,'BUY'),(1,'SELL');
/*!40000 ALTER TABLE `order_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `phrase_template`
--

DROP TABLE IF EXISTS `phrase_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `phrase_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `template` varchar(100) NOT NULL,
  `topic_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_phrase_template_user_comment_topic` (`topic_id`),
  CONSTRAINT `FK_phrase_template_user_comment_topic` FOREIGN KEY (`topic_id`) REFERENCES `user_comment_topic` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `phrase_template`
--

LOCK TABLES `phrase_template` WRITE;
/*!40000 ALTER TABLE `phrase_template` DISABLE KEYS */;
/*!40000 ALTER TABLE `phrase_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `policy`
--

DROP TABLE IF EXISTS `policy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `policy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `policy`
--

LOCK TABLES `policy` WRITE;
/*!40000 ALTER TABLE `policy` DISABLE KEYS */;
/*!40000 ALTER TABLE `policy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qubera_response_log`
--

DROP TABLE IF EXISTS `qubera_response_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qubera_response_log` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qubera_response_log`
--

LOCK TABLES `qubera_response_log` WRITE;
/*!40000 ALTER TABLE `qubera_response_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `qubera_response_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qubera_user_details`
--

DROP TABLE IF EXISTS `qubera_user_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qubera_user_details` (
  `user_id` int(11) NOT NULL,
  `currency_id` int(11) NOT NULL,
  `account_number` varchar(50) NOT NULL,
  `iban` varchar(100) NOT NULL,
  UNIQUE KEY `uq_user_id_and_currency_id_qubera_user_details` (`user_id`,`currency_id`),
  KEY `fk_qubera_user_details_on_currencies` (`currency_id`),
  CONSTRAINT `fk_qubera_user_details_on_currencies` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_qubera_user_details_on_users` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qubera_user_details`
--

LOCK TABLES `qubera_user_details` WRITE;
/*!40000 ALTER TABLE `qubera_user_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `qubera_user_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `referral_level`
--

DROP TABLE IF EXISTS `referral_level`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `referral_level` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `level` int(11) NOT NULL,
  `percent` double DEFAULT NULL,
  `datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `referral_level`
--

LOCK TABLES `referral_level` WRITE;
/*!40000 ALTER TABLE `referral_level` DISABLE KEYS */;
/*!40000 ALTER TABLE `referral_level` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `referral_transaction`
--

DROP TABLE IF EXISTS `referral_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `referral_transaction` (
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
  CONSTRAINT `REFERRAL_TRANSACTION_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `exorders` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `REFERRAL_TRANSACTION_ibfk_2` FOREIGN KEY (`referral_level_id`) REFERENCES `referral_level` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `REFERRAL_TRANSACTION_ibfk_3` FOREIGN KEY (`initiator_id`) REFERENCES `user` (`id`),
  CONSTRAINT `REFERRAL_TRANSACTION_ibfk_4` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `referral_transaction`
--

LOCK TABLES `referral_transaction` WRITE;
/*!40000 ALTER TABLE `referral_transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `referral_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `referral_user_graph`
--

DROP TABLE IF EXISTS `referral_user_graph`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `referral_user_graph` (
  `child` int(11) NOT NULL DEFAULT '0',
  `parent` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`child`,`parent`),
  KEY `REFERRAL_USER_GRAPH` (`parent`),
  CONSTRAINT `REFERRAL_USER_GRAPH_ibfk_2` FOREIGN KEY (`parent`) REFERENCES `user` (`id`),
  CONSTRAINT `referral_user_graph_ibfk_1` FOREIGN KEY (`child`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `referral_user_graph`
--

LOCK TABLES `referral_user_graph` WRITE;
/*!40000 ALTER TABLE `referral_user_graph` DISABLE KEYS */;
/*!40000 ALTER TABLE `referral_user_graph` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refill_request`
--

DROP TABLE IF EXISTS `refill_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `refill_request` (
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
  CONSTRAINT `FK_refill_request_admin_holder` FOREIGN KEY (`admin_holder_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_refill_request_commission` FOREIGN KEY (`commission_id`) REFERENCES `commission` (`id`),
  CONSTRAINT `FK_refill_request_currency` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
  CONSTRAINT `FK_refill_request_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`id`),
  CONSTRAINT `FK_refill_request_refill_request_address` FOREIGN KEY (`refill_request_address_id`) REFERENCES `refill_request_address` (`id`),
  CONSTRAINT `FK_refill_request_refill_request_address_2` FOREIGN KEY (`currency_id`, `merchant_id`, `user_id`, `refill_request_address_id`) REFERENCES `refill_request_address` (`currency_id`, `merchant_id`, `user_id`, `id`),
  CONSTRAINT `FK_refill_request_refill_request_new_status` FOREIGN KEY (`status_id`) REFERENCES `refill_request_status` (`id`),
  CONSTRAINT `FK_refill_request_refill_request_param` FOREIGN KEY (`refill_request_param_id`) REFERENCES `refill_request_param` (`id`),
  CONSTRAINT `FK_refill_request_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refill_request`
--

LOCK TABLES `refill_request` WRITE;
/*!40000 ALTER TABLE `refill_request` DISABLE KEYS */;
/*!40000 ALTER TABLE `refill_request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refill_request_address`
--

DROP TABLE IF EXISTS `refill_request_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `refill_request_address` (
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
  CONSTRAINT `FK_refill_request_address_currency` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
  CONSTRAINT `FK_refill_request_address_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refill_request_address`
--

LOCK TABLES `refill_request_address` WRITE;
/*!40000 ALTER TABLE `refill_request_address` DISABLE KEYS */;
/*!40000 ALTER TABLE `refill_request_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refill_request_confirmation`
--

DROP TABLE IF EXISTS `refill_request_confirmation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `refill_request_confirmation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `refill_request_id` int(11) NOT NULL,
  `datetime` timestamp NULL DEFAULT NULL,
  `confirmation_number` int(11) NOT NULL,
  `amount` decimal(40,9) NOT NULL,
  `blockhash` varchar(96) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_refill_request_confirmation_refill_request` (`refill_request_id`),
  CONSTRAINT `FK_refill_request_confirmation_refill_request` FOREIGN KEY (`refill_request_id`) REFERENCES `refill_request` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refill_request_confirmation`
--

LOCK TABLES `refill_request_confirmation` WRITE;
/*!40000 ALTER TABLE `refill_request_confirmation` DISABLE KEYS */;
/*!40000 ALTER TABLE `refill_request_confirmation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refill_request_param`
--

DROP TABLE IF EXISTS `refill_request_param`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `refill_request_param` (
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
  CONSTRAINT `FK_refill_request_param_invoice_bank` FOREIGN KEY (`recipient_bank_id`) REFERENCES `invoice_bank` (`id`),
  CONSTRAINT `FK_refill_request_param_refill_request` FOREIGN KEY (`id`) REFERENCES `refill_request` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refill_request_param`
--

LOCK TABLES `refill_request_param` WRITE;
/*!40000 ALTER TABLE `refill_request_param` DISABLE KEYS */;
/*!40000 ALTER TABLE `refill_request_param` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refill_request_status`
--

DROP TABLE IF EXISTS `refill_request_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `refill_request_status` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refill_request_status`
--

LOCK TABLES `refill_request_status` WRITE;
/*!40000 ALTER TABLE `refill_request_status` DISABLE KEYS */;
INSERT INTO `refill_request_status` VALUES (1,'CREATED_USER'),(2,'CREATED_BY_FACT'),(3,'WAITING_CONFIRMATION_USER'),(4,'ON_PENDING'),(5,'CONFIRMED_USER'),(6,'ON_BCH_EXAM'),(7,'IN_WORK_OF_ADMIN'),(8,'DECLINED_ADMIN'),(9,'ACCEPTED_AUTO'),(10,'ACCEPTED_ADMIN'),(11,'REVOKED_USER'),(12,'EXPIRED'),(13,'TAKEN_FROM_PENDING'),(14,'TAKEN_FROM_EXAM'),(15,'ON_INNER_TRANSFERRING'),(16,'WAITING_REVIEWING'),(17,'TAKEN_FOR_REFILL');
/*!40000 ALTER TABLE `refill_request_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_mailing_params`
--

DROP TABLE IF EXISTS `report_mailing_params`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report_mailing_params` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `param_name` varchar(50) NOT NULL,
  `param_value` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `report_mailing_params_param_name_uindex` (`param_name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report_mailing_params`
--

LOCK TABLES `report_mailing_params` WRITE;
/*!40000 ALTER TABLE `report_mailing_params` DISABLE KEYS */;
INSERT INTO `report_mailing_params` VALUES (1,'ENABLE_REPORT_MAILING','true'),(2,'MAIL_TIME','00:00');
/*!40000 ALTER TABLE `report_mailing_params` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_subscribers`
--

DROP TABLE IF EXISTS `report_subscribers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report_subscribers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `REPORT_SUBSCRIBERS_email_uindex` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report_subscribers`
--

LOCK TABLES `report_subscribers` WRITE;
/*!40000 ALTER TABLE `report_subscribers` DISABLE KEYS */;
/*!40000 ALTER TABLE `report_subscribers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schema_version`
--

DROP TABLE IF EXISTS `schema_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schema_version`
--

LOCK TABLES `schema_version` WRITE;
/*!40000 ALTER TABLE `schema_version` DISABLE KEYS */;
/*!40000 ALTER TABLE `schema_version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_alerts`
--

DROP TABLE IF EXISTS `service_alerts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_alerts` (
  `alert_type` enum('UPDATE','TECHNICAL_WORKS') NOT NULL,
  `enable` tinyint(1) NOT NULL DEFAULT '0',
  `time_of_start` timestamp NULL DEFAULT NULL,
  `length` int(11) DEFAULT NULL,
  `launch_date` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_alerts`
--

LOCK TABLES `service_alerts` WRITE;
/*!40000 ALTER TABLE `service_alerts` DISABLE KEYS */;
INSERT INTO `service_alerts` VALUES ('UPDATE',0,NULL,NULL,NULL),('TECHNICAL_WORKS',0,NULL,NULL,NULL);
/*!40000 ALTER TABLE `service_alerts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `session_life_time_type`
--

DROP TABLE IF EXISTS `session_life_time_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `session_life_time_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `session_life_time_type`
--

LOCK TABLES `session_life_time_type` WRITE;
/*!40000 ALTER TABLE `session_life_time_type` DISABLE KEYS */;
INSERT INTO `session_life_time_type` VALUES (1,'FIXED_LIFETIME',0),(2,'INACTIVE_COUNT_LIFETIME',1);
/*!40000 ALTER TABLE `session_life_time_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `session_params`
--

DROP TABLE IF EXISTS `session_params`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `session_params` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `session_time_minutes` int(11) NOT NULL,
  `session_life_type_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `FK_session_params_user_id` (`user_id`),
  KEY `FK_session_params_session_life_type_id` (`session_life_type_id`),
  CONSTRAINT `FK_session_params_session_life_type_id` FOREIGN KEY (`session_life_type_id`) REFERENCES `session_life_time_type` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_session_params_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `session_params`
--

LOCK TABLES `session_params` WRITE;
/*!40000 ALTER TABLE `session_params` DISABLE KEYS */;
/*!40000 ALTER TABLE `session_params` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_subscription`
--

DROP TABLE IF EXISTS `sms_subscription`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sms_subscription` (
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
  CONSTRAINT `fk_2FA_SMS_SUBSCRIPTION_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_subscription`
--

LOCK TABLES `sms_subscription` WRITE;
/*!40000 ALTER TABLE `sms_subscription` DISABLE KEYS */;
/*!40000 ALTER TABLE `sms_subscription` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_currency_pair`
--

DROP TABLE IF EXISTS `stock_currency_pair`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stock_currency_pair` (
  `stock_exchange_id` int(11) NOT NULL,
  `currency_pair_id` int(11) NOT NULL,
  PRIMARY KEY (`stock_exchange_id`,`currency_pair_id`),
  KEY `fk_stock_corrency_pair_id_idx` (`currency_pair_id`),
  CONSTRAINT `fk_stock_corrency_pair_id` FOREIGN KEY (`currency_pair_id`) REFERENCES `currency_pair` (`id`),
  CONSTRAINT `fk_stock_curr_pair_stock_ex_id` FOREIGN KEY (`stock_exchange_id`) REFERENCES `stock_exchange` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_currency_pair`
--

LOCK TABLES `stock_currency_pair` WRITE;
/*!40000 ALTER TABLE `stock_currency_pair` DISABLE KEYS */;
/*!40000 ALTER TABLE `stock_currency_pair` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_exchange`
--

DROP TABLE IF EXISTS `stock_exchange`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stock_exchange` (
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_exchange`
--

LOCK TABLES `stock_exchange` WRITE;
/*!40000 ALTER TABLE `stock_exchange` DISABLE KEYS */;
/*!40000 ALTER TABLE `stock_exchange` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_exchange_currency_alias`
--

DROP TABLE IF EXISTS `stock_exchange_currency_alias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stock_exchange_currency_alias` (
  `stock_exchange_id` int(11) NOT NULL,
  `currency_id` int(11) NOT NULL,
  `alias` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`stock_exchange_id`,`currency_id`),
  KEY `stock_exchange_currency_alias___fk_cur_id` (`currency_id`),
  CONSTRAINT `stock_exchange_currency_alias___fk_cur_id` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
  CONSTRAINT `stock_exchange_currency_alias___fk_st_ex_id` FOREIGN KEY (`stock_exchange_id`) REFERENCES `stock_exchange` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_exchange_currency_alias`
--

LOCK TABLES `stock_exchange_currency_alias` WRITE;
/*!40000 ALTER TABLE `stock_exchange_currency_alias` DISABLE KEYS */;
/*!40000 ALTER TABLE `stock_exchange_currency_alias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_exrate`
--

DROP TABLE IF EXISTS `stock_exrate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stock_exrate` (
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
  CONSTRAINT `fk_STOCK_CURRENCY_PAIR_id` FOREIGN KEY (`currency_pair_id`) REFERENCES `currency_pair` (`id`),
  CONSTRAINT `fk_stock_exchange_id` FOREIGN KEY (`stock_exchange_id`) REFERENCES `stock_exchange` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_exrate`
--

LOCK TABLES `stock_exrate` WRITE;
/*!40000 ALTER TABLE `stock_exrate` DISABLE KEYS */;
/*!40000 ALTER TABLE `stock_exrate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stop_orders`
--

DROP TABLE IF EXISTS `stop_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stop_orders` (
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
  CONSTRAINT `fk_stop_orders_CHILD_ORDER` FOREIGN KEY (`child_order_id`) REFERENCES `exorders` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_stop_orders_COMMISSION` FOREIGN KEY (`commission_id`) REFERENCES `commission` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_stop_orders_CURRENCY_PAIR` FOREIGN KEY (`currency_pair_id`) REFERENCES `currency_pair` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_stop_orders_OPERATION_TYPE` FOREIGN KEY (`operation_type_id`) REFERENCES `operation_type` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_stop_orders_ORDER_STATUS` FOREIGN KEY (`status_id`) REFERENCES `order_status` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_stop_orders_USER_CREATOR` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stop_orders`
--

LOCK TABLES `stop_orders` WRITE;
/*!40000 ALTER TABLE `stop_orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `stop_orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `survey`
--

DROP TABLE IF EXISTS `survey`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `token` varchar(100) NOT NULL,
  `active` tinyint(4) NOT NULL DEFAULT '1',
  `json` varchar(21000) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `survey`
--

LOCK TABLES `survey` WRITE;
/*!40000 ALTER TABLE `survey` DISABLE KEYS */;
/*!40000 ALTER TABLE `survey` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `survey_item`
--

DROP TABLE IF EXISTS `survey_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `survey_id` int(11) NOT NULL,
  `lang` varchar(5) NOT NULL,
  `name` varchar(50) NOT NULL,
  `title` varchar(300) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `survey_id_lang_name` (`survey_id`,`lang`,`name`),
  CONSTRAINT `FK_survey_item_survey` FOREIGN KEY (`survey_id`) REFERENCES `survey` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `survey_item`
--

LOCK TABLES `survey_item` WRITE;
/*!40000 ALTER TABLE `survey_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `survey_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `survey_lang_param`
--

DROP TABLE IF EXISTS `survey_lang_param`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey_lang_param` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `survey_id` int(11) NOT NULL,
  `lang` varchar(5) NOT NULL,
  `description` varchar(1024) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `survey_lang_param_id_lang_name` (`survey_id`,`lang`),
  CONSTRAINT `FK_survey_item_survey_lang_param` FOREIGN KEY (`survey_id`) REFERENCES `survey` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `survey_lang_param`
--

LOCK TABLES `survey_lang_param` WRITE;
/*!40000 ALTER TABLE `survey_lang_param` DISABLE KEYS */;
/*!40000 ALTER TABLE `survey_lang_param` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `telegram_chat_en`
--

DROP TABLE IF EXISTS `telegram_chat_en`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `telegram_chat_en` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `telegram_chat_en`
--

LOCK TABLES `telegram_chat_en` WRITE;
/*!40000 ALTER TABLE `telegram_chat_en` DISABLE KEYS */;
/*!40000 ALTER TABLE `telegram_chat_en` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `telegram_subscription`
--

DROP TABLE IF EXISTS `telegram_subscription`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `telegram_subscription` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `code` varchar(64) DEFAULT NULL,
  `subscription_state` enum('SUBSCRIBED','WAIT_FOR_SUBSCRIBE') NOT NULL,
  `user_account` varchar(64) DEFAULT NULL,
  `chat_id` mediumtext,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `TELEGRAM_SUBSCRIPTION_user_id` (`user_id`),
  CONSTRAINT `fk_TELEGRAM_SUBSCRIPTION_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `telegram_subscription`
--

LOCK TABLES `telegram_subscription` WRITE;
/*!40000 ALTER TABLE `telegram_subscription` DISABLE KEYS */;
/*!40000 ALTER TABLE `telegram_subscription` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `temporal_token`
--

DROP TABLE IF EXISTS `temporal_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `temporal_token` (
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
  CONSTRAINT `fk_REGISTRATION_TOKEN_USER1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fk_TEMPORAL_TOKEN_TYPE` FOREIGN KEY (`token_type_id`) REFERENCES `token_type` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `temporal_token`
--

LOCK TABLES `temporal_token` WRITE;
/*!40000 ALTER TABLE `temporal_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `temporal_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `token_type`
--

DROP TABLE IF EXISTS `token_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `token_type` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `token_type`
--

LOCK TABLES `token_type` WRITE;
/*!40000 ALTER TABLE `token_type` DISABLE KEYS */;
INSERT INTO `token_type` VALUES (1,'registration'),(2,'changePassword'),(3,'changeFinPassword'),(4,'confirmNewIp');
/*!40000 ALTER TABLE `token_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction` (
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
  CONSTRAINT `fk_COMPANY_ACCOUNT_WALLET1` FOREIGN KEY (`user_wallet_id`) REFERENCES `wallet` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_EXORDERS` FOREIGN KEY (`order_id`) REFERENCES `exorders` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `transaction_ibfk_1` FOREIGN KEY (`commission_id`) REFERENCES `commission` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `transaction_ibfk_3` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `transaction_ibfk_4` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `transaction_ibfk_5` FOREIGN KEY (`operation_type_id`) REFERENCES `operation_type` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `transaction_ibfk_6` FOREIGN KEY (`company_wallet_id`) REFERENCES `company_wallet` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `transaction_status_fk` FOREIGN KEY (`status_id`) REFERENCES `transaction_status` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction_source_type`
--

DROP TABLE IF EXISTS `transaction_source_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction_source_type` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_source_type`
--

LOCK TABLES `transaction_source_type` WRITE;
/*!40000 ALTER TABLE `transaction_source_type` DISABLE KEYS */;
INSERT INTO `transaction_source_type` VALUES (1,'ORDER'),(3,'REFERRAL'),(4,'ACCRUAL'),(5,'MANUAL'),(6,'USER_TRANSFER'),(9,'WITHDRAW'),(10,'STOP_ORDER'),(11,'REFILL'),(12,'NOTIFICATIONS'),(13,'IEO');
/*!40000 ALTER TABLE `transaction_source_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction_status`
--

DROP TABLE IF EXISTS `transaction_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction_status` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_status`
--

LOCK TABLES `transaction_status` WRITE;
/*!40000 ALTER TABLE `transaction_status` DISABLE KEYS */;
INSERT INTO `transaction_status` VALUES (1,'created',''),(2,'deleted','deleted by admin');
/*!40000 ALTER TABLE `transaction_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transfer_request`
--

DROP TABLE IF EXISTS `transfer_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transfer_request` (
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
  CONSTRAINT `FK_transfer_request_commission` FOREIGN KEY (`commission_id`) REFERENCES `commission` (`id`),
  CONSTRAINT `FK_transfer_request_currency` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
  CONSTRAINT `FK_transfer_request_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`id`),
  CONSTRAINT `FK_transfer_request_transfer_request_status` FOREIGN KEY (`status_id`) REFERENCES `transfer_request_status` (`id`),
  CONSTRAINT `FK_transfer_request_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_transfer_request_user_2` FOREIGN KEY (`recipient_user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transfer_request`
--

LOCK TABLES `transfer_request` WRITE;
/*!40000 ALTER TABLE `transfer_request` DISABLE KEYS */;
/*!40000 ALTER TABLE `transfer_request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transfer_request_status`
--

DROP TABLE IF EXISTS `transfer_request_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transfer_request_status` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transfer_request_status`
--

LOCK TABLES `transfer_request_status` WRITE;
/*!40000 ALTER TABLE `transfer_request_status` DISABLE KEYS */;
INSERT INTO `transfer_request_status` VALUES (1,'CREATED_USER'),(2,'POSTED'),(3,'REVOKED'),(4,'POSTPONED_AS_VOUCHER'),(5,'REVOKED_ADMIN');
/*!40000 ALTER TABLE `transfer_request_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
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
  `kyc_reference` varchar(55) NOT NULL DEFAULT 'none',
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
  CONSTRAINT `fk_USER_ROLE_id` FOREIGN KEY (`roleid`) REFERENCES `user_role` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_USER_USER_STATUS1` FOREIGN KEY (`status`) REFERENCES `user_status` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_admin_authority`
--

DROP TABLE IF EXISTS `user_admin_authority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_admin_authority` (
  `user_id` int(11) NOT NULL,
  `admin_authority_id` int(11) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`user_id`,`admin_authority_id`),
  KEY `fk_admin_authority_id_idx` (`admin_authority_id`),
  CONSTRAINT `fk_admin_authority_id` FOREIGN KEY (`admin_authority_id`) REFERENCES `admin_authority` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_id_auth` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_admin_authority`
--

LOCK TABLES `user_admin_authority` WRITE;
/*!40000 ALTER TABLE `user_admin_authority` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_admin_authority` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_admin_authority_role_application`
--

DROP TABLE IF EXISTS `user_admin_authority_role_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_admin_authority_role_application` (
  `user_id` int(11) NOT NULL,
  `admin_authority_id` int(11) NOT NULL,
  `applied_to_role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`admin_authority_id`,`applied_to_role_id`),
  KEY `user_admin_authority_role_application___fk_aaid` (`admin_authority_id`),
  KEY `user_admin_authority_role_application___fk_role` (`applied_to_role_id`),
  KEY `user_admin_authority_role_application___fk_u_id` (`user_id`),
  CONSTRAINT `user_admin_authority_role_application___fk_aaid` FOREIGN KEY (`admin_authority_id`) REFERENCES `admin_authority` (`id`),
  CONSTRAINT `user_admin_authority_role_application___fk_role` FOREIGN KEY (`applied_to_role_id`) REFERENCES `user_role` (`id`),
  CONSTRAINT `user_admin_authority_role_application___fk_u_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_admin_authority_role_application`
--

LOCK TABLES `user_admin_authority_role_application` WRITE;
/*!40000 ALTER TABLE `user_admin_authority_role_application` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_admin_authority_role_application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_comment`
--

DROP TABLE IF EXISTS `user_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_comment` (
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
  CONSTRAINT `FK_user_comment_user_comment_topic` FOREIGN KEY (`topic_id`) REFERENCES `user_comment_topic` (`id`),
  CONSTRAINT `fk_COMMENT_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_COMMENT_USER_CREATOR` FOREIGN KEY (`user_creator_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_comment`
--

LOCK TABLES `user_comment` WRITE;
/*!40000 ALTER TABLE `user_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_comment_topic`
--

DROP TABLE IF EXISTS `user_comment_topic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_comment_topic` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `topic` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_comment_topic`
--

LOCK TABLES `user_comment_topic` WRITE;
/*!40000 ALTER TABLE `user_comment_topic` DISABLE KEYS */;
INSERT INTO `user_comment_topic` VALUES (1,'GENERAL'),(2,'REFILL_DECLINE'),(3,'REFILL_CURRENCY_WARNING'),(4,'WITHDRAW_DECLINE'),(5,'WITHDRAW_POSTED'),(6,'WITHDRAW_CURRENCY_WARNING'),(7,'REFILL_ACCEPTED'),(8,'TRANSFER_CURRENCY_WARNING'),(10,'REFILL_MERCHANT_WARNING'),(11,'WITHDRAW_MERCHANT_WARNING');
/*!40000 ALTER TABLE `user_comment_topic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_currency_invoice_operation_permission`
--

DROP TABLE IF EXISTS `user_currency_invoice_operation_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_currency_invoice_operation_permission` (
  `user_id` int(11) NOT NULL,
  `currency_id` int(11) NOT NULL,
  `operation_direction` enum('REFILL','WITHDRAW','TRANSFER_VOUCHER') NOT NULL,
  `invoice_operation_permission_id` int(11) NOT NULL,
  `operation_direction_id` int(11) DEFAULT NULL,
  UNIQUE KEY `user_id_currency_id_operation_direction` (`user_id`,`currency_id`,`operation_direction`),
  KEY `FK_user_currency_invoice_permission_invoice_permission` (`invoice_operation_permission_id`),
  KEY `u_op_perm_op_direction_id_fk` (`operation_direction_id`),
  CONSTRAINT `FK_user_currency_invoice_permission_invoice_permission` FOREIGN KEY (`invoice_operation_permission_id`) REFERENCES `invoice_operation_permission` (`id`),
  CONSTRAINT `u_op_perm_op_direction_id_fk` FOREIGN KEY (`operation_direction_id`) REFERENCES `invoice_operation_direction` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_currency_invoice_operation_permission`
--

LOCK TABLES `user_currency_invoice_operation_permission` WRITE;
/*!40000 ALTER TABLE `user_currency_invoice_operation_permission` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_currency_invoice_operation_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_doc`
--

DROP TABLE IF EXISTS `user_doc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_doc` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `path` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `path` (`path`),
  KEY `USER_DOCS` (`user_id`),
  CONSTRAINT `USER_DOC_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_doc`
--

LOCK TABLES `user_doc` WRITE;
/*!40000 ALTER TABLE `user_doc` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_doc` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_favorite_currency_pairs`
--

DROP TABLE IF EXISTS `user_favorite_currency_pairs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_favorite_currency_pairs` (
  `user_id` int(11) NOT NULL,
  `currency_pair_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`currency_pair_id`),
  KEY `user_cp_idx` (`user_id`),
  KEY `user_cp_cp_fk` (`currency_pair_id`),
  CONSTRAINT `USER_FAVORITE_CURRENCY_PAIRS_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `USER_FAVORITE_CURRENCY_PAIRS_ibfk_2` FOREIGN KEY (`currency_pair_id`) REFERENCES `currency_pair` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_favorite_currency_pairs`
--

LOCK TABLES `user_favorite_currency_pairs` WRITE;
/*!40000 ALTER TABLE `user_favorite_currency_pairs` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_favorite_currency_pairs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_initial_exchange_offerings`
--

DROP TABLE IF EXISTS `user_initial_exchange_offerings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_initial_exchange_offerings` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_initial_exchange_offerings`
--

LOCK TABLES `user_initial_exchange_offerings` WRITE;
/*!40000 ALTER TABLE `user_initial_exchange_offerings` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_initial_exchange_offerings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_ip`
--

DROP TABLE IF EXISTS `user_ip`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_ip` (
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
  CONSTRAINT `user_ip_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_ip`
--

LOCK TABLES `user_ip` WRITE;
/*!40000 ALTER TABLE `user_ip` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_ip` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_operation`
--

DROP TABLE IF EXISTS `user_operation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_operation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_operation`
--

LOCK TABLES `user_operation` WRITE;
/*!40000 ALTER TABLE `user_operation` DISABLE KEYS */;
INSERT INTO `user_operation` VALUES (1,'INPUT','2018-10-04 10:21:56','2018-10-04 10:21:56'),(2,'OUTPUT','2018-10-04 10:21:56','2018-10-04 10:21:56'),(3,'TRANSFER','2018-10-04 10:21:56','2018-10-04 10:21:56'),(4,'TRADING','2018-10-04 10:21:56','2018-10-04 10:21:56');
/*!40000 ALTER TABLE `user_operation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_operation_authority`
--

DROP TABLE IF EXISTS `user_operation_authority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_operation_authority` (
  `user_id` int(11) NOT NULL,
  `user_operation_id` int(11) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`,`user_operation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_operation_authority`
--

LOCK TABLES `user_operation_authority` WRITE;
/*!40000 ALTER TABLE `user_operation_authority` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_operation_authority` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_polices`
--

DROP TABLE IF EXISTS `user_polices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_polices` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `policy_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `policy_id` (`policy_id`),
  CONSTRAINT `USER_POLICES_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `USER_POLICES_ibfk_2` FOREIGN KEY (`policy_id`) REFERENCES `policy` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_polices`
--

LOCK TABLES `user_polices` WRITE;
/*!40000 ALTER TABLE `user_polices` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_polices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `user_role_business_feature_id` int(11) DEFAULT NULL,
  `user_role_group_feature_id` int(11) DEFAULT NULL,
  `user_role_report_group_feature_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_user_role_user_role_business_feature` (`user_role_business_feature_id`),
  KEY `FK_user_role_user_role_group_feature` (`user_role_group_feature_id`),
  KEY `user_role___fk_report_group_feature` (`user_role_report_group_feature_id`),
  CONSTRAINT `FK_user_role_user_role_business_feature` FOREIGN KEY (`user_role_business_feature_id`) REFERENCES `user_role_business_feature` (`id`),
  CONSTRAINT `FK_user_role_user_role_group_feature` FOREIGN KEY (`user_role_group_feature_id`) REFERENCES `user_role_group_feature` (`id`),
  CONSTRAINT `user_role___fk_report_group_feature` FOREIGN KEY (`user_role_report_group_feature_id`) REFERENCES `user_role_report_group_feature` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1,'ADMINISTRATOR',1,1,1),(2,'ACCOUNTANT',1,1,1),(3,'ADMIN_USER',1,1,1),(4,'USER',2,2,2),(5,'ROLE_CHANGE_PASSWORD',NULL,2,NULL),(6,'EXCHANGE',3,2,2),(7,'VIP_USER',4,2,2),(8,'TRADER',5,2,3),(9,'FIN_OPERATOR',1,1,1),(10,'BOT_TRADER',6,3,4),(11,'ICO_MARKET_MAKER',7,2,2),(12,'OUTER_MARKET_BOT',9,2,4);
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role_business_feature`
--

DROP TABLE IF EXISTS `user_role_business_feature`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role_business_feature` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role_business_feature`
--

LOCK TABLES `user_role_business_feature` WRITE;
/*!40000 ALTER TABLE `user_role_business_feature` DISABLE KEYS */;
INSERT INTO `user_role_business_feature` VALUES (1,'ADMIN'),(2,'USER'),(3,'EXCHANGE'),(4,'VIP_USER'),(5,'TRADER'),(6,'BOT'),(7,'MARKET_MAKER'),(9,'OUTER_MARKET_BOT');
/*!40000 ALTER TABLE `user_role_business_feature` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role_group_feature`
--

DROP TABLE IF EXISTS `user_role_group_feature`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role_group_feature` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role_group_feature`
--

LOCK TABLES `user_role_group_feature` WRITE;
/*!40000 ALTER TABLE `user_role_group_feature` DISABLE KEYS */;
INSERT INTO `user_role_group_feature` VALUES (1,'ADMINS'),(2,'USERS'),(3,'BOT');
/*!40000 ALTER TABLE `user_role_group_feature` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role_report_group_feature`
--

DROP TABLE IF EXISTS `user_role_report_group_feature`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role_report_group_feature` (
  `id` int(11) NOT NULL,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_role_report_group_feature_name_uindex` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role_report_group_feature`
--

LOCK TABLES `user_role_report_group_feature` WRITE;
/*!40000 ALTER TABLE `user_role_report_group_feature` DISABLE KEYS */;
INSERT INTO `user_role_report_group_feature` VALUES (1,'ADMIN'),(4,'BOT'),(3,'TRADER'),(2,'USER');
/*!40000 ALTER TABLE `user_role_report_group_feature` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role_settings`
--

DROP TABLE IF EXISTS `user_role_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role_settings` (
  `user_role_id` int(11) NOT NULL,
  `order_acception_same_role_only` tinyint(1) NOT NULL DEFAULT '0',
  `manual_change_allowed` tinyint(1) NOT NULL DEFAULT '1',
  `bot_acception_allowed` tinyint(1) NOT NULL DEFAULT '0',
  `considered_for_price_range` tinyint(1) NOT NULL DEFAULT '0',
  `use_real_money` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`user_role_id`),
  CONSTRAINT `user_role_settings_user_role_id_fk` FOREIGN KEY (`user_role_id`) REFERENCES `user_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role_settings`
--

LOCK TABLES `user_role_settings` WRITE;
/*!40000 ALTER TABLE `user_role_settings` DISABLE KEYS */;
INSERT INTO `user_role_settings` VALUES (1,0,0,0,1,1),(2,0,0,0,0,1),(3,0,0,0,1,1),(4,0,1,0,1,1),(5,0,0,0,0,1),(6,0,0,0,0,1),(7,0,1,0,0,1),(8,0,0,0,1,0),(9,0,0,0,0,1),(10,1,0,1,1,0),(11,0,0,0,0,1),(12,0,0,0,0,1);
/*!40000 ALTER TABLE `user_role_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_status`
--

DROP TABLE IF EXISTS `user_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_status` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_status`
--

LOCK TABLES `user_status` WRITE;
/*!40000 ALTER TABLE `user_status` DISABLE KEYS */;
INSERT INTO `user_status` VALUES (1,'registered','without email confirmation'),(2,'activated','with email confirmation'),(3,'blocked','blocked by admin'),(4,'banned_in_chat','banned in chat by admin');
/*!40000 ALTER TABLE `user_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_transfer`
--

DROP TABLE IF EXISTS `user_transfer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_transfer` (
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
  CONSTRAINT `FK_user_trnasfer_currency_id` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_user_trnasfer_from_user_id` FOREIGN KEY (`from_user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_user_trnasfer_to_user_id` FOREIGN KEY (`to_user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_transfer`
--

LOCK TABLES `user_transfer` WRITE;
/*!40000 ALTER TABLE `user_transfer` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_transfer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_verification_info`
--

DROP TABLE IF EXISTS `user_verification_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_verification_info` (
  `user_id` int(11) NOT NULL,
  `doc_type` enum('ID','P') DEFAULT 'P',
  `doc_id` varchar(55) DEFAULT NULL,
  UNIQUE KEY `user_id` (`user_id`,`doc_type`),
  CONSTRAINT `fk_user_id_on_user_verification_info` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_verification_info`
--

LOCK TABLES `user_verification_info` WRITE;
/*!40000 ALTER TABLE `user_verification_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_verification_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wallet`
--

DROP TABLE IF EXISTS `wallet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `wallet` (
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
  CONSTRAINT `fk_WALLET_CURRENCIES1` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_WALLET_USERS1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallet`
--

LOCK TABLES `wallet` WRITE;
/*!40000 ALTER TABLE `wallet` DISABLE KEYS */;
/*!40000 ALTER TABLE `wallet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `withdraw_request`
--

DROP TABLE IF EXISTS `withdraw_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `withdraw_request` (
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
  CONSTRAINT `FK_withdraw_request_admin_holder` FOREIGN KEY (`admin_holder_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_withdraw_request_commission` FOREIGN KEY (`commission_id`) REFERENCES `commission` (`id`),
  CONSTRAINT `FK_withdraw_request_currency` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
  CONSTRAINT `FK_withdraw_request_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`id`),
  CONSTRAINT `FK_withdraw_request_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_withdraw_request_withdraw_request_status` FOREIGN KEY (`status_id`) REFERENCES `withdraw_request_status` (`id`),
  CONSTRAINT `WITHDRAW_REQUEST_ibfk_2` FOREIGN KEY (`processed_by`) REFERENCES `user` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_merchant_image_id` FOREIGN KEY (`merchant_image_id`) REFERENCES `merchant_image` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `withdraw_request`
--

LOCK TABLES `withdraw_request` WRITE;
/*!40000 ALTER TABLE `withdraw_request` DISABLE KEYS */;
/*!40000 ALTER TABLE `withdraw_request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `withdraw_request_status`
--

DROP TABLE IF EXISTS `withdraw_request_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `withdraw_request_status` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `withdraw_request_status`
--

LOCK TABLES `withdraw_request_status` WRITE;
/*!40000 ALTER TABLE `withdraw_request_status` DISABLE KEYS */;
INSERT INTO `withdraw_request_status` VALUES (1,'CREATED_USER'),(2,'WAITING_MANUAL_POSTING'),(3,'WAITING_AUTO_POSTING'),(4,'WAITING_CONFIRMATION'),(5,'IN_WORK_OF_ADMIN'),(6,'WAITING_CONFIRMED_POSTING'),(7,'REVOKED_USER'),(8,'DECLINED_ADMIN'),(9,'POSTED_MANUAL'),(10,'POSTED_AUTO'),(11,'IN_POSTING'),(12,'DECLINED_ERROR'),(13,'ON_BCH_EXAM'),(14,'WAITING_REVIEWING'),(15,'TAKEN_FOR_WITHDRAW');
/*!40000 ALTER TABLE `withdraw_request_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'birzha_test'
--

--
-- Dumping routines for database 'birzha_test'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-06-11 12:04:50
