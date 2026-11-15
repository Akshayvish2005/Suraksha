-- =====================================================================
-- Suraksha — Assessment Questions seed
-- 5 questions per module (purpose=BOTH so they serve as pre AND post test)
-- =====================================================================

-- ── EARTHQUAKE QUESTIONS ─────────────────────────────────────────

-- Q1 Earthquake MCQ
INSERT INTO question (module_id, purpose, q_type, sort_order, status)
SELECT lm.id, 'BOTH', 'MCQ', 1, 'PUBLISHED'
FROM learning_module lm JOIN hazard h ON h.id = lm.hazard_id WHERE h.code = 'EARTHQUAKE';

INSERT INTO question_text (question_id, language, question_text, explanation)
SELECT q.id, 'en',
       'What should you do FIRST when an earthquake starts while you are indoors?',
       'Drop, Cover, and Hold On is the globally recommended action. Running outside during shaking causes most injuries from falling debris and tripping.'
FROM question q JOIN learning_module lm ON lm.id = q.module_id JOIN hazard h ON h.id = lm.hazard_id
WHERE h.code = 'EARTHQUAKE' AND q.sort_order = 1;

INSERT INTO answer_option (question_id, sort_order, is_correct) VALUES
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='EARTHQUAKE' AND q.sort_order=1), 1, FALSE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='EARTHQUAKE' AND q.sort_order=1), 2, TRUE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='EARTHQUAKE' AND q.sort_order=1), 3, FALSE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='EARTHQUAKE' AND q.sort_order=1), 4, FALSE);

-- option texts for Q1 earthquake
INSERT INTO answer_option_text (option_id, language, option_text)
SELECT ao.id, 'en',
       CASE ao.sort_order
           WHEN 1 THEN 'Run outside immediately'
           WHEN 2 THEN 'Drop, Cover, and Hold On under a sturdy desk'
           WHEN 3 THEN 'Stand in a doorway'
           WHEN 4 THEN 'Call emergency services first'
       END
FROM answer_option ao
JOIN question q ON q.id = ao.question_id
JOIN learning_module lm ON lm.id = q.module_id
JOIN hazard h ON h.id = lm.hazard_id
WHERE h.code = 'EARTHQUAKE' AND q.sort_order = 1;

-- Q2 Earthquake TRUE_FALSE
INSERT INTO question (module_id, purpose, q_type, sort_order, status)
SELECT lm.id, 'BOTH', 'TRUE_FALSE', 2, 'PUBLISHED'
FROM learning_module lm JOIN hazard h ON h.id = lm.hazard_id WHERE h.code = 'EARTHQUAKE';

INSERT INTO question_text (question_id, language, question_text, explanation)
SELECT q.id, 'en',
       'It is safe to use lifts (elevators) immediately after an earthquake to evacuate quickly.',
       'FALSE. Elevators may be structurally damaged and could fail. Always use stairs after an earthquake. Elevators may also trap you during aftershocks.'
FROM question q JOIN learning_module lm ON lm.id = q.module_id JOIN hazard h ON h.id = lm.hazard_id
WHERE h.code = 'EARTHQUAKE' AND q.sort_order = 2;

INSERT INTO answer_option (question_id, sort_order, is_correct) VALUES
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='EARTHQUAKE' AND q.sort_order=2), 1, FALSE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='EARTHQUAKE' AND q.sort_order=2), 2, TRUE);

INSERT INTO answer_option_text (option_id, language, option_text)
SELECT ao.id, 'en', CASE ao.sort_order WHEN 1 THEN 'True' WHEN 2 THEN 'False' END
FROM answer_option ao JOIN question q ON q.id = ao.question_id
JOIN learning_module lm ON lm.id = q.module_id JOIN hazard h ON h.id = lm.hazard_id
WHERE h.code = 'EARTHQUAKE' AND q.sort_order = 2;

-- Q3 Earthquake MCQ
INSERT INTO question (module_id, purpose, q_type, sort_order, status)
SELECT lm.id, 'BOTH', 'MCQ', 3, 'PUBLISHED'
FROM learning_module lm JOIN hazard h ON h.id = lm.hazard_id WHERE h.code = 'EARTHQUAKE';

