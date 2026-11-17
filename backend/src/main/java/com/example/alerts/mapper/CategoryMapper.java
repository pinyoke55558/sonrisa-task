package com.example.alerts.mapper;

import com.example.alerts.dto.CategoryResponse;
import com.example.alerts.dto.CategorySaveRequest;
import com.example.alerts.dto.CategoryUpdateRequest;
import com.example.alerts.model.EventCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CategoryMapper {

    CategoryResponse toResponse(EventCategory category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    EventCategory toEntity(CategorySaveRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "version", source = "request.version")
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    EventCategory toEntity(Long id, CategoryUpdateRequest request);
}
