package com.example.alerts.dto;

import java.util.List;
import java.util.Set;

public record RuleResponse(
        Long id,
        String name,
        boolean enabled,
        Set<String> keywords,
        List<CategoryResponse> categories,
        List<TargetResponse> targets,
        String owner,
        Long version) {
}
