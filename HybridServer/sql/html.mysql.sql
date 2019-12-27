CREATE DATABASE IF NOT EXISTS `hstestdb`;
USE `hstestdb`;

DROP TABLE IF EXISTS `HTML`;
CREATE  TABLE `hstestdb`.`HTML` (
  `uuid` CHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  PRIMARY KEY (`uuid`)
);

DROP TABLE IF EXISTS `XSD`;
CREATE  TABLE `hstestdb`.`XSD` (
  `uuid` CHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  PRIMARY KEY (`uuid`)
);

DROP TABLE IF EXISTS `XML`;
CREATE  TABLE `hstestdb`.`XML` (
  `uuid` CHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  PRIMARY KEY (`uuid`)
);

DROP TABLE IF EXISTS `XSLT`;
CREATE  TABLE `hstestdb`.`XSLT` (
  `uuid` CHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  `xsd` CHAR(36) NOT NULL ,
  PRIMARY KEY (`uuid`)
);

CREATE USER 'hsdb'@'localhost' IDENTIFIED BY 'hsdbpass';
GRANT ALL PRIVILEGES ON hstestdb.* TO 'hsdb'@'localhost';
