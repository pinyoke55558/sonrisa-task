package com.example.alerts.mapper;

import com.example.alerts.dto.EventResponse;
import com.example.alerts.dto.EventSimulationRequest;
import com.example.alerts.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = CategoryMapper.class, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EventMapper {

    EventResponse toResponse(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    Event toEntity(EventSimulationRequest request);
}
