package com.example.bikerentapi.exception;

import com.example.bikerentapi.dto.response.StatusResponse;
import com.example.bikerentapi.exception.bicycle.*;
import com.example.bikerentapi.exception.booking.*;
import com.example.bikerentapi.exception.customer.*;
import com.example.bikerentapi.exception.rental.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            BicycleNotFoundException.class,
            CustomerNotFoundException.class,
            BookingNotFoundException.class,
            RentalNotFoundException.class,
            ResourceNotFoundException.class
    })
    public ResponseEntity<StatusResponse> handleNotFound(RuntimeException ex) {
        log.warn("Not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new StatusResponse("error", ex.getMessage()));
    }

    @ExceptionHandler({
            CustomerPhoneAlreadyExistsException.class,
            BicycleUnderMaintenanceException.class,
            BicycleNotAvailableException.class,
            BookingTimeConflictException.class,
            BookingNotConfirmedException.class,
            RentalAlreadyCompletedException.class
    })
    public ResponseEntity<StatusResponse> handleConflict(RuntimeException ex) {
        log.warn("Conflict/Logic error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new StatusResponse("error", ex.getMessage()));
    }

    @ExceptionHandler({
            InvalidBicycleStatusException.class,
            InvalidBookingTimeRangeException.class
    })
    public ResponseEntity<StatusResponse> handleBadRequest(RuntimeException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new StatusResponse("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StatusResponse> handleValidation(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("Validation failed: {}", errorMessage);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new StatusResponse("error", "Validation error: " + errorMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StatusResponse> handleGeneralException(Exception ex) {
        log.error("Unexpected system error", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new StatusResponse("error", "Internal server error: " + ex.getMessage()));
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<StatusResponse> handleExternalServiceError(ExternalServiceException ex) {
        log.error("External integration error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new StatusResponse("error", ex.getMessage()));
    }
}