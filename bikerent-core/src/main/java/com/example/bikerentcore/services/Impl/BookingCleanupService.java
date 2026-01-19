package com.example.bikerentcore.services.Impl;

import com.example.bikerentcore.entities.Booking;
import com.example.bikerentcore.entities.enums.BookingStatus;
import com.example.bikerentcore.repositories.BookingRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingCleanupService {

    private final BookingRepository bookingRepository;
    public BookingCleanupService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void cancelNoShowBookings() {
        LocalDateTime cutOffTime = LocalDateTime.now().minusMinutes(30);

        List<Booking> expiredBookings = bookingRepository.findByStatusAndPlannedStartTimeBefore(
                BookingStatus.CONFIRMED,
                cutOffTime
        );
        if (!expiredBookings.isEmpty()) {
            for (Booking booking : expiredBookings) {
                booking.setStatus(BookingStatus.CANCELLED);
            }
            bookingRepository.saveAll(expiredBookings);
        }
    }
}