package com.example.alerts.service;

import com.example.alerts.model.AlertRule;
import com.example.alerts.model.Event;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService {

    private final RuleMatcher ruleMatcher;
    private final RuleService ruleService;
    private final EventService eventService;
    private final NotificationService notificationService;

    @Async("matchingExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onEventSaved(EventSavedEvent eventSaved) {
        Event event = eventService.loadEntity(eventSaved.eventId());
        int matchedRules = 0;
        for (AlertRule rule : ruleService.listMatchable()) {
            Set<String> matchedKeywords = ruleMatcher.listMatchedKeywords(rule, event);
            if (!matchedKeywords.isEmpty()) {
                notificationService.notify(event, rule, matchedKeywords);
                matchedRules++;
            }
        }
        log.info("Event {} matched {} rule(s)", event.getId(), matchedRules);
    }
}
