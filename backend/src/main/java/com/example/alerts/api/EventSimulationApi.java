package com.example.alerts.api;

import com.example.alerts.dto.EventResponse;
import com.example.alerts.dto.EventSimulationRequest;
import com.example.alerts.service.EventSimulationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/simulation/events")
@RequiredArgsConstructor
public class EventSimulationApi {

    private final EventSimulationService eventSimulationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse simulate(@Valid @RequestBody EventSimulationRequest request) {
        return eventSimulationService.simulate(request);
    }
}