INSERT INTO question_text (question_id, language, question_text, explanation)
SELECT q.id, 'en',
       'What is an aftershock?',
       'Aftershocks are smaller earthquakes that follow the main quake. They can occur minutes, hours, or even days later and can cause further damage to weakened structures.'
FROM question q JOIN learning_module lm ON lm.id = q.module_id JOIN hazard h ON h.id = lm.hazard_id
WHERE h.code = 'EARTHQUAKE' AND q.sort_order = 3;

INSERT INTO answer_option (question_id, sort_order, is_correct) VALUES
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='EARTHQUAKE' AND q.sort_order=3), 1, FALSE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='EARTHQUAKE' AND q.sort_order=3), 2, FALSE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='EARTHQUAKE' AND q.sort_order=3), 3, TRUE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='EARTHQUAKE' AND q.sort_order=3), 4, FALSE);

INSERT INTO answer_option_text (option_id, language, option_text)
SELECT ao.id, 'en',
       CASE ao.sort_order
           WHEN 1 THEN 'A large earthquake that happens before the main quake'
           WHEN 2 THEN 'A tsunami triggered by the earthquake'
           WHEN 3 THEN 'Smaller earthquakes that follow the main earthquake'
           WHEN 4 THEN 'Ground vibrations caused by heavy vehicles'
       END
FROM answer_option ao JOIN question q ON q.id = ao.question_id
JOIN learning_module lm ON lm.id = q.module_id JOIN hazard h ON h.id = lm.hazard_id
WHERE h.code = 'EARTHQUAKE' AND q.sort_order = 3;

-- Q4 Earthquake TRUE_FALSE
INSERT INTO question (module_id, purpose, q_type, sort_order, status)
SELECT lm.id, 'BOTH', 'TRUE_FALSE', 4, 'PUBLISHED'
FROM learning_module lm JOIN hazard h ON h.id = lm.hazard_id WHERE h.code = 'EARTHQUAKE';

INSERT INTO question_text (question_id, language, question_text, explanation)
SELECT q.id, 'en',
       'You should prepare an emergency kit before an earthquake strikes.',
       'TRUE. Being prepared with water, food, torch, first-aid kit, and important documents allows you to survive and recover much more effectively after a disaster.'
FROM question q JOIN learning_module lm ON lm.id = q.module_id JOIN hazard h ON h.id = lm.hazard_id
WHERE h.code = 'EARTHQUAKE' AND q.sort_order = 4;

INSERT INTO answer_option (question_id, sort_order, is_correct) VALUES
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='EARTHQUAKE' AND q.sort_order=4), 1, TRUE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='EARTHQUAKE' AND q.sort_order=4), 2, FALSE);

INSERT INTO answer_option_text (option_id, language, option_text)
SELECT ao.id, 'en', CASE ao.sort_order WHEN 1 THEN 'True' WHEN 2 THEN 'False' END
FROM answer_option ao JOIN question q ON q.id = ao.question_id
JOIN learning_module lm ON lm.id = q.module_id JOIN hazard h ON h.id = lm.hazard_id
WHERE h.code = 'EARTHQUAKE' AND q.sort_order = 4;

-- Q5 Earthquake MCQ
INSERT INTO question (module_id, purpose, q_type, sort_order, status)
SELECT lm.id, 'BOTH', 'MCQ', 5, 'PUBLISHED'
FROM learning_module lm JOIN hazard h ON h.id = lm.hazard_id WHERE h.code = 'EARTHQUAKE';

INSERT INTO question_text (question_id, language, question_text, explanation)
SELECT q.id, 'en',
       'Why should you NOT use open flames or switch on lights immediately after an earthquake?',
       'Gas leaks are common after earthquakes. An open flame or electrical spark can ignite leaking gas, causing an explosion or fire.'
FROM question q JOIN learning_module lm ON lm.id = q.module_id JOIN hazard h ON h.id = lm.hazard_id
WHERE h.code = 'EARTHQUAKE' AND q.sort_order = 5;

