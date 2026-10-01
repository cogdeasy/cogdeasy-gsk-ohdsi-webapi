# Inspection pack: <ticket key>

Draft. Not a signed GxP record.

Index of every artefact for one change, for an auditor. Each row says where the artefact lives
and its status. Signed copies live in GSK's quality system; this index points to them.

| field | value |
|---|---|
| change id | CR-<ticket key> |
| ticket | <Jira link> |
| pull request | <PR link> |
| merge commit | <sha> |
| risk / approach | <low CSA / medium CSA / high CSV> |

| # | artefact | location | status | signed copy in GSK's quality system |
|---|---|---|---|---|
| 1 | risk assessment | risk-assessment.md | | |
| 2 | URS delta | urs-delta.md | | |
| 3 | FS / DS delta | fs-ds-delta.md | | |
| 4 | validation plan delta | validation-plan-delta.md | | |
| 5 | IQ checklist | iq-checklist.md | | |
| 6 | OQ protocol | oq-protocol.md | | |
| 7 | CSA test record | csa-test-record.md | | |
| 8 | PQ / UAT script | pq-uat-script.md | | |
| 9 | traceability | traceability.csv | | |
| 10 | test summary report | test-summary-report.md | | |
| 11 | validation summary report | validation-summary-report.md | | |
| 12 | deviations / CAPA | deviation-capa.md | | |
| 13 | QA review checklist | qa-review-checklist.md | | |
| 14 | change record | change-record.md | | |
| 15 | release notes | release-notes.md | | |
| 16 | training note | training-note.md | | |
| 17 | CI runs (`build`, `tests`, `scan`) | <GitHub Actions links> | | n/a |
| 18 | code review and approval | <PR review links> | | n/a |
| 19 | audit trail | events so far: <session created, PR opened, review, CI runs>; QA adds approval and merge after merge (sources: audit-log.md) | | n/a |

Mark rows the risk matrix does not require as "n/a".

| field | value |
|---|---|
| compiled by | <name or Devin session link> |
| checked by (QA) | __________________ |
| date | __________________ |
