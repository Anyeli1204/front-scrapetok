package com.example.scrapetok.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "emailExecutor")
    public Executor emailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);       // 2 hilos básicos
        executor.setMaxPoolSize(3);        // máximo 3 simultáneos
        executor.setQueueCapacity(50);     // cola para 50 tareas si los hilos están ocupados
        executor.setThreadNamePrefix("EmailSender-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "welcomeEventExecutor")
    public Executor welcomeEventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("WelcomeEmailSender-");
        executor.initialize();
        return executor;
    }
}
