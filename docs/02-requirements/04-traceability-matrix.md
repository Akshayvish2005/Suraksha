# Requirements Traceability Matrix

Shows that every objective is covered by requirements, stories and use cases.
The **Test cases** column is filled in during Phase 5 (`docs/04-testing/`).

| Objective | Functional requirements | User stories | Use cases | Test cases |
|---|---|---|---|---|
| **O1** Learning modules (EN + HI) | LM-01 to LM-08, CM-01, CM-04 to CM-06, UM-08 | US-02, US-03, US-09, US-21, US-23 | UC-02, UC-10 | _TBD_ |
| **O2** Scenario simulations | SS-01 to SS-09, CM-03 | US-06, US-07, US-22 | UC-04, UC-10 | _TBD_ |
| **O3** Virtual drills | VD-01 to VD-10, NT-01 to NT-03, UM-05 | US-08, US-13, US-14, US-16 | UC-06, UC-07, UC-08 | _TBD_ |
| **O4** Pre/post assessment | AS-01 to AS-10, CM-02 | US-04, US-05 | UC-03 | _TBD_ |
| **O5** Dashboard and reports | DR-01 to DR-07 | US-12, US-17, US-18 | UC-09 | _TBD_ |
| **O6** Emergency plan tools | EP-01 to EP-05, VD-08 | US-19 | UC-11 | _TBD_ |
| **O7** Accessibility / low bandwidth | LM-07, NFR-01, NFR-08, NFR-09 | US-10 | UC-02 | _TBD_ |
| **O8** Pilot validation | NFR-02, NFR-03, NFR-07 | — (validated by pilot) | All | _TBD_ |
| Foundation (not tied to one objective) | UM-01 to UM-09, NFR-04 to NFR-06 | US-01, US-15, US-20 | UC-01, UC-12 | _TBD_ |

## Coverage check

- Every **Must** objective (O1–O5) has at least one Must requirement, story and use case. ✔
- Almost every Must requirement in `01-srs.md` appears in a user story. A few system-level ones (FR-UM-04 role-based access, FR-AS-04 attempt history, FR-AS-06 question types, FR-SS-05 three scenarios, FR-VD-06 drill history, FR-DR-03 student progress view) are covered only indirectly through other stories and use cases. Add stories for them if your guide wants a strict one-to-one mapping.
- Update this table whenever a requirement is added, changed or removed (and record it in `docs/06-reports/change-log.md`).
