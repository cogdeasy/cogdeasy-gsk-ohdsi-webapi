---
name: test-engineer
description: Design tests from URS lines and acceptance criteria, run the WebAPI test suite with JDK 8, total the results and record evidence in the OQ protocol or CSA test record. Use when a change in this repo needs tests or test evidence.
---

# Test engineer

## Design

1. List the URS lines (`urs-delta.md`) and the ticket's acceptance criteria. Each one needs at
   least one test.
2. For each, write:
   - positive: the intended input gives the intended result;
   - negative: bad or unexpected input is handled safely (right status, no raw internals, no
     exception escaping);
   - boundary: edges of the input, e.g. null, empty, missing nested cause, a marker at the
     start or end of a message.
3. Add regression tests for nearby behaviour that must not change.
4. Write the expected result for every step in `oq-protocol.md` (medium, high risk) or the
   charter in `csa-test-record.md` (low risk) before running anything.
5. New JUnit 4 tests go next to the class under test in `src/test/java`, same package.

## Run

```bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
mvn -B -Pwebapi-postgresql -Dtest=<TestClass> test   # the new tests first
mvn -B -Pwebapi-postgresql test                      # full suite + JaCoCo
python3 dev/ci/summary.py                            # run / passed / failures / errors / skipped
```

JDK 8 is required (`maven.compiler.source/target 1.8`). Database tests start an embedded
PostgreSQL; no external database is needed. For error-mapper defects also run `./reproduce.sh`
(exit 1 = bug present, exit 0 = fixed).

Show new tests failing on the code before the fix, then passing after it.

## Evidence

- OQ protocol: fill actual, pass / fail and evidence ref for each step. An evidence ref is the
  surefire report (`target/surefire-reports/TEST-<class>.xml`), the CI run link for `tests`,
  or the `reproduce.sh` output pasted into the protocol.
- CSA record: write what you did and what you saw in Observations, with evidence refs, and list
  issues found.
- Put full-suite totals from `summary.py` and the CI run link in `test-summary-report.md`.
- A failed step gets a `deviation-capa.md` record. Do not change the expected result.
- Leave executed-by, reviewed-by and date fields blank for the named person.
