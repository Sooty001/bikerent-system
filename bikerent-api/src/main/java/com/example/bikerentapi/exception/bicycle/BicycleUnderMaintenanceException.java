package com.example.bikerentapi.exception.bicycle;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.UUID;

@ResponseStatus(HttpStatus.CONFLICT)
public class BicycleUnderMaintenanceException extends RuntimeException {
  public BicycleUnderMaintenanceException(UUID id) {
    super(String.format("Bicycle %s is currently under maintenance and cannot be booked", id));
  }
}