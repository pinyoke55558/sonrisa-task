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
