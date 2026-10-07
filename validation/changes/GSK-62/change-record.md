# CR-GSK-62

Draft change record. Not a signed GxP record.

| field | value |
|---|---|
| change id | CR-GSK-62 |
| application | OHDSI WebAPI (GSK demo fork) |
| tier | silver |
| ticket | https://cog-gtm.atlassian.net/browse/GSK-62 |
| pull request | https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/pull/9 |
| commits | 4bd2caa6 (validation pack), 9ec90574 (regression tests), 6bc56e8b (fix), 218134a7, cca2a5a1 and the results commit (evidence pack / results) |
| author | Devin (https://app.devin.ai/sessions/7fc803986df240c895a535aecec1fcdc) |
| description | Cohort generation on 2.15.1-si.3 silently stored only part of each cohort (cohort #1 on EUNOMIA: 625 of 830). Restores a complete copy from the generation cache to the cohort table, adds regression tests, and drafts the as-built URS for cohort generation. |
| root cause | `25937aee` split the cache-to-cohort copy into 4 slices on `subject_id % 4` but iterated slices 1..4, so remainder 0 (and negative ids) were never copied; the listener then reported the short count as COMPLETE / valid. |
| risk | high - data integrity yes; severity 2 x likelihood 3 x detectability 3 = 18 (risk-assessment.md) |
| URS impact | URS-CG-007, URS-CG-008 restored (urs-delta.md) |
| verification | `GenerateCohortTaskletTest` (5 tests); full suite 253 run, 249 passed, 0 failures, 0 errors, 4 skipped (local); CI `tests` [run 37568157926](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/actions/runs/37568157926) on cca2a5a1: 253 run (242 unit + 11 integration), 249 passed, 0 failures, 0 errors, 4 skipped; ATLAS 830 / 830 and SQL gap 0 (oq-protocol.md steps 8-10) |
| security scan | `scan` green on cca2a5a1 (Trivy over WebAPI.war, configured exit-code 0 so it does not fail on findings); finding counts are in the run summary and code scanning, not the job log, so new findings are `<pending>` for QA to read there |
| rollback | revert the PR and redeploy the previous WAR; no schema or data change. Rolling back re-introduces the defect. |
| reviewer | __________________ (named GSK approver) |
| decision (approve / reject / rework) | __________________ |
| date | __________________ |
