package com.example.bikerentapi.exception.bicycle;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BicycleNotFoundException extends RuntimeException {
    public BicycleNotFoundException(UUID id) {
        super(String.format("Bicycle with ID %s not found", id));
    }
}