#!/usr/bin/env bash
# Reproduces the GenericExceptionMapper error-mapping defect (GSK-102) outside the test suite.
# Exit 0: every scenario returns a safe 409. Exit 1: the defect reproduces.
set -euo pipefail
cd "$(dirname "$0")"

PROFILE="${MAVEN_PROFILE:-webapi-postgresql}"
OUT=target/repro

mvn -B -q -P"$PROFILE" -DskipUnitTests -DskipITtests compile
mkdir -p "$OUT"
if [ ! -f "$OUT/classpath.txt" ] || [ pom.xml -nt "$OUT/classpath.txt" ]; then
  mvn -B -q -P"$PROFILE" dependency:build-classpath -Dmdep.outputFile="$OUT/classpath.txt" >/dev/null
fi
CP="target/classes:$(cat "$OUT/classpath.txt")"
javac -nowarn -d "$OUT" -cp "$CP" dev/repro/ErrorMapperRepro.java
java -cp "$OUT:$CP" ErrorMapperRepro 2>/dev/null
