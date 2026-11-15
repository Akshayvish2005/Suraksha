# Software Requirements Specification (SRS)

**Project:** Disaster Preparedness and Response Education System (Suraksha)
**Version:** 0.1 (Draft) | **Date:** 20 September 2026 | **Phase:** 2 — Requirements

---

## 1. Introduction

### 1.1 Purpose
This document states **what** the system must do and the qualities it must have. It is the
agreed reference for design, development and testing. It is based on the Project Charter
(`docs/01-project-initiation/`).

### 1.2 Scope
A web-based platform that delivers disaster-preparedness learning modules, assessments,
interactive scenario simulations and virtual drills for schools and colleges, with readiness
reporting for administrators. Hazards in Phase 1: **earthquake, flood, fire**.

### 1.3 Definitions

| Term | Meaning |
|---|---|
| **Module** | A learning unit on one hazard, made of ordered lessons plus assessments |
| **Lesson** | One page/section of learning content |
| **Scenario** | An interactive, decision-based emergency situation with branching outcomes |
| **Virtual drill** | A scheduled exercise where assigned participants respond to a scenario online, and their actions and timings are recorded |
| **Readiness Index** | A single 0–100 score summarising an institution's (or class's) preparedness |
| **Institution** | A school or college registered on the system |
| **SME** | Subject-matter expert who reviews safety content |

### 1.4 References
- Project Charter, Purpose, Objectives and Scope documents (this project)
- Disaster Management Act, 2005
- NDMA guidelines on school safety (to be cited in final report)

---

## 2. Overall description

### 2.1 Product perspective
A stand-alone, browser-based system with a student interface, a teacher interface, an
institution-admin interface and a system-admin interface, backed by a database and a
scenario/drill engine. No integration with external systems in Phase 1.

### 2.2 User classes

| Role | Description | Technical skill |
|---|---|---|
| Student | School or college learner (age about 11 and above) | Low–medium |
| Teacher / faculty | Assigns content, leads drills, reviews class results | Low–medium |
| Institution admin | Principal, safety officer or coordinator; manages users, drills, plan, reports | Medium |
| System admin | Project team; manages institutions, content, settings | High |

### 2.3 Operating environment
Modern browsers (Chrome, Edge, Firefox, Safari) on Windows, Android and iOS; basic
smartphones (about 2 GB RAM) and slow mobile networks must be supported.

### 2.4 Constraints
16-week schedule; small team; free/open-source tools preferred; content must follow
official guidance; student data (including minors) must be protected.

### 2.5 Assumptions and dependencies
Pilot institutions and an SME are available; users have internet access and a device; public
guidelines may be used with credit.

---

## 3. Functional requirements

**Priority:** M = Must, S = Should, C = Could. **Obj** = objective from `02-objectives.md`.

### 3.1 User and institution management (UM)

| ID | Requirement | Pri | Obj |
|---|---|---|---|
| FR-UM-01 | The system admin shall be able to create and manage institution accounts. | M | — |
| FR-UM-02 | Users shall be able to register or be created using an institution code / by the institution admin. | M | — |
| FR-UM-03 | Users shall be able to log in and log out securely. | M | — |
| FR-UM-04 | The system shall enforce role-based access (student, teacher, institution admin, system admin). | M | — |
| FR-UM-05 | The institution admin shall be able to organise users into classes, departments or groups. | M | O3 |
| FR-UM-06 | The institution admin shall be able to bulk-import users from a CSV file. | S | — |
| FR-UM-07 | Users shall be able to reset a forgotten password. | S | — |
| FR-UM-08 | Users shall be able to choose their language (English / Hindi). | S | O1 |
| FR-UM-09 | The system shall record institutional/parental consent for student accounts where required. | S | — |

### 3.2 Learning modules (LM)

| ID | Requirement | Pri | Obj |
|---|---|---|---|
| FR-LM-01 | The system shall list available modules grouped by hazard. | M | O1 |
| FR-LM-02 | Each module shall contain ordered lessons covering *before, during and after* the hazard. | M | O1 |
| FR-LM-03 | Lessons shall support text, images and optional short animation/video. | M | O1 |
| FR-LM-04 | The system shall track lesson and module completion per learner and let them resume where they left off. | M | O1 |
| FR-LM-05 | A teacher shall be able to assign a module to a class/group with a due date. | M | O1 |
| FR-LM-06 | Module content shall be available in English and Hindi. | S | O1 |
| FR-LM-07 | A low-bandwidth mode shall load text first and make images optional. | S | O7 |
| FR-LM-08 | Learners shall be able to download/print a one-page safety summary card per hazard. | C | O1 |

### 3.3 Assessment (AS)

