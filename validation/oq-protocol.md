# OQ protocol: <ticket key>

Draft. Not a signed GxP record.

Scripted operational qualification. Used for CSV (high risk) and for CSA medium risk, where it
covers the changed functions only.

| field | value |
|---|---|
| protocol id | OQ-<ticket key> |
| change id | CR-<ticket key> |
| risk | medium / high |
| build under test | <commit sha> |
| environment | <CI run link or environment name> |
| prerequisites | <JDK, profile, data, accounts> |

## Steps

Expected results are written before execution. Actual, pass/fail and evidence are filled when the
step is run.

| step | URS / AC | action | expected | actual | pass / fail | evidence ref |
|---|---|---|---|---|---|---|
| 1 | URS-<n> | | | | | |

## Deviations

<!-- Any failed step or departure from the script: reference deviation-capa.md. "None" if none. -->

## Execution

| field | value |
|---|---|
| executed by | __________________ |
| execution date | __________________ |
| reviewed by | __________________ |
| review date | __________________ |
