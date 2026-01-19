package com.example.bikerentcore.repositories;

import com.example.bikerentcore.entities.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends GeneralRepository<Customer, UUID> {
    Optional<Customer> findByPhoneNumber(String phoneNumber);
    List<Customer> findByDeletedFalse();
}