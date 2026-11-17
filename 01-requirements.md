# 01 – Requirements: World Event Alerts

> **Status:** Draft v1 (2026-11-17). Not reviewed yet.
> This document describes **what** the system must do and why. **How** it is built is in [02-architecture-plan.md](02-architecture-plan.md).
> Items marked **[ASSUMPTION]** or **[VERIFY]** still have to be confirmed or corrected.

---

## 1. The brief (verbatim, the only input)

> "We want users to be able to set up alerts so they get notified when something important happens in the world – like breaking news, market movements, natural disasters, that kind of thing. Should work for both email and Slack. Make it flexible enough that we can add more channels later. We need an admin view too."

No further specification, wireframes or schema exist. Everything below is our interpretation and is open to challenge.

---

## 2. How the brief is read

| # | Requirement from the brief | How it is read |
|---|---|---|
| R1 | "users … set up alerts" | An authenticated user can create, edit, enable/disable and delete **alert rules** |
| R2 | "something important happens in the world" | The system **ingests events** from external sources and stores them in one normalized model with a **severity** |
| R3 | "breaking news, market movements, natural disasters" | At least three **event categories**; at least one fed by a *real* external source |
| R4 | "get notified" | When an event matches a rule, a **notification** goes to the rule's targets |
| R5 | "email and Slack" | Two working channels: email and Slack |
| R6 | "add more channels later" | Adding a new channel must not require changes to rules, matching or delivery logic |
| R7 | "admin view" | Admins can see users, rules, ingested events, delivery outcomes and source health, and act on them |

---

## 3. What "important" means

The brief doesn't define "important", so it is defined in **two layers**:

1. **Objective layer (system):** every event gets a normalized **severity**: `LOW(1)`, `MEDIUM(2)`, `HIGH(3)`, `CRITICAL(4)`, derived from the source's raw signal by a documented, source-specific rule.
2. **Subjective layer (user):** each user decides what matters *to them* through rule criteria (category, minimum severity, keywords, region).

So "important" = *the event's severity passes the user's threshold and it matches the user's filters*. That keeps the system out of editorial decisions and lets each user set their own threshold.

### 3.1 Event categories
`NATURAL_DISASTER`, `MARKET`, `NEWS`. A fixed list for the MVP; admins can't add categories at runtime.

### 3.2 Severity mapping (business rules, first draft)

| Source | Rule |
|---|---|
| Earthquakes (real feed) | magnitude < 4.5 → LOW; 4.5–5.9 → MEDIUM; 6.0–6.9 → HIGH; ≥ 7.0 → CRITICAL **[VERIFY thresholds are reasonable; also check whether the USGS `alert` field (green/yellow/orange/red) is the better signal]** |
| Market (simulated) | abs(% change) ≥ 2 → MEDIUM; ≥ 5 → HIGH; ≥ 10 → CRITICAL |
| News (simulated / manual) | severity set by the producer (admin-injected event or fixture) |

---

## 4. Scope

### 4.1 In scope (MVP)
- User accounts with roles `USER` and `ADMIN`
- Self-service management of alert rules and notification targets (per user)
- Event intake from **1 real** source (natural disasters) + **simulated/manual** sources for news and markets
- Event normalization, deduplication and storage
- Rule matching
- Reliable delivery with retries and a persisted delivery history
- Email and Slack channels
- Admin capabilities (§6.6)
- Runs locally with a single command; automated tests

### 4.2 Out of scope (explicit non-goals)
- Production-grade news intelligence (NLP, trending detection, source credibility)
- Real-time market data feeds (paid/licensed; out of scope for 24 h)
- Slack OAuth app / bot install flow (incoming webhooks are enough to prove the point)
- Email verification and double opt-in flows (noted as a production gap)
- Multi-tenancy and horizontal scaling
- Mobile push, SMS (but adding them must be easy, see R6)
- A rich front-end SPA
- Notification digests (one message per event, see A3)

---

## 5. Assumptions

| ID | Assumption | Impact if wrong |
|---|---|---|
| A1 | **[ASSUMPTION]** "Users" are end users of our product, not just internal staff | Self-service rule management and per-user data isolation are needed either way |
| A2 | **[ASSUMPTION]** Near real-time (minutes) is enough; second-level latency isn't required | Polling is fine; no streaming infrastructure needed |
| A3 | **[ASSUMPTION]** One alert per event per target is the expected behaviour; digests are a later feature | Delivery is modeled per message; a digest would need batching |
| A4 | **[ASSUMPTION]** A single deployed instance is enough for the exercise | Simpler infrastructure; the design should still not break with more than one instance |
| A5 | **[VERIFY]** The USGS Earthquake Hazards GeoJSON summary feeds are public, keyless and stable (e.g., `…/earthquakes/feed/v1.0/summary/all_hour.geojson`) | If wrong, fall back to GDACS RSS or to a simulated source |
| A6 | **[VERIFY]** Slack incoming webhooks accept a JSON `{"text": …}` POST and return `200 ok` | The Slack channel and its tests depend on this contract |
| A7 | **[ASSUMPTION]** No free, keyless, ToS-compliant market data API exists that is reliable enough for a demo | Market source is simulated |

