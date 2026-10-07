# Test summary report: GSK-61

Draft. Not a signed GxP record.

| field | value |
|---|---|
| change id | CR-GSK-61 |
| risk / approach | high, full CSV |
| build under test | `<pending>` |
| CI run | `<pending>` |
| protocols / records | oq-protocol.md |

## Results

From `python3 dev/ci/summary.py`.

| scope | run | passed | failures | errors | skipped |
|---|---|---|---|---|---|
| new tests, before the fix (`2df02d24`) | 5 | 1 | 4 | 0 | 0 |
| new tests, after the fix | `<pending>` | `<pending>` | `<pending>` | `<pending>` | `<pending>` |
| full suite | `<pending>` | `<pending>` | `<pending>` | `<pending>` | `<pending>` |

Line coverage (JaCoCo): `<pending>`

## Requirement coverage

| URS id | tests | result |
|---|---|---|
| URS-CG-007 | `GenerateCohortTaskletTest#copiesEveryCachedRowIntoCohortTable`; `#copiesSubjectsWhateverTheirIdValue`; `#copiesSingleSubjectWithIdDivisibleBySliceCount`; ATLAS OQ step 8; SQL OQ step 9 | `<pending>` |
| URS-CG-008 | `GenerateCohortTaskletTest#copiesEachRowOnceAndRegenerationReplacesRows`; `#leavesOtherCohortsAndCopiesStatistics`; ATLAS OQ step 10 | `<pending>` |

## Deviations

| ref | summary | status |
|---|---|---|
| DEV-GSK-61-1 | silent incomplete cohort result on 2.15.1-si.3 | open (QA) |
| DEV-GSK-61-2 | baseline run before URS / OQ expected results were written | open (QA) |

## Security scan

`scan` check: `<pending>`, new findings: `<pending>`

## Conclusion

`<pending>`

| field | value |
|---|---|
| prepared by | Devin (https://app.devin.ai/sessions/0e6a1ba1a6104b5fb5e0efb8886f9605) |
| reviewed by (test engineer) | __________________ |
| date | __________________ |
