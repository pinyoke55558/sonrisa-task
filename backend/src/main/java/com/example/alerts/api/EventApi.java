package com.example.alerts.api;

import com.example.alerts.dto.EventResponse;
import com.example.alerts.dto.PageResponse;
import com.example.alerts.service.EventService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventApi {

    private final EventService eventService;

    @GetMapping
    public PageResponse<EventResponse> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return eventService.list(page, size);
    }

    @GetMapping("/{id}")
    public EventResponse load(@PathVariable Long id) {
        return eventService.load(id);
    }
}
