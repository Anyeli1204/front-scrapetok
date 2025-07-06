package com.example.scrapetok.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CorsProperties {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    public List<String> getAllowedOrigins() {
        return List.of(allowedOrigins.split(","));
    }
}
