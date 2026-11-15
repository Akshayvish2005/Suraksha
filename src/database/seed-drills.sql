-- =====================================================================
-- Suraksha — Virtual Drills & Evacuation Routes Seed (FR-VD-01 to FR-VD-09)
-- =====================================================================

-- ── 1. EVACUATION ROUTES ─────────────────────────────────────────────
INSERT INTO evacuation_route (institution_id, name, description, assembly_point, image_path, sort_order)
VALUES
(1, 'North Stairwell & Main Field Route', 'Follow the fluorescent green signs along the north corridor down the wide concrete stairwell directly to the perimeter gate.', 'Main Football Field (Zone A)', '/images/routes/north-stairwell.png', 1),
(1, 'East Science Wing Fire Escape', 'Exit science labs via exterior metal fire escape stairs. Avoid elevators. Proceed straight past the canteen.', 'Basketball Court Assembly Point (Zone B)', '/images/routes/east-escape.png', 2),
(1, 'West Ground Floor Exit', 'Exit directly through double fire-rated doors to the open quadrangle.', 'Primary Courtyard Quadrangle (Zone C)', '/images/routes/west-ground.png', 3);

-- ── 2. DRILLS ────────────────────────────────────────────────────────
-- Drill 1: Active Earthquake Drill (Open for participation today)
INSERT INTO drill (institution_id, hazard_id, scenario_id, title, created_by, start_time, end_time, mode, campus_wide, status)
SELECT 1, h.id, s.id,
       'Annual Campus Earthquake Evacuation Drill',
       (SELECT id FROM app_user WHERE username = 'admin1' LIMIT 1),
       DATE_SUB(NOW(), INTERVAL 1 HOUR),
       DATE_ADD(NOW(), INTERVAL 48 HOUR),
       'SELF_PACED',
       TRUE,
       'OPEN'
FROM hazard h, scenario s
WHERE h.code = 'EARTHQUAKE' AND s.code = 'EQ-CLASSROOM'
LIMIT 1;

-- Drill 2: Upcoming Fire Safety Drill
INSERT INTO drill (institution_id, hazard_id, scenario_id, title, created_by, start_time, end_time, mode, campus_wide, status)
SELECT 1, h.id, s.id,
       'Science Block Chemical Fire & Smoke Drill',
       (SELECT id FROM app_user WHERE username = 'teacher1' LIMIT 1),
       DATE_ADD(NOW(), INTERVAL 3 DAY),
       DATE_ADD(NOW(), INTERVAL 5 DAY),
       'SELF_PACED',
       TRUE,
       'SCHEDULED'
FROM hazard h, scenario s
WHERE h.code = 'FIRE' AND s.code = 'FIRE-CHEMLAB'
LIMIT 1;

-- Drill 3: Past Monsoon Flood Preparedness Exercise (Completed)
INSERT INTO drill (institution_id, hazard_id, scenario_id, title, created_by, start_time, end_time, mode, campus_wide, status)
SELECT 1, h.id, s.id,
       'Monsoon High-Tide Flood Preparedness Drill',
       (SELECT id FROM app_user WHERE username = 'admin1' LIMIT 1),
       DATE_SUB(NOW(), INTERVAL 14 DAY),
       DATE_SUB(NOW(), INTERVAL 12 DAY),
       'SELF_PACED',
       TRUE,
       'CLOSED'
FROM hazard h, scenario s
WHERE h.code = 'FLOOD' AND s.code = 'FLD-CAMPUS'
LIMIT 1;

-- ── 3. DRILL PARTICIPANTS ────────────────────────────────────────────
-- Enrol student1 in Active Drill 1
INSERT INTO drill_participant (drill_id, user_id, drill_role, status, joined_at)
SELECT d.id, u.id, 'STUDENT', 'ASSIGNED', NULL
FROM drill d, app_user u
WHERE d.title = 'Annual Campus Earthquake Evacuation Drill' AND u.username = 'student1'
ON DUPLICATE KEY UPDATE status = VALUES(status);

-- Enrol teacher1 as Floor Warden in Drill 1
INSERT INTO drill_participant (drill_id, user_id, drill_role, status, joined_at, completed_at, evacuation_time_seconds)
SELECT d.id, u.id, 'FLOOR_WARDEN', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 30 MINUTE), DATE_SUB(NOW(), INTERVAL 28 MINUTE), 114
FROM drill d, app_user u
WHERE d.title = 'Annual Campus Earthquake Evacuation Drill' AND u.username = 'teacher1'
ON DUPLICATE KEY UPDATE status = VALUES(status);

-- Drill 3 (Past drill) participants history
INSERT INTO drill_participant (drill_id, user_id, drill_role, status, joined_at, completed_at, evacuation_time_seconds)
SELECT d.id, u.id, 'STUDENT', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY), 135
FROM drill d, app_user u
WHERE d.title = 'Monsoon High-Tide Flood Preparedness Drill' AND u.username = 'student1'
ON DUPLICATE KEY UPDATE status = VALUES(status);

INSERT INTO drill_participant (drill_id, user_id, drill_role, status, joined_at, completed_at, evacuation_time_seconds)
SELECT d.id, u.id, 'FIRE_MARSHAL', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY), 98
FROM drill d, app_user u
WHERE d.title = 'Monsoon High-Tide Flood Preparedness Drill' AND u.username = 'admin1'
ON DUPLICATE KEY UPDATE status = VALUES(status);
