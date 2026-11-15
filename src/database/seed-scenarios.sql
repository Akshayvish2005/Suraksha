-- =====================================================================
-- Suraksha — Emergency Scenarios Seed (Phase 1: Earthquake, Flood, Fire)
-- Structured JSON definition matching ScenarioDef, ScenarioStep, ScenarioOption
-- =====================================================================

-- ── 1. EARTHQUAKE SCENARIO ──────────────────────────────────────────
INSERT INTO scenario (hazard_id, code, language, title, description, estimated_minutes, definition, status, source_reference, sme_reviewed, reviewed_by, reviewed_on)
SELECT h.id, 'EQ-CLASSROOM', 'en',
       'Classroom Earthquake Crisis',
       'You are in a 3rd floor classroom when strong tremors strike. Windows rattle, ceiling fans sway violently. You have seconds to act.',
       5,
       '{
         "steps": [
           {
             "id": "1",
             "narrative": "Sudden violent tremors shake the classroom floor. Books fall from shelves, and students begin screaming. What is your immediate action?",
             "timerSeconds": 15,
             "options": [
               {
                 "id": "1A",
                 "text": "Drop, Cover, and Hold On under your sturdy classroom desk",
                 "nextStepId": "2A",
                 "correct": true,
                 "points": 30,
                 "feedback": "Excellent! Drop, Cover, and Hold On is the gold standard. Protecting your head and neck prevents over 85% of non-structural injuries."
               },
               {
                 "id": "1B",
                 "text": "Run immediately toward the hallway door and stairs",
                 "nextStepId": "2B",
                 "correct": false,
                 "points": 5,
                 "feedback": "Dangerous! Running during violent shaking causes falls and exposes you to falling lights, ceiling tiles, and glass debris."
               },
               {
                 "id": "1C",
                 "text": "Stand next to the large glass windows to see outside",
                 "nextStepId": "2B",
                 "correct": false,
                 "points": 0,
                 "feedback": "Critically dangerous! Glass windows can shatter under shear stress, causing deep lacerations."
               }
             ]
           },
           {
             "id": "2A",
             "narrative": "The primary tremor ceases after 40 seconds. Dust fills the air and the fire alarm is blaring. The teacher announces evacuation.",
             "timerSeconds": 20,
             "options": [
               {
                 "id": "2A1",
                 "text": "Evacuate calmly using the marked staircase, protecting your head with a notebook or bag",
                 "nextStepId": "3",
                 "correct": true,
                 "points": 35,
                 "feedback": "Correct! Always use stairwells. Head protection guards against loose debris during possible aftershocks."
               },
               {
                 "id": "2A2",
                 "text": "Take the passenger elevator to reach ground floor faster",
                 "nextStepId": "3",
                 "correct": false,
                 "points": 0,
                 "feedback": "Hazardous! Never use elevators during or after earthquakes. Power failure or cable distortion can trap you."
               }
             ]
           },
           {
             "id": "2B",
             "narrative": "You stumbled in the hallway as loose ceiling plaster fell nearby. The main shaking has now stopped, but aftershocks are expected.",
             "timerSeconds": 20,
             "options": [
               {
                 "id": "2B1",
                 "text": "Check yourself for cuts, cover your head, and follow the teacher down the emergency staircase",
                 "nextStepId": "3",
                 "correct": true,
                 "points": 25,
                 "feedback": "Good recovery! Regaining composure and taking the designated staircase is the right choice."
               },
               {
                 "id": "2B2",
                 "text": "Run back into the classroom to fetch your backpack and water bottle",
                 "nextStepId": "3",
                 "correct": false,
                 "points": 0,
                 "feedback": "Incorrect! Never turn back into a potentially compromised building for personal possessions."
               }
             ]
           },
           {
             "id": "3",
             "narrative": "You exit the building onto the school grounds. Crowds are gathering. Where should you assemble?",
             "timerSeconds": 20,
             "options": [
               {
                 "id": "3A",
                 "text": "Move to the open football field, well away from buildings, trees, and overhead electric wires",
                 "nextStepId": null,
                 "correct": true,
                 "points": 35,
                 "feedback": "Perfect! Open fields clear of facades, glass, and electrical poles are safe assembly zones. Report for roll-call immediately."
               },
               {
                 "id": "3B",
                 "text": "Wait directly under the front entrance portico canopy for shade",
                 "nextStepId": null,
                 "correct": false,
                 "points": 5,
                 "feedback": "Unsafe! Overhangs and decorative porticos frequently collapse during aftershocks."
               }
             ]
           }
         ]
       }',
       'PUBLISHED',
       'NDMA School Safety Framework 2021',
       TRUE, 'Dr. R. Sharma (SME)', '2026-09-15'
