# Development Environment Setup (IntelliJ IDEA + Spring Boot)

Follow this once, before Phase 4 (development). The goal is a Spring Boot app that starts on
your machine and lives in `src/backend/` of your existing IntelliJ project.

## 1. Install Java 21 (JDK)

You can let IntelliJ do it:

1. **File > Project Structure** (`Ctrl+Alt+Shift+S`) > **Project**.
2. Open the **SDK** drop-down > **Download JDK...**
3. Version **21**, Vendor **Eclipse Temurin** (or any OpenJDK 21) > **Download**.
4. Set **Language level** to **21** and click **Apply**.

## 2. Generate the Spring Boot project

Open **https://start.spring.io** in your browser and fill it in like this:

| Field | Value |
|---|---|
| Project | **Maven** |
| Language | **Java** |
| Spring Boot | the latest **stable** version offered (never one ending in SNAPSHOT, M1 or RC). Any current 3.x or 4.x release is fine for this project. |
| Group | `com.suraksha` |
| Artifact / Name | `suraksha` |
| Packaging | **Jar** |
| Java | **21** |

Click **Add Dependencies** and add all of these:

| Dependency | Used for |
|---|---|
| Spring Web | pages and REST API |
| Thymeleaf | HTML templates |
| Spring Data JPA | database access |
| Spring Security | login and roles |
| Validation | input checks |
| H2 Database | development database (no installation needed) |
| MySQL Driver | pilot database |
| Spring Boot DevTools | automatic restart while coding |

Click **Generate**. A file `suraksha.zip` downloads.

## 3. Install MySQL

If you already have **XAMPP** or **MySQL** installed (common in many college labs), skip to
step 3.4 and just note your root password.

1. Download **MySQL Community Server** from `dev.mysql.com/downloads/mysql` (choose the
   Windows installer), or install **XAMPP** and enable its MySQL module — either works.
2. During setup, choose **Developer Default** (or just MySQL Server + MySQL Workbench),
   keep the default port **3306**, and set a **root password** you will remember.
3. Also install **MySQL Workbench** if it wasn't bundled — it gives you a visual view of
   your tables, similar to phpMyAdmin.
4. Open **MySQL Workbench**, connect to `localhost:3306` with `root` and your password,
   then run:

```sql
CREATE DATABASE suraksha CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

5. Open `src/database/schema.sql` from this project (**File > Open SQL Script** in
   Workbench, or paste it into a new query tab connected to the `suraksha` database) and
   run it (the lightning-bolt icon). This creates the 32 tables and loads the seed data
   (hazards, national emergency numbers).
6. In Workbench's left panel, refresh **Schemas > suraksha > Tables** — you should see
   `app_user`, `institution`, `drill`, and the rest.

## 4. Put the project into `src/backend`

1. Unzip `suraksha.zip`. Open the extracted `suraksha` folder.
2. Copy **everything inside it** (`pom.xml`, `mvnw`, `mvnw.cmd`, `.mvn`, `src`, `HELP.md`, `.gitignore`) into
   `C:\Users\WINDOWS 11\Documents\Disaster System\src\backend`.
   Do not copy the `suraksha` folder itself, only what is inside it.
3. Your result should look like this:

```
Disaster System/
└── src/
    └── backend/
        ├── README.md                 (already there)
        ├── pom.xml
        ├── mvnw, mvnw.cmd, .mvn/
        └── src/
            ├── main/java/com/suraksha/SurakshaApplication.java
            ├── main/resources/application.properties
            └── test/java/com/suraksha/SurakshaApplicationTests.java
```

4. In IntelliJ, delete the old sample file `src/Main.java` (right-click > **Delete**).
   You no longer need it.

## 5. Tell IntelliJ it is a Maven project

1. In the Project panel, right-click `src/backend/pom.xml` > **Add as Maven Project**.
2. Wait for the progress bar at the bottom right (Maven downloads libraries; the first time can take several minutes).
3. If a **Load Maven Changes** icon (a small "m" with a refresh arrow) appears in the editor, click it.

**If IntelliJ complains about overlapping source roots:** open **File > Project Structure > Modules**,
select the old module **Disaster System**, go to the **Sources** tab, click the `src` folder in the
tree on the right and click **Sources** (blue) once to unmark it. Then **Apply**.

## 6. Add the starter configuration

Open `src/backend/src/main/resources/application.properties` and replace its content with:

```properties
spring.application.name=suraksha
spring.profiles.active=dev
server.compression.enabled=true
server.servlet.session.timeout=30m
spring.messages.basename=messages
```

Create `application-dev.properties` in the same folder — this is the profile you will use
day to day, pointing at the local MySQL database you created in step 3:

```properties
# Development: your local MySQL database
spring.datasource.url=jdbc:mysql://localhost:3306/suraksha?useSSL=false&serverTimezone=Asia/Kolkata&characterEncoding=utf8mb4
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_ROOT_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Replace `YOUR_MYSQL_ROOT_PASSWORD` with the password you set in step 3.2. Since this file
holds a password, add `application-dev.properties` to `.gitignore` before committing, or
each teammate can keep their own local copy.

