# OQ protocol: GSK-55

Draft. Not a signed GxP record. Expected results were written before the post-migration run.

| field | value |
|---|---|
| ticket | GSK-55 (https://cog-gtm.atlassian.net/browse/GSK-55) |
| build under test | branch `devin/gsk-55-spring-boot-2-7`, commit `8cbf8114` |
| environment | Ubuntu VM, OpenJDK 17, Maven 3.6.3, embedded PostgreSQL (zonky), profile `webapi-postgresql` |
| executed by | __________________ |
| date | __________________ |

| step | URS | action | expected result | actual result | pass / fail |
|---|---|---|---|---|---|
| 1 | URS-55-1 | `mvn -B -Pwebapi-postgresql -DskipUnitTests -DskipITtests compile` with JDK 17 | BUILD SUCCESS, class files major version 61 | Run as `package` (compile is part of it): BUILD SUCCESS; `javap -v` on `org.ohdsi.webapi.WebApi` shows major version 61. Evidence: `package.log` (session) | pass |
| 2 | URS-55-2 | `mvn -B -Pwebapi-postgresql test` with JDK 17 | surefire 237 run, 0 failures, 0 errors, 3 skipped; failsafe 11 run, 0 failures, 0 errors, 1 skipped (same as baseline) | surefire 237 run, 0 failures, 0 errors, 3 skipped; failsafe 11 run, 0 failures, 0 errors, 1 skipped; BUILD SUCCESS. `dev/ci/summary.py`: 248 run, 244 passed. Evidence: `evidence/boot2.7-8cbf811/test-summary.txt` (run 10 on 8cbf8114; run 9 on the parent commit gave the same totals). Runs 1-8 failed first, see DEV-GSK-55-1 | pass |
| 3 | URS-55-3 | read `target/site/jacoco/jacoco.csv` | report produced; `org.ohdsi.webapi.util` 362/730 lines and 148/346 branches, or a stated reason for any change | Report produced. `org.ohdsi.webapi.util` 356/731 lines (48.7%), 147/346 branches (42.5%). Stated reason: no test calls the changed lines directly. `GenericExceptionMapper` lines 49-51 (class load only) and `SourceUtils.hasSourceDaimon` lines 47-51 were hit on Boot 1.5 as a side effect of context start-up and are not hit on Boot 2.7; `ExpiringMultimap` line 45 is a new line entry from javac 17 (+1 total line). No logic in these classes changed. Overall 9,651/132,012 lines (7.3%) vs 9,724/131,990 (7.4%). See DEV-GSK-55-3 (URS-55-3) | pass (reason stated) |
| 4 | URS-55-4 | `./reproduce.sh` with JDK 17 | exit 1; the same four lines as baseline (3 of 4 unsafe). The GSK-1 defect is kept on purpose for the Q1 act | exit 1; the same four lines as baseline: NPE, NPE, raw `uq_cs_name` text, `Key (name)=(Diabetes) already exists.` (3 of 4 unsafe). Evidence: `evidence/boot2.7-8cbf811/reproduce.log` | pass |
| 5 | URS-55-5 | start the WAR against PostgreSQL (local ATLAS stack) and call `GET /WebAPI/info` and `GET /WebAPI/source/sources` | HTTP 200 for both; context path is still `/WebAPI` | Local ATLAS stack (demo-ops `local-stack`, runtime image on Temurin 17.0.20.1, PostgreSQL 16.4). `GET /WebAPI/info` 200, `buildInfo.commitId` `8cbf811`, branch `devin/gsk-55-spring-boot-2-7`; `GET /WebAPI/source/sources` 200, returns `EUNOMIA`. Evidence: `evidence/boot2.7-8cbf811/runtime-info-sources.txt` | pass |
| 6 | URS-55-6 | create a concept set, then create a second one with the same name, through the REST API | first returns 200; second returns 409 with the same body as on Boot 1.5 (defect kept) | first 200; second 409, same `payload.message` (`Key (concept_set_name)=(...) already exists...`) and `headers` as Boot 1.5 `main` on the same stack, but the body has an extra `"originalMessage":null` and different key order. On the first try (2d55ddd) dates were ISO strings, fixed in 8cbf811. GET after conflict: one concept set; test data deleted (204). Evidence: `evidence/boot*/runtime-duplicate-cs.txt`, DEV-GSK-55-2 | fail, see DEV-GSK-55-2 |
| 7 | URS-55-7 | Flyway on start-up | log shows schema up to date or migrations applied; no Flyway error | Flyway 4.2.0: baselined `webapi.schema_history` at 2.2.5.20180212152023, `Successfully applied 153 migrations`, then `Successfully applied 5 migrations`; `Started WebApi in 11.231 seconds`; no Flyway error. Same counts on Boot 1.5 `main`. Evidence: `evidence/*/flyway-startup.log` | pass |
