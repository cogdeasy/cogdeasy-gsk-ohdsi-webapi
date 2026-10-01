# OQ protocol: GSK-1

Draft. Not a signed GxP record.

| field | value |
|---|---|
| protocol id | OQ-GSK-1 |
| change id | CR-GSK-1 |
| risk | medium (CSA, scripted tests for the changed function) |
| build under test | `<pending>` (fix commit) |
| environment | GitHub Actions `tests` check on the fix PR: `<pending>`; local re-run with JDK 8 |
| prerequisites | JDK 8, Maven, profile `webapi-postgresql`; no external database (tests use embedded PostgreSQL) |

## Baseline (before the fix)

Run on `main` at `3de09d96`, `./reproduce.sh`, exit code 1:

```
FAIL   no cause                       mapper threw java.lang.NullPointerException
FAIL   single cause                   mapper threw java.lang.NullPointerException
FAIL   nested cause without Detail    409 "duplicate key value violates unique constraint "uq_cs_name""  <- raw database text
ok     nested cause with Detail       409 "Key (name)=(Diabetes) already exists."

REPRODUCED: 3 of 4 scenarios return an unsafe response
```

## Steps

Expected results were written before execution. Test names are the planned regression tests in
`src/test/java/org/ohdsi/webapi/util/GenericExceptionMapperTest.java`; confirm them against the
fix PR.

| step | URS / AC | action | expected | actual | pass / fail | evidence ref |
|---|---|---|---|---|---|---|
| 1 | - | `mvn -B -Pwebapi-postgresql -DskipUnitTests -DskipITtests compile` on the fix commit | build succeeds | `<pending>` | `<pending>` | `<pending>` |
| 2 | URS-1..4 | `./reproduce.sh` | all 4 scenarios `ok`, exit 0 | `<pending>` | `<pending>` | `<pending>` |
| 3 | URS-1 / AC1 | map `new DataIntegrityViolationException("could not execute statement")` | status 409; no exception thrown | `<pending>` | `<pending>` | `<pending>` |
| 4 | URS-2 / AC2 | map a violation with one `RuntimeException` cause and no nested cause | status 409; no exception thrown | `<pending>` | `<pending>` | `<pending>` |
| 5 | URS-3 / AC3 | map a violation whose nested `SQLException` message is `ERROR: duplicate key value violates unique constraint "uq_cs_name"` | status 409; message is generic; message contains neither `duplicate key` nor `uq_cs_name` | `<pending>` | `<pending>` | `<pending>` |
| 6 | URS-4 / AC4 | as step 5, message followed by `\n  Detail: Key (name)=(Diabetes) already exists.` | status 409; message is `Key (name)=(Diabetes) already exists.` | `<pending>` | `<pending>` | `<pending>` |
| 7 | URS-5 / AC5 | map `UnauthorizedException`, `ForbiddenException`, `NotFoundException`, `BadRequestException`, `ConceptRecommendedNotInstalledException`, and an `IllegalStateException` | 403, 403, 404, 400, 501, and 500 with message `An exception occurred: java.lang.IllegalStateException` | `<pending>` | `<pending>` | `<pending>` |
| 8 | URS-6 | step 5 with a log appender on `GenericExceptionMapper` | log event at ERROR contains `uq_cs_name`; response does not | `<pending>` | `<pending>` | `<pending>` |
| 9 | AC6 | `mvn -B -Pwebapi-postgresql test`, then `python3 dev/ci/summary.py` | 0 failures, 0 errors | `<pending>` | `<pending>` | `<pending>` |
| 10 | AC6 | CI checks on the fix PR | `build`, `tests`, `scan` green; no new scan findings | `<pending>` | `<pending>` | `<pending>` |

## Deviations

None raised yet.

## Execution

| field | value |
|---|---|
| executed by | __________________ |
| execution date | __________________ |
| reviewed by | __________________ |
| review date | __________________ |