INSERT INTO answer_option (question_id, sort_order, is_correct) VALUES
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='EARTHQUAKE' AND q.sort_order=5), 1, TRUE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='EARTHQUAKE' AND q.sort_order=5), 2, FALSE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='EARTHQUAKE' AND q.sort_order=5), 3, FALSE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='EARTHQUAKE' AND q.sort_order=5), 4, FALSE);

INSERT INTO answer_option_text (option_id, language, option_text)
SELECT ao.id, 'en',
       CASE ao.sort_order
           WHEN 1 THEN 'Gas leaks may be present, posing explosion risk'
           WHEN 2 THEN 'Electricity is not needed after earthquakes'
           WHEN 3 THEN 'The power grid is always damaged after earthquakes'
           WHEN 4 THEN 'It wastes energy during an emergency'
       END
FROM answer_option ao JOIN question q ON q.id = ao.question_id
JOIN learning_module lm ON lm.id = q.module_id JOIN hazard h ON h.id = lm.hazard_id
WHERE h.code = 'EARTHQUAKE' AND q.sort_order = 5;

-- ── FLOOD QUESTIONS (5 questions, same pattern) ───────────────────

INSERT INTO question (module_id, purpose, q_type, sort_order, status)
SELECT lm.id, 'BOTH', 'MCQ', 1, 'PUBLISHED' FROM learning_module lm JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD';
INSERT INTO question (module_id, purpose, q_type, sort_order, status)
SELECT lm.id, 'BOTH', 'TRUE_FALSE', 2, 'PUBLISHED' FROM learning_module lm JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD';
INSERT INTO question (module_id, purpose, q_type, sort_order, status)
SELECT lm.id, 'BOTH', 'MCQ', 3, 'PUBLISHED' FROM learning_module lm JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD';
INSERT INTO question (module_id, purpose, q_type, sort_order, status)
SELECT lm.id, 'BOTH', 'TRUE_FALSE', 4, 'PUBLISHED' FROM learning_module lm JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD';
INSERT INTO question (module_id, purpose, q_type, sort_order, status)
SELECT lm.id, 'BOTH', 'MCQ', 5, 'PUBLISHED' FROM learning_module lm JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD';

INSERT INTO question_text (question_id, language, question_text, explanation)
SELECT q.id, 'en', 'How deep does fast-moving floodwater need to be to sweep away an adult?',
       '30 cm (about knee height) of fast-moving water is enough to knock over an adult. Never attempt to walk through fast-moving floodwater.'
FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=1;

INSERT INTO question_text (question_id, language, question_text, explanation)
SELECT q.id, 'en', 'You should wait until floodwater enters your home before evacuating.',
       'FALSE. You should evacuate as soon as a flood warning is issued, not wait for water to arrive. Early evacuation is always safer.'
FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=2;

INSERT INTO question_text (question_id, language, question_text, explanation)
SELECT q.id, 'en', 'What should you do with drinking water after a flood?',
       'Floodwater contaminates the water supply with sewage and chemicals. All drinking water must be boiled or disinfected until authorities declare it safe.'
FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=3;

INSERT INTO question_text (question_id, language, question_text, explanation)
SELECT q.id, 'en', 'Wearing rubber gloves and boots when cleaning flood-affected areas is important.',
       'TRUE. Floodwater carries sewage, chemicals, and pathogens. Personal protective equipment prevents serious infections and chemical exposure.'
FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=4;

INSERT INTO question_text (question_id, language, question_text, explanation)
SELECT q.id, 'en', 'If you are trapped in a flooded building, you should:',
       'Moving to the highest floor and signalling for help is the safest option. Swimming through floodwater is very dangerous due to currents, debris, and contamination.'
FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=5;

-- Flood Q1 options
INSERT INTO answer_option (question_id, sort_order, is_correct) VALUES
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=1),1,FALSE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=1),2,TRUE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=1),3,FALSE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=1),4,FALSE);
INSERT INTO answer_option_text (option_id, language, option_text)
SELECT ao.id,'en', CASE ao.sort_order WHEN 1 THEN '10 cm' WHEN 2 THEN '30 cm' WHEN 3 THEN '60 cm' WHEN 4 THEN '1 metre' END
FROM answer_option ao JOIN question q ON q.id=ao.question_id JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=1;

