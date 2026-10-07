# CR-GSK-64

Draft change record. Not a signed GxP record.

| field | value |
|---|---|
| change id | CR-GSK-64 |
| application | OHDSI WebAPI (GSK demo fork) |
| tier | silver |
| ticket | [GSK-64](https://cog-gtm.atlassian.net/browse/GSK-64) |
| pull request | [#11](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/pull/11) |
| commits | d940f57c (tests, red), af3a4db0 (fix), e6793084 (URS, traceability and evidence) |
| author | Devin (https://app.devin.ai/sessions/325fce4553a54754adad463854a60fb5) |
| description | Restore the complete copy of cached cohort rows into the cohort table on `release/2.15.1-si.3`, so cohort generation stores every person the definition selects. |
| root cause | 25937aee (2.15.1-si.3) split the copy into 4 slices on `subject_id % 4` but ran slices 1..4, so remainder-0 subjects (and negative ids) were never copied; cohort #1 on EUNOMIA dropped from 830 to 625 with status COMPLETE. |
| risk | high - data integrity: silent loss of cohort members in a COMPLETE generation; the fix itself is a two-line change on a path now covered by tests |
| URS impact | URS-CG-007, URS-CG-008 restored (`urs-delta.md`) |
| verification | `GenerateCohortTaskletTest` 5 tests: 4 failures before the fix (`evidence/red-run-before-fix.txt`), 0 after (`evidence/new-tests-after-fix.txt`); full suite on af3a4db0 253 run, 249 passed, 0 failures, 0 errors, 4 skipped (`evidence/full-suite-after-fix.txt`); ATLAS re-run on af3a4db shows 830 / 830 COMPLETE (`evidence/after_03_generation_complete.png`), SQL gap 0 (`evidence/sql-after.txt`); CI on PR #11 at 8815046b: `build`, `tests`, `scan` and `Trivy` passed |
| security scan | `scan` and `Trivy` passed on PR #11 at 8815046b |
| rollback | revert the PR; no schema or data change. After rollback, generations are short again |
| deviation | DEV-GSK-64-1 (`deviation-capa.md`) |
| reviewer | __________________ (named GSK approver) |
| decision (approve / reject / rework) | __________________ |
| date | __________________ |
