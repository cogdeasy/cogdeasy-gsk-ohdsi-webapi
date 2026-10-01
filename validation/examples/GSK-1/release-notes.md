# Release notes: GSK-1

Draft. Not a signed GxP record.

| field | value |
|---|---|
| change id | CR-GSK-1 |
| release | `<pending>` |
| pull request | `<pending>` |
| risk | medium |
| planned date | __________________ |

## What changed

Saving something that clashes with existing data, for example a concept set or cohort with a
name that is already used, now always returns a 409 Conflict with a safe message. Before, some of
these saves failed inside the error handler instead, and some returned raw database text such
as constraint names.

## Who is affected

ATLAS users who save concept sets, cohorts or other definitions through WebAPI. Only the error
response changes; successful saves are not affected.

## Known issues

None.

## Deployment

- Steps: deploy the new `WebAPI.war` from the `build` check.
- Schema or data change: none.
- Rollback: revert the fix PR and redeploy the previous WAR.

## Approval

| field | value |
|---|---|
| release manager | __________________ |
| decision | __________________ |
| date | __________________ |
