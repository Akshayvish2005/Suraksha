-- =====================================================================
-- Suraksha (DPRES) - reference database schema, version 0.2
-- Target: MySQL 8.0+ (InnoDB, utf8mb4). H2 (MySQL mode) is used for
-- local development so nothing needs installing while you code.
-- In the Spring Boot app the tables are created from JPA entities; keep
-- this file as the design reference, and to load the seed data.
-- Run once per database:
--   CREATE DATABASE suraksha CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
--   USE suraksha;
--   -- then run this whole file
-- =====================================================================

-- ---------- Reference data ----------
CREATE TABLE hazard (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    code     VARCHAR(30)  NOT NULL UNIQUE,
    name_en  VARCHAR(100) NOT NULL,
    name_hi  VARCHAR(100),
    active   BOOLEAN      NOT NULL DEFAULT TRUE
);

-- ---------- Institutions, users, groups ----------
CREATE TABLE institution (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(200) NOT NULL,
    institution_type VARCHAR(20) NOT NULL CHECK (institution_type IN ('SCHOOL','COLLEGE')),
    state          VARCHAR(100),
    district       VARCHAR(100),
    address        VARCHAR(300),
    contact_email  VARCHAR(150),
    join_code      VARCHAR(20)  NOT NULL UNIQUE,
    active         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE app_user (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    institution_id    BIGINT REFERENCES institution(id),      -- NULL for system admins
    username          VARCHAR(80)  NOT NULL UNIQUE,
    email             VARCHAR(150),
    full_name         VARCHAR(150) NOT NULL,
    password_hash     VARCHAR(100) NOT NULL,                  -- BCrypt hash only
    role              VARCHAR(30)  NOT NULL
                      CHECK (role IN ('STUDENT','TEACHER','INSTITUTION_ADMIN','SYSTEM_ADMIN')),
    language          VARCHAR(5)   NOT NULL DEFAULT 'en' CHECK (language IN ('en','hi')),
    active            BOOLEAN      NOT NULL DEFAULT TRUE,
    consent_recorded  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at     DATETIME
);
CREATE INDEX idx_user_institution ON app_user(institution_id);

CREATE TABLE user_group (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    institution_id  BIGINT       NOT NULL REFERENCES institution(id),
    name            VARCHAR(100) NOT NULL,
    group_type      VARCHAR(20)  NOT NULL DEFAULT 'CLASS'
                    CHECK (group_type IN ('CLASS','DEPARTMENT','OTHER')),
    UNIQUE (institution_id, name)
);

CREATE TABLE group_member (
    group_id  BIGINT NOT NULL REFERENCES user_group(id),
    user_id   BIGINT NOT NULL REFERENCES app_user(id),
    PRIMARY KEY (group_id, user_id)
);

-- ---------- Learning content ----------
CREATE TABLE learning_module (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    hazard_id          BIGINT  NOT NULL REFERENCES hazard(id),
    sort_order         INT     NOT NULL DEFAULT 0,
    status             VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT','PUBLISHED')),
    pass_mark_percent  INT     NOT NULL DEFAULT 60,
    allow_retry        BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE module_text (
    module_id    BIGINT       NOT NULL REFERENCES learning_module(id),
    language     VARCHAR(5)   NOT NULL,
    title        VARCHAR(200) NOT NULL,
    description  VARCHAR(1000),
    PRIMARY KEY (module_id, language)
);

CREATE TABLE lesson (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    module_id         BIGINT NOT NULL REFERENCES learning_module(id),
    sort_order        INT    NOT NULL DEFAULT 0,
    phase             VARCHAR(10) NOT NULL DEFAULT 'DURING' CHECK (phase IN ('BEFORE','DURING','AFTER')),
    status            VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT','PUBLISHED')),
    source_reference  VARCHAR(500),
    sme_reviewed      BOOLEAN NOT NULL DEFAULT FALSE,
    reviewed_by       VARCHAR(150),
    reviewed_on       DATE
);
CREATE INDEX idx_lesson_module ON lesson(module_id);

CREATE TABLE lesson_text (
    lesson_id  BIGINT       NOT NULL REFERENCES lesson(id),
    language   VARCHAR(5)   NOT NULL,
    title      VARCHAR(200) NOT NULL,
    body       TEXT         NOT NULL,
    image_path VARCHAR(300),
    PRIMARY KEY (lesson_id, language)
);

-- ---------- Assessment content ----------
CREATE TABLE question (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    module_id   BIGINT NOT NULL REFERENCES learning_module(id),
    purpose     VARCHAR(10) NOT NULL DEFAULT 'BOTH' CHECK (purpose IN ('PRE','POST','BOTH')),
    q_type      VARCHAR(20) NOT NULL DEFAULT 'MCQ' CHECK (q_type IN ('MCQ','TRUE_FALSE','MULTI_SELECT')),
    sort_order  INT    NOT NULL DEFAULT 0,
    status      VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT','PUBLISHED'))
);
CREATE INDEX idx_question_module ON question(module_id);

CREATE TABLE question_text (
    question_id    BIGINT     NOT NULL REFERENCES question(id),
    language       VARCHAR(5) NOT NULL,
    question_text  TEXT       NOT NULL,
    explanation    TEXT,
    PRIMARY KEY (question_id, language)
);

CREATE TABLE answer_option (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id  BIGINT  NOT NULL REFERENCES question(id),
    sort_order   INT     NOT NULL DEFAULT 0,
    is_correct   BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE answer_option_text (
    option_id    BIGINT       NOT NULL REFERENCES answer_option(id),
    language     VARCHAR(5)   NOT NULL,
    option_text  VARCHAR(500) NOT NULL,
    PRIMARY KEY (option_id, language)
);

-- ---------- Learner progress and assignments ----------
CREATE TABLE module_progress (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT NOT NULL REFERENCES app_user(id),
    module_id     BIGINT NOT NULL REFERENCES learning_module(id),
    status        VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED'
                  CHECK (status IN ('NOT_STARTED','IN_PROGRESS','COMPLETED')),
    started_at    DATETIME,
    completed_at  DATETIME,
    UNIQUE (user_id, module_id)
);

CREATE TABLE lesson_progress (
    user_id       BIGINT    NOT NULL REFERENCES app_user(id),
    lesson_id     BIGINT    NOT NULL REFERENCES lesson(id),
    completed_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, lesson_id)
);

CREATE TABLE assignment (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    institution_id  BIGINT NOT NULL REFERENCES institution(id),
    module_id       BIGINT NOT NULL REFERENCES learning_module(id),
    group_id        BIGINT NOT NULL REFERENCES user_group(id),
    assigned_by     BIGINT NOT NULL REFERENCES app_user(id),
    due_date        DATE,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE assessment_attempt (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT NOT NULL REFERENCES app_user(id),
    module_id        BIGINT NOT NULL REFERENCES learning_module(id),
    attempt_type     VARCHAR(10) NOT NULL CHECK (attempt_type IN ('PRE','POST')),
    status           VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS' CHECK (status IN ('IN_PROGRESS','SUBMITTED')),
    total_questions  INT,
    correct_count    INT,
    score_percent    DECIMAL(5,2),
    started_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    submitted_at     DATETIME
);
CREATE INDEX idx_attempt_user_module ON assessment_attempt(user_id, module_id);

CREATE TABLE attempt_answer (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt_id   BIGINT  NOT NULL REFERENCES assessment_attempt(id),
    question_id  BIGINT  NOT NULL REFERENCES question(id),
    is_correct   BOOLEAN NOT NULL,
    answered_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (attempt_id, question_id)
);

CREATE TABLE attempt_answer_option (
    attempt_answer_id  BIGINT NOT NULL REFERENCES attempt_answer(id),
    option_id          BIGINT NOT NULL REFERENCES answer_option(id),
    PRIMARY KEY (attempt_answer_id, option_id)
);

-- ---------- Scenarios and virtual drills ----------
CREATE TABLE scenario (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    hazard_id          BIGINT       NOT NULL REFERENCES hazard(id),
    code               VARCHAR(50)  NOT NULL,
    language           VARCHAR(5)   NOT NULL DEFAULT 'en',
    title              VARCHAR(200) NOT NULL,
    description        VARCHAR(1000),
    estimated_minutes  INT,
    definition         TEXT         NOT NULL,                  -- scenario JSON (steps and options)
    status             VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT','PUBLISHED')),
    source_reference   VARCHAR(500),
    sme_reviewed       BOOLEAN      NOT NULL DEFAULT FALSE,
    reviewed_by        VARCHAR(150),
    reviewed_on        DATE,
    created_at         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (code, language)
);

CREATE TABLE evacuation_route (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    institution_id  BIGINT       NOT NULL REFERENCES institution(id),
    name            VARCHAR(150) NOT NULL,
    description     VARCHAR(1000),
    assembly_point  VARCHAR(200),
    image_path      VARCHAR(300),
    sort_order      INT NOT NULL DEFAULT 0
);

CREATE TABLE drill (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    institution_id  BIGINT       NOT NULL REFERENCES institution(id),
    hazard_id       BIGINT       NOT NULL REFERENCES hazard(id),
    scenario_id     BIGINT       NOT NULL REFERENCES scenario(id),
    title           VARCHAR(200) NOT NULL,
    created_by      BIGINT       NOT NULL REFERENCES app_user(id),
    start_time      DATETIME    NOT NULL,
    end_time        DATETIME    NOT NULL,
    mode            VARCHAR(20)  NOT NULL DEFAULT 'SELF_PACED' CHECK (mode IN ('SELF_PACED','LIVE')),
    campus_wide     BOOLEAN      NOT NULL DEFAULT FALSE,
    status          VARCHAR(20)  NOT NULL DEFAULT 'SCHEDULED'
                    CHECK (status IN ('SCHEDULED','OPEN','CLOSED','CANCELLED')),
    created_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cancelled_at    DATETIME,
    CHECK (end_time > start_time)
);
CREATE INDEX idx_drill_institution ON drill(institution_id, start_time);

CREATE TABLE drill_group (
    drill_id  BIGINT NOT NULL REFERENCES drill(id),
    group_id  BIGINT NOT NULL REFERENCES user_group(id),
    PRIMARY KEY (drill_id, group_id)
);

CREATE TABLE scenario_attempt (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES app_user(id),
    scenario_id         BIGINT NOT NULL REFERENCES scenario(id),
    drill_id            BIGINT REFERENCES drill(id),           -- NULL for normal practice
    started_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at        DATETIME,
    score               INT,
    max_score           INT,
    total_time_seconds  INT
);
CREATE INDEX idx_sattempt_user ON scenario_attempt(user_id);
CREATE INDEX idx_sattempt_drill ON scenario_attempt(drill_id);

CREATE TABLE scenario_decision (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt_id       BIGINT      NOT NULL REFERENCES scenario_attempt(id),
    step_id          VARCHAR(50) NOT NULL,
    option_id        VARCHAR(50),                             -- NULL if the timer ran out
    points           INT         NOT NULL DEFAULT 0,
    is_correct       BOOLEAN     NOT NULL DEFAULT FALSE,
    timed_out        BOOLEAN     NOT NULL DEFAULT FALSE,
    response_time_ms INT,
    decided_at       DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_decision_attempt ON scenario_decision(attempt_id);

CREATE TABLE drill_participant (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    drill_id                BIGINT NOT NULL REFERENCES drill(id),
    user_id                 BIGINT NOT NULL REFERENCES app_user(id),
    drill_role              VARCHAR(20) NOT NULL DEFAULT 'STUDENT'
                            CHECK (drill_role IN ('STUDENT','TEACHER','FLOOR_WARDEN','FIRE_MARSHAL')),
    status                  VARCHAR(20) NOT NULL DEFAULT 'ASSIGNED'
                            CHECK (status IN ('ASSIGNED','JOINED','COMPLETED','MISSED','INCOMPLETE')),
    joined_at               DATETIME,
    completed_at            DATETIME,
    scenario_attempt_id     BIGINT REFERENCES scenario_attempt(id),
    evacuation_route_id     BIGINT REFERENCES evacuation_route(id),
    evacuation_time_seconds INT,
    UNIQUE (drill_id, user_id)
);

-- ---------- Emergency plan tools ----------
CREATE TABLE emergency_plan (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    institution_id  BIGINT NOT NULL UNIQUE REFERENCES institution(id),
    content         TEXT,
    updated_by      BIGINT REFERENCES app_user(id),
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE emergency_contact (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    institution_id  BIGINT REFERENCES institution(id),        -- NULL = national default
    name            VARCHAR(150) NOT NULL,
    phone           VARCHAR(30)  NOT NULL,
    category        VARCHAR(50),
    is_national     BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order      INT NOT NULL DEFAULT 0
);

CREATE TABLE checklist_item (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    hazard_id   BIGINT REFERENCES hazard(id),                 -- NULL = general
    item_text   VARCHAR(300) NOT NULL,
    sort_order  INT NOT NULL DEFAULT 0
);

CREATE TABLE checklist_status (
    institution_id  BIGINT  NOT NULL REFERENCES institution(id),
    item_id         BIGINT  NOT NULL REFERENCES checklist_item(id),
    is_checked      BOOLEAN NOT NULL DEFAULT FALSE,
    checked_by      BIGINT REFERENCES app_user(id),
    checked_on      DATE,
    PRIMARY KEY (institution_id, item_id)
);

-- ---------- Notifications and reporting ----------
CREATE TABLE notification (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id            BIGINT NOT NULL REFERENCES app_user(id),
    notification_type  VARCHAR(30) NOT NULL
                       CHECK (notification_type IN ('ASSIGNMENT','DRILL_SCHEDULED','DRILL_REMINDER','DRILL_CANCELLED','GENERAL')),
    message            VARCHAR(500) NOT NULL,
    link               VARCHAR(300),
    is_read            BOOLEAN NOT NULL DEFAULT FALSE,
    created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_notification_user ON notification(user_id, is_read);

CREATE TABLE readiness_snapshot (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    institution_id   BIGINT NOT NULL REFERENCES institution(id),
    group_id         BIGINT REFERENCES user_group(id),        -- NULL = whole institution
    snapshot_date    DATE   NOT NULL,
    readiness_index  DECIMAL(5,2) NOT NULL,
    training_pct     DECIMAL(5,2),
    knowledge_pct    DECIMAL(5,2),
    drill_pct        DECIMAL(5,2)
);

-- =====================================================================
-- Seed data
-- =====================================================================
INSERT INTO hazard (code, name_en, name_hi) VALUES
    ('EARTHQUAKE', 'Earthquake', 'भूकंप'),
    ('FLOOD',      'Flood',      'बाढ़'),
    ('FIRE',       'Fire',       'आग');

-- National emergency numbers. VERIFY these before release; ambulance numbers vary by state.
INSERT INTO emergency_contact (institution_id, name, phone, category, is_national, sort_order) VALUES
    (NULL, 'National Emergency Number (all services)', '112', 'General',   TRUE, 1),
    (NULL, 'Police',                                   '100', 'Police',    TRUE, 2),
    (NULL, 'Fire Service',                             '101', 'Fire',      TRUE, 3),
    (NULL, 'Ambulance',                                '108', 'Ambulance', TRUE, 4);
