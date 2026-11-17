package com.example.alerts.notification;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "app.notification.mail")
public class MailNotifierProperties {

    @NotBlank
    private String from;

    @NotBlank
    private String timeFormat = "yyyy-MM-dd HH:mm 'UTC'";
}
