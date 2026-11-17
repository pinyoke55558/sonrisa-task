package com.example.alerts.dto;

import com.example.alerts.model.EventCategory;

public record CategoryResponse(Long id, String code, String name, String description, Long version) {

    public static CategoryResponse from(EventCategory category) {
        return new CategoryResponse(
                category.getId(),
                category.getCode(),
                category.getName(),
                category.getDescription(),
                category.getVersion());
    }
}
