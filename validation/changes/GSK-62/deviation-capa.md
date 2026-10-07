# Deviations and CAPA: GSK-62

Draft. Not a signed GxP record.

## Deviation DEV-GSK-62-1

| field | value |
|---|---|
| deviation id | DEV-GSK-62-1 |
| change id | CR-GSK-62 |
| raised in | production use of release 2.15.1-si.3 (reported in GSK-62); reproduced in OQ baseline |
| raised by | Devin (https://app.devin.ai/sessions/7fc803986df240c895a535aecec1fcdc) |
| date raised | 2026-10-07 |

### Description

Expected: generating cohort #1 "Demo new users of diclofenac" on EUNOMIA gives People = 830,
Records = 830 (validated baseline). Actual on 2.15.1-si.3: People = 625, Records = 625, status
COMPLETE, `is_valid` true, no error or warning. The cohort definition was not changed.

Independent SQL on the CDM gives 830 / 830; the generation cache holds 830 rows; the stored
cohort holds 625. All 205 missing rows have `subject_id % 4 = 0`. Evidence:
`evidence/gsk62-sql-before.txt`, `evidence/impact-before.txt`, ATLAS screenshot and recording
attached to the PR, `evidence/bisect.txt`.

### Impact

| area | impact | reason |
|---|---|---|
| patient safety | no (draft) | research analytics; no direct clinical or safety decision; counts were caught before the study report was refreshed |
| product quality | no | not used for manufacturing or batch release |
| data integrity | yes | every cohort generated on 2.15.1-si.3 stored an incomplete result shown as valid; also feeds analyses that read the cohort table |
| validation status | yes | the validated generation function did not meet its requirement on 2.15.1-si.3; 2.15.1-si.3 was released with no test of the copy step |

Classification: __________________ (critical / major / minor, set by QA)

### Root cause

`25937aee` ("perf(cohortdefinition): copy cached cohort rows into the cohort table in slices")
split the cache-to-cohort copy into 4 statements with `subject_id % 4 = slice` but looped
`slice = 1..4`. Remainder 0 is never copied; slice 4 matches nothing; negative ids (negative
remainder) are never copied. The listener then counts the stored rows, so the short count is
shown as COMPLETE / valid (draft analysis; confirmed by the bisect and regression tests).

Contributing (hypotheses for QA): no test exercised `GenerateCohortTasklet` (the suite stayed
green); the listener does not reconcile the stored cohort with the computed result (gap G-2);
the SI release was accepted without a re-run of validated cohort baselines.

### Impact on stored data (aggregate only)

Query `evidence/impact-query.sql` lists every generation recorded on the deployment with start
time, status, is_valid and counts, plus stored vs cached rows per design. On the test stack:
1 generation on the bad build (cohort #1, EUNOMIA, 625 stored vs 830 cached). What stored data
cannot tell:

- `cohort_generation_info` keeps one row per cohort and source, overwritten by each run, so earlier runs on the bad build and their counts are lost once a cohort is re-generated;
- no build / version is stored with a generation, so "ran on the bad build" must be inferred from start time vs the 2.15.1-si.3 deploy time (and `/WebAPI/info` at the time);
- exports, downloaded counts and downstream analyses (characterization, pathways, IR) computed from a short cohort are not tracked;
- where the cache entry was evicted, the full result is no longer stored and must be recomputed.

### CAPA

| type | action | owner | due | status |
|---|---|---|---|---|
| correction | Fix the sliced copy (this PR, commit 6bc56e8b); re-generate every cohort generated since the 2.15.1-si.3 deploy and compare with its baseline; mark affected study outputs not for use until re-generated | | | open |
| corrective | Regression tests `GenerateCohortTaskletTest` (5) in the normal suite; bisect evidence attached | | | open (this PR) |
| preventive | Reconcile stored cohort with the computed result before marking a generation valid (G-2); end-to-end generation job test (G-3); per-run generation history with build id (G-4); re-run validated cohort baselines as part of accepting SI releases | | | proposed |

### Closure

| field | value |
|---|---|
| effectiveness check | `<pending>` |
| closed by (QA) | __________________ |
| date | __________________ |

## Deviation DEV-GSK-62-2

| field | value |
|---|---|
| deviation id | DEV-GSK-62-2 |
| change id | CR-GSK-62 |
| raised in | procedure (gxp-sdlc rule step 3, OQ "expected results before execution") |
| raised by | Devin (https://app.devin.ai/sessions/7fc803986df240c895a535aecec1fcdc) |
| date raised | 2026-10-07 |

### Description

The regression test `GenerateCohortTaskletTest` was prepared (adapted from an earlier equivalent ticket's test) and run
red on `2df02d24`, and used as the bisect check, before the URS delta, the as-built URS and the
OQ protocol's expected results were written in this pack. The expected values (830 rows, all 12
ids, etc.) were fixed in the test assertions and taken from the ticket before the run, and no
expected result was changed after the run.

### Impact

| area | impact | reason |
|---|---|---|
| patient safety | no | procedure order only |
| product quality | no | procedure order only |
| data integrity | no | expected results were fixed in code before the run and not edited |
| validation status | yes (minor, draft) | the record does not show requirements written before the test |

Classification: __________________ (critical / major / minor, set by QA)

### Root cause

Draft: the reproduction and bisect were started first to answer the reporter's question (which
number is right) quickly; the pack was drafted after.

### CAPA

| type | action | owner | due | status |
|---|---|---|---|---|
| correction | URS delta and OQ expected results written before the fix and before the green run; baseline kept as-is | | | done |
| corrective | Draft the URS delta before preparing or running regression tests on later tickets | | | open |
| preventive | none proposed | | | - |

### Closure

| field | value |
|---|---|
| effectiveness check | |
| closed by (QA) | __________________ |
| date | __________________ |