Create `application-prod.properties` (used later for the pilot server — same MySQL syntax,
different host and a stricter `ddl-auto` so the app never changes the schema by accident):

```properties
# Pilot: MySQL, tables already created from src/database/schema.sql
spring.datasource.url=jdbc:mysql://${DB_HOST}:3306/suraksha?useSSL=true&serverTimezone=Asia/Kolkata&characterEncoding=utf8mb4
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
```

**If MySQL isn't installed on a teammate's machine** (or for quick automated tests), an H2
fallback profile avoids blocking them — create `application-h2.properties`:

```properties
# Fallback only: in-memory database, no installation needed, data lost on stop
spring.datasource.url=jdbc:h2:mem:suraksha;MODE=MySQL;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=update
```

Use it by setting `spring.profiles.active=h2` instead of `dev` — everything else about the
app behaves the same either way.

## 7. Run it and check

1. Open `SurakshaApplication.java`.
2. Click the green **Run** triangle next to `main` > **Run 'SurakshaApplication'**.
3. In the Run window, look for a line like `Started SurakshaApplication in 3.1 seconds`.
4. Open **http://localhost:8080** in your browser. You should see a **Spring Security login page**.
   Log in with username `user` and the password printed in the console
   (`Using generated security password: ...`).
   Seeing the login page means the setup works. We replace it with our own login in Phase 4.
5. Stop the app with the red square.

Command-line alternative (Windows): open the Terminal tab in IntelliJ, go to `src\backend`, run `.\mvnw.cmd spring-boot:run`.

Once real tables exist (Phase 4), refreshing **MySQL Workbench** will show your data
appearing in `suraksha`'s tables as you use the app — handy for checking things are saved
correctly.

## 8. Start version control (recommended)

1. **Git > Create Git Repository...** (or the **Version control** menu) and select the project root.
2. Commit the docs and setup as "Phase 1-3: initiation, requirements, design".
3. Commit small and often during Phase 4. Push to GitHub so teammates can share the code.

## 9. Troubleshooting

| Problem | Fix |
|---|---|
| `release version 21 not supported` or `invalid target release` | Set the Project SDK to 21 (step 1) and reload Maven |
| Code shows red / `cannot resolve symbol` | Click **Load Maven Changes**; then **File > Invalidate Caches > Restart** if it persists |
| `Port 8080 was already in use` | Stop the other app, or add `server.port=8081` to `application.properties` |
| Maven cannot download dependencies | Check the internet connection or a proxy in **Settings > Build Tools > Maven** |
| `Communications link failure` / `Connection refused` connecting to MySQL | MySQL service isn't running — open **Services** (Windows) and start **MySQL80** (or start it from the XAMPP control panel) |
| `Access denied for user 'root'@'localhost'` | The password in `application-dev.properties` doesn't match MySQL — re-check step 3.2, or reset it in Workbench |
| `Unknown database 'suraksha'` | You skipped step 3.4/3.5 — create the database and run `schema.sql` in Workbench |
| `Public Key Retrieval is not allowed` | Add `&allowPublicKeyRetrieval=true` to the JDBC URL |
| The app fails with a different database error | Check `spring.profiles.active=dev` is set, and that `spring.datasource.url` matches your MySQL setup |
| Two `Main`/application classes conflict | Make sure the old `src/Main.java` is deleted |

## 10. Done when

- [ ] JDK 21 is set in IntelliJ
- [ ] MySQL is installed and the `suraksha` database exists with tables from `schema.sql`
- [ ] `src/backend/pom.xml` is loaded as a Maven project without errors
- [ ] The app starts and the login page appears at `http://localhost:8080`
- [ ] The project is committed to Git (with `application-dev.properties` excluded or password-free)
