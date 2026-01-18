package com.example.bikerentapi.exception.rental;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.UUID;

@ResponseStatus(HttpStatus.CONFLICT)
public class RentalAlreadyCompletedException extends RuntimeException {
  public RentalAlreadyCompletedException(UUID id) {
    super(String.format("Rental %s is already completed", id));
  }
}