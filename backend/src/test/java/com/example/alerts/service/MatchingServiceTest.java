package com.example.alerts.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.alerts.model.AlertRule;
import com.example.alerts.model.Event;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private RuleMatcher ruleMatcher;
    @Mock
    private RuleService ruleService;
    @Mock
    private EventService eventService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private MatchingService matchingService;

    @Test
    void notifiesOnlyMatchingRules() {
        Event event = new Event();
        event.setId(1L);
        AlertRule matching = new AlertRule();
        AlertRule notMatching = new AlertRule();
        when(eventService.loadEntity(1L)).thenReturn(event);
        when(ruleService.listMatchable()).thenReturn(List.of(matching, notMatching));
        when(ruleMatcher.listMatchedKeywords(matching, event)).thenReturn(Set.of("earthquake"));
        when(ruleMatcher.listMatchedKeywords(notMatching, event)).thenReturn(Set.of());

        matchingService.onEventSaved(new EventSavedEvent(1L));

        verify(notificationService).notify(event, matching, Set.of("earthquake"));
        verify(notificationService, never()).notify(eq(event), eq(notMatching), any());
    }

    @Test
    void noRulesMeansNoNotifications() {
        Event event = new Event();
        event.setId(1L);
        when(eventService.loadEntity(1L)).thenReturn(event);
        when(ruleService.listMatchable()).thenReturn(List.of());

        matchingService.onEventSaved(new EventSavedEvent(1L));

        verify(notificationService, never()).notify(any(), any(), any());
    }
}
