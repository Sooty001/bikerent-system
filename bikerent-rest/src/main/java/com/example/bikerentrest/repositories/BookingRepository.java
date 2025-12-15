package com.example.bikerentrest.repositories;

import com.example.bikerentrest.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

public interface BookingRepository extends GeneralRepository<Booking, Long> {

    // В SQL запросе Enum корректно преобразуется в строку благодаря JPA
    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.bicycle.id = :bicycleId " +
            "AND b.status = 'CONFIRMED' " +
            "AND (b.plannedReturnTime > :start AND b.plannedStartTime < :end)")
    boolean existsBookingConflict(@Param("bicycleId") Long bicycleId,
                                  @Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end);
}