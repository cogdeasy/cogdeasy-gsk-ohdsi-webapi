#!/usr/bin/env bash
# GSK-64 bisect check: drop the GSK-64 regression test into the commit under test and run it on embedded Postgres.
# exit 0 = good (all rows copied), 1 = bad, 125 = cannot build (skip).
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
T=src/test/java/org/ohdsi/webapi/cohortdefinition/GenerateCohortTaskletTest.java
mkdir -p "$(dirname $T)"; cp ~/gsk64/GenerateCohortTaskletTest.java "$T"
mvn -B -Pwebapi-postgresql -Dmaven.gitcommitid.skip=true -Dtest=GenerateCohortTaskletTest -DfailIfNoTests=false -DskipITtests clean test > ~/gsk64/bisect-step.log 2>&1
rc=$?
line=$(grep -E "Tests run: [0-9]+, Failures" ~/gsk64/bisect-step.log | tail -1)
echo "$(git rev-parse --short HEAD): ${line:-no test result}"
rm -f "$T"; git checkout -q -- . 2>/dev/null
if [ -z "$line" ]; then exit 125; fi
[ $rc -eq 0 ] && exit 0 || exit 1
