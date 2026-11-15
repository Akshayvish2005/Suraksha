# System Architecture

**Project:** Suraksha (DPRES) | **Version:** 0.1 (Draft) | **Phase:** 3 — Design
Input documents: `docs/02-requirements/01-srs.md` (FR-* and NFR-* IDs are referenced below).

---

## 1. Architecture at a glance

Suraksha is a **single Spring Boot application** (one deployable JAR) that serves web pages
and a JSON REST API, and stores data in one relational database.

```
+-------------------+        HTTPS         +--------------------------------------------+
|  Browser          | <------------------> |  Spring Boot application (one JAR)         |
|  (phone / laptop) |                      |                                            |
|  HTML + CSS + JS  |                      |  Web layer      Thymeleaf pages + REST API |
+-------------------+                      |  Security       login, roles, CSRF         |
                                           |  Service layer  business rules             |
                                           |  Engines        scenario + drill engine    |
                                           |  Reports        readiness index, PDF, CSV  |
                                           |  Scheduler      drill status, reminders    |
                                           |  Data layer     Spring Data JPA            |
                                           +----------------------+---------------------+
                                                                  |
                                                  +---------------+----------------+
                                                  | MySQL  (H2 for dev/test)       |
                                                  +--------------------------------+
                                                  | File storage: uploaded images  |
                                                  +--------------------------------+
```

## 2. Technology stack

| Layer | Choice | Why |
|---|---|---|
| Language | **Java 21 (LTS)** | Team choice; long-term support; works well in IntelliJ |
| Framework | **Spring Boot** (Web, Data JPA, Security, Validation, Thymeleaf) | One framework for pages, API, security and data |
| Build tool | **Maven** | Standard, IntelliJ-friendly |
| Database | **MySQL 8** (pilot); **H2** in-memory (development and tests) | Free, widely taught, easy local setup with XAMPP/MySQL Workbench; H2 needs no installation |
| Front end | **Thymeleaf** templates + **Bootstrap 5** + small vanilla **JavaScript** files | Light pages that load fast on slow networks (NFR-01); no separate front-end project to build and host |
| Authentication | Spring Security, session login, **BCrypt** password hashing, CSRF protection | Fits server-rendered pages (NFR-05) |
| Language support | Spring `MessageSource` files (`messages.properties`, `messages_hi.properties`) for the interface; language-specific rows for learning content | English/Hindi now, more languages later (NFR-10, NFR-13) |
| PDF / CSV export | OpenPDF library for PDF; plain Java for CSV | FR-DR-06, FR-EP-05 |
| Testing | JUnit 5, Mockito, Spring Boot Test | Phase 5 |
| Version control | Git + GitHub | Team collaboration |

> **Spring Boot version:** use the latest stable version offered at start.spring.io. The
> design does not depend on version-specific features. See `05-dev-environment-setup.md`.

## 3. Key design decisions

| # | Decision | Reason |
|---|---|---|
| D1 | **Modular monolith**, not microservices | Small team and 16 weeks; one app is far easier to build, test and deploy |
| D2 | **Server-rendered pages** (Thymeleaf) plus a REST API used by the interactive parts (quiz, simulation, drill player) | Fast on low-end phones; the same API can serve a mobile app later |
| D3 | **Session-based login**, not JWT | Simpler and safer for a server-rendered site |
| D4 | **Scoring is done on the server** | Learners cannot cheat by editing browser code; the answer key never leaves the server before answering |
| D5 | **Content is data**: lessons, questions and scenarios live in the database / JSON, not in code | New content without code changes (FR-SS-06, NFR-11) |
| D6 | **One row per language** for content, linked to a common parent | English first, Hindi added later without schema change |
| D7 | **Self-paced drill window first**; "live" synchronised mode is optional later | Much simpler; meets FR-VD-01 to VD-06 (see SRS open question 3) |
| D8 | **Readiness Index calculated on demand** (with optional daily snapshot for trends) | Data volume is small in a pilot; no extra infrastructure |
| D9 | **Notifications shown in-app** and fetched when a page loads | No WebSocket or push complexity (FR-NT-01, NT-02) |
| D10 | **H2 for development, MySQL for pilot**, selected by Spring profiles | Anyone can run the project immediately; MySQL is what most teams already have installed |

