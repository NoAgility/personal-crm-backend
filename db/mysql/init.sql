-- MySQL Script generated by MySQL Workbench
-- Sat Sep  4 16:15:01 2021
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema personalCrmDB
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema personalCrmDB
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `personalCrmDB` DEFAULT CHARACTER SET utf8 ;
USE `personalCrmDB` ;

-- -----------------------------------------------------
-- Table `personalCrmDB`.`Accounts`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `personalCrmDB`.`Accounts` (
  `AccountID` INT NOT NULL AUTO_INCREMENT,
  `AccountUsername` VARCHAR(45) NOT NULL,
  `AccountName` VARCHAR(45) NOT NULL,
  `AccountDOB` DATE NOT NULL,
  `AccountCreation` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `AccountActive` TINYINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`AccountID`, `AccountUsername`),
  UNIQUE INDEX `AccountID_UNIQUE` (`AccountID` ASC) VISIBLE,
  UNIQUE INDEX `AccountUsername_UNIQUE` (`AccountUsername` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `personalCrmDB`.`Chats`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `personalCrmDB`.`Chats` (
  `ChatID` INT NOT NULL,
  `ChatCreation` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ChatID`),
  UNIQUE INDEX `ChatID_UNIQUE` (`ChatID` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `personalCrmDB`.`Messages`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `personalCrmDB`.`Messages` (
  `MessageID` INT NOT NULL,
  `ChatID` INT NOT NULL,
  `AccountID` INT NOT NULL,
  `MessageTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `MessageText` VARCHAR(1000) NOT NULL,
  PRIMARY KEY (`MessageID`, `ChatID`),
  INDEX `fk_Messages_Chat1_idx` (`ChatID` ASC) VISIBLE,
  INDEX `fk_Messages_Accounts1_idx` (`AccountID` ASC) VISIBLE,
  CONSTRAINT `fk_Messages_Chat1`
    FOREIGN KEY (`ChatID`)
    REFERENCES `personalCrmDB`.`Chats` (`ChatID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Messages_Accounts1`
    FOREIGN KEY (`AccountID`)
    REFERENCES `personalCrmDB`.`Accounts` (`AccountID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `personalCrmDB`.`Tasks`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `personalCrmDB`.`Tasks` (
  `TaskID` INT NOT NULL,
  `AccountID` INT NOT NULL,
  `TaskName` VARCHAR(45) NOT NULL,
  `TaskDeadline` DATETIME NULL,
  `TaskPriority` INT NULL,
  PRIMARY KEY (`TaskID`, `AccountID`),
  INDEX `fk_Tasks_Accounts1_idx` (`AccountID` ASC) VISIBLE,
  CONSTRAINT `fk_Tasks_Accounts1`
    FOREIGN KEY (`AccountID`)
    REFERENCES `personalCrmDB`.`Accounts` (`AccountID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `personalCrmDB`.`TaskNotes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `personalCrmDB`.`TaskNotes` (
  `TaskID` INT NOT NULL,
  `TaskNoteID` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`TaskID`, `TaskNoteID`),
  CONSTRAINT `fk_Notes_Tasks1`
    FOREIGN KEY (`TaskID`)
    REFERENCES `personalCrmDB`.`Tasks` (`TaskID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `personalCrmDB`.`Meetings`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `personalCrmDB`.`Meetings` (
  `MeetingID` INT NOT NULL,
  `MeetingName` VARCHAR(45) NOT NULL,
  `MeetingDescription` VARCHAR(100) NULL,
  `MeetingCreatorID` INT NOT NULL,
  `MeetingStart` DATETIME NOT NULL,
  `MeetingEnd` DATETIME NOT NULL,
  UNIQUE INDEX `EventID_UNIQUE` (`MeetingID` ASC) VISIBLE,
  PRIMARY KEY (`MeetingID`),
  INDEX `fk_Meetings_Accounts1_idx` (`MeetingCreatorID` ASC) VISIBLE,
  CONSTRAINT `fk_Meetings_Accounts1`
    FOREIGN KEY (`MeetingCreatorID`)
    REFERENCES `personalCrmDB`.`Accounts` (`AccountID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `personalCrmDB`.`AccountLoginDetails`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `personalCrmDB`.`AccountLoginDetails` (
  `AccountID` INT NOT NULL,
  `AccountUsername` VARCHAR(45) NOT NULL,
  `AccountPassword` VARCHAR(45) NOT NULL,
  INDEX `fk_AccountLogin_Accounts_idx` (`AccountID` ASC, `AccountUsername` ASC) VISIBLE,
  PRIMARY KEY (`AccountID`),
  UNIQUE INDEX `AccountID_UNIQUE` (`AccountID` ASC) VISIBLE,
  UNIQUE INDEX `AccountUsername_UNIQUE` (`AccountUsername` ASC) VISIBLE,
  CONSTRAINT `fk_AccountLogin_Accounts`
    FOREIGN KEY (`AccountID` , `AccountUsername`)
    REFERENCES `personalCrmDB`.`Accounts` (`AccountID` , `AccountUsername`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `personalCrmDB`.`Account_Contacts`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `personalCrmDB`.`Account_Contacts` (
  `AccountID` INT NOT NULL,
  `ContactID` INT NOT NULL,
  `ContactCreatedOn` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`AccountID`, `ContactID`),
  INDEX `fk_Account_Contacts_Accounts2_idx` (`ContactID` ASC) VISIBLE,
  CONSTRAINT `fk_Account_Contacts_Accounts1`
    FOREIGN KEY (`AccountID`)
    REFERENCES `personalCrmDB`.`Accounts` (`AccountID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Account_Contacts_Accounts2`
    FOREIGN KEY (`ContactID`)
    REFERENCES `personalCrmDB`.`Accounts` (`AccountID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `personalCrmDB`.`Account_Meetings`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `personalCrmDB`.`Account_Meetings` (
  `AccountID` INT NOT NULL,
  `MeetingID` INT NOT NULL,
  PRIMARY KEY (`AccountID`, `MeetingID`),
  INDEX `fk_Accounts_has_Event_Event1_idx` (`MeetingID` ASC) VISIBLE,
  INDEX `fk_Accounts_has_Event_Accounts1_idx` (`AccountID` ASC) VISIBLE,
  CONSTRAINT `fk_Accounts_has_Event_Accounts1`
    FOREIGN KEY (`AccountID`)
    REFERENCES `personalCrmDB`.`Accounts` (`AccountID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Accounts_has_Event_Event1`
    FOREIGN KEY (`MeetingID`)
    REFERENCES `personalCrmDB`.`Meetings` (`MeetingID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `personalCrmDB`.`PublicHolidays`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `personalCrmDB`.`PublicHolidays` (
  `PublicHolidayID` INT NOT NULL,
  `PublicHolidayName` VARCHAR(45) NOT NULL,
  `PublicHolidayDesc` VARCHAR(100) NULL,
  `PublicHolidayStart` DATETIME NOT NULL,
  `PublicHolidayEnd` DATETIME NOT NULL,
  PRIMARY KEY (`PublicHolidayID`),
  UNIQUE INDEX `PublicHolidayID_UNIQUE` (`PublicHolidayID` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `personalCrmDB`.`Accounts_Chats`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `personalCrmDB`.`Accounts_Chats` (
  `AccountID` INT NOT NULL,
  `ChatID` INT NOT NULL,
  PRIMARY KEY (`AccountID`, `ChatID`),
  INDEX `fk_Chat_has_Accounts_Accounts1_idx` (`AccountID` ASC) VISIBLE,
  INDEX `fk_Chat_has_Accounts_Chat1_idx` (`ChatID` ASC) VISIBLE,
  CONSTRAINT `fk_Chat_has_Accounts_Chat1`
    FOREIGN KEY (`ChatID`)
    REFERENCES `personalCrmDB`.`Chats` (`ChatID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Chat_has_Accounts_Accounts1`
    FOREIGN KEY (`AccountID`)
    REFERENCES `personalCrmDB`.`Accounts` (`AccountID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `personalCrmDB`.`Account_Contacts_Tasks`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `personalCrmDB`.`Account_Contacts_Tasks` (
  `TaskID` INT NOT NULL,
  `ContactID` INT NOT NULL,
  PRIMARY KEY (`TaskID`, `ContactID`),
  INDEX `fk_Account_Contacts_has_Tasks_Tasks1_idx` (`TaskID` ASC) VISIBLE,
  INDEX `fk_Account_Contacts_has_Tasks_Account_Contacts1_idx` (`ContactID` ASC) VISIBLE,
  CONSTRAINT `fk_Account_Contacts_has_Tasks_Account_Contacts1`
    FOREIGN KEY (`ContactID`)
    REFERENCES `personalCrmDB`.`Account_Contacts` (`ContactID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Account_Contacts_has_Tasks_Tasks1`
    FOREIGN KEY (`TaskID`)
    REFERENCES `personalCrmDB`.`Tasks` (`TaskID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
