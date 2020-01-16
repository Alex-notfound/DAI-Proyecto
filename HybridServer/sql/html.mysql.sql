CREATE USER 'hsdb'@'localhost' IDENTIFIED BY 'hsdbpass';
GRANT ALL PRIVILEGES ON hstestdb.* TO 'hsdb'@'localhost';

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

CREATE DATABASE IF NOT EXISTS `hstestdb1`;
USE `hstestdb1`;

DROP TABLE IF EXISTS `HTML`;
CREATE  TABLE `hstestdb1`.`HTML` (
  `uuid` CHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  PRIMARY KEY (`uuid`)
);

DROP TABLE IF EXISTS `XSD`;
CREATE  TABLE `hstestdb1`.`XSD` (
  `uuid` CHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  PRIMARY KEY (`uuid`)
);

DROP TABLE IF EXISTS `XML`;
CREATE  TABLE `hstestdb1`.`XML` (
  `uuid` CHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  PRIMARY KEY (`uuid`)
);

DROP TABLE IF EXISTS `XSLT`;
CREATE  TABLE `hstestdb1`.`XSLT` (
  `uuid` CHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  `xsd` CHAR(36) NOT NULL ,
  PRIMARY KEY (`uuid`)
);

CREATE DATABASE IF NOT EXISTS `hstestdb2`;
USE `hstestdb2`;

DROP TABLE IF EXISTS `HTML`;
CREATE  TABLE `hstestdb2`.`HTML` (
  `uuid` CHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  PRIMARY KEY (`uuid`)
);

DROP TABLE IF EXISTS `XSD`;
CREATE  TABLE `hstestdb2`.`XSD` (
  `uuid` CHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  PRIMARY KEY (`uuid`)
);

DROP TABLE IF EXISTS `XML`;
CREATE  TABLE `hstestdb2`.`XML` (
  `uuid` CHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  PRIMARY KEY (`uuid`)
);

DROP TABLE IF EXISTS `XSLT`;
CREATE  TABLE `hstestdb2`.`XSLT` (
  `uuid` CHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  `xsd` CHAR(36) NOT NULL ,
  PRIMARY KEY (`uuid`)
);

CREATE DATABASE IF NOT EXISTS `hstestdb3`;
USE `hstestdb3`;

DROP TABLE IF EXISTS `HTML`;
CREATE  TABLE `hstestdb3`.`HTML` (
  `uuid` CHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  PRIMARY KEY (`uuid`)
);

DROP TABLE IF EXISTS `XSD`;
CREATE  TABLE `hstestdb3`.`XSD` (
  `uuid` CHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  PRIMARY KEY (`uuid`)
);

DROP TABLE IF EXISTS `XML`;
CREATE  TABLE `hstestdb3`.`XML` (
  `uuid` CHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  PRIMARY KEY (`uuid`)
);

DROP TABLE IF EXISTS `XSLT`;
CREATE  TABLE `hstestdb3`.`XSLT` (
  `uuid` CHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  `xsd` CHAR(36) NOT NULL ,
  PRIMARY KEY (`uuid`)
);

CREATE DATABASE IF NOT EXISTS `hstestdb4`;
USE `hstestdb4`;

DROP TABLE IF EXISTS `HTML`;
CREATE  TABLE `hstestdb4`.`HTML` (
  `uuid` CHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  PRIMARY KEY (`uuid`)
);

DROP TABLE IF EXISTS `XSD`;
CREATE  TABLE `hstestdb4`.`XSD` (
  `uuid` CHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  PRIMARY KEY (`uuid`)
);

DROP TABLE IF EXISTS `XML`;
CREATE  TABLE `hstestdb4`.`XML` (
  `uuid` CHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  PRIMARY KEY (`uuid`)
);

DROP TABLE IF EXISTS `XSLT`;
CREATE  TABLE `hstestdb4`.`XSLT` (
  `uuid` CHAR(36) NOT NULL ,
  `content` TEXT NOT NULL ,
  `xsd` CHAR(36) NOT NULL ,
  PRIMARY KEY (`uuid`)
);

