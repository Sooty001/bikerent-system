package com.example.bikerentrest.controllers;

import com.example.bikerentapi.dto.request.BookingRequest;
import com.example.bikerentapi.dto.response.BookingResponse;
import com.example.bikerentapi.endpoints.BookingApi;
import com.example.bikerentrest.assemblers.BookingModelAssembler;
import com.example.bikerentrest.services.BookingService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookingController implements BookingApi {

    private final BookingService bookingService;
    private final BookingModelAssembler bookingAssembler;

    public BookingController(BookingService bookingService, BookingModelAssembler bookingAssembler) {
        this.bookingService = bookingService;
        this.bookingAssembler = bookingAssembler;
    }

    @Override
    public CollectionModel<EntityModel<BookingResponse>> getAllBookings() {
        List<BookingResponse> bookings = bookingService.findAll();
        return bookingAssembler.toCollectionModel(bookings);
    }

    @Override
    public EntityModel<BookingResponse> getBookingById(Long id) {
        BookingResponse booking = bookingService.findById(id);
        return bookingAssembler.toModel(booking);
    }

    @Override
    public ResponseEntity<EntityModel<BookingResponse>> createBooking(@Valid BookingRequest request) {
        BookingResponse newBooking = bookingService.createBooking(request);
        EntityModel<BookingResponse> entityModel = bookingAssembler.toModel(newBooking);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @Override
    public EntityModel<BookingResponse> cancelBooking(Long id) {
        BookingResponse cancelledBooking = bookingService.cancelBooking(id);
        return bookingAssembler.toModel(cancelledBooking);
    }
}
