-- =====================================================================
-- Suraksha — Modules + Lessons seed
-- Requires: hazard rows already present (seeded in schema.sql)
-- =====================================================================

-- ── MODULES ──────────────────────────────────────────────────────────

INSERT INTO learning_module (hazard_id, sort_order, status, pass_mark_percent, allow_retry)
SELECT id, 1, 'PUBLISHED', 60, TRUE FROM hazard WHERE code = 'EARTHQUAKE';

INSERT INTO learning_module (hazard_id, sort_order, status, pass_mark_percent, allow_retry)
SELECT id, 2, 'PUBLISHED', 60, TRUE FROM hazard WHERE code = 'FLOOD';

INSERT INTO learning_module (hazard_id, sort_order, status, pass_mark_percent, allow_retry)
SELECT id, 3, 'PUBLISHED', 60, TRUE FROM hazard WHERE code = 'FIRE';

-- ── MODULE TEXT (English) ──────────────────────────────────────────

INSERT INTO module_text (module_id, language, title, description)
SELECT lm.id, 'en',
       'Earthquake Safety',
       'Learn how to protect yourself before, during, and after an earthquake. Understand drop-cover-hold procedures, safe spots, and recovery actions.'
FROM learning_module lm JOIN hazard h ON h.id = lm.hazard_id WHERE h.code = 'EARTHQUAKE';

INSERT INTO module_text (module_id, language, title, description)
SELECT lm.id, 'en',
       'Flood Preparedness',
       'Understand flood warnings, evacuation procedures, and how to stay safe during and after a flood. Learn to identify flood-prone areas.'
FROM learning_module lm JOIN hazard h ON h.id = lm.hazard_id WHERE h.code = 'FLOOD';

INSERT INTO module_text (module_id, language, title, description)
SELECT lm.id, 'en',
       'Fire Safety',
       'Learn fire prevention, evacuation routes, how to use a fire extinguisher, and what to do if you are caught in a fire emergency.'
FROM learning_module lm JOIN hazard h ON h.id = lm.hazard_id WHERE h.code = 'FIRE';

-- ── LESSONS — EARTHQUAKE ──────────────────────────────────────────

INSERT INTO lesson (module_id, sort_order, phase, status, source_reference, sme_reviewed)
SELECT lm.id, 1, 'BEFORE', 'PUBLISHED',
       'NDMA Guidelines on Earthquake Safety (2021)',
       FALSE
FROM learning_module lm JOIN hazard h ON h.id = lm.hazard_id WHERE h.code = 'EARTHQUAKE';

INSERT INTO lesson (module_id, sort_order, phase, status, source_reference, sme_reviewed)
SELECT lm.id, 2, 'DURING', 'PUBLISHED',
       'NDMA Guidelines on Earthquake Safety (2021)',
       FALSE
FROM learning_module lm JOIN hazard h ON h.id = lm.hazard_id WHERE h.code = 'EARTHQUAKE';

INSERT INTO lesson (module_id, sort_order, phase, status, source_reference, sme_reviewed)
SELECT lm.id, 3, 'AFTER', 'PUBLISHED',
       'NDMA Guidelines on Earthquake Safety (2021)',
       FALSE
FROM learning_module lm JOIN hazard h ON h.id = lm.hazard_id WHERE h.code = 'EARTHQUAKE';

-- ── LESSON TEXT — EARTHQUAKE ──────────────────────────────────────

-- Before
INSERT INTO lesson_text (lesson_id, language, title, body)
SELECT l.id, 'en',
       'Preparing for an Earthquake',
       '<p>Earthquakes strike without warning, but you can significantly reduce risk by preparing in advance.</p>
        <h3>Key Preparations</h3>
        <ul>
          <li><strong>Secure heavy furniture</strong>: Bolt bookcases, water heaters, and heavy appliances to walls.</li>
          <li><strong>Identify safe spots</strong>: Know where to take cover in every room — under a sturdy desk or table, or against an interior wall away from windows.</li>
          <li><strong>Prepare an emergency kit</strong>: Water (3 litres per person per day), food, torch, first-aid kit, copies of documents.</li>
          <li><strong>Know your building</strong>: Locate fire extinguishers, first-aid kits, and emergency exits.</li>
          <li><strong>Practice Drop, Cover, Hold</strong>: Conduct regular drills so it becomes automatic.</li>
        </ul>
        <h3>Key Takeaway</h3>
        <p>Always keep safe spots in your home, school, and office marked. Practice the drill every 6 months.</p>'
