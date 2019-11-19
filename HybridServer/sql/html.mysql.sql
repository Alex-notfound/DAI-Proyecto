CREATE DATABASE IF NOT EXISTS `hstestdb`;
USE `hstestdb`;

DROP TABLE IF EXISTS `HTML`;
CREATE  TABLE `hstestdb`.`HTML` (
  `uuid` VARCHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  PRIMARY KEY (`uuid`)
);

DROP TABLE IF EXISTS `XSD`;
CREATE  TABLE `hstestdb`.`XSD` (
  `uuid` VARCHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  PRIMARY KEY (`uuid`)
);

DROP TABLE IF EXISTS `XML`;
CREATE  TABLE `hstestdb`.`XML` (
  `uuid` VARCHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  PRIMARY KEY (`uuid`)
);

DROP TABLE IF EXISTS `XSLT`;
CREATE  TABLE `hstestdb`.`XSLT` (
  `uuid` VARCHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  `xsd` CHAR(36) NOT NULL ,
  PRIMARY KEY (`uuid`)
);
