# IQ checklist: <ticket key>

Draft. Not a signed GxP record.

Installation qualification. Used for CSV (high risk) only. Run in each target environment after
deployment.

| field | value |
|---|---|
| change id | CR-<ticket key> |
| environment | <test / production> |
| release | <version or commit sha> |
| artefact | `WebAPI.war`, checksum <sha256> |

| # | check | expected | actual | pass / fail | evidence ref |
|---|---|---|---|---|---|
| 1 | artefact checksum matches the CI build | sha256 matches `build` artifact | | | |
| 2 | Java runtime version | JDK 8 | | | |
| 3 | application server / container version | <expected> | | | |
| 4 | database connection and schema version | Flyway at expected version | | | |
| 5 | configuration values for the change | <expected settings> | | | |
| 6 | application starts, health endpoint responds | `GET /WebAPI/info` returns 200 | | | |
| 7 | logs written to the expected location | <path> | | | |
| 8 | security scan result recorded | `scan` check, no new critical findings | | | |

## Execution

| field | value |
|---|---|
| executed by | __________________ |
| date | __________________ |
| reviewed by | __________________ |
| review date | __________________ |
