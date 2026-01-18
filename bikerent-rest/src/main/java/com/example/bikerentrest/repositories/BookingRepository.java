package com.example.bikerentrest.repositories;

import com.example.bikerentrest.entities.Booking;
import com.example.bikerentrest.entities.enums.BookingStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends GeneralRepository<Booking, UUID> {

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.bicycle.id = :bicycleId " +
            "AND b.status = 'CONFIRMED' " +
            "AND (b.plannedReturnTime > :start AND b.plannedStartTime < :end)")
    boolean existsBookingConflict(@Param("bicycleId") UUID bicycleId,
                                  @Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end);

    List<Booking> findByStatusAndPlannedStartTimeBefore(BookingStatus status, LocalDateTime time);
}