package com.example.bikerentrest.controllers;

import com.example.bikerentapi.dto.request.RentalRequest;
import com.example.bikerentapi.dto.response.RentalResponse;
import com.example.bikerentapi.endpoints.RentalApi;
import com.example.bikerentrest.assemblers.RentalModelAssembler;
import com.example.bikerentrest.services.RentalService;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class RentalController implements RentalApi {

    private final RentalService rentalService;
    private final RentalModelAssembler rentalAssembler;

    public RentalController(RentalService rentalService, RentalModelAssembler rentalAssembler) {
        this.rentalService = rentalService;
        this.rentalAssembler = rentalAssembler;
    }

    @Override
    public EntityModel<RentalResponse> getRentalById(UUID id) {
        RentalResponse rental = rentalService.findById(id);
        return rentalAssembler.toModel(rental);
    }

    @Override
    public ResponseEntity<EntityModel<RentalResponse>> startWalkInRental(@Valid RentalRequest request) {
        RentalResponse rental = rentalService.startWalkInRental(request);
        EntityModel<RentalResponse> entityModel = rentalAssembler.toModel(rental);
        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @Override
    public ResponseEntity<EntityModel<RentalResponse>> startRentalFromBooking(UUID bookingId) {
        RentalResponse rental = rentalService.startRentalFromBooking(bookingId);
        EntityModel<RentalResponse> entityModel = rentalAssembler.toModel(rental);
        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @Override
    public EntityModel<RentalResponse> completeRental(UUID id) {
        RentalResponse rental = rentalService.completeRental(id);
        return rentalAssembler.toModel(rental);
    }
}