-- Flood Q2 T/F
INSERT INTO answer_option (question_id, sort_order, is_correct) VALUES
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=2),1,FALSE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=2),2,TRUE);
INSERT INTO answer_option_text (option_id, language, option_text)
SELECT ao.id,'en', CASE ao.sort_order WHEN 1 THEN 'True' WHEN 2 THEN 'False' END
FROM answer_option ao JOIN question q ON q.id=ao.question_id JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=2;

-- Flood Q3 options
INSERT INTO answer_option (question_id, sort_order, is_correct) VALUES
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=3),1,FALSE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=3),2,TRUE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=3),3,FALSE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=3),4,FALSE);
INSERT INTO answer_option_text (option_id, language, option_text)
SELECT ao.id,'en', CASE ao.sort_order WHEN 1 THEN 'Drink it directly — floods make water cleaner' WHEN 2 THEN 'Boil or disinfect it before drinking' WHEN 3 THEN 'Filter it with a cloth' WHEN 4 THEN 'Add salt to purify it' END
FROM answer_option ao JOIN question q ON q.id=ao.question_id JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=3;

-- Flood Q4 T/F
INSERT INTO answer_option (question_id, sort_order, is_correct) VALUES
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=4),1,TRUE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=4),2,FALSE);
INSERT INTO answer_option_text (option_id, language, option_text)
SELECT ao.id,'en', CASE ao.sort_order WHEN 1 THEN 'True' WHEN 2 THEN 'False' END
FROM answer_option ao JOIN question q ON q.id=ao.question_id JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=4;

-- Flood Q5 options
INSERT INTO answer_option (question_id, sort_order, is_correct) VALUES
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=5),1,FALSE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=5),2,TRUE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=5),3,FALSE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=5),4,FALSE);
INSERT INTO answer_option_text (option_id, language, option_text)
SELECT ao.id,'en', CASE ao.sort_order WHEN 1 THEN 'Try to swim to a nearby building' WHEN 2 THEN 'Move to the highest floor and signal for help' WHEN 3 THEN 'Go to the basement to shelter from current' WHEN 4 THEN 'Open windows to let water flow through faster' END
FROM answer_option ao JOIN question q ON q.id=ao.question_id JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FLOOD' AND q.sort_order=5;

-- ── FIRE QUESTIONS (5 questions) ──────────────────────────────────

INSERT INTO question (module_id, purpose, q_type, sort_order, status)
SELECT lm.id, 'BOTH', 'MCQ', 1, 'PUBLISHED' FROM learning_module lm JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE';
INSERT INTO question (module_id, purpose, q_type, sort_order, status)
SELECT lm.id, 'BOTH', 'MCQ', 2, 'PUBLISHED' FROM learning_module lm JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE';
INSERT INTO question (module_id, purpose, q_type, sort_order, status)
SELECT lm.id, 'BOTH', 'TRUE_FALSE', 3, 'PUBLISHED' FROM learning_module lm JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE';
INSERT INTO question (module_id, purpose, q_type, sort_order, status)
SELECT lm.id, 'BOTH', 'MCQ', 4, 'PUBLISHED' FROM learning_module lm JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE';
INSERT INTO question (module_id, purpose, q_type, sort_order, status)
SELECT lm.id, 'BOTH', 'TRUE_FALSE', 5, 'PUBLISHED' FROM learning_module lm JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE';

INSERT INTO question_text (question_id, language, question_text, explanation)
SELECT q.id,'en','What does PASS stand for when using a fire extinguisher?',
       'PASS: Pull the safety pin, Aim at the base of the fire, Squeeze the handle, Sweep side to side. This is the standard technique for using a portable fire extinguisher.'
FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=1;

INSERT INTO question_text (question_id, language, question_text, explanation)
SELECT q.id,'en','If you are caught in a smoke-filled corridor, you should:',
       'Smoke rises, so cleaner air is near the floor. Crawling keeps you below the smoke layer and gives you more time to escape safely.'
FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=2;

INSERT INTO question_text (question_id, language, question_text, explanation)
SELECT q.id,'en','It is safe to use a lift (elevator) to evacuate during a building fire.',
       'FALSE. Lifts can malfunction in a fire and trap occupants. Always use stairs during a fire evacuation.'
FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=3;

INSERT INTO question_text (question_id, language, question_text, explanation)
SELECT q.id,'en','What is the RACE procedure in fire emergencies?',
       'RACE stands for Rescue, Alert, Contain, Evacuate. It is a standard fire response protocol used in many institutions.'
FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=4;

INSERT INTO question_text (question_id, language, question_text, explanation)
SELECT q.id,'en','Closing doors behind you slows the spread of fire and smoke.',
       'TRUE. Closed doors act as a barrier, slowing fire spread significantly and buying extra minutes for evacuation and rescue.'
FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=5;

-- Fire Q1
INSERT INTO answer_option (question_id, sort_order, is_correct) VALUES
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=1),1,FALSE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=1),2,FALSE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=1),3,TRUE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=1),4,FALSE);
INSERT INTO answer_option_text (option_id, language, option_text)
SELECT ao.id,'en', CASE ao.sort_order WHEN 1 THEN 'Prevent, Alert, Spray, Spray' WHEN 2 THEN 'Prepare, Aim, Spray, Sweep' WHEN 3 THEN 'Pull, Aim, Squeeze, Sweep' WHEN 4 THEN 'Press, Angle, Spray, Scan' END
FROM answer_option ao JOIN question q ON q.id=ao.question_id JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=1;

-- Fire Q2
INSERT INTO answer_option (question_id, sort_order, is_correct) VALUES
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=2),1,FALSE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=2),2,TRUE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=2),3,FALSE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=2),4,FALSE);
INSERT INTO answer_option_text (option_id, language, option_text)
SELECT ao.id,'en', CASE ao.sort_order WHEN 1 THEN 'Run upright as fast as possible' WHEN 2 THEN 'Crawl low under the smoke to the exit' WHEN 3 THEN 'Hold your breath and sprint through' WHEN 4 THEN 'Open all windows to clear the smoke' END
FROM answer_option ao JOIN question q ON q.id=ao.question_id JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=2;

-- Fire Q3 T/F
INSERT INTO answer_option (question_id, sort_order, is_correct) VALUES
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=3),1,FALSE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=3),2,TRUE);
INSERT INTO answer_option_text (option_id, language, option_text)
SELECT ao.id,'en', CASE ao.sort_order WHEN 1 THEN 'True' WHEN 2 THEN 'False' END
FROM answer_option ao JOIN question q ON q.id=ao.question_id JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=3;

-- Fire Q4
INSERT INTO answer_option (question_id, sort_order, is_correct) VALUES
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=4),1,FALSE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=4),2,FALSE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=4),3,FALSE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=4),4,TRUE);
INSERT INTO answer_option_text (option_id, language, option_text)
SELECT ao.id,'en', CASE ao.sort_order WHEN 1 THEN 'Run, Alert, Climb, Escape' WHEN 2 THEN 'React, Alarm, Call, Exit' WHEN 3 THEN 'Report, Assess, Control, Escape' WHEN 4 THEN 'Rescue, Alert, Contain, Evacuate' END
FROM answer_option ao JOIN question q ON q.id=ao.question_id JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=4;

-- Fire Q5 T/F
INSERT INTO answer_option (question_id, sort_order, is_correct) VALUES
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=5),1,TRUE),
((SELECT q.id FROM question q JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=5),2,FALSE);
INSERT INTO answer_option_text (option_id, language, option_text)
SELECT ao.id,'en', CASE ao.sort_order WHEN 1 THEN 'True' WHEN 2 THEN 'False' END
FROM answer_option ao JOIN question q ON q.id=ao.question_id JOIN learning_module lm ON lm.id=q.module_id JOIN hazard h ON h.id=lm.hazard_id WHERE h.code='FIRE' AND q.sort_order=5;
