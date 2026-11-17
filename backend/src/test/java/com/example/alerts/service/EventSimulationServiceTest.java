package com.example.alerts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.alerts.dto.EventResponse;
import com.example.alerts.dto.EventSimulationRequest;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventSimulationServiceTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventSimulationService eventSimulationService;

    @Test
    void simulateSavesEventThroughEventService() {
        EventSimulationRequest request =
                new EventSimulationRequest(5L, "Strong earthquake", null, null, Instant.parse("2026-11-17T14:30:00Z"));
        EventResponse saved = new EventResponse(1L, null, "Strong earthquake", null, null, request.occurredAt(), null);
        when(eventService.save(request)).thenReturn(saved);

        assertThat(eventSimulationService.simulate(request)).isSameAs(saved);
        verify(eventService).save(request);
    }
}
