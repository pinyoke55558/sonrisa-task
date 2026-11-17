package com.example.alerts.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.example.alerts.model.ChannelType;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class MailNotifierTest {

    @Mock
    private JavaMailSender mailSender;

    private MailNotifier mailNotifier;

    @BeforeEach
    void setUp() {
        MailNotifierProperties properties = new MailNotifierProperties();
        properties.setFrom("alerts@localhost");
        mailNotifier = new MailNotifier(mailSender, properties);
    }

    @Test
    void handlesEmailChannel() {
        assertThat(mailNotifier.channel()).isEqualTo(ChannelType.EMAIL);
    }

    @Test
    void sendsMailWithAllEventDetails() {
        mailNotifier.send("anna@example.com", message("https://example.com/news/1"));

        ArgumentCaptor<SimpleMailMessage> sent = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(sent.capture());
        SimpleMailMessage mail = sent.getValue();
        assertThat(mail.getFrom()).isEqualTo("alerts@localhost");
        assertThat(mail.getTo()).containsExactly("anna@example.com");
        assertThat(mail.getSubject()).isEqualTo("[Alert] Strong earthquake");
        assertThat(mail.getText())
                .contains("A magnitude 6.8 earthquake was recorded.")
                .contains("Category: Dummy category A")
                .contains("Matched keywords: earthquake")
                .contains("Occurred at: 2026-11-17 14:30 UTC")
                .contains("Link: https://example.com/news/1");
    }

    @Test
    void leavesOutMissingLink() {
        mailNotifier.send("anna@example.com", message(null));

        ArgumentCaptor<SimpleMailMessage> sent = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(sent.capture());
        assertThat(sent.getValue().getText()).doesNotContain("Link:");
    }

    @Test
    void wrapsMailFailures() {
        doThrow(new MailSendException("SMTP down")).when(mailSender).send(any(SimpleMailMessage.class));

        assertThatThrownBy(() -> mailNotifier.send("anna@example.com", message(null)))
                .isInstanceOf(NotificationException.class)
                .hasMessageContaining("SMTP down");
    }

    private static NotificationMessage message(String url) {
        return new NotificationMessage(
                "Strong earthquake",
                "A magnitude 6.8 earthquake was recorded.",
                "Dummy category A",
                Set.of("earthquake"),
                Instant.parse("2026-11-17T14:30:00Z"),
                url);
    }
}