## 4. Layers and rules

```
Browser  ->  Controller  ->  Service  ->  Repository  ->  Database
             (pages, REST)   (rules)      (JPA)
```

| Layer | Responsibility | Must not |
|---|---|---|
| Controller | Receive requests, validate input, call a service, return a page or JSON | Contain business rules or call repositories |
| Service | Business rules, transactions, permission checks, scoring, readiness | Know about HTML or HTTP |
| Repository | Read/write the database | Contain business rules |
| Entity | Database mapping | Be sent directly to the browser (use DTOs) |
| DTO | Data sent to/from the browser (Java `record`s) | Contain secrets such as password hashes or correct-answer flags |

## 5. Code structure (package by feature)

```
com.suraksha
├── SurakshaApplication.java
├── config/          security, i18n, scheduling, web configuration
├── common/          exceptions, global error handler, enums (Role, Language, Hazard), helpers
├── institution/     Institution
├── user/            AppUser, registration, CSV import
├── group/           UserGroup (classes, departments) and members
├── learning/        modules, lessons, progress, assignments
├── assessment/      questions, attempts, scoring, pre/post improvement
├── scenario/        scenario definition parser, ScenarioEngine, attempts, decisions
├── drill/           Drill, participants, DrillService, DrillScheduler, drill report
├── plan/            emergency plan, evacuation routes, contacts, checklist
├── dashboard/       ReadinessService, dashboards, exports
├── notification/    notifications
└── content/         admin content management (lessons, questions, scenarios)
```

Inside each feature package: `XController` (pages), `XApiController` (REST), `XService`,
`XRepository`, `entity/`, `dto/`.

Resources:

```
src/main/resources/
├── application.properties            common settings
├── application-dev.properties        H2 database, H2 console, debug logging
├── application-prod.properties       MySQL, production settings
├── messages.properties               English interface text
├── messages_hi.properties            Hindi interface text
├── templates/                        Thymeleaf pages (layout, fragments, student/, teacher/, admin/)
├── static/                           css/, js/ (quiz.js, scenario-player.js, drill.js), img/
└── scenarios/                        seed scenario JSON files
```

## 6. Security design (FR-UM-03/04, NFR-05/06)

**Roles:** `STUDENT`, `TEACHER`, `INSTITUTION_ADMIN`, `SYSTEM_ADMIN`.

| Capability | Student | Teacher | Inst. admin | System admin |
|---|:-:|:-:|:-:|:-:|
| Learn modules, take tests, play scenarios, join drills | ✔ | ✔ | ✔ | ✔ |
| View own progress | ✔ | ✔ | ✔ | — |
| Assign modules, view class results | — | ✔ | ✔ | — |
| Schedule drills, view drill reports | — | ✔ (own classes) | ✔ | — |
| Manage users, groups, emergency plan | — | — | ✔ | — |
| View institution dashboard and readiness | — | — | ✔ | — |
| Manage institutions | — | — | — | ✔ |
| Manage content (lessons, questions, scenarios) | — | — | — | ✔ |

Rules that apply everywhere:
- **Institution isolation:** every query for institution data is filtered by the logged-in user's `institution_id`. One institution can never see another's data.
- Passwords stored only as BCrypt hashes; HTTPS in the pilot; CSRF protection on all form/API posts; session timeout after 30 minutes idle.
- All input validated on the server (Bean Validation); output escaped by Thymeleaf (protects against XSS); JPA parameter binding (protects against SQL injection).
- Minimum personal data: name, username, class. No location, phone number or photo is required (NFR-06).
- The correct-answer flag and scenario scoring data are **never** sent to the browser before the learner answers.

## 7. Scenario engine design (FR-SS-01 to SS-09)

A scenario is a small **decision graph** stored as JSON (see
`content/drill-scenarios/earthquake-classroom-en.json`).

```
[Step s1] --choose option a--> [Step s2] --choose option a--> [Step s3] ---> [END]
    |                             |
    +--choose option b---------->[Step s1b: feedback] --> [Step s2]
```

Flow for one play-through:

1. `POST /scenario-attempts` — server creates an attempt and returns the first step
   (situation text and option texts only).
