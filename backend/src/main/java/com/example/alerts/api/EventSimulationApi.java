package com.example.alerts.api;

import com.example.alerts.dto.EventResponse;
import com.example.alerts.dto.EventSimulationRequest;
import com.example.alerts.service.EventSimulationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Event simulation", description = "Dummy event source; ADMIN only")
@RestController
@RequestMapping("/api/simulation/events")
@RequiredArgsConstructor
public class EventSimulationApi {

    private final EventSimulationService eventSimulationService;

    @GetMapping
    public List<EventResponse> listRecent(@RequestParam(defaultValue = "5") @Min(1) @Max(20) int limit) {
        return eventSimulationService.listRecent(limit);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse simulate(@Valid @RequestBody EventSimulationRequest request) {
        return eventSimulationService.simulate(request);
    }
}
