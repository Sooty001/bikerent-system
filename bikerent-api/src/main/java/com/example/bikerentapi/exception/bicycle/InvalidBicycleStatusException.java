package com.example.bikerentapi.exception.bicycle;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidBicycleStatusException extends RuntimeException {
    public InvalidBicycleStatusException(String status) {
        super(String.format("Invalid bicycle status provided: '%s'. Allowed values: AVAILABLE, RENTED, MAINTENANCE", status));
    }
}