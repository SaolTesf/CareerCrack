CREATE USER "career_user" WITH PASSWORD 'password123';

-- database definition
-- update "career_user" to a user you have created
create database careercrack
    with owner career_user;

GRANT CONNECT, TEMPORARY ON DATABASE careercrack TO "career_user";
GRANT ALL PRIVILEGES ON DATABASE careercrack TO "career_user";

------------------------------------------------------------------------------------------------------------------------


-- ** MAKE SURE TO SWITCH THE DATABASE TO CAREERCRACK BEFORE RUNNING ANYTHING BELOW **

CREATE SCHEMA IF NOT EXISTS careercrack AUTHORIZATION career_user;
SET search_path TO careercrack;

ALTER USER "career_user" SET search_path TO careercrack;

ALTER DEFAULT PRIVILEGES IN SCHEMA careercrack GRANT ALL PRIVILEGES ON TABLES TO "career_user";

SET ROLE career_user;

-- users table
CREATE TABLE careercrack.users
(
    id              BIGSERIAL PRIMARY KEY,

    first_name      VARCHAR(50)  NOT NULL,
    last_name       VARCHAR(50)  NOT NULL,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    email           VARCHAR(255) NOT NULL UNIQUE,

    hashed_password VARCHAR(255) NOT NULL,

    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO careercrack.users(first_name, last_name, username, email, hashed_password)
VALUES ('John', 'Doe', 'JohnDoe', 'johndoe@email.com', 'someHashedPassword'),
       ('Jane', 'Smith', 'jsmith99', 'jane.smith@example.net', 'someHashedPassword');

-- problems section
CREATE TABLE careercrack.problem_categories
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

INSERT INTO careercrack.problem_categories (name, description)
VALUES ('LEETCODE', 'Data structures and algorithms problems'),
       ('BEHAVIORAL', 'Behavioral interview questions'),
       ('SYSTEM_DESIGN', 'System design interview prep');

CREATE TABLE careercrack.problems
(
    id            BIGSERIAL PRIMARY KEY,

    user_id       BIGINT       NOT NULL,
    category_id   BIGINT       NOT NULL,

    title         VARCHAR(255) NOT NULL,
    external_link TEXT,

    -- easy, medium, hard, hardcore
    difficulty    VARCHAR(10),

    -- TODO, ATTEMPTED, SOLVED, REVIEW, MASTERED
    status        VARCHAR(20)  NOT NULL,

    description   TEXT,
    solution      TEXT,

    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_problem_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_problem_category
        FOREIGN KEY (category_id)
            REFERENCES problem_categories (id)
            ON DELETE CASCADE
);

INSERT INTO careercrack.problems (user_id, category_id, title, external_link, difficulty, status, description, solution)
VALUES (1, 1, 'Two Sum', 'https://leetcode.com/problems/two-sum/', 'easy', 'SOLVED',
        'Given an array of integers, return indices of the two numbers that add up to a target.',
        'Use hash map for O(n) solution'),
       (1, 1, 'Merge Two Sorted Lists', 'https://leetcode.com/problems/merge-two-sorted-lists/', 'easy', 'REVIEW',
        'Merge two sorted linked lists.', 'Use two pointers'),
       (1, 2, 'Tell me about yourself', NULL, 'medium', 'TODO', 'Common behavioral question', NULL),
       (1, 3, 'Design a URL Shortener', NULL, 'hard', 'ATTEMPTED', 'Design a scalable URL shortening service',
        'Use base62 encoding and distributed cache'),
       (1, 1, 'Valid Parentheses', 'https://leetcode.com/problems/valid-parentheses/', 'easy', 'MASTERED',
        'Check if parentheses are balanced', 'Use stack data structure');


-- allows quick querying on these columns
CREATE INDEX idx_problems_user_id ON careercrack.problems (user_id);
CREATE INDEX idx_problem_category_id ON careercrack.problems (category_id);
CREATE INDEX idx_problems_status ON careercrack.problems (status);

CREATE TABLE careercrack.problem_resources
(
    id            BIGSERIAL PRIMARY KEY,

    problem_id    BIGINT NOT NULL,
    resource_type VARCHAR(50),
    url           TEXT   NOT NULL,
    description   TEXT,

    CONSTRAINT fk_resource_problem
        FOREIGN KEY (problem_id)
            REFERENCES problems (id)
            ON DELETE CASCADE
);

-- Sample data for problem_resources
INSERT INTO careercrack.problem_resources (problem_id, resource_type, url, description)
VALUES ((SELECT MIN(id) FROM careercrack.problems), 'video', 'https://youtube.com/example1',
        'NeetCode solution explanation'),
       ((SELECT MIN(id) FROM careercrack.problems), 'article', 'https://medium.com/example1', 'Alternative approach'),
       ((SELECT MAX(id) FROM careercrack.problems), 'video', 'https://youtube.com/example2',
        'System design walkthrough'),
       ((SELECT MAX(id) FROM careercrack.problems), 'article', 'https://leetcode.com/discuss/example',
        'Official solution');


CREATE TABLE careercrack.tags
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO careercrack.tags (name)
VALUES ('Array'),
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

CREATE TABLE careercrack.problem_tags
(
    problem_id BIGINT NOT NULL,
    tag_id     BIGINT NOT NULL,

    PRIMARY KEY (problem_id, tag_id),

    CONSTRAINT fk_pt_problem
        FOREIGN KEY (problem_id)
            REFERENCES problems (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_pt_tag
        FOREIGN KEY (tag_id)
            REFERENCES tags (id)
            ON DELETE CASCADE
);

------------------------------------------------------------------------------------------------------------------------
-- APPLICATIONS (job hunt tracker)
-- Already have the database from the section above? Run only this section as career_user, after:
--     SET search_path TO careercrack;
--     SET ROLE career_user;
------------------------------------------------------------------------------------------------------------------------

-- every CSV/Excel import is recorded so errors can be reviewed and a bad import can be undone
CREATE TABLE careercrack.import_jobs
(
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT       NOT NULL,

    file_name     VARCHAR(255) NOT NULL,
    -- CSV, XLSX
    file_format   VARCHAR(10)  NOT NULL,
    -- PENDING, COMPLETED, FAILED
    status        VARCHAR(20)  NOT NULL DEFAULT 'PENDING',

    total_rows    INTEGER      NOT NULL DEFAULT 0,
    imported_rows INTEGER      NOT NULL DEFAULT 0,
    failed_rows   INTEGER      NOT NULL DEFAULT 0,
    -- row-level problems found during the import, e.g. "row 14: missing role title"
    error_report  TEXT,

    created_at    TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    completed_at  TIMESTAMP,

    CONSTRAINT chk_import_format CHECK (file_format IN ('CSV', 'XLSX')),
    CONSTRAINT chk_import_status CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED')),

    CONSTRAINT fk_import_user
        FOREIGN KEY (user_id)
            REFERENCES careercrack.users (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_import_jobs_user_id ON careercrack.import_jobs (user_id);

-- companies are per user, so contacts and applications can point at one shared record
CREATE TABLE careercrack.companies
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL,

    name       VARCHAR(255) NOT NULL,
    website    TEXT,
    industry   VARCHAR(100),
    location   VARCHAR(255),
    notes      TEXT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_company_user
        FOREIGN KEY (user_id)
            REFERENCES careercrack.users (id)
            ON DELETE CASCADE
);

-- "Acme" and "acme" are the same company for one user
CREATE UNIQUE INDEX uq_companies_user_name ON careercrack.companies (user_id, LOWER(name));

-- recruiters, hiring managers and interviewers
CREATE TABLE careercrack.contacts
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    company_id   BIGINT       NOT NULL,

    name         VARCHAR(255) NOT NULL,
    -- free text: Recruiter, Hiring Manager, Interviewer, ...
    contact_role VARCHAR(100),
    email        VARCHAR(255),
    phone        VARCHAR(50),
    linkedin_url TEXT,
    notes        TEXT,

    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_contact_user
        FOREIGN KEY (user_id)
            REFERENCES careercrack.users (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_contact_company
        FOREIGN KEY (company_id)
            REFERENCES careercrack.companies (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_contacts_user_id ON careercrack.contacts (user_id);
CREATE INDEX idx_contacts_company_id ON careercrack.contacts (company_id);

CREATE TABLE careercrack.applications
(
    id              BIGSERIAL PRIMARY KEY,

    user_id         BIGINT       NOT NULL,
    company_id      BIGINT       NOT NULL,
    -- set when the row came from an import, so the whole import can be undone
    import_job_id   BIGINT,

    role_title      VARCHAR(255) NOT NULL,

    -- BOOKMARKED, APPLIED, INTERVIEWING, OFFER_RECEIVED, REJECTED, WITHDRAWN
    -- (must match the Java enum names exactly)
    status          VARCHAR(30)  NOT NULL DEFAULT 'BOOKMARKED',

    -- a posting can list a range; either side can be empty
    salary_min      NUMERIC(12, 2),
    salary_max      NUMERIC(12, 2),
    salary_currency VARCHAR(3)   NOT NULL DEFAULT 'USD',
    -- YEARLY, HOURLY
    salary_period   VARCHAR(10)  NOT NULL DEFAULT 'YEARLY',

    location        VARCHAR(255),
    -- ONSITE, HYBRID, REMOTE
    work_mode       VARCHAR(10),

    -- link to the job description, plus a saved copy because postings get taken down
    job_url         TEXT,
    job_description TEXT,
    posting_date    DATE,
    applied_date    DATE,
    -- where it was found: LinkedIn, referral, company site, ...
    source          VARCHAR(100),
    notes           TEXT,

    -- position inside its Kanban column (lower = higher up)
    sort_order      INTEGER      NOT NULL DEFAULT 0,

    created_at      TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_application_status
        CHECK (status IN ('BOOKMARKED', 'APPLIED', 'INTERVIEWING', 'OFFER_RECEIVED', 'REJECTED', 'WITHDRAWN')),
    CONSTRAINT chk_application_salary_period CHECK (salary_period IN ('YEARLY', 'HOURLY')),
    CONSTRAINT chk_application_work_mode CHECK (work_mode IS NULL OR work_mode IN ('ONSITE', 'HYBRID', 'REMOTE')),
    CONSTRAINT chk_application_salary_range
        CHECK (salary_min IS NULL OR salary_max IS NULL OR salary_max >= salary_min),

    CONSTRAINT fk_application_user
        FOREIGN KEY (user_id)
            REFERENCES careercrack.users (id)
            ON DELETE CASCADE,

    -- a company that still has applications cannot be deleted
    CONSTRAINT fk_application_company
        FOREIGN KEY (company_id)
            REFERENCES careercrack.companies (id)
            ON DELETE RESTRICT,

    CONSTRAINT fk_application_import
        FOREIGN KEY (import_job_id)
            REFERENCES careercrack.import_jobs (id)
            ON DELETE SET NULL
);

-- the Kanban board and status filters
CREATE INDEX idx_applications_user_status ON careercrack.applications (user_id, status);
CREATE INDEX idx_applications_company_id ON careercrack.applications (company_id);
CREATE INDEX idx_applications_import_job_id ON careercrack.applications (import_job_id);
CREATE INDEX idx_applications_user_applied_date ON careercrack.applications (user_id, applied_date);

-- one row each time an application changes status (the backend inserts it, including the first status).
-- Without this the dashboard cannot tell that a rejected application had reached interviews.
CREATE TABLE careercrack.application_status_history
(
    id             BIGSERIAL PRIMARY KEY,
    application_id BIGINT      NOT NULL,

    -- NULL on the first row, when the application is created
    from_status    VARCHAR(30),
    to_status      VARCHAR(30) NOT NULL,
    changed_at     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_history_from_status
        CHECK (from_status IS NULL OR
               from_status IN ('BOOKMARKED', 'APPLIED', 'INTERVIEWING', 'OFFER_RECEIVED', 'REJECTED', 'WITHDRAWN')),
    CONSTRAINT chk_history_to_status
        CHECK (to_status IN ('BOOKMARKED', 'APPLIED', 'INTERVIEWING', 'OFFER_RECEIVED', 'REJECTED', 'WITHDRAWN')),

    CONSTRAINT fk_history_application
        FOREIGN KEY (application_id)
            REFERENCES careercrack.applications (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_status_history_application ON careercrack.application_status_history (application_id, changed_at);

-- follow-ups, interviews, assessments and offer deadlines
CREATE TABLE careercrack.application_events
(
    id               BIGSERIAL PRIMARY KEY,
    application_id   BIGINT       NOT NULL,

    -- FOLLOW_UP, INTERVIEW, ASSESSMENT, OFFER_DEADLINE, OTHER
    event_type       VARCHAR(20)  NOT NULL,
    title            VARCHAR(255) NOT NULL,

    -- with time zone, because interviews are often scheduled in someone else's time zone (map to Instant in Java)
    event_at         TIMESTAMPTZ  NOT NULL,
    -- address or meeting link
    location         TEXT,

    -- when to nudge the user; reminder_sent_at lets a scheduled job skip ones it already sent
    remind_at        TIMESTAMPTZ,
    reminder_sent_at TIMESTAMPTZ,

    completed        BOOLEAN      NOT NULL DEFAULT FALSE,
    notes            TEXT,

    created_at       TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_event_type
        CHECK (event_type IN ('FOLLOW_UP', 'INTERVIEW', 'ASSESSMENT', 'OFFER_DEADLINE', 'OTHER')),
    CONSTRAINT chk_event_remind_before_event CHECK (remind_at IS NULL OR remind_at <= event_at),

    CONSTRAINT fk_event_application
        FOREIGN KEY (application_id)
            REFERENCES careercrack.applications (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_events_application_id ON careercrack.application_events (application_id);
-- "what is coming up" and "which reminders are due" only ever look at unfinished events
CREATE INDEX idx_events_upcoming ON careercrack.application_events (event_at) WHERE completed = FALSE;
CREATE INDEX idx_events_due_reminders ON careercrack.application_events (remind_at)
    WHERE remind_at IS NOT NULL AND reminder_sent_at IS NULL AND completed = FALSE;

-- notes after each round: what was asked, how it went, what to improve
CREATE TABLE careercrack.application_activities
(
    id              BIGSERIAL PRIMARY KEY,
    application_id  BIGINT       NOT NULL,
    -- optional link to the scheduled interview this note is about
    event_id        BIGINT,

    -- NOTE, INTERVIEW, EMAIL, CALL, OTHER
    activity_type   VARCHAR(20)  NOT NULL DEFAULT 'NOTE',
    -- e.g. "Technical round 1"
    title           VARCHAR(255) NOT NULL,
    occurred_at     TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    questions_asked TEXT,
    -- feedback and thoughts
    notes           TEXT,
    -- how the user felt it went, 1 (poorly) to 5 (great)
    self_rating     SMALLINT,

    created_at      TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_activity_type CHECK (activity_type IN ('NOTE', 'INTERVIEW', 'EMAIL', 'CALL', 'OTHER')),
    CONSTRAINT chk_activity_rating CHECK (self_rating IS NULL OR self_rating BETWEEN 1 AND 5),

    CONSTRAINT fk_activity_application
        FOREIGN KEY (application_id)
            REFERENCES careercrack.applications (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_activity_event
        FOREIGN KEY (event_id)
            REFERENCES careercrack.application_events (id)
            ON DELETE SET NULL
);

CREATE INDEX idx_activities_application ON careercrack.application_activities (application_id, occurred_at);

-- which contacts are involved in which application
CREATE TABLE careercrack.application_contacts
(
    application_id BIGINT NOT NULL,
    contact_id     BIGINT NOT NULL,

    PRIMARY KEY (application_id, contact_id),

    CONSTRAINT fk_ac_application
        FOREIGN KEY (application_id)
            REFERENCES careercrack.applications (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_ac_contact
        FOREIGN KEY (contact_id)
            REFERENCES careercrack.contacts (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_application_contacts_contact ON careercrack.application_contacts (contact_id);

-- Each row is one specific version of a resume or cover letter. The file itself lives outside the database
-- (a folder on disk or a bucket), and storage_key says where.
CREATE TABLE careercrack.documents
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT       NOT NULL,

    -- RESUME, COVER_LETTER, OTHER
    doc_type     VARCHAR(20)  NOT NULL,
    -- what the user calls it, e.g. "Backend resume"
    name         VARCHAR(255) NOT NULL,
    version      INTEGER      NOT NULL DEFAULT 1,

    file_name    VARCHAR(255) NOT NULL,
    content_type VARCHAR(100),
    file_size    BIGINT,
    storage_key  TEXT         NOT NULL,
    -- what changed in this version
    notes        TEXT,

    created_at   TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_document_type CHECK (doc_type IN ('RESUME', 'COVER_LETTER', 'OTHER')),
    CONSTRAINT chk_document_version CHECK (version >= 1),
    CONSTRAINT chk_document_size CHECK (file_size IS NULL OR file_size >= 0),
    CONSTRAINT uq_document_version UNIQUE (user_id, doc_type, name, version),

    CONSTRAINT fk_document_user
        FOREIGN KEY (user_id)
            REFERENCES careercrack.users (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_documents_user_id ON careercrack.documents (user_id);

-- the exact versions sent with each application
CREATE TABLE careercrack.application_documents
(
    application_id BIGINT NOT NULL,
    document_id    BIGINT NOT NULL,
    attached_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (application_id, document_id),

    CONSTRAINT fk_ad_application
        FOREIGN KEY (application_id)
            REFERENCES careercrack.applications (id)
            ON DELETE CASCADE,

    -- a version that was sent with an application cannot be deleted; detach it first
    CONSTRAINT fk_ad_document
        FOREIGN KEY (document_id)
            REFERENCES careercrack.documents (id)
            ON DELETE RESTRICT
);

CREATE INDEX idx_application_documents_document ON careercrack.application_documents (document_id);

-- dashboard numbers, one row per user: how many were sent, and how many got to each later stage.
-- "Reached" is read from the history table, so a rejected application still counts as having been interviewed.
CREATE VIEW careercrack.application_funnel AS
SELECT a.user_id,
       COUNT(*)                                       AS total,
       COUNT(*) FILTER (WHERE h.reached_applied)      AS applied,
       COUNT(*) FILTER (WHERE h.reached_interview)    AS interviewed,
       COUNT(*) FILTER (WHERE h.reached_offer)        AS offers,
       COUNT(*) FILTER (WHERE a.status = 'REJECTED')  AS rejected,
       COUNT(*) FILTER (WHERE a.status = 'WITHDRAWN') AS withdrawn
FROM careercrack.applications a
         CROSS JOIN LATERAL (
    SELECT COALESCE(bool_or(s.to_status IN ('APPLIED', 'INTERVIEWING', 'OFFER_RECEIVED')), FALSE) AS reached_applied,
           COALESCE(bool_or(s.to_status IN ('INTERVIEWING', 'OFFER_RECEIVED')), FALSE)            AS reached_interview,
           COALESCE(bool_or(s.to_status = 'OFFER_RECEIVED'), FALSE)                               AS reached_offer
    FROM careercrack.application_status_history s
    WHERE s.application_id = a.id
    ) h
GROUP BY a.user_id;

-- sample data for the first user (documents are left out: they need a real uploaded file)
INSERT INTO careercrack.companies (user_id, name, website, industry, location)
VALUES (1, 'Acme Corp', 'https://acme.example.com', 'Software', 'Austin, TX'),
       (1, 'Globex', 'https://globex.example.com', 'Fintech', 'Dallas, TX'),
       (1, 'Initech', 'https://initech.example.com', 'Enterprise software', 'Remote'),
       (1, 'Umbrella Labs', 'https://umbrella.example.com', 'Biotech', 'Houston, TX'),
       (1, 'Hooli', 'https://hooli.example.com', 'Consumer tech', 'Remote');

INSERT INTO careercrack.applications
(user_id, company_id, role_title, status, salary_min, salary_max, location, work_mode, job_url, posting_date,
 applied_date, source, sort_order)
VALUES (1, (SELECT id FROM careercrack.companies WHERE user_id = 1 AND name = 'Acme Corp'), 'Backend Engineer',
        'INTERVIEWING', 120000, 150000, 'Austin, TX', 'HYBRID', 'https://acme.example.com/careers/101', '2026-09-28',
        '2026-10-01', 'LinkedIn', 0),
       (1, (SELECT id FROM careercrack.companies WHERE user_id = 1 AND name = 'Globex'), 'Platform Engineer', 'APPLIED',
        130000, 160000, 'Dallas, TX', 'ONSITE', 'https://globex.example.com/jobs/platform', '2026-10-10', '2026-10-14',
        'Company site', 0),
       (1, (SELECT id FROM careercrack.companies WHERE user_id = 1 AND name = 'Initech'), 'Software Engineer II',
        'BOOKMARKED', NULL, NULL, 'Remote', 'REMOTE', 'https://initech.example.com/jobs/se2', '2026-10-20', NULL,
        'Referral', 0),
       (1, (SELECT id FROM careercrack.companies WHERE user_id = 1 AND name = 'Umbrella Labs'), 'Cloud Engineer',
        'REJECTED', 110000, 140000, 'Houston, TX', 'HYBRID', 'https://umbrella.example.com/careers/7', '2026-09-01',
        '2026-09-05', 'LinkedIn', 0),
       (1, (SELECT id FROM careercrack.companies WHERE user_id = 1 AND name = 'Hooli'), 'Site Reliability Engineer',
        'OFFER_RECEIVED', 145000, 175000, 'Remote', 'REMOTE', 'https://hooli.example.com/jobs/sre', '2026-08-25',
        '2026-08-28', 'Recruiter', 0);

-- history for the five applications above (ids 1 to 5 on a fresh database)
INSERT INTO careercrack.application_status_history (application_id, from_status, to_status, changed_at)
VALUES (1, NULL, 'BOOKMARKED', '2026-09-29 09:00'),
       (1, 'BOOKMARKED', 'APPLIED', '2026-10-01 10:00'),
       (1, 'APPLIED', 'INTERVIEWING', '2026-10-09 15:30'),
       (2, NULL, 'BOOKMARKED', '2026-10-12 08:00'),
       (2, 'BOOKMARKED', 'APPLIED', '2026-10-14 11:00'),
       (3, NULL, 'BOOKMARKED', '2026-10-20 19:00'),
       (4, NULL, 'BOOKMARKED', '2026-09-02 09:00'),
       (4, 'BOOKMARKED', 'APPLIED', '2026-09-05 09:30'),
       (4, 'APPLIED', 'INTERVIEWING', '2026-09-15 13:00'),
       (4, 'INTERVIEWING', 'REJECTED', '2026-09-30 16:45'),
       (5, NULL, 'BOOKMARKED', '2026-08-26 09:00'),
       (5, 'BOOKMARKED', 'APPLIED', '2026-08-28 12:00'),
       (5, 'APPLIED', 'INTERVIEWING', '2026-09-08 14:00'),
       (5, 'INTERVIEWING', 'OFFER_RECEIVED', '2026-10-15 17:00');

INSERT INTO careercrack.contacts (user_id, company_id, name, contact_role, email, phone, linkedin_url, notes)
VALUES (1, (SELECT id FROM careercrack.companies WHERE user_id = 1 AND name = 'Acme Corp'), 'Dana Lee', 'Recruiter',
        'dana.lee@acme.example.com', '555-0142', 'https://linkedin.com/in/dana-lee-example',
        'Prefers email. Replied within a day.'),
       (1, (SELECT id FROM careercrack.companies WHERE user_id = 1 AND name = 'Acme Corp'), 'Marcus Chen',
        'Hiring Manager', 'marcus.chen@acme.example.com', NULL, 'https://linkedin.com/in/marcus-chen-example',
        'Leads the platform team.');

INSERT INTO careercrack.application_contacts (application_id, contact_id)
VALUES (1, (SELECT id FROM careercrack.contacts WHERE email = 'dana.lee@acme.example.com')),
       (1, (SELECT id FROM careercrack.contacts WHERE email = 'marcus.chen@acme.example.com'));

INSERT INTO careercrack.application_events (application_id, event_type, title, event_at, location, remind_at)
VALUES (1, 'INTERVIEW', 'Technical interview with Marcus', '2026-11-03 19:00+00', 'https://meet.example.com/acme-tech',
        '2026-11-02 19:00+00'),
       (1, 'ASSESSMENT', 'Take-home exercise due', '2026-11-01 04:59+00', NULL, '2026-10-31 15:00+00'),
       (2, 'FOLLOW_UP', 'Email recruiter for an update', '2026-10-28 15:00+00', NULL, '2026-10-28 14:00+00'),
       (5, 'OFFER_DEADLINE', 'Respond to the offer', '2026-11-06 22:00+00', NULL, '2026-11-04 15:00+00');

INSERT INTO careercrack.application_activities (application_id, event_id, activity_type, title, occurred_at,
                                                questions_asked, notes, self_rating)
VALUES (1, NULL, 'INTERVIEW', 'Recruiter screen', '2026-10-09 15:30+00',
        'Why Acme? Walk me through your last project. Salary expectations?',
        'Friendly call. Next step is a technical round with the hiring manager. Ask about on-call.', 4);