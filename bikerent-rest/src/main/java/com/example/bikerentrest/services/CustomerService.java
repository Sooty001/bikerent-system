package com.example.bikerentrest.services;

import com.example.bikerentapi.dto.request.CustomerRequest;
import com.example.bikerentapi.dto.response.CustomerResponse;
import com.example.bikerentrest.entities.Customer;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    CustomerResponse registerCustomer(CustomerRequest request);
    void updateLoyaltyPoints(UUID customerId, int pointsDelta);
    CustomerResponse findById(UUID id);
    Customer findEntityById(UUID id);
    List<CustomerResponse> findAll();
    void deleteById(UUID id);
}