# Test summary report: GSK-55

Draft. Not a signed GxP record.

| field | value |
|---|---|
| change id | CR-GSK-55 |
| risk / approach | medium CSA |
| build under test | `8cbf8114` |
| CI run | `<pending>` |
| protocols / records | oq-protocol.md, deviation-capa.md |

## Results

From `python3 dev/ci/summary.py` on the local run of 8cbf8114 (JDK 17).

| scope | run | passed | failures | errors | skipped |
|---|---|---|---|---|---|
| new / changed tests | 2 classes changed (`IncidenceRatesShinyPackagingServiceTest`, `CohortPathwaysShinyPackagingServiceTest`), 0 new | | 0 | 0 | |
| full suite | 248 | 244 | 0 | 0 | 4 |

Line coverage (JaCoCo): 9,651 / 132,012, 7.3% (`main`: 9,724 / 131,990, 7.4%).
`org.ohdsi.webapi.util`: 356/731 lines, 147/346 branches (`main`: 362/730, 148/346).

## Requirement coverage

| URS id | tests | result |
|---|---|---|
| URS-55-1 | OQ 1 | pass |
| URS-55-2 | OQ 2, full suite | pass |
| URS-55-3 | OQ 3 | pass with reason; URS line not met as written (DEV-GSK-55-3) |
| URS-55-4 | OQ 4, `./reproduce.sh` | pass |
| URS-55-5 | OQ 5 | pass |
| URS-55-6 | OQ 6 | fail (DEV-GSK-55-2) |
| URS-55-7 | OQ 7 | pass |

## Deviations

| ref | summary | status |
|---|---|---|
| DEV-GSK-55-1 | development runs 1-8 failed on Boot 2 / Java 17 defaults | corrected; runs 9-10 green |
| DEV-GSK-55-2 | 409 body has extra `originalMessage:null`; dates were ISO strings on first try | dates fixed; body for QA decision |
| DEV-GSK-55-3 | `util` coverage 6 lines lower, start-up side effects only | for QA decision |

## Security scan

Local Trivy over the WAR: 165 CVEs (13 critical, 69 high) vs 187 (17 critical, 81 high) on `main`;
38 closed, 16 new (0 critical). CI `scan` check: `<pending>`.

## Conclusion

Draft: the suite, coverage report, defect reproducer, start-up, Flyway and REST checks show the
migration keeps current behaviour, with the two differences in DEV-GSK-55-2 (b) and DEV-GSK-55-3
for QA to accept or reject. PQ (pq-uat-script.md) is not run yet.

| field | value |
|---|---|
| prepared by | https://devin-gtm.devinenterprise.com/sessions/c35b1b7ee6334fbca00709807dc3d950 |
| reviewed by (test engineer) | __________________ |
| date | __________________ |
