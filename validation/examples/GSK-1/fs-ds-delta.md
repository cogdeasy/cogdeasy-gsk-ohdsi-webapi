# Functional and design spec delta: GSK-1

Draft. Not a signed GxP record.

| field | value |
|---|---|
| change id | CR-GSK-1 |
| URS delta | urs-delta.md |
| pull request | `<pending>` |

## Functional spec delta

| FS id | type | behaviour | satisfies |
|---|---|---|---|
| FS-1 | changed | Any `DataIntegrityViolationException` maps to HTTP 409, whatever the shape of its cause chain. | URS-1, URS-2 |
| FS-2 | changed | The 409 message is the text after `Detail: ` when the nested cause message has that marker; otherwise a fixed generic message. | URS-3, URS-4 |
| FS-3 | unchanged | All other exception types map as before. | URS-5 |
| FS-4 | unchanged | The full stack trace, including cause messages, is logged at ERROR before mapping. | URS-6 |

## Design spec delta

| DS id | component | design | satisfies |
|---|---|---|---|
| DS-1 | `org.ohdsi.webapi.util.GenericExceptionMapper#toResponse` | Read the nested cause message only after null checks on `getCause()` and `getCause().getCause()` and on the message. Exact code `<pending>` (fix PR). | FS-1 |
| DS-2 | same | Use the `Detail: ` substring only when `indexOf` is not -1; otherwise build the response from a constant generic message. Generic message wording `<pending>`. | FS-2 |
| DS-3 | same | No change to the other branches or to `LOGGER.error(...)` of the stack trace. | FS-3, FS-4 |

## Interfaces and data

- API contract changes: none; 409 was already the intended status, the message body format
  (`ErrorMessage`) is unchanged.
- Database schema or data changes: none.
- Configuration changes: none.

## Unaffected design areas

- Other exception branches: not edited.
- Services that throw `DataIntegrityViolationException` (concept set, cohort definition
  saves): not edited.
