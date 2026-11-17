package com.example.alerts.notification;

import com.example.alerts.model.ChannelType;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailNotifier implements Notifier {

    private final JavaMailSender mailSender;
    private final MailNotifierProperties properties;

    @Override
    public ChannelType channel() {
        return ChannelType.EMAIL;
    }

    @Override
    public void send(String destination, NotificationMessage message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(properties.getFrom());
        mail.setTo(destination);
        mail.setSubject("[Alert] " + message.title());
        mail.setText(body(message, properties.getTimeFormat()));

        try {
            mailSender.send(mail);
        } catch (MailException ex) {
            throw new NotificationException("Sending email failed: " + ex.getMessage(), ex);
        }
    }
}
