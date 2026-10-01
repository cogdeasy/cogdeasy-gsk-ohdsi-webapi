# Validation plan delta: <ticket key>

Draft. Not a signed GxP record.

Used for high-risk changes (full CSV) only. It states what this change adds to, or changes in,
the system validation plan held in GSK's quality system.

| field | value |
|---|---|
| change id | CR-<ticket key> |
| system | OHDSI WebAPI (GSK demo fork) |
| validation plan reference | <plan id and version in GSK's quality system> |
| risk assessment | risk-assessment.md, result: high |
| GAMP 5 category | <n> |

## Scope

- In scope: <functions, endpoints, interfaces, data flows touched by the change>
- Out of scope: <areas reviewed and not affected, one line of reasoning each>

## Deliverables

| deliverable | file | owner |
|---|---|---|
| URS delta | urs-delta.md | R&D scientist |
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
| CI | build, automated OQ | GitHub Actions `build`, `tests`, `scan` |
| test | IQ, OQ | <environment name> |
| production | IQ after release, PQ | <environment name> |

## Acceptance criteria for the validation

- Every URS line traces to at least one passed test.
- No open critical or major deviation; minor deviations have a CAPA reference.
- IQ, OQ and PQ executed and reviewed.

## Approval

| role | name | signature | date |
|---|---|---|---|
| CSV lead | | | |
| system owner | | | |
| QA | | | |
