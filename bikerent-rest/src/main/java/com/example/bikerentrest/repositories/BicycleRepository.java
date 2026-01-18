package com.example.bikerentrest.repositories;

import com.example.bikerentrest.entities.Bicycle;
import com.example.bikerentrest.entities.enums.BicycleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BicycleRepository extends GeneralRepository<Bicycle, UUID> {
    Page<Bicycle> findByDeletedFalse(Pageable pageable);
    Page<Bicycle> findByStatusAndDeletedFalse(BicycleStatus status, Pageable pageable);
}