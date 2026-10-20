# CR-GSK-55

Draft change record. Not a signed GxP record.

| field | value |
|---|---|
| change id | CR-GSK-55 |
| application | OHDSI WebAPI (GSK demo fork) |
| tier | silver |
| ticket | https://cog-gtm.atlassian.net/browse/GSK-55 |
| pull request | https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/pull/12 |
| commits | `fdb211f0` (Boot 2.7 / Java 17), `2d55ddd0` (ADR), `8cbf8114` (date JSON, commons-collections), evidence commit `<pending>` |
| author | Devin (https://devin-gtm.devinenterprise.com/sessions/c35b1b7ee6334fbca00709807dc3d950) |
| description | Spring Boot 1.5.22 -> 2.7.18 and Java 8 -> 17, per demo-ops `playbooks/gsk-boot2-migration.md` and `docs/adr/0001-spring-boot-2-7-java-17.md`. Spring Data 2 / Jersey 2.35 / Mockito 4 API changes; Boot 1.5 defaults kept by property where Boot 2 changed them; Flyway 4.2, Shiro 1.13, Hibernate 5.4.2, Jackson 2.12.7 and `javax.*` unchanged. CI and Dockerfile on Java 17. |
| root cause | not a defect fix |
| risk | medium - platform-wide framework upgrade; see risk-assessment.md |
| URS impact | URS-55-1 to URS-55-7 (urs-delta.md) |
| verification | full suite on 8cbf8114 with JDK 17: 248 run, 244 passed, 0 failures, 0 errors, 4 skipped (same as `main`); OQ steps 1-5, 7 pass, step 6 fail (DEV-GSK-55-2); coverage DEV-GSK-55-3; CI run `<pending>` |
| security scan | local Trivy over the WAR: 165 CVEs vs 187 on `main`; 38 closed, 16 new (0 critical), see evidence/trivy-summary.md; CI `scan` `<pending>` |
| rollback | revert the PR; no schema or data change (Flyway migrations unchanged). Runtime must go back to Java 8 with it |
| reviewer | __________________ (named GSK approver) |
| decision (approve / reject / rework) | __________________ |
| date | __________________ |
