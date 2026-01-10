-- database definition
-- update "career_user" to a user you have created
create database careercrack
    with owner career_user;

--users table
create sequence user_info.users_new_id_seq
    as integer;

alter sequence user_info.users_new_id_seq owner to career_user;

create sequence users_new_id_seq
    as integer;

alter sequence users_new_id_seq owner to career_user;

create table users
(
    id              bigint    default nextval('user_info.users_new_id_seq'::regclass) not null
        constraint users_new_pkey
            primary key,
    first_name      varchar(50)                                                       not null,
    last_name       varchar(50)                                                       not null,
    username        varchar(50)                                                       not null,
    email           varchar(255),
    hashed_password varchar(255)                                                      not null,
    created_at      timestamp default CURRENT_TIMESTAMP,
    updated_at      timestamp default CURRENT_TIMESTAMP
);

alter table users
    owner to career_user;

alter sequence users_new_id_seq owned by users.id;