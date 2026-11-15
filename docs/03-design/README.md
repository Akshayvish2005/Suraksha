# 03-design

**Purpose:** System architecture, database design, API design, UI/UX wireframes and development setup.

_Status: draft complete (v0.1) — awaiting review and sign-off (Milestone M3)._

| # | Document | What it contains |
|---|---|---|
| 1 | [01-architecture.md](01-architecture.md) | Tech stack (Java 21 + Spring Boot), layers, packages, security, scenario and drill engine, readiness index |
| 2 | [02-database-design.md](02-database-design.md) | Entities, relationships, design rules, example queries (SQL is in `src/database/schema.sql`) |
| 3 | [03-api-design.md](03-api-design.md) | REST endpoints, conventions, example requests and responses |
| 4 | [04-ui-wireframes.md](04-ui-wireframes.md) | Design principles, screen list, 13 wireframes |
| 5 | [05-dev-environment-setup.md](05-dev-environment-setup.md) | Step-by-step IntelliJ + Spring Boot setup |

Related files outside this folder:

- `src/database/schema.sql` — full database schema and seed data
- `content/drill-scenarios/earthquake-classroom-en.json` — sample scenario (needs expert review)

## Design review checklist (Milestone M3)

- [ ] Every Must requirement in the SRS is covered by a table, endpoint or screen
- [ ] Guide has reviewed the architecture and technology choices
- [ ] Database design reviewed (tables, keys, relationships)
- [ ] Wireframes reviewed by at least 2 target users (a student and a teacher)
- [ ] Open questions from the SRS are answered (login method, live drill, Hindi review, pilot institution)
