# Deviations and CAPA: GSK-64

Draft. Not a signed GxP record. Prepared by Devin (https://app.devin.ai/sessions/325fce4553a54754adad463854a60fb5).

## Deviation DEV-GSK-64-1

| field | value |
|---|---|
| deviation id | DEV-GSK-64-1 |
| change id | CR-GSK-64 |
| raised in | production use: re-run of the validated cohort #1 "Demo new users of diclofenac" on EUNOMIA after the WebAPI 2.15.1-si.3 deployment (GSK-64) |
| raised by | https://app.devin.ai/sessions/325fce4553a54754adad463854a60fb5 (from the GSK-64 report) |
| date raised | 2026-10-07 |

### Description

Expected (validated baseline): People = 830, Records = 830. Actual on 2.15.1-si.3: People = 625,
Records = 625, status COMPLETE, no error or warning. The cohort definition was not edited.

Reproduced on the local stack running `release/2.15.1-si.3` (`2df02d2`): ATLAS Generation tab shows
625 / 625 COMPLETE (`evidence/before_03_generation_complete.png`, `evidence/before_atlas_generation.mp4`).
An independent SQL implementation of the cohort expression on `demo_cdm` gives 830 / 830, and
WebAPI's own `cohort_cache` for the design holds 830 / 830; `demo_cdm_results.cohort` holds 625 /
625. The 205 missing subjects are exactly the subjects with `subject_id % 4 = 0`
(`evidence/sql-before.txt`). The old number (830) is right; the new number (625) is wrong.

### Impact

| area | impact | reason |
|---|---|---|
| patient safety | no | research analytics; counts are not used for treatment or batch release |
| product quality | no | no manufacturing or release decision depends on WebAPI |
| data integrity | yes | every cohort generated on 2.15.1-si.3 stores about a quarter fewer subjects than the definition selects, with status COMPLETE and is_valid true; downstream counts tables, characterisations and studies built on those cohorts are wrong |
| validation status | yes | the validated baseline for cohort #1 no longer reproduces on 2.15.1-si.3; release 2.15.1-si.3 shipped a change to the generation path with no test coverage |

Classification: __________________ (critical / major / minor, set by QA)

### Affected records

Impact query: `evidence/impact-query.sql`; output on the bad build: `evidence/impact-before.txt`.
Every `webapi.cohort_generation_info` row written on 2.15.1-si.3 is affected unless the cohort is
empty. A row is affected when `person_count` is lower than the distinct subjects in
`cohort_cache` for the same design hash (on the local stack: cohort #1, EUNOMIA, 625 vs 830, gap 205).
Generations started before the 2.15.1-si.3 deploy time are not affected. The ticket also names an
ibuprofen new-user cohort; it is not defined on the local stack, so run the impact query on the
GSK environment to list it and every other affected generation.

What stored data cannot tell:

- `cohort_generation_info` keeps one row per cohort and source and each run overwrites it, so earlier
  runs' counts, and which runs happened on the bad build, can only be inferred from
  `start_time` against the deploy time (gap G-4).
- Generation records do not store the WebAPI build (`/WebAPI/info` shows only the build running now).
- Results copied out of `results.cohort` before a re-run (exports, characterisation, pathway and
  incidence-rate results built on a short cohort) are not traced back to the generation. They have to
  be found and re-run separately.
- With security disabled, every generation's author is `anonymous`, so stored data cannot say who ran an
  affected generation.

### Root cause

Bisect from `v2.15.1` (good) to `v2.15.1-si.3` (bad), with the GSK-64 regression test as the check,
names 25937aee "perf(cohortdefinition): copy cached cohort rows into the cohort table in slices" as
the first bad commit (`evidence/bisect.txt`, 130 s). The commit replaced the single
`INSERT ... SELECT` from `cohort_cache` with `COPY_SLICES = 4` statements filtered on
`subject_id % 4 = slice`. The loop runs `slice = 1..4`, so remainder 0 is never copied, and
remainder 4 never occurs. Postgres `%` also returns negative remainders for negative ids, which
no slice would match. The copy step had no test, so the suite stayed green (gap G-3).

### CAPA

| type | action | owner | due | status |
|---|---|---|---|---|
| correction | Run slices 0..COPY_SLICES-1 and compare `ABS(subject_id % n)` (commit af3a4db0, this PR); re-generate every cohort listed by the impact query after deployment | | | open |
| corrective | Regression tests `GenerateCohortTaskletTest` (5 tests) for the copy step on embedded PostgreSQL, part of the required `tests` check | | | open |
| preventive | Reconcile stored cohort rows with the cached result before marking a generation valid (G-2); end-to-end generation job test (G-3); per-run generation history with WebAPI build (G-4); require tests for any change to the generation path in SI hand-over reviews | | | open |

### Closure

| field | value |
|---|---|
| effectiveness check | after deployment, re-run the impact query: gap = 0 for every generation; cohort #1 on EUNOMIA = 830 / 830 |
| closed by (QA) | __________________ |
| date | __________________ |
