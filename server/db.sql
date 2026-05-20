-- database definition
-- update "career_user" to a user you have created
create database careercrack
    with owner career_user;

CREATE SCHEMA IF NOT EXISTS careercrack AUTHORIZATION career_user;

SET search_path TO careercrack;

-- users table
CREATE TABLE careercrack.users (
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
CREATE TABLE careercrack.problem_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULl UNIQUE,
    description TEXT
);

INSERT INTO careercrack.problem_categories (name, description) VALUES
    ('LEETCODE', 'Data structures and algorithms problems'),
    ('BEHAVIORAL', 'Behavioral interview questions'),
    ('SYSTEM_DESIGN', 'System design interview prep');

CREATE TABLE careercrack.problems (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGSERIAL NOT NULL,
    category_id BIGSERIAL NOT NULL,

    title VARCHAR(255) NOT NULL,
    external_link TEXT,

    -- easy, medium, hard, hardcore
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
CREATE INDEX idx_problems_user_id ON careercrack.problems(user_id);
CREATE INDEX idx_problem_category_id ON careercrack.problems(category_id);
CREATE INDEX idx_problems_status ON careercrack.problems(status);

CREATE TABLE careercrack.problem_resources (
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

-- Sample data for problem_resources
INSERT INTO careercrack.problem_resources (problem_id, resource_type, url, description) VALUES
    (1, 'video', 'https://youtube.com/example1', 'NeetCode solution explanation'),
    (1, 'article', 'https://medium.com/example1', 'Alternative approach'),
    (4, 'video', 'https://youtube.com/example2', 'System design walkthrough'),
    (5, 'article', 'https://leetcode.com/discuss/example', 'Official solution');


CREATE TABLE careercrack.tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO careercrack.tags (name) VALUES
    ('Array'),
    ('String'),
    ('Hash Table'),
    ('Dynamic Programming'),
    ('Math'),
    ('Sorting'),
    ('Greedy'),
    ('Depth-First Search'),
    ('Binary Search'),
    ('Tree'),
    ('Graph'),
    ('Backtracking'),
    ('Stack'),
    ('Queue'),
    ('Linked List'),
    ('Two Pointers'),
    ('Sliding Window');

CREATE TABLE careercrack.problem_tags (
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
);


-- Sample data for problem_tags (linking problems with tags)
INSERT INTO careercrack.problem_tags (problem_id, tag_id) VALUES
    (1, 1),  -- Two Sum: Array
    (1, 3),  -- Two Sum: Hash Table
    (2, 15), -- Merge Lists: Linked List
    (3, 2),  -- Tell me about yourself: String (behavioral tag could be added)
    (5, 13); -- Valid Parentheses: Stack
