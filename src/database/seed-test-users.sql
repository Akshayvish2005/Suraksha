-- =====================================================================
-- Suraksha — Test institution + users seed
-- Run AFTER schema.sql has been applied.
-- Passwords are BCrypt hashes of the plaintext shown in comments.
-- =====================================================================

-- 1. Institution
INSERT INTO institution (name, institution_type, state, district, address, contact_email, join_code)
VALUES ('Delhi Public School Bhopal', 'SCHOOL', 'Madhya Pradesh', 'Bhopal',
        '123 Arera Colony, Bhopal 462016', 'admin@dpsbhopal.edu.in', 'DPS-BPL-001');

-- 2. Users
-- student1  / password: student123
-- teacher1  / password: teacher123
-- admin1    / password: admin123

INSERT INTO app_user (institution_id, username, email, full_name, password_hash, role, language)
VALUES
    (1, 'student1', 'student1@dpsbhopal.edu.in', 'Khushi Lakhera',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh3y',
     'STUDENT', 'en'),

    (1, 'teacher1', 'teacher1@dpsbhopal.edu.in', 'Priya Sharma',
     '$2a$10$GRpn4mHVvqnFSAVXIZWw5e.X1Kd7VHjWMFtlJrFj4BVJfX7Y3e.6',
     'TEACHER', 'en'),

    (1, 'admin1', 'admin1@dpsbhopal.edu.in', 'Rajesh Verma',
     '$2a$10$TcGPFkuKIHrp8HtDiTbT/.ohX0dMT2CmpD0hDHbJDw/rJQ0gQkVMa',
     'INSTITUTION_ADMIN', 'en'),

    (NULL, 'sysadmin', 'sysadmin@suraksha.edu', 'System Administrator',
     '$2a$10$TcGPFkuKIHrp8HtDiTbT/.ohX0dMT2CmpD0hDHbJDw/rJQ0gQkVMa',
     'SYSTEM_ADMIN', 'en');

-- 3. A class group
INSERT INTO user_group (institution_id, name, group_type) VALUES (1, 'Class 9-A', 'CLASS');

-- 4. Enrol student in the group
INSERT INTO group_member (group_id, user_id)
SELECT ug.id, au.id
FROM user_group ug, app_user au
WHERE ug.name = 'Class 9-A' AND au.username = 'student1';
