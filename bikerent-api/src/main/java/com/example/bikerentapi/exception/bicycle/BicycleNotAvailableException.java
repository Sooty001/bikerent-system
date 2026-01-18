package com.example.bikerentapi.exception.bicycle;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.UUID;

@ResponseStatus(HttpStatus.CONFLICT)
public class BicycleNotAvailableException extends RuntimeException {
    public BicycleNotAvailableException(UUID id) {
        super(String.format("Bicycle %s is not available for rental at this moment", id));
    }
}