use trade;

drop table IF EXISTS authorities;
drop table IF EXISTS users;
drop table if exists logon_log;
CREATE TABLE users (
    id int  primary key auto_increment,
    username VARCHAR(50) NOT NULL unique,
    password VARCHAR(500) NOT NULL,
    address varchar(500) null,
    email varchar(500) null,
    enabled BOOLEAN NOT NULL
);
CREATE TABLE authorities (
    username VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    CONSTRAINT fk_authorities_users FOREIGN KEY (username)
        REFERENCES users (username)
);
create unique index ix_auth_username on authorities(username,authority);

create table logon_log(
  id int  primary key auto_increment,
  username varchar(500) not null references users(username),
  time_stamp timestamp not null,
  ip varchar(50) not null
)