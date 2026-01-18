package com.example.bikerentrest.repositories;

import com.example.bikerentrest.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends GeneralRepository<Customer, UUID> {
    Optional<Customer> findByPhoneNumber(String phoneNumber);
    List<Customer> findByDeletedFalse();
}