package com.example.scrapetok.application.emailservice;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AlertEmailListener {

    final private EmailService emailService;
    public AlertEmailListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async("emailExecutor")
    @EventListener
    public void sendEmail(AlertEmailEvent alertEmailEvent) {
        emailService.sendEmail(alertEmailEvent.getEmail(), alertEmailEvent.getSubject(), alertEmailEvent.getBody());
    }

    @Async("welcomeEventExecutor")
    @EventListener
    public void sendWelcomeEmail(WelcomeEmailEvent welcomeEmailEvent) {
        emailService.sendEmail(welcomeEmailEvent.getEmail(), welcomeEmailEvent.getSubject(), welcomeEmailEvent.getBody());
    }




}
