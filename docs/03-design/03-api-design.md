# REST API Design

**Version:** 0.1 (Draft) | Base path: `/api/v1` | Format: JSON (UTF-8)

The web pages (Thymeleaf) call this API for the interactive parts: lessons, quizzes,
scenarios, drills, notifications and dashboards.

## 1. Conventions

| Topic | Rule |
|---|---|
| Authentication | Session cookie from the login form; API calls from the pages reuse the session. Include the CSRF token on every `POST/PUT/PATCH/DELETE`. |
| Roles | S = Student, T = Teacher, IA = Institution admin, SA = System admin. The role column shows who may call the endpoint (higher roles noted where they also can). |
| Language | `?lang=en\|hi` (defaults to the user's saved language; falls back to English) |
| Institution scope | Never sent by the client. The server always uses the logged-in user's institution. |
| Success codes | `200 OK`, `201 Created` (with `Location`), `204 No Content` |
| Error codes | `400` invalid input, `401` not logged in, `403` not allowed, `404` not found, `409` conflict (e.g. drill already closed), `500` server error |
| Error body | RFC 7807 problem JSON: `{ "type", "title", "status", "detail", "instance", "errors": [{"field","message"}] }` |
| Lists | Paged: `?page=0&size=20`; response `{ "items": [...], "page": 0, "size": 20, "total": 134 }` |
| Dates | ISO-8601, e.g. `2026-10-05T10:30:00+05:30` |
| Never returned | Password hashes; `correct` flags or scoring data before a question/step has been answered |

## 2. Endpoints

### 2.1 Authentication and profile (FR-UM-02, 03, 07, 08)

| Method | Path | Role | Purpose |
|---|---|---|---|
| POST | `/login` (form) | public | Log in (handled by Spring Security) |
| POST | `/logout` | any | Log out |
| POST | `/api/v1/auth/register` | public | Self-register using an institution `joinCode` |
| POST | `/api/v1/auth/password-reset/request` | public | Send a reset link |
| POST | `/api/v1/auth/password-reset/confirm` | public | Set a new password with the token |
| GET | `/api/v1/me` | any | Current user profile |
| PATCH | `/api/v1/me/language` | any | Change language |

### 2.2 Institutions, users, groups (FR-UM-01, 05, 06)

| Method | Path | Role | Purpose |
|---|---|---|---|
| GET / POST | `/api/v1/institutions` | SA | List / create institutions |
| PUT | `/api/v1/institutions/{id}` | SA | Update |
| PATCH | `/api/v1/institutions/{id}/active` | SA | Activate / deactivate |
| GET / POST | `/api/v1/users` | IA | List / create users in my institution |
| PUT | `/api/v1/users/{id}` | IA | Update a user |
| PATCH | `/api/v1/users/{id}/active` | IA | Activate / deactivate |
| POST | `/api/v1/users/import` | IA | Upload CSV of users; returns created count and row errors |
| GET / POST | `/api/v1/groups` | IA (GET also T) | List / create classes and departments |
| POST | `/api/v1/groups/{id}/members` | IA | Add members `{ "userIds": [..] }` |
| DELETE | `/api/v1/groups/{id}/members/{userId}` | IA | Remove a member |

### 2.3 Learning (FR-LM-01 to LM-08)

| Method | Path | Role | Purpose |
|---|---|---|---|
| GET | `/api/v1/modules?hazard=FIRE` | S, T, IA | List published modules with my progress |
| GET | `/api/v1/modules/{id}` | S, T, IA | Module details and lesson list |
| GET | `/api/v1/lessons/{id}` | S, T, IA | One lesson (text, image) |
| POST | `/api/v1/lessons/{id}/complete` | S | Mark lesson complete (saves progress) |
| GET | `/api/v1/me/progress` | S, T | My progress across all modules |
| POST | `/api/v1/assignments` | T, IA | Assign a module to a group with due date |
| GET | `/api/v1/assignments?groupId=` | T, IA | Assignments I created |
| GET | `/api/v1/me/assignments` | S | Modules assigned to me |

### 2.4 Assessment (FR-AS-01 to AS-08)

| Method | Path | Role | Purpose |
|---|---|---|---|
| POST | `/api/v1/modules/{id}/attempts` | S | Start a `PRE` or `POST` test `{ "type": "PRE" }`; returns questions (no answer key) |
| POST | `/api/v1/attempts/{id}/answers` | S | Save one answer; returns correct/incorrect and explanation |
| POST | `/api/v1/attempts/{id}/submit` | S | Finish; returns score |
| GET | `/api/v1/attempts/{id}` | S, T | Result of an attempt |
| GET | `/api/v1/modules/{id}/improvement` | S, T, IA | Pre vs post improvement (mine, or a group's `?groupId=`) |

### 2.5 Scenarios (FR-SS-01 to SS-09)

| Method | Path | Role | Purpose |
|---|---|---|---|
| GET | `/api/v1/scenarios?hazard=` | S, T, IA | List published scenarios |
| POST | `/api/v1/scenarios/{id}/attempts` | S | Start; returns first step |
| POST | `/api/v1/scenario-attempts/{id}/decisions` | S | Send a decision; returns feedback and next step or result |
| GET | `/api/v1/scenario-attempts/{id}` | S, T | Full result and decision summary |
| GET | `/api/v1/me/scenario-attempts?scenarioId=` | S | My earlier scores (replay comparison) |

### 2.6 Virtual drills (FR-VD-01 to VD-10)

| Method | Path | Role | Purpose |
|---|---|---|---|
| POST | `/api/v1/drills` | T, IA | Create a drill |
| GET | `/api/v1/drills?status=` | T, IA | Drill history and upcoming drills |
| GET | `/api/v1/drills/{id}` | T, IA | Drill details and participants |
| PUT | `/api/v1/drills/{id}` | T, IA | Reschedule (only while `SCHEDULED`) |
| POST | `/api/v1/drills/{id}/cancel` | T, IA | Cancel (only while `SCHEDULED`) |
| GET | `/api/v1/me/drills` | S, T | Drills assigned to me |
| POST | `/api/v1/drills/{id}/join` | S, T | Join an `OPEN` drill; returns first scenario step |
| POST | `/api/v1/drills/{id}/evacuation` | S, T | Save chosen route and evacuation time |
| GET | `/api/v1/drills/{id}/report` | T, IA | Drill report |
| GET | `/api/v1/drills/{id}/report.csv` / `.pdf` | T, IA | Export report |

### 2.7 Emergency plan (FR-EP-01 to EP-05)

| Method | Path | Role | Purpose |
|---|---|---|---|
| GET | `/api/v1/plan` | all in institution | View plan |
| PUT | `/api/v1/plan` | IA | Save plan |
| GET / POST / PUT / DELETE | `/api/v1/plan/routes[/{id}]` | IA (GET all) | Evacuation routes and assembly points |
| GET / POST / PUT / DELETE | `/api/v1/plan/contacts[/{id}]` | IA (GET all) | Emergency contacts (national defaults included, read-only) |
| GET / PUT | `/api/v1/plan/checklist` | IA | Safety checklist |
| GET | `/api/v1/plan/export.pdf` | all in institution | Printable plan |

### 2.8 Dashboard and reports (FR-DR-01 to DR-07)

| Method | Path | Role | Purpose |
|---|---|---|---|
| GET | `/api/v1/dashboard/me` | S | My progress, scores, completions |
| GET | `/api/v1/dashboard/groups/{id}` | T, IA | Class results |
| GET | `/api/v1/dashboard/institution` | IA | Participation, scores, drills, Readiness Index |
| GET | `/api/v1/reports/readiness?from=&to=&hazard=&groupId=` | IA | Filtered readiness report |
| GET | `/api/v1/reports/readiness.csv` / `.pdf` | IA | Export |
| GET | `/api/v1/admin/statistics` | SA | Aggregate statistics across institutions |

### 2.9 Notifications (FR-NT-01, NT-02)

| Method | Path | Role | Purpose |
|---|---|---|---|
| GET | `/api/v1/notifications?unread=true` | any | My notifications |
| POST | `/api/v1/notifications/{id}/read` | any | Mark as read |
| POST | `/api/v1/notifications/read-all` | any | Mark all as read |

### 2.10 Content management (FR-CM-01 to CM-06)

| Method | Path | Role | Purpose |
|---|---|---|---|
| GET / POST / PUT / DELETE | `/api/v1/admin/modules[/{id}]` | SA | Modules |
| GET / POST / PUT / DELETE | `/api/v1/admin/lessons[/{id}]` | SA | Lessons (all languages) |
| GET / POST / PUT / DELETE | `/api/v1/admin/questions[/{id}]` | SA | Questions and options |
| GET / POST / PUT / DELETE | `/api/v1/admin/scenarios[/{id}]` | SA | Scenarios |
| POST | `/api/v1/admin/scenarios/import` | SA | Import scenario JSON |
| POST | `/api/v1/admin/{lessons\|scenarios}/{id}/publish` | SA | Publish (refused if not SME-reviewed) |

## 3. Example requests and responses

### 3.1 Start a scenario

`POST /api/v1/scenarios/3/attempts`

```json
{
  "attemptId": 812,
  "scenarioTitle": "Earthquake in the Classroom",
  "step": {
    "id": "s1",
    "situation": "You are in a maths lesson. The floor suddenly starts shaking...",
    "timeLimitSeconds": 10,
    "options": [
      { "id": "a", "text": "Drop to the floor, take cover under your desk and hold on to it." },
      { "id": "b", "text": "Run out of the classroom as fast as you can." },
      { "id": "c", "text": "Stand by the window to see what is happening." }
    ]
  }
}
```

Note that `points`, `correct` and `feedback` are **not** included.

### 3.2 Send a decision

`POST /api/v1/scenario-attempts/812/decisions`

```json
{ "stepId": "s1", "optionId": "b", "responseTimeMs": 4200, "timedOut": false }
```

Response:

```json
{
  "correct": false,
  "points": 0,
  "feedback": "Running while the ground is shaking is dangerous...",
  "nextStep": { "id": "s2", "situation": "...", "timeLimitSeconds": 20, "options": [ ... ] },
  "finished": false
}
```

When the last step is answered, `finished` is `true`, `nextStep` is `null` and the response
adds `score`, `maxScore`, `percent`, `resultLabel`, `resultMessage` and a `decisions` list.

### 3.3 Create a drill

`POST /api/v1/drills`

```json
{
  "title": "Term 2 Earthquake Drill",
  "scenarioId": 3,
  "startTime": "2026-10-05T10:00:00+05:30",
  "endTime": "2026-10-05T18:00:00+05:30",
  "mode": "SELF_PACED",
  "campusWide": false,
  "groupIds": [12, 13],
  "roleAssignments": [ { "userId": 45, "drillRole": "FLOOR_WARDEN" } ]
}
```

Response `201 Created`: `{ "id": 27, "status": "SCHEDULED", "participantCount": 71 }`

### 3.4 Drill report

`GET /api/v1/drills/27/report`

```json
{
  "drillId": 27,
  "title": "Term 2 Earthquake Drill",
  "assigned": 71,
  "completed": 58,
  "missed": 10,
  "incomplete": 3,
  "participationPercent": 81.7,
  "averageScorePercent": 74.3,
  "averageResponseTimeSeconds": 6.8,
  "mostMissedSteps": [
    { "stepId": "s2", "situation": "The shaking stops...", "wrongCount": 21 }
  ],
  "participants": [ { "name": "A. Sharma", "group": "Class 8B", "status": "COMPLETED", "scorePercent": 75 } ]
}
```

### 3.5 Error example

```json
{
  "type": "about:blank",
  "title": "Conflict",
  "status": 409,
  "detail": "This drill is closed and can no longer be joined.",
  "instance": "/api/v1/drills/27/join"
}
```

## 4. Validation rules (examples)

| Item | Rule |
|---|---|
| Username | 4–80 characters; letters, digits, dot, underscore; unique |
| Password | At least 8 characters |
| Drill time | `endTime` after `startTime`; `startTime` in the future at creation |
| Drill target | Either `campusWide = true` or at least one group |
| Publishing content | `sme_reviewed` must be true and `source_reference` filled |
| Scenario decision | `stepId` must be the attempt's current step; `optionId` must exist in that step (or `timedOut = true`) |