FROM lesson l
JOIN learning_module lm ON lm.id = l.module_id
JOIN hazard h ON h.id = lm.hazard_id
WHERE h.code = 'EARTHQUAKE' AND l.phase = 'BEFORE';

-- During
INSERT INTO lesson_text (lesson_id, language, title, body)
SELECT l.id, 'en',
       'What to Do During an Earthquake',
       '<p>When shaking starts, every second counts. Follow Drop, Cover, and Hold On immediately.</p>
        <h3>DROP, COVER, HOLD ON</h3>
        <ol>
          <li><strong>DROP</strong> to your hands and knees so you are not knocked over.</li>
          <li><strong>COVER</strong> your head and neck with one arm. If a sturdy table or desk is nearby, crawl underneath it.</li>
          <li><strong>HOLD ON</strong> until the shaking stops. If under a table, hold one leg. Be prepared to move with it.</li>
        </ol>
        <h3>If you are indoors</h3>
        <ul>
          <li>Stay away from windows, outside walls, and anything that could fall.</li>
          <li>Do NOT run outside during shaking — most injuries happen when people try to move or run.</li>
          <li>If in bed, stay there and protect your head with a pillow.</li>
        </ul>
        <h3>If you are outdoors</h3>
        <ul>
          <li>Move away from buildings, streetlights, and utility wires.</li>
          <li>Drop to the ground and stay until shaking stops.</li>
        </ul>
        <h3>Key Takeaway</h3>
        <p>Do NOT run outside during shaking. Drop, Cover, Hold On — stay in position until the shaking completely stops.</p>'
FROM lesson l
JOIN learning_module lm ON lm.id = l.module_id
JOIN hazard h ON h.id = lm.hazard_id
WHERE h.code = 'EARTHQUAKE' AND l.phase = 'DURING';

-- After
INSERT INTO lesson_text (lesson_id, language, title, body)
SELECT l.id, 'en',
       'After an Earthquake',
       '<p>When shaking stops, hazards may still exist. Proceed carefully.</p>
        <h3>Immediate Steps</h3>
        <ul>
          <li>Check for injuries on yourself and others. Provide first aid if trained.</li>
          <li>Watch for fires and put them out if safe to do so.</li>
          <li>Do NOT use elevators after an earthquake.</li>
          <li>Exit carefully — watch for fallen debris and broken glass.</li>
        </ul>
        <h3>Aftershocks</h3>
        <p>Aftershocks are smaller earthquakes that follow the main quake. They can occur minutes, hours, or days later. Be prepared to Drop, Cover, Hold On again at any time.</p>
        <h3>Utilities</h3>
        <ul>
          <li>Do NOT use open flames — gas leaks may be present.</li>
          <li>Do NOT switch on lights if you smell gas.</li>
          <li>Report damage to gas, water, and electrical lines to authorities.</li>
        </ul>
        <h3>Key Takeaway</h3>
        <p>Expect aftershocks. Stay away from damaged structures and do not re-enter a building until declared safe by authorities.</p>'
FROM lesson l
JOIN learning_module lm ON lm.id = l.module_id
JOIN hazard h ON h.id = lm.hazard_id
WHERE h.code = 'EARTHQUAKE' AND l.phase = 'AFTER';

-- ── LESSONS — FLOOD ──────────────────────────────────────────────

INSERT INTO lesson (module_id, sort_order, phase, status, source_reference, sme_reviewed)
SELECT lm.id, 1, 'BEFORE', 'PUBLISHED', 'NDMA Flood Guidelines (2022)', FALSE
FROM learning_module lm JOIN hazard h ON h.id = lm.hazard_id WHERE h.code = 'FLOOD';

INSERT INTO lesson (module_id, sort_order, phase, status, source_reference, sme_reviewed)
SELECT lm.id, 2, 'DURING', 'PUBLISHED', 'NDMA Flood Guidelines (2022)', FALSE
FROM learning_module lm JOIN hazard h ON h.id = lm.hazard_id WHERE h.code = 'FLOOD';

INSERT INTO lesson (module_id, sort_order, phase, status, source_reference, sme_reviewed)
SELECT lm.id, 3, 'AFTER', 'PUBLISHED', 'NDMA Flood Guidelines (2022)', FALSE
FROM learning_module lm JOIN hazard h ON h.id = lm.hazard_id WHERE h.code = 'FLOOD';

