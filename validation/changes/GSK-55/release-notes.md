# Release notes: GSK-55

Draft. Not a signed GxP record.

| field | value |
|---|---|
| change id | CR-GSK-55 |
| release | `<pending>` (merge commit) |
| pull request | `<pending>` |
| risk | medium |
| planned date | `<pending>` |

## What changed

WebAPI moves from Spring Boot 1.5 / Java 8 to Spring Boot 2.7 / Java 17. ATLAS screens and REST
paths are the same. ADR: `docs/adr/0001-spring-boot-2-7-java-17.md`.

## Who is affected

Platform team (runtime is now Java 17). ATLAS users should see no change.

## Known issues

- DEV-GSK-55-2: the 409 error body has an extra `"originalMessage":null` field.
- DEV-GSK-55-3: `org.ohdsi.webapi.util` coverage 6 lines lower (no logic change).
- REST docs (Miredot) are not built on Java 17.
- 16 CVEs new in Boot 2.7.18-managed libraries (0 critical); 38 closed. See evidence/trivy-summary.md.

## Deployment

- Steps: deploy the WAR on a Java 17 runtime with `--add-opens java.base/java.net=ALL-UNNAMED`
  (set in the `Dockerfile`); `server.context-path` settings move to `server.servlet.context-path`.
- Schema or data change: none.
- Rollback: revert the PR and redeploy the previous WAR on Java 8.

## Approval

| field | value |
|---|---|
| release manager | __________________ |
| decision (release / hold) | __________________ |
| date | __________________ |
