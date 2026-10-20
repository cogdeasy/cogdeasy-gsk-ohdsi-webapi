# ADR 0001: Spring Boot 1.5 -> 2.7 and Java 8 -> 17

Status: Proposed. Decision and approver fields are left for GSK.

| field | value |
|---|---|
| ticket | GSK-55 (https://cog-gtm.atlassian.net/browse/GSK-55) |
| playbook | cogdeasy/cogdeasy-gsk-demo-ops `playbooks/gsk-boot2-migration.md` |
| decided by | __________________ |
| date | __________________ |

## Context

WebAPI runs on Spring Boot 1.5.22 (end of life since August 2019) and Java 8. Boot 1.5 keeps
Spring 4.3, Spring Data 1.11, Spring Batch 3 and Tomcat 8.5 on the classpath, all of them out of
support. Boot 3 needs Java 17 and `jakarta.*`. Going there in one step would combine a namespace
rename with a framework upgrade, which is too much to review safely in a silver-tier PR.
Upstream OHDSI did that move in commit 62b9cb1d (Boot 3.2 / Java 21).

## Decision

Upgrade to Spring Boot 2.7.18 and Java 17. Keep `javax.*`. Keep these libraries where they are,
because each one is its own family with its own risk:

| kept | why |
|---|---|
| Flyway 4.2.0 | Boot 2.7's Flyway auto-configuration needs Flyway 8. `FlywayAutoConfiguration` is excluded and `FlywayConfig` keeps its own initializer. The Arachne migration resolver depends on the Flyway 4 API. |
| Shiro 1.13.0 | Shiro is the security layer. Spring Security is used only for `BCryptPasswordEncoder` (now 5.7), so there is no `WebSecurityConfigurerAdapter` work. |
| tomcat-jdbc pool for the Flyway datasource | Boot 2 `DataSourceBuilder` prefers HikariCP, which is on the classpath for `AuthDataSource`. `FlywayConfig` now asks for tomcat-jdbc explicitly, as Boot 1.5 chose it. |
| JUnit 4 tests | run on the JUnit Platform via `junit-vintage-engine`. New tests may use JUnit 5. |
| Hibernate 5.4.2 | same version as on `main`. Boot 2.7 manages 5.6, but 5.6 rejects the HQL in `IncidenceRateAnalysisRepository.findOneWithExecutionsOnExistingSources` at start-up. Hibernate is its own family and goes in its own PR. |
| Jackson 2.12.7 | same as on `main`. `jackson-bom` is imported before the Boot BOM so Boot 2.7 does not pull 2.13 modules onto the 2.12 databind (`NoClassDefFoundError: AnnotationIntrospector$XmlExtensions`). |
| jettison 1.2 | was transitive via Spring Batch 3. It is now declared directly, at the same version, for `EvidenceService`. |

Code changes that the upgrade forces:

- Boot class moves: `SpringBootServletInitializer`, `ErrorMvcAutoConfiguration`, `DataSourceBuilder`.
- Spring Data 2: `findOne(id)` -> `findById(id).orElse(null)`; `save(Iterable)` -> `saveAll`;
  `delete(id)` / `delete(Iterable)` -> `deleteById` / `deleteAll`; `PageRequest.of`, `Sort.by`.
  Repository `findById` overrides that clash with `CrudRepository.findById` were removed.
- spring-data-jpa-entity-graph 1.11 -> 2.7.10; `EntityGraphUtils.fromName(name, false)`.
- Jersey 2.35 (HK2 injection API removed): `PageableValueFactoryProvider` is now a `ValueParamProvider`.
- Spring Batch 4: `batchDatabaseInitializer` -> `batchDataSourceInitializer`.
- Boot 2.1+ forbids bean overriding by default. `JobConfig` overrides Spring Batch `jobBuilders`, so
  `spring.main.allow-bean-definition-overriding=true` keeps the Boot 1.5 behaviour.
- Properties: `server.servlet.context-path`, `spring.sql.init.mode`, `spring.batch.jdbc.initialize-schema`.
- Boot 2.6+ forbids circular references (e.g. `TagService` <-> `CDMCacheService`):
  `spring.main.allow-circular-references=true`, as on Boot 1.5.
- Spring 5 does not inject a `null` `@Bean` into a required field. `AuthDataSource` returns null
  when the auth database is unreachable, so `AtlasRegularSecurity.jdbcDataSource` is
  `@Autowired(required = false)`; the existing `jdbcDataSource != null` check handles it as before.
- spring-hateoas 1.5 (from the Boot BOM) needs `spring-plugin-core` 2.0; it is pinned there.
- Spring JMS 5 needs the JMS 2.0 API: `jakarta.jms-api` 2.0.3 (still `javax.jms`) replaces
  `geronimo-jms_1.1_spec`.
- Redshift JDBC 1.2.10 reflects into `java.net`, blocked on Java 17 (JEP 403). Test JVMs and the
  Docker image run with `--add-opens java.base/java.net=ALL-UNNAMED`; the Docker images move to
  Temurin 17. Any other runtime must add the same flag until the Redshift driver is upgraded.
- Mockito 1 -> 4 in tests: `ArgumentMatchers`, `org.mockito.junit.MockitoJUnitRunner`. Mockito 2+
  `anyString()` no longer matches `null`; one verify uses `nullable(String.class)`, which is what
  Mockito 1 `anyString()` meant.
  Mockito 2+ `MockitoJUnitRunner` is strict; `CohortPathwaysShinyPackagingServiceTest` uses
  `MockitoJUnitRunner.Silent` (Mockito 1 behaviour). Strict mode had added a 238th pseudo-test.
- Build: `gmaven-plugin` 1.5 (fails on Java 17) -> `gmavenplus-plugin` 3.0.2; CI on Temurin 17.

Out of scope: the GSK-1 / GSK-102 error-mapper defect. It is the Q1 live act in the demo runbook,
so `./reproduce.sh` still exits 1 after this change.

## Consequences

- Security patches for the Spring family resume (Boot 2.7 OSS support has ended; commercial
  support runs to 2029). The Trivy `scan` job reports the CVE delta on the PR.
- Java 17 is required to build and run. The live demo environment on `main` stays on Java 8
  until this PR is merged.
- Next steps, one PR each: Hibernate 5.4 -> 5.6; Flyway 4 -> 8; Shiro 1.13 -> 2.x; Redshift JDBC 2.x
  (drops `--add-opens`); remove `allow-circular-references`; then Boot 3 / `jakarta`.

## Alternatives considered

| option | why not |
|---|---|
| Straight to Boot 3 / Java 21 (as upstream did) | combines the `javax` -> `jakarta` rename with the framework upgrade in one silver-tier PR |
| Java 11 (GSK-55 text) | Boot 2.7 supports it, but Java 11 public updates have ended and Boot 3 needs 17 anyway |
| Stay on 1.5 | no security fixes for Spring 4.3 / Tomcat 8.5 |
