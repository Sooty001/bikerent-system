package com.example.bikerentcore.repositories;

import com.example.bikerentcore.entities.Bicycle;
import com.example.bikerentcore.entities.enums.BicycleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BicycleRepository extends GeneralRepository<Bicycle, UUID> {
    Page<Bicycle> findByDeletedFalse(Pageable pageable);
    Page<Bicycle> findByStatusAndDeletedFalse(BicycleStatus status, Pageable pageable);
}