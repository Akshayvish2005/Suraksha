# Suraksha — Python / Flask migration

This repository is a Python migration baseline for the supplied Suraksha Spring Boot project.

## Technology
- Python 3.11+
- Flask
- Flask-SQLAlchemy + SQLAlchemy reflected ORM mappings
- MySQL 8 / PyMySQL
- Flask-Migrate
- Jinja templates, CSS and JavaScript

## Important migration decision
The original `src/database/schema.sql` is retained as the database source of truth. On startup, SQLAlchemy reflects the existing MySQL tables and maps them with SQLAlchemy Automap. The app does **not** run `create_all()` or alter the existing 32-table schema automatically. This avoids damaging data or drifting away from the Java application's database design.

The original Java files are preserved under `migration_reference/java/` for traceability. The original Thymeleaf templates are preserved under `src/backend/src/main/resources/templates/`. Python-rendered templates are in `templates/`; the visual language (Suraksha blue, white cards, rounded corners, Inter font) is retained and adapted to Jinja.

## 1. Setup in VS Code

1. Install Python 3.11+ and MySQL 8.
2. Open this folder in VS Code.
3. Create and activate a virtual environment:

   Windows PowerShell:
   ```powershell
   py -3 -m venv .venv
   .venv\Scripts\Activate.ps1
   ```

4. Install dependencies:
   ```powershell
   pip install -r requirements.txt
   ```
5. Create the database and run the retained schema and seed scripts. In MySQL Workbench, run in this order:
   - `CREATE DATABASE suraksha CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
   - `src/database/schema.sql`
   - `src/database/seed-test-users.sql`
   - `src/database/seed-modules-lessons.sql`
   - `src/database/seed-questions.sql`
   - `src/database/seed-scenarios.sql`
   - `src/database/seed-emergency-plan.sql`
   - `src/database/seed-drills.sql`

   Run seed scripts only once on a fresh database. Some seed scripts may assume IDs or empty tables; inspect them before re-running.

6. Copy `.env.example` to `.env` and set a strong `SECRET_KEY` and your MySQL credentials:
   ```env
   SECRET_KEY=use-a-long-random-secret
   DATABASE_URL=mysql+pymysql://root:YOUR_MYSQL_PASSWORD@localhost:3306/suraksha?charset=utf8mb4
   ```
   Do not commit `.env`. Do not put real passwords in source code or share them in chat.

7. Start the app:
   ```powershell
   python app.py
   ```
8. Open `http://127.0.0.1:5000`.

## Test accounts
The supplied SQL seed documents these usernames:
- `student1`
- `teacher1`
- `admin1`
- `sysadmin`

The corresponding plaintext test passwords are documented in `src/database/seed-test-users.sql`. Use only in a local test database; change them before deployment.

## Migrated web features in this baseline
- BCrypt-compatible sign-in against the existing `app_user` table and sign-out.
- Student dashboard and published learning module listing.
- Module and lesson viewing; lesson completion/progress persistence.
- Pre/post assessment attempt creation, answer persistence and score calculation for single-select and exact-set multi-select questions.
- Branching scenario practice based on the source JSON `steps`, `narrative`, `options`, `nextStepId`, `points`, `correct`, and `feedback`.
- Institution drill listing and teacher/admin drill creation.
- Emergency plan viewing/editing for institution admins and display of national/institution emergency contacts.
- Learner progress view and profile.
- Initial JSON endpoints: `/api/v1/me`, `/api/v1/modules`, `/api/v1/notifications`, notification read, and `/health`.

## Migration status — read before using
This is a substantial first migration pass, **not yet verified feature-for-feature parity with the Spring Boot app**. The uploaded Java project contains controllers and requirements for additional workflows that still need implementation and end-to-end verification, including:
- full registration, password reset, institution/user/group administration and CSV import;
- assignment creation and learner assignment views;
- complete progress completion rules and pre/post improvement reporting;
- full scenario result history and timing metrics;
- drill participant targeting, join window enforcement, drill completion/reporting, evacuation-route selection and CSV/PDF exports;
- emergency route/contact/checklist CRUD and PDF export;
- certificate eligibility and issuance;
- notification generation, dashboard analytics/readiness index and content-management screens;
- full documented `/api/v1` endpoint set, CSRF protection, stricter tenant isolation and automated integration tests.

Do not deploy this baseline publicly until the remaining items, security hardening, and tests are complete. In particular, set a secure secret key, serve over HTTPS, enable CSRF protection for all state-changing forms, configure secure cookie settings, and verify institution-scoped authorization on every data access.

## Database / migration safety
- Existing MySQL schema and seed files are kept under `src/database/`.
- Do not run `db.create_all()` against the live database.
- Back up the database before schema changes.
- Use Flask-Migrate only for deliberate, reviewed future schema changes.
- The existing Java application's `application-dev.properties` is intentionally not copied because it contains local database credentials.

## Project layout
```text
app.py
config.py
suraksha/
  __init__.py
  auth.py
  db_helpers.py
  extensions.py
  models.py
  routes.py
templates/
static/css/app.css
src/database/                 # original schema and seed SQL
docs/                         # original requirements and design docs
migration_reference/java/     # original Java source for comparison
src/backend/.../templates/    # original Thymeleaf templates for comparison
```