INSERT INTO lesson_text (lesson_id, language, title, body)
SELECT l.id, 'en', 'Preparing for a Flood',
       '<p>Floods can be predicted with warnings. Early preparation saves lives.</p>
        <h3>Know Your Risk</h3>
        <ul>
          <li>Identify if your home or school is in a flood-prone area using government flood maps.</li>
          <li>Know the local warning signals and evacuation routes.</li>
        </ul>
        <h3>Before Flooding Begins</h3>
        <ul>
          <li>Move important documents, electronics, and valuables to higher floors.</li>
          <li>Prepare a Go-Bag: water, food, medicines, torch, phone charger, copies of ID.</li>
          <li>Know evacuation shelters in your area.</li>
          <li>Disconnect electrical appliances.</li>
        </ul>
        <h3>Key Takeaway</h3>
        <p>Never wait for water to reach your door before evacuating. Leave early when a flood warning is issued.</p>'
FROM lesson l JOIN learning_module lm ON lm.id = l.module_id JOIN hazard h ON h.id = lm.hazard_id
WHERE h.code = 'FLOOD' AND l.phase = 'BEFORE';

INSERT INTO lesson_text (lesson_id, language, title, body)
SELECT l.id, 'en', 'Staying Safe During a Flood',
       '<p>Moving floodwater is extremely dangerous. Even shallow water can knock you off your feet.</p>
        <h3>If Indoors</h3>
        <ul>
          <li>Move to the highest floor. Do not go to the basement.</li>
          <li>Signal for help from windows — do not try to swim to safety.</li>
          <li>Do not touch electrical equipment if wet or standing in water.</li>
        </ul>
        <h3>If You Must Evacuate</h3>
        <ul>
          <li>Walk only in areas you can see the ground clearly.</li>
          <li>Never walk, swim, or drive through floodwater.</li>
          <li>30 cm of fast-moving water can sweep a person away; 60 cm can carry a car.</li>
        </ul>
        <h3>Key Takeaway</h3>
        <p>Turn Around, Don''t Drown. Never attempt to cross flooded roads or streams on foot or by vehicle.</p>'
FROM lesson l JOIN learning_module lm ON lm.id = l.module_id JOIN hazard h ON h.id = lm.hazard_id
WHERE h.code = 'FLOOD' AND l.phase = 'DURING';

INSERT INTO lesson_text (lesson_id, language, title, body)
SELECT l.id, 'en', 'Recovering After a Flood',
       '<p>Floodwater carries sewage, chemicals, and other hazards. Recovery requires caution.</p>
        <h3>Health and Safety</h3>
        <ul>
          <li>Do not return home until authorities declare it safe.</li>
          <li>Wear rubber boots and gloves when cleaning — floodwater is contaminated.</li>
          <li>Boil drinking water until the supply is declared safe.</li>
        </ul>
        <h3>Structural Safety</h3>
        <ul>
          <li>Check for structural damage before entering any building.</li>
          <li>Watch for downed power lines and report them immediately.</li>
          <li>Document damage with photos for insurance purposes.</li>
        </ul>
        <h3>Key Takeaway</h3>
        <p>Floodwater is never safe to touch or drink. Always use PPE when cleaning and boil water until cleared by authorities.</p>'
FROM lesson l JOIN learning_module lm ON lm.id = l.module_id JOIN hazard h ON h.id = lm.hazard_id
WHERE h.code = 'FLOOD' AND l.phase = 'AFTER';

-- ── LESSONS — FIRE ───────────────────────────────────────────────

INSERT INTO lesson (module_id, sort_order, phase, status, source_reference, sme_reviewed)
SELECT lm.id, 1, 'BEFORE', 'PUBLISHED', 'NDMA Fire Safety Guidelines (2020)', FALSE
FROM learning_module lm JOIN hazard h ON h.id = lm.hazard_id WHERE h.code = 'FIRE';

INSERT INTO lesson (module_id, sort_order, phase, status, source_reference, sme_reviewed)
SELECT lm.id, 2, 'DURING', 'PUBLISHED', 'NDMA Fire Safety Guidelines (2020)', FALSE
FROM learning_module lm JOIN hazard h ON h.id = lm.hazard_id WHERE h.code = 'FIRE';

