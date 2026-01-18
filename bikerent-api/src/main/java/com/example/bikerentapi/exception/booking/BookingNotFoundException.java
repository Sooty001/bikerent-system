package com.example.bikerentapi.exception.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(UUID id) {
        super(String.format("Booking with ID %s not found", id));
    }
}