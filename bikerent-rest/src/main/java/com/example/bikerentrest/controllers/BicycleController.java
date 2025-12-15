package com.example.bikerentrest.controllers;

import com.example.bikerentapi.dto.request.BicycleRequest;
import com.example.bikerentapi.dto.response.BicycleResponse;
import com.example.bikerentapi.dto.response.PagedResponse;
import com.example.bikerentapi.dto.response.StatusResponse;
import com.example.bikerentapi.endpoints.BicycleApi;
import com.example.bikerentrest.assemblers.BicycleModelAssembler;
import com.example.bikerentrest.services.BicycleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BicycleController implements BicycleApi {

    private final BicycleService bicycleService;
    private final BicycleModelAssembler bicycleAssembler;
    private final PagedResourcesAssembler<BicycleResponse> pagedAssembler;

    public BicycleController(BicycleService bicycleService, BicycleModelAssembler bicycleAssembler,
                             PagedResourcesAssembler<BicycleResponse> pagedAssembler) {
        this.bicycleService = bicycleService;
        this.bicycleAssembler = bicycleAssembler;
        this.pagedAssembler = pagedAssembler;
    }

    @Override
    public EntityModel<BicycleResponse> getBicycleById(Long id) {
        BicycleResponse bicycle = bicycleService.findById(id);
        return bicycleAssembler.toModel(bicycle);
    }

    @Override
    public PagedModel<EntityModel<BicycleResponse>> getAllBicycles(String status, int page, int size) {
        PagedResponse<BicycleResponse> pagedResponse = bicycleService.findAll(status, page, size);
        Page<BicycleResponse> bicyclePage = new PageImpl<>(
                pagedResponse.content(),
                PageRequest.of(pagedResponse.page(), pagedResponse.size()),
                pagedResponse.totalElements()
        );
        return pagedAssembler.toModel(bicyclePage, bicycleAssembler);
    }

    @Override
    public ResponseEntity<EntityModel<BicycleResponse>> addBicycle(BicycleRequest request) {
        BicycleResponse createdBicycle = bicycleService.addBicycle(request);
        EntityModel<BicycleResponse> entityModel = bicycleAssembler.toModel(createdBicycle);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @Override
    public StatusResponse deleteBicycle(Long id) {
        bicycleService.deleteById(id);
        return new StatusResponse("success", "Велосипед с ID " + id + " успешно удален.");
    }
}
