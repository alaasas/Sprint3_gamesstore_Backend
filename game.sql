DROP DATABASE IF EXISTS game;
CREATE DATABASE game;
USE game;

DROP TABLE IF EXISTS games;
DROP TABLE IF EXISTS users;

CREATE TABLE games (
    ID int NOT NULL auto_increment,
    name VARCHAR(50),
    platform VARCHAR(50),
    description VARCHAR(2000),
    PRIMARY KEY(ID)
);

CREATE TABLE users (
    fullname VARCHAR(50),
    password VARCHAR(50),
    emailid VARCHAR(100),
    PRIMARY KEY(emailid)
);
