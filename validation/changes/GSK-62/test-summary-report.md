# Test summary report: GSK-62

Draft. Not a signed GxP record.

| field | value |
|---|---|
| change id | CR-GSK-62 |
| risk / approach | high, full CSV |
| build under test | `6bc56e8b` |
| CI run | `<pending>` |
| protocols / records | oq-protocol.md |

## Results

From `python3 dev/ci/summary.py`.

| scope | run | passed | failures | errors | skipped |
|---|---|---|---|---|---|
| new tests, before the fix (`2df02d24`) | 5 | 1 | 4 | 0 | 0 |
| new tests, after the fix (`6bc56e8b`) | 5 | 5 | 0 | 0 | 0 |
| full suite (`6bc56e8b`, local JDK 8) | 253 | 249 | 0 | 0 | 4 |

Line coverage (JaCoCo): 9762 / 131999, 7.4% (local run)

## Requirement coverage

| URS id | tests | result |
|---|---|---|
| URS-CG-007 | `GenerateCohortTaskletTest#copiesEveryCachedRowIntoCohortTable`; `#copiesSubjectsWhateverTheirIdValue`; `#copiesSingleSubjectWithIdDivisibleBySliceCount`; ATLAS OQ step 8; SQL OQ step 9 | pass |
| URS-CG-008 | `GenerateCohortTaskletTest#copiesEachRowOnceAndRegenerationReplacesRows`; `#leavesOtherCohortsAndCopiesStatistics`; ATLAS OQ step 10 | pass |

## Deviations

| ref | summary | status |
|---|---|---|
| DEV-GSK-62-1 | silent incomplete cohort result on 2.15.1-si.3 | open (QA) |
| DEV-GSK-62-2 | baseline run before URS / OQ expected results were written | open (QA) |

## Security scan

`scan` check: `<pending>`, new findings: `<pending>`

## Conclusion

Draft: on the fix commit the new tests pass (5 / 5, red 4 / 5 before), the full suite has no failures or errors, ATLAS shows 830 / 830 for cohort #1 on EUNOMIA and the independent SQL gap is 0. CI result `<pending>`. Cohorts generated on 2.15.1-si.3 still need re-generation (DEV-GSK-62-1).

| field | value |
|---|---|
| prepared by | Devin (https://app.devin.ai/sessions/7fc803986df240c895a535aecec1fcdc) |
| reviewed by (test engineer) | __________________ |
| date | __________________ |
