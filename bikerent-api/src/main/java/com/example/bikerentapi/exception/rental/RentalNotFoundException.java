package com.example.bikerentapi.exception.rental;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RentalNotFoundException extends RuntimeException {
    public RentalNotFoundException(UUID id) {
        super(String.format("Rental with ID %s not found", id));
    }
}