# Functional and design spec delta: GSK-62

Draft. Not a signed GxP record.

| field | value |
|---|---|
| change id | CR-GSK-62 |
| URS delta | urs-delta.md |
| pull request | `<pending>` |

## Functional spec delta

| FS id | type | behaviour | satisfies |
|---|---|---|---|
| FS-1 | changed (restored) | Generating a cohort stores every row of the computed result for that definition and source, whatever the person id (positive, negative, very large, any remainder). | URS-CG-007 |
| FS-2 | unchanged (guarded) | Re-generation first deletes the cohort's earlier rows and statistics, then stores each row once; other cohorts are untouched; inclusion / summary / censor statistics are copied. | URS-CG-008, URS-CG-010 |

## Design spec delta

| DS id | component | design | satisfies |
|---|---|---|---|
| DS-1 | `GenerateCohortTasklet.prepareQueriesDefault` | The copy from `cohort_cache` to `cohort` stays split into `COPY_SLICES` (4) statements, but the slice index runs `0 .. COPY_SLICES-1`, the set of values `x % 4` can take. Before: `1 .. COPY_SLICES`, so remainder 0 was never copied and slice 4 matched nothing. | FS-1 |
| DS-2 | `copyGenerationSliceIntoCohortTableSql.sql` | Predicate `ABS(cc.subject_id % @slice_count) = @slice`. SQL `%` keeps the sign of the dividend (PostgreSQL, SQL Server, Oracle `MOD`), so negative ids give a negative remainder; `ABS` folds them into 0..3. | FS-1 |
| DS-3 | `copyGenerationIntoCohortTableSql.sql` | Unchanged: deletes the cohort's rows and statistics, copies statistics. Runs before the slices, in the same statement list. | FS-2 |

## Interfaces and data

- API contract changes: none.
- Database schema or data changes: none. Results stored by the bad build stay short until the cohort is re-generated on the fixed build (see deviation DEV-GSK-62-1).
- Configuration changes: none.

## Unaffected design areas

- Cohort SQL building (`CohortGenerationUtils`, Circe): not changed; the cache held the full 830 rows on the bad build.
- Generation cache (`GenerationCacheHelper`): not changed; a cached design is reused, and the copy now reads all of it.
- Job listener (`GenerationJobExecutionListener`): not changed; counts are read from the stored cohort, now complete.
- Other analyses (characterization, pathways, IR) read `cohort`; they inherit the fix, no code change.

## Data integrity review (ALCOA+)

Records touched: `results.cohort` (delete + insert per generation), `webapi.cohort_generation_info`
(person / record count, status, is_valid; unchanged code), `results.cohort_cache` (read).

| # | check | status | evidence (file:line, test) | gap / action |
|---|---|---|---|---|
| DI-1 | attributable | partial | `CohortGenerationService` L105 sets `created_by`; local stack runs with security off, so every run is `anonymous` | not changed by this PR; gap G-5 |
| DI-2 | legible | yes | standard OMOP cohort table columns | - |
| DI-3 | contemporaneous | yes | start time and duration set server-side (`GenerationJobExecutionListener` L123-L146) | - |
| DI-4 | original | partial | generation info and cohort rows are overwritten by each re-run; earlier counts are not kept | gap G-4 |
| DI-5 | accurate | yes after fix | `GenerateCohortTaskletTest` (5 tests); independent SQL count gap 0 (`evidence/`) | this PR |
| DI-6 | complete | yes after fix | before: 205 of 830 rows silently missing; `GenerateCohortTaskletTest#copiesEveryCachedRowIntoCohortTable` | this PR; reconciliation check is gap G-2 |
| DI-7 | consistent | yes | slices run in order in one tasklet after the delete | - |
| DI-8 | enduring | yes | `cohort` is a results table, not temp | - |
| DI-9 | available | yes | `/cohortdefinition/{id}/info`, results schema | - |
| AT-1 | audit trail who / what / when / why | partial | Spring Batch job tables hold job, parameters, author and times; no old / new value of counts | gap G-4, G-5 |
| AT-2 | tamper-evident | no | `AuditTrailServiceImpl` writes to log files only | not in scope (GSK-39 / GSK-40) |
| AT-3 | always on | n/a | not changed | - |
| AT-4 | no PHI | yes | the change logs only a statement count; evidence holds aggregate counts only | - |
| AT-5 | time sync | yes | server clock | - |
| AT-6 | e-signature | n/a | none | - |

Data integrity impact for the risk assessment: yes - the change restores completeness of a stored GxP-adjacent result.
Reviewed by (QA): __________________
