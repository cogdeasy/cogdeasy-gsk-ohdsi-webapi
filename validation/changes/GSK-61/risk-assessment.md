# Risk assessment: GSK-61

Draft. Not a signed GxP record.

## Change summary

| field | value |
|---|---|
| ticket | [GSK-61](https://cog-gtm.atlassian.net/browse/GSK-61) |
| pull request | https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/pull/8 |
| application | OHDSI WebAPI (GSK demo fork) |
| tier | silver |
| component | `GenerateCohortTasklet` sliced copy of cached cohort rows (`copyGenerationSliceIntoCohortTableSql.sql`) |
| summary | Since 2.15.1-si.3 cohort generation silently stores only the rows whose `subject_id % 4` is 1, 2 or 3 (cohort #1 on EUNOMIA: 625 of 830). The fix makes the sliced copy cover every row. |

## GAMP 5 software category

Category for this change: 5 - custom code in the cohort generation path (SI change to the open-source WebAPI).

## GxP impact

| area | impact (yes / no) | reason |
|---|---|---|
| patient safety | no | ATLAS on this platform is research analytics; cohort counts feed study reports, not clinical, safety or pharmacovigilance decisions directly |
| product quality | no | does not feed manufacturing, batch release or quality decisions |
| data integrity | yes | creates the stored cohort result and the People / Records shown and signed off by scientists; the defect presented an incomplete result as valid (ALCOA complete / accurate) |
| regulatory (Part 11 / Annex 11) | no | no electronic signature or audit-trail function changed |

## Scoring

| factor | score | reason |
|---|---|---|
| severity | 2 | wrong study counts; recoverable by re-generating on a fixed build; source CDM data and the cache are intact |
| likelihood | 3 | every generation of every cohort with a person id divisible by 4, i.e. practically every generation |
| detectability | 3 | went unnoticed: status COMPLETE, no warning, CI green; found only by a scientist comparing with a signed-off baseline |

Risk score: 2 x 3 x 3 = 18

Resulting risk: high

Rated on the failure this change addresses as it occurred in production. With the new
regression tests a recurrence would be caught by targeted tests (detectability 2, score 12,
medium), but the rating is not lowered on the strength of the change's own tests. The CSV lead
may change the rating.

## Decision

Approach for this change: full CSV (GAMP 5): validation plan delta, scripted IQ / OQ / PQ.

## Required documents

| document | required / optional / n/a | included |
|---|---|---|
| risk-assessment.md | required | [x] |
| urs-delta.md | required | [x] |
| fs-ds-delta.md | required | [x] |
| validation-plan-delta.md | required | [x] |
| oq-protocol.md | required | [x] |
| csa-test-record.md | n/a (low risk only) | [ ] |
| iq-checklist.md | required | [x] |
| pq-uat-script.md | required | [x] |
| traceability.csv | required | [x] |
| test-summary-report.md | required | [x] |
| validation-summary-report.md | required | [x] |
| qa-review-checklist.md | required | [x] |
| deviation-capa.md | required (silent wrong result; procedure departure) | [x] |
| change-record.md | required | [x] |
| release-notes.md | required | [x] |
| training-note.md | required | [x] |
| inspection-pack.md | required | [x] |

Also in this pack (as-built URS, `urs-traceability`): `urs.md`, `urs.docx`, `traceability.md`,
`gap-report.md`, and `evidence/`.

## Review

| field | value |
|---|---|
| prepared by | Devin (https://app.devin.ai/sessions/0e6a1ba1a6104b5fb5e0efb8886f9605) |
| reviewed by (CSV lead) | __________________ |
| decision (agree / change rating) | __________________ |
| date | __________________ |
