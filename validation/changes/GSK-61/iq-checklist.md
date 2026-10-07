# IQ checklist: GSK-61

Draft. Not a signed GxP record.

| field | value |
|---|---|
| change id | CR-GSK-61 |
| environment | test / production: __________________ |
| release | `<pending>` (fix commit on `release/2.15.1-si.3`) |
| artefact | `WebAPI.war`, checksum `<pending>` |

| # | check | expected | actual | pass / fail | evidence ref |
|---|---|---|---|---|---|
| 1 | artefact checksum matches the CI build | sha256 matches `build` artifact | | | |
| 2 | Java runtime version | JDK 8 | | | |
| 3 | application server / container version | Tomcat as in the current 2.15.1-si.3 deployment | | | |
| 4 | database connection and schema version | Flyway at the same version as 2.15.1-si.3 (no migration in this change) | | | |
| 5 | configuration values for the change | none (no configuration change) | | | |
| 6 | application starts, health endpoint responds | `GET /WebAPI/info` returns 200 with the fix commit id | | | |
| 7 | logs written to the expected location | as current deployment | | | |
| 8 | security scan result recorded | `scan` check, no new critical findings | | | |

## Execution

| field | value |
|---|---|
| executed by | __________________ |
| date | __________________ |
| reviewed by | __________________ |
| review date | __________________ |
