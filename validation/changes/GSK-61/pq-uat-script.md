# PQ / UAT script: GSK-61

Draft. Not a signed GxP record.

| field | value |
|---|---|
| change id | CR-GSK-61 |
| environment | __________________ |
| release | `<pending>` |
| user role | R&D scientist (ATLAS user) |
| test data | EUNOMIA source; cohort #1 "Demo new users of diclofenac" (validated baseline 830); the team's other cohorts generated since the 2.15.1-si.3 upgrade (e.g. ibuprofen new users) |

| step | URS | user action in ATLAS | expected | actual | pass / fail | evidence ref |
|---|---|---|---|---|---|---|
| 1 | URS-CG-003 | Open cohort #1, Versions tab | no edits since the validated version | | | |
| 2 | URS-CG-007 | Generation tab, Generate on "OHDSI Eunomia Demo Database", wait for COMPLETE | People = 830, Records = 830, no error | | | |
| 3 | URS-CG-008 | Generate again | People = 830, Records = 830 | | | |
| 4 | URS-CG-007 | Re-generate each other cohort generated since the upgrade and compare with its signed-off baseline | equal to the baseline (or explained by a definition change in Versions) | | | |
| 5 | URS-CG-010 | Open the inclusion report for cohort #1 | final count matches People | | | |

## Fit for intended use

| field | value |
|---|---|
| executed by (R&D scientist) | __________________ |
| date | __________________ |
| reviewed by | __________________ |
| review date | __________________ |
