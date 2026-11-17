package com.example.alerts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record EventSimulationRequest(
        @NotNull Long categoryId,
        @NotBlank @Size(max = 255) String title,
        String summary,
        @Size(max = 2048) String url,
        @NotNull Instant occurredAt) {
}
