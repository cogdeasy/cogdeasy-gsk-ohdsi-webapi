# Test summary report: GSK-61

Draft. Not a signed GxP record.

| field | value |
|---|---|
| change id | CR-GSK-61 |
| risk / approach | high, full CSV |
| build under test | `2419b469` |
| CI run | PR #8 checks https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/pull/8/checks (build job 112615548453, tests job 112615548577, scan job 112615860671, Trivy job 112615975596) |
| protocols / records | oq-protocol.md |

## Results

From `python3 dev/ci/summary.py`.

| scope | run | passed | failures | errors | skipped |
|---|---|---|---|---|---|
| new tests, before the fix (`2df02d24`) | 5 | 1 | 4 | 0 | 0 |
| new tests, after the fix (`2419b469`) | 5 | 5 | 0 | 0 | 0 |
| full suite (`2419b469`, local JDK 8) | 253 | 249 | 0 | 0 | 4 |

Line coverage (JaCoCo): 9762 / 131999, 7.4% (local run)

## Requirement coverage

| URS id | tests | result |
|---|---|---|
| URS-CG-007 | `GenerateCohortTaskletTest#copiesEveryCachedRowIntoCohortTable`; `#copiesSubjectsWhateverTheirIdValue`; `#copiesSingleSubjectWithIdDivisibleBySliceCount`; ATLAS OQ step 8; SQL OQ step 9 | pass |
| URS-CG-008 | `GenerateCohortTaskletTest#copiesEachRowOnceAndRegenerationReplacesRows`; `#leavesOtherCohortsAndCopiesStatistics`; ATLAS OQ step 10 | pass |

## Deviations

| ref | summary | status |
|---|---|---|
| DEV-GSK-61-1 | silent incomplete cohort result on 2.15.1-si.3 | open (QA) |
| DEV-GSK-61-2 | baseline run before URS / OQ expected results were written | open (QA) |

## Security scan

`scan` check: passed; Trivy: passed; new findings: none reported (checks green)

## Conclusion

Draft: on the fix commit the new tests pass (5 / 5, red 4 / 5 before), the full suite has no failures or errors, ATLAS shows 830 / 830 for cohort #1 on EUNOMIA and the independent SQL gap is 0. CI `build`, `tests`, `scan` and Trivy passed on PR #8. Cohorts generated on 2.15.1-si.3 still need re-generation (DEV-GSK-61-1).

| field | value |
|---|---|
| prepared by | Devin (https://app.devin.ai/sessions/0e6a1ba1a6104b5fb5e0efb8886f9605) |
| reviewed by (test engineer) | __________________ |
| date | __________________ |
