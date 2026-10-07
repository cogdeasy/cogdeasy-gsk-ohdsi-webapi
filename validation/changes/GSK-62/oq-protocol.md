# OQ protocol: GSK-62

Draft. Not a signed GxP record.

| field | value |
|---|---|
| protocol id | OQ-GSK-62 |
| change id | CR-GSK-62 |
| risk | high (full CSV) |
| build under test | `6bc56e8b` (fix commit) |
| environment | GitHub Actions `tests` on PR #9: https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/actions/runs/37568157926; local JDK 8 run; local ATLAS stack |
| prerequisites | JDK 8, Maven, profile `webapi-postgresql`; embedded PostgreSQL for unit tests; demo-ops local stack with Eunomia for steps 8-10 |

## Baseline (before the fix)

Run on `release/2.15.1-si.3` at `2df02d24`, 2026-10-07:

- ATLAS, cohort #1, Generate on EUNOMIA: COMPLETE, People 625, Records 625, no warning (`evidence/` screenshots and recording, attached to the PR).
- Independent SQL (`evidence/gsk62-independent-count.sql`): CDM 830 / 830, stored cohort 625 / 625, cache 830 / 830; all 205 missing rows have `subject_id % 4 = 0` (`evidence/gsk62-sql-before.txt`).
- `GenerateCohortTaskletTest` (new): 5 run, 4 failures (`evidence/red-run-before-fix.txt`).
- Bisect `v2.15.1` (good) .. `v2.15.1-si.3` (bad) with `GenerateCohortTaskletTest`: first bad commit `25937aee` (`evidence/bisect.txt`).

## Steps

Expected results below were written on 2026-10-07 before the steps were executed on the fix
commit. The baseline run of steps 2-6 happened before this protocol existed (see DEV-GSK-62-2).

| step | URS / AC | action | expected | actual | pass / fail | evidence ref |
|---|---|---|---|---|---|---|
| 1 | - | `mvn -B -q -Pwebapi-postgresql -DskipUnitTests -DskipITtests test-compile` on the fix commit | build succeeds | build succeeds (compiled in the step 2-6 run, BUILD SUCCESS) | pass | evidence/new-tests-after-fix.txt |
| 2 | URS-CG-007 / AC1 | `GenerateCohortTaskletTest#copiesEveryCachedRowIntoCohortTable`: 830 cached rows with ids 1..830 | cohort holds 830 records, 830 people | 830 records, 830 people | pass | target/surefire-reports/TEST-org.ohdsi.webapi.cohortdefinition.GenerateCohortTaskletTest.xml |
| 3 | URS-CG-007 / AC2 | `GenerateCohortTaskletTest#copiesSubjectsWhateverTheirIdValue`: ids -7, -4, 1, 2, 3, 4, 5, 8, 12, 16, 2^31, 2^53 | all 12 ids stored | all 12 ids stored | pass | target/surefire-reports/TEST-org.ohdsi.webapi.cohortdefinition.GenerateCohortTaskletTest.xml |
| 4 | URS-CG-007 / AC2 | `GenerateCohortTaskletTest#copiesSingleSubjectWithIdDivisibleBySliceCount`: one row, id 4 | 1 row stored | 1 row stored | pass | target/surefire-reports/TEST-org.ohdsi.webapi.cohortdefinition.GenerateCohortTaskletTest.xml |
| 5 | URS-CG-008 / AC3 | `GenerateCohortTaskletTest#copiesEachRowOnceAndRegenerationReplacesRows`: 100 rows, copy twice | 100 rows after each run, no duplicates | 100 rows after each run, no duplicates | pass | target/surefire-reports/TEST-org.ohdsi.webapi.cohortdefinition.GenerateCohortTaskletTest.xml |
| 6 | URS-CG-008 / AC4, URS-CG-010 | `GenerateCohortTaskletTest#leavesOtherCohortsAndCopiesStatistics` | other cohort's rows unchanged; summary stats copied | other cohort unchanged; summary stats copied | pass | target/surefire-reports/TEST-org.ohdsi.webapi.cohortdefinition.GenerateCohortTaskletTest.xml |
| 7 | all | `mvn -B -Pwebapi-postgresql test`, then `python3 dev/ci/summary.py` | 0 failures, 0 errors | 253 run, 249 passed, 0 failures, 0 errors, 4 skipped | pass | evidence/full-suite-summary.txt (`6bc56e8b`, 2026-10-07); CI `tests`: `<pending>` |
| 8 | URS-CG-007 / AC1 | rebuild WAR on the fix, restart local stack, ATLAS: cohort #1 -> Generation -> Generate on EUNOMIA | COMPLETE, People 830, Records 830 | COMPLETE, People 830, Records 830 (`/WebAPI/info` commitId 6bc56e8) | pass | gsk62-after-2-generation-complete.png, gsk62-atlas-generation-after.mp4 (on the PR); evidence/impact-after.txt |
| 9 | URS-CG-007 / AC1 | re-run `evidence/gsk62-independent-count.sql` | CDM = stored cohort = 830 / 830; every remainder copied; gap 0 | CDM 830 / 830 = stored 830 / 830 = cache 830 / 830; remainders 0-3 all copied; gap 0 | pass | evidence/gsk62-sql-after.txt, evidence/impact-after.txt |
| 10 | URS-CG-008 | generate cohort #1 a second time in ATLAS | People 830, Records 830 again | People 830, Records 830; 830 rows, 830 distinct; is_valid true | pass | gsk62-after-regen-2-generation-complete.png, gsk62-atlas-generation-after-regen.mp4 (on the PR); evidence/gsk62-sql-after-regen.txt |
| 11 | - | CI checks on the fix PR | `build`, `tests`, `scan` green; no new scan findings | build, tests, scan, Trivy green; tests 253 run, 249 passed, 0 failures, 0 errors, 4 skipped; scan findings not in the job log | pass (findings `<pending>`) | https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/actions/runs/37568157926 |

## Deviations

DEV-GSK-62-1 (the defect: silent incomplete cohort result) and DEV-GSK-62-2 (baseline run and
tests prepared before the URS / OQ expected results were written), in `deviation-capa.md`.

## Execution

| field | value |
|---|---|
| executed by | __________________ |
| execution date | __________________ |
| reviewed by | __________________ |
| review date | __________________ |
