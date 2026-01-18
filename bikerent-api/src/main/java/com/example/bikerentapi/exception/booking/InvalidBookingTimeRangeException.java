package com.example.bikerentapi.exception.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidBookingTimeRangeException extends RuntimeException {
    public InvalidBookingTimeRangeException(String message) {
        super(message);
    }
}