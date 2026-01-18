package com.example.bikerentrest.services;

import com.example.bikerentapi.dto.request.BookingRequest;
import com.example.bikerentapi.dto.response.BookingResponse;
import com.example.bikerentrest.entities.Booking;

import java.util.List;
import java.util.UUID;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request);
    BookingResponse cancelBooking(UUID id);
    BookingResponse findById(UUID id);
    Booking findEntityById(UUID id);
    List<BookingResponse> findAll();
    void updateBookingStatus(UUID id, String newStatus);
}