package com.example.scrapetok.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ApiError {
    private LocalDateTime timestamp;
    private int status;
    private HttpStatus error;
    private String message;
    private String path;
    private String debugMessage;
    public ApiError(HttpStatus status, String message, String debugMessage, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status.value();
        this.error = status;
        this.message = message;
        this.path = path;
        this.debugMessage = debugMessage;
    }
    public ApiError(HttpStatus status, String message, String path) {
        this(status, message, null, path);
    }
}