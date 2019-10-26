CREATE DATABASE IF NOT EXISTS `hstestdb`;
USE `hstestdb`;

DROP TABLE IF EXISTS `HTML`;

CREATE  TABLE `hstestdb`.`HTML` (
  `uuid` VARCHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  PRIMARY KEY (`uuid`)
);