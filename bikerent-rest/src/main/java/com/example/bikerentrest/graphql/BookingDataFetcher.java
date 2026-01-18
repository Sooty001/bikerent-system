package com.example.bikerentrest.graphql;

import com.example.bikerentapi.dto.request.BookingRequest;
import com.example.bikerentapi.dto.response.BookingResponse;
import com.example.bikerentrest.services.BookingService;
import com.netflix.graphql.dgs.*;

import java.util.List;
import java.util.UUID;

@DgsComponent
public class BookingDataFetcher {

    private final BookingService bookingService;

    public BookingDataFetcher(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @DgsQuery
    public BookingResponse bookingById(@InputArgument UUID id) {
        return bookingService.findById(id);
    }

    @DgsQuery
    public List<BookingResponse> bookings() {
        return bookingService.findAll();
    }

    @DgsMutation
    public BookingResponse createBooking(@InputArgument("input") BookingRequest request) {
        return bookingService.createBooking(request);
    }

    @DgsMutation
    public BookingResponse cancelBooking(@InputArgument UUID bookingId) {
        return bookingService.cancelBooking(bookingId);
    }
}