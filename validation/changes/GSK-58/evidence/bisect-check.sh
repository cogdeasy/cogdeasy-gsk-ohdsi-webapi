#!/usr/bin/env bash
# GSK-58 bisect check: run the GSK-58 regression test against the checked-out commit.
# exit 0 = tests pass (good), 1 = assertion failures (bad), 125 = build/context error (skip, not evidence).
set -u
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
sha=$(git rev-parse --short HEAD)
log=$HOME/gsk58/bisect-step-$sha.log
mkdir -p src/test/java/org/ohdsi/webapi/cohortdefinition
cp ~/gsk58/GenerateCohortTaskletTest.java src/test/java/org/ohdsi/webapi/cohortdefinition/
mvn -B -o -Pwebapi-postgresql -Dtest=GenerateCohortTaskletTest -DfailIfNoTests=false -DskipITtests clean test > "$log" 2>&1
rc=$?
rm -f src/test/java/org/ohdsi/webapi/cohortdefinition/GenerateCohortTaskletTest.java
summary=$(grep -E "^\[(INFO|ERROR|WARNING)\] Tests run: [0-9]+, Failures: [0-9]+, Errors: [0-9]+, Skipped: [0-9]+$" "$log" | tail -1)
echo "$sha: $summary"
[ $rc -eq 0 ] && exit 0
echo "$summary" | grep -q "Errors: 0," && echo "$summary" | grep -qv "Failures: 0," && exit 1
exit 125
