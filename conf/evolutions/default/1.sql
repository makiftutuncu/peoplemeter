# --- !Ups
CREATE TABLE user_contact_infos (
    id int unsigned NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
    phone varchar(200) NOT NULL,
    email varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE family_member_types (
    id int unsigned NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
    name varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE genders (
    id int unsigned NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
    name varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE education_statuses (
    id int unsigned NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
    name varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE questions (
    id int unsigned NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
    question varchar(200) NOT NULL,
    answer varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE locations (
    id int unsigned NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
    name varchar(200) NOT NULL,
    street varchar(200) NOT NULL,
    building_name varchar(200) NOT NULL,
    door_number int NOT NULL,
    postal_code varchar(200) NOT NULL,
    town varchar(200) NOT NULL,
    district_name varchar(200) NOT NULL,
    province varchar(200) NOT NULL,
    country varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE houses (
    id int unsigned NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE families (
    id int unsigned NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
    name varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE accounts (
    id int unsigned NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
    email varchar(200) UNIQUE NOT NULL,
    password varchar(200) NOT NULL,
    name varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE records (
    id int unsigned NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
    start_time date NOT NULL,
    end_time date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE channels (
    id int unsigned NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
    name varchar(200) NOT NULL,
    logo varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE surveys (
    id int unsigned NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
    name varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE users (
    id int unsigned NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
    birth_date date NOT NULL,
    button_number int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE sessions (
    id char(32) NOT NULL PRIMARY KEY,
    user_id int unsigned NOT NULL UNIQUE
) ENGINE=MEMORY DEFAULT CHARSET=utf8;

# --- !Downs
DROP TABLE user_contact_infos;
DROP TABLE family_member_types;
DROP TABLE genders;
DROP TABLE education_statuses;
DROP TABLE questions;
DROP TABLE locations;
DROP TABLE houses;
DROP TABLE families;
DROP TABLE accounts;
DROP TABLE records;
DROP TABLE channels;
DROP TABLE surveys;
DROP TABLE users;
DROP TABLE sessions;