| ID | Requirement | Pri | Obj |
|---|---|---|---|
| FR-AS-01 | Each module shall have a pre-test taken before the lessons. | M | O4 |
| FR-AS-02 | Each module shall have a post-test taken after the lessons. | M | O4 |
| FR-AS-03 | The system shall score answers automatically and show explanations for correct answers. | M | O4 |
| FR-AS-04 | The system shall store every attempt with score, date and time. | M | O4 |
| FR-AS-05 | The system shall show the learner (and teacher) the pre-test vs post-test improvement. | M | O4 |
| FR-AS-06 | Supported question types shall include multiple-choice and true/false. | M | O4 |
| FR-AS-07 | Multi-select and scenario-based questions shall also be supported. | S | O4 |
| FR-AS-08 | An admin shall be able to set a pass mark and allow retries. | S | O4 |
| FR-AS-09 | Question and answer order may be randomised. | C | O4 |
| FR-AS-10 | The system may issue a completion certificate. | C | O4 |

### 3.4 Scenario simulation (SS)

| ID | Requirement | Pri | Obj |
|---|---|---|---|
| FR-SS-01 | The system shall present a scenario as a sequence of situations, each with decision options. | M | O2 |
| FR-SS-02 | The next situation shall depend on the option the learner chooses (branching). | M | O2 |
| FR-SS-03 | After each decision the system shall give immediate feedback explaining why it was right or wrong. | M | O2 |
| FR-SS-04 | The system shall calculate and display a final score and a summary of decisions. | M | O2 |
| FR-SS-05 | At least three scenarios shall be provided — one each for earthquake, flood and fire. | M | O2 |
| FR-SS-06 | Scenarios shall be stored as structured data so new ones can be added without changing code. | S | O2 |
| FR-SS-07 | Selected decisions shall be timed to simulate pressure. | S | O2 |
| FR-SS-08 | Learners shall be able to replay a scenario and see how their score changes. | S | O2 |
| FR-SS-09 | Scenarios may include visual and audio cues (e.g. alarm, siren). | C | O2 |

### 3.5 Virtual drills (VD)

| ID | Requirement | Pri | Obj |
|---|---|---|---|
| FR-VD-01 | An institution admin or teacher shall be able to create a drill by choosing hazard, scenario, date/time window and participants (class, group or whole campus). | M | O3 |
| FR-VD-02 | Assigned participants shall be notified about the drill. | M | O3 |
| FR-VD-03 | A participant shall be able to join the drill in its time window and respond to the scenario steps. | M | O3 |
| FR-VD-04 | The system shall record each participant's decisions and response times. | M | O3 |
| FR-VD-05 | The system shall generate a drill report (participation, average response time, common mistakes). | M | O3 |
| FR-VD-06 | The system shall keep a history of all drills. | M | O3 |
| FR-VD-07 | Drills shall support participant roles (student, teacher, floor warden, fire marshal) with role-specific steps. | S | O3 |
| FR-VD-08 | Participants shall be able to practise evacuation by choosing a route and assembly point on a floor plan/map. | S | O3, O6 |
| FR-VD-09 | Drill creators shall be able to reschedule or cancel a drill before it starts. | S | O3 |
| FR-VD-10 | A synchronised "live" mode, where all participants start together, may be provided. | C | O3 |

### 3.6 Emergency plan tools (EP)

| ID | Requirement | Pri | Obj |
|---|---|---|---|
| FR-EP-01 | The institution admin shall be able to fill in and edit an emergency plan from a template. | S | O6 |
| FR-EP-02 | The admin shall be able to record evacuation routes and assembly points (text and/or uploaded image). | S | O6 |
| FR-EP-03 | The system shall keep an emergency contact directory, pre-filled with national numbers (e.g. 112) and editable for local contacts. | S | O6 |
| FR-EP-04 | The system shall provide a safety checklist (e.g. exits clear, extinguishers checked) that can be completed and saved. | C | O6 |
| FR-EP-05 | The plan shall be exportable/printable as PDF. | S | O6 |

### 3.7 Dashboard and reporting (DR)

| ID | Requirement | Pri | Obj |
|---|---|---|---|
| FR-DR-01 | The institution admin shall see a dashboard of participation %, average scores and drill history. | M | O5 |
| FR-DR-02 | A teacher shall see results for each of their classes. | M | O5 |
| FR-DR-03 | A student shall see their own progress, scores and badges/completions. | M | O5 |
| FR-DR-04 | The system shall compute and display a Readiness Index (see 3.10). | M | O5 |
| FR-DR-05 | Reports shall be filterable by hazard, class and date range. | S | O5 |
| FR-DR-06 | Reports shall be exportable as PDF and CSV. | S | O5 |
| FR-DR-07 | The system admin may view aggregate statistics across institutions. | C | O5 |

### 3.8 Notifications (NT)

| ID | Requirement | Pri | Obj |
|---|---|---|---|
| FR-NT-01 | The system shall show in-app notifications for new assignments and upcoming drills. | S | O3 |
| FR-NT-02 | The system shall remind users of pending modules and drills. | S | O3 |
| FR-NT-03 | The system may send e-mail notifications. | C | O3 |

### 3.9 Content management (CM)

| ID | Requirement | Pri | Obj |
|---|---|---|---|
| FR-CM-01 | An authorised admin shall be able to create, edit and delete lessons. | M | O1 |
| FR-CM-02 | An authorised admin shall be able to create, edit and delete quiz questions. | M | O4 |
| FR-CM-03 | An authorised admin shall be able to add or edit scenarios (form or structured file import). | S | O2 |
| FR-CM-04 | Content shall have draft and published states. | S | O1 |
| FR-CM-05 | Each lesson shall store its source/reference. | S | O1 |
| FR-CM-06 | Each lesson shall have an "SME reviewed" flag and reviewer/date. | S | O1 |

### 3.10 Readiness Index (proposed formula — to be validated with the SME)

```
Readiness Index (0-100) =
      40% x Training completion   (% of assigned modules completed)
    + 30% x Knowledge score       (average post-test score)
    + 30% x Drill performance     (participation % x average decision accuracy)
```

| Index | Band |
|---|---|
| 80–100 | Well prepared |
| 60–79 | Partially prepared |
| Below 60 | Needs attention |

---

## 4. Non-functional requirements

| ID | Category | Requirement |
|---|---|---|
| NFR-01 | Performance | Core pages shall load within **5 seconds** on a typical 3G-speed connection and within 2 seconds on broadband. |
| NFR-02 | Capacity | The system shall support **100 concurrent users** in the pilot (design target: 500). |
| NFR-03 | Availability | At least **95% uptime** during the pilot period. |
| NFR-04 | Reliability | Learner progress shall be saved automatically; no loss of progress on refresh or disconnection. Daily database backup. |
| NFR-05 | Security | Passwords stored hashed; all traffic over HTTPS; role-based authorisation on every request; input validation against injection/XSS; automatic session timeout. |
| NFR-06 | Privacy | Collect only the data needed (name, class, institution, login ID); no location tracking; data deletable on request; data accessible only to the user's own institution. |
| NFR-07 | Usability | A new student shall be able to start a module within **3 clicks** of logging in; simple, age-appropriate language. |
| NFR-08 | Accessibility | Target WCAG 2.1 Level AA: sufficient colour contrast, resizable text, keyboard navigation, alt text on images. |
| NFR-09 | Compatibility | Works on the latest two versions of Chrome, Edge, Firefox and Safari, and on Android phones with about 2 GB RAM. |
| NFR-10 | Localisation | UTF-8 throughout; English and Hindi supported; design allows more languages later. |
| NFR-11 | Maintainability | Modular code; content and scenarios stored as data; documented code and APIs. |
| NFR-12 | Content accuracy | Every safety lesson shall be traceable to an official source and reviewed by an SME before publishing. |
| NFR-13 | Scalability | Architecture shall allow adding hazards, institutions and languages without redesign. |

---

## 5. Data requirements (main entities)

| Entity | Key information |
|---|---|
| Institution | name, type (school/college), address/state, contact, code |
| User | name, login ID, role, institution, group, language, consent status |
| Group | name (class/department), institution |
| Module / Lesson | hazard, title, language, order, content, source, review status |
| Question / Attempt | question text, options, correct answer, explanation; user, score, timestamp |
| Scenario / Step / Option | hazard, narrative, decision options, next step, points, feedback |
| Drill / Participant / Event | scenario, time window, participants, role, decisions, response times |
| Notification | user, message, type, read status |
| Emergency plan / Contact | institution, routes, assembly points, contact name/number |

---

## 6. External interfaces

- **User interface:** responsive web pages, mobile-first, with English/Hindi toggle.
- **Hardware:** none beyond a device with a browser.
- **External systems:** none in Phase 1. Optional e-mail service for FR-NT-03.

---

## 7. Open questions (to settle with the guide/team)

1. Final technology stack (front end, back end, database)? *(Charter lists a tentative stack.)*
2. Login method: institution ID, e-mail, or username? Minimum student age supported?
3. Is a synchronised live drill (FR-VD-10) needed for the demo, or is a self-paced drill window enough?
4. Who will translate and review the Hindi content?
5. Which institution(s) will be the pilot, and what consent do they require?
6. Is the Readiness Index formula acceptable, or should the weights change?
