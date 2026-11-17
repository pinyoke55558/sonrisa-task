package com.example.alerts.mapper;

import com.example.alerts.dto.CategoryResponse;
import com.example.alerts.dto.RuleResponse;
import com.example.alerts.dto.RuleSaveRequest;
import com.example.alerts.dto.RuleUpdateRequest;
import com.example.alerts.dto.TargetRequest;
import com.example.alerts.dto.TargetResponse;
import com.example.alerts.model.AlertRule;
import com.example.alerts.model.EventCategory;
import com.example.alerts.model.NotificationTarget;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RuleMapper {

    @Mapping(target = "owner", source = "owner.displayName")
    @Mapping(target = "categories", source = "categories", qualifiedByName = "activeCategories")
    @Mapping(target = "targets", source = "targets", qualifiedByName = "activeTargets")
    RuleResponse toResponse(AlertRule rule);

    CategoryResponse toCategoryResponse(EventCategory category);

    TargetResponse toTargetResponse(NotificationTarget target);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "targets", ignore = true)
    @Mapping(target = "enabled", source = "enabled", defaultValue = "true")
    AlertRule toEntity(RuleSaveRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "version", source = "request.version")
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "targets", ignore = true)
    @Mapping(target = "enabled", source = "request.enabled", defaultValue = "true")
    AlertRule toEntity(Long id, RuleUpdateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "rule", ignore = true)
    NotificationTarget toTarget(TargetRequest request);

    @Named("activeCategories")
    default List<CategoryResponse> activeCategories(Set<EventCategory> categories) {
        return categories.stream()
                .filter(category -> !category.isDeleted())
                .sorted(Comparator.comparing(EventCategory::getName))
                .map(this::toCategoryResponse)
                .toList();
    }

    @Named("activeTargets")
    default List<TargetResponse> activeTargets(List<NotificationTarget> targets) {
        return targets.stream()
                .filter(target -> !target.isDeleted())
                .map(this::toTargetResponse)
                .toList();
    }
}
