
-- -----------------------------------------------------
-- Table `Accounts`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Accounts` (
  `AccountID` INT NOT NULL AUTO_INCREMENT,
  `AccountUsername` VARCHAR(45) NOT NULL,
  `AccountName` VARCHAR(45) NOT NULL,
  `AccountDOB` DATE NOT NULL,
  `AccountCreation` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `AccountActive` TINYINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`AccountID`, `AccountUsername`),
  UNIQUE INDEX `AccountID_UNIQUE` (`AccountID` ASC),
  UNIQUE INDEX `AccountUsername_UNIQUE` (`AccountUsername` ASC) )
;

-- -----------------------------------------------------
-- Table `Chats`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Chats` (
  `ChatID` INT NOT NULL,
  `ChatCreation` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ChatID`),
  UNIQUE INDEX `ChatID_UNIQUE` (`ChatID` ASC) )
;


-- -----------------------------------------------------
-- Table `Messages`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Messages` (
  `MessageID` INT NOT NULL AUTO_INCREMENT,
  `ChatID` INT NOT NULL,
  `AccountID` INT NOT NULL,
  `MessageTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `MessageText` VARCHAR(1000) NOT NULL,
  PRIMARY KEY (`MessageID`, `ChatID`),
  INDEX `fk_Messages_Chat1_idx` (`ChatID` ASC) ,
  INDEX `fk_Messages_Accounts1_idx` (`AccountID` ASC) ,
  CONSTRAINT `fk_Messages_Chat1`
    FOREIGN KEY (`ChatID`)
    REFERENCES `Chats` (`ChatID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Messages_Accounts1`
    FOREIGN KEY (`AccountID`)
    REFERENCES `Accounts` (`AccountID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
;


-- -----------------------------------------------------
-- Table `Tasks`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Tasks` (
  `TaskID` INT NOT NULL,
  `AccountID` INT NOT NULL,
  `TaskName` VARCHAR(45) NOT NULL,
  `TaskDeadline` DATETIME NULL,
  `TaskPriority` INT NULL,
  `TaskComplete` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`TaskID`, `AccountID`),
  INDEX `fk_Tasks_Accounts1_idx` (`AccountID` ASC) ,
  CONSTRAINT `fk_Tasks_Accounts1`
    FOREIGN KEY (`AccountID`)
    REFERENCES `Accounts` (`AccountID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
;


-- -----------------------------------------------------
-- Table `TaskNotes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `TaskNotes` (
  `TaskID` INT NOT NULL,
  `TaskNoteID` INT NOT NULL,
  `Note` VARCHAR(1000) NULL,
  PRIMARY KEY (`TaskID`, `TaskNoteID`),
  CONSTRAINT `fk_Notes_Tasks1`
    FOREIGN KEY (`TaskID`)
    REFERENCES `Tasks` (`TaskID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
;


-- -----------------------------------------------------
-- Table `Meetings`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Meetings` (
  `MeetingID` INT NOT NULL,
  `MeetingName` VARCHAR(45) NOT NULL,
  `MeetingDescription` VARCHAR(255) NULL,
  `MeetingCreatorID` INT NOT NULL,
  `MeetingStart` DATETIME NOT NULL,
  `MeetingEnd` DATETIME NOT NULL,
  `MeetingCreation` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE INDEX `EventID_UNIQUE` (`MeetingID` ASC) ,
  PRIMARY KEY (`MeetingID`),
  INDEX `fk_Meetings_Accounts1_idx` (`MeetingCreatorID` ASC) ,
  CONSTRAINT `fk_Meetings_Accounts1`
    FOREIGN KEY (`MeetingCreatorID`)
    REFERENCES `Accounts` (`AccountID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
;


-- -----------------------------------------------------
-- Table `AccountLoginDetails`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `AccountLoginDetails` (
  `AccountID` INT NOT NULL,
  `AccountUsername` VARCHAR(45) NOT NULL,
  `AccountPassword` VARCHAR(60) NOT NULL,
  INDEX `fk_AccountLogin_Accounts_idx` (`AccountID` ASC, `AccountUsername` ASC) ,
  PRIMARY KEY (`AccountID`),
  UNIQUE INDEX `AccountID_UNIQUE_ACCOUNTLOGINDETAILS` (`AccountID` ASC) ,
  UNIQUE INDEX `AccountUsername_UNIQUE_ACCOUNTLOGINDETAILS` (`AccountUsername` ASC) ,
  CONSTRAINT `fk_AccountLogin_Accounts`
    FOREIGN KEY (`AccountID`)
    REFERENCES `Accounts` (`AccountID`),
    FOREIGN KEY (`AccountUsername`)
    REFERENCES `Accounts` (`AccountUsername`))
;


-- -----------------------------------------------------
-- Table `Account_Contacts`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Account_Contacts` (
  `AccountID` INT NOT NULL,
  `ContactID` INT NOT NULL,
  `ContactEmail` VARCHAR(255),
  `ContactAddress` VARCHAR(255),
  `ContactPhone` VARCHAR(20),
  `ContactCompany` VARCHAR(255),
  `ContactJobTitle` VARCHAR(255),
  `ContactCreatedOn` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`AccountID`, `ContactID`),
  INDEX `fk_Account_Contacts_Accounts2_idx` (`ContactID` ASC) ,
  CONSTRAINT `fk_Account_Contacts_Accounts1`
    FOREIGN KEY (`AccountID`)
    REFERENCES `Accounts` (`AccountID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Account_Contacts_Accounts2`
    FOREIGN KEY (`ContactID`)
    REFERENCES `Accounts` (`AccountID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
;


-- -----------------------------------------------------
-- Table `Accounts_Meetings`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Accounts_Meetings` (
  `AccountID` INT NOT NULL,
  `MeetingID` INT NOT NULL,
  `Accounts_MeetingsAccepted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`AccountID`, `MeetingID`),
  INDEX `fk_Accounts_has_Event_Event1_idx` (`MeetingID` ASC) ,
  INDEX `fk_Accounts_has_Event_Accounts1_idx` (`AccountID` ASC) ,
  CONSTRAINT `fk_Accounts_has_Event_Accounts1`
    FOREIGN KEY (`AccountID`)
    REFERENCES `Accounts` (`AccountID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Accounts_has_Event_Event1`
    FOREIGN KEY (`MeetingID`)
    REFERENCES `Meetings` (`MeetingID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
;


-- -----------------------------------------------------
-- Table `PublicHolidays`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PublicHolidays` (
  `PublicHolidayID` INT NOT NULL,
  `PublicHolidayName` VARCHAR(45) NOT NULL,
  `PublicHolidayDesc` VARCHAR(100) NULL,
  `PublicHolidayStart` DATETIME NOT NULL,
  `PublicHolidayEnd` DATETIME NOT NULL,
  PRIMARY KEY (`PublicHolidayID`),
  UNIQUE INDEX `PublicHolidayID_UNIQUE` (`PublicHolidayID` ASC) )
;


-- -----------------------------------------------------
-- Table `Accounts_Chats`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Accounts_Chats` (
  `AccountID` INT NOT NULL,
  `ChatID` INT NOT NULL,
  `Account_ChatActive` TINYINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`AccountID`, `ChatID`),
  INDEX `fk_Chat_has_Accounts_Accounts1_idx` (`AccountID` ASC) ,
  INDEX `fk_Chat_has_Accounts_Chat1_idx` (`ChatID` ASC) ,
  CONSTRAINT `fk_Chat_has_Accounts_Chat1`
    FOREIGN KEY (`ChatID`)
    REFERENCES `Chats` (`ChatID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Chat_has_Accounts_Accounts1`
    FOREIGN KEY (`AccountID`)
    REFERENCES `Accounts` (`AccountID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
;


-- -----------------------------------------------------
-- Table `Account_Contacts_Tasks`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Account_Contacts_Tasks` (
  `TaskID` INT NOT NULL,
  `ContactID` INT NOT NULL,
  PRIMARY KEY (`TaskID`, `ContactID`),
  INDEX `fk_Account_Contacts_has_Tasks_Tasks1_idx` (`TaskID` ASC) ,
  INDEX `fk_Account_Contacts_has_Tasks_Account_Contacts1_idx` (`ContactID` ASC) ,
  CONSTRAINT `fk_Account_Contacts_has_Tasks_Account_Contacts1`
    FOREIGN KEY (`ContactID`)
    REFERENCES `Account_Contacts` (`ContactID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Account_Contacts_has_Tasks_Tasks1`
    FOREIGN KEY (`TaskID`)
    REFERENCES `Tasks` (`TaskID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
;

-- -----------------------------------------------------
-- Table `personalCrmDB`.`Minutes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Minutes` (
  `MinuteID` INT NOT NULL AUTO_INCREMENT,
  `MeetingID` INT NOT NULL,
  `AccountID` INT NOT NULL,
  `MinuteText` VARCHAR(255) NOT NULL,
  `MinuteCreation` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`MinuteID`, `MeetingID`),
  INDEX `fk_Minute_Meetings1_idx` (`MeetingID` ASC),
  INDEX `fk_Minutes_Accounts1_idx` (`AccountID` ASC),
  CONSTRAINT `fk_Minute_Meetings1`
    FOREIGN KEY (`MeetingID`)
    REFERENCES `Meetings` (`MeetingID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Minutes_Accounts1`
    FOREIGN KEY (`AccountID`)
    REFERENCES `Accounts` (`AccountID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;


