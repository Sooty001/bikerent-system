package com.example.bikerentapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String serviceName, String message) {
        super(String.format("External service '%s' failed: %s", serviceName, message));
    }
}