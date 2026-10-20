# Risk assessment: GSK-55

Draft. Not a signed GxP record.

| field | value |
|---|---|
| ticket / PR | GSK-55 (https://cog-gtm.atlassian.net/browse/GSK-55) / https://github.com/cogdeasy/cogdeasy-gsk-ohdsi-webapi/pull/12 |
| tier | silver |
| component | whole application runtime: Spring Boot 1.5.22 -> 2.7.18 (Spring 4.3 -> 5.3, Spring Data 1.11 -> 2.7, Spring Batch 3 -> 4.3, Jersey 2.25 -> 2.35, Tomcat 8.5 -> 9, Hibernate 5.4 -> 5.6), Java 8 -> 17 |
| GAMP 5 category | 5 - custom code in a forked open-source application; the platform change touches every module |

| GxP impact | yes / no | reason |
|---|---|---|
| patient safety | no | research definitions for observational analytics; no treatment decisions |
| product quality | no | not used in manufacturing or batch release |
| data integrity | yes | every read and write of concept sets, cohorts and analyses now goes through Spring Data 2 / Hibernate 5.6; a wrong mapping could present or store wrong research definitions |
| regulatory (Part 11 / Annex 11) | no | no electronic signature; the audit trail aspect (`AuditTrailAspect`) is unchanged code but runs on the new stack |

| factor | score | reason |
|---|---|---|
| severity | 2 | a wrong result is recoverable; schema and Flyway migrations are not changed, so stored data is intact |
| likelihood | 3 | every request uses the upgraded framework |
| detectability | 2 | compile errors and the 248-test suite catch most breakage; behaviour drift in less-tested endpoints needs targeted runtime checks |

Score: 2 x 3 x 2 = 12. Risk: medium (top of the band). Approach: CSA scripted (`oq-protocol.md`).
The CSV lead may raise this to high (full CSV) because the change is platform-wide.

Required documents: risk-assessment, urs-delta, fs-ds-delta, oq-protocol, pq-uat-script,
traceability, test-summary-report, qa-review-checklist, change-record, release-notes,
inspection-pack.

| CSV lead review | |
|---|---|
| reviewed by | __________________ |
| decision (agree / change rating) | __________________ |
| date | __________________ |
