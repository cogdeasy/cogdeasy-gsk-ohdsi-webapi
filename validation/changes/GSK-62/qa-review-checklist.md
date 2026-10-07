# QA review checklist: GSK-62

Draft. Not a signed GxP record.

| field | value |
|---|---|
| change id | CR-GSK-62 |
| pull request | https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/pull/9 |
| risk | high |

| # | check | yes / no / n/a | comment |
|---|---|---|---|
| 1 | Risk assessment is complete and the rating is justified | | risk-assessment.md (2 x 3 x 3 = 18) |
| 2 | Every document required by the matrix for this risk is present | | inspection-pack.md rows 1-16 |
| 3 | Every URS line maps to at least one test in traceability.csv | | delta lines CG-007/008 traced; as-built has 8 `NO TEST`, listed in gap-report.md |
| 4 | Expected results were written before execution | | no for the baseline run: DEV-GSK-62-2 |
| 5 | Every test has an actual result and evidence ref | | oq-protocol.md |
| 6 | Failed steps have a deviation record | | deviation-capa.md |
| 7 | Full-suite totals and CI run link are recorded | | test-summary-report.md |
| 8 | Security scan result is recorded; new findings are triaged | | test-summary-report.md |
| 9 | Rollback is stated | | change-record.md |
| 10 | Change record matches the PR (commits, description, root cause) | | change-record.md vs PR |
| 11 | No reviewer, decision, signature or date field was filled by Devin | | all such fields blank |
| 12 | Audit trail (audit-log.md events) is available for the change | | inspection-pack.md row 19 |

## Findings

| # | finding | severity | owner | due |
|---|---|---|---|---|
| | | | | |

| field | value |
|---|---|
| QA reviewer | __________________ |
| decision (accept / accept with findings / reject) | __________________ |
| date | __________________ |
