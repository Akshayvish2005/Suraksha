# UI Design and Wireframes

**Version:** 0.1 (Draft) | Mobile-first: every screen is one column on a phone and widens on a laptop.
These are low-fidelity sketches. Turn them into Figma screens (or HTML mock-ups in
`src/frontend/`) for review, then into Thymeleaf templates.

## 1. Design principles

| Principle | What it means |
|---|---|
| Simple and calm | Plain language, one main action per screen, no clutter (NFR-07) |
| Light and fast | Text first, images optional, minimal scripts (NFR-01, FR-LM-07) |
| Accessible | Text at least 16 px and resizable; colour contrast meets WCAG AA; never use colour alone to show meaning; every image has alt text; everything works with the keyboard (NFR-08) |
| Touch-friendly | Buttons and options at least 44 px high |
| Bilingual | Language switch (EN / HI) always visible in the header |
| Safe by design | Every drill and scenario screen shows a clear **"DRILL - THIS IS A PRACTICE"** banner. Alarm visuals must not flash rapidly, and audio alarms are optional and never start automatically at high volume. |

Suggested hazard accents (always paired with an icon and a text label): Earthquake = brown,
Flood = blue, Fire = red/orange.

## 2. Screen list and navigation

| # | Screen | Route | Roles |
|---|---|---|---|
| 1 | Login / Register | `/login`, `/register` | public |
| 2 | Student home | `/home` | S |
| 3 | Module page | `/modules/{id}` | S, T |
| 4 | Lesson | `/lessons/{id}` | S, T |
| 5 | Test (pre/post) | `/modules/{id}/test/{type}` | S |
| 6 | Scenario player | `/scenarios/{id}/play` | S |
| 7 | Drill join / player | `/drills/{id}/play` | S, T |
| 8 | Teacher home: class results | `/teacher/classes/{id}` | T |
| 9 | Assign module | `/teacher/assign` | T, IA |
| 10 | Schedule drill | `/drills/new` | T, IA |
| 11 | Drill report | `/drills/{id}/report` | T, IA |
| 12 | Institution dashboard | `/admin/dashboard` | IA |
| 13 | Users and groups | `/admin/users`, `/admin/groups` | IA |
| 14 | Emergency plan | `/plan` | all (edit: IA) |
| 15 | Content manager | `/content/...` | SA |
| 16 | Institutions | `/system/institutions` | SA |

```
Login -> Home ─┬─> Module page -> Pre-test -> Lessons -> Post-test -> Result
               ├─> Scenario list -> Scenario player -> Result -> Replay
               ├─> My drills -> Drill player -> Result
               └─> Emergency plan (view)

Teacher home ─┬─> Class results
              ├─> Assign module
              └─> Schedule drill -> Drill report

Admin dashboard ─┬─> Readiness details + export
                 ├─> Users and groups
                 ├─> Drill history -> Drill report
                 └─> Emergency plan (edit)
```

## 3. Wireframes

### W1 — Login (FR-UM-03)

```
+--------------------------------------------+
| Suraksha                          [EN | HI] |
+--------------------------------------------+
|                                            |
|           Be ready. Stay safe.             |
|                                            |
|   Username  [_________________________]    |
|   Password  [_________________________]    |
|                                            |
|   [            Log in                ]     |
|                                            |
|   Forgot password?   Register with code    |
+--------------------------------------------+
```

### W2 — Student home (US-02, US-08, US-10)

```
+--------------------------------------------+
| Suraksha    Hi, Asha      (2)bell    [EN|HI]|
+--------------------------------------------+
| CONTINUE LEARNING                          |
| +----------------------------------------+ |
| | Fire Safety - Lesson 3 of 6            | |
| | [=========>          ] 50%  [Continue] | |
| +----------------------------------------+ |
|                                            |
| UPCOMING DRILL                             |
| +----------------------------------------+ |
| | Earthquake Drill                       | |
| | Opens 5 Oct, 10:00     [Join] (locked) | |
| +----------------------------------------+ |
|                                            |
| MY MODULES                                 |
| [Earthquake  done ] [Flood  0%] [Fire 50%] |
|                                            |
| SCENARIOS                                  |
| [Play: Earthquake in the Classroom]        |
|                                            |
| MY RESULTS   Pre-test 40%  ->  Post 85%    |
|              Improvement: +45%             |
+--------------------------------------------+
| Low-bandwidth mode: [ ON / off ]           |
+--------------------------------------------+
```

