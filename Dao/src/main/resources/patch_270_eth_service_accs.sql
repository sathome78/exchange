CREATE TABLE `ETH_SERVICE_ACCOUNTS` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `active` boolean default false,
  `private_key` VARCHAR(80) NOT NULL,
  `address` VARCHAR(80) NOT NULL,
  `url` VARCHAR(80) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
)