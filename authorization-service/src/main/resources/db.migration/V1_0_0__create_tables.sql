create table if not exists users
(
    id       bigint not null primary key,
    password varchar(255) not null,
    role     varchar(255)not null default 'USER',
    username varchar(255) unique not null
    );

create sequence user_sequence
    start with 2
    increment by 50;
