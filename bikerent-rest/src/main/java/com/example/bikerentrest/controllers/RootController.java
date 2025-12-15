package com.example.bikerentrest.controllers;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
public class RootController {

    @GetMapping
    public RepresentationModel<?> getRoot() {
        RepresentationModel<?> rootModel = new RepresentationModel<>();

        rootModel.add(
                linkTo(methodOn(BicycleController.class).getAllBicycles(null, 0, 10)).withRel("bicycles"),
                linkTo(methodOn(CustomerController.class).getAllCustomers()).withRel("customers"),
                linkTo(methodOn(BookingController.class).getAllBookings()).withRel("bookings"),

                Link.of("/swagger-ui/index.html").withRel("documentation")
        );

        return rootModel;
    }
}
