package com.example.alerts.service;

import com.example.alerts.model.AlertRule;
import com.example.alerts.model.Event;
import com.example.alerts.model.Notification;
import com.example.alerts.model.NotificationStatus;
import com.example.alerts.model.NotificationTarget;
import com.example.alerts.notification.NotificationException;
import com.example.alerts.notification.NotificationMessage;
import com.example.alerts.notification.Notifier;
import com.example.alerts.repository.NotificationRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final List<Notifier> notifiers;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void notify(Event event, AlertRule rule, Set<String> matchedKeywords) {
        NotificationMessage message = new NotificationMessage(
                event.getTitle(),
                event.getSummary(),
                event.getCategory().getName(),
                matchedKeywords,
                event.getOccurredAt(),
                event.getUrl());

        rule.getTargets().stream()
                .filter(target -> !target.isDeleted())
                .forEach(target -> notificationRepository.saveAndFlush(send(event, target, message)));
    }

    private Notification send(Event event, NotificationTarget target, NotificationMessage message) {
        Notification notification = new Notification();
        notification.setEvent(event);
        notification.setTarget(target);
        try {
            loadNotifier(target).send(target.getDestination(), message);
            notification.setStatus(NotificationStatus.SENT);
        } catch (NotificationException ex) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(ex.getMessage());
        } catch (RuntimeException ex) {
            log.error("Unexpected error sending notification for event {} to target {}", event.getId(), target.getId(), ex);
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage("Unexpected error while sending.");
        }
        log.info("Notification for event {} to target {} ({}): {}",
                event.getId(), target.getId(), target.getChannel(), notification.getStatus());
        return notification;
    }

    private Notifier loadNotifier(NotificationTarget target) {
        return notifiers.stream()
                .filter(notifier -> notifier.channel() == target.getChannel())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No notifier for channel " + target.getChannel()));
    }
}
