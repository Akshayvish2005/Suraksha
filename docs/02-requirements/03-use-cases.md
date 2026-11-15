# Use Cases

## Actors
Student (S), Teacher (T), Institution Admin (IA), System Admin (SA).

## Use case list

| ID | Use case | Primary actor | Pri |
|---|---|---|---|
| UC-01 | Log in | All | M |
| UC-02 | Complete a learning module | S | M |
| UC-03 | Take pre-test / post-test | S | M |
| UC-04 | Play a scenario simulation | S | M |
| UC-05 | Assign a module to a class | T | M |
| UC-06 | Schedule a virtual drill | T / IA | M |
| UC-07 | Participate in a virtual drill | S | M |
| UC-08 | View drill report | T / IA | M |
| UC-09 | View readiness dashboard | IA | M |
| UC-10 | Manage content | SA | M |
| UC-11 | Manage emergency plan | IA | S |
| UC-12 | Manage users and groups | IA | M |

---

## UC-01 Log in
- **Actor:** any user
- **Precondition:** account exists and is active
- **Main flow:** 1. User opens the site. 2. Enters login ID and password. 3. System validates. 4. System opens the dashboard for the user's role.
- **Alternate:** 3a. Invalid credentials → error shown, user retries. 3b. Account inactive → message to contact institution admin. 3c. Forgot password → reset flow.
- **Postcondition:** user has an authenticated session.

## UC-02 Complete a learning module
- **Actor:** Student
- **Precondition:** logged in; module available
- **Main flow:** 1. Student selects a hazard module. 2. System shows the pre-test (UC-03). 3. Student reads lessons in order. 4. System saves progress after each lesson. 5. Student takes the post-test. 6. System marks the module complete and shows improvement.
- **Alternate:** 3a. Student leaves midway → progress saved; resumes later. 3b. Low-bandwidth mode on → images hidden until requested. 5a. Score below pass mark → student may review lessons and retry.
- **Postcondition:** completion, scores and time recorded.

## UC-03 Take pre-test / post-test
- **Actor:** Student
- **Main flow:** 1. System shows questions one at a time. 2. Student answers. 3. System shows correct/incorrect with explanation. 4. At the end system shows total score.
- **Alternate:** 2a. Student closes browser → attempt saved and can be resumed.
- **Postcondition:** attempt stored with score and timestamp.

## UC-04 Play a scenario simulation
- **Actor:** Student
- **Precondition:** scenario published
- **Main flow:** 1. Student selects a scenario. 2. System shows the first situation and options. 3. Student chooses an option (timed if enabled). 4. System gives feedback and moves to the next situation. 5. Steps 3–4 repeat until an end state. 6. System shows final score and decision summary.
- **Alternate:** 3a. Timer expires → system applies the "no action" outcome. 6a. Student chooses replay.
- **Postcondition:** score and decisions saved.

## UC-05 Assign a module to a class
- **Actor:** Teacher
- **Main flow:** 1. Teacher selects module. 2. Chooses class/group and due date. 3. Confirms. 4. System notifies students.
- **Postcondition:** assignment appears on students' dashboards and teacher's tracking view.

## UC-06 Schedule a virtual drill
- **Actor:** Teacher or Institution Admin
- **Main flow:** 1. Actor chooses hazard and scenario. 2. Sets date and time window. 3. Selects participants (class, group or whole campus) and optionally assigns roles. 4. Confirms. 5. System saves the drill and sends notifications.
- **Alternate:** 4a. Actor edits, reschedules or cancels before the start → participants notified.
- **Postcondition:** drill is scheduled.

## UC-07 Participate in a virtual drill
- **Actor:** Student (or staff in a role)
- **Precondition:** drill scheduled; user assigned; time window open
- **Main flow:** 1. User receives notification and opens the drill. 2. System shows the alert (e.g. alarm and message). 3. User makes decisions step by step (and optionally picks an evacuation route/assembly point). 4. System records each decision and response time. 5. System shows the user's result.
- **Alternate:** 1a. User misses the window → recorded as "not participated". 3a. Connection lost → system resumes at the last step if window still open.
- **Postcondition:** participation, decisions and timings stored.

## UC-08 View drill report
- **Actor:** Teacher / Institution Admin
- **Precondition:** drill window has ended
- **Main flow:** 1. Actor opens drill history. 2. Selects a drill. 3. System shows participation %, average response time, common mistakes and per-participant results. 4. Actor may export as PDF/CSV.
- **Postcondition:** report viewed/exported.

## UC-09 View readiness dashboard
- **Actor:** Institution Admin
- **Main flow:** 1. Admin opens dashboard. 2. System calculates and shows Readiness Index and band. 3. Admin filters by hazard, class or date range. 4. Admin drills into weak areas (e.g. a class with low scores).
- **Postcondition:** none (read-only).

## UC-10 Manage content
- **Actor:** System Admin / content manager
- **Main flow:** 1. Admin opens content manager. 2. Creates or edits a lesson, question or scenario. 3. Adds source and SME review details. 4. Saves as draft. 5. Publishes after review.
- **Alternate:** 5a. Not reviewed → publishing blocked.
- **Postcondition:** published content visible to learners.

## UC-11 Manage emergency plan
- **Actor:** Institution Admin
- **Main flow:** 1. Admin opens the plan page. 2. Fills the template (roles, routes, assembly points). 3. Edits emergency contacts. 4. Saves. 5. Exports as PDF.
- **Postcondition:** plan stored and viewable by all institution users.

## UC-12 Manage users and groups
- **Actor:** Institution Admin
- **Main flow:** 1. Admin opens user management. 2. Adds a user or uploads a CSV. 3. Assigns role and group. 4. Saves. 5. System reports any invalid rows.
- **Postcondition:** users can log in.
