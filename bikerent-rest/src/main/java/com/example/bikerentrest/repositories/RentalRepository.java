package com.example.bikerentrest.repositories;

import com.example.bikerentrest.entities.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface RentalRepository extends GeneralRepository<Rental, UUID> {
    @Query("SELECT COUNT(r) > 0 FROM Rental r " +
            "WHERE r.bicycle.id = :bicycleId " +
            "AND r.status = 'ACTIVE' " +
            "AND (r.expectedReturnTime > :bookingStart)")
    boolean existsActiveRentalConflict(@Param("bicycleId") UUID bicycleId,
                                       @Param("bookingStart") LocalDateTime bookingStart);
}