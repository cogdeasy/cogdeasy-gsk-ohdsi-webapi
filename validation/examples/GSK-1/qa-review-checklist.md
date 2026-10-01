# QA review checklist: GSK-1

Draft. Not a signed GxP record.

| field | value |
|---|---|
| change id | CR-GSK-1 |
| pull request | `<pending>` |
| risk | medium |

The comment column is pre-filled by Devin with where to look. The yes / no / n/a column is left
for QA.

| # | check | yes / no / n/a | comment |
|---|---|---|---|
| 1 | Risk assessment is complete and the rating is justified | | risk-assessment.md, score 8, medium |
| 2 | Every document required by the matrix for this risk is present | | inspection-pack.md lists them |
| 3 | Every URS line maps to at least one test in traceability.csv | | URS-1..6 -> OQ steps 3-8 |
| 4 | Expected results were written before execution | | oq-protocol.md, actuals still `<pending>` |
| 5 | Every test has an actual result and evidence ref | | `<pending>` |
| 6 | Failed steps have a deviation record | | none raised yet |
| 7 | Full-suite totals and CI run link are recorded | | `<pending>` |
| 8 | Security scan result is recorded; new findings are triaged | | `<pending>` |
| 9 | Rollback is stated | | release-notes.md: revert the fix PR |
| 10 | Change record matches the PR (commits, description, root cause) | | `<pending>` |
| 11 | No reviewer, decision, signature or date field was filled by Devin | | |
| 12 | Audit trail (audit-log.md events) is available for the change | | `<pending>` |

## Findings

| # | finding | severity | owner | due |
|---|---|---|---|---|
| | | | | |

| field | value |
|---|---|
| QA reviewer | __________________ |
| decision | __________________ |
| date | __________________ |
