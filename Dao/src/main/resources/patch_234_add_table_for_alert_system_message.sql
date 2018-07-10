DROP TABLE IF EXISTS `SERVICE_ALERTS_SYSTEM_MESSAGE`;

CREATE TABLE `SERVICE_ALERTS_SYSTEM_MESSAGE` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(1024) DEFAULT NULL,
  `content` varchar(8192) DEFAULT NULL,
  `language` enum('ru','en','cn','in','ar') DEFAULT 'en',
  `added_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

INSERT INTO SERVICE_ALERTS_SYSTEM_MESSAGE (title, content, language) VALUES ('titleRU', 'contentRU', 'ru');

INSERT INTO SERVICE_ALERTS_SYSTEM_MESSAGE (title, content, language) VALUES ('titleEN', 'contentEN', 'en');

INSERT INTO SERVICE_ALERTS_SYSTEM_MESSAGE (title, content, language) VALUES ('titleCN', 'contentCN', 'cn');

INSERT INTO SERVICE_ALERTS_SYSTEM_MESSAGE (title, content, language) VALUES ('titleIN', 'contentIN', 'in');

INSERT INTO SERVICE_ALERTS_SYSTEM_MESSAGE (title, content, language) VALUES ('titleAR', 'contentAR', 'ar');