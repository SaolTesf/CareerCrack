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
CREATE TABLE problem_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULl UNIQUE,
    description TEXT
);

INSERT INTO problem_categories (name, description) VALUES
('LeetCode', 'Data structures and algorithms problems'),
('Behavioral', 'Behavioral interview questions'),
('System Design', 'System design interview prep');

CREATE TABLE problems (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGSERIAL NOT NULL,
    category_id BIGSERIAL NOT NULL,

    title VARCHAR(255) NOT NULL,
    external_link TEXT,

    -- peaceful, easy, medium, hard, hardcore
    difficulty VARCHAR(10),

    -- TODO, ATTEMPTED, SOLVED, REVIEW, MASTERED
    status VARCHAR(20) NOT NULL,

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
        REFERENCES problem_categories(id)
        ON DELETE CASCADE
);

INSERT INTO careercrack.problems (user_id, category_id, title, external_link, difficulty, status, description, solution) VALUES
(1, 1, 'Two Sum', 'https://leetcode.com/problems/two-sum/', 'easy', 'SOLVED', 'Given an array of integers, return indices of the two numbers that add up to a target.', 'Use hash map for O(n) solution'),
(1, 1, 'Merge Two Sorted Lists', 'https://leetcode.com/problems/merge-two-sorted-lists/', 'easy', 'REVIEW', 'Merge two sorted linked lists.', 'Use two pointers'),
(1, 2, 'Tell me about yourself', NULL, 'medium', 'TODO', 'Common behavioral question', NULL),
(1, 3, 'Design a URL Shortener', NULL, 'hard', 'ATTEMPTED', 'Design a scalable URL shortening service', 'Use base62 encoding and distributed cache'),
(1, 1, 'Valid Parentheses', 'https://leetcode.com/problems/valid-parentheses/', 'easy', 'MASTERED', 'Check if parentheses are balanced', 'Use stack data structure');


-- allows quick querying on these columns
CREATE INDEX idx_problems_user_id ON problems(user_id);
CREATE INDEX idx_problem_category_id ON problems(category_id);
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