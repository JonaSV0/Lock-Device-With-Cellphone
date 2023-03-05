CREATE TABLE user_l (
	id int AUTO_INCREMENT,
    mail varchar(100),
    pass varchar(100),
    name varchar(100),
    surname varchar(100),
    dni varchar(8),
    CONSTRAINT pk_user_l PRIMARY KEY (id)
);

CREATE TABLE lock_l (
	id int AUTO_INCREMENT,
    unicode varchar(10),
    stat tinyint(1),
    nickname varchar(100),
    stat_lock tinyint(1),
    pin int(3),
    CONSTRAINT pk_lock_l PRIMARY KEY (id)
);

CREATE TABLE user_lock (
    id int AUTO_INCREMENT,
    id_user int,
    id_lock int,
    type_rel int(2),
    CONSTRAINT pk_user_lock PRIMARY KEY (id),
    CONSTRAINT fk_user_userlock FOREIGN KEY (id_user) REFERENCES user_l(id),
    CONSTRAINT fk_lock_userlock FOREIGN KEY (id_lock) REFERENCES lock_l(id)
);

CREATE TABLE device_ble(
    id int AUTO_INCREMENT,
    name_b varchar(100),
    mac_b varchar(100),
    CONSTRAINT pk_device_ble PRIMARY KEY (id)
);

CREATE TABLE user_lock_ble(
    id int AUTO_INCREMENT,
    id_user int,
    id_lock int,
    id_ble int,
    type_rel int(2),
    CONSTRAINT pk_user_lock_ble PRIMARY KEY (id),
    CONSTRAINT fk_user_userlockble FOREIGN KEY (id_user) REFERENCES user_l(id),
    CONSTRAINT fk_lock_userlockble FOREIGN KEY (id_lock) REFERENCES lock_l(id),
    CONSTRAINT fk_deviceble_userlockble FOREIGN KEY (id_ble) REFERENCES device_ble(id)
);


CREATE TABLE history(
    id int AUTO_INCREMENT,
    id_user int,
    id_lock int,
    type_in varchar(100),
    name_bluet varchar(100),
    name_in varchar(100),
    dni_in varchar(8),
    date_in date,
    time_in time,
    CONSTRAINT pk_history PRIMARY KEY (id)
);
