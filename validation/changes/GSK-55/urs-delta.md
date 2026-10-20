# URS delta: GSK-55

Draft. Not a signed GxP record. Prepared by Devin for review by a named GSK approver.

GSK-55 is a platform change. It adds no user function. The requirements say that behaviour
must not change.

| URS id | type | requirement | source | acceptance |
|---|---|---|---|---|
| URS-55-1 | new | The application shall build and run on Java 17 with Spring Boot 2.7. | GSK-55 | OQ step 1 |
| URS-55-2 | changed (behaviour kept) | All existing automated tests shall pass with the same run and skip counts as the Boot 1.5 baseline. | GSK-55; demo-ops `docs/BASELINE.md` | OQ step 2 |
| URS-55-3 | changed (behaviour kept) | Code coverage shall still be reported, and `org.ohdsi.webapi.util` coverage shall not fall. | GSK-55; demo-ops `docs/BASELINE.md` | OQ step 3 |
| URS-55-4 | changed (behaviour kept) | Error mapping shall not change in this PR. The GSK-1 fix is a separate change. | demo-ops `docs/RUNBOOK.md` act 3a | OQ step 4 |
| URS-55-5 | changed (behaviour kept) | The REST API shall stay at `/WebAPI` with the same endpoints. | `application.properties` | OQ step 5 |
| URS-55-6 | changed (behaviour kept) | Concept set create and duplicate-name responses shall not change. | GSK-1 baseline | OQ step 6 |
| URS-55-7 | changed (behaviour kept) | Flyway migrations shall run unchanged against PostgreSQL. | `FlywayConfig` | OQ step 7 |

## Unaffected requirements

- Database schema and Flyway migration scripts: no change.
- Shiro authentication and authorisation: library version unchanged (1.13.0). It now runs on Spring 5.
- GSK-1 URS-1 to URS-6: not addressed here; the defect is intentionally still present.
