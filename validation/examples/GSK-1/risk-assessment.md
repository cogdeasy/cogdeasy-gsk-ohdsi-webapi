# Risk assessment: GSK-1

Draft. Not a signed GxP record.

## Change summary

| field | value |
|---|---|
| ticket | GSK-1 (https://cog-gtm.atlassian.net/browse/GSK-1) |
| pull request | `<pending>` |
| application | OHDSI WebAPI (GSK demo fork) |
| tier | silver |
| component | `org.ohdsi.webapi.util.GenericExceptionMapper`, `DataIntegrityViolationException` branch |
| summary | The mapper reads `ex.getCause().getCause().getMessage()` and cuts the text after `Detail: `. With no cause or one level of cause it throws a `NullPointerException`, so the client does not get a 409. With a nested cause that has no `Detail:` marker, `indexOf` returns -1 and the client gets raw PostgreSQL text including the constraint name. The fix makes the branch null-safe and returns a generic message when there is no `Detail:` line. |

## GAMP 5 software category

Category for this change: 5 - the change is custom code in a forked open-source application.

## GxP impact

| area | impact (yes / no) | reason |
|---|---|---|
| patient safety | no | WebAPI holds research definitions (concept sets, cohorts) for observational analytics; it does not drive treatment of individual patients |
| product quality | no | not used in manufacturing or batch release |
| data integrity | yes | the defect is in the error path for writes rejected by a database integrity constraint. The database still rejects the write, so stored data is not changed, but the user gets a wrong status and possibly raw database text, and may not understand why a save failed |

## Scoring

| factor | score | reason |
|---|---|---|
| severity | 2 | wrong status (exception escapes the mapper instead of 409) and disclosure of database internals; recoverable, stored data intact |
| likelihood | 2 | happens in normal use whenever a save hits a constraint, e.g. saving a duplicate concept set or cohort name, and the exception chain has one of the three failing shapes |
| detectability | 2 | the escaped exception is visible as a failed save; the raw text leak is only caught by targeted tests (`reproduce.sh`) or response review |

Risk score: 2 x 2 x 2 = 8

Resulting risk: medium

## Decision

Approach for this change: CSA with scripted tests for the changed function (`oq-protocol.md`).

## Required documents

| document | required / optional / n/a | included |
|---|---|---|
| risk-assessment.md | required | [x] |
| urs-delta.md | required | [x] |
| fs-ds-delta.md | required | [x] |
| validation-plan-delta.md | n/a | [ ] |
| oq-protocol.md | required | [x] |
| csa-test-record.md | n/a | [ ] |
| iq-checklist.md | n/a | [ ] |
| pq-uat-script.md | required | [x] |
| traceability.csv | required | [x] |
| test-summary-report.md | required | [x] |
| validation-summary-report.md | n/a | [ ] |
| qa-review-checklist.md | required | [x] |
| deviation-capa.md | optional | [ ] none raised yet; required if a step fails |
| change-record.md | required | [x] |
| release-notes.md | required | [x] |
| training-note.md | n/a | [ ] |
| inspection-pack.md | required | [x] |

## Review

| field | value |
|---|---|
| prepared by | Devin |
| reviewed by (CSV lead) | __________________ |
| decision | __________________ |
| date | __________________ |
