package com.example.bikerentapi.exception.customer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CustomerPhoneAlreadyExistsException extends RuntimeException {
    public CustomerPhoneAlreadyExistsException(String phoneNumber) {
        super(String.format("Customer with phone number '%s' already exists", phoneNumber));
    }
}