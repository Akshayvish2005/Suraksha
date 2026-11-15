-- Fix BCrypt password hashes for test users
-- student1 / student123
UPDATE app_user SET password_hash='$2a$10$pyJXeZmezG4EKYfAMnHAreZNQJhrO7heR6VnUCQ955mvh6E2XlvQq' WHERE username='student1';
-- teacher1 / teacher123
UPDATE app_user SET password_hash='$2a$10$UipPsI8rhPt0gAAgagpqQ.7FttDSZULU3L3pECWc3KdnbBSMnu2H.' WHERE username='teacher1';
-- admin1 / admin123
UPDATE app_user SET password_hash='$2a$10$vd9EnpwD88CKmKHVPBRYvuSBB4AVJNGLQrR0qPPZPoLLafWIh6s3q' WHERE username='admin1';
-- sysadmin / admin123
UPDATE app_user SET password_hash='$2a$10$vd9EnpwD88CKmKHVPBRYvuSBB4AVJNGLQrR0qPPZPoLLafWIh6s3q' WHERE username='sysadmin';

-- Verify
SELECT username, role, active, LEFT(password_hash,20) as hash_prefix FROM app_user;
