# Database Design

**Version:** 0.1 (Draft) | Exact table definitions: `src/database/schema.sql` (32 tables).
This document explains the design; the SQL file is the detailed reference.

## 1. Design rules

1. **MySQL 8** for the pilot, **H2 (MySQL mode)** for development, so nothing needs installing while you code. Text uses `utf8mb4` so English and Hindi content store correctly.
2. Every table has a numeric `id` primary key (or a composite key for pure link tables).
3. **Institution isolation:** institution-owned data carries `institution_id`, directly or through its parent, and every query filters on it.
4. **Content is translated by rows:** `*_text` tables hold one row per language (`en`, `hi`), so a new language needs no schema change (NFR-10).
5. **Hazards are a table**, not an enum, so a new hazard is a new row (NFR-13).
6. Only a password **hash** is stored. No sensitive personal data is stored (NFR-06).
7. Status values are limited by `CHECK` constraints so bad data cannot be saved.
8. `is_correct` flags and scenario answer data stay on the server (design decision D4).

## 2. Entity overview (by area)

```
PEOPLE
  institution 1 ─── * app_user
  institution 1 ─── * user_group * ─── * app_user      (via group_member)

LEARNING CONTENT
  hazard 1 ─── * learning_module 1 ─── * lesson
  learning_module 1 ─── * question 1 ─── * answer_option
  (module_text, lesson_text, question_text, answer_option_text hold the words per language)

LEARNER ACTIVITY
  app_user 1 ─── * module_progress / lesson_progress
  learning_module + user_group ─── assignment  (teacher assigns a module to a group)
  app_user 1 ─── * assessment_attempt 1 ─── * attempt_answer * ─── * answer_option

SCENARIOS AND DRILLS
  hazard 1 ─── * scenario
  app_user 1 ─── * scenario_attempt 1 ─── * scenario_decision
  scenario 1 ─── * drill 1 ─── * drill_participant ─── 1 scenario_attempt
  drill * ─── * user_group   (via drill_group; or campus_wide = true)

EMERGENCY PLAN
  institution 1 ─── 1 emergency_plan
  institution 1 ─── * evacuation_route,  emergency_contact,  checklist_status * ─── 1 checklist_item

SYSTEM
  app_user 1 ─── * notification
  institution 1 ─── * readiness_snapshot   (optional daily trend data)
```

## 3. Tables and what they support

| Area | Tables | Requirements |
|---|---|---|
| Reference | `hazard` | FR-LM-01, NFR-13 |
| People | `institution`, `app_user`, `user_group`, `group_member` | FR-UM-01 to UM-09 |
| Learning content | `learning_module`, `module_text`, `lesson`, `lesson_text` | FR-LM-01 to LM-08, FR-CM-01, CM-04 to CM-06 |
| Assessment content | `question`, `question_text`, `answer_option`, `answer_option_text` | FR-AS-06 to AS-09, FR-CM-02 |
| Progress | `module_progress`, `lesson_progress`, `assignment` | FR-LM-04, LM-05 |
| Assessment results | `assessment_attempt`, `attempt_answer`, `attempt_answer_option` | FR-AS-01 to AS-05 |
| Scenarios | `scenario`, `scenario_attempt`, `scenario_decision` | FR-SS-01 to SS-09 |
| Drills | `drill`, `drill_group`, `drill_participant` | FR-VD-01 to VD-10 |
| Emergency plan | `emergency_plan`, `evacuation_route`, `emergency_contact`, `checklist_item`, `checklist_status` | FR-EP-01 to EP-05, FR-VD-08 |
| Notifications | `notification` | FR-NT-01 to NT-03 |
| Reporting | `readiness_snapshot` (plus live queries) | FR-DR-01 to DR-07 |

## 4. Important design details

**Content review (NFR-12).** `lesson` and `scenario` carry `source_reference`, `sme_reviewed`,
`reviewed_by` and `reviewed_on`. The application refuses to publish content whose
`sme_reviewed` is false.

**Scenario storage.** A scenario's steps and options are stored as JSON text in
`scenario.definition` (format: `content/drill-scenarios/earthquake-classroom-en.json`).
Each decision a learner makes is stored in `scenario_decision` with the step and option
identifiers, points, correctness and response time. This is what lets the drill report show
"most-missed steps".

**Drills.** `drill.status` moves `SCHEDULED → OPEN → CLOSED` (or `CANCELLED`). Each assigned
person is a `drill_participant` row with their role and status; joining a drill links the
participant to a `scenario_attempt`. Practice attempts outside a drill have `drill_id = NULL`.

**Pre-test and post-test.** Both are `assessment_attempt` rows with `attempt_type` `PRE` or
`POST`. The improvement shown to learners compares the **first** submitted PRE attempt with the
**latest** submitted POST attempt for the module.

**Emergency contacts.** Rows with `institution_id = NULL` and `is_national = TRUE` are the
national defaults (e.g. 112) shown to every institution; institutions add their own rows.

## 5. Example queries the application will need

Pre-test vs post-test improvement (FR-AS-05):

```sql
SELECT pre.user_id, pre.module_id,
       pre.score_percent  AS pre_score,
       post.score_percent AS post_score,
       post.score_percent - pre.score_percent AS improvement
FROM assessment_attempt pre
JOIN assessment_attempt post
  ON post.user_id = pre.user_id AND post.module_id = pre.module_id
WHERE pre.attempt_type  = 'PRE'  AND pre.status  = 'SUBMITTED'
  AND post.attempt_type = 'POST' AND post.status = 'SUBMITTED';
```

Drill participation (FR-VD-05):

```sql
SELECT d.id, d.title,
       COUNT(*) AS assigned,
       SUM(CASE WHEN p.status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed,
       ROUND(100.0 * SUM(CASE WHEN p.status = 'COMPLETED' THEN 1 ELSE 0 END) / COUNT(*), 1)
           AS participation_pct
FROM drill d
JOIN drill_participant p ON p.drill_id = d.id
WHERE d.institution_id = ?
GROUP BY d.id, d.title;
```

Most-missed steps in a drill, i.e. "common mistakes" (FR-VD-05):

```sql
SELECT sd.step_id, COUNT(*) AS wrong_count
FROM scenario_decision sd
JOIN scenario_attempt sa ON sa.id = sd.attempt_id
WHERE sa.drill_id = ? AND sd.is_correct = FALSE
GROUP BY sd.step_id
ORDER BY wrong_count DESC;
```

## 6. Notes for the development phase

- In Spring Boot the tables are created from JPA entities (`spring.jpa.hibernate.ddl-auto=update` in the `dev` profile). Use `schema.sql` to check that your entities match the design, and to load the seed data.
- Implement **English first**. Add Hindi rows later; no schema change is needed.
- `readiness_snapshot` and `checklist_*` are optional (Could/Should); build them last.
- Add an index whenever a report becomes slow; the schema already indexes the main foreign keys.
