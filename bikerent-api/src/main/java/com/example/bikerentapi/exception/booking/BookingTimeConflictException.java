package com.example.bikerentapi.exception.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.UUID;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookingTimeConflictException extends RuntimeException {
    public BookingTimeConflictException(UUID bicycleId) {
        super(String.format("Bicycle %s is already booked or rented for the selected time range", bicycleId));
    }
}