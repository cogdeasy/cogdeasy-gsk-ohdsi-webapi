---
name: gxp-evidence-pack
description: Pick the GxP validation documents for a change from its risk assessment and draft them from the PR and Jira ticket. Use when a PR in this repo needs its evidence pack (validation/) filled.
---

# GxP evidence pack

Everything you write is a draft for a named GSK person to review and sign in GSK's quality
system. It is not a signed GxP record.

## Hard rules

- Never fill reviewer, approver, decision, signature or date fields. Leave the
  `__________________` blank or the empty cell as it is. Options in brackets in the label, e.g.
  `decision (approve / reject / rework)`, are for the named person to choose.
- Every URS line maps to at least one test, in `traceability.csv` and in the OQ protocol or CSA
  record. A URS line with no test is a gap: say so in the PR, do not drop the line.
- Write expected results before running anything. Do not edit an expected result after a run;
  raise `deviation-capa.md` instead.
- Use `<pending>` for any commit, test name, total or link you do not have yet. Never invent one.
- Do not name a vendor system for GSK. Say "GSK's quality system" or "GSK's change system".
- Do not add Jira labels. Several labels are live automation triggers.

## Steps

1. If the PR has no ticket (tooling or docs only, no application code change), no pack is
   needed: say so in the PR and stop.
2. Read the ticket (summary, acceptance criteria, labels) and the PR diff. Note the tier from
   `validation/README.md` (this WebAPI is silver).
3. Copy `validation/risk-assessment.md` to `validation/changes/<key>/risk-assessment.md` and
   fill it: change summary, GAMP 5 category, patient safety / product quality / data integrity
   (yes or no with a reason), severity, likelihood and detectability (1-3 each, with reasons),
   score and risk. Leave the CSV lead review block blank.
4. Take the column for that risk from the document matrix in `validation/README.md`. Add the
   silver-tier minimum (URS delta, traceability, change record). Tick the list at the bottom of
   the risk assessment.
5. Copy each required template into `validation/changes/<key>/`, keeping the file name, and
   fill it. Include exactly the documents the matrix requires, no more, no less:
   - `urs-delta.md`: one URS line per acceptance criterion that describes system behaviour.
     Process criteria (CI green, PR sections) go to the QA checklist instead.
   - `oq-protocol.md` (medium, high) or `csa-test-record.md` (low): use the `test-engineer`
     skill to design and run the tests.
   - `change-record.md`: from the PR and ticket; root cause for defects; rollback.
   - `traceability.csv`: one row per URS id, header copied from `validation/traceability.csv`:
     `urs_id,requirement,ticket,commit,test,result,reviewer`; leave `reviewer` empty.
   - `test-summary-report.md`: totals from `python3 dev/ci/summary.py`, the CI run link, and
     the scan result.
   - `fs-ds-delta.md` (medium, high), `validation-plan-delta.md` and `iq-checklist.md` (high),
     `pq-uat-script.md` (medium, high), written in the R&D scientist's terms.
   - `release-notes.md`; `training-note.md` (high).
   - `validation-summary-report.md` (high).
   - `qa-review-checklist.md` (medium, high): pre-fill the comment column with where to look;
     the yes / no column is for QA. `deviation-capa.md` for any failed step.
   - `inspection-pack.md`: one row per artefact with its path and status; mark documents the
     matrix does not require as n/a with the reason. List the audit events for the change
     (sources in `validation/audit-log.md`).
6. Check: `python3 -c "import csv,sys; list(csv.DictReader(open(sys.argv[1])))" <file>` for every
   CSV; every URS id appears in traceability and in a test step; no signature field is filled.
7. Fill the PR template sections (Ticket, URS delta, Test mapping, Change record) from the pack.

A worked medium-risk example is in `validation/examples/GSK-1/`.
