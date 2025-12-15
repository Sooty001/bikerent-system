package com.example.bikerentrest.assemblers;

import com.example.bikerentapi.dto.response.BicycleResponse;
import com.example.bikerentrest.controllers.BicycleController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class BicycleModelAssembler implements RepresentationModelAssembler<BicycleResponse, EntityModel<BicycleResponse>> {

    @Override
    public EntityModel<BicycleResponse> toModel(BicycleResponse bicycle) {
        return EntityModel.of(bicycle,
                linkTo(methodOn(BicycleController.class).getBicycleById(bicycle.getId())).withSelfRel(),
                linkTo(methodOn(BicycleController.class).getAllBicycles(null, 0, 10)).withRel("bicycles")
        );
    }
}