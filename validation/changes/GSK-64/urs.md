# As-built URS: cohort definition and generation (WebAPI)

Draft, prepared by Devin. Not approved. To be reconciled with the approved URS in GSK's quality system.

| field | value |
|---|---|
| system | OHDSI WebAPI (GSK demo fork), tier silver |
| module | cohort definition and cohort generation (`CohortDefinitionService`, `CohortGenerationService`, `GenerateCohortTasklet`, `GenerationJobExecutionListener`, `GenerationCacheHelper`, cohort copy SQL) |
| ticket | [GSK-64](https://cog-gtm.atlassian.net/browse/GSK-64) |
| code read at | `2df02d245ba6e415866181757145d0343113179c` (`release/2.15.1-si.3`) |
| prepared by | Devin (https://app.devin.ai/sessions/325fce4553a54754adad463854a60fb5) |

## Purpose

GSK-64 changes cohort generation. `release/2.15.1-si.3` has no URS traced to code for this module
(`validation/` holds templates only; `validation/changes/` is empty on this branch), so this is an
as-built URS reconstructed from the code and Jira, for QA to reconcile with the approved go-live
URS held in GSK's quality system. It is not a claim that the system has no approved URS.

## Scope

In scope: create, change, version, copy and delete cohort definitions; generate a cohort on a data
source; the stored result, re-generation and cache reuse; generation statistics and the
inclusion-rule report; status, counts, failure and cancellation shown to the user; who / when of a
generation; source permissions on generation results.

Out of scope: cohort expression SQL building (Circe library), ATLAS client code, characterization,
pathways, incidence rates, estimation and prediction, vocabulary and concept sets, security
configuration beyond the source filter cited.

## How it was derived

Read the endpoints, services, batch tasklet and listener, cache helper and SQL templates at
`2df02d24`, the ticket GSK-64 and related Jira tickets (GSK-39, GSK-40). Every code link is pinned to
that commit and its target was opened to check the statement; where the code does not do what the
statement says, the statement is the intended behaviour and the mismatch is listed in
`gap-report.md`. Tests were found by searching `src/test` for tests that exercise the cited code.
Risk follows the `gxp-risk-assessment` impact questions; patient safety and product quality are
"no" for every line (research analytics, no clinical or batch-release use), so the rating rests on
data integrity.

## Requirements

| id | requirement | source | code | acceptance criteria | risk |
|--------------|--------------------------|------------|--------------|----------------------|------------|
| URS-CG-001 | The system shall let an authorised user create a cohort definition with a unique name and store its expression. | derived from code | [CohortDefinitionService.java#L724-L755](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/service/CohortDefinitionService.java#L724-L755) | Create a definition with a new name: it is stored and returned with an id. A second definition with the same name is refused. | medium |
| URS-CG-002 | The system shall let an authorised user change a cohort definition and keep the changed expression as the current one. | derived from code | [CohortDefinitionService.java#L827-L852](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/service/CohortDefinitionService.java#L827-L852) | Save a changed expression: reading the definition back returns the changed expression. | medium |
| URS-CG-003 | The system shall keep each saved change of a cohort definition as a version that can be listed and read back. | GSK-64 (the reporter used the Versions tab to show the definition was unchanged); derived from code | [CohortDefinitionService.java#L1336-L1345](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/service/CohortDefinitionService.java#L1336-L1345); [CohortDefinitionService.java#L1355-L1364](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/service/CohortDefinitionService.java#L1355-L1364) | Save a definition twice: two versions are listed and each returns the expression saved at that time. | medium |
| URS-CG-004 | The system shall let a user copy a cohort definition (or one of its versions) to a new definition with a unique name. | derived from code | [CohortDefinitionService.java#L961-L974](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/service/CohortDefinitionService.java#L961-L974) | Copy a definition: the copy has the same expression and a unique 'COPY OF' name. | low |
| URS-CG-005 | The system shall let an authorised user delete a cohort definition together with its generation results. | derived from code | [CohortDefinitionService.java#L990-L1047](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/service/CohortDefinitionService.java#L990-L1047) | Delete a generated definition: it can no longer be read and its generation info is removed. | medium |
| URS-CG-006 | The system shall let an authorised user generate a cohort definition on a named data source as a background job and return the job reference. | GSK-64 steps 1-2; derived from code | [CohortDefinitionService.java#L861-L871](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/service/CohortDefinitionService.java#L861-L871); [CohortGenerationService.java#L99-L122](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/service/CohortGenerationService.java#L99-L122) | Request generation of definition #1 on EUNOMIA: a job is started and its reference returned; the Generation tab shows the run. | high |
| URS-CG-007 | The system shall store, for each generation, every person and record that the cohort definition selects on the data source, so that the stored cohort equals the definition's result. | GSK-64 expected result (People = 830, Records = 830 on EUNOMIA); derived from code | [GenerateCohortTasklet.java#L183-L220](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/cohortdefinition/GenerateCohortTasklet.java#L183-L220); [copyGenerationSliceIntoCohortTableSql.sql#L1-L5](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/resources/resources/cohortdefinition/sql/copyGenerationSliceIntoCohortTableSql.sql#L1-L5) | Generate definition #1 on EUNOMIA: People = Records = 830, equal to an independent SQL count on the CDM. No person is left out because of the value of their person id. | high |
| URS-CG-008 | The system shall replace a cohort's earlier result when it is generated again, storing each record once and leaving other cohorts' results unchanged. | GSK-64 (re-run of a previously validated cohort); derived from code | [copyGenerationIntoCohortTableSql.sql#L1-L5](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/resources/resources/cohortdefinition/sql/copyGenerationIntoCohortTableSql.sql#L1-L5); [GenerateCohortTasklet.java#L183-L220](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/cohortdefinition/GenerateCohortTasklet.java#L183-L220) | Generate the same definition twice: the second result has the same rows, none duplicated; rows of other cohorts are unchanged. | high |
| URS-CG-009 | The system shall give the same generation result whether it reuses a cached result for an unchanged definition or computes it again, and shall recompute when the cached result is missing or invalid. | derived from code | [GenerationCacheHelper.java#L42-L78](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/generationcache/GenerationCacheHelper.java#L42-L78) | Generate twice without changes: the second run reuses the cache with the same counts. Invalidate the cache: the next run recomputes. | high |
| URS-CG-010 | The system shall store the inclusion-rule, summary and censoring statistics of each generation and report them for the cohort and source. | derived from code | [copyGenerationIntoCohortTableSql.sql#L7-L25](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/resources/resources/cohortdefinition/sql/copyGenerationIntoCohortTableSql.sql#L7-L25); [CohortDefinitionService.java#L1109-L1142](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/service/CohortDefinitionService.java#L1109-L1142) | Generate a definition with inclusion rules: the report shows base and final counts that match the generated cohort. | medium |
| URS-CG-011 | The system shall show, per data source, the status of the latest generation and the number of people and records in its result, and shall only mark a generation valid when its stored result is complete. | GSK-64 step 3 (People / Records columns); derived from code | [CohortDefinitionService.java#L931-L949](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/service/CohortDefinitionService.java#L931-L949); [GenerationJobExecutionListener.java#L99-L109](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/cohortdefinition/GenerationJobExecutionListener.java#L99-L109) | After a generation completes, the Generation tab shows COMPLETE and People / Records equal to the stored cohort; an incomplete result is not shown as valid. | high |
| URS-CG-012 | The system shall record a failed or cancelled generation as not valid, clear its counts and keep a safe failure message. | derived from code | [GenerationJobExecutionListener.java#L93-L98](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/cohortdefinition/GenerationJobExecutionListener.java#L93-L98); [GenerationJobExecutionListener.java#L111-L120](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/cohortdefinition/GenerationJobExecutionListener.java#L111-L120) | Make a generation fail: it is marked not valid, counts are empty and a failure message is kept; the user is not shown counts. | high |
| URS-CG-013 | The system shall let the user who can generate a cohort cancel a running generation on a source. | derived from code | [CohortDefinitionService.java#L889-L916](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/service/CohortDefinitionService.java#L889-L916) | Cancel a running generation: the job stops and the generation is marked cancelled. | medium |
| URS-CG-014 | The system shall record who started each generation, when it started and how long it took. | derived from code; GSK-39 (audit_event table, Done in Jira) | [CohortGenerationService.java#L99-L122](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/service/CohortGenerationService.java#L99-L122); [GenerationJobExecutionListener.java#L123-L129](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/cohortdefinition/GenerationJobExecutionListener.java#L123-L129); [GenerationJobExecutionListener.java#L132-L156](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/cohortdefinition/GenerationJobExecutionListener.java#L132-L156) | Generate as a signed-in user: generation info records that user, the server start time and the duration. | high |
| URS-CG-015 | The system shall only show generation results for data sources the user is permitted to access. | derived from code | [CohortDefinitionService.java#L931-L949](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/service/CohortDefinitionService.java#L931-L949) | A user without access to a source does not see that source's generation row. | medium |
| URS-CG-016 | The system shall move a generation through PENDING, RUNNING and COMPLETE and show the current status to the user. | derived from code | [CohortGenerationService.java#L110-L111](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/service/CohortGenerationService.java#L110-L111); [GenerationJobExecutionListener.java#L143-L146](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/cohortdefinition/GenerationJobExecutionListener.java#L143-L146); [GenerationJobExecutionListener.java#L88-L91](https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/blob/2df02d245ba6e415866181757145d0343113179c/src/main/java/org/ohdsi/webapi/cohortdefinition/GenerationJobExecutionListener.java#L88-L91) | Start a generation: the status is PENDING, then RUNNING, then COMPLETE. | medium |

## Risk rationale

Impact questions: patient safety no, product quality no for every line; the rating rests on data integrity.

| id | risk | reason |
|------------|--------|----------------------------------------------|
| URS-CG-001 | medium | data integrity yes: creates the record studies rely on |
| URS-CG-002 | medium | data integrity yes: changes the definition that produces study counts |
| URS-CG-003 | medium | data integrity yes: evidence that a validated definition was not edited |
| URS-CG-004 | low | a wrong copy is visible to the user before use |
| URS-CG-005 | medium | data integrity yes: deletes a record and its results |
| URS-CG-006 | high | data integrity yes: entry point for every study count |
| URS-CG-007 | high | data integrity yes: a silent loss gives wrong study counts with status COMPLETE |
| URS-CG-008 | high | data integrity yes: duplicates or cross-cohort deletes change counts silently |
| URS-CG-009 | high | data integrity yes: cache reuse decides what most re-runs return |
| URS-CG-010 | medium | data integrity yes: attrition tables are quoted in study reports |
| URS-CG-011 | high | data integrity yes: this is the number scientists sign off |
| URS-CG-012 | high | data integrity yes: a failure shown as success leads to wrong numbers |
| URS-CG-013 | medium | data integrity yes: cancelled runs must not be reported as results |
| URS-CG-014 | high | data integrity yes: ALCOA attributable / contemporaneous |
| URS-CG-015 | medium | confidentiality and data integrity of results per source |
| URS-CG-016 | medium | a stale status leads users to read old counts |

## Summary

16 requirements: 8 traced to at least one test, 8 `NO TEST`. Gaps: `gap-report.md`.
Traceability: `traceability.md` / `traceability.csv`. This ticket's delta: `urs-delta.md`
(URS-CG-007 and URS-CG-008 restored).

| QA approval | |
|---|---|
| name | __________________ |
| role | __________________ |
| signature | __________________ |
| date | __________________ |
