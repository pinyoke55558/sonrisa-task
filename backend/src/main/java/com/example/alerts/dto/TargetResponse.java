package com.example.alerts.dto;

import com.example.alerts.model.ChannelType;

public record TargetResponse(Long id, ChannelType channel, String destination) {
}
