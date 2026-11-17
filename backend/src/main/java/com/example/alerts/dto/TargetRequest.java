package com.example.alerts.dto;

import com.example.alerts.model.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TargetRequest(
        @NotNull ChannelType channel,
        @NotBlank @Size(max = 2048) String destination) {
}
