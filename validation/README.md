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

## Contents

| file | purpose |
|---|---|
| `urs-delta.md` | user requirements added or changed by the change |
| `traceability.csv` | requirement -> ticket -> commit -> test -> result -> reviewer |
| `change-record.md` | change record, reviewer field left for a named person |
| `audit-log.md` | where the who-did-what-when trail comes from |

## Flow

1. A ticket is labelled for Devin (Jira automation).
2. Devin's PR fills the template sections: Ticket, URS delta, Test mapping, Change record.
3. The `gsk-gxp-evidence-pack` Devin automation (defined in cogdeasy/cogdeasy-gsk-demo-ops,
   triggered when a PR opens here) drafts `validation/changes/CR-<ticket>.md` and appends rows
   to `validation/traceability.csv` on the PR branch. Nothing in this repository runs that step.
4. The CODEOWNERS approver reviews code and pack together. Nothing merges without them.
