# Deviations and CAPA: GSK-58

Draft. Not a signed GxP record.

## Deviation DEV-GSK-58-1

| field | value |
|---|---|
| deviation id | DEV-GSK-58-1 |
| change id | CR-GSK-58 |
| raised in | Production use: cohort generation on WebAPI 2.15.1-si.3 (reported in GSK-58); reproduced on the local ATLAS stack |
| raised by | Devin, https://app.devin.ai/sessions/9a7025a62f4f4f8d8c03f9c7fa63953d |
| date raised | 2026-10-06 |

### Description

Cohort definition #1 "Demo new users of diclofenac" on source EUNOMIA:

- Expected: People = 830, Records = 830 (validated study baseline, signed off before the upgrade).
- Actual on 2.15.1-si.3: People = 625, Records = 625. Status COMPLETE, `is_valid = true`, with no error or warning.

Independent SQL against `demo_cdm` implements the cohort expression: first diclofenac exposure
(concept 1124300 and its descendants), age at least 16, and at least 365 days of prior
observation. It returns 830. WebAPI's `cohort_cache` for the same design hash also holds 830
rows, but only 625 reached `demo_cdm_results.cohort`. All 205 missing rows have
`subject_id % 4 = 0`; every other residue was copied in full. The counts here are aggregates
only; no person ids are recorded.

Evidence (in `evidence/` unless noted):

- `gsk58-independent-count.sql`, `gsk58-sql-before.txt`: independent count, plus WebAPI table/cache counts by residue, on the bad build.
- `impact-before.txt`: `webapi.cohort_generation_info` row for the bad generation (start 2026-10-06 17:29:35, 625/625).
- `bisect.txt`, `bisect-check.sh`: `git bisect v2.15.1-si.3 v2.15.1` driven by `GenerateCohortTaskletTest`. The first bad commit is 25937aeec45ae2a4453887fe29a23cdf308dda7f (135 s).
- ATLAS recording and screenshots of the 625 result: attached to the PR.

### Impact

| area | impact | reason |
|---|---|---|
| patient safety | no (direct) | ATLAS / WebAPI is GxP-adjacent research analytics, not a clinical or batch-release system. An indirect risk remains if study conclusions built on undercounted cohorts feed safety decisions, so QA should assess this. |
| product quality | no | No product, batch or release decision is made in this system. |
| data integrity | yes | Generations silently persisted about 25% fewer subjects than the definition selects (the rows with `subject_id % 4 = 0`), marked COMPLETE/valid. Any count, characterization, incidence rate or export built on a cohort generated on 2.15.1-si.3 is incomplete. |
| validation status | yes | The 2.15.1-si.3 release no longer meets the validated baseline for cohort generation. The cohort copy step had no automated test coverage, so the defect passed CI. |

Classification: __________________ (critical / major / minor, set by QA)

### Root cause

Confirmed by bisect and by the regression tests: commit 25937ae ("perf(cohortdefinition): copy
cached cohort rows into the cohort table in slices") replaced the single cache-to-cohort
`INSERT ... SELECT` with `COPY_SLICES = 4` statements filtered on
`cc.subject_id % @slice_count = @slice`. The loop in `GenerateCohortTasklet.prepareQueries` ran
`slice = 1 .. 4`, but `subject_id % 4` only takes the values 0..3. Slice 0 was never copied, and
slice 4 matches no rows. A negative `subject_id` would also give a negative residue that no
slice matches.

Contributing cause: no test exercised the cache-to-cohort copy, so CI stayed green.

Hypothesis, not confirmed: the ibuprofen new-user cohort the reporter mentions is affected
through the same path, because the copy step is shared by every cohort generation. That cohort is
not in the local stack, so this was not tested.

### Impact on stored data (generations run on the bad build)

Local stack: the 2.15.1-si.3 build (commit 2df02d2) was deployed 2026-10-06T17:24:19Z, and
the fix build (f6bcd2c) was deployed 2026-10-06T17:33:44Z. `evidence/job-history.txt` lists the
`generateCohort` executions: 17:25:10 and 17:29:35 ran on the bad build (cohort 1, source 1,
author `anonymous`), and 17:34:09 ran on the fix build (830/830, `evidence/impact-after.txt`).

What the stored data cannot tell you:

- `webapi.cohort_generation_info` keeps only the latest row per cohort and source, so earlier counts are overwritten. Use `batch_job_execution` history and deploy times to find which generations ran on the bad build.
- No build or commit id is stored with a generation; it has to be inferred from timestamps against deploy records (`/WebAPI/info`).
- The local stack runs with security disabled, so every author is `anonymous`. In production, `created_by_id` / `jobAuthor` identify who to notify.
- Results already exported, copied into reports, or used downstream (characterizations, incidence rates, pathways, estimation) are not visible from the cohort tables.
- Production generations: the queries in `evidence/impact-query.sql` and `evidence/job-history.sql` must be run against the production WebAPI database by GSK. They were run only on the local stack.

### CAPA

| type | action | owner | due | status |
|---|---|---|---|---|
| correction | Fix the slice loop to 0..COPY_SLICES-1 and partition on `ABS(subject_id % COPY_SLICES)` (commit f6bcd2ca on `devin/gsk-58-cohort-copy-drops-subjects`), then regenerate every cohort generated on 2.15.1-si.3 after the fix is deployed | | | fix drafted, PR pending approval |
| corrective | Regression tests `GenerateCohortTaskletTest` (embedded Postgres) cover the copy step: every row copied, residue-0 / negative / large ids, no duplicates on regeneration, other cohorts untouched | | | drafted in PR |
| preventive | Require automated test coverage for changes to the cohort generation SQL path, and an independent-count check against the validated baseline cohorts as part of release qualification | | | proposed |

### Closure

| field | value |
|---|---|
| effectiveness check | |
| closed by (QA) | __________________ |
| date | __________________ |

## Deviation DEV-GSK-58-2

| field | value |
|---|---|
| deviation id | DEV-GSK-58-2 |
| change id | CR-GSK-58 |
| raised in | Investigation step: git bisect v2.15.1..v2.15.1-si.3 |
| raised by | Devin, https://app.devin.ai/sessions/9a7025a62f4f4f8d8c03f9c7fa63953d |
| date raised | 2026-10-06 |

### Description

Expected: each bisect step builds the revision and runs `GenerateCohortTaskletTest`, and only
assertion failures count as bad. Actual: the first bisect run had build and test-context errors
on some revisions (missing generated build metadata from an incremental build in a worktree).
Those errors were counted as results, so that run is not valid evidence, although it named the
same commit. The first run's log is kept in the session evidence directory (`invalid-bisect-worktree`).

### Impact

| area | impact | reason |
|---|---|---|
| patient safety | no | Investigation tooling only |
| product quality | no | Investigation tooling only |
| data integrity | no | No stored data touched |
| validation status | no | Replaced by a valid re-run before any conclusion was drawn |

Classification: __________________ (critical / major / minor, set by QA)

### Root cause

The bisect check script ran an incremental build and did not tell build errors apart from test failures.

### CAPA

| type | action | owner | due | status |
|---|---|---|---|---|
| correction | Re-ran bisect with `evidence/bisect-check.sh`: a clean build, Maven offline, build/context errors exit 125 (skip), and only assertion failures count as bad. Result in `evidence/bisect.txt` | | | done |
| corrective | Keep the corrected check script with the evidence | | | done |
| preventive | | | | |

### Closure

| field | value |
|---|---|
| effectiveness check | |
| closed by (QA) | __________________ |
| date | __________________ |
