# QA review checklist: GSK-55

Draft. Not a signed GxP record. Devin filled the comment column only.

| field | value |
|---|---|
| change id | CR-GSK-55 |
| pull request | `<pending>` |
| risk | medium |

| # | check | yes / no / n/a | comment |
|---|---|---|---|
| 1 | Risk assessment is complete and the rating is justified | | risk-assessment.md, 2 x 3 x 2 = 12, medium; CSV lead may raise it |
| 2 | Every document required by the matrix for this risk is present | | risk, URS delta, FS/DS delta, OQ, PQ script, traceability, change record, test summary, deviations, release notes, inspection pack |
| 3 | Every URS line maps to at least one test in traceability.csv | | URS-55-1 to URS-55-7, one OQ step each |
| 4 | Expected results were written before execution | | OQ expected results committed in the draft before step execution; not edited after |
| 5 | Every test has an actual result and evidence ref | | OQ steps 1-7 filled; evidence under `evidence/` |
| 6 | Failed steps have a deviation record | | step 6 -> DEV-GSK-55-2; URS-55-3 -> DEV-GSK-55-3; dev runs -> DEV-GSK-55-1 |
| 7 | Full-suite totals and CI run link are recorded | | totals recorded (248/244/0/0/4); CI run `<pending>` |
| 8 | Security scan result is recorded; new findings are triaged | | local Trivy in evidence/trivy-summary.md; CI `scan` `<pending>` |
| 9 | Rollback is stated | | change-record.md: revert PR, Java 8 runtime back with it |
| 10 | Change record matches the PR (commits, description, root cause) | | |
| 11 | No reviewer, decision, signature or date field was filled by Devin | | |
| 12 | Audit trail (audit-log.md events) is available for the change | | session link in change record; PR and CI events `<pending>` |

## Findings

| # | finding | severity | owner | due |
|---|---|---|---|---|
| | | | | |

| field | value |
|---|---|
| QA reviewer | __________________ |
| decision (accept / accept with findings / reject) | __________________ |
| date | __________________ |