INSERT INTO lesson (module_id, sort_order, phase, status, source_reference, sme_reviewed)
SELECT lm.id, 3, 'AFTER', 'PUBLISHED', 'NDMA Fire Safety Guidelines (2020)', FALSE
FROM learning_module lm JOIN hazard h ON h.id = lm.hazard_id WHERE h.code = 'FIRE';

INSERT INTO lesson_text (lesson_id, language, title, body)
SELECT l.id, 'en', 'Fire Prevention and Preparedness',
       '<p>Most fires are preventable. Knowing the risks reduces the chance of a fire starting.</p>
        <h3>Prevention Tips</h3>
        <ul>
          <li>Never leave cooking unattended on a stove.</li>
          <li>Keep flammable materials away from heat sources.</li>
          <li>Check electrical wiring regularly; replace frayed cords.</li>
          <li>Never overload electrical sockets.</li>
        </ul>
        <h3>Prepare Your Space</h3>
        <ul>
          <li>Install smoke detectors on every floor. Test them monthly.</li>
          <li>Keep fire extinguishers accessible and know how to use them (PASS technique).</li>
          <li>Know and practise your building''s evacuation plan and assembly point.</li>
          <li>Keep exit routes clear of obstruction.</li>
        </ul>
        <h3>Key Takeaway</h3>
        <p>Know where fire extinguishers are and practice the evacuation route. Two minutes is the average time to escape a home fire safely.</p>'
FROM lesson l JOIN learning_module lm ON lm.id = l.module_id JOIN hazard h ON h.id = lm.hazard_id
WHERE h.code = 'FIRE' AND l.phase = 'BEFORE';

INSERT INTO lesson_text (lesson_id, language, title, body)
SELECT l.id, 'en', 'Escaping a Fire',
       '<p>When a fire breaks out, every second matters. Stay calm and follow your escape plan.</p>
        <h3>The R.A.C.E Procedure</h3>
        <ol>
          <li><strong>Rescue</strong>: Move anyone in immediate danger if you can do so safely.</li>
          <li><strong>Alert</strong>: Activate the nearest fire alarm. Call 101 (Fire Service).</li>
          <li><strong>Contain</strong>: Close doors to slow the spread of fire and smoke.</li>
          <li><strong>Evacuate</strong>: Leave the building using the nearest safe exit. Do NOT use lifts.</li>
        </ol>
        <h3>If Trapped</h3>
        <ul>
          <li>Close doors between you and the fire.</li>
          <li>Signal from a window by waving a cloth.</li>
          <li>Put wet cloth under the door to block smoke.</li>
          <li>Stay low — smoke rises; cleaner air is near the floor.</li>
        </ul>
        <h3>Using a Fire Extinguisher (PASS)</h3>
        <ul>
          <li><strong>Pull</strong> the safety pin.</li>
          <li><strong>Aim</strong> at the base of the fire.</li>
          <li><strong>Squeeze</strong> the handle.</li>
          <li><strong>Sweep</strong> side to side.</li>
        </ul>
        <h3>Key Takeaway</h3>
        <p>Stay low, move fast, close doors behind you. Never use the lift during a fire.</p>'
FROM lesson l JOIN learning_module lm ON lm.id = l.module_id JOIN hazard h ON h.id = lm.hazard_id
WHERE h.code = 'FIRE' AND l.phase = 'DURING';

INSERT INTO lesson_text (lesson_id, language, title, body)
SELECT l.id, 'en', 'After a Fire',
       '<p>Even after a fire is out, the building and environment may be dangerous.</p>
        <h3>Immediate Actions</h3>
        <ul>
          <li>Do not re-enter a building until the fire brigade declares it safe.</li>
          <li>Seek medical attention for burns or smoke inhalation — even mild symptoms.</li>
          <li>Contact family or friends and let them know you are safe.</li>
        </ul>
        <h3>Structural and Health Risks</h3>
        <ul>
          <li>Smoke and soot contain toxic particles — use a mask when clearing debris.</li>
          <li>Check for structural damage — floors, walls, and roofs may be weakened.</li>
          <li>Do not switch on electricity until an electrician has inspected wiring.</li>
        </ul>
        <h3>Key Takeaway</h3>
        <p>Smoke inhalation is a medical emergency. Seek help immediately even if you feel fine — symptoms can be delayed.</p>'
FROM lesson l JOIN learning_module lm ON lm.id = l.module_id JOIN hazard h ON h.id = lm.hazard_id
WHERE h.code = 'FIRE' AND l.phase = 'AFTER';
