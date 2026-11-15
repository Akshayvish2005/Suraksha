# 3. Project Scope

## 3.1 Scope statement

Design, develop, test and pilot a web-based **Disaster Preparedness and Response Education
System** that delivers learning modules, scenario simulations, virtual drills, assessments
and readiness reporting for schools and colleges, covering **earthquakes, floods and fires**.

## 3.2 In scope

### A. Users and roles

| Role | What they can do |
|---|---|
| **Student** | Register/login, study modules, take quizzes, play scenarios, join virtual drills, view own progress |
| **Teacher / faculty** | Assign modules and drills to classes, view class results, lead drills |
| **Institution admin** | Manage users, schedule campus drills, maintain the emergency plan, view readiness dashboard and reports |
| **System admin (project team)** | Manage institutions, content and system settings |

### B. Functional modules

| # | Module | Description |
|---|---|---|
| 1 | User & institution management | Registration, login, roles, institution profile |
| 2 | Learning modules | Lessons on causes, risks, before/during/after safety actions, for each hazard |
| 3 | Assessment engine | Pre-test, post-test, quizzes, scoring, progress tracking |
| 4 | Scenario simulation | Interactive, decision-based scenarios (e.g. earthquake during a class, fire in a lab, flood warning on campus) with consequences and feedback |
| 5 | Virtual drill system | Scheduling, participant assignment, role-based drills, timed evacuation practice, drill report |
| 6 | Emergency plan tools | Emergency plan template, evacuation routes / assembly points, contact directory, safety checklists |
| 7 | Dashboard & reporting | Participation, scores, drill history, readiness indicator, exportable reports |
| 8 | Notifications | In-app reminders for drills, assignments and pending modules |
| 9 | Content management | Admin can add or edit lessons, quizzes and scenarios |

### C. Hazards covered

- **Phase 1 (this project):** Earthquake, Flood, Fire
- **Future:** Cyclone, heatwave, stampede, chemical leak, landslide

### D. Supporting activities

- Research of existing NDMA/NIDM guidelines and educational content
- Requirements gathering (survey/interviews with students, teachers, administrators)
- Content creation and review by a subject-matter advisor
- UI/UX design, database design and system architecture
- Testing (functional, usability, performance) and a small pilot
- Documentation: user guides, technical documentation, final report

## 3.3 Out of scope

- Real-time disaster prediction, early-warning or alerting systems
- Integration with government emergency systems or sensors/IoT
- Live emergency management during an actual disaster
- Native mobile apps (Android/iOS) — the web app will be mobile-friendly instead
- Full immersive VR/AR (may be considered as a future enhancement)
- Physical drill equipment, hardware or infrastructure
- Legal certification or official accreditation of the training
- Hazards beyond the three named in Phase 1

## 3.4 Feature priorities

| Priority | Features |
|---|---|
| **Must** | Login & roles, 3 hazard modules, quizzes with pre/post tests, 3 scenarios, virtual drill creation & participation, admin dashboard |
| **Should** | Emergency plan builder, exportable reports, Hindi + English, low-bandwidth mode |
| **Could** | Badges and leaderboards, certificates, audio narration, additional regional languages |
| **Won't (this phase)** | VR/AR, native apps, real-time alerts |

## 3.5 Deliverables

1. Project charter and planning documents
2. Requirements specification (SRS) and user stories
3. System design (architecture, database, UI/UX wireframes)
4. Working web application (student, teacher, admin interfaces)
5. Learning content and quiz bank for 3 hazards
6. 3 or more interactive scenarios and the virtual drill engine
7. Test plan, test report and pilot feedback report
8. User guides and technical documentation
9. Final report and presentation

## 3.6 Assumptions

- Target institutions have basic internet access and devices (phone, computer or lab)
- Institution administrators are willing to take part in the pilot
- A subject-matter advisor is available to review content for accuracy
- Content can be based on publicly available government guidelines with proper credit
- The team has the skills, or will acquire them, for the chosen technology

## 3.7 Constraints

| Type | Constraint |
|---|---|
| Time | Fixed project duration (proposed 16 weeks) |
| Resources | Limited team size and budget; preference for free / open-source tools |
| Technical | Must work on low-end devices and slow networks |
| Content | Safety guidance must be accurate and consistent with official sources |
| Privacy | Student data (especially minors) must be handled securely; collect only what is needed |

## 3.8 Dependencies

- Availability of official guidelines and reference material (NDMA, NIDM, state authorities)
- Access to pilot institutions and participants
- Availability of a subject-matter advisor for content review
- Hosting and development tools

## 3.9 Proposed technology (tentative — to be confirmed in the design phase)

| Layer | Option |
|---|---|
| Front end | HTML/CSS/JavaScript with React (responsive, mobile-first) |
| Back end | Node.js (Express) or Python (Flask/Django) |
| Database | PostgreSQL or MySQL |
| Simulation | 2D scenario engine in JavaScript (Canvas / SVG), decision trees in JSON |
| Hosting | Any free-tier / institutional cloud hosting |
| Tools | Git/GitHub, Figma, Postman |

## 3.10 Scope change control

Any addition to or removal from the scope above must be written down, discussed with the
project guide/sponsor, and approved before work begins. Record accepted changes in
`docs/06-reports/change-log.md`.