FROM hazard h WHERE h.code = 'EARTHQUAKE'
ON DUPLICATE KEY UPDATE title = VALUES(title), definition = VALUES(definition), status = VALUES(status);


-- ── 2. FLOOD SCENARIO ───────────────────────────────────────────────
INSERT INTO scenario (hazard_id, code, language, title, description, estimated_minutes, definition, status, source_reference, sme_reviewed, reviewed_by, reviewed_on)
SELECT h.id, 'FLD-CAMPUS', 'en',
       'Monsoon Flash Inundation',
       'Incessant monsoon torrential rain has caused the nearby canal to breach. Muddy floodwater is gushing into the campus ground floor.',
       5,
       '{
         "steps": [
           {
             "id": "1",
             "narrative": "The municipal flood warning siren sounds. Within minutes, brownish stormwater begins surging into the ground floor classrooms and corridors, reaching ankle depth.",
             "timerSeconds": 20,
             "options": [
               {
                 "id": "1A",
                 "text": "Alert peers and systematically evacuate to the 2nd and 3rd floor classrooms",
                 "nextStepId": "2A",
                 "correct": true,
                 "points": 35,
                 "feedback": "Excellent decision! Vertical evacuation to upper floors is the safest protocol when flash floods rapidly enter low-lying premises."
               },
               {
                 "id": "1B",
                 "text": "Wade through the courtyard to reach the bicycle and scooter parking",
                 "nextStepId": "2B",
                 "correct": false,
                 "points": 5,
                 "feedback": "Extremely dangerous! Fast-flowing water just 6 inches deep can knock an adult off their feet; submerged manholes may be open."
               },
               {
                 "id": "1C",
                 "text": "Go down to the basement computer lab to unplug the machines",
                 "nextStepId": "2B",
                 "correct": false,
                 "points": 0,
                 "feedback": "Deadly mistake! Basements can flood to ceiling height in minutes, trapping anyone inside."
               }
             ]
           },
           {
             "id": "2A",
             "narrative": "On the first-floor landing, you observe water nearing a wall-mounted electrical switchboard on the ground floor with sparks visible.",
             "timerSeconds": 20,
             "options": [
               {
                 "id": "2A1",
                 "text": "Warn everyone to stay clear and alert the floor warden/faculty to shut down the main power grid",
                 "nextStepId": "3",
                 "correct": true,
                 "points": 35,
                 "feedback": "Vital action! Electrocution from submerged wiring is a primary killer during indoor flood events."
               },
               {
                 "id": "2A2",
                 "text": "Step into the water to manually pull out power plugs",
                 "nextStepId": "3",
                 "correct": false,
                 "points": 0,
                 "feedback": "Fatal hazard! Water conducts electricity; never touch electrical equipment when standing in or near floodwaters."
               }
             ]
           },
           {
             "id": "2B",
             "narrative": "The current in the courtyard was surprisingly strong, and you slipped. A staff member pulls you up onto the staircase landing.",
             "timerSeconds": 20,
             "options": [
               {
                 "id": "2B1",
                 "text": "Head straight upstairs to join your class cohort on the top floor",
                 "nextStepId": "3",
                 "correct": true,
                 "points": 25,
                 "feedback": "Good recovery. Staying with your group ensures accurate roll calls and orderly assistance."
               },
               {
                 "id": "2B2",
                 "text": "Attempt to walk towards the main road entrance gate alone",
                 "nextStepId": "3",
                 "correct": false,
                 "points": 0,
                 "feedback": "High risk! Road currents are unpredictable and open drains cannot be seen under turbid floodwaters."
               }
             ]
           },
           {
             "id": "3",
             "narrative": "Everyone is gathered safely on the upper floor. The school is surrounded by 3 feet of water. How do you manage the wait for NDRF rescue boats?",
             "timerSeconds": 20,
             "options": [
               {
                 "id": "3A",
                 "text": "Conserve phone battery, drink only sealed bottled water, and await official emergency broadcasts",
                 "nextStepId": null,
                 "correct": true,
                 "points": 30,
                 "feedback": "Superb! Floodwater contaminates tap water pipelines. Conserving phone battery is critical for coordinated rescue."
               },
               {
                 "id": "3B",
                 "text": "Drink from the school water cooler on the floor and swim out to explore",
                 "nextStepId": null,
                 "correct": false,
                 "points": 0,
                 "feedback": "Dangerous! Municipal water lines may be polluted with sewage, causing waterborne cholera/typhoid."
               }
             ]
           }
         ]
       }',
       'PUBLISHED',
       'NDMA Guidelines on Flood Disaster Preparedness',
       TRUE, 'Dr. R. Sharma (SME)', '2026-09-15'
FROM hazard h WHERE h.code = 'FLOOD'
ON DUPLICATE KEY UPDATE title = VALUES(title), definition = VALUES(definition), status = VALUES(status);


