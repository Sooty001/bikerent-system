package com.example.bikerentrest.graphql;

import com.example.bikerentapi.dto.request.BookingRequest;
import com.example.bikerentapi.dto.response.BicycleResponse;
import com.example.bikerentapi.dto.response.BookingResponse;
import com.example.bikerentapi.dto.response.CustomerResponse;
import com.example.bikerentrest.services.BookingService;
import com.netflix.graphql.dgs.*;
import graphql.schema.DataFetchingEnvironment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@DgsComponent
public class BookingDataFetcher {

    private final BookingService bookingService;

    public BookingDataFetcher(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @DgsQuery
    public BookingResponse bookingById(@InputArgument Long id) {
        return bookingService.findById(id);
    }

    @DgsQuery
    public List<BookingResponse> bookings() {
        return bookingService.findAll();
    }

    @DgsMutation
    public BookingResponse createBooking(@InputArgument("input") Map<String, Object> input) {
        BookingRequest request = new BookingRequest(
                Long.parseLong(input.get("customerId").toString()),
                Long.parseLong(input.get("bicycleId").toString()),
                LocalDateTime.parse(input.get("plannedStartTime").toString()),
                LocalDateTime.parse(input.get("plannedReturnTime").toString())
        );
        return bookingService.createBooking(request);
    }

    @DgsMutation
    public BookingResponse cancelBooking(@InputArgument Long bookingId) {
        return bookingService.cancelBooking(bookingId);
    }

    @DgsData(parentType = "Booking", field = "customer")
    public CustomerResponse customerForBooking(DataFetchingEnvironment dfe) {
        BookingResponse booking = dfe.getSource();
        return booking.getCustomer();
    }



    @DgsData(parentType = "Booking", field = "bicycle")
    public BicycleResponse bicycleForBooking(DataFetchingEnvironment dfe) {
        BookingResponse booking = dfe.getSource();
        return booking.getBicycle();
    }
}