2. Browser shows the step (with a countdown if `timeLimitSeconds` is set).
3. `POST /scenario-attempts/{id}/decisions` with `stepId`, `optionId`, `responseTimeMs`.
4. Server looks up the option, awards points, stores the decision, and returns
   **feedback + the next step** (or the final result).
5. When an END step is reached, the server stores the total score and time and returns a
   summary of every decision.

If the timer runs out, the browser sends `timedOut = true`; the server applies the step's
`onTimeout` outcome (FR-SS-07). The same engine is reused by virtual drills.

## 8. Virtual drill design (FR-VD-01 to VD-10)

**Drill status** (changed by the scheduler and by the creator):

```
SCHEDULED --start_time reached--> OPEN --end_time reached--> CLOSED
    |
    +--creator cancels--> CANCELLED
```

**Participant status:** `ASSIGNED` → `JOINED` → `COMPLETED`.
When the drill closes: `ASSIGNED` becomes `MISSED`; `JOINED` (unfinished) becomes `INCOMPLETE`.

Creating a drill: the server resolves the target (whole campus or selected groups) into one
`drill_participant` row per user, sends a notification to each, and stores the role for each
participant. Joining a drill starts a normal scenario attempt linked to the drill
(`scenario_attempt.drill_id`). The **drill report** is calculated from these attempts:
participation %, average response time, most-missed steps (common mistakes), per-participant
results, and evacuation time where a route was practised.

A `@Scheduled` job runs every minute to update statuses and to create reminder notifications
(FR-NT-02).

## 9. Readiness Index design (FR-DR-04)

```
Readiness Index = 0.40 x Training completion
                + 0.30 x Knowledge score
                + 0.30 x Drill performance
```

| Component | Calculation (for a group or a whole institution) |
|---|---|
| Training completion | completed assigned modules ÷ all assigned modules × 100 |
| Knowledge score | average of the latest post-test scores × 1 (0–100) |
| Drill performance | (participants who completed ÷ participants assigned) × (average decision accuracy) × 100, over the last 12 months |

Bands: 80–100 well prepared, 60–79 partially prepared, below 60 needs attention.
The weights are stored in configuration so they can be changed without code changes.

## 10. How the design meets the non-functional requirements

| NFR | Design measure |
|---|---|
| NFR-01 Performance on slow networks | Server-rendered light pages; gzip compression; images lazy-loaded and optional in low-bandwidth mode; static-file caching; no heavy JavaScript framework |
| NFR-02 / 03 Capacity, availability | Stateless controllers; database connection pool; indexes on foreign keys; simple single-server deployment adequate for 100 concurrent users |
| NFR-04 Reliability | Progress and answers saved on every step; daily database backup |
| NFR-05 / 06 Security, privacy | Section 6 |
| NFR-07 Usability | Dashboard shows "Continue learning" first; every main task ≤ 3 clicks (see wireframes) |
| NFR-08 Accessibility | Semantic HTML, sufficient colour contrast, alt text, keyboard navigation, resizable text |
| NFR-09 Compatibility | Responsive Bootstrap layout; tested on Chrome, Edge, Firefox, Safari and a basic Android phone |
| NFR-10 Localisation | UTF-8 everywhere; `messages_*.properties`; language-specific content rows |
| NFR-11 / 13 Maintainability, scalability | Package-by-feature; content as data; hazards stored in a table so new hazards need no code change |
| NFR-12 Content accuracy | `source_reference` and `sme_reviewed` fields; publishing blocked until reviewed |

## 11. Deployment view (pilot)

```
Institution / cloud server
├── Java 21 runtime  ->  suraksha.jar   (Spring profile: prod)
├── MySQL 8           ->  daily backup (mysqldump)
└── Reverse proxy (e.g. Nginx) -> HTTPS certificate
```

For the project demo the app can also run on a laptop with the `dev` profile (H2, no
installation needed).

## 12. Mapping to the repository folders

| Folder | Contents |
|---|---|
| `src/backend/` | The Spring Boot application (Maven project). **Pages, CSS and JavaScript are inside it** under `src/main/resources`. |
| `src/database/` | `schema.sql` (reference schema and seed data) and later scripts |
| `src/simulation/` | Notes and the scenario JSON format; the engine code itself lives in the `scenario` package of the backend |
| `src/frontend/` | Optional: static HTML mock-ups made from the wireframes before they become Thymeleaf templates |
