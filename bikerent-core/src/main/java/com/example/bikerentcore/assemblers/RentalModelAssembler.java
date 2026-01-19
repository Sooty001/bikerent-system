package com.example.bikerentcore.assemblers;

import com.example.bikerentapi.dto.response.RentalResponse;
import com.example.bikerentcore.controllers.BicycleController;
import com.example.bikerentcore.controllers.CustomerController;
import com.example.bikerentcore.controllers.RentalController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RentalModelAssembler implements RepresentationModelAssembler<RentalResponse, EntityModel<RentalResponse>> {

    @Override
    public EntityModel<RentalResponse> toModel(RentalResponse rental) {
        EntityModel<RentalResponse> rentalModel = EntityModel.of(rental,
                linkTo(methodOn(RentalController.class).getRentalById(rental.getId())).withSelfRel(),
                linkTo(methodOn(CustomerController.class).getCustomerById(rental.getCustomerId())).withRel("customer"),
                linkTo(methodOn(BicycleController.class).getBicycleById(rental.getBicycleId())).withRel("bicycle")
        );

        if ("active".equalsIgnoreCase(rental.getStatus())) {
            rentalModel.add(linkTo(methodOn(RentalController.class).completeRental(rental.getId())).withRel("complete"));
        }

        return rentalModel;
    }
}
