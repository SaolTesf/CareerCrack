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

CREATE TABLE problems (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGSERIAL NOT NULL,
    category_id BIGSERIAL NOT NULL,

    title VARCHAR(255) NOT NULL,
    external_link TEXT,
    difficulty VARCHAR(5),
    status VARCHAR(20),

    description TEXT,
    solution TEXT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_problem_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT  fk_problem_category
        FOREIGN KEY (category_id)
        REFERENCES problems_categories(id)
        ON DELETE CASCADE
);

-- allows quick querying on these columns
CREATE INDEX idx_problems_user_id ON problems(user_id);
CREATE INDEX idx_problems_category_id ON problems(category_id);
CREATE INDEX idx_problems_status ON problems(status);

CREATE TABLE problem_resources (
    id BIGSERIAL PRIMARY KEY,

    problem_id BIGINT NOT NULL,
    resource_type VARCHAR(50),
    url TEXT NOT NULL,
    description TEXT,

    CONSTRAINT fk_resource_problem
        FOREIGN KEY (problem_id)
        REFERENCES problems(id)
        ON DELETE CASCADE
);

CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE problem_tags (
    problem_id BIGSERIAL,
    tag_id BIGSERIAL,

    PRIMARY KEY(problem_id, tag_id),

    CONSTRAINT fk_pt_problem
        FOREIGN KEY  (problem_id)
        REFERENCES problems(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_pt_tag
        FOREIGN KEY (tag_id)
        REFERENCES tags(id)
        ON DELETE CASCADE
)