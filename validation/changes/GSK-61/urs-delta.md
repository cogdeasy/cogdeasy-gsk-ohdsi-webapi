# URS delta: GSK-61

Draft. Prepared by Devin for review by a named GSK approver.

GSK-61 has no acceptance criteria. They were derived from the ticket's expected result
(People = 830, Records = 830 on EUNOMIA, no change to the definition) and the reproduced
defect:

- AC1: generating cohort #1 "Demo new users of diclofenac" on EUNOMIA stores People = Records = 830, equal to an independent SQL count on the CDM (gap 0).
- AC2: no person is dropped or kept because of the value of their person id (every remainder, negative and very large ids, a single-person cohort).
- AC3: each record is stored once, and re-generating the cohort replaces the earlier result.
- AC4: generation does not change other cohorts' results, and the inclusion / summary statistics are still stored.

| URS id | type | requirement | source | acceptance |
|---|---|---|---|---|
| URS-CG-007 | restored | The system shall store, for each generation, every person and record that the cohort definition selects on the data source, so that the stored cohort equals the definition's result. | GSK-61 AC1, AC2 | `GenerateCohortTaskletTest#copiesEveryCachedRowIntoCohortTable`, `#copiesSubjectsWhateverTheirIdValue`, `#copiesSingleSubjectWithIdDivisibleBySliceCount`; ATLAS re-run shows 830 / 830; independent SQL gap 0 |
| URS-CG-008 | restored | The system shall replace a cohort's earlier result when it is generated again, storing each record once and leaving other cohorts' results unchanged. | GSK-61 AC3, AC4 | `GenerateCohortTaskletTest#copiesEachRowOnceAndRegenerationReplacesRows`, `#leavesOtherCohortsAndCopiesStatistics` |

"Restored": the behaviour held at `v2.15.1` and was lost in `25937aee` (`release/2.15.1-si.3`).
Ids are from the as-built URS in `urs.md`.

## Unaffected requirements

- URS-CG-001..005 (create, change, version, copy, delete): code not touched; the Versions tab confirmed the definition was not edited.
- URS-CG-006 (start generation): endpoint and job set-up unchanged; behaviour is a known gap (G-3), not changed here.
- URS-CG-009 (cache): the cache was complete (830 rows) on the bad build; cache code unchanged.
- URS-CG-010 (statistics): statistics copy unchanged; covered by `#leavesOtherCohortsAndCopiesStatistics` as a regression guard.
- URS-CG-011 (status / counts): listener unchanged; it reports the stored cohort, which is now complete. The missing reconciliation is gap G-2, out of this ticket's minimal fix.
- URS-CG-012..016: code not touched.