### W3 — Module page (FR-LM-02, FR-AS-01/02)

```
+--------------------------------------------+
| < Back            FIRE SAFETY              |
+--------------------------------------------+
| Progress: [=======>        ] 3 of 8 steps  |
|                                            |
| 1. Pre-test (5 questions)        done      |
| BEFORE a fire                              |
| 2. Causes and prevention         done      |
| 3. Know your exits               done      |
| DURING a fire                              |
| 4. Raise the alarm and evacuate  [Start]   |
| 5. Smoke and heat safety         locked    |
| AFTER a fire                               |
| 6. Roll call and first aid       locked    |
| 7. Post-test (10 questions)      locked    |
+--------------------------------------------+
```

### W4 — Lesson (FR-LM-03, FR-LM-04)

```
+--------------------------------------------+
| < Fire Safety             Lesson 4 of 6    |
+--------------------------------------------+
| Raise the alarm and evacuate               |
|                                            |
| +----------------------------------------+ |
| |            [ image / diagram ]         | |
| |   (alt text: people leaving a room)    | |
| +----------------------------------------+ |
|                                            |
| Text of the lesson in short paragraphs     |
| and numbered steps...                      |
|                                            |
| Source: NDMA guidance (reviewed)           |
|                                            |
| [< Previous]                    [Next >]   |
+--------------------------------------------+
```

### W5 — Test question (FR-AS-01 to AS-03)

```
+--------------------------------------------+
| Post-test: Fire Safety        Question 3/10|
+--------------------------------------------+
| What should you do if your clothes catch   |
| fire?                                      |
|                                            |
|   ( ) Run to the nearest exit              |
|   ( ) Stop, drop and roll                  |
|   ( ) Take off your bag and wait           |
|   ( ) Pour water on a nearby wire          |
|                                            |
| [        Check answer        ]             |
|                                            |
| (after answering)                          |
| Correct.  Explanation: rolling smothers... |
| [ Next question > ]                        |
+--------------------------------------------+
```

### W6 — Scenario player (FR-SS-01 to SS-04, SS-07)

```
+--------------------------------------------+
| ***  DRILL - THIS IS A PRACTICE  ***       |
+--------------------------------------------+
| Earthquake in the Classroom     Step 1 / 4 |
|                                            |
| You are in a maths lesson. The floor       |
| suddenly starts shaking and desks rattle.  |
| What do you do FIRST?                      |
|                                            |
| Time left:  [======>      ]  7 s           |
|                                            |
| [ A. Drop, take cover and hold on        ] |
| [ B. Run out of the classroom            ] |
| [ C. Stand by the window                 ] |
+--------------------------------------------+
```

After choosing an option:

```
+--------------------------------------------+
| Correct!   +10 points                      |
| Drop, Cover and Hold On protects you from  |
| falling objects while the shaking goes on. |
|                                            |
| [           Next step >              ]     |
+--------------------------------------------+
```

Final result:

```
+--------------------------------------------+
| RESULT: Well prepared         Score 40/40  |
| "You made the right decisions..."          |
|                                            |
| Step 1  Correct   4.2 s                    |
| Step 2  Correct   6.0 s                    |
| Step 3  Wrong     9.1 s   (see feedback)   |
| Step 4  Correct   5.5 s                    |
|                                            |
| [ Replay ]   [ Back to home ]              |
+--------------------------------------------+
```

### W7 — Drill screen (FR-VD-03, FR-VD-08)

```
+--------------------------------------------+
| ***  DRILL - THIS IS A PRACTICE  ***       |
+--------------------------------------------+
| Term 2 Earthquake Drill                    |
| Your role: Student          Window: open   |
| Closes today at 18:00                      |
|                                            |
| [        Start drill        ]              |
|                                            |
| (later) Choose your evacuation route:      |
|  ( ) Route A - Stairs 1 -> Main ground     |
|  ( ) Route B - Stairs 2 -> Back ground     |
|  Time taken: 00:48                         |
+--------------------------------------------+
```

### W8 — Teacher: class results (US-11, US-12)

