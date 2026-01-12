-- database definition
-- update "career_user" to a user you have created
create database careercrack
    with owner career_user;

CREATE SCHEMA IF NOT EXISTS careercrack AUTHORIZATION career_user;

SET search_path TO careercrack;

-- users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,

    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(50) NOT NULL,
    username   VARCHAR(50) NOT NULL UNIQUE,
    email      VARCHAR(255) NOT NULL UNIQUE,

    hashed_password VARCHAR(255) NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- problems section
CREATE TABLE problems_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULl UNIQUE,
    description TEXT
);

INSERT INTO problems_categories (name, description) VALUES
('LeetCode', 'Data structures and algorithms problems'),
('Behavioral', 'Behavioral interview questions'),
('System Design', 'System design interview prep');