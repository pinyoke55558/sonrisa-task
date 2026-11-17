package com.example.alerts.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.example.alerts.model.ChannelType;
import java.io.IOException;
import java.time.Instant;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class SlackNotifierTest {

    private static final String WEBHOOK = "https://hooks.slack.com/services/T000/B000/secret";

    private MockRestServiceServer server;
    private SlackNotifier slackNotifier;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        slackNotifier = new SlackNotifier(builder.build(), new SlackNotifierProperties());
    }

    @Test
    void handlesSlackChannel() {
        assertThat(slackNotifier.channel()).isEqualTo(ChannelType.SLACK);
    }

    @Test
    void postsMessageTextAsJsonToWebhook() {
        server.expect(requestTo(WEBHOOK))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text", Matchers.containsString("Strong earthquake")))
                .andExpect(jsonPath("$.text", Matchers.containsString("Matched keywords: earthquake")))
                .andExpect(jsonPath("$.text", Matchers.containsString("Occurred at: 2026-11-17 14:30 UTC")))
                .andRespond(withSuccess("ok", MediaType.TEXT_PLAIN));

        slackNotifier.send(WEBHOOK, message());

        server.verify();
    }

    @Test
    void errorResponseBecomesNotificationExceptionWithoutWebhookUrl() {
        server.expect(requestTo(WEBHOOK)).andRespond(withBadRequest().body("invalid_payload"));

        assertThatThrownBy(() -> slackNotifier.send(WEBHOOK, message()))
                .isInstanceOf(NotificationException.class)
                .hasMessage("Slack webhook answered 400: invalid_payload")
                .message().doesNotContain("hooks.slack.com");
    }

    @Test
    void unreachableWebhookBecomesNotificationExceptionWithoutWebhookUrl() {
        server.expect(requestTo(WEBHOOK)).andRespond(withException(new IOException("Connection refused")));

        assertThatThrownBy(() -> slackNotifier.send(WEBHOOK, message()))
                .isInstanceOf(NotificationException.class)
                .hasMessage("Slack webhook could not be reached.");
    }

    private static NotificationMessage message() {
        return new NotificationMessage(
                "Strong earthquake",
                "A magnitude 6.8 earthquake was recorded.",
                "Dummy category A",
                Set.of("earthquake"),
                Instant.parse("2026-11-17T14:30:00Z"),
                null);
    }
}
