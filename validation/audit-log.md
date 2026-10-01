# Audit log

The who-did-what-when trail for each change is assembled from systems of record, not written by
hand:

| event | source |
|---|---|
| `session.created` | Devin enterprise audit-log API (session id, triggering automation, Jira key) |
| `pr.opened` | GitHub pull request event |
| `review.completed` | Devin Review / GitHub review event |
| `pr.approved` | GitHub review by the CODEOWNERS approver |
| `pr.merged` | GitHub merge event with branch-protection status |

Example, as attached to a change record:

```
09:52 session.created   GSK-102  automation=gsk-ticket-to-pr
09:53 pr.opened         #12      author=devin
09:58 review.completed  #12      devin-review: 0 blocking
10:02 pr.approved       #12      reviewer=<GSK approver>
```
