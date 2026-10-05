# SI hand-over notes: WebAPI 2.15.1

Prepared by the SI delivery team for the GSK maintenance team.

- Build: `mvn -B -Pwebapi-postgresql -DskipTests package` on JDK 8.
- Tests: `mvn -B -Pwebapi-postgresql test` (embedded PostgreSQL, no external database needed).
- Cohort generation path: `CohortGenerationService` -> `GenerateCohortTasklet` -> `GenerationCacheHelper`
  (computes or reuses the cached result) -> copy from the cache tables into `@results_database_schema.cohort`.
- Known operational issue raised during hypercare: very large cohorts hold one long `INSERT ... SELECT`
  on the results schema during the copy step. Addressed in the 2.15.1 performance work.

## Release notes: 2.15.1-si.3

- Cohort generation: the cohort row copy from the generation cache is split into smaller statements
  to avoid long-running statements on large cohorts.
- Housekeeping: unused imports, whitespace, test comment typo, debug logging of copy statements.
- Full test suite green on JDK 8 (`mvn -B -Pwebapi-postgresql test`).
