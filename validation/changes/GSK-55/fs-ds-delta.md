# Functional and design spec delta: GSK-55

Draft. Not a signed GxP record.

| field | value |
|---|---|
| change id | CR-GSK-55 |
| URS delta | urs-delta.md |
| pull request | https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/pull/12 |

## Functional spec delta

| FS id | type | behaviour | satisfies |
|---|---|---|---|
| FS-55-1 | changed | The system builds with Maven and runs on Java 17 with Spring Boot 2.7.18. | URS-55-1 |
| FS-55-2 | unchanged | All REST endpoints stay under `/WebAPI` with the same paths and JSON (dates as epoch ms). | URS-55-5, URS-55-6 |
| FS-55-3 | unchanged | Unhandled exceptions go through the same `GenericExceptionMapper`; the GSK-1 defect is kept. | URS-55-4 |
| FS-55-4 | unchanged | Flyway 4.2 applies the same migrations to the `webapi` schema at start-up. | URS-55-7 |

## Design spec delta

| DS id | component | design | satisfies |
|---|---|---|---|
| DS-55-1 | `pom.xml` | Boot BOM 2.7.18; `maven.compiler.release` 17; JUnit Vintage; GMavenPlus; Hibernate 5.4.2, Jackson BOM 2.12.7, commons-collections 3.2.2, `spring-plugin-core` 2.0, `jakarta.jms-api` pinned; Miredot off on Java 9+; `--add-opens java.base/java.net` for test JVMs | FS-55-1 |
| DS-55-2 | `WebApi`, `FlywayConfig` | Boot 2 package moves; Boot Flyway auto-config excluded, own Flyway bean on a tomcat-jdbc datasource | FS-55-4 |
| DS-55-3 | `application.properties` | `server.servlet.context-path`, `spring.sql.init.mode`, `spring.batch.jdbc.initialize-schema`, `allow-bean-definition-overriding`, `allow-circular-references`, `write-dates-as-timestamps=true` | FS-55-2 |
| DS-55-4 | `PageableValueFactoryProvider`, `JerseyConfig` | Jersey 2.35 `ValueParamProvider` | FS-55-2 |
| DS-55-5 | repositories and services | Spring Data 2 (`findById`, `saveAll`, `deleteById`, `PageRequest.of`, entity graph 2.7) | FS-55-1 |
| DS-55-6 | `AtlasRegularSecurity` | `authDataSource` optional (Spring 5 does not inject `null` beans); existing null check unchanged | FS-55-1 |
| DS-55-7 | `.github/workflows/ci.yml`, `Dockerfile` | Java 17 for `build` and `tests`; Temurin 17 images with `--add-opens` | FS-55-1 |

## Interfaces and data

- API contract changes: 409 error body gains `"originalMessage":null` (DEV-GSK-55-2). No other change seen.
- Database schema or data changes: none.
- Configuration changes: Java 17 runtime with `--add-opens java.base/java.net=ALL-UNNAMED`; `server.context-path` is now `server.servlet.context-path`.

## Unaffected design areas

- Security provider logic (Shiro 1.13): only the injection of `authDataSource` changed.
- SQL templates and SqlRender: not touched.
- `GenericExceptionMapper`: not touched (GSK-1 lane).
