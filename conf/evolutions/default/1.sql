# --- !Ups
CREATE TABLE houses (
    id int(10) NOT NULL UNIQUE AUTO_INCREMENT,
    device_id char(32) NOT NULL,
    family_name varchar(128) NOT NULL,
    district varchar(128) NOT NULL,
    street varchar(128) NOT NULL,
    building_name varchar(128) NOT NULL,
    door_number varchar(16) NOT NULL,
    postal_code varchar(16) NOT NULL,
    town varchar(64) NOT NULL,
    city varchar(64) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE people (
    id int(10) NOT NULL UNIQUE AUTO_INCREMENT,
    name varchar(128) NOT NULL,
    birth_date date NOT NULL,
    is_male tinyint(1) NOT NULL,
    house_id int(10) NOT NULL,
    button_number tinyint(4) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE channels (
    id int(10) NOT NULL UNIQUE AUTO_INCREMENT,
    name varchar(128) NOT NULL,
    logo_position tinyint(4) NOT NULL,
    PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE records (
    id int(10) NOT NULL UNIQUE AUTO_INCREMENT,
    house_id int(10) NOT NULL,
    button_number tinyint(4) NOT NULL,
    channel_id int(10) NOT NULL,
    start_time datetime NOT NULL,
    end_time datetime NOT NULL,
    PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE accounts (
    id int(10) NOT NULL UNIQUE AUTO_INCREMENT,
    email varchar(128) NOT NULL,
    password char(128) NOT NULL,
    PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE sessions (
    id char(32) NOT NULL UNIQUE,
    account_id int(10) NOT NULL,
    PRIMARY KEY(id)
) ENGINE=MEMORY DEFAULT CHARSET=utf8;

CREATE VIEW channel_stats_view AS (
    SELECT records.id AS id,
           channels.name AS name,
           (records.end_time - records.start_time) AS duration
    FROM channels, records
    WHERE channels.id = records.channel_id
);

# --- !Downs
DROP TABLE houses;
DROP TABLE people;
DROP TABLE channels;
DROP TABLE records;
DROP TABLE accounts;
DROP TABLE sessions;
DROP VIEW channel_stats_view;
