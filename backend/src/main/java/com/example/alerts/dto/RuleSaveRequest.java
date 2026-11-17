package com.example.alerts.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;

public record RuleSaveRequest(
        @NotBlank @Size(max = 100) String name,
        Boolean enabled,
        @NotEmpty Set<@NotBlank @Size(max = 100) String> keywords,
        Set<Long> categoryIds,
        @NotEmpty List<@Valid TargetRequest> targets) {
}
