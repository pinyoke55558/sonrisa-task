package com.example.alerts.service;

import com.example.alerts.dto.EventResponse;
import com.example.alerts.dto.EventSimulationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventSimulationService {

    public EventResponse simulate(EventSimulationRequest request) {
        throw new UnsupportedOperationException("Event simulation is not implemented yet.");
    }
}
