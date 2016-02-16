DROP DATABASE IF EXISTS kouponify;

create database kouponify;

DROP TABLE IF EXISTS kouponify.user;

CREATE TABLE
kouponify.user (
user_id bigint(20) NOT NULL AUTO_INCREMENT,
username varchar(55) NOT NULL,
password varchar(50) NOT NULL,
email  varchar(100)  NULL,
fname varchar(156)  NULL,
lname varchar(156)  NULL,
shop varchar(156)  NULL,
created_by bigint(20) NOT NULL,
updated_by bigint(20) NOT NULL,
created_at bigint(20) DEFAULT NULL,
updated_at bigint(20) DEFAULT NULL,
active tinyint(1) DEFAULT '0',
PRIMARY KEY (user_id)
) ENGINE=InnoDB;
        

DROP TABLE IF EXISTS kouponify.coupon;

CREATE TABLE
kouponify.coupon (
coupon_id bigint(20) NOT NULL AUTO_INCREMENT,
code varchar(126) DEFAULT NULL,
discount_type varchar(50) DEFAULT NULL,
applies_to_resource  varchar(55) DEFAULT NULL,
value double DEFAULT NULL,
starts_at bigint(20) NOT NULL,
ends_at bigint(20) NULL,
minimum_order_amount double DEFAULT NULL,
applies_once tinyint(1) DEFAULT '0',
created_by bigint(20) NOT NULL,
updated_by bigint(20) NOT NULL,
created_at bigint(20) DEFAULT NULL,
updated_at bigint(20) DEFAULT NULL,
active tinyint(1) DEFAULT '0',
PRIMARY KEY (coupon_id)
) ENGINE=InnoDB;


INSERT into kouponify.user values 
(1,'jen','jen','jen@bizzy.io','Jennifer', 'Kessler', 'BIZZY',1, 1, 1455576538,1455576538,1),
(2,'paul','paul','paul@bizzy.io','Paul', 'Booth', 'BIZZY',1, 1, 1455576538,1455576538,1);
