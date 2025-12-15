package com.example.bikerentrest.repositories;

import com.example.bikerentrest.entities.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends GeneralRepository<Rental, Long> {
}