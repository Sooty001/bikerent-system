package com.example.bikerentcore.services;

import com.example.bikerentapi.dto.request.RentalRequest;
import com.example.bikerentapi.dto.response.RentalResponse;

import java.util.UUID;

public interface RentalService {
    RentalResponse findById(UUID id);
    RentalResponse startWalkInRental(RentalRequest request);
    RentalResponse startRentalFromBooking(UUID bookingId);
    RentalResponse completeRental(UUID id);
}