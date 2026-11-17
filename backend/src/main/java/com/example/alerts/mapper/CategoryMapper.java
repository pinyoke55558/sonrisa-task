package com.example.alerts.mapper;

import com.example.alerts.dto.CategoryResponse;
import com.example.alerts.model.EventCategory;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CategoryMapper {

    CategoryResponse toResponse(EventCategory category);
}
