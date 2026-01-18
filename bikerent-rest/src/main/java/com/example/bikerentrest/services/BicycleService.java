package com.example.bikerentrest.services;

import com.example.bikerentapi.dto.request.BicycleRequest;
import com.example.bikerentapi.dto.response.BicycleResponse;
import com.example.bikerentapi.dto.response.PagedResponse;
import com.example.bikerentrest.entities.Bicycle;
import com.example.bikerentrest.entities.enums.BicycleStatus;

import java.util.UUID;

public interface BicycleService {
    BicycleResponse addBicycle(BicycleRequest request);
    BicycleResponse findById(UUID id);
    Bicycle findEntityById(UUID id);
    PagedResponse<BicycleResponse> findAll(String status, int page, int size);
    void updateBicycleStatus(UUID id, BicycleStatus newStatus);
    void deleteById(UUID id);
}