package com.example.alerts.notification;

import java.time.Instant;
import java.util.Set;

public record NotificationMessage(
        String title,
        String summary,
        String category,
        Set<String> matchedKeywords,
        Instant occurredAt,
        String url) {
}
