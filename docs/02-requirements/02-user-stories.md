# User Stories

Format: **As a** *role*, **I want** *goal*, **so that** *benefit*.
Priority: **M** Must, **S** Should, **C** Could. "FR" links to `01-srs.md`.

---

## Student

| ID | Story | Acceptance criteria | Pri | FR |
|---|---|---|---|---|
| US-01 | As a student, I want to log in with my school/college account so that my progress is saved. | • Valid login opens my dashboard • Wrong password shows a clear error • Logout ends my session | M | UM-03 |
| US-02 | As a student, I want to see modules for earthquake, flood and fire so that I can choose what to learn. | • Three hazards are listed • Each shows my progress (not started / in progress / done) | M | LM-01, LM-04 |
| US-03 | As a student, I want to read short lessons on what to do before, during and after a disaster so that I know how to act. | • Lessons are in order • Each has text and images • Next/Previous works • I can resume later | M | LM-02, LM-03 |
| US-04 | As a student, I want to take a pre-test and a post-test so that I can see how much I learned. | • Pre-test comes before lessons • Post-test after • My improvement is shown as a percentage | M | AS-01, AS-02, AS-05 |
| US-05 | As a student, I want instant feedback with explanations on each answer so that I learn from mistakes. | • Correct/incorrect shown after answering • A short explanation is displayed | M | AS-03 |
| US-06 | As a student, I want to play an emergency scenario and make decisions so that I practise under realistic conditions. | • Each step shows a situation with options • My choice changes what happens next • I get feedback and a final score | M | SS-01–SS-04 |
| US-07 | As a student, I want to replay a scenario so that I can improve my score. | • Replay is available at the end • Previous and new scores are both shown | S | SS-08 |
| US-08 | As a student, I want to join a virtual drill when my teacher schedules it so that I practise with my class. | • I see a notification • I can join in the time window • My actions are recorded | M | VD-02, VD-03 |
| US-09 | As a student, I want to use the app in Hindi so that I understand it easily. | • Language toggle available • Lessons and quizzes appear in the chosen language | S | UM-08, LM-06 |
| US-10 | As a student on a slow phone connection, I want a light version so that pages load quickly. | • Low-bandwidth mode loads text first • Images load only on request | S | LM-07 |

## Teacher / faculty

| ID | Story | Acceptance criteria | Pri | FR |
|---|---|---|---|---|
| US-11 | As a teacher, I want to assign a module to my class with a due date so that everyone completes it. | • I pick class, module and date • Students see the assignment | M | LM-05 |
| US-12 | As a teacher, I want to see who has completed modules and their scores so that I can help students who are behind. | • Class list with status and scores • Filter by hazard | M | DR-02 |
| US-13 | As a teacher, I want to schedule a drill for my class so that students practise the response. | • I select scenario, time window and class • Students are notified | M | VD-01, VD-02 |
| US-14 | As a teacher, I want to see a drill report so that I know what to improve. | • Participation %, average response time and common mistakes are shown | M | VD-05 |

## Institution admin (principal / safety officer)

| ID | Story | Acceptance criteria | Pri | FR |
|---|---|---|---|---|
| US-15 | As an institution admin, I want to add users and classes (or import a CSV) so that everyone can access the system. | • I can create/edit/deactivate users • I can group users • CSV import shows errors for bad rows | M / S | UM-02, UM-05, UM-06 |
| US-16 | As an institution admin, I want to schedule a campus-wide drill so that the whole institution practises together. | • I can select "whole campus" • All users are notified | M | VD-01 |
| US-17 | As an institution admin, I want a dashboard with a readiness score so that I can see how prepared we are. | • Shows Readiness Index and band • Shows participation, scores, drill history | M | DR-01, DR-04 |
| US-18 | As an institution admin, I want to export reports so that I can share them with management or authorities. | • PDF and CSV download • Filters apply to the export | S | DR-06 |
| US-19 | As an institution admin, I want to maintain our emergency plan, routes and contacts so that everyone can find them in one place. | • Editable plan template • Assembly points saved • Contact list with 112 pre-filled • Printable PDF | S | EP-01–EP-05 |

## System admin / content manager

| ID | Story | Acceptance criteria | Pri | FR |
|---|---|---|---|---|
| US-20 | As a system admin, I want to add and manage institutions so that new schools and colleges can be onboarded. | • Create, edit, deactivate institutions | M | UM-01 |
| US-21 | As a content manager, I want to add or edit lessons and quiz questions so that content stays accurate and up to date. | • Create/edit/delete lessons and questions • Draft vs published state | M / S | CM-01, CM-02, CM-04 |
| US-22 | As a content manager, I want to add new scenarios without changing code so that we can expand the library. | • Scenario added through a form or structured file • Appears in the scenario list after publishing | S | SS-06, CM-03 |
| US-23 | As a content manager, I want to record the source and SME review of each lesson so that safety content is trustworthy. | • Source field • "Reviewed by / date" field • Only reviewed lessons can be published | S | CM-05, CM-06, NFR-12 |
