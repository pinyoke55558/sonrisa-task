package com.example.alerts.service;

import com.example.alerts.dto.EventResponse;
import com.example.alerts.dto.EventSimulationRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventSimulationService {

    private final EventService eventService;

    public EventResponse simulate(EventSimulationRequest request) {
        return eventService.save(request);
    }

    public List<EventResponse> listRecent(int limit) {
        return eventService.listRecent(limit);
    }
}