-- ── 3. FIRE SCENARIO ────────────────────────────────────────────────
INSERT INTO scenario (hazard_id, code, language, title, description, estimated_minutes, definition, status, source_reference, sme_reviewed, reviewed_by, reviewed_on)
SELECT h.id, 'FIRE-CHEMLAB', 'en',
       'Chemistry Lab Fire Outbreak',
       'A solvent bottle drops and ignites during a lab session. Flames are racing toward reagent cabinets. The room begins filling with dense fumes.',
       5,
       '{
         "steps": [
           {
             "id": "1",
             "narrative": "An open flask of ethanol knocks over and catches fire on the reagent bench. Flames spread quickly across the wooden counter. What should you do first?",
             "timerSeconds": 15,
             "options": [
               {
                 "id": "1A",
                 "text": "Shout fire alert, pull the nearest manual call point (alarm), and evacuate toward the door",
                 "nextStepId": "2A",
                 "correct": true,
                 "points": 35,
                 "feedback": "Spot on! Raising the alarm immediately protects everyone in the building and summons the emergency response team."
               },
               {
                 "id": "1B",
                 "text": "Pour a glass of water from the sink over the chemical solvent fire",
                 "nextStepId": "2B",
                 "correct": false,
                 "points": 0,
                 "feedback": "Dangerous mistake! Pouring water onto flammable liquid fires splatters burning solvent, spreading the fire rapidly."
               },
               {
                 "id": "1C",
                 "text": "Try to blow out the flames with your breath or a notebook",
                 "nextStepId": "2B",
                 "correct": false,
                 "points": 5,
                 "feedback": "Ineffective and dangerous! Fanning provides oxygen to the flames and puts your face right in the fire zone."
               }
             ]
           },
           {
             "id": "2A",
             "narrative": "The fire alarm rings loudly across the science wing. Dense, acrid smoke is accumulating along the hallway ceiling, reducing visibility.",
             "timerSeconds": 15,
             "options": [
               {
                 "id": "2A1",
                 "text": "Crawl low on hands and knees beneath the smoke, covering your mouth and nose with a damp handkerchief",
                 "nextStepId": "3",
                 "correct": true,
                 "points": 35,
                 "feedback": "Correct! Smoke and toxic gases (carbon monoxide) rise. The cleanest breathable air is 12 to 24 inches above the floor."
               },
               {
                 "id": "2A2",
                 "text": "Run upright through the smoke with your eyes closed",
                 "nextStepId": "3",
                 "correct": false,
                 "points": 5,
                 "feedback": "Hazardous! Inhaling hot toxic smoke burns the respiratory tract and causes disorientation within seconds."
               }
             ]
           },
           {
             "id": "2B",
             "narrative": "The chemical fire surged larger. The lab teacher deploys a CO2 fire extinguisher while directing all students out into the smoky corridor.",
             "timerSeconds": 15,
             "options": [
               {
                 "id": "2B1",
                 "text": "Drop low immediately and follow the glowing illuminated exit signs along the baseboard",
                 "nextStepId": "3",
                 "correct": true,
                 "points": 25,
                 "feedback": "Good adaptation. Lowering yourself beneath the thermal layer and following signage is proper procedure."
               },
               {
                 "id": "2B2",
                 "text": "Hide in the lab storage closet until the smoke clears",
                 "nextStepId": "3",
                 "correct": false,
                 "points": 0,
                 "feedback": "Never hide! Closets and washrooms become toxic death traps during building fires."
               }
             ]
           },
           {
             "id": "3",
             "narrative": "You reach the exterior fire escape stairs. The ground level assembly area is clearly marked in green. What is your action upon arrival?",
             "timerSeconds": 20,
             "options": [
               {
                 "id": "3A",
                 "text": "Proceed directly to your class assembly grid and check in with your teacher for roll-call",
                 "nextStepId": null,
                 "correct": true,
                 "points": 30,
                 "feedback": "Outstanding! Roll-call accountability ensures firefighters don’t risk their lives searching for people who are already safe."
               },
               {
                 "id": "3B",
                 "text": "Leave the school premises immediately to go home without telling anyone",
                 "nextStepId": null,
                 "correct": false,
                 "points": 0,
                 "feedback": "Wrong! Leaving without reporting causes panic and sends rescue teams back into burning structures unnecessarily."
               }
             ]
           }
         ]
       }',
       'PUBLISHED',
       'National Building Code (NBC 2016) Part 4 Fire and Life Safety',
       TRUE, 'Chief Fire Officer M. Das', '2026-09-15'
FROM hazard h WHERE h.code = 'FIRE'
ON DUPLICATE KEY UPDATE title = VALUES(title), definition = VALUES(definition), status = VALUES(status);
