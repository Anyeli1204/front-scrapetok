package com.example.scrapetok.application.emailservice;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class WelcomeEmailEvent extends ApplicationEvent {
    private final String email;
    private final String subject;
    private final String body;

    public WelcomeEmailEvent(Object source, String email, String subject, String body) {
        super(source);
        this.email = email;
        this.subject = subject;
        this.body = body;
    }
}