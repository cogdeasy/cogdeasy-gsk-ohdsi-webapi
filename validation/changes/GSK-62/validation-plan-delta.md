# Validation plan delta: GSK-62

Draft. Not a signed GxP record.

| field | value |
|---|---|
| change id | CR-GSK-62 |
| system | OHDSI WebAPI (GSK demo fork) |
| validation plan reference | __________________ (plan id and version in GSK's quality system) |
| risk assessment | risk-assessment.md, result: high |
| GAMP 5 category | 5 |

## Scope

- In scope: cohort generation copy step (`GenerateCohortTasklet`, `copyGenerationSliceIntoCohortTableSql.sql`); the stored cohort and the People / Records shown in ATLAS for every cohort generated on 2.15.1-si.3.
- Out of scope: cohort SQL building, cache, listener, endpoints (code unchanged, covered as regression by the full suite); ATLAS client (unchanged).
- Also in scope as a draft: as-built URS for cohort definition and generation (`urs.md`) for reconciliation with the approved URS.

## Deliverables

| deliverable | file | owner |
|---|---|---|
| URS delta (+ as-built URS) | urs-delta.md, urs.md | R&D scientist |
| FS / DS delta | fs-ds-delta.md | developer |
| IQ checklist | iq-checklist.md | release manager |
| OQ protocol | oq-protocol.md | test engineer |
| PQ / UAT script | pq-uat-script.md | R&D scientist |
| traceability | traceability.csv | test engineer |
| test summary report | test-summary-report.md | test engineer |
| validation summary report | validation-summary-report.md | CSV lead |

## Environments

| environment | purpose | identifier |
|---|---|---|
| CI | build, automated OQ | GitHub Actions `build`, `tests`, `scan` on the PR |
| test | OQ end to end, dry-run IQ | Devin VM local stack (demo-ops `local-stack`, ATLAS 2.15 + Eunomia on PostgreSQL 5433) |
| production | IQ after release, PQ | __________________ |

## Acceptance criteria for the validation

- Every URS line in the delta traces to at least one passed test.
- No open critical or major deviation; minor deviations have a CAPA reference.
- IQ, OQ and PQ executed and reviewed.
- Every cohort generated on the 2.15.1-si.3 build is re-generated on the fixed build or marked not for use (DEV-GSK-62-1 correction).

## Approval

| role | name | signature | date |
|---|---|---|---|
| CSV lead | | | |
| system owner | | | |
| QA | | | |
