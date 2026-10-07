# URS delta: GSK-64

Draft. Prepared by Devin for review by a named GSK approver. Not approved. Ids are from the
as-built URS in `urs.md` (reconstructed from code; to be reconciled with the approved URS in
GSK's quality system).

| URS id | type | requirement | source | acceptance |
|---|---|---|---|---|
| URS-CG-007 | restored | The system shall store, for each generation, every person and record that the cohort definition selects on the data source, so that the stored cohort equals the definition's result. | GSK-64 expected result (830 / 830 on EUNOMIA) | Generate #1 "Demo new users of diclofenac" on EUNOMIA: People = Records = 830, equal to the independent SQL count; tests `GenerateCohortTaskletTest#copiesEveryCachedRowIntoCohortTable`, `#copiesSubjectsWhateverTheirIdValue`, `#copiesSingleSubjectWithIdDivisibleBySliceCount` |
| URS-CG-008 | restored | The system shall replace a cohort's earlier result when it is generated again, storing each record once and leaving other cohorts' results unchanged. | GSK-64 (re-run of a validated cohort) | Generate twice: same rows, none duplicated, other cohorts unchanged; tests `#copiesEachRowOnceAndRegenerationReplacesRows`, `#leavesOtherCohortsAndCopiesStatistics` |

Acceptance criteria derived from the ticket's expected vs actual result (the ticket has none):

| AC | criterion | URS |
|---|---|---|
| AC1 | People / Records written by generation equal the rows the cohort definition selects (no silent loss). | URS-CG-007 |
| AC2 | No subject is dropped or kept because of its subject id value (every remainder, negative and large ids, a single row). | URS-CG-007 |
| AC3 | Each selected row is stored exactly once, also when generation is re-run from the cache. | URS-CG-008 |
| AC4 | Other cohorts' rows and the cached statistics are unaffected by the copy step. | URS-CG-008, URS-CG-010 |

## Unaffected requirements

- URS-CG-001..005 (create, change, version, copy, delete definitions): not touched; the reporter's Versions tab shows no edit, and the fix does not touch definition storage.
- URS-CG-006 (start a generation job): unchanged entry point; the defect is after the job starts.
- URS-CG-009 (cache reuse): the cache already held all 830 rows (evidence/sql-before.txt); cache code not changed.
- URS-CG-010 (statistics): copied by the unchanged single statement; regression-tested by AC4.
- URS-CG-011 (status and counts shown, valid only when complete): unchanged by the fix; the missing completeness check is gap G-2 in `gap-report.md`, not fixed here.
- URS-CG-012..016: not touched.
