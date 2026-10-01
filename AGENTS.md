# Working in this repository

GSK demo fork of [OHDSI WebAPI](https://github.com/OHDSI/WebAPI): the RESTful services behind
ATLAS (cohorts, concept sets, characterisations, incidence rates) over OMOP CDM v5 databases.
In the GSK framing it is a **silver-tier** GxP-adjacent application: changes need a named human
approver and a change record, but not full CSV re-validation. See `validation/README.md`.

## Toolchain

- Java 8 (`maven.compiler.source/target 1.8`), Spring Boot 1.5, Jersey (JAX-RS), Shiro.
- Maven profile `webapi-postgresql` for every command.
- Tests use JUnit 4. Database tests start an embedded PostgreSQL (zonky) on Linux x86-64, no
  external database is needed.

```bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
mvn -B -Pwebapi-postgresql -DskipUnitTests -DskipITtests compile   # fast compile
mvn -B -Pwebapi-postgresql test                                    # full suite + JaCoCo
mvn -B -Pwebapi-postgresql -Dtest=GenericExceptionMapperTest test  # one class
./reproduce.sh                                                     # GSK-102 repro, exit 1 = bug present
```

## CI gate

`.github/workflows/ci.yml` runs three required checks on every PR to `main`:

| check   | what it does |
|---------|--------------|
| `build` | packages `target/WebAPI.war` |
| `tests` | surefire + failsafe with JaCoCo; summary on the run page |
| `scan`  | Trivy over the WAR (all severities), SARIF to code scanning; reports, does not block |

## How to work a ticket

1. Read the Jira ticket and its acceptance criteria. Put the ticket key in the branch name
   (`devin/gsk-102-error-mapper`) and in the PR title.
2. Reproduce before changing code. For error-mapping defects run `./reproduce.sh`.
3. Write the regression tests from the acceptance criteria first and show them failing on the
   current code. Then fix the root cause, not the symptom.
4. Run the full suite. Report passed / failed / skipped from `target/surefire-reports`.
5. Open the PR using the template. Fill **every** section: Ticket, URS delta, Test mapping,
   Change record. Use the templates in `validation/`.
6. Do not merge. A named GSK approver (CODEOWNERS) approves and merges.

## Validation evidence

- `validation/README.md`: CSA by default, full CSV for high risk; the document matrix says which
  templates a change needs.
- `.agents/skills/gxp-evidence-pack/SKILL.md`: pick and fill the documents from the risk
  assessment, PR and ticket.
- `.agents/skills/test-engineer/SKILL.md`: design tests from the URS, run them, record evidence.

## Conventions

- Minimal, focused diffs. Do not reformat files you did not need to change; the upstream code
  mixes tabs and spaces.
- Never return stack traces, SQL, constraint names or other raw database text to API clients.
  Log them server-side; the client gets a status code and a safe message.
- New tests live next to the class under test in `src/test/java` with the same package.
- Dependency upgrades: one library family per PR, state the CVEs closed and re-run `scan`.
- Conventional commit subjects (`fix:`, `feat:`, `chore:`, `test:`).
