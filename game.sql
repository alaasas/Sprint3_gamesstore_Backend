DROP DATABASE IF EXISTS game;
CREATE DATABASE game;
USE game;

DROP TABLE IF EXISTS users;

CREATE TABLE users (
    fullname VARCHAR(50),
    password VARCHAR(50),
    emailid VARCHAR(100),
    PRIMARY KEY(emailid)
);
