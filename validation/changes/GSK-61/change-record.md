# CR-GSK-61

Draft change record. Not a signed GxP record.

| field | value |
|---|---|
| change id | CR-GSK-61 |
| application | OHDSI WebAPI (GSK demo fork) |
| tier | silver |
| ticket | https://cog-gtm.atlassian.net/browse/GSK-61 |
| pull request | https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/pull/8 |
| commits | 1530543b (validation pack), 52d20a3d (regression tests), 2419b469 (fix), 43ccef90 (results update), `<pending>` (CI results update) |
| author | Devin (https://app.devin.ai/sessions/0e6a1ba1a6104b5fb5e0efb8886f9605) |
| description | Cohort generation on 2.15.1-si.3 silently stored only part of each cohort (cohort #1 on EUNOMIA: 625 of 830). Restores a complete copy from the generation cache to the cohort table, adds regression tests, and drafts the as-built URS for cohort generation. |
| root cause | `25937aee` split the cache-to-cohort copy into 4 slices on `subject_id % 4` but iterated slices 1..4, so remainder 0 (and negative ids) were never copied; the listener then reported the short count as COMPLETE / valid. |
| risk | high - data integrity yes; severity 2 x likelihood 3 x detectability 3 = 18 (risk-assessment.md) |
| URS impact | URS-CG-007, URS-CG-008 restored (urs-delta.md) |
| verification | `GenerateCohortTaskletTest` (5 tests); full suite 253 run, 249 passed, 0 failures, 0 errors, 4 skipped (local); CI build / tests / scan / Trivy passed (https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/pull/8/checks); ATLAS 830 / 830 and SQL gap 0 (oq-protocol.md steps 8-10) |
| security scan | `scan` and Trivy passed on PR #8 |
| rollback | revert the PR and redeploy the previous WAR; no schema or data change. Rolling back re-introduces the defect. |
| reviewer | __________________ (named GSK approver) |
| decision (approve / reject / rework) | __________________ |
| date | __________________ |
