# PQ / UAT script: GSK-55

Draft. Not a signed GxP record.

| field | value |
|---|---|
| change id | CR-GSK-55 |
| environment | test (local ATLAS stack or GSK test environment) |
| release | `<pending>` (merge commit) |
| user role | R&D scientist (ATLAS user) |
| test data | EUNOMIA source; one new concept set and one existing cohort definition |

| step | URS | user action in ATLAS | expected | actual | pass / fail | evidence ref |
|---|---|---|---|---|---|---|
| 1 | URS-55-5 | Open ATLAS, go to Configuration | the EUNOMIA source is listed; no error banner | | | |
| 2 | URS-55-6 | Create a concept set, add a concept, save | saved; created date shows a normal date | | | |
| 3 | URS-55-6 | Save another concept set with the same name | ATLAS says the name is taken; nothing new is saved | | | |
| 4 | URS-55-5 | Open an existing cohort definition and generate it on EUNOMIA | generation completes with a person count | | | |

## Fit for intended use

| field | value |
|---|---|
| executed by (R&D scientist) | __________________ |
| date | __________________ |
| reviewed by | __________________ |
| review date | __________________ |
