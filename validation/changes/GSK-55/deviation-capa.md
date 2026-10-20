# Deviations and CAPA: GSK-55

Draft. Not a signed GxP record.

## Deviation DEV-GSK-55-1

| field | value |
|---|---|
| deviation id | DEV-GSK-55-1 |
| change id | CR-GSK-55 |
| raised in | oq-protocol.md step 2 (full suite with JDK 17), development runs before formal execution |
| raised by | https://devin-gtm.devinenterprise.com/sessions/c35b1b7ee6334fbca00709807dc3d950 |
| date raised | 2026-10-20 |

### Description

Expected (step 2): surefire 237 run / 0 failures / 0 errors / 3 skipped; failsafe 11 / 0 / 0 / 1.

Actual, development runs of `mvn -B -Pwebapi-postgresql test` on JDK 17 (logs kept in the
session, `post-test2.log` to `post-test9.log`):

| run | surefire result | cause found in the log |
|---|---|---|
| 2 | 238 run, 1 failure, 170 errors, 3 skipped | (a) Boot 2.1+ forbids bean overriding; `JobConfig.jobBuilders` overrides Spring Batch's bean, so every Spring test context failed. (b) `IncidenceRatesShinyPackagingServiceTest`: Mockito 2+ `anyString()` no longer matches `null` |
| 3 | 238 run, 0 failures, 170 errors, 3 skipped | Boot 2 `DataSourceBuilder` picked HikariCP for the Flyway datasource ("jdbcUrl is required with driverClassName") |
| 4 | 238 run, 0 failures, 170 errors, 3 skipped | (c) bean `batchDataSourceInitializer` does not exist when `spring.batch.jdbc.initialize-schema=never`, but `JobConfig` had `@DependsOn` on it. (d) Redshift JDBC 1.2.10 static init fails on Java 17: `InaccessibleObjectException ... java.base does not "opens java.net"` |
| 5 | stopped | (d) again |
| 6 | 238 run, 0 failures, 170 errors, 3 skipped | (f) Boot 2.6+ forbids circular references (`TagService` <-> `CDMCacheService`) |
| targeted (`PermissionTest`, `CohortTaggingTest`) | 7 run, 7 errors | (g) Hibernate 5.6 rejects HQL in `IncidenceRateAnalysisRepository`; (h) Spring 5 does not inject the `null` `authDataSource`; (i) spring-hateoas 1.5 with spring-plugin 1.1; (j) Spring JMS 5 needs JMS 2.0 API |
| 8 | 238 run, 0 failures, 1 error, 3 skipped; failsafe not reached | (k) Mockito 2+ strict runner: "unnecessary Mockito stubbings" in `CohortPathwaysShinyPackagingServiceTest`, counted as an extra test (explains 238 vs 237); (l) Jackson modules 2.13.5 on databind 2.12.7: `NoClassDefFoundError: AnnotationIntrospector$XmlExtensions` |

Surefire also reports 238 tests, not 237: the extra one is Mockito's strict-stubs check, see (k).

This deviation was written after runs 2 to 4, not before the first fix. That is itself a departure
from the deviation procedure and is recorded here.

### Impact

| area | impact | reason |
|---|---|---|
| patient safety | no | development build on a branch; nothing deployed |
| product quality | no | failures were found before PR and before formal OQ execution |
| data integrity | no | tests run against throw-away embedded PostgreSQL |
| validation status | yes | step 2 cannot pass until each cause is corrected and the suite re-run |

Classification: __________________ (critical / major / minor, set by QA)

### Root cause

Draft. Boot 2.x and Java 17 change defaults that the Boot 1.5 / Java 8 code relied on: bean
overriding, connection-pool choice, batch initializer bean name and condition, Mockito null
matching, and strong encapsulation of JDK internals (JEP 403). The playbook did not list them
because they only show up when Spring contexts start, after compilation succeeds.

### CAPA

| type | action | owner | due | status |
|---|---|---|---|---|
| correction | (a) `spring.main.allow-bean-definition-overriding=true`; (b) `nullable(String.class)` in one verify (Mockito 1 meaning of `anyString()`); (c) Flyway datasource asks for tomcat-jdbc; (d) remove `@DependsOn("batchDataSourceInitializer")`, schema init is off; (e) `--add-opens java.base/java.net=ALL-UNNAMED` for test JVMs and documented for the runtime JVM. (f) `spring.main.allow-circular-references=true`; (g) keep Hibernate 5.4.2 as on `main`; (h) `@Autowired(required = false)` on `AtlasRegularSecurity.jdbcDataSource`; (i) pin `spring-plugin-core` 2.0.0; (j) `jakarta.jms-api` instead of `geronimo-jms_1.1_spec`. (k) `MockitoJUnitRunner.Silent` on that one test; (l) import `jackson-bom` ${jackson.version} before the Boot BOM. Commits `fdb211f0`, `8cbf8114` | | | done; runs 9 and 10 green (237/0/0/3, 11/0/0/1) |
| corrective | add these items to the demo-ops playbook `gsk-boot2-migration.md` | | | done (demo-ops PR `<pending>`) |
| preventive | playbook step: run one Spring context test (`-Dtest=PermissionTest`) right after the first green compile, before the full suite | | | in playbook (demo-ops PR `<pending>`) |

