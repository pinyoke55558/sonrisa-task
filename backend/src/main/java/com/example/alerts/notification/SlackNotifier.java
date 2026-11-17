package com.example.alerts.notification;

import com.example.alerts.model.ChannelType;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
@RequiredArgsConstructor
public class SlackNotifier implements Notifier {

    private final RestClient slackRestClient;
    private final SlackNotifierProperties properties;

    @Override
    public ChannelType channel() {
        return ChannelType.SLACK;
    }

    @Override
    public void send(String destination, NotificationMessage message) {
        try {
            slackRestClient.post()
                    .uri(destination)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("text", body(message, properties.getTimeFormat())))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            throw new NotificationException(
                    "Slack webhook answered " + ex.getStatusCode().value() + ": " + ex.getResponseBodyAsString(), ex);
        } catch (RestClientException ex) {
            throw new NotificationException("Slack webhook could not be reached.", ex);
        }
    }
}
