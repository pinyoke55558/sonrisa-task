package com.example.alerts.dto;

import java.time.Instant;

public record EventResponse(
        Long id,
        CategoryResponse category,
        String title,
        String summary,
        String url,
        Instant occurredAt,
        Instant createdAt) {
}
