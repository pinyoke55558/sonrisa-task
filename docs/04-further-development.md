# Further Development Recommendations

Recommended improvements for the next development phase, in priority order.

---

## 1. Integration with real event sources (highest priority)

**Current state:** events enter the system only through the simulation API (`POST /api/simulation/events`, admin only). This is enough to test rule matching and the email and Slack notifications from start to finish, but nothing arrives on its own.

**Recommendation:** connect the system to real event sources so that it receives and processes events automatically instead of relying on manually simulated ones.

Points to settle:

- **Which sources:** for example news feeds (RSS/Atom), market data APIs or disaster alert services (such as GDACS or USGS earthquake feeds), chosen to fit the brief: breaking news, market movements, natural disasters.
- **Pull or push:** scheduled polling of feeds and APIs, or webhooks where the source supports them.
- **Mapping:** convert each source's format into our event model (category, title, summary, link, occurred at).
- **Duplicates:** the same event can arrive more than once, or from several sources, so events need a source id to avoid repeated notifications.
- **Reliability:** retries, rate limits and source outages must not block the rest of the system.

The rest of the processing chain can stay as it is: an incoming event is saved, matched against the rules after the transaction commits, and notifications are sent. The simulation API should stay available for testing.

---

## 2. Define the event evaluation methodology

**Current state:** an event matches a rule when the rule's category filter matches (or the rule has none) and at least one of its keywords appears as a whole word, case-insensitively, in the event's title or summary. This is simple and predictable, but it can't judge how *important* an event is.

**Recommendation:** define exactly how the system decides whether an incoming event meets the threshold of a particular rule. The methodology should cover:

- how incoming events are evaluated against existing rules
- how rule thresholds are defined and calculated (for example a minimum severity, or a relevance score)
- how multiple rules and conditions are combined (AND/OR, keyword groups, exclusions)
- how the system decides whether an event should trigger a specific rule

### Possible direction: LLM-based evaluation

An LLM could interpret more complex or unstructured events and decide whether they meet a rule's criteria, for example a rule described in plain language ("major earthquakes near populated areas"). This would **complement** rule-based evaluation, not replace it: cheap keyword and category filters run first, and the LLM is used only where predefined conditions aren't enough.

### Selection criteria

The final solution should be chosen based on:

| Criterion | Question |
|---|---|
| Accuracy | Does it notify on the right events and skip the wrong ones? |
| Consistency | Does the same event always give the same result? |
| Explainability | Can we tell the user *why* a rule was triggered? |
| Performance | Is evaluation fast enough for the expected event volume? |
| Operational cost | What do evaluations cost per event, and at scale? |

---

## 3. Matching and notification behaviour under load

**Current state:** after an event is saved and the transaction commits, matching runs asynchronously on a small thread pool (`matchingExecutor`: 2 core threads, at most 4, a queue of 100). For each matched rule, `NotificationService` sends the email or Slack message synchronously on that same thread, inside the matching transaction, one target after another. When all 4 threads are busy and the queue is full, new matching tasks are rejected. The rejection is only logged: the event stays saved, but it is never matched and no notifications are sent.

**Risk:** a slow or unavailable mail server or Slack API holds a matching thread and a database connection for the whole duration of the call. With slow delivery, throughput drops to a few events per second, the queue fills up, and events start to be dropped silently. The queue is held in memory, so pending tasks are also lost on restart.

**Recommendation:** load test the matching and notification flow and decide how it should behave under pressure.

Points to settle:

- **Measure:** event throughput, queue length and rejected tasks with normal and with artificially slow notifiers (for example a mocked SMTP server or Slack endpoint with added latency or timeouts).
- **Timeouts:** set explicit connect and read timeouts on the mail and Slack clients so that one slow call can't hold a thread indefinitely.
- **No silent loss:** at minimum, use a `CallerRunsPolicy` on the executor so that overflow slows down the caller instead of dropping events.
- **Decouple sending from matching:** save notifications as `PENDING` in the matching transaction, and send them separately (own executor or scheduled job), outside the database transaction, with retries for failed deliveries.
- **Durability:** if events must never be lost, even on restart, replace the in-memory queue with an outbox table or a message broker.
- **Monitoring:** expose executor metrics (active threads, queue size, rejections) and notification failure counts through Actuator.
