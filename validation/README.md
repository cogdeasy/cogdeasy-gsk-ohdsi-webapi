# GxP evidence pack

Every change to this repository carries a draft evidence pack. Devin drafts it; a named GSK
person reviews, completes and signs it in GSK's own quality system.

**Boundary.** These are example templates, not signed GxP records. This repository is not GxP
validated or 21 CFR Part 11 certified; the pack supports GSK's own validation process.

## Application tiers

The tier sets where people sit in the flow, in line with GSK's risk-based AI framework.

| tier | examples | approval | evidence |
|---|---|---|---|
| gold | GxP-validated systems, batch release, pharmacovigilance | two named approvers (system owner + QA); no agent merge rights | full pack + CSV impact assessment |
| silver | GxP-adjacent analytics, e.g. this WebAPI | one named approver (CODEOWNERS), branch protection | URS delta, test mapping, change record |
| bronze | internal tooling, non-GxP | standard code review | change record only |

## Validation approach

Risk-based CSA (Computer Software Assurance) is the default. A change rated high risk in
[`risk-assessment.md`](risk-assessment.md) gets full GAMP 5 CSV (scripted IQ / OQ / PQ).

| risk | approach | testing record |
|---|---|---|
| low | CSA | unscripted or exploratory testing ([`csa-test-record.md`](csa-test-record.md)) |
| medium | CSA | scripted tests for the changed functions ([`oq-protocol.md`](oq-protocol.md)) |
| high | full CSV | validation plan delta, scripted IQ / OQ / PQ |

The tier minimum always applies on top of the matrix: a silver change always has a URS delta,
test mapping (traceability) and change record.

## Document matrix

R = required, O = optional, n/a = not used for that risk.

| document | low | medium | high | owner |
|---|---|---|---|---|
| [`risk-assessment.md`](risk-assessment.md) | R | R | R | CSV lead |
| [`urs-delta.md`](urs-delta.md) | R | R | R | R&D scientist |
| [`fs-ds-delta.md`](fs-ds-delta.md) | n/a | R | R | developer |
| [`validation-plan-delta.md`](validation-plan-delta.md) | n/a | n/a | R | CSV lead |
| [`csa-test-record.md`](csa-test-record.md) | R | n/a | n/a | test engineer |
| [`oq-protocol.md`](oq-protocol.md) | n/a | R | R | test engineer |
| [`iq-checklist.md`](iq-checklist.md) | n/a | n/a | R | release manager |
| [`pq-uat-script.md`](pq-uat-script.md) | n/a | R | R | R&D scientist |
| [`traceability.csv`](traceability.csv) | R | R | R | test engineer |
| [`test-summary-report.md`](test-summary-report.md) | R | R | R | test engineer |
| [`validation-summary-report.md`](validation-summary-report.md) | n/a | n/a | R | CSV lead |
| [`qa-review-checklist.md`](qa-review-checklist.md) | O | R | R | QA |
| [`deviation-capa.md`](deviation-capa.md) | O | O | O | QA |
| [`change-record.md`](change-record.md) | R | R | R | developer |
| [`release-notes.md`](release-notes.md) | R | R | R | release manager |
| [`training-note.md`](training-note.md) | n/a | n/a | R | release manager |
| [`inspection-pack.md`](inspection-pack.md) | R | R | R | QA |
| [`audit-log.md`](audit-log.md) | R | R | R | QA |

Filled copies for a change go in `validation/changes/<key>/`, one file per template with the
same name (for example `validation/changes/GSK-1/risk-assessment.md`, `change-record.md`,
`traceability.csv`). The pack has exactly the documents its risk column requires.

`deviation-capa.md` becomes required as soon as a test step fails or a protocol is not followed.

## Roles

| role | does |
|---|---|
| developer | drafts the change record and FS / DS delta; writes the code and unit tests (Devin drafts as developer) |
| test engineer | designs tests from the URS, writes and runs the OQ protocol or CSA record, keeps traceability, writes the test summary |
| CSV lead | confirms the risk rating; owns the validation plan delta and validation summary for high risk |
| QA | reviews the pack, classifies deviations, owns the inspection pack |
| release manager | runs IQ, issues release notes and the training note |
| R&D scientist | end user of ATLAS; owns the URS delta and runs PQ / UAT |
| auditor | reads the inspection pack and audit trail; changes nothing |

## Fan-out flow

1. A ticket is labelled for Devin (Jira automation).
2. Risk assessment: fill `risk-assessment.md`. The result picks the column of the matrix above.
3. Requirements: `change-record.md`, `urs-delta.md`, `traceability.csv` rows; then
   `fs-ds-delta.md` (medium, high) and, for high risk, `validation-plan-delta.md`.
4. Test design: expected results written first in `oq-protocol.md` (medium, high) or a charter
   in `csa-test-record.md` (low); `iq-checklist.md` for high risk; `pq-uat-script.md` for the
   R&D scientist (medium, high).
5. Build: code and tests on the PR; CI runs `build`, `tests`, `scan`.
6. Execution: fill actual results and evidence refs; add rows to `traceability.csv`; raise
   `deviation-capa.md` for any failure.
7. Report: `test-summary-report.md`.
8. Review: `qa-review-checklist.md`. The CODEOWNERS approver reviews code and pack together.
   Nothing merges without them.
9. Release: IQ in the target environment (high), PQ / UAT by an R&D scientist (medium, high),
   then `validation-summary-report.md` (high).
10. Hand-over: `release-notes.md`, `training-note.md` (high), and `inspection-pack.md` indexing
    every artefact.

The `gsk-gxp-evidence-pack` Devin automation (defined in cogdeasy/cogdeasy-gsk-demo-ops,
triggered when a PR opens here) runs steps 2-7 as drafts: it fills the risk assessment, then
writes the required documents under `validation/changes/<key>/` on the PR branch. Nothing in
this repository runs that step.
Fields that need a named person (reviewer, decision, signature, date) stay blank.

## Worked example

[`examples/GSK-1/`](examples/GSK-1/) is a draft medium-risk pack for the GSK-1 error-mapper
defect.

## Skills

- `.agents/skills/gxp-evidence-pack/SKILL.md`: pick and fill the documents.
- `.agents/skills/test-engineer/SKILL.md`: design, run and record the tests.
