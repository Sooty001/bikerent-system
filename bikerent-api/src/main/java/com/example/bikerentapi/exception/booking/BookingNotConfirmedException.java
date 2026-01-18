package com.example.bikerentapi.exception.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.UUID;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookingNotConfirmedException extends RuntimeException {
    public BookingNotConfirmedException(UUID bookingId, String currentStatus) {
        super(String.format("Cannot start rental for booking %s. Status is %s, but must be CONFIRMED", bookingId, currentStatus));
    }
}