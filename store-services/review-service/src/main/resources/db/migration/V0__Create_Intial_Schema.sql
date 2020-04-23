-- Create schema is required because we are using MySQL 5.7
-- Flyway with MySQL version 8 creates the schema by default

-- CREATE DATABASE `review-db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `review-db`;

CREATE TABLE `review-db`.`review` (
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `author` VARCHAR(50) DEFAULT NULL,
    `content` VARCHAR(255) DEFAULT NULL,
    `product_id` INTEGER NOT NULL,
    `review_id` INTEGER NOT NULL,
    `subject` VARCHAR(255) DEFAULT NULL,
    `version` INTEGER NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `review_unique_idx` (`product_id` , `review_id`)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8MB4 COLLATE = UTF8MB4_0900_AI_CI;