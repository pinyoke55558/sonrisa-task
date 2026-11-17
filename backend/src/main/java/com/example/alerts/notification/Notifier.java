package com.example.alerts.notification;

import com.example.alerts.model.ChannelType;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public interface Notifier {

    ChannelType channel();

    void send(String destination, NotificationMessage message);

    default String body(NotificationMessage message, String timeFormat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat).withZone(ZoneOffset.UTC);
        StringBuilder body = new StringBuilder()
                .append(message.title()).append("\n\n");
        if (message.summary() != null) {
            body.append(message.summary()).append("\n\n");
        }
        body.append("Category: ").append(message.category()).append('\n')
                .append("Matched keywords: ").append(String.join(", ", message.matchedKeywords())).append('\n')
                .append("Occurred at: ").append(formatter.format(message.occurredAt())).append('\n');
        if (message.url() != null) {
            body.append("Link: ").append(message.url()).append('\n');
        }
        return body.toString();
    }
}
