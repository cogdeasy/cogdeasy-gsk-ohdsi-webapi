"""Writes a Markdown test and coverage summary from surefire, failsafe and JaCoCo output."""
from __future__ import annotations

import csv
import glob
import xml.etree.ElementTree as ET
from pathlib import Path


def test_totals() -> dict[str, int]:
    totals = {"tests": 0, "failures": 0, "errors": 0, "skipped": 0}
    for path in glob.glob("target/surefire-reports/TEST-*.xml") + glob.glob(
        "target/failsafe-reports/TEST-*.xml"
    ):
        suite = ET.parse(path).getroot()
        for key in totals:
            totals[key] += int(suite.get(key, "0"))
    return totals


def coverage() -> tuple[int, int] | None:
    report = Path("target/site/jacoco/jacoco.csv")
    if not report.exists():
        return None
    covered = missed = 0
    with report.open() as handle:
        for row in csv.DictReader(handle):
            covered += int(row["LINE_COVERED"])
            missed += int(row["LINE_MISSED"])
    return covered, missed


def main() -> None:
    t = test_totals()
    passed = t["tests"] - t["failures"] - t["errors"] - t["skipped"]
    print("## Tests")
    print()
    print("| run | passed | failures | errors | skipped |")
    print("|---|---|---|---|---|")
    print(f"| {t['tests']} | {passed} | {t['failures']} | {t['errors']} | {t['skipped']} |")
    print()
    cov = coverage()
    if cov:
        covered, missed = cov
        total = covered + missed
        print("## Line coverage (JaCoCo)")
        print()
        print(f"{covered} / {total} lines, {100 * covered / total:.1f}%")


if __name__ == "__main__":
    main()
