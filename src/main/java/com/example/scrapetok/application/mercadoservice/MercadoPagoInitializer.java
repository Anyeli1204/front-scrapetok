package com.example.scrapetok.application.mercadoservice;


import com.mercadopago.MercadoPagoConfig;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.logging.Level;

@Configuration
@ConfigurationProperties(prefix = "mercadopago")
@Data
public class MercadoPagoInitializer {
    private String publicKey;
    private String accessToken;
    private String webhookSecret;
    private String successUrl;
    private String failureUrl;
    private String pendingUrl;
    private String webhookUrl;
    private String environment;

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);
        MercadoPagoConfig.setConnectionRequestTimeout(5000);
        MercadoPagoConfig.setSocketTimeout(5000);
        MercadoPagoConfig.setSocketTimeout(5000);
        MercadoPagoConfig.setLoggingLevel(Level.INFO);
        MercadoPagoConfig.setMaxConnections(20);
    }
}