### Closure

| field | value |
|---|---|
| effectiveness check | step 2 re-run on 8cbf8114 (run 10): surefire 237/0/0/3, failsafe 11/0/0/1, meets its expected result |
| closed by (QA) | __________________ |
| date | __________________ |

## Deviation DEV-GSK-55-2

| field | value |
|---|---|
| deviation id | DEV-GSK-55-2 |
| change id | CR-GSK-55 |
| raised in | oq-protocol.md step 6 (duplicate concept set on the local ATLAS stack) |
| raised by | https://devin-gtm.devinenterprise.com/sessions/c35b1b7ee6334fbca00709807dc3d950 |
| date raised | 2026-10-20 |

### Description

Expected (step 6): first create 200; second create 409 with the same body as on Boot 1.5.

Actual, same requests against Boot 1.5 (`main` 37e8066, JDK 8) and Boot 2.7 on the same stack
(`evidence/boot1.5-main-37e8066/`, `evidence/boot2.7-8cbf811/`):

| build | create | duplicate | difference from Boot 1.5 |
|---|---|---|---|
| Boot 1.5, 37e8066 | 200, `createdDate` epoch ms | 409, `payload`, `headers` | - |
| Boot 2.7, 2d55ddd (first try) | 200, `createdDate` ISO-8601 string | 409 | (a) dates are strings: Boot 2 sets `write-dates-as-timestamps=false` by default |
| Boot 2.7, 8cbf811 | 200, `createdDate` epoch ms | 409, same `payload.message` and `headers` | (b) extra top-level `"originalMessage":null`; `payload` keys in a different order |

(b) comes from Spring 5: `GenericExceptionMapper` returns `org.springframework.messaging.support.ErrorMessage`,
which gained `getOriginalMessage()`. JSON key order has no meaning to clients. The raw database
text in the 409 is the GSK-1 defect kept on purpose (URS-55-4).

### Impact

| area | impact | reason |
|---|---|---|
| patient safety | no | nothing deployed |
| product quality | (a) yes, fixed; (b) low | (a) would change every date in the API for ATLAS; (b) one extra null field on the error body only |
| data integrity | no | stored data unchanged; GET after the conflict shows only the first concept set |
| validation status | yes | step 6 does not meet its expected result exactly |

Classification: __________________ (critical / major / minor, set by QA)

### CAPA

| type | action | owner | due | status |
|---|---|---|---|---|
| correction | (a) `spring.jackson.serialization.write-dates-as-timestamps=true`, commit `8cbf8114` | | | done, re-run shows epoch ms |
| correction | (b) none in this PR: changing the error body belongs to GSK-1 (Q1), which replaces it with a safe body | | | proposed, for QA decision |
| preventive | playbook: compare one JSON response from `main` and the branch on the same stack | | | in playbook (demo-ops PR `<pending>`) |

### Closure

| field | value |
|---|---|
| effectiveness check | (a) re-run on 8cbf811 returns epoch ms; (b) QA to accept or ask for a fix |
| closed by (QA) | __________________ |
| date | __________________ |

## Deviation DEV-GSK-55-3

| field | value |
|---|---|
| deviation id | DEV-GSK-55-3 |
| change id | CR-GSK-55 |
| raised in | oq-protocol.md step 3 (JaCoCo), against URS-55-3 |
| raised by | https://devin-gtm.devinenterprise.com/sessions/c35b1b7ee6334fbca00709807dc3d950 |
| date raised | 2026-10-20 |

### Description

URS-55-3 says `org.ohdsi.webapi.util` coverage shall not fall. Actual: lines 356/731 (48.7%) vs
362/730 (49.6%); branches 147/346 (42.5%) vs 148/346 (42.8%). Step 3's expected result allows a
stated reason; the URS line does not.

Per-line diff of the two `jacoco.xml` files: `GenericExceptionMapper` lines 49-51 (class load,
logger and a constant) and `SourceUtils.hasSourceDaimon` lines 47-51 were covered on Boot 1.5
only as a side effect of start-up, by no test that asserts on them; `ExpiringMultimap` line 45 is a
new line entry from javac 17. No logic in the package changed.

### Impact

| area | impact | reason |
|---|---|---|
| patient safety | no | nothing deployed |
| product quality | no | the lost lines were never asserted by a test |
| validation status | yes | URS-55-3 not met as written |

Classification: __________________ (critical / major / minor, set by QA)

### CAPA

| type | action | owner | due | status |
|---|---|---|---|---|
| correction | none proposed in this PR: adding tests for unrelated classes widens the change | | | for QA decision |
| corrective | GSK-1 (Q1) adds `GenericExceptionMapperTest`, which covers these mapper lines directly | | | `<pending>` |
| preventive | word coverage URS lines as "covered by tests that assert", not raw line totals | | | for QA decision |

### Closure

| field | value |
|---|---|
| closed by (QA) | __________________ |
| date | __________________ |
