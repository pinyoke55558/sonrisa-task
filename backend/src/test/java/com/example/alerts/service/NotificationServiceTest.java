package com.example.alerts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.alerts.model.AlertRule;
import com.example.alerts.model.ChannelType;
import com.example.alerts.model.Event;
import com.example.alerts.model.EventCategory;
import com.example.alerts.model.Notification;
import com.example.alerts.model.NotificationStatus;
import com.example.alerts.model.NotificationTarget;
import com.example.alerts.notification.NotificationException;
import com.example.alerts.notification.NotificationMessage;
import com.example.alerts.notification.Notifier;
import com.example.alerts.repository.NotificationRepository;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NotificationServiceTest {

    @Mock
    private Notifier mailNotifier;
    @Mock
    private Notifier slackNotifier;
    @Mock
    private NotificationRepository notificationRepository;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        when(mailNotifier.channel()).thenReturn(ChannelType.EMAIL);
        when(slackNotifier.channel()).thenReturn(ChannelType.SLACK);
        when(notificationRepository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));
        notificationService = new NotificationService(List.of(mailNotifier, slackNotifier), notificationRepository);
    }

    @Test
    void sendsToEachTargetWithMatchingNotifierAndStoresSent() {
        AlertRule rule = rule(target(ChannelType.EMAIL, "anna@example.com"), target(ChannelType.SLACK, "https://hooks.slack.com/x"));

        notificationService.notify(event(), rule, Set.of("earthquake"));

        ArgumentCaptor<NotificationMessage> message = ArgumentCaptor.forClass(NotificationMessage.class);
        verify(mailNotifier).send(eq("anna@example.com"), message.capture());
        verify(slackNotifier).send(eq("https://hooks.slack.com/x"), any());
        assertThat(message.getValue().title()).isEqualTo("Strong earthquake");
        assertThat(message.getValue().category()).isEqualTo("Dummy A");
        assertThat(message.getValue().matchedKeywords()).containsExactly("earthquake");
        assertThat(savedNotifications(2)).extracting(Notification::getStatus)
                .containsExactly(NotificationStatus.SENT, NotificationStatus.SENT);
    }

    @Test
    void failedTargetIsStoredAsFailedAndOthersStillSent() {
        AlertRule rule = rule(target(ChannelType.SLACK, "https://hooks.slack.com/x"), target(ChannelType.EMAIL, "anna@example.com"));
        doThrow(new NotificationException("Slack webhook answered 404: no_service", null))
                .when(slackNotifier).send(any(), any());

        notificationService.notify(event(), rule, Set.of("earthquake"));

        List<Notification> saved = savedNotifications(2);
        assertThat(saved.get(0).getStatus()).isEqualTo(NotificationStatus.FAILED);
        assertThat(saved.get(0).getErrorMessage()).isEqualTo("Slack webhook answered 404: no_service");
        assertThat(saved.get(1).getStatus()).isEqualTo(NotificationStatus.SENT);
        verify(mailNotifier).send(eq("anna@example.com"), any());
    }

    @Test
    void unexpectedErrorIsStoredAsFailedWithoutInternals() {
        AlertRule rule = rule(target(ChannelType.EMAIL, "anna@example.com"));
        doThrow(new IllegalStateException("secret detail")).when(mailNotifier).send(any(), any());

        notificationService.notify(event(), rule, Set.of("earthquake"));

        Notification saved = savedNotifications(1).getFirst();
        assertThat(saved.getStatus()).isEqualTo(NotificationStatus.FAILED);
        assertThat(saved.getErrorMessage()).isEqualTo("Unexpected error while sending.");
    }

    @Test
    void deletedTargetsAreSkipped() {
        NotificationTarget deleted = target(ChannelType.EMAIL, "old@example.com");
        deleted.setDeleted(true);

        notificationService.notify(event(), rule(deleted), Set.of("earthquake"));

        verify(mailNotifier, never()).send(any(), any());
        verify(notificationRepository, never()).saveAndFlush(any());
    }

    private List<Notification> savedNotifications(int expected) {
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(expected)).saveAndFlush(captor.capture());
        return captor.getAllValues();
    }

    private static AlertRule rule(NotificationTarget... targets) {
        AlertRule rule = new AlertRule();
        for (NotificationTarget target : targets) {
            rule.addTarget(target);
        }
        return rule;
    }

    private static NotificationTarget target(ChannelType channel, String destination) {
        NotificationTarget target = new NotificationTarget();
        target.setChannel(channel);
        target.setDestination(destination);
        return target;
    }

    private static Event event() {
        EventCategory category = new EventCategory();
        category.setName("Dummy A");
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Strong earthquake");
        event.setCategory(category);
        event.setOccurredAt(Instant.parse("2026-11-17T14:30:00Z"));
        return event;
    }
}
