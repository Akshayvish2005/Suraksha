# 4. Project Charter

## 4.1 Project identification

| | |
|---|---|
| **Project title** | Disaster Preparedness and Response Education System for Schools and Colleges |
| **Working name** | Suraksha (सुरक्षा) |
| **Document version** | 1.0 (Draft for approval) |
| **Date prepared** | 20 September 2026 |
| **Project sponsor / guide** | [Name, designation] |
| **Project manager** | [Name] |
| **Institution / department** | [Name] |

## 4.2 Project purpose and justification

Schools and colleges in India are often unprepared for earthquakes, floods and fires.
Guidelines exist on paper, but disaster education is not a structured part of the
curriculum and institutions lack digital tools to simulate emergencies or run virtual
drills. This project will build a digital system that teaches, simulates, practises and
measures disaster preparedness, so that students and staff know what to do *before* an
emergency happens.

See `01-purpose.md` for the full problem statement.

## 4.3 Project objectives

| ID | Objective |
|---|---|
| O1 | Build learning modules for earthquake, flood and fire (English + Hindi) |
| O2 | Build at least 3 interactive, decision-based scenario simulations |
| O3 | Enable scheduling and running of virtual drills |
| O4 | Measure learning through pre/post tests (target: ≥ 25% improvement in pilot) |
| O5 | Provide an admin dashboard with readiness reporting |
| O6 | Provide emergency plan tools (template, routes, contacts) |
| O7 | Ensure low-bandwidth, low-end-device usability |
| O8 | Pilot with 1–2 institutions and ≥ 100 users (satisfaction ≥ 4/5) |

See `02-objectives.md` for details and KPIs.

## 4.4 Scope summary

**In scope:** web platform with user roles; learning modules; assessments; scenario
simulations; virtual drills; emergency plan tools; dashboard and reports; content
management; pilot testing; documentation. Hazards: earthquake, flood, fire.

**Out of scope:** early warning/prediction, IoT or government system integration, live
emergency management, native mobile apps, VR/AR, physical equipment, other hazards.

See `03-scope.md` for the full scope.

## 4.5 Key deliverables

| # | Deliverable |
|---|---|
| 1 | Requirements specification and user stories |
| 2 | System design (architecture, database, wireframes) |
| 3 | Working web application (student, teacher, admin) |
| 4 | Learning content and quiz bank |
| 5 | Scenario simulations and virtual drill engine |
| 6 | Test plan, test report, pilot feedback report |
| 7 | User guides and technical documentation |
| 8 | Final report and presentation |

## 4.6 High-level timeline and milestones (proposed — 16 weeks)

| Phase | Weeks | Milestone |
|---|---|---|
| 1. Initiation | 1 | **M1** — Charter approved |
| 2. Requirements & research | 2–4 | **M2** — Requirements (SRS) signed off; content outline ready |
| 3. Design | 5–6 | **M3** — Architecture, database and UI designs approved |
| 4. Development — Sprint 1 | 7–9 | **M4** — Login, roles, learning modules, assessments working |
| 4. Development — Sprint 2 | 10–12 | **M5** — Scenarios, virtual drills, dashboard working |
| 5. Testing & pilot | 13–15 | **M6** — Testing done; pilot completed; feedback analysed |
| 6. Deployment & handover | 16 | **M7** — Final system, documentation and report delivered |

## 4.7 Budget estimate

Mostly effort-based (student/team time) with a preference for free and open-source tools.

| Item | Estimate |
|---|---|
| Team effort | [person-hours — to be estimated] |
| Hosting / domain | [₹ — free tier if possible] |
| Design and development tools | ₹0 (open-source / free tier) |
| Pilot logistics (printing, travel, refreshments) | [₹ — to be estimated] |
| Contingency (10%) | [₹] |
| **Total** | **[₹ — to be finalised]** |

## 4.8 Stakeholders

| Stakeholder | Role / interest | Influence |
|---|---|---|
| Project sponsor / guide | Approves scope, reviews progress | High |
| Project team | Designs, builds and tests the system | High |
| Subject-matter advisor | Verifies accuracy of safety content | High |
| Institution administrators | Adopt and manage the system | High |
| Teachers / faculty | Use it to teach and run drills | Medium |
| Students | Primary learners and users | Medium |
| Parents | Interested in child safety | Low |
| Disaster authorities (NDMA / SDMA / NIDM) | Source of guidelines; potential future adopters | Low (for now) |

## 4.9 Roles and responsibilities

| Role | Responsibility | Assigned to |
|---|---|---|
| Project manager | Plan, schedule, coordinate, report progress | [Name] |
| Business / requirements analyst | Gather and document requirements | [Name] |
| UI/UX designer | Wireframes, prototypes, usability | [Name] |
| Front-end developer | Student, teacher and admin interfaces | [Name] |
| Back-end developer | APIs, database, authentication | [Name] |
| Simulation & content developer | Scenarios, drill engine, learning content | [Name] |
| QA / tester | Test plan, testing, pilot support | [Name] |
| Subject-matter advisor | Content review and validation | [Name] |

## 4.10 Assumptions and constraints

**Assumptions:** pilot institutions are available; users have basic internet access and
devices; a subject-matter advisor can review content; public guidelines can be used with
credit.

**Constraints:** fixed 16-week duration; limited team and budget; must work on low-end
devices; safety content must be accurate; student data privacy must be protected.

## 4.11 Initial risk assessment

| # | Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|---|
| R1 | Safety content is inaccurate or outdated | Low | **High** | Base on official NDMA/NIDM material; expert review before release |
| R2 | Scope creep (extra hazards, VR, apps) | High | Medium | Strict change control; keep a "future work" list |
| R3 | No pilot institution available in time | Medium | High | Approach institutions in Week 1–2; keep a fallback (own campus / class) |
| R4 | Low engagement from students and teachers | Medium | Medium | Short modules, gamified feedback, teacher-led sessions |
| R5 | Poor performance on low-end devices/slow networks | Medium | Medium | Lightweight design, compressed assets, early testing on basic phones |
| R6 | Team skill gaps in chosen technology | Medium | Medium | Choose familiar stack; allocate learning time in early weeks |
| R7 | Data privacy issues with student data | Low | High | Collect minimum data; secure storage; parental/institutional consent for the pilot |
| R8 | Schedule slip | Medium | Medium | Weekly progress check; prioritise Must-have features first |

## 4.12 Success criteria

The project is considered successful when:

1. All **Must-have** objectives (O1–O5) are delivered and demonstrated
2. The pilot shows an average knowledge improvement of **≥ 25%**
3. At least **100 users** across **1–2 institutions** complete the pilot
4. Average satisfaction is **≥ 4 out of 5**
5. Safety content is reviewed and approved by the subject-matter advisor
6. Documentation is complete and the system is handed over on schedule

## 4.13 Communication and reporting

| What | Frequency | Format |
|---|---|---|
| Team meeting | Weekly | Short stand-up + task update |
| Progress report to guide/sponsor | Fortnightly | Brief report in `docs/06-reports/` |
| Milestone review | At each milestone (M1–M7) | Demo + review |

## 4.14 Approval

| Name | Role | Signature | Date |
|---|---|---|---|
| [Name] | Project sponsor / guide | | |
| [Name] | Project manager | | |
| [Name] | Team representative | | |
