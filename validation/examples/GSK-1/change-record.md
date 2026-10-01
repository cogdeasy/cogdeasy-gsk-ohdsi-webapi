# CR-GSK-1

Draft change record. Not a signed GxP record.

| field | value |
|---|---|
| change id | CR-GSK-1 |
| application | OHDSI WebAPI (GSK demo fork) |
| tier | silver |
| ticket | https://cog-gtm.atlassian.net/browse/GSK-1 |
| pull request | `<pending>` |
| commits | `<pending>` |
| author | Devin (session link `<pending>`) |
| description | Make the `DataIntegrityViolationException` branch of `GenericExceptionMapper` null-safe and stop it returning raw database text. Every data integrity violation returns 409; the client gets the PostgreSQL `Detail:` text when there is one, otherwise a generic message. The full exception stays in the server log. |
| root cause | The branch reads `ex.getCause().getCause().getMessage()` without null checks, so a violation with no cause or one level of cause throws `NullPointerException` inside the mapper. It then takes `substring(indexOf("Detail: ") + 8)`; when the marker is missing `indexOf` returns -1, so the substring starts at index 7 and returns the rest of the raw message, e.g. `duplicate key value violates unique constraint "uq_cs_name"`. |
| risk | medium - data-integrity relevant error handling, score 8 (risk-assessment.md) |
| URS impact | URS-1 to URS-6 (urs-delta.md) |
| verification | OQ-GSK-1; `GenericExceptionMapperTest` `<pending>`; CI run `<pending>`; full-suite totals `<pending>` |
| security scan | `<pending>` |
| rollback | revert the fix PR; no schema or data change |
| reviewer | __________________ (named GSK approver) |
| decision | __________________ |
| date | __________________ |
