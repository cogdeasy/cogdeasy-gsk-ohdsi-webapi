# PQ / UAT script: GSK-1

Draft. Not a signed GxP record.

| field | value |
|---|---|
| change id | CR-GSK-1 |
| environment | `<pending>` (test environment with ATLAS and the fixed WebAPI) |
| release | `<pending>` |
| user role | R&D scientist (ATLAS user) |
| test data | an existing concept set and cohort definition in the test environment, names `<pending>` |

| step | URS | user action in ATLAS | expected | actual | pass / fail | evidence ref |
|---|---|---|---|---|---|---|
| 1 | URS-3, URS-4 | Create a new concept set with the same name as an existing one and save | Save is refused with a clear message; no database text such as "duplicate key" or a constraint name is shown | | | |
| 2 | URS-3, URS-4 | Do the same for a cohort definition | As step 1 | | | |
| 3 | URS-5 | Open a concept set you do not have rights to (if a restricted account is available) | Access is refused as before | | | |
| 4 | - | Rename the new concept set to a unique name and save | Save succeeds; the concept set opens with the new name | | | |

## Fit for intended use

<!-- Left for the R&D scientist. -->

| field | value |
|---|---|
| executed by (R&D scientist) | __________________ |
| date | __________________ |
| reviewed by | __________________ |
| review date | __________________ |