---

## 6. Functional requirements

### 6.1 Accounts and roles
- **FR-1.1** A person can register and log in; passwords are never stored in plain text.
- **FR-1.2** Two roles: `USER` (manages own rules and targets) and `ADMIN` (everything in §6.6).
- **FR-1.3** An admin can deactivate a user; a deactivated user receives no notifications.

### 6.2 Notification targets
- **FR-2.1** A user can create, list, update, enable/disable and delete notification targets. A target = channel type + destination (email address or Slack webhook URL) + display name.
- **FR-2.2** The destination is validated per channel when it's saved (valid email format; Slack URL must start with `https://hooks.slack.com/`).
- **FR-2.3** A user can send a **test notification** to a target.
- **FR-2.4** Users can only see and change their own targets.

### 6.3 Alert rules
- **FR-3.1** A user can create, list, view, update, enable/disable and delete alert rules.
- **FR-3.2** A rule has: name, categories (empty = any), minimum severity, keywords (optional), region (optional bounding box), and one or more of the user's own targets.
- **FR-3.3** Users can only see and change their own rules.

### 6.4 Matching semantics
An event matches a rule when **all** of these hold:
1. the rule is enabled, and the owning user is active
2. `event.category ∈ rule.categories` (empty set = any category)
3. `event.severity ≥ rule.minSeverity`
4. `rule.keywords` is empty **or** at least one keyword occurs (case-insensitive, whole word) in the title or summary
5. `rule.region` is empty **or** the event's location lies inside the region (events without a location never match a region-filtered rule)
6. the event is not older than a staleness window (default 24 h), so the first data load doesn't flood users

### 6.5 Event intake and notifications
- **FR-5.1** Events are fetched from each source periodically; the same source event is stored only once.
- **FR-5.2** A failing source must not affect the other sources.
- **FR-5.3** For every matching (event, rule), each of the rule's enabled targets gets a notification with: title, summary, category, severity, time, link to the source.
- **FR-5.4** A target receives **at most one** notification per event, even if several of the user's rules match the same event **[DECISION – D007]**.
- **FR-5.5** Failed sends are retried with growing delays; after a maximum number of attempts the notification is marked as permanently failed. Permanent errors (e.g., invalid address) aren't retried.
- **FR-5.6** A target receives at most **N notifications per hour** (default 20) to protect against floods **[DECISION – D010]**.
- **FR-5.7** A user can see their own notification history.

### 6.6 Admin view
- **FR-6.1** List users; deactivate a user.
- **FR-6.2** List all rules, filterable by user, category and enabled state.
- **FR-6.3** List ingested events, filterable by category, severity and date.
- **FR-6.4** Manually inject an event (the "breaking news" source and the demo path).
- **FR-6.5** List deliveries, filterable by status and channel; re-queue a permanently failed delivery.
- **FR-6.6** See source health: last run, last success, last error, events fetched.
- **FR-6.7** See basic statistics: events per day, deliveries by status and channel.
- Form of the admin view: REST API first, a simple web page if time allows **[DECISION – D011]**.

---

## 7. Non-functional requirements

| ID | Area | Requirement |
|---|---|---|
| NFR-1 | Latency | An event is delivered within a few minutes of appearing at the source (A2) |
| NFR-2 | Reliability | No notification is lost on app restart; delivery is **at-least-once** (rare duplicates after a crash are acceptable and documented) |
| NFR-3 | Extensibility | A new channel or event source can be added without changes to rules, matching or delivery logic (R6) |
| NFR-4 | Security – isolation | Users can't read or change another user's data; covered by negative tests |
| NFR-5 | Security – secrets | Slack webhook URLs are credentials: masked in responses, never logged; SMTP credentials come from environment variables |
| NFR-6 | Security – SSRF | Users can't make the server call arbitrary URLs (FR-2.2) |
| NFR-7 | Observability | Health endpoint; logs traceable by event and delivery ID; counters for events ingested and deliveries sent/failed per channel |
| NFR-8 | Operability | Whole stack starts locally with one command; configuration through properties/env vars |
| NFR-9 | Testability | Automated tests at unit and integration level; external systems (feeds, Slack, SMTP) are stubbed in tests |
| NFR-10 | Time | All timestamps in UTC |

---

## 8. Open requirement decisions

| ID | Question | Recommendation |
|---|---|---|
| D006 | Which real event source? | USGS earthquakes (after verifying A5) |
| D007 | One notification per (event, target) or per (event, rule, target)? | Per (event, target) |
| D010 | Flood protection | Simple per-target hourly cap |
| D011 | Admin view form | REST API first; simple web page if time allows |

Also still to be checked: assumptions A1–A7 and the severity thresholds in §3.2.
