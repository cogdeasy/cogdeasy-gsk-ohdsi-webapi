# Release notes: GSK-62

Draft. Not a signed GxP record.

| field | value |
|---|---|
| change id | CR-GSK-62 |
| release | `<pending>` |
| pull request | https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/pull/9 |
| risk | high |
| planned date | __________________ |

## What changed

Cohort generation stores the full cohort again. On 2.15.1-si.3 about a quarter of people in
every generated cohort were silently left out (diclofenac new users on EUNOMIA: 625 instead
of 830). No error was shown.

## Who is affected

Everyone who generated a cohort in ATLAS since the 2.15.1-si.3 deployment, and any
characterization, pathway or incidence-rate analysis that used those cohorts.

## Known issues

Cohorts generated on 2.15.1-si.3 keep their short result until they are generated again
(DEV-GSK-62-1).

## Deployment

- Steps: deploy the WAR built from the fix commit.
- Schema or data change: none.
- After deploy: re-generate cohorts generated since the 2.15.1-si.3 deploy.
- Rollback: revert the PR and redeploy the previous WAR.

## Approval

| field | value |
|---|---|
| release manager | __________________ |
| decision (release / hold) | __________________ |
| date | __________________ |