```
+--------------------------------------------+
| Class 8B                     [Assign module]|
+--------------------------------------------+
| Filter: Hazard [All v]   Sort: [Name v]    |
|                                            |
| Student      Modules  Pre   Post   Change  |
| ------------------------------------------ |
| Asha K.      3 / 3    40%   85%    +45     |
| Ravi S.      1 / 3    55%   --     --      |
| Meena P.     0 / 3    --    --     --      |
| ------------------------------------------ |
| Class average         48%   85%            |
|                                            |
| [Schedule drill]     [Export CSV]          |
+--------------------------------------------+
```

### W9 — Schedule a drill (US-13, US-16)

```
+--------------------------------------------+
| New virtual drill                          |
+--------------------------------------------+
| Title      [ Term 2 Earthquake Drill    ]  |
| Hazard     [ Earthquake v ]                |
| Scenario   [ Earthquake in the Classroom v]|
| Opens      [ 05 Oct 2026 ] [ 10:00 ]       |
| Closes     [ 05 Oct 2026 ] [ 18:00 ]       |
| Who        (o) Selected groups             |
|            (  ) Whole campus               |
|            [x] Class 8A  [x] Class 8B      |
|            [ ] Class 9A  [ ] Class 9B      |
| Roles (optional)  [ + Assign warden ]      |
|                                            |
| [ Cancel ]                 [ Schedule ]    |
+--------------------------------------------+
```

### W10 — Institution dashboard (US-17)

```
+--------------------------------------------+
| Dashboard - Sunrise Public School          |
+--------------------------------------------+
| READINESS INDEX                            |
|        72 / 100      Partially prepared    |
| Training 65%   Knowledge 78%   Drills 74%  |
|                                            |
| BY HAZARD                                  |
| Earthquake  [==========>     ]  81%        |
| Flood       [=====>          ]  52%        |
| Fire        [=========>      ]  74%        |
|                                            |
| ATTENTION NEEDED                           |
| ! Class 9A - no modules completed          |
| ! Flood module - low participation         |
|                                            |
| RECENT DRILLS                              |
| 5 Oct Earthquake  82% participated  [View] |
|                                            |
| [Export PDF]  [Export CSV]  [Schedule drill]|
+--------------------------------------------+
```

### W11 — Drill report (US-14)

```
+--------------------------------------------+
| Report: Term 2 Earthquake Drill            |
+--------------------------------------------+
| Assigned 71   Completed 58   Missed 10     |
| Participation 82%    Avg score 74%         |
| Avg response time 6.8 s                    |
|                                            |
| MOST-MISSED STEPS                          |
| Step 2  "After shaking stops"   21 wrong   |
| Step 3  "Lift or stairs"        14 wrong   |
|                                            |
| PARTICIPANTS                               |
| Name       Group   Status     Score        |
| A. Sharma  8B      Completed  75%          |
| ...                                        |
| [Export PDF]  [Export CSV]                 |
+--------------------------------------------+
```

### W12 — Emergency plan (US-19)

```
+--------------------------------------------+
| Emergency plan                     [Edit]  |
+--------------------------------------------+
| EMERGENCY NUMBERS                          |
| 112 National emergency   100 Police        |
| 101 Fire                 108 Ambulance     |
| Principal: +91 ..........                  |
|                                            |
| EVACUATION ROUTES AND ASSEMBLY POINTS      |
| Route A - Block 1, Stairs 1 -> Main ground |
| Route B - Block 2, Stairs 2 -> Back ground |
|                                            |
| SAFETY CHECKLIST      4 of 6 done          |
| [x] Exits unlocked and clear               |
| [ ] Fire extinguishers checked             |
|                                            |
| [Download PDF]                             |
+--------------------------------------------+
```

### W13 — Content manager (SA) (US-21 to US-23)

```
+--------------------------------------------+
| Content > Lessons > Fire Safety            |
+--------------------------------------------+
| # | Title (EN)          | HI | Reviewed |  |
| 1 | Causes, prevention  | ok | yes      |[Edit]
| 2 | Know your exits     | -- | yes      |[Edit]
| 3 | Raise the alarm     | ok | NO       |[Edit]
|                                            |
| [+ Add lesson]                             |
| Status: DRAFT / PUBLISHED  (publish is     |
| disabled until "SME reviewed" is ticked)   |
+--------------------------------------------+
```

## 4. Next steps for the UI

1. Review these sketches with your guide and two or three students or teachers.
2. Build clickable mock-ups in Figma, or static HTML in `src/frontend/`.
3. Choose a small colour palette and check contrast (for example with a WCAG contrast checker).
4. Convert the approved screens into Thymeleaf templates in Phase 4.
