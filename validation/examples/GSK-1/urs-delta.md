# URS delta: GSK-1

Draft. Not a signed GxP record. Prepared by Devin for review by a named GSK approver.

| URS id | type | requirement | source | acceptance |
|---|---|---|---|---|
| URS-1 | new | When a save fails with a data integrity violation that has no cause, the system shall return HTTP 409 and the error mapper shall not throw. | GSK-1 AC1 | OQ step 3 |
| URS-2 | new | When a data integrity violation has one level of cause and no nested cause, the system shall return HTTP 409. | GSK-1 AC2 | OQ step 4 |
| URS-3 | new | When the nested cause message has no `Detail:` marker, the system shall return HTTP 409 with a generic message and no raw database text (no SQL, no constraint name). | GSK-1 AC3 | OQ step 5 |
| URS-4 | changed (behaviour kept) | When the nested cause message has a PostgreSQL `Detail:` line, the system shall return HTTP 409 with only the detail text. | GSK-1 AC4 | OQ step 6 |
| URS-5 | changed (behaviour kept) | Existing mappings shall not change: 403 for `UnauthorizedException` / `ForbiddenException`, 404 for `NotFoundException`, 400 for `BadRequestException`, 501 for `ConceptRecommendedNotInstalledException`, 500 with a generic message for other exceptions. | GSK-1 AC5 | OQ step 7 |
| URS-6 | new | The full exception, including database text, shall be written to the server log and not to the API response. | GSK-1 summary; AGENTS.md conventions | OQ step 8 |

GSK-1 AC6 (regression tests, full suite, CI green) and AC7 (PR sections, named approver) are
process criteria. They are checked in OQ steps 9-10 and in `qa-review-checklist.md`.

## Unaffected requirements

- Other exception branches of `GenericExceptionMapper`: code not changed; covered by URS-5 as a
  regression check.
- Concept set and cohort save logic: not changed; the database constraint still rejects the
  duplicate.
- Authentication and authorisation (Shiro): not touched.
- Database schema and Flyway migrations: no change.
