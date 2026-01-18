package com.example.bikerentrest.graphql;

import com.example.bikerentapi.dto.request.BicycleRequest;
import com.example.bikerentapi.dto.response.BicycleResponse;
import com.example.bikerentapi.dto.response.PagedResponse;
import com.example.bikerentrest.services.Impl.BicycleServiceImpl;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;

import java.util.UUID;

@DgsComponent
public class BicycleDataFetcher {

    private final BicycleServiceImpl bicycleService;

    public BicycleDataFetcher(BicycleServiceImpl bicycleService) {
        this.bicycleService = bicycleService;
    }

    @DgsQuery
    public PagedResponse<BicycleResponse> bicycles(
            @InputArgument String status,
            @InputArgument int page,
            @InputArgument int size) {
        return bicycleService.findAll(status, page, size);
    }

    @DgsQuery
    public BicycleResponse bicycleById(@InputArgument UUID id) {
        return bicycleService.findById(id);
    }

    @DgsMutation
    public BicycleResponse addBicycle(@InputArgument("input") BicycleRequest request) {
        return bicycleService.addBicycle(request);
    }

    @DgsMutation
    public boolean deleteBicycle(@InputArgument UUID id) {
        bicycleService.deleteById(id);
        return true;
    }
}