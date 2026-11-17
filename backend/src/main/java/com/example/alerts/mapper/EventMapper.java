package com.example.alerts.mapper;

import com.example.alerts.dto.EventResponse;
import com.example.alerts.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = CategoryMapper.class, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EventMapper {

    EventResponse toResponse(Event event);
}
