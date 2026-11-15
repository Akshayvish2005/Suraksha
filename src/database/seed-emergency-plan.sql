-- =====================================================================
-- Suraksha — Emergency Plan, Contacts & Safety Checklist Seed
-- Run AFTER schema.sql, seed-test-users.sql, and seed-drills.sql
-- =====================================================================

-- 1. Institutional Emergency Contacts for DPS Bhopal (institution_id = 1)
INSERT INTO emergency_contact (institution_id, name, phone, category, is_national, sort_order) VALUES
    (1, 'Campus Emergency Control Desk',   '+91 755 2420000', 'Security',    FALSE, 10),
    (1, 'Campus Infirmary / Medical Duty', '+91 755 2420005', 'Medical',     FALSE, 11),
    (1, 'Safety Officer (Rajesh Verma)',   '+91 98260 12345', 'Admin',       FALSE, 12),
    (1, 'Arera Colony Police Station',     '+91 755 2555100', 'Police',      FALSE, 13),
    (1, 'Bhopal Central Fire Brigade',     '+91 755 2555101', 'Fire',        FALSE, 14),
    (1, 'Hamidia Hospital Emergency',      '+91 755 2540222', 'Ambulance',   FALSE, 15),
    (1, 'MP State Disaster Control Room',  '+91 755 2441419', 'Disaster Hub',FALSE, 16);

-- 2. Safety Checklist Items
INSERT INTO checklist_item (hazard_id, item_text, sort_order) VALUES
    (NULL, 'All primary and secondary emergency exit doors are unlocked and unobstructed.', 1),
    (1,    'Classroom "Drop, Cover, and Hold On" earthquake procedure placards posted.',     2),
    (3,    'ABC powder fire extinguishers inspected with pressure needle in the green zone.', 3),
    (NULL, 'Emergency first-aid trauma kits on each floor corridor verified and fully stocked.', 4),
    (NULL, 'Battery-backed emergency sirens and campus PA announcement horns tested.',       5),
    (NULL, 'Evacuation maps with primary assembly point routes displayed at each stair landing.', 6),
    (2,    'Ground-floor storm water drain gratings cleared of silt and debris for flood safety.', 7),
    (3,    'Electrical distribution panels and circuit breakers inspected for zero overload.', 8);

-- 3. Pre-seed Checklist Status for Institution 1
INSERT INTO checklist_status (institution_id, item_id, is_checked, checked_by, checked_on)
SELECT 1, ci.id, TRUE, 3, CURRENT_DATE()
FROM checklist_item ci
WHERE ci.sort_order IN (1, 2, 3, 5);

INSERT INTO checklist_status (institution_id, item_id, is_checked, checked_by, checked_on)
SELECT 1, ci.id, FALSE, NULL, NULL
FROM checklist_item ci
WHERE ci.sort_order NOT IN (1, 2, 3, 5);

-- 4. Institution Emergency Plan
INSERT INTO emergency_plan (institution_id, content, updated_by, updated_at) VALUES
(1,
'## 1. Objectives & Scope
This Emergency Preparedness & Response Plan provides mandatory standard operating procedures (SOPs) for Delhi Public School Bhopal in accordance with National Disaster Management Authority (NDMA) guidelines and the Disaster Management Act, 2005.

## 2. Emergency Response Roles
- Incident Commander (Principal / Safety Officer): Oversees campus evacuation and coordinates with civil defense (112).
- Floor Wardens (Teachers & Faculty): Direct students quietly to secondary stairs and emergency exit routes; ensure no one is left behind.
- First Aid & Triage Team: Set up first-aid station at the primary assembly ground.

## 3. Hazard Specific Standard Operating Procedures
### Earthquake
1. Drop, Cover, and Hold On under heavy desks away from glass windows.
2. When tremors cease, evacuate in orderly single files via designated emergency stairs — never use elevators.
3. Assemble at the Main Sports Ground away from electric poles and overhead wires.

### Fire & Smoke
1. Sound the manual fire alarm and immediately dial Fire Services (101 / 112).
2. Stay low below rising toxic smoke; feel closed doors for heat before turning handles.
3. Evacuate to North Open Field and take headcounts.

### Flood / Flash Inundation
1. Move immediately to upper floors (Floor 2 and Terrace) if ground level is breached.
2. Cut main electrical circuit breakers to prevent electrocution.
3. Await NDRF rescue boats; never walk or drive through moving water.',
3,
NOW()
)
ON DUPLICATE KEY UPDATE content = VALUES(content), updated_at = NOW();

