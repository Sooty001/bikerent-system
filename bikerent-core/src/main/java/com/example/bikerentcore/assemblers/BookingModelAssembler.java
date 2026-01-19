package com.example.bikerentcore.assemblers;

import com.example.bikerentapi.dto.response.BookingResponse;
import com.example.bikerentcore.controllers.BicycleController;
import com.example.bikerentcore.controllers.BookingController;
import com.example.bikerentcore.controllers.CustomerController;
import com.example.bikerentcore.controllers.RentalController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BookingModelAssembler implements RepresentationModelAssembler<BookingResponse, EntityModel<BookingResponse>> {

    @Override
    public EntityModel<BookingResponse> toModel(BookingResponse booking) {
        EntityModel<BookingResponse> bookingModel = EntityModel.of(booking,
                linkTo(methodOn(BookingController.class).getBookingById(booking.getId())).withSelfRel(),
                linkTo(methodOn(CustomerController.class).getCustomerById(booking.getCustomerId())).withRel("customer"),
                linkTo(methodOn(BicycleController.class).getBicycleById(booking.getBicycleId())).withRel("bicycle"),
                linkTo(methodOn(BookingController.class).getAllBookings()).withRel("bookings")
        );

        if ("confirmed".equalsIgnoreCase(booking.getStatus())) {
            bookingModel.add(linkTo(methodOn(BookingController.class).cancelBooking(booking.getId())).withRel("cancel"));
            bookingModel.add(linkTo(methodOn(RentalController.class).startRentalFromBooking(booking.getId())).withRel("start-rental"));
        }

        return bookingModel;
    }

    @Override
    public CollectionModel<EntityModel<BookingResponse>> toCollectionModel(Iterable<? extends BookingResponse> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(BookingController.class).getAllBookings()).withSelfRel());
    }
}
