CREATE DATABASE IF NOT EXISTS personalCrmDB;

USE personalCrmDB;

CREATE TABLE test (
id int NOT NULL AUTO_INCREMENT,
username varchar(10),
PRIMARY KEY(id)
);

INSERT INTO test (username)
VALUES ("Apple");