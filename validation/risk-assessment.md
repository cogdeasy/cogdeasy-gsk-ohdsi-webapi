# Risk assessment: <ticket key>

Draft. Not a signed GxP record.

Fill this first. The result picks the validation approach and the documents this change needs.

## Change summary

| field | value |
|---|---|
| ticket | <ticket key and link> |
| pull request | <PR link> |
| application | OHDSI WebAPI (GSK demo fork) |
| tier | silver |
| component | <class, module or endpoint> |
| summary | <one or two sentences: what changes and why> |

## GAMP 5 software category

| category | applies to |
|---|---|
| 1 | infrastructure software (OS, database engine, JVM) |
| 3 | non-configured product, used as supplied |
| 4 | configured product (configuration only, no code change) |
| 5 | custom application or custom code |

Category for this change: <1 / 3 / 4 / 5> - <reason>

## GxP impact

| area | impact (yes / no) | reason |
|---|---|---|
| patient safety | | |
| product quality | | |
| data integrity | | |

## Scoring

Score each factor 1-3. Risk score = severity x likelihood x detectability (1-27).

| factor | 1 | 2 | 3 | score | reason |
|---|---|---|---|---|---|
| severity | no GxP impact if it fails | wrong or unsafe result, recoverable, data intact | patient safety, product quality or lasting data loss | | |
| likelihood | rare path, unusual input | happens in normal use under some conditions | happens on most uses of the function | | |
| detectability | failure is obvious to the user or caught by CI | caught only by targeted tests or log review | likely to go unnoticed | | |

Risk score: <n>

| score | risk |
|---|---|
| 1-4 | low |
| 6-12 | medium |
| 18-27 | high |

Override: a "yes" on patient safety or product quality with severity 3 is high risk whatever the score.

Resulting risk: <low / medium / high>

## Decision

| risk | approach | testing |
|---|---|---|
| low | CSA | unscripted or exploratory testing, recorded in `csa-test-record.md` |
| medium | CSA | scripted tests for the changed functions, in `oq-protocol.md` |
| high | full CSV (GAMP 5) | validation plan delta, scripted IQ / OQ / PQ |

Approach for this change: <CSA unscripted / CSA scripted / full CSV>

## Required documents

Take the column for the resulting risk from the document matrix in `README.md`. Tick each
document this change carries.

| document | required / optional / n/a | included |
|---|---|---|
| risk-assessment.md | required | [ ] |
| urs-delta.md | | [ ] |
| fs-ds-delta.md | | [ ] |
| validation-plan-delta.md | | [ ] |
| oq-protocol.md | | [ ] |
| csa-test-record.md | | [ ] |
| iq-checklist.md | | [ ] |
| pq-uat-script.md | | [ ] |
| traceability.csv | | [ ] |
| test-summary-report.md | | [ ] |
| validation-summary-report.md | | [ ] |
| qa-review-checklist.md | | [ ] |
| deviation-capa.md | | [ ] |
| change-record.md | | [ ] |
| release-notes.md | | [ ] |
| training-note.md | | [ ] |
| inspection-pack.md | | [ ] |

## Review

| field | value |
|---|---|
| prepared by | <name or Devin session link> |
| reviewed by (CSV lead) | __________________ |
| decision (agree / change rating) | __________________ |
| date | __________________